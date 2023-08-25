package wekb

import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.security.crypto.password.PasswordEncoder
import wekb.auth.User
import org.hibernate.SessionFactory
import org.springframework.security.access.annotation.Secured
import wekb.helper.RDStore
import wekb.system.FTControl
import wekb.system.SavedSearch

import java.time.LocalDate
import java.time.ZoneId

@Secured(['IS_AUTHENTICATED_FULLY'])
class HomeController {

  SpringSecurityService springSecurityService
  PasswordEncoder passwordEncoder
  SessionFactory sessionFactory

  static stats_cache = null;
  static stats_timestamp = null;

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def statistic() {
    if ( ( stats_timestamp == null )|| ( System.currentTimeMillis() - stats_timestamp > 3600000 ) || params.reset) {
      stats_timestamp = System.currentTimeMillis()
      // Initialise
      stats_cache = [:];
      stats_cache = calculate();
    }
    else {
      log.debug("stats from cache (${System.currentTimeMillis() - stats_timestamp})...");
    }

    return stats_cache
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index () {
    log.debug("Home::index -- ${params}")

    Map result = [:]

    result.dateNow = new Date()

    Date dateFor14Days = Date.from(LocalDate.now().minusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant())

    List newsAboutObjects = [//'CuratoryGroup',
                             //'KbartSource',
                             'Org',
                             'Package',
                             'Platform',
                             'TitleInstancePackagePlatform'
    ]

    List status = [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_EXPECTED]
    result.news = [:]

    newsAboutObjects.each{ String domainClassName ->
      result.news[domainClassName.toLowerCase()] = [:]

      String queryNewCount = "select count(*) from ${domainClassName} where TO_CHAR(dateCreated,'YYYY-MM-DD') = TO_CHAR(lastUpdated,'YYYY-MM-DD') and status in (:status) and dateCreated >= :daysBefore"
      result.news[domainClassName.toLowerCase()] .countNewInDB = Package.executeQuery(queryNewCount, [status: status, daysBefore: dateFor14Days])[0]
      String queryLastUpdatedCount = "select count(*) from ${domainClassName} where TO_CHAR(dateCreated,'YYYY-MM-DD') != TO_CHAR(lastUpdated,'YYYY-MM-DD') and status in (:status) and lastUpdated >= :daysBefore"
      result.news[domainClassName.toLowerCase()] .countLastUpdatedInDB = Package.executeQuery(queryLastUpdatedCount, [status: status, daysBefore: dateFor14Days])[0]


      String queryNew = "from ${domainClassName} where TO_CHAR(dateCreated,'YYYY-MM-DD') = TO_CHAR(lastUpdated,'YYYY-MM-DD') and status in (:status) and dateCreated >= :daysBefore order by dateCreated desc"
      result.news[domainClassName.toLowerCase()] .newInDB = Package.executeQuery(queryNew, [status: status, daysBefore: dateFor14Days], [max: 100, offset: 0])
      String queryLastUpdated = "from ${domainClassName} where TO_CHAR(dateCreated,'YYYY-MM-DD') != TO_CHAR(lastUpdated,'YYYY-MM-DD') and status in (:status) and lastUpdated >= :daysBefore order by lastUpdated desc"
      result.news[domainClassName.toLowerCase()] .lastUpdatedInDB = Package.executeQuery(queryLastUpdated, [status: status, daysBefore: dateFor14Days], [max: 100, offset: 0])

    }

    result

  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def userdash() {
    def result = [:]
    result.user = springSecurityService.currentUser

    result.editable = true

    result.removeMax = params.removeMax ?: false

    if(params.removeSearch){
      SavedSearch.withTransaction {
        SavedSearch savedSearch = SavedSearch.findById(params.search_id)
        if(savedSearch && savedSearch.owner == result.user){
          savedSearch.delete()
          flash.sussess = 'Saved search has been deleted!'
        }
      }
    }

    params.sort = params.sort ?: 'name'
    params.order = params.order ?: 'desc'

    result.saved_items = SavedSearch.executeQuery('Select ss from SavedSearch as ss where ss.owner = :user',[user: result.user], [sort: params.sort, order: params.order])

    result
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def dash() {
    def result=[:]

    User user = springSecurityService.currentUser

    result
  }

  def releaseNotes() {
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def dsgvo() {

  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def profile() {
    def result = [:]
    User user = springSecurityService.currentUser
    result.user = user
    result.editable = true
    result.curator = [user]

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def changePass() {
    if ( params.newpass != '' && params.newpass == params.repeatpass ) {
      User user = springSecurityService.currentUser
      if (passwordEncoder.matches(params.origpass, user.password)) {
        User.withTransaction {
          user.password = params.newpass
          user.save()
        }
        flash.success = "Password Changed!"
      }
      else {
        flash.error = "Existing password does not match: Not changing!"
      }
    }
    else {
      flash.error = "New password does not match repeat password: Not changing"
    }
    redirect(action:'profile')
  }


  private def calculate() {
    log.debug("Calculating stats...");
    def result=[:]

    def components = [
            'Titles' : [
                    'componentName' : 'TitleInstancePackagePlatform',
            ],
            'Providers' : [
                    'componentName' : 'Org',
            ],
            'Packages' : [
                    'componentName' : 'Package'
            ],
            'Platforms' : [
                    'componentName' : 'Platform',
            ],
    ]

    // The calendar for the queries.
    Calendar calendar = Calendar.getInstance()
    int start_year = calendar.get(Calendar.YEAR) - 1
    int start_month = calendar.get(Calendar.MONTH) + 1
    if ( start_month == 12 ) {
      start_month = 0
      start_year++
    }

    result.components = [:]
    result.xAxis = []

    // For each month in the past 12 months, execute each stat query defined in the month_queries array and stuff
    // the count for that stat in the matrix (Rows = stats, cols = months)
    components.each {component_name, component_data ->

      log.debug("Processing counts for ${component_name}");
      result.components."${component_name}" = [:]
      result.components."${component_name}".newComponentsData = []
      result.components."${component_name}".allComponentsData = []

      // Clear the calendar.
      calendar.clear();
      calendar.set(Calendar.MONTH, start_month);
      calendar.set(Calendar.YEAR, start_year);

      for ( int i=0; i<12; i++ ) {

        log.debug("Period ${i}")

        def comp_stats = ComponentStatistic.executeQuery("from ComponentStatistic where componentType = :comType and year = :year and month = :month", [comType: component_data.componentName, year: calendar.get(Calendar.YEAR), month: calendar.get(Calendar.MONTH)], [readOnly: true])[0]
        def cur_month = calendar.get(Calendar.MONTH) + 1

        String xVal = "${calendar.get(Calendar.YEAR)}-${cur_month < 10 ? '0'+ cur_month : cur_month}"

        if(!(xVal in result.xAxis))
        result.xAxis << xVal

        result.components."${component_name}".allComponentsData << comp_stats?.numTotal
        result.components."${component_name}".newComponentsData << comp_stats?.numNew


        calendar.add(Calendar.MONTH, 1)

      }
    }

    //log.debug("${result}")
    result
  }

}

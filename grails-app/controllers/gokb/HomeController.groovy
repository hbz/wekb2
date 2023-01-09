package gokb

import de.wekb.helper.RCConstants
import org.gokb.cred.*
import org.hibernate.SessionFactory
import org.springframework.security.access.annotation.Secured

@Secured(['IS_AUTHENTICATED_FULLY'])
class HomeController {

  def springSecurityService
  def passwordEncoder
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

    redirect(action:'statistic')
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def userdash() {
    def result = [:]
    result.user = springSecurityService.currentUser

    result.saved_items = SavedSearch.executeQuery('Select ss from SavedSearch as ss where ss.owner = :user order by name',[user: result.user])

    result
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def dash() {
    def result=[:]

    User user = springSecurityService.currentUser
    def active_status = RefdataCategory.lookupOrCreate(RCConstants.ACTIVITY_STATUS, 'Active')
    def needs_review_status = RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Needs Review')

    result.openActivities = Activity.findAllByOwnerAndStatus(user,active_status)
    result.recentlyClosedActivities = Activity.findAllByOwnerAndStatusNotEqual(user,active_status,[max: 10, sort: "lastUpdated", order: "desc"])

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
    if ( params.newpass == params.repeatpass ) {
      User user = springSecurityService.currentUser
      if ( passwordEncoder.isPasswordValid(user.password, params.origpass, null) ) {
        user.password = params.newpass
        user.save(flush:true, failOnError:true);
        flash.success = "Password Changed!"
      }
      else {
        flash.error = "Existing password does not match: not changing"
      }
    }
    else {
      flash.error = "New password does not match repeat password: not changing"
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

        def comp_stats = ComponentStatistic.executeQuery("from ComponentStatistic where componentType = ? and year = ? and month = ?", [component_data.componentName, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)], [readOnly: true])[0]
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

package wekb

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.security.web.savedrequest.DefaultSavedRequest
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import wekb.helper.RCConstants
import wekb.helper.RDStore
import wekb.utils.ServerUtils
import grails.plugins.mail.MailService

import javax.servlet.ServletOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId

class PublicController {

  def genericOIDService
  def dateFormatService
  ExportService exportService
  MailService mailService
  SearchService searchService

  def robots() {
    String text = "User-agent: *\n"

    if(ServerUtils.getCurrentServer() == ServerUtils.SERVER_PROD) {
      text += "Disallow: /search/index/ \n"
      text += "Disallow: /search/componentSearch/ \n"
    }
    else {
      text += "Disallow: / \n"
    }
    render(text: text, contentType: "text/plain", encoding: "UTF-8")
  }

  def wcagPlainEnglish() {
    log.info("wcagPlainEnglish::${params}")
    def result = [:]
    //println(params)
    result
  }

  def sendFeedbackForm() {
    def result = [:]
    try {

      mailService.sendMail {
        to 'barrierefreiheitsbelange@hbz-nrw.de'
        from 'laser@hbz-nrw.de'
        subject grailsApplication.config.getProperty('systemId', String) + ' - Feedback-Mechanismus Barrierefreiheit'
        body (view: '/mailTemplate/text/wcagFeedback', model: [name:params.name, email:params.eMail, url:params.url, comment:params.comment])

      }
    }
    catch (Exception e) {
      println "Unable to perform email due to exception ${e.message}"
    }
    result
  }

  def wcagFeedbackForm() {
    log.info("wcagFeedbackForm::${params}")
    def result = [:]
    //println(params)
    result
  }

  def packageContent() {
    log.info("packageContent::${params}")
    logRequestFrom()
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def tippContent() {
    log.info("tippContent::${params}")
    logRequestFrom()
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def identifierContent() {
    log.info("identifierContent::${params}")
    logRequestFrom()
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def orgContent() {
    log.info("orgContent::${params}")
    logRequestFrom()
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def sourceContent() {
    log.info("sourceContent::${params}")
    logRequestFrom()
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def platformContent() {
    log.info("tippContent::${params}")
    logRequestFrom()
    redirect(controller: 'resource', action: 'show', id: params.id)
  }


  def index() {
    log.info("PublicController::index ${params}");
    logRequestFrom()
    def result = [:]

    def searchResult = [:]

    params.qbe = 'g:publicPackages'
    searchResult = searchService.search(null, searchResult, params)

    result = searchResult.result

    //result.s_action = actionName
    //result.s_controller = controllerName


    //for statistic panel
    Map query_params = [forbiddenStatus : [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_EXPECTED]]

    //List providerRoles = [RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Content Provider'), RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Platform Provider'), RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Publisher')]

    //def query_params2 = [forbiddenStatus : [RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_REMOVED], roles: providerRoles]

    def query_params2 = [forbiddenStatus : [RDStore.KBC_STATUS_REMOVED]]

    result.componentsOfStatistic = ["Provider", "Package", "Platform", "TitleInstancePackagePlatform"]

    result.countComponent = [:]
    result.componentsOfStatistic.each { component ->
      if(component == "Provider"){
        //result.countComponent."${component.toLowerCase()}" = Org.executeQuery("select count(o.id) from Org as o where exists (select orgRoles from o.roles as orgRoles where orgRoles in (:roles)) and o.status not in (:forbiddenStatus)", query_params2, [readOnly: true])[0]
        result.countComponent."${component.toLowerCase()}" = Org.executeQuery("select count(*) from Org as o where o.status in (:forbiddenStatus)", query_params, [readOnly: true])[0]
      }else {
        def fetch_all = "select count(*) from ${component} as o where status in (:forbiddenStatus)"
        result.countComponent."${component.toLowerCase()}" = Package.executeQuery(fetch_all.toString(), query_params, [readOnly: true])[0]
      }

    }

   /* params.max = mutableParams.max
    params.offset = mutableParams.offset
    params.remove('newMax')
    params.remove('search')*/

    result
  }

  def kbart() {
    log.info("kbart::${params}")
    wekb.Package pkg = genericOIDService.resolveOID(params.id)

    if(!pkg){
      pkg = wekb.Package.findByUuid(params.id)
    }

    if(!pkg){
      response.sendError(404)
      return
    }

    String export_date = dateFormatService.formatDate(new Date());

    String filename = "kbart_${pkg.name}_${export_date}.txt"

    try {
      logRequestFrom()
      response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

      def out = response.outputStream

      exportService.exportOriginalKBART(out, pkg)
      return

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
      flash.error = 'Kbart export not possible at the moment! '
      redirect(url: request.getHeader('referer'))
    }
  }

  def packageTSVExport() {
    log.info("packageTSVExport::${params}")
    wekb.Package pkg = genericOIDService.resolveOID(params.id)

    if(!pkg){
      pkg = wekb.Package.findByUuid(params.id)
    }

    if(!pkg || (pkg && pkg.getTippCount() > 200000)){
      flash.error = 'The export of the selected number of titles unfortunately exceeds the line limitation provided in Excel, so that only a smaller fraction of the selected titles can be exported. For a complete title list please contact the content provider directly.'
      redirect(url: request.getHeader('referer'))
      return
    }

    String export_date = dateFormatService.formatDate(new Date());

    String filename = "wekb_package_${pkg.name.toLowerCase()}_${export_date}"

    try {
      logRequestFrom()
      List status = []

      if("Current" in params.list('status') || "Current" == params.status){
        status << RDStore.KBC_STATUS_CURRENT
      }
      if("Retired" in params.list('status') || "Retired" == params.status){
        status << RDStore.KBC_STATUS_RETIRED
      }
      if("Expected" in params.list('status') || "Expected" == params.status){
        status << RDStore.KBC_STATUS_EXPECTED
      }
      if("Deleted" in params.list('status') || "Deleted" == params.status){
        status << RDStore.KBC_STATUS_DELETED
      }

      if(status.size() == 0){
        status = [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_EXPECTED, RDStore.KBC_STATUS_DELETED]
      }


      //Map<String,List> export = exportService.exportPackageTippsAsTSVNew(pkg, status)
      Map<String,List> export = exportService.exportPackageTippsAsTSVWithSQL(pkg, status)

      if(params.exportFormat == 'xcel') {
        response.setHeader("Content-disposition", "attachment; filename=${filename}.xlsx")
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        Map sheetData = [:]

        /*List columnData = []

        export.columnData.each { row ->
          List rowData = []
          row.each {
            Map rowMap = [:]
            rowMap.field = it.value
            rowData << rowMap
          }
          columnData << rowData
        }
        */
        XSSFWorkbook workbook = exportService.generateXLSXWorkbook('Package_Export', export.titleRow, export.columnData)
        workbook.write(response.outputStream)
        response.outputStream.flush()
        response.outputStream.close()
        return
      }else {
        response.setContentType('text/tab-separated-values');
        response.setHeader("Content-disposition", "attachment; filename=${filename}.tsv")
        String exportString = exportService.generateSeparatorTableString(export.titleRow, export.columnData, '\t')

        ServletOutputStream out = response.outputStream
        out.withWriter { writer ->
          writer.write("we:kb Export : Provider (${pkg.provider?.name}) : Package (${pkg.name}) : ${export_date}\n")
          writer.write(exportString)
        }
        out.flush()
        out.close()
      }
      return

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
      flash.error = 'Bug in Export. Export not possible at the moment! '
      redirect(url: request.getHeader('referer'))
      return
    }
  }

  def ygor() {
    log.info("ygor::${params}")
    def result = [:]
    result
  }

  private void logRequestFrom(){
    log.info 'Request from ' + request.getRemoteAddr() + ' for ' + request.requestURI + ' ---> Host: ' + request.getRemoteHost() + ''

  }

  def wekbNews(){
    log.info("wekbNews::${params}")
    Map result = [:]

    result.dateNow = new Date()

    Date dateFor14Days = Date.from(LocalDate.now().minusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant())

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd")

    result.dateFor14Days =  format.format(dateFor14Days)

    List newsAboutObjects = [//'CuratoryGroup',
                             //'KbartSource',
                             'Org',
                             'Package',
                             'Platform',
                             'TitleInstancePackagePlatform',
                             'Vendor'
    ]

    List status = [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_EXPECTED]
    result.news = [:]

    newsAboutObjects.each{ String domainClassName ->
      result.news[domainClassName.toLowerCase()] = [:]

      String queryNewCount = "select count(*) from ${domainClassName} where  status in (:status) and dateCreated >= :daysBefore"
      result.news[domainClassName.toLowerCase()] .countNewInDB = Package.executeQuery(queryNewCount, [status: status, daysBefore: dateFor14Days])[0]
      String queryLastUpdatedCount = "select count(*) from ${domainClassName} where TO_CHAR(dateCreated,'YYYY-MM-DD') != TO_CHAR(lastUpdated,'YYYY-MM-DD') and status in (:status) and lastUpdated >= :daysBefore"
      result.news[domainClassName.toLowerCase()] .countLastUpdatedInDB = Package.executeQuery(queryLastUpdatedCount, [status: status, daysBefore: dateFor14Days])[0]


      String queryNew = "from ${domainClassName} where  status in (:status) and dateCreated >= :daysBefore order by dateCreated desc"
      result.news[domainClassName.toLowerCase()] .newInDB = Package.executeQuery(queryNew, [status: status, daysBefore: dateFor14Days], [max: 50, offset: 0])
      String queryLastUpdated = "from ${domainClassName} where TO_CHAR(dateCreated,'YYYY-MM-DD') != TO_CHAR(lastUpdated,'YYYY-MM-DD') and status in (:status) and lastUpdated >= :daysBefore order by lastUpdated desc"
      result.news[domainClassName.toLowerCase()] .lastUpdatedInDB = Package.executeQuery(queryLastUpdated, [status: status, daysBefore: dateFor14Days], [max: 50, offset: 0])

    }

    result
  }
}

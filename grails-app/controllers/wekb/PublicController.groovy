package wekb

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import wekb.helper.RCConstants
import wekb.helper.RDStore
import wekb.utils.ServerUtils
import grails.plugins.mail.MailService

import javax.servlet.ServletOutputStream

class PublicController {

  def genericOIDService
  def dateFormatService
  ExportService exportService
  MailService mailService
  SearchService searchService

  def robots() {
    if(ServerUtils.getCurrentServer() != ServerUtils.SERVER_PROD) {
      def text = "User-agent: *\n"+
              "Disallow: / \n"
      render(text: text, contentType: "text/plain", encoding: "UTF-8")
    }
    else render (status: 404, text: 'Failed to load robots.txt')
  }

  def wcagPlainEnglish() {
    log.debug("wcagPlainEnglish::${params}")
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
    log.debug("wcagFeedbackForm::${params}")
    def result = [:]
    //println(params)
    result
  }

  def packageContent() {
    log.debug("packageContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def tippContent() {
    log.debug("tippContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def identifierContent() {
    log.debug("identifierContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def orgContent() {
    log.debug("orgContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def sourceContent() {
    log.debug("sourceContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def platformContent() {
    log.debug("tippContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }


  def index() {
    log.debug("PublicController::index ${params}");
    def result = [:]

    def searchResult = [:]

    params.qbe = 'g:publicPackages'
    searchResult = searchService.search(null, searchResult, params, response.format)

    result = searchResult.result

    //result.s_action = actionName
    //result.s_controller = controllerName


    //for statistic panel
    def query_params = [forbiddenStatus : [RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_REMOVED]]

    List providerRoles = [RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Content Provider'), RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Platform Provider'), RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Publisher')]

    def query_params2 = [forbiddenStatus : [RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_REMOVED], roles: providerRoles]

    result.componentsOfStatistic = ["Provider", "Package", "Platform", "CuratoryGroup", "TitleInstancePackagePlatform"]

    result.countComponent = [:]
    result.componentsOfStatistic.each { component ->
      if(component == "Provider"){
        result.countComponent."${component.toLowerCase()}" = Org.executeQuery("select count(o.id) from Org as o join o.roles rdv where rdv in (:roles) and o.status not in (:forbiddenStatus)", query_params2, [readOnly: true])[0]
      }else {
        def fetch_all = "select count(o.id) from ${component} as o where status not in (:forbiddenStatus)"
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

      response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

      def out = response.outputStream

      exportService.exportOriginalKBART(out, pkg)
      return

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
    }
  }

  def packageTSVExport() {

    wekb.Package pkg = genericOIDService.resolveOID(params.id)

    if(!pkg){
      pkg = wekb.Package.findByUuid(params.id)
    }

    if(!pkg){
      response.sendError(404)
      return
    }

    String export_date = dateFormatService.formatDate(new Date());

    String filename = "wekb_package_${pkg.name.toLowerCase()}_${export_date}"

    try {

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


      Map<String,List> export = exportService.exportPackageTippsAsTSVNew(pkg, status)

      if(params.exportFormat == 'xcel') {
        response.setHeader("Content-disposition", "attachment; filename=${filename}.xlsx")
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        Map sheetData = [:]

        List columnData = []

        export.rows.each { row ->
          List rowData = []
          row.each {
            Map rowMap = [:]
            rowMap.field = it
            rowData << rowMap
          }
          columnData << rowData
        }

        XSSFWorkbook workbook = exportService.generateXLSXWorkbook('Package_Export', export.titleRow, columnData)
        workbook.write(response.outputStream)
        response.outputStream.flush()
        response.outputStream.close()
        return
      }else {
        response.setContentType('text/tab-separated-values');
        response.setHeader("Content-disposition", "attachment; filename=${filename}.tsv")

        ServletOutputStream out = response.outputStream
        out.withWriter { writer ->
          writer.write("we:kb Export : Provider (${pkg.provider?.name}) : Package (${pkg.name}) : ${export_date}\n");
          writer.write(exportService.generateSeparatorTableString(export.titleRow, export.rows, '\t'))
        }
        out.flush()
        out.close()
      }
      return

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
    }
  }

  def ygor() {
    log.debug("ygor::${params}")
    def result = [:]
    result
  }
}

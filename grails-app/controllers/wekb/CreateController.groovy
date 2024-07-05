package wekb


import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.mozilla.universalchardet.UniversalDetector
import org.springframework.security.access.annotation.Secured
import org.springframework.web.multipart.MultipartFile
import wekb.auth.User

@Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
class CreateController {

  ClassExaminationService classExaminationService
  SpringSecurityService springSecurityService
  DisplayTemplateService displayTemplateService
  AccessService accessService
  ExportService exportService
  CreateComponentService createComponentService


  def index() {
    log.debug("CreateControler::index... ${params}");
    def result=[:]
    User user = springSecurityService.currentUser

    if ((params.tmpl in accessService.allowedToCreate && SpringSecurityUtils.ifAnyGranted("ROLE_EDITOR")) || SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")) {

      // Create a new empty instance of the object to create
      result.newclassname = params.tmpl
      if (params.tmpl) {
        def newclass = grailsApplication.getArtefact("Domain", result.newclassname)
        if (newclass) {
          log.debug("Got new class")
          try {
            result.displayobj = newclass.getClazz().getDeclaredConstructor().newInstance()
            log.debug("Got new instance");
            result.editable = true
            result.createObject = true

            if (params.tmpl) {
              result.displaytemplate = displayTemplateService.getTemplateInfo(params.tmpl)
              result.displayobjclassname_short = result.displayobj.class.simpleName

              if(result.displayobjclassname_short == TitleInstancePackagePlatform.class.simpleName)
              {
                if(params.linkwithPkg){
                  result.displayobj.pkg = Package.get(params.linkwithPkg)
                }
                if(params.linkwithPlatform){
                  result.displayobj.hostPlatform = Platform.get(params.linkwithPlatform)
                }

                if(params.linkwithStatus){
                  result.displayobj.status = RefdataValue.get(params.linkwithStatus)
                }
              }

            }
          }
          catch (Exception e) {
            log.error("Problem", e);
          }
        } else {
          log.info("No Permission for ${result.newclassname} in CreateControler::index... ${params}");
          response.sendError(401)
          return
        }
      }

    }else{
      flash.error = "Your are not allowed to create this component!"
    }
    result
  }

  def process() {
    log.debug("CreateController::process... ${params}");

    def result=[:]

    result = createComponentService.process(result, params)

    /*if(result.error)
      flash.error = result.error

    if(result.message)
      flash.message = result.message*/

    log.debug("CreateController::process return ${result}");

    result = result as JSON
    render result

    /*if(result.urlMap) {
      redirect(result.urlMap)
    }else {
      redirect(url: request.getHeader('referer'))
    }*/
  }

  def exportPackageBatchImportTemplate() {

    String filename = "template_package_import.xlsx"

    try {

      response.setHeader("Content-disposition","attachment; filename=\"${filename}\"")
      response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

      def out = response.outputStream

      exportService.exportPackageBatchImportTemplate(out)

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
    }
  }

  def packageBatch() {
    log.debug("CreateControler::packageBatch... ${params}");
    def result=[:]
    User user = springSecurityService.currentUser

    result.mappingCols = ["package_uuid", "package_name", "provider_uuid", "nominal_platform_uuid", "description", "description_url", "breakable", "content_type",
            "file", "open_access", "payment_type", "scope", "national_range", "regional_range", "provider_product_id", "ddc", "source_default_supply_method", "source_url", "source_ftp_server_url", "source_ftp_directory", "source_ftp_file_name", "source_ftp_username", "source_ftp_password", "frequency", "automated_updates", "archiving_agency", "open_access_of_archiving_agency", "post_cancellation_access_of_archiving_agency"]

    result
  }

  def processPackageBatch() {
    log.debug("CreateControler::processPackageBatch... ${params}");
        User user = springSecurityService.currentUser
        MultipartFile tsvFile = request.getFile("tsvFile")
        if(tsvFile && tsvFile.size > 0) {
          String encoding = UniversalDetector.detectCharset(tsvFile.getInputStream())
          if(encoding in ["UTF-8", "US-ASCII", "WINDOWS-1252"]) {
            Map packagesData = createComponentService.packageBatchImport(tsvFile, user)

            render view: 'packageBatchCompleted', model: packagesData
          }
          else {
            String errorText = "The file you have uploaded has a wrong character encoding! Please ensure that your file is encoded in UTF-8. Guessed encoding has been: ${encoding}"
            flash.error = errorText
            redirect(url: request.getHeader('referer'))
          }
        }
        else {
          String errorText = "You have not uploaded a valid file!"
          flash.error = errorText

          redirect(url: request.getHeader('referer'))
        }
  }
}

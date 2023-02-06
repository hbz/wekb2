package wekb

import wekb.helper.RCConstants
import org.springframework.security.access.annotation.Secured

import java.util.concurrent.ExecutorService

@Secured(['IS_AUTHENTICATED_FULLY'])
class WorkflowController{

  def genericOIDService
  def springSecurityService
  def reviewRequestService
  def dateFormatService
  GlobalSearchTemplatesService globalSearchTemplatesService
  ExportService exportService
  AutoUpdatePackagesService autoUpdatePackagesService
  AccessService accessService
  KbartImportService kbartImportService
  ExecutorService executorService

  def actionConfig = [
      'deleteIdentifierNamespace'        : [actionType: 'process', method: 'deleteIdentifierNamespace'],

      'manualKbartImport'  : [actionType: 'redirectToView', view: 'kbartImport', controller: 'package'],

      'method::deleteSoft'     : [actionType: 'simple'],
      'method::retire'         : [actionType: 'simple'],
      'method::removeWithTipps' : [actionType: 'simple'],
      'method::currentWithTipps': [actionType: 'simple'],
      'method::setCurrent'      : [actionType: 'simple'],

      'packageUrlUpdate'       : [actionType: 'process', method: 'triggerSourceUpdate'],
      'packageUrlUpdateAllTitles':[actionType:'process', method: 'triggerSourceUpdateAllTitles'],

      'setStatus::Retired'     : [actionType: 'simple'],
      'setStatus::Current'     : [actionType: 'simple'],
      'setStatus::Expected'    : [actionType: 'simple'],
      'setStatus::Deleted'     : [actionType: 'simple'],
      'setStatus::Removed'     : [actionType: 'simple'],
  ]


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def action(){
    log.debug("WorkflowController::action(${params})")
    def result = [:]
    result.ref = request.getHeader('referer')

    def action_config = actionConfig[params.selectedBulkAction]

    if (action_config){
      result.objects_to_action = []

      if (params.batch_on == 'all'){
        log.debug("Requested batch_on all.. so evaluate the query and do the right thing...")
        if (params.qbe){
          def qresult = [:]
          if (params.qbe.startsWith('g:')){
            // Global template, look in config
            def global_qbe_template_shortcode = params.qbe.substring(2, params.qbe.length())
            // log.debug("Looking up global template ${global_qbe_template_shortcode}")
            qresult.qbetemplate = globalSearchTemplatesService.getGlobalSearchTemplate(global_qbe_template_shortcode)
            // log.debug("Using template: ${result.qbetemplate}")
          }

          // Looked up a template from somewhere, see if we can execute a search
          if (qresult.qbetemplate){
            log.debug("Execute query")
            // doQuery(result.qbetemplate, params, result)
            def target_class = grailsApplication.getArtefact("Domain", qresult.qbetemplate.baseclass)
            com.k_int.HQLBuilder.build(grailsApplication, qresult.qbetemplate, params, qresult, target_class, genericOIDService)

            qresult.recset.each{
              def oid_to_action = "${it.class.name}:${it.id}"
              result.objects_to_action.add(genericOIDService.resolveOID(oid_to_action))
            }
          }
        }
      }else if(params.component){
        def component = genericOIDService.resolveOID(params.component)
        if(component){
          result.objects_to_action.add(component)
        }
      }
      else{
        log.debug("Assuming standard selection of rows to action")
        params.each{ p ->
          if ((p.key.startsWith('bulk:')) && (p.value) && (p.value instanceof String)){
            def oid_to_action = p.key.substring(5)
            result.objects_to_action.add(genericOIDService.resolveOID(oid_to_action))
          }
        }
      }

      switch (action_config.actionType){
        case 'simple':
          def method_config = params.selectedBulkAction.split(/\:\:/) as List
          switch (method_config[0]){
            case "method":
              def context = [user: springSecurityService.currentUser]
              // Everything after the first 2 "parts" are args for the method.
              def method_params = []
              method_params.add(context)
              if (method_config.size() > 2){
                method_params.addAll(method_config.subList(2, method_config.size()))
              }
              // We should just call the method on the targets.
              result.objects_to_action.each{ def target ->
                log.debug("Target: ${target} (${target.class.name})")
                log.debug("Attempting to fire method ${method_config[1]} (${method_params})")
                // Wrap in a transaction.
                KBComponent.withTransaction{ def trans_status ->
                  try{
                    // Just try and fire the method.
                    target.invokeMethod("${method_config[1]}", method_params ? method_params as Object[] : null)
                    // Save the object.
                    target.save(failOnError: true)
                  }
                  catch (Throwable t){
                    // Rollback and log error.
                    trans_status.setRollbackOnly()
                    t.printStackTrace()
                    log.error("${t}")
                  }
                }
                // target.save(flush: true, failOnError:true)
                log.debug("After transaction: ${target?.status}")
              }
              result.objects_to_action.each{
                log.debug("${it.status}")
              }
              break
            case "setStatus":
              log.debug("SetStatus: ${method_config[1]}")
              def status_to_set = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, method_config[1])
              // def ota_ids = result.objects_to_action.collect{ it.id }
              if (status_to_set){
                def res = KBComponent.executeUpdate("update KBComponent as kbc set kbc.status = :st, kbc.lastUpdated = :currentDate where kbc IN (:clist)", [st: status_to_set, clist: result.objects_to_action, currentDate: new Date()])
                log.debug("Updated status of ${res} components")
              }
              break
          }
          // Do stuff
          redirect(url: result.ref)
          break
        case 'workflow':
          render view: action_config.view, model: result
          break
        case 'redirectToView':
          if(result.objects_to_action.size() == 1){
            redirect(controller: action_config.controller, action: action_config.view, id: result.objects_to_action[0].id)
          }else {
            flash.error = "This action can only be performed for one component! Try again, but only with one component."
          }

          break
        case 'process':
          this."${action_config.method}"(result.objects_to_action)
          break
        default:
          flash.error = "Invalid action type information: ${action_config.actionType}".toString()
          break
      }
    }
    else{
      flash.error = "Unable to locate action config for ${params.selectedBulkAction}".toString()
      log.warn("Unable to locate action config for ${params.selectedBulkAction}")
      redirect(url: result.ref)
    }
  }

  private def triggerSourceUpdateAllTitles(packages_to_update) {
    triggerSourceUpdate(packages_to_update, true)
  }

  private def triggerSourceUpdate(packages_to_update, boolean allTitles=false) {
    log.info("triggerSourceUpdate for Packages ${packages_to_update}..")
    def user = springSecurityService.currentUser
    def pars = [:]
    def denied = false

    boolean userAdmin = user.isAdmin()

    if (packages_to_update.size() > 1){
      flash.error = "Please select a single Package to update!"
    }
    else{
      packages_to_update.each{ ptv ->
        def pkgObj = Package.get(ptv.id)

        if (pkgObj && pkgObj.kbartSource?.url){

          if (accessService.checkEditableObject(pkgObj, userAdmin)) {
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet()
            Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()])
            boolean processRunning = false
            threadArray.each { Thread thread ->
              if (thread.name == 'triggerSourceUpdate' + pkgObj.id) {
                processRunning = true
              }
            }

            if(processRunning){
              flash.error = 'A package update is already in progress. Please wait this has finished.'
            }else {
              executorService.execute({
                Package aPackage = Package.get(pkgObj.id)
                Thread.currentThread().setName('triggerSourceUpdate_' + pkgObj.id)
                autoUpdatePackagesService.startAutoPackageUpdate(aPackage, !allTitles)
              })

              flash.success = "The package update for Package '${pkgObj.name}' was started. This runs in the background. When the update has gone through, you will see this on the Auto Update Info of the package tab."
            }

          }
          else{
            flash.error = "No permissions to update this Package!"
          }
        }
        else if (!pkgObj){
          flash.error = "Unable to reference provided Package!"
        }
        else{
          flash.error = "Please check the Package KbartSource for validity!"
        }
      }
    }
    log.debug('triggerSourceUpdate() done - redirecting')
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY'])
  def deleteIdentifierNamespace(identifierNamespaces) {
    log.info("deleteIdentifierNamespace ${identifierNamespaces}..")
    identifierNamespaces.each { idn ->
      IdentifierNamespace identifierNamespace = IdentifierNamespace.get(idn.id)
      if(!Platform.findByTitleNamespace(identifierNamespace) && !KbartSource.findByTargetNamespace(identifierNamespace) && !Identifier.findByNamespace(identifierNamespace)){
        identifierNamespace.delete(flush: true)
      }else {
        flash.error = "Identifier Namespace is linked with identifier or org or source or platform! Please unlink first!"
      }


    }
    redirect(url: request.getHeader('referer'))
  }
}

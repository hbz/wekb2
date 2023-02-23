package wekb

import wekb.helper.RCConstants
import org.springframework.security.access.annotation.Secured

import java.util.concurrent.ExecutorService

@Secured(['IS_AUTHENTICATED_FULLY'])
class WorkflowController{

  def genericOIDService
  def springSecurityService
  GlobalSearchTemplatesService globalSearchTemplatesService

  WorkflowService workflowService


  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def action(){
    log.debug("WorkflowController::action(${params})")
    def result = [:]
    result.ref = request.getHeader('referer')

    result.objects_to_action = []

    if(params.component){
      def component = genericOIDService.resolveOID(params.component)
      if(component){
        result.objects_to_action.add(component)
      }
    }

    if(params.selectedAction){
      result.selectedAction = params.selectedAction
    }

      try {
          result = workflowService.processAction(result, params)
      }catch (Exception e) {
          log.error("Problem by processAction : ${e.message}")
          println("Problem by processAction : ${e.printStackTrace()}")
          flash.error = "The operation could not be performed. An unexpected error has occurred. Try again later."
      }

      if(result.error){
          flash.error = result.error
      }
      if(result.success){
          flash.success = result.success
      }

    redirect(url: result.ref)
  }

/*  @Deprecated
  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def actionOld() {
    log.debug("WorkflowController::action(${params})")
    def result = [:]
    result.ref = request.getHeader('referer')

    def action_config = actionConfig[params.selectedBulkAction]

    if (action_config) {
      result.objects_to_action = []

      if (params.batch_on == 'all') {
        log.debug("Requested batch_on all.. so evaluate the query and do the right thing...")
        if (params.qbe) {
          def qresult = [:]
          if (params.qbe.startsWith('g:')) {
            // Global template, look in config
            def global_qbe_template_shortcode = params.qbe.substring(2, params.qbe.length())
            // log.debug("Looking up global template ${global_qbe_template_shortcode}")
            qresult.qbetemplate = globalSearchTemplatesService.getGlobalSearchTemplate(global_qbe_template_shortcode)
            // log.debug("Using template: ${result.qbetemplate}")
          }

          // Looked up a template from somewhere, see if we can execute a search
          if (qresult.qbetemplate) {
            log.debug("Execute query")
            // doQuery(result.qbetemplate, params, result)
            def target_class = grailsApplication.getArtefact("Domain", qresult.qbetemplate.baseclass)
            HQLBuilder.build(grailsApplication, qresult.qbetemplate, params, qresult, target_class, genericOIDService)

            qresult.recset.each {
              def oid_to_action = "${it.class.name}:${it.id}"
              result.objects_to_action.add(genericOIDService.resolveOID(oid_to_action))
            }
          }
        }
      } else if (params.component) {
        def component = genericOIDService.resolveOID(params.component)
        if (component) {
          result.objects_to_action.add(component)
        }
      } else {
        log.debug("Assuming standard selection of rows to action")
        params.each { p ->
          if ((p.key.startsWith('bulk:')) && (p.value) && (p.value instanceof String)) {
            def oid_to_action = p.key.substring(5)
            result.objects_to_action.add(genericOIDService.resolveOID(oid_to_action))
          }
        }
      }

      switch (action_config.actionType) {
        case 'simple':
          def method_config = params.selectedBulkAction.split(/\:\:/) as List
          switch (method_config[0]) {
            case "method":
              def context = [user: springSecurityService.currentUser]
              // Everything after the first 2 "parts" are args for the method.
              def method_params = []
              method_params.add(context)
              if (method_config.size() > 2) {
                method_params.addAll(method_config.subList(2, method_config.size()))
              }
              // We should just call the method on the targets.
              result.objects_to_action.each { def target ->
                log.debug("Target: ${target} (${target.class.name})")
                log.debug("Attempting to fire method ${method_config[1]} (${method_params})")
                // Wrap in a transaction.
                Package.withTransaction { def trans_status ->
                  try {
                    // Just try and fire the method.
                    target.invokeMethod("${method_config[1]}", method_params ? method_params as Object[] : null)
                    // Save the object.
                    target.save(failOnError: true)
                  }
                  catch (Throwable t) {
                    // Rollback and log error.
                    trans_status.setRollbackOnly()
                    t.printStackTrace()
                    log.error("${t}")
                  }
                }
                // target.save(flush: true, failOnError:true)
                log.debug("After transaction: ${target?.status}")
              }
              result.objects_to_action.each {
                log.debug("${it.status}")
              }
              break
            case "setStatus":
              log.debug("SetStatus: ${method_config[1]}")
              def status_to_set = RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, method_config[1])
              // def ota_ids = result.objects_to_action.collect{ it.id }
              if (status_to_set) {
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
          if (result.objects_to_action.size() == 1) {
            redirect(controller: action_config.controller, action: action_config.view, id: result.objects_to_action[0].id)
          } else {
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
    } else {
      flash.error = "Unable to locate action config for ${params.selectedBulkAction}".toString()
      log.warn("Unable to locate action config for ${params.selectedBulkAction}")
      redirect(url: result.ref)
    }
  }*/
}

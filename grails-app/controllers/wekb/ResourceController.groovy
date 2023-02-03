package wekb

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.access.annotation.Secured
import wekb.auth.User

class ResourceController {

  GenericOIDService genericOIDService
  SpringSecurityService springSecurityService
  DisplayTemplateService displayTemplateService
  AccessService accessService

  def index() {
  }

  def show() {
    log.debug("ResourceController::show ${params}")
    User user = springSecurityService.currentUser

    log.debug("ResourceController::show ${params}");
    def result = ['params':params]
    def oid = params.id
    def displayobj = null
    Boolean read_perm = false

    if (params.type && params.id) {
      oid = "wekb." + params.type + ":" + params.id
    } else if (params.int('id')) {
      displayobj = KBComponent.get(params.int('id'))
      oid = (displayobj ? (displayobj.class.name + ":" + params.id) : null)
    }

    if (oid) {
      displayobj = KBComponent.findByUuid(oid)

      if (!displayobj) {
        displayobj = genericOIDService.resolveOID(oid)
      }

      if (displayobj) {

        if ((displayobj.class.simpleName in accessService.allowedPublicShow) || (springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN"))) {

          result.displayobjclassname = displayobj.class.name
          //result.__oid = "${result.displayobjclassname}:${displayobj.id}"

          log.debug("Looking up display template for ${result.displayobjclassname}")

          result.displaytemplate = displayTemplateService.getTemplateInfo(result.displayobjclassname)

          log.debug("Using displaytemplate: ${result.displaytemplate}")

          result.displayobjclassname_short = displayobj.class.simpleName

          //result.isComponent = (displayobj instanceof KBComponent)

          result.displayobj = displayobj

          if (springSecurityService.isLoggedIn()) {
            read_perm = accessService.checkReadable(displayobj.class.name)

            if (read_perm) {

              if (displayobj instanceof wekb.Package) {
                displayobj.createCoreIdentifiersIfNotExist()
              }

              // Need to figure out whether the current user has curatorial rights (or is an admin).
              // Defaults to true as not all components have curatorial groups defined.

              def curatedObj = displayobj.respondsTo("getCuratoryGroups") ? displayobj : (displayobj.hasProperty('pkg') ? displayobj.pkg : false)

              if (curatedObj && curatedObj.curatoryGroups && curatedObj.niceName != 'User' && user.curatoryGroupUsers) {
                def cur = user.curatoryGroupUsers.curatoryGroup.id.intersect(curatedObj.curatoryGroups.curatoryGroup.id) ?: []
                request.curator = cur
              } else {
                request.curator = null
              }

              result.editable = accessService.checkEditableObject(displayobj, params)

              // Add any refdata property names for this class to the result.
              //result.refdata_properties = classExaminationService.getRefdataPropertyNames(result.displayobjclassname)

            } else {
              flash.error = "You have no permission to view this resource."
                result.noPermission = true
            }
          }
        } else {
            flash.error = "You have no permission to view this resource."
            result.noPermission = true
        }
      } else {
        log.debug("unable to resolve object")
        flash.error = "Unable to find the requested resource."
      }
    }else {
      flash.error = "Unable to find the requested resource."
    }
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def showLogin() {
    redirect(action: 'show', params: params)
  }
}

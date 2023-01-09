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
  SearchService searchService

  def index() {
  }

  def show() {

    User user = springSecurityService.currentUser

    log.debug("ResourceController::show ${params}");
    def result = ['params':params]
    def oid = params.id
    def displayobj = null
    def read_perm = false

    if (params.type && params.id) {
      oid = "wekb." + params.type + ":" + params.id
    }
    else if (params.int('id')) {
      displayobj = KBComponent.get(params.int('id'))
      oid = (displayobj ? (displayobj.class.name + ":" + params.id) : null)
    }

    if ( oid ) {
      displayobj = KBComponent.findByUuid(oid)

      if (!displayobj) {
        displayobj = genericOIDService.resolveOID(oid)
      }
      else {
        oid = "${displayobj?.class?.name}:${displayobj?.id}"
      }

      if ( displayobj ) {

        if ((displayobj.class.simpleName in accessService.allowedPublicShow) || (springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN"))) {

          result.displayobjclassname = displayobj.class.name
          result.__oid = "${result.displayobjclassname}:${displayobj.id}"

          log.debug("Looking up display template for ${result.displayobjclassname}")

          result.displaytemplate = displayTemplateService.getTemplateInfo(result.displayobjclassname)

          log.debug("Using displaytemplate: ${result.displaytemplate}")

          result.displayobjclassname_short = displayobj.class.simpleName

          result.isComponent = (displayobj instanceof KBComponent)

          result.displayobj = displayobj

          if(springSecurityService.isLoggedIn()) {
            read_perm = accessService.checkReadable(displayobj.class.name)

            if (read_perm) {

              if (displayobj instanceof wekb.Package) {
                displayobj.createCoreIdentifiersIfNotExist()
              }

              // Need to figure out whether the current user has curatorial rights (or is an admin).
              // Defaults to true as not all components have curatorial groups defined.

              def curatedObj = displayobj.respondsTo("getCuratoryGroups") ? displayobj : (displayobj.hasProperty('pkg') ? displayobj.pkg : false)

              if (curatedObj && curatedObj.curatoryGroups && curatedObj.niceName != 'User') {

                def cur = user.curatoryGroups?.id.intersect(curatedObj.curatoryGroups?.id) ?: []
                request.curator = cur
              } else {
                request.curator = null
              }

              result.editable = accessService.checkEditableObject(displayobj, params)

              // Add any refdata property names for this class to the result.
              //result.refdata_properties = classExaminationService.getRefdataPropertyNames(result.displayobjclassname)

              //result.acl = gokbAclService.readAclSilently(displayobj)

              def oid_components = oid.split(':');
              def qry_params = [result.displayobjclassname, Long.parseLong(oid_components[1])];
              result.ownerClass = oid_components[0]
              result.ownerId = oid_components[1]
              result.num_notes = KBComponent.executeQuery("select count(n.id) from Note as n where ownerClass=? and ownerId=?", qry_params)[0];
              // How many people are watching this object
              result.num_watch = KBComponent.executeQuery("select count(n.id) from ComponentWatch as n where n.component=?", displayobj)[0];
              result.user_watching = KBComponent.executeQuery("select count(n.id) from ComponentWatch as n where n.component=? and n.user=?", [displayobj, user])[0] == 1 ? true : false;
            } else {
              flash.error = "You have no permission to view this resource."
            }
          }
        }else {
          flash.error = "You have no permission to view this resource."
        }
      }
      else {
        log.debug("unable to resolve object")
        flash.error = "Unable to find the requested resource."
      }
    }
        result
    }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def showLogin() {

    redirect(action: 'show', params: params)
  }
}

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

    def result = ['params':params]
    String oid = ''
    def displayobj = null
    Boolean read_perm = false

    if (params.type && params.id) {
      oid = "wekb." + params.type + ":" + params.id
    }
    if(!oid && params.id && params.id.startsWith('wekb.')){
      oid = params.id
    }

    if (oid || params.id) {

      if (oid) {
        displayobj = genericOIDService.resolveOID(oid)
      }else {
        Set<Class> classesWithUuid = [TitleInstancePackagePlatform.class, Package.class, Platform.class, KbartSource.class,
                                      Org.class, Vendor.class, CuratoryGroup.class, Identifier.class, UpdatePackageInfo.class, UpdateTippInfo.class]
        int clscnt = 0
        while(displayobj == null && clscnt < classesWithUuid.size()) {
          displayobj = classesWithUuid[clscnt].findByUuid(params.id)
          clscnt++
        }
        /*
        if (TitleInstancePackagePlatform.findByUuid(params.id)) {
          displayobj = TitleInstancePackagePlatform.findByUuid(params.id)
        } else if (Package.findByUuid(params.id)) {
          displayobj = Package.findByUuid(params.id)
        } else if (Platform.findByUuid(params.id)) {
          displayobj = Platform.findByUuid(params.id)
        } else if (KbartSource.findByUuid(params.id)) {
          displayobj = KbartSource.findByUuid(params.id)
        } else if (Org.findByUuid(params.id)) {
          displayobj = Org.findByUuid(params.id)
        } else if (CuratoryGroup.findByUuid(params.id)) {
          displayobj = CuratoryGroup.findByUuid(params.id)
        } else if (Identifier.findByUuid(params.id)) {
          displayobj = Identifier.findByUuid(params.id)
        } else if (UpdatePackageInfo.findByUuid(params.id)) {
          displayobj = UpdatePackageInfo.findByUuid(params.id)
        } else if (UpdateTippInfo.findByUuid(params.id)) {
          displayobj = UpdateTippInfo.findByUuid(params.id)
        }
        */
      }

      if (displayobj) {

        if ((displayobj.class.simpleName in accessService.allowedPublicShow) || (springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN"))) {

          result.displayobjclassname = displayobj.class.name

          log.debug("Looking up display template for ${result.displayobjclassname}")

          result.displaytemplate = displayTemplateService.getTemplateInfo(result.displayobjclassname)

          log.debug("Using displaytemplate: ${result.displaytemplate}")

          result.displayobjclassname_short = displayobj.class.simpleName

          result.displayobj = displayobj

          if (springSecurityService.isLoggedIn()) {
            read_perm = accessService.checkReadable(displayobj.class.name)

            if (read_perm) {

              if (displayobj instanceof wekb.Package) {
                displayobj.createCoreIdentifiersIfNotExist()
              }

              // Need to figure out whether the current user has curatorial rights (or is an admin).
              // Defaults to true as not all components have curatorial groups defined.

              def curatedObj = displayobj.hasProperty('curatoryGroups') ? displayobj : (displayobj.hasProperty('pkg') ? displayobj.pkg : false)

              if (curatedObj && curatedObj.curatoryGroups && !(curatedObj instanceof User) && user.curatoryGroupUsers) {
                def cur = user.curatoryGroupUsers.curatoryGroup.id.intersect(curatedObj.curatoryGroups.curatoryGroup.id) ?: []
                request.curator = cur
                result.curator = cur
              } else {
                request.curator = null
              }

              result.editable = accessService.checkEditableObject(displayobj, params)

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

package wekb


import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import wekb.auth.User

@Transactional
class AccessService {

    SpringSecurityService springSecurityService

    List allowedBaseClasses = ['wekb.CuratoryGroup',
                               'wekb.system.JobResult',
                               'wekb.IdentifierNamespace',
                               'wekb.Identifier',
                               'wekb.Org',
                               'wekb.Package',
                               'wekb.Platform',
                               'wekb.ReviewRequest',
                               'wekb.TitleInstancePackagePlatform',
                               'wekb.KbartSource',
                               'wekb.UpdatePackageInfo',
                               'wekb.UpdateTippInfo']

    List allowedToCreate = ['wekb.Org',
                            'wekb.Package',
                            'wekb.Platform',
                            'wekb.TitleInstancePackagePlatform',
                            'wekb.KbartSource',]

    List allowedPublicShow = ['UpdatePackageInfo',
                              'UpdateTippInfo',
                              'CuratoryGroup',
                              'Identifier',
                              'Org',
                              'Package',
                              'Platform',
                              'Source',
                              'TitleInstancePackagePlatform']

    List allowedComponentSearch = ["g:updatePackageInfos",
                                   "g:updateTippInfos",
                                   "g:curatoryGroups",
                                   "g:identifiers",
                                   "g:orgs",
                                   "g:packages",
                                   "g:platforms",
                                   "g:tipps",
                                   "g:tippsOfPkg",
                                   "g:sources"]

    List allowedInlineSearch = ["g:updatePackageInfos",
                                "g:updateTippInfos",
                                "g:curatoryGroups",
                                "g:identifiers",
                                "g:orgs",
                                "g:packages",
                                "g:platforms",
                                "g:tipps",
                                "g:tippsOfPkg",
                                "g:sources"]


    boolean checkEditableObject(Object o, GrailsParameterMap grailsParameterMap) {
        checkEditableObject(o, (grailsParameterMap && grailsParameterMap.curationOverride == 'true'))
    }

    boolean checkEditableObject(Object o, boolean curationOverride = false) {
        boolean editable = false

        if (SpringSecurityUtils.ifAnyGranted("ROLE_EDITOR, ROLE_ADMIN, ROLE_SUPERUSER")) {
            def curatedObj = null
            if(o instanceof Identifier){
                curatedObj = o.reference.respondsTo("getCuratoryGroups") ? o.reference : ( o.reference.hasProperty('pkg') ? o.reference.pkg : null )
            }else if(o instanceof Contact){
                curatedObj = o.org
            }
            else {
                curatedObj = o.respondsTo("getCuratoryGroups") ? o : ( o.hasProperty('pkg') ? o.pkg : null )
            }

            User user = springSecurityService.currentUser
            if (curatedObj && curatedObj.curatoryGroups && user.curatoryGroupUsers && !(curatedObj instanceof User)) {

                if(user && user.curatoryGroupUsers.curatoryGroup.id.intersect(curatedObj.curatoryGroups.id).size() > 0)
                {
                    editable = true //SecurityApi.isTypeEditable(o.getClass(), true) ?: (grailsParameterMap.curationOverride == 'true' && user.isAdmin())
                }else {
                    editable = (curationOverride && user.isAdmin()) //SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER') ?: (grailsParameterMap.curationOverride == 'true' && user.isAdmin())
                }
            }else {
                if(o instanceof CuratoryGroup && user && user.curatoryGroupUsers && o.id in user.curatoryGroupUsers.curatoryGroup?.id){
                    editable = SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN')
                }
                else{
                    editable = SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')
                }
            }
        }

        editable

    }
    boolean checkReadable(String baseclassName) {

        if(baseclassName in allowedBaseClasses){
            return true
        }else {
            return SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')
        }


    }

    boolean checkEditable(String baseclassName) {

        if(baseclassName in allowedBaseClasses && SpringSecurityUtils.ifAnyGranted('ROLE_EDITOR')){
            return true
        }else {
            return SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')
        }


    }

    boolean checkDeletable(String baseclassName) {

        if(baseclassName in allowedToCreate){
            return true
        }else {
            return SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')
        }


    }

}

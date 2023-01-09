package wekb


import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.gokb.cred.CuratoryGroup
import org.gokb.cred.Identifier
import org.gokb.cred.User

@Transactional
class AccessService {

    SpringSecurityService springSecurityService

    List allowedBaseClasses = ['org.gokb.cred.ComponentWatch',
                               'org.gokb.cred.CuratoryGroup',
                               'org.gokb.cred.JobResult',
                               'org.gokb.cred.IdentifierNamespace',
                               'org.gokb.cred.Identifier',
                               'org.gokb.cred.Org',
                               'org.gokb.cred.Package',
                               'org.gokb.cred.Platform',
                               'org.gokb.cred.ReviewRequest',
                               'org.gokb.cred.TitleInstancePackagePlatform',
                               'org.gokb.cred.Source',
                               'wekb.UpdatePackageInfo',
                               'wekb.UpdateTippInfo']

    List allowedToCreate = ['org.gokb.cred.Org',
                            'org.gokb.cred.Package',
                            'org.gokb.cred.Platform',
                            'org.gokb.cred.TitleInstancePackagePlatform',
                            'org.gokb.cred.Source',]

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
            if (curatedObj && curatedObj.curatoryGroups && !(curatedObj instanceof User)) {

                if(user && user.curatoryGroups?.id.intersect(curatedObj.curatoryGroups?.id).size() > 0)
                {
                    editable = true //SecurityApi.isTypeEditable(o.getClass(), true) ?: (grailsParameterMap.curationOverride == 'true' && user.isAdmin())
                }else {
                    editable = (curationOverride && user.isAdmin()) //SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER') ?: (grailsParameterMap.curationOverride == 'true' && user.isAdmin())
                }
            }else {
                if(o instanceof CuratoryGroup && user && o.id in user.curatoryGroups?.id){
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

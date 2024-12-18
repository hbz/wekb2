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
                               'wekb.Identifier',
                               'wekb.KbartSource',
                               'wekb.Org',
                               'wekb.Package',
                               'wekb.Platform',
                               'wekb.RefdataCategory',
                               'wekb.TitleInstancePackagePlatform',
                               'wekb.UpdatePackageInfo',
                               'wekb.UpdateTippInfo',
                               'wekb.Vendor']

    List allowedToCreate = ['wekb.KbartSource',
                            'wekb.Org',
                            'wekb.Package',
                            'wekb.Platform',
                            'wekb.TitleInstancePackagePlatform',]

    List allowedPublicShow = ['CuratoryGroup',
                              'Identifier',
                              'KbartSource',
                              'Org',
                              'Package',
                              'Platform',
                              'RefdataCategory',
                              'TitleInstancePackagePlatform',
                              'UpdatePackageInfo',
                              'UpdateTippInfo',
                              'Vendor']

    List allowedComponentSearch = ["g:curatoryGroups",
                                   "g:identifiers",
                                   "g:orgs",
                                   "g:packages",
                                   "g:publicPackages",
                                   "g:platforms",
                                   "g:refdataCategoriesPublic",
                                   "g:sources",
                                   "g:tipps",
                                   "g:tippsOfPkg",
                                   "g:updatePackageInfos",
                                   "g:updateTippInfos",
                                   "g:vendors"]

    List allowedInlineSearch = ["g:curatoryGroups",
                                "g:identifiers",
                                "g:orgs",
                                "g:packages",
                                "g:platforms",
                                "g:sources",
                                "g:tipps",
                                "g:tippsOfPkg",
                                "g:updatePackageInfos",
                                "g:updateTippInfos",
                                "g:vendors"]


    boolean checkEditableObject(Object o, GrailsParameterMap grailsParameterMap) {
        checkEditableObject(o, (grailsParameterMap && grailsParameterMap.curationOverride == 'true'))
    }

    boolean checkEditableObject(Object o, boolean curationOverride = false) {
        boolean editable = false

        if (SpringSecurityUtils.ifAnyGranted("ROLE_EDITOR, ROLE_VENDOR_EDITOR, ROLE_ADMIN, ROLE_SUPERUSER")) {
            def curatedObj = null
            if(o instanceof Identifier){
                curatedObj = o.reference.hasProperty('curatoryGroups') ? o.reference : ( o.reference.hasProperty('pkg') ? o.reference.pkg : null )
            }else if(o instanceof Contact){
                curatedObj = o.org ?: o.vendor
            }else if(o instanceof VendorElectronicDeliveryDelay){
                curatedObj = o.vendor
            }else if(o instanceof VendorLibrarySystem){
                curatedObj = o.vendor
            }
            else if(o instanceof VendorInvoiceDispatch){
                curatedObj = o.vendor
            }
            else if(o instanceof VendorElectronicBilling){
                curatedObj = o.vendor
            } else if (o instanceof ProviderInvoicingVendor) {
                curatedObj = o.provider
            } else if (o instanceof ProviderInvoiceDispatch) {
                curatedObj = o.provider
            } else if (o instanceof ProviderElectronicBilling) {
                curatedObj = o.provider
            }
            else {
                curatedObj = o.hasProperty('curatoryGroups') ? o : ( o.hasProperty('pkg') ? o.pkg : null )
            }

            User user = springSecurityService.currentUser
            if (curatedObj && curatedObj.curatoryGroups && user.curatoryGroupUsers && !(curatedObj instanceof User)) {

                if(user && user.curatoryGroupUsers.curatoryGroup.id.intersect(curatedObj.curatoryGroups.curatoryGroup.id).size() > 0)
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

        if(baseclassName in allowedBaseClasses && SpringSecurityUtils.ifAnyGranted('ROLE_EDITOR, ROLE_VENDOR_EDITOR')){
            return true
        }else {
            return SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')
        }


    }

    @Deprecated
    boolean checkDeletable(String baseclassName) {

        if(baseclassName in allowedToCreate){
            return true
        }else {
            return SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')
        }


    }

    void updateLastLogin() {
        User user = springSecurityService.currentUser
        user.lastLogin = new Date()
        user.invalidLoginAttempts = 0
        user.save()

    }

}

package wekb

import grails.converters.JSON
import grails.core.GrailsClass
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.security.access.annotation.Secured
import wekb.auth.User
import wekb.helper.RCConstants
import wekb.helper.RDStore


class AjaxJsonController {

    SpringSecurityService springSecurityService

    /**
     *  lookup : Calls the refdataFind function of a specific class and returns a simple result list.
     * @param baseClass : The class name to
     * @param addEmpty : Add an empty row at the start of the list
     * @param filter1 : A status value string which should be filtered out after the query has been executed
     */
    def lookup() {
        log.debug("AjaxJsonController::lookup ${params}");
        def result = [:]
        def domain_class = grailsApplication.getArtefact('Domain',params.baseClass)
        if (domain_class) {
            result.values = domain_class.getClazz().refdataFind(params);
        }
        else {
            log.debug("Unable to locate domain class ${params.baseClass} or not readable");
            result.values = []
            result.error = "Unable to locate domain class ${params.baseClass}, or this user is not permitted to view it."
        }

        if(params.preparForEditable)
        result = result.values.collect { Map value -> [value: value.id, text: "${value.text} ${value.status ? "("+value.status+")": ""}"]}

        render result as JSON
    }

    def lookupMyComponents() {
        log.debug("AjaxJsonController::lookupMyComponents ${params}")

        List result = []
        User user = springSecurityService.currentUser

        List groups = user.curatoryGroupUsers.curatoryGroup

        RefdataValue status_filter = null

        if (params.filter1) {
            status_filter = RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, params.filter1)
        }

        if(params.baseClass == Org.class.name){
            List objectList = null
            objectList = CuratoryGroupOrg.executeQuery("select curorg.org from CuratoryGroupOrg as curorg where curorg.curatoryGroup in (:cur) and curorg.org.status != :status and curorg.org.status != :removed", [cur: groups, status: RDStore.KBC_STATUS_DELETED, removed: RDStore.KBC_STATUS_REMOVED])

            if (objectList) {
                objectList.each { t ->
                    if (!status_filter || t.status == status_filter) {
                        result.add([value: "${t.class.name}:${t.id}", text: "${t.name}", status: "${t.status?.value}"])
                    }
                }
            }
        }

        if(params.baseClass == Platform.class.name){
            List objectList = null
            objectList = CuratoryGroupPlatform.executeQuery("select curorg.platform from CuratoryGroupPlatform as curorg where curorg.curatoryGroup in (:cur) and curorg.platform.status != :status and curorg.platform.status != :removed", [cur: groups, status: RDStore.KBC_STATUS_DELETED, removed: RDStore.KBC_STATUS_REMOVED])

            if (objectList) {
                objectList.each { t ->
                    if (!status_filter || t.status == status_filter) {
                        result.add([value: "${t.class.name}:${t.id}", text: "${t.name}", status: "${t.status?.value}"])
                    }
                }
            }
        }

        if(params.baseClass == KbartSource.class.name){
            List objectList = null
            objectList = CuratoryGroupKbartSource.executeQuery("select curorg.kbartSource from CuratoryGroupKbartSource as curorg where curorg.curatoryGroup in (:cur) and curorg.kbartSource.status != :status and curorg.kbartSource.status != :removed", [cur: groups, status: RDStore.KBC_STATUS_DELETED, removed: RDStore.KBC_STATUS_REMOVED])

            if (objectList) {
                objectList.each { t ->
                    if (!status_filter || t.status == status_filter) {
                        result.add([value: "${t.class.name}:${t.id}", text: "${t.name}", status: "${t.status?.value}"])
                    }
                }
            }
        }

        render result as JSON
    }

    /**
     *  getRefdata : Used to retrieve a list of all RefdataValues for a specific category.
     * @param id : The label of the RefdataCategory
     */
    @Secured(['ROLE_USER', 'ROLE_EDITOR', 'ROLE_VENDOR_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def getRefdata() {
        log.debug("AjaxController::getRefdata ${params}")
        def result = []

        if (params.id == 'boolean') {
            result.add([text: 'Yes', value: 1])
            result.add([text: 'No', value: 0])
        } else if (params.id) {
            List rq = RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc = :desc order by rdv.order asc, rdv.value asc, rdv.description asc', [desc: params.id], [max: 400, offset: 0]);

            if (!params.required) {
                result.add([id: '', text: '', value: '']);
            }

            rq.each { it ->
                RefdataValue o = ClassUtils.deproxy(it)
                result.add([id: "${o.class.name}:${o.id}", text: o.getI10n('value'), value: "${o.class.name}:${o.id}"]);
            }
        }

        render result as JSON
    }

    @Secured(['ROLE_USER', 'ROLE_EDITOR', 'ROLE_VENDOR_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def getProfilPageSizeList() {
        List result = [
                [value: 10, text: '10'],
                [value: 25, text: '25'],
                [value: 50, text: '50'],
                [value: 100, text: '100'],
        ]
        render result as JSON
    }

}

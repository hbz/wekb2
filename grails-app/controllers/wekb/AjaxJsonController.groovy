package wekb

import grails.converters.JSON
import grails.core.GrailsClass
import org.springframework.security.access.annotation.Secured
import wekb.helper.RCConstants

class AjaxJsonController {

    /**
     *  lookup : Calls the refdataFind function of a specific class and returns a simple result list.
     * @param baseClass : The class name to
     * @param addEmpty : Add an empty row at the start of the list
     * @param filter1 : A status value string which should be filtered out after the query has been executed
     */
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
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

    /**
     *  getRefdata : Used to retrieve a list of all RefdataValues for a specific category.
     * @param id : The label of the RefdataCategory
     */
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def getRefdata() {
        log.debug("AjaxController::getRefdata ${params}")
        def result = []

        if (params.id == 'boolean') {
            result.add([text: 'Yes', value: 1])
            result.add([text: 'No', value: 0])
        } else if (params.id) {
            List rq = RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc = :desc order by rdv.value asc, rdv.description asc', [desc: params.id], [max: 400, offset: 0]);

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


}

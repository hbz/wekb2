package wekb

import grails.converters.JSON

class AjaxJsonController {

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
}

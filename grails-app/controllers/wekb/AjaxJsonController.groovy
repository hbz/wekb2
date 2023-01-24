package wekb

import grails.converters.JSON
import grails.core.GrailsClass
import org.springframework.security.access.annotation.Secured
import wekb.helper.RCConstants

class AjaxJsonController {

    def refdata_config = [
            'KBComponent.Status' : [
                    domain:'RefdataValue',
                    countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    required:true,
                    qryParams:[],
                    rdvCat: RCConstants.KBCOMPONENT_STATUS,
                    cols:['value'],
                    format:'simple'
            ],
            'KBComponentVariantName.VariantType' : [
                    domain:'RefdataValue',
                    countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    qryParams:[],
                    rdvCat: RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE,
                    cols:['value'],
                    format:'simple'
            ],
            'Locale' : [
                    domain:'RefdataValue',
                    countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    qryParams:[],
                    rdvCat: RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL,
                    cols:['value'],
                    format:'simple'
            ],
            'TitleInstancePackagePlatform.CoverageDepth' : [
                    domain:'RefdataValue',
                    countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    required:true,
                    qryParams:[],
                    rdvCat: RCConstants.TIPP_COVERAGE_DEPTH,
                    cols:['value'],
                    format:'simple'
            ],
            'TIPPCoverageStatement.CoverageDepth' : [
                    domain:'RefdataValue',
                    countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    required:true,
                    qryParams:[],
                    rdvCat: RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH,
                    cols:['value'],
                    format:'simple'
            ],
    ]

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

    /**
     *  getRefdata : Used to retrieve a list of all RefdataValues for a specific category.
     * @param id : The label of the RefdataCategory
     */
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def getRefdata() {
        log.debug("AjaxController::getRefdata ${params}")

        def result = []

        def config = refdata_config[params.id]

        if (!config) {
            log.debug("Use generic config.")

            config = [
                    domain:'RefdataValue',
                    countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
                    rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=? order by rdv.value asc, rdv.description asc",
                    rdvCat: "${params.id}",
                    qryParams:[],
                    cols:['value'],
                    format:'simple'
            ]
        }

        if ( params.id == 'boolean' ) {
            result.add([text:'Yes', value: 1])
            result.add([text:'No', value: 0])
        } else {
            def query_params = [config.rdvCat]

            config.qryParams.each { qp ->
                if ( qp.clos ) {
                    query_params.add(qp.clos(params[qp.param]?:''));
                }
                else {
                    query_params.add(params[qp.param] ?: qp.cat);
                }
            }

            log.debug("Params: ${query_params}");
            log.debug("Count qry: ${config.countQry}");
            log.debug("Row qry: ${config.rowQry}");
            log.debug("DOMAIN: ${config.domain}");

            GrailsClass dc = grailsApplication.getArtefact("Domain", 'wekb.'+ config.domain)

            if (dc) {
                def cq = dc.getClazz().executeQuery(config.countQry,query_params);
                def rq = dc.getClazz().executeQuery(config.rowQry,
                        query_params,
                        [max:params.iDisplayLength?:400,offset:params.iDisplayStart?:0]);

                if (!config.required) {
                    result.add([id:'', text:'', value:'']);
                }

                rq.each { it ->
                    def o = ClassUtils.deproxy(it)
                    result.add([id:"${o.class.name}:${o.id}", text: o[config.cols[0]], value:"${o.class.name}:${o.id}"]);
                }
            }
        }

        render result as JSON
    }


}

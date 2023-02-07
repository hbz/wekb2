package wekb

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import wekb.auth.User
import wekb.system.SavedSearch

@Transactional
class SearchService {
    def grailsApplication
    GenericOIDService genericOIDService
    GlobalSearchTemplatesService globalSearchTemplatesService
    DisplayTemplateService displayTemplateService
    ClassExaminationService classExaminationService
    AccessService accessService

    def doQuery (qbetemplate, params, result) {
        def target_class = grailsApplication.getArtefact("Domain",qbetemplate.baseclass);
        HQLBuilder.build(grailsApplication, qbetemplate, params, result, target_class, genericOIDService)
    }

    Map search(User user = null, Map result, GrailsParameterMap params, String responseFormat = null){

        if ( params.init ) {
            result.init = true
        }

        def cleaned_params = params.findAll { it.value && it.value != "" }

        log.debug("Cleaned: ${cleaned_params}");

        if ( params.refOID && !params.refOID.endsWith('null')) {
            result.refOID = params.refOID
            result.refObject = genericOIDService.resolveOID(result.refOID)
        }

        result.max = params.max ? Integer.parseInt(params.max) : user ? user.defaultPageSizeAsInteger : 10
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

        if( params.inline && !params.max) {
            result.max = 10
        }

        if ( params.jumpToPage ) {
            result.offset = ( ( Integer.parseInt(params.jumpToPage) - 1 ) * result.max )
        }
        params.remove('jumpToPage')
        params.offset = result.offset
        result.hide = params.list("hide") ?: []

        if ( params.searchAction == 'Save' ) {
            log.debug("Saving query... ${params.searchName}");
            def defn = [:] << params
            defn.remove('searchAction')

            try {
                log.debug("Saving..");
                def saved_search = new SavedSearch(name:params.searchName,owner:user,searchDescriptor:(defn as JSON).toString())
                saved_search.save(flush:true, failOnError:true)
                log.debug("Saved.. ${saved_search.id}");
            }
            catch ( Exception e ) {
                log.error("Problem",e);
            }
        }

        if ( params.det )
            result.det = Integer.parseInt(params.det)

        if ( params.qbe) {
            if ( params.qbe.startsWith('g:') ) {
                // Global template, look in config
                def global_qbe_template_shortcode = params.qbe.substring(2,params.qbe.length());
                // log.debug("Looking up global template ${global_qbe_template_shortcode}");
                result.qbetemplate = globalSearchTemplatesService.getGlobalSearchTemplate(global_qbe_template_shortcode)
                // log.debug("Using template: ${result.qbetemplate}");
            }

            // Looked up a template from somewhere, see if we can execute a search
            if ( result.qbetemplate) {

                Class target_class = Class.forName(result.qbetemplate.baseclass);
                def read_perm = accessService.checkReadable(result.qbetemplate.baseclass)

                if (read_perm && !params.init) {

                    log.debug("Execute query");
                    doQuery(result.qbetemplate, cleaned_params, result)
                    log.debug("Query complete");
                    result.lasthit = result.offset + result.max > result.reccount ? result.reccount : ( result.offset + result.max )

                    // Add the page information.
                    result.page_current = (result.offset / result.max) + 1
                    result.page_total = (result.reccount / result.max).toInteger() + (result.reccount % result.max > 0 ? 1 : 0)

                }else if (!read_perm){
                    return [status: 403]
                }
            }
            else {
                log.error("no template ${result?.qbetemplate}");
            }


            if ( result.det && result.recset ) {

                log.debug("Got details page");

                int recno = result.det - result.offset - 1

                if ( result.recset.size() >= recno) {

                    if ( recno >= result.recset.size() ) {
                        recno = result.recset.size() - 1;
                        result.det = result.reccount;
                    }
                    else if ( recno < 0 ) {
                        recno = 0;
                        result.det = 0;
                    }

                    // log.debug("Trying to display record ${recno}");

                    result.displayobj = result.recset.get(recno)

                    def display_start_time = System.currentTimeMillis();
                    if ( result.displayobj != null ) {

                        result.displayobjclassname = result.displayobj.class.name
                        result.displaytemplate = displayTemplateService.getTemplateInfo(result.displayobjclassname)
                        result.__oid = "${result.displayobjclassname}:${result.displayobj.id}"

                        // Add any refdata property names for this class to the result.
                        result.refdata_properties = classExaminationService.getRefdataPropertyNames(result.displayobjclassname)
                        result.displayobjclassname_short = result.displayobj.class.simpleName

                        //result.acl = gokbAclService.readAclSilently(result.displayobj)

                        if ( result.displaytemplate == null ) {
                            log.error("Unable to locate display template for class ${result.displayobjclassname} (oid ${params.displayoid})");
                        }
                        else {
                            // log.debug("Got display template ${result.displaytemplate} for rec ${result.det} - class is ${result.displayobjclassname}");
                        }
                    }
                    else {
                        log.error("Result row for display was NULL");
                    }
                    log.debug("Display completed after ${System.currentTimeMillis() - display_start_time}");
                }
                else {
                    log.error("Record display request out of range");
                }
            }
        }

        def apiresponse = ['count': result.reccount, 'max': result.max, 'offset': result.offset, records: []]

        result.new_recset = []
        log.debug("Create new recset..")
        result.recset.each { r ->
            if(params.sort == 'currentTippCount'){
                r = r[0]
            }

            def response_record = [:]
            response_record.oid = "${r.class.name}:${r.id}"
            response_record.obj = r
            response_record.cols = []
            def api_record = ['oid': response_record.oid]

            result.qbetemplate.qbeConfig.qbeResults.each { rh ->
                def ppath = rh.property.split(/\./)
                def cobj = r
                def final_oid = cobj.hasProperty('uuid') ? cobj.uuid : cobj.class.name + ':' + cobj.id

                if (!params.hide || (params.hide instanceof String ? (params.hide != rh.qpEquiv) : !params.hide.contains(rh.qpEquiv))) {

                    ppath.eachWithIndex { prop, idx ->
                        def sp = prop.minus('?')

                        if(result.qbetemplate.baseclass != 'wekb.RefdataValue' && cobj?.class?.name == 'wekb.RefdataValue' ) {
                            cobj = cobj.value
                        }
                        else if(sp == 'curatoryGroupsCuratoryGroup') {
                            if(cobj instanceof Package){
                                cobj = CuratoryGroupPackage.findAllByPkg(cobj)?.curatoryGroup
                            }else if(cobj instanceof Platform){
                                cobj = CuratoryGroupPlatform.findAllByPlatform(cobj)?.curatoryGroup
                            }else if(cobj instanceof Org){
                                cobj = CuratoryGroupOrg.findAllByOrg(cobj)?.curatoryGroup
                            }else if(cobj instanceof KbartSource){
                                cobj = CuratoryGroupKbartSource.findAllByKbartSource(cobj)?.curatoryGroup
                            }
                        }
                        else {
                            if ( cobj && (cobj.hasProperty(sp) || (cobj.respondsTo(sp)?.size() > 0))) {

                                def oobj = cobj

                                cobj = cobj[sp]

                                if ( sp == 'name' && !cobj && oobj.respondsTo('getDisplayName')) {
                                    cobj = oobj.displayName
                                }

                                if (ppath.size() > 1 && idx == ppath.size()-2) {
                                    if (cobj && sp != 'class') {
                                        final_oid = cobj.hasProperty('uuid') ? cobj.uuid : cobj.class.name + ':' + cobj.id
                                    }
                                    else {
                                        final_oid = null
                                    }
                                }
                            }
                            else {
                                cobj = null
                            }
                        }
                    }
                    if( responseFormat == 'json' || responseFormat == 'xml' ) {
                        api_record['oid'] = response_record.oid
                        api_record["${rh.heading}"] = cobj ?: null
                    }
                    else {

                        String jumpToLink = null
                        if(rh.jumpToLink) {
                            jumpToLink = rh.jumpToLink.replace("objectID", "${r.id}")
                        }

                        response_record.cols.add([
                                link: (rh.link ? (final_oid ?: response_record.oid ) : null),
                                value: (cobj != null ? (cobj) : '-Empty-'),
                                outGoingLink: rh.outGoingLink ?: null,
                                jumpToLink: jumpToLink ?: null])
                    }
                }
            }
            if( responseFormat == 'json' || responseFormat == 'xml' ) {
                apiresponse.records.add(api_record)
            }
            else {
                result.new_recset.add(response_record)
            }
        }
        log.debug("Finished new recset!")

        result.withoutJump = cleaned_params
        result.remove('jumpToPage')
        result.withoutJump.remove('jumpToPage')

        [result: result, apiresponse: apiresponse]
    }
}

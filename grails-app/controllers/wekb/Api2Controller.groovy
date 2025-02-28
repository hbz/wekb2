package wekb

import grails.plugin.springsecurity.SpringSecurityService
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.security.web.savedrequest.DefaultSavedRequest
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import wekb.auth.User
import grails.converters.JSON

import java.security.SecureRandom


class Api2Controller {
    SecureRandom rand = new SecureRandom()

    Api2Service api2Service
    SpringSecurityService springSecurityService

    /**
     * Check if the api is up. Just return true.
     */
    def isUp() {
        apiReturn(["isUp": true])
    }

    // Internal API return object that ensures consistent formatting of API return objects
    private def apiReturn = { result, String message = "", String status = (result instanceof Throwable) ? "error" : "success" ->

        // If the status is error then we should log an entry.
        if (status == 'error') {

            // Generate 6bytes of random data to be base64 encoded which can be returned to the user to help with tracking issues in the logs.
            byte[] randomBytes = new byte[6]
            rand.nextBytes(randomBytes)
            def ticket = Base64.encodeBase64String(randomBytes);

            // Let's see if we have a throwable.
            if (result && result instanceof Throwable) {

                // Log the error with the stack...
                log.error("[[${ticket}]] - ${message == "" ? result.getLocalizedMessage() : message}", result)
            } else {
                log.error("[[${ticket}]] - ${message == "" ? 'An error occured, but no message or exception was supplied. Check preceding log entries.' : message}")
            }

            // Ensure we have something to send back to the user.
            if (message == "") {
                message = "An unknow error occurred."
            } else {

                // We should now send the message along with the ticket.
                message = "${message}".replaceFirst("\\.\\s*\$", ". The error has been logged with the reference '${ticket}'")
            }
        }

        def data = [
                code   : (status),
                result : (result),
                message: (message),
        ]

        def json = data as JSON
        //log.debug("json:"+json.toString())
        render json
        //    render (text: "${params.callback}(${json})", contentType: "application/javascript", encoding: "UTF-8")
    }

    def index() {
    }

    def searchApi() {
        log.info("Api2Controller:searchApi ${params}")

        Map<String, Object> result = checkPermisson(params)

        if(result.code == 'success') {

            if (!params.componentType && !params.uuid) {
                result.code = 'error'
                result.message = "No componentType set!"
            }else {
                try {
                    if(params.changedSince || params.changedBefore) {
                        log.info("Api2Controller: INCREMENTAL HARVESTING ---------------------------------------------------------------------------------------------------------------------------")
                    }
                    result = api2Service.search(result, params)
                } catch (Throwable e) {
                    result.code = 'error'
                    result.message = e.message
                    log.error("Problem by search api: ", e)
                }
            }
        }


        render result as JSON

    }

    def namespaces() {
        log.info("Api2Controller:namespaces ${params}")
        Map<String, Object> result = checkPermisson(params)

        if(result.code == 'success') {
            def results = []
            def all_ns = null

            if (params.category && params.category?.trim().size() > 0) {
                all_ns = IdentifierNamespace.findAllByFamily(params.category)
            } else {
                all_ns = IdentifierNamespace.findAll()
            }

            all_ns.each { ns ->
                results.add([value: ns.value, namespaceName: ns.name, category: ns.family ?: "", id: ns.id])
            }

            //Add information to result
            result.result_count_total = all_ns.size()
            result.result_count = all_ns.size()

            result.result = results
        }

        render result as JSON
    }

    def refdataCategories() {
        log.info("Api2Controller:refdataCategories ${params}")
        Map<String, Object> result = checkPermisson(params)

        if(result.code == 'success') {
            def results = []
            RefdataCategory.list().each { RefdataCategory refdataCategory ->
                Map refCatMap = ['id'     : refdataCategory.id,
                                 'desc'   : refdataCategory.desc,
                                 'desc_en': refdataCategory.desc_en,
                                 'desc_de': refdataCategory.desc_de]

                refCatMap.refDataValues = []

                refdataCategory.values.each { RefdataValue refdataValue ->
                    Map refValueMap = ['value'   : refdataValue.value,
                                       'value_de': refdataValue.value_de,
                                       'value_en': refdataValue.value_en]

                    refCatMap.refDataValues << refValueMap
                }

                results << refCatMap

            }
            //Add information to result
            result.result_count_total = results.size()
            result.result_count = results.size()

            result.result = results
        }

        render result as JSON
    }

    def groups() {
        log.info("Api2Controller:groups ${params}")
        Map<String, Object> result = checkPermisson(params)

        if(result.code == 'success') {
            def results = []
            switch(params.componentType) {
                case 'Package':
                    List rows = CuratoryGroup.executeQuery('select new map(cg.id as id, cg.name as name, cg.status.value as status, cg.uuid as uuid, pkg.uuid as packageUuid) from Package pkg join pkg.curatoryGroups pcg join pcg.curatoryGroup cg order by cg.name')
                    results.addAll(rows)
                    break
                case 'Platform':
                    List rows = CuratoryGroup.executeQuery('select new map(cg.id as id, cg.name as name, cg.status.value as status, cg.uuid as uuid, plat.uuid as platformUuid) from Platform plat join plat.curatoryGroups pcg join pcg.curatoryGroup cg order by cg.name')
                    results.addAll(rows)
                    break
                default:
                    CuratoryGroup.list(sort: 'name').each {
                        results << [
                                'id'    : it.id,
                                'name'  : it.name,
                                'status': it.status?.value ?: null,
                                'uuid'  : it.uuid
                        ]
                    }
                    break
            }
            result.result = results
            //Add information to result
            result.result_count_total = results.size()
            result.result_count = results.size()

            result.result = results
        }

        render result as JSON
    }

    def sushiSources() {
        log.info("Api2Controller:counterSources ${params}")
        Map<String, Object> result = checkPermisson(params, 'ROLE_COUNTER')

        if(result.code == 'success') {
           result = api2Service.counterSources(params, result)
        }
        render result as JSON
    }

    private Map checkPermisson(GrailsParameterMap params, String checkRole = null){
        Map result = [code: 'success', message: '']
        User user

        log.info 'API request from ' + request.getRemoteAddr() + ' for ' + request.requestURI + ' ---> ' + request.getHeaderNames().findAll{
            it in ['host', 'referer', 'cookie', 'user-agent']
        }.collect{it + ': ' + request.getHeaders( it )}

        if(!springSecurityService.loggedIn){

            if (params.username && params.password) {
                springSecurityService.reauthenticate(params.username, params.password)

                if(!springSecurityService.loggedIn){
                    result.code = 'error'
                    result.message = 'Your login is not correct!'
                    return result
                }
            }else {
                result.code = 'error'
                result.message = 'Please set your authentication to login!'
                log.warn('checkPermisson: Please set your authentication to login!')
                return result
            }
        }

        user = springSecurityService.getCurrentUser()

        if(checkRole){
            if (!user.hasRole('ROLE_COUNTER')) {
                result.code = 'error'
                result.message = 'This user does not have permission to access the api!'
                log.warn('checkPermisson: This user does not have permission to access the api!')
                return result
            }
        }else {
            if (!user.apiUserStatus) {
                result.code = 'error'
                result.message = 'This user does not have permission to access the api!'
                log.warn('checkPermisson: This user does not have permission to access the api!')
                return result
            }
        }

        return result
    }

}

package wekb

import grails.plugin.springsecurity.SpringSecurityService
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.security.access.annotation.Secured
import wekb.auth.User
import grails.converters.JSON
import wekb.helper.RDStore

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
        log.debug("Api2Controller:searchApi ${params}")

        Map<String, Object> result = checkPermisson(params)

        if(result.code == 'success') {

            if (!params.componentType) {
                return apiReturn([], "No componentType set!")
            }

            try {
                result = api2Service.search(result, params)
            } catch (Throwable e) {
                result.code = 'error'
                result.message = e.message
                log.error("Problem by search api: ", e)
            }
        }


        render result as JSON

    }

    def namespaces() {
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
            result.results = results
        }

        apiReturn(result)
    }

    @Secured(['ROLE_API', 'IS_AUTHENTICATED_FULLY'])
    def refdataCategories() {

        def result = []

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

            result << refCatMap
        }

        apiReturn(result)
    }

    @Secured(['ROLE_API', 'IS_AUTHENTICATED_FULLY'])
    def groups() {

        def result = []

        CuratoryGroup.list().each {
            result << [
                    'id'    : it.id,
                    'name'  : it.name,
                    'status': it.status?.value ?: null,
                    'uuid'  : it.uuid
            ]
        }

        apiReturn(result)
    }

    def sushiSources() {
        Map<String, Object> result = checkPermisson(params, 'ROLE_SUSHI')
        RefdataValue yes = RDStore.YN_YES

        if(result.code == 'success') {
            Set counter4Platforms = []
            Set counter5Platforms = []
            //Set<Platform> counter4Platforms = Platform.findAllByCounterR4SushiApiSupportedAndCounterR5SushiApiSupportedNotEqual(yes, yes).toSet(), counter5Platforms = Platform.findAllByCounterR5SushiApiSupported(yes).toSet()

            if(params.uuid){
                counter4Platforms = Platform.executeQuery("select plat.uuid, plat.counterR4SushiServerUrl, plat.statisticsUpdate.value, sushiApiAuthenticationMethod.value, centralApiKey from Platform plat where plat.counterR4SushiApiSupported = :r4support and plat.counterR5SushiApiSupported != :r5support and plat.counterR4SushiServerUrl is not null and plat.uuid = :uuid", [r4support: yes, r5support: yes, uuid: params.uuid]).toSet()
                counter5Platforms = Platform.executeQuery("select plat.uuid, plat.counterR5SushiServerUrl, plat.statisticsUpdate.value, sushiApiAuthenticationMethod.value, centralApiKey from Platform plat where plat.counterR5SushiApiSupported = :r5support and plat.counterR5SushiServerUrl is not null and plat.uuid = :uuid", [r5support: yes, uuid: params.uuid]).toSet()
            }else {
                counter4Platforms = Platform.executeQuery("select plat.uuid, plat.counterR4SushiServerUrl, plat.statisticsUpdate.value, sushiApiAuthenticationMethod.value, centralApiKey from Platform plat where plat.counterR4SushiApiSupported = :r4support and plat.counterR5SushiApiSupported != :r5support and plat.counterR4SushiServerUrl is not null", [r4support: yes, r5support: yes]).toSet()
                counter5Platforms = Platform.executeQuery("select plat.uuid, plat.counterR5SushiServerUrl, plat.statisticsUpdate.value, sushiApiAuthenticationMethod.value, centralApiKey from Platform plat where plat.counterR5SushiApiSupported = :r5support and plat.counterR5SushiServerUrl is not null", [r5support: yes]).toSet()
            }

            result.counter4ApiSources = counter4Platforms.size() > 0 ? counter4Platforms : []
            result.counter5ApiSources = counter5Platforms.size() > 0 ? counter5Platforms : []
        }
        render result as JSON
    }

    private Map checkPermisson(GrailsParameterMap params, String checkRole = null){
        Map result = [code: 'success']
        User user

        if(!springSecurityService.loggedIn){

            if (params.username && params.password) {
                user = springSecurityService.reauthenticate(params.username, params.password)

                if(!user){
                    result.code = 'error'
                    result.message = 'Your login are not correct!'
                    return result
                }
            }else {
                result.code = 'error'
                result.message = 'Please set your authentication to login!'
                return result
            }
        }

        user = springSecurityService.getCurrentUser()

        if(checkRole){
            if (!user.hasRole('ROLE_SUSHI')) {
                result.code = 'error'
                result.message = 'This user does not have permission to access the api!'
                return result
            }
        }else {
            if (!user.apiUserStatus) {
                result.code = 'error'
                result.message = 'This user does not have permission to access the api!'
                return result
            }
        }

        return result
    }

}

package wekb

import org.springframework.security.access.annotation.Secured
import wekb.helper.RCConstants
import grails.converters.JSON
import wekb.helper.RDStore

import java.security.SecureRandom

@Secured(['ROLE_API', 'IS_AUTHENTICATED_FULLY'])
class Api2Controller {
    SecureRandom rand = new SecureRandom()

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
        //  log.debug (json)
        render json
        //    render (text: "${params.callback}(${json})", contentType: "application/javascript", encoding: "UTF-8")
    }

    def index() {
    }


    def namespaces() {

        def result = []
        def all_ns = null

        if (params.category && params.category?.trim().size() > 0) {
            all_ns = IdentifierNamespace.findAllByFamily(params.category)
        } else {
            all_ns = IdentifierNamespace.findAll()
        }

        all_ns.each { ns ->
            result.add([value: ns.value, namespaceName: ns.name, category: ns.family ?: "", id: ns.id])
        }

        apiReturn(result)
    }

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
        Map<String, Object> result = [:]
        RefdataValue yes = RDStore.YN_YES
        //Set<Platform> counter4Platforms = Platform.findAllByCounterR4SushiApiSupportedAndCounterR5SushiApiSupportedNotEqual(yes, yes).toSet(), counter5Platforms = Platform.findAllByCounterR5SushiApiSupported(yes).toSet()
        Set counter4Platforms = Platform.executeQuery("select plat.uuid, plat.counterR4SushiServerUrl, plat.statisticsUpdate.value from Platform plat where plat.counterR4SushiApiSupported = :r4support and plat.counterR5SushiApiSupported != :r5support and plat.counterR4SushiServerUrl is not null", [r4support: yes, r5support: yes]).toSet()
        Set counter5Platforms = Platform.executeQuery("select plat.uuid, plat.counterR5SushiServerUrl, plat.statisticsUpdate.value from Platform plat where plat.counterR5SushiApiSupported = :r5support and plat.counterR5SushiServerUrl is not null", [r5support: yes]).toSet()
        result.counter4ApiSources = counter4Platforms.size() > 0 ? counter4Platforms : []
        result.counter5ApiSources = counter5Platforms.size() > 0 ? counter5Platforms : []
        render result as JSON
    }


}

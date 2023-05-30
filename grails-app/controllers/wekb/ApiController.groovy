package wekb

import grails.plugin.springsecurity.SpringSecurityService
import wekb.CuratoryGroup
import wekb.ESSearchService
import wekb.IdentifierNamespace
import wekb.Platform
import wekb.RefdataCategory
import wekb.RefdataValue
import wekb.auth.User
import wekb.helper.RCConstants
import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.springframework.security.access.annotation.Secured
import wekb.helper.RDStore

import java.security.SecureRandom

@Slf4j
class ApiController {

  SecureRandom rand = new SecureRandom()
  ESSearchService ESSearchService

  static def reversemap = ['subject':'subjectKw','componentType':'componentType','identifier':'identifiers.value']
  static def non_analyzed_fields = ['componentType','identifiers.value']

  private static final Closure TRANSFORMER_USER = { User u ->
    [
      "id"      : "${u.id}",
      "email"     : "${u.email}",
      "username"    : "${u.username}",
      "displayName"   : "${u.displayName ?: u.username}"
    ]
  }

  SpringSecurityService springSecurityService

  /**
   * Check if the api is up. Just return true.
   */
  def isUp() {
    apiReturn(["isUp" : true])
  }

  // Internal API return object that ensures consistent formatting of API return objects
  private def apiReturn = {result, String message = "", String status = (result instanceof Throwable) ? "error" : "success" ->

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
      code    : (status),
      result    : (result),
      message    : (message),
    ]

    def json = data as JSON
    //  log.debug (json)
    render json
    //    render (text: "${params.callback}(${json})", contentType: "application/javascript", encoding: "UTF-8")
  }

  def index() {
  }

  @Secured(['IS_AUTHENTICATED_FULLY'])
  def checkLogin() {
    apiReturn(["login": true])
  }

  def userData() {
    if (!springSecurityService.currentUser) {
      return
    }
    apiReturn ( TRANSFORMER_USER( springSecurityService.currentUser ) )
  }

  def namespaces() {

    def result = []
    def all_ns = null

    if (params.category && params.category?.trim().size() > 0) {
      all_ns = IdentifierNamespace.findAllByFamily(params.category)
    }
    else {
      all_ns = IdentifierNamespace.findAll()
    }

    all_ns.each { ns ->
      result.add([value: ns.value, namespaceName:ns.name, category: ns.family ?: "", id:ns.id])
    }

    apiReturn(result)
  }

  def groups() {

    def result = []

    CuratoryGroup.list().each {
      result << [
        'id':  it.id,
        'name': it.name,
        'status': it.status?.value ?: null,
        'uuid': it.uuid
      ]
    }

    apiReturn(result)
  }

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

  def suggest() {
    log.info("API Call: suggest - " + params)
    def result = [:]
    def searchParams = params

    try {

      if ( params.q?.length() > 0 ) {
        searchParams.suggest = params.q
        searchParams.remove("q")

        if (!searchParams.mapRecords) {
          searchParams.skipDomainMapping = true
        }
        else {
          searchParams.remove("mapRecords")
        }

        result = ESSearchService.find(searchParams)
      }
      else{
        result.errors = ['fatal': "No query parameter 'q=' provided"]
        result.result = "ERROR"
      }

    }finally {
      if (result.errors) {
        response.setStatus(400)
      }
    }

    render result as JSON
  }

  /**
   * find : Query the Elasticsearch index via ESSearchService
  **/
  def find() {
    log.info("API Call: find - " + params)
    def result = [:]
    def searchParams = params

    if (!searchParams.mapRecords) {
      searchParams.skipDomainMapping = true
    }
    else {
      searchParams.remove("mapRecords")
    }

    try {
      result = ESSearchService.find(searchParams)
    }
    finally {
      if (result.errors) {
        response.setStatus(400)
      }
    }
    render result as JSON
  }


  /**
    * scroll : Deliver huge amounts of Elasticsearch data
    **/
  def scroll() {
    log.info("API Call: scroll - " + params)
    def result = [:]
    try {
      result = ESSearchService.scroll(params)
    }
    catch(Exception e){
      result.result = "ERROR"
      result.message = e.message
      result.cause = e.cause
      log.error("Could not process scroll request. Exception was: ${e.message}")
      response.setStatus(400)
    }
    render result as JSON
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

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def esconfig () {

    // If etag matches then we can just return the 304 to denote that the resource is unchanged.
    render grailsApplication.config.getProperty('wekb.es.searchApi', Map) as JSON
  }
}

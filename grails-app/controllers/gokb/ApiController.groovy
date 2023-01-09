package gokb


import de.wekb.helper.RCConstants
import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.gokb.cred.*
import org.springframework.security.access.annotation.Secured

import java.security.SecureRandom

@Slf4j
class ApiController {

  SecureRandom rand = new SecureRandom()
  def ESSearchService

  static def reversemap = ['subject':'subjectKw','componentType':'componentType','identifier':'identifiers.value']
  static def non_analyzed_fields = ['componentType','identifiers.value']

  private static final Closure TRANSFORMER_USER = {User u ->
    [
      "id"      : "${u.id}",
      "email"     : "${u.email}",
      "username"    : "${u.username}",
      "displayName"   : "${u.displayName ?: u.username}"
    ]
  }

  def springSecurityService
  def genericOIDService

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

/*  def refdata() {
    def result = [:];

    // Should take a type parameter and do the right thing. Initially only do one type
    switch ( params.type ) {
      case 'cp' :
        def oq = Org.createCriteria()
        def orgs = oq.listDistinct {
          roles {
            "owner" {
              eq('desc','Org.Role');
            }
            eq('value','Content Provider');
          }
          order("name", "asc")
        }
        result.datalist=new java.util.ArrayList()
        orgs.each { o ->
          result.datalist.add([ "value" : "${o.id}", "name" : (o.name) ])
        }
        break;

      case 'org' :
        def oq = Org.createCriteria()
        def orgs = oq.listDistinct {
          order("name", "asc")
        }
        result.datalist=new java.util.ArrayList()
        orgs.each { o ->
          result.datalist.add([ "value" : "${o.id}", "name" : (o.name) ])
        }
        break;
      default:
        break;
    }
    apiReturn(result)
  }*/

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

  /*// this is used as an entrypoint for single page apps based on frameworks like angular.
  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def search() {
    def result = [result: 'OK']

    User user = springSecurityService.currentUser

    log.debug("Entering SearchController:index");

    result.max = params.max ? Integer.parseInt(params.max) : user.defaultPageSizeAsInteger
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    if ( request.JSON ) {

        result.qbetemplate = request.JSON.cfg

        // Looked up a template from somewhere, see if we can execute a search
        if ( result.qbetemplate ) {

          Class target_class = Class.forName(result.qbetemplate.baseclass);
          def read_perm = target_class?.isTypeReadable()

          if (read_perm) {
            log.debug("Execute query");
            def qresult = [max:result.max, offset:result.offset]
            result.rows = doQuery(result.qbetemplate, params, qresult)
            log.debug("Query complete");
            result.lasthit = result.offset + result.max > qresult.reccount ? qresult.reccount : ( result.offset + result.max )

            // Add the page information.
            result.page_current = (result.offset / result.max) + 1
            result.page_total = (qresult.reccount / result.max).toInteger() + (qresult.reccount % result.max > 0 ? 1 : 0)
          }
          else {
            result.result = 'ERROR'
            result.code = 403
            response.setStatus(403)
            result.message = "Insufficient permissions to view this resource."

            log.debug("No permission to view this resource!")
          }
        }
        else {
          log.debug("no template ${result?.qbetemplate}");
        }
    }
    else {
      log.debug("No request json");
    }

    apiReturn(result)
  }*/

  /**
   * suggest : Get a list of autocomplete suggestions from ES
   *
   * @param max : Define result size
   * @param offset : Define offset
   * @param from : Define offset
   * @param q : Search term
   * @param componentType : Restrict search to specific component type (Package, Org, Platform, TIPP)
   * @param role : Filter by Org role (only in context of componentType=Org)
   * @return JSON Object
  **/

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
    RefdataValue yes = RefdataCategory.lookup(RCConstants.YN, 'Yes')
    //Set<Platform> counter4Platforms = Platform.findAllByCounterR4SushiApiSupportedAndCounterR5SushiApiSupportedNotEqual(yes, yes).toSet(), counter5Platforms = Platform.findAllByCounterR5SushiApiSupported(yes).toSet()
    Set counter4Platforms = Platform.executeQuery("select plat.uuid, plat.counterR4SushiServerUrl, plat.statisticsUpdate.value from Platform plat where plat.counterR4SushiApiSupported = :r4support and plat.counterR5SushiApiSupported != :r5support and plat.counterR4SushiServerUrl is not null", [r4support: yes, r5support: yes]).toSet()
    Set counter5Platforms = Platform.executeQuery("select plat.uuid, plat.counterR5SushiServerUrl, plat.statisticsUpdate.value from Platform plat where plat.counterR5SushiApiSupported = :r5support and plat.counterR5SushiServerUrl is not null", [r5support: yes]).toSet()
    result.counter4ApiSources = counter4Platforms.size() > 0 ? counter4Platforms : []
    result.counter5ApiSources = counter5Platforms.size() > 0 ? counter5Platforms : []
    render result as JSON
  }



/*  *//**
   * show : Returns a simplified JSON serialization of a domain class object
   * @param oid : The OID ("<FullyQualifiedClassName>:<PrimaryKey>") of the object
   * @param withCombos : Also return all combos directly linked to the object
  **//*
  def show() {
    def result = ['result':'OK', 'params': params]
    if (params.oid || params.id) {
      def obj = genericOIDService.resolveOID(params.oid ?: params.id)

      if ( obj?.isReadable() || (obj?.class?.simpleName == 'User' && obj?.equals(springSecurityService.currentUser)) ) {

        if(obj.class in KBComponent) {

          result.resource = obj.getAllPropertiesWithLinks(params.withCombos ? true : false)

          result.resource.combo_props = obj.allComboPropertyNames
        }
        else if (obj.class.name == 'org.gokb.cred.User'){

          def cur_groups = []

          obj.curatoryGroups?.each { cg ->
            cur_groups.add([name: cg.name, id: cg.id])
          }

          result.resource = ['id': obj.id, 'username': obj.username, 'displayName': obj.displayName, 'curatoryGroups': cur_groups]
        }
        else {
          result.resource = obj
        }
      }
      else if (!obj) {
        result.error = "Object ID could not be resolved!"
        response.setStatus(404)
        result.code = 404
        result.result = 'ERROR'
      }
      else {
        result.error = "Access to object was denied!"
        response.setStatus(403)
        result.code = 403
        result.result = 'ERROR'
      }
    }
    else {
      result.result = 'ERROR'
      response.setStatus(400)
      result.code = 400
      result.error = 'No object id supplied!'
    }

    render result as JSON
  }*/


/*  private def buildQuery(params) {

    StringWriter sw = new StringWriter()

    if ( params?.q != null )
      if(params.q.equals("*")){
        sw.write(params.q)
      }
      else{
        sw.write("(${params.q})")
      }
    else
      sw.write("*:*")

    // For each reverse mapping
    reversemap.each { mapping ->

      // log.debug("testing ${mapping.key}");

      // If the query string supplies a value for that mapped parameter
      if ( params[mapping.key] != null ) {

        // If we have a list of values, rather than a scalar
        if ( params[mapping.key].class == java.util.ArrayList) {
          params[mapping.key].each { p ->
            sw.write(" AND ")
            sw.write(mapping.value)
            sw.write(":")

            if(non_analyzed_fields.contains(mapping.value)) {
              sw.write("${p}")
            }
            else {
              sw.write("\"${p}\"")
            }
          }
        }
        else {
          // We are dealing with a single value, this is "a good thing" (TM)
          // Only add the param if it's length is > 0 or we end up with really ugly URLs
          // II : Changed to only do this if the value is NOT an *
          if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {
            sw.write(" AND ")
            // Write out the mapped field name, not the name from the source
            sw.write(mapping.value)
            sw.write(":")

            if(non_analyzed_fields.contains(mapping.value)) {
              sw.write("${params[mapping.key]}")
            }
            else {
              sw.write("\"${params[mapping.key]}\"")
            }
          }
        }
      }
    }

    def result = sw.toString();
    result;
  }

  def private doQuery (qbetemplate, params, result) {
    log.debug("doQuery ${result}");
    def target_class = grailsApplication.getArtefact("Domain",qbetemplate.baseclass);
    com.k_int.HQLBuilder.build(grailsApplication, qbetemplate, params, result, target_class, genericOIDService)
    def resultrows = []

    log.debug("process recset..");
    int seq = result.offset
    result.recset.each { rec ->
      // log.debug("process rec..");
      def response_row = [:]
      response_row['__oid'] = rec.class.name+':'+rec.id
      response_row['__seq'] = seq++
      qbetemplate.qbeConfig.qbeResults.each { r ->
        def ppath = r.property.split(/\./)
        def cobj = rec
        def final_oid = "${cobj.class.name}:${cobj.id}"

        ppath.eachWithIndex { prop, idx ->
          def sp = prop.minus('?')

          if( cobj?.class?.name == 'org.gokb.cred.RefdataValue' ) {
            cobj = cobj.value
          }
          else {
            if ( cobj && KBComponent.has(cobj, sp)) {
              if (sp == 'password' || sp == 'email') {
                cobj = null
              }
              else {
                cobj = cobj[sp]
              }

              if (ppath.size() > 1 && idx == ppath.size()-2) {
                if (cobj && sp != 'class') {
                  final_oid = "${cobj.class.name}:${cobj.id}"
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
        response_row["${r.property}"] = [heading: r.heading, link: (r.link ? (final_oid ?: response_row.__oid ) : null), value: (cobj ?: '-Empty-')]
      }
      resultrows.add(response_row);
    }
    resultrows
  }*/

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def esconfig () {

    // If etag matches then we can just return the 304 to denote that the resource is unchanged.
    render grailsApplication.config.wekb.es.searchApi as JSON
  }



  /**
   * See the service method {@link com.k_int.ESSearchService#getApiTunnel(java.lang.Object)} for usage instructions.
   */
  /*def elasticsearchTunnel() {
    def result = [:]
    try {
      result = ESSearchService.getApiTunnel(params)
    }
    catch(Exception e){
      result.result = "ERROR"
      result.message = e.message
      result.cause = e.cause
      log.error("Could not process Elasticsearch API request. Exception was: ${e.message}")
      response.setStatus(400)
    }

    if(result == null) {
      result = [:]
      result.result = "ERROR"
      result.message = 'Params is null or Params.q is null'
      response.setStatus(400)
    }

    render result as JSON
  }*/
}

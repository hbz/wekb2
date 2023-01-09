package wekb

import wekb.helper.RCConstants
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovyx.net.http.URIBuilder
import org.apache.lucene.search.join.ScoreMode
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchScrollRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.SortOrder

import java.text.ParseException
import java.text.SimpleDateFormat


class ESSearchService{
// Map the parameter names we use in the webapp with the ES fields
  def reversemap = [
      'curatoryGroup':'curatoryGroups.curatoryGroup',
      'cpname':'cpname',
      'provider':'provider',
      'componentType':'componentType',
      'lastUpdatedDisplay':'lastUpdatedDisplay']

  def ESWrapperService
  def grailsApplication
  def genericOIDService
  def classExaminationService

  def requestMapping = [
      generic: [
          "id",
          "uuid"
      ],
      simpleMap: [
          "curatoryGroup": "curatoryGroups.name",
          "curatoryGroupExact": "curatoryGroups.name.keyword",
          "role": "roles"
      ],
      complex: [
          "identifier",
          "ids",
          "identifiers",
          "ddcs",
          "ddc",
          "curatoryGroupType",
          "languages",
          "language",
          "status",
          "componentType",
          "platform",
          "suggest",
          "label",
          "name",
          "altname",
          "q"
      ],
      linked: [
          provider: "provider",
          publisher: "publisher",
          currentPublisher: "publisher",
          linkedPackage: "tippPackage",
          tippPackage: "tippPackage",
          pkg: "tippPackage",
          tippTitle: "tippTitle",
          linkedTitle: "tippTitle",
          title: "tippTitle"
      ],
      refdata: [
          "shibbolethAuthentication",
          "counterCertified",
          "ipAuthentication"
      ],
      dates: [
          "changedSince",
          "changedBefore"
      ],
      ignore: [
          "controller",
          "action",
          "max",
          "offset",
          "from",
          "skipDomainMapping",
          "sort",
          "order",
          "_embed",
          "_include",
          "_exclude"
      ]
  ]

  static Map indicesPerType = [
      "TitleInstancePackagePlatform" : "wekbtipps",
      "Org" : "wekborgs",
      "Package" : "wekbpackages",
      "Platform" : "wekbplatforms"
  ]


  def search(params){
    search(params,reversemap)
  }

  def search(params, field_map){
    log.debug("ESSearchService::search - ${params}")

    def result = [:]

    RestHighLevelClient esclient = ESWrapperService.getClient()

    try {
      if ( (params.q && params.q.length() > 0)) {

        if ((!params.all) || (!params.all?.equals("yes"))) {
          params.max = Math.min(params.max ? params.max : 15, 100)
        }

        params.offset = params.offset ? params.offset : 0

        def query_str = buildQuery(params,field_map)

        if (params.tempFQ) {
          log.debug("found tempFQ, adding to query string")
          query_str = query_str + " AND ( " + params.tempFQ + " ) "
          params.remove("tempFQ") //remove from GSP access
        }


        log.info("query: ${query_str}")

        SearchResponse searchResponse
        try {
          SearchRequest searchRequest = new SearchRequest()
          SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
          log.debug("srb built: ${searchSourceBuilder} sort=${params.sort}")
          if (params.sort) {
            SortOrder order = SortOrder.ASC
            if (params.order) {
              order = SortOrder.valueOf(params.order?.toUpperCase())
            }
            searchSourceBuilder.sort(new FieldSortBuilder("${params.sort}").order(order))
          }
          log.debug("srb start to add query and aggregration query string is ${query_str}")

          List providerRoles = [RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Content Provider'), RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Platform Provider'), RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Publisher')]

          Integer countCuratoryGroups = CuratoryGroup.executeQuery("select count(o.id) from CuratoryGroup as o where status != :forbiddenStatus", [forbiddenStatus : RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, KBComponent.STATUS_DELETED)], [readOnly: true])[0]
          Integer countProvider = Org.executeQuery("select count(o.id) from Org as o join o.roles rdv where rdv in (:roles) and o.status != :forbiddenStatus", [forbiddenStatus : RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, KBComponent.STATUS_DELETED), roles: providerRoles], [readOnly: true])[0]


          searchSourceBuilder.query(QueryBuilders.queryStringQuery(query_str))
          searchSourceBuilder.aggregation(AggregationBuilders.terms('curatoryGroup').size(countCuratoryGroups).field('curatoryGroups.curatoryGroup'))
          searchSourceBuilder.aggregation(AggregationBuilders.terms('provider').size(countProvider).field('provider'))

          searchSourceBuilder.from(params.offset)
          searchSourceBuilder.size(params.max)

          searchRequest.source(searchSourceBuilder)

          searchResponse = esclient.search(searchRequest, RequestOptions.DEFAULT)

        }
        catch (Exception ex) {
          log.error("Error processing  ${query_str}",ex)
        }

        //TODO: change this part to represent what we really need if this is not it, see the final part of this method where hits are done
        if (searchResponse) {
          result.hits = searchResponse.getHits()
          result.firstrec = params.offset + 1
          result.resultsTotal = searchResponse.getHits().getTotalHits().value ?: 0
          result.lastrec = Math.min ( params.offset + params.max, result.resultsTotal)

          if (searchResponse.getAggregations()) {
            result.facets = [:]
            searchResponse.getAggregations().each { entry ->
              log.debug("Aggregation entry ${entry} ${entry.getName()}")
              def facet_values = []
              if(entry.type == 'nested'){
                entry.getAggregations().each { subEntry ->
                  subEntry.buckets.each { bucket ->
                    bucket.each { bi ->
                      String display = "Unknown"
                      if (bi.getKey().startsWith('wekb') && KBComponent.get(bi.getKey().split(':')[1].toLong())) {
                        display = KBComponent.get(bi.getKey().split(':')[1].toLong()).name
                      }
                      facet_values.add([term: bi.getKey(), display: display, count: bi.getDocCount()])
                    }
                  }
                }
              }else {
                entry.buckets.each { bucket ->
                  bucket.each { bi ->
                    String display = "Unknown"
                    if (bi.getKey().startsWith('wekb') && KBComponent.get(bi.getKey().split(':')[1].toLong())) {
                      display = KBComponent.get(bi.getKey().split(':')[1].toLong()).name
                    }
                    facet_values.add([term: bi.getKey(), display: display, count: bi.getDocCount()])
                  }
                }
              }
              result.facets[entry.getName()] = facet_values
            }
          }
        }
        log.debug("finished results facets")
      }
      else {
        log.debug("No query.. Show search page")
      }
    }
    finally {
      try {
        esclient.close()
      }
      catch ( Exception e ) {
        log.error("Problem by Close ES Client",e)
      }
    }
    result
  }

  def buildQuery(params,field_map) {

    log.debug("BuildQuery... with params ${params}. ReverseMap: ${field_map}")

    StringWriter sw = new StringWriter()

    if ( params?.q != null ){
      sw.write("name:${params.q}")
    }

    field_map.each { mapping ->

      if ( params[mapping.key] != null ) {

        log.debug("Class is: ${params[mapping.key].class.name}")

        if ( params[mapping.key] instanceof String[] ) {
          log.debug("mapping is an arraylist: ${mapping} ${mapping.key} ${params[mapping.key]}")
          if(sw.toString()) sw.write(" AND ")

          def plist = params[mapping.key]

          plist.eachWithIndex { p, idx ->
            if (p) {
              if (idx == 0){
                sw.write(" ( ")
              }
              sw.write(mapping.value?.toString())
              sw.write(":".toString())

              p = p.replaceAll(":","\\\\:")

              sw.write(p.toString())
              if(idx == plist.size()-1) {
                sw.write(" ) ")
              }else{
                sw.write(" OR ")
              }
            }
          }
        }
        else {
          // Only add the param if it's length is > 0 or we end up with really ugly URLs
          // II : Changed to only do this if the value is NOT an *

          log.debug("Processing - scalar value : ${params[mapping.key]}")

          try {
            if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {

              def pval = params[mapping.key].replaceAll(":","\\\\:")

              log.debug("pval = ${pval}")

              if(sw.toString()) sw.write(" AND ")

              sw.write(mapping.value)
              sw.write(":")

              if(params[mapping.key].startsWith("[") && params[mapping.key].endsWith("]")){
                sw.write(pval)
              }else{
                sw.write(pval)
              }
            }
          }
          catch ( Exception e ) {
            log.error("Problem procesing mapping, key is ${mapping.key} value is ${params[mapping.key]}",e)
          }
        }
      }
    }

    if(!params['status']) {
      sw.write(" AND NOT (status:Deleted)")
    }

    def result = sw.toString()
    log.debug("Result of buildQuery is ${result}")

    result
  }

  private void checkInt(result, errors, str, String field) {
    def value = null
    if (str && str instanceof String) {
      try {
        value = str as Integer
        result[field] = value
      } catch (Exception e) {
        errors[field] = "Could not convert ${field} to Int."
      }
    }
    else if (str && str instanceof Integer) {
      result[field] = value
    }
  }

  private void addDateQueries(query, errors, qpars) {
    if ( qpars.changedSince || qpars.changedBefore ) {
      QueryBuilder dateQuery = QueryBuilders.rangeQuery("lastUpdatedDisplay")

      if (qpars.changedSince) {
        dateQuery.gte(qpars.changedSince)
      }
      if (qpars.changedBefore) {
        dateQuery.lt(qpars.changedBefore)
      }
      dateQuery.format("yyyy-MM-dd HH:mm:ss||yyyy-MM-dd")

      query.must(dateQuery)
    }
  }

  private void addStatusQuery(query, errors, status) {
    if (!status){
      query.must(QueryBuilders.termQuery('status', 'Current'))
      return
    }
    QueryBuilder statusQuery = QueryBuilders.boolQuery()
    if (status.getClass().isArray() || status instanceof List){
      status.each {
        addStatusToQuery(it, statusQuery)
      }
    }
    if (status instanceof String){
      addStatusToQuery(status, statusQuery)
    }
    //statusQuery.minimumNumberShouldMatch(1)
    query.must(statusQuery)
    return
  }


  private void addStatusToQuery(String status, QueryBuilder statusQuery){
    try{
      status = RefdataValue.get(Long.valueOf(status))
    }
    catch (Exception e){
    }
    statusQuery.should(QueryBuilders.termQuery('status', status))
  }


  private void addIdentifierQuery(query, errors, qpars, boolean orLinked) {
    List valList = []

    if (qpars.identifier) {
      valList.addAll(qpars.list("identifier"))
    }
    else if (qpars.ids) {
      valList.addAll(qpars.list("ids"))
    }
    else if (qpars.identifiers) {
      valList.addAll(qpars.list("identifiers"))
    }

    valList.each { String val ->
      if ( val.trim() ) {
        Map id_params = [:]
        if (val.contains(',')) {
          id_params['identifiers.namespace'] = val.split(',')[0]
          id_params['identifiers.value'] = val.split(',')[1]
        }else{
          id_params['identifiers.value'] = val
        }

        log.debug("Query ids for ${id_params}")
        if(orLinked)
          query.should(QueryBuilders.nestedQuery("identifiers", addIdQueries(id_params), ScoreMode.Max))
        else
          query.must(QueryBuilders.nestedQuery("identifiers", addIdQueries(id_params), ScoreMode.Max))
      }
    }

  }

  private void processNameFields(query, errors, qpars) {
    if (qpars.label) {

      QueryBuilder labelQuery = QueryBuilders.boolQuery()

      if (qpars.int('label')) {
        def oid = KBComponent.get(qpars.int('label'))?.uuid ?: null

        if (oid) {
          labelQuery.should(QueryBuilders.termQuery('uuid', oid).boost(10))
        }
      }
      else {
        labelQuery.should(QueryBuilders.termQuery('uuid', qpars.q).boost(10))
      }

      labelQuery.should(QueryBuilders.wildcardQuery('name', "*${qpars.label.toLowerCase()}*").boost(2))
      labelQuery.should(QueryBuilders.wildcardQuery('altname', "*${qpars.label.toLowerCase()}*").boost(1.3))
      labelQuery.should(QueryBuilders.wildcardQuery('suggest', "*${qpars.label.toLowerCase()}*").boost(0.6))
      labelQuery.minimumShouldMatch(1)

      query.must(labelQuery)
    }
    else if (qpars.name) {
      query.must(QueryBuilders.wildcardQuery('name', "*${qpars.name.toLowerCase()}*"))
    }
    else if (qpars.altname) {
      query.must(QueryBuilders.wildcardQuery('altname', "*${qpars.altname.toLowerCase()}*"))
    }
    else if (qpars.suggest) {
      query.must(QueryBuilders.wildcardQuery('suggest', "*${qpars.suggest}*").boost(0.6))
    }
  }

  private void processGenericFields(query, errors, qpars) {
    if (qpars.q?.trim()) {
      QueryBuilder genericQuery = QueryBuilders.boolQuery()
      def id_params = ['identifiers.value': qpars.q]

      if (qpars.int('q')) {
        def oid = KBComponent.get(qpars.int('q'))?.uuid ?: null

        if (oid) {
          genericQuery.should(QueryBuilders.termQuery('uuid', oid).boost(10))
        }
      }
      else {
        genericQuery.should(QueryBuilders.termQuery('uuid', qpars.q).boost(10))
      }

      genericQuery.should(QueryBuilders.matchQuery('name', qpars.q).boost(2))
      genericQuery.should(QueryBuilders.matchQuery('altname', qpars.q).boost(1.3))
      genericQuery.should(QueryBuilders.matchQuery('suggest', qpars.q).boost(0.6))
      genericQuery.should(QueryBuilders.nestedQuery('identifiers', addIdQueries(id_params), ScoreMode.Max).boost(10))
      genericQuery.minimumShouldMatch(1)

      query.must(genericQuery)
    }
  }

  private void processNestedFields(query, errors, qpars) {
    //QueryBuilder subQuery = QueryBuilders.boolQuery()
    String field
    Map<String, String> subQueryParams = [:]
    //TODO this may be extended upon free-text (then, I need wildcardQuery as second field)
    if(qpars.ddc) {
      subQueryParams['ddcs.value'] = qpars.list("ddc")
    }
    else if(qpars.ddcs) {
      subQueryParams['ddcs.value'] = qpars.list("ddcs")
    }
    if(qpars.language) {
      subQueryParams['languages.value'] = qpars.list("language")
    }
    else if(qpars.languages) {
      subQueryParams['languages.value'] = qpars.list("languages")
    }
    if(qpars.curatoryGroupType) {
      subQueryParams['curatoryGroups.type'] = qpars.curatoryGroupType
    }
    subQueryParams.each { String k, v ->
      if(v instanceof String) {
        if(k == 'curatoryGroups.type' && v.toLowerCase() == 'other') {
          QueryBuilder curatoryGroupsSubQuery = QueryBuilders.boolQuery()
          curatoryGroupsSubQuery.should(QueryBuilders.termQuery(k, v))
          curatoryGroupsSubQuery.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(k)))
          query.must(curatoryGroupsSubQuery)
        }
        else {
          query.must(QueryBuilders.termQuery(k, v))
        }
      }
      else if(v instanceof List) {
        QueryBuilder listSubQuery = QueryBuilders.boolQuery()
        v.each { String subV ->
          listSubQuery.should(QueryBuilders.termQuery(k, subV))
        }
        query.must(listSubQuery)
      }
    }
  }

  private void processLinkedField(query, field, val) {
    QueryBuilder linkedFieldQuery = QueryBuilders.boolQuery()
    def finalVal = val

    try {
      finalVal = KBComponent.get(Long.valueOf(val)).getLogEntityId()
    }
    catch (java.lang.NumberFormatException nfe) {
    }
    if(finalVal instanceof String)
        finalVal = finalVal.replaceAll('ampersand', '&')
    println "processLinkedField: ${field} -> ${finalVal}"

    linkedFieldQuery.should(QueryBuilders.termQuery(field, finalVal))
    linkedFieldQuery.should(QueryBuilders.termQuery("${field}Uuid".toString(), finalVal))
    //linkedFieldQuery.should(QueryBuilders.termQuery("${field}Name".toString(), val))
    linkedFieldQuery.should(QueryBuilders.wildcardQuery("${field}Name".toString(), "*${finalVal}*")) //check if it does not cause side effects! May be subject of further precision!
    linkedFieldQuery.minimumShouldMatch(1)

    query.must(linkedFieldQuery)
  }

  private void addPlatformQuery(query, errors, val) {
    QueryBuilder linkedFieldQuery = QueryBuilders.boolQuery()

    linkedFieldQuery.should(QueryBuilders.termQuery('nominalPlatform', val))
    linkedFieldQuery.should(QueryBuilders.termQuery('nominalPlatformName', val))
    linkedFieldQuery.should(QueryBuilders.termQuery('nominalPlatformUuid', val))
    linkedFieldQuery.should(QueryBuilders.termQuery('hostPlatform', val))
    linkedFieldQuery.should(QueryBuilders.termQuery('hostPlatformName', val))
    linkedFieldQuery.should(QueryBuilders.termQuery('hostPlatformUuid', val))
    linkedFieldQuery.minimumShouldMatch(1)

    query.must(linkedFieldQuery)

    log.debug("Processing platform value ${val} .. ")
  }


  /**
   * scroll : Get large amounts of data from the Elasticsearch index --
   * @param params : Elasticsearch query params
   * @return chunks of scrollSize data sets. In case the answer's size is smaller than scrollSize,
   *         then the end of scrolling is reached.
   **/
  def scroll(params) throws Exception{
    def result = [:]
    def usedComponentTypes = getUsedComponentTypes(params, result)
    if (result.error){
      return result
    }
    // now search
    int scrollSize = params.max ? params.int("max") : 5000
    result.result = "OK"
    result.scrollSize = scrollSize
    RestHighLevelClient esclient = ESWrapperService.getClient()
    def errors = [:]                              // TODO: use errors

    def unknown_fields = []

    SearchResponse searchResponse

    try {
      if (!params.scrollId) {
        QueryBuilder scrollQuery = QueryBuilders.boolQuery()
        if (params.component_type) {
          QueryBuilder typeFilter = QueryBuilders.matchQuery("componentType", params.component_type)
          scrollQuery.must(typeFilter)
        }
        if (params.componentType) {
          QueryBuilder typeFilter = QueryBuilders.matchQuery("componentType", params.componentType)
          scrollQuery.must(typeFilter)
        }
        addStatusQuery(scrollQuery, errors, params.status)
        //TODO: add this after upgrade to Elasticsearch 7 -> DONE
        addDateQueries(scrollQuery, errors, params)
        //TODO: alternative query builders for scroll searches with q
        specifyQueryWithParams(params, scrollQuery, errors, unknown_fields)

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
        searchSourceBuilder.query(scrollQuery)
        searchSourceBuilder.size(scrollSize)
        //SearchRequest searchRequest = new SearchRequest(usedComponentTypes.values() as String[])
        SearchRequest searchRequest = new SearchRequest(grailsApplication.config.wekb.es.searchApi.indices as String[])
        //searchRequest.scroll("1m")
        // ... set scroll interval to 15 minutes, reason: ERMS-3460
        //SearchRequest searchRequest = new SearchRequest(grailsApplication.config.wekb.es.index)
        searchRequest.scroll("15m")
        searchRequest.source(searchSourceBuilder)
        searchResponse = esclient.search(searchRequest, RequestOptions.DEFAULT)
        result.lastPage = 0
      } else {
        SearchScrollRequest scrollRequest = new SearchScrollRequest(params.scrollId)
        scrollRequest.scroll("15m")
        searchResponse = esclient.scroll(scrollRequest, RequestOptions.DEFAULT)
        try {
          if (params.lastPage && Integer.valueOf(params.lastPage) > -1) {
            result.lastPage = Integer.valueOf(params.lastPage) + 1
          }
        }
        catch (Exception e) {
          log.debug("Could not process page information on scroll request.")
        }
      }
      result.scrollId = searchResponse.getScrollId()

      SearchHit[] searchHits = searchResponse.getHits().getHits()
      result.hasMoreRecords = searchHits.length == scrollSize

      // TODO: remove this after upgrade to Elasticsearch 7
      //result.records = filterLastUpdatedDisplay(searchHits, params, errors, result)

      result.records = []
      searchHits.each { r ->
        result.records.add(r.getSourceAsMap().sort {it.key})
      }

    }
    finally {
      try {
        esclient.close()
      }
      catch ( Exception e ) {
        log.error("Problem by Close ES Client",e)
      }
    }

    result.size = result.records.size()
    result
  }

  private Map getUsedComponentTypes(params, LinkedHashMap<Object, Object> result){
    Map usedComponentTypes = new HashMap()
    if (!params.component_type){
      result.result = "ERROR"
      result.message = "Error. Needs 'component_type' specification."
    }

    if (params.component_type instanceof String){
      usedComponentTypes."${params.component_type}" = null
    }
    else if (params.component_type instanceof List){
      for (def componentType in params.component_type){
        usedComponentTypes."${componentType}" = null
      }
    }
    for (def ct in usedComponentTypes.keySet()){
      if (ct in indicesPerType.keySet()){
        usedComponentTypes."${ct}" = indicesPerType.get(ct)
      }
      else{
        result.result = "ERROR"
        result.message = "Error. Wrong 'component_type' specification: ${ct}"
      }
    }
    return usedComponentTypes
  }


  /**
   * This is a workaround for the not working scroll request with date range query in Elasticsearch 5.6.10.
   * TODO: check if this can be removed when having migrated to a higher Elasticsearch version.
   */
  private List<SearchHit> filterLastUpdatedDisplay(SearchHit[] searchHitsArray, params,
                                               Map<String, Object> errors, Serializable result){
    List filteredHits = []
    SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd")
    SimpleDateFormat YYYY_MM_DD_HH_mm_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    Date changedSince = parseDate(params.changedSince, YYYY_MM_DD_HH_mm_SS, YYYY_MM_DD, ISO)
    for (SearchHit hit in searchHitsArray){
      String dateString = hit.getSourceAsMap().get("lastUpdatedDisplay")
      Date date
      if(dateString) {
        try {
          date = ISO.parse(dateString)
        }
        catch (ParseException ignored) {
          date = YYYY_MM_DD_HH_mm_SS.parse(dateString)
        }
        if (changedSince == null || date && !date.before(changedSince)){
          filteredHits.add(hit.getSourceAsMap())
        }
      }
    }
    return filteredHits
  }


  private Date parseDate(String dateString, SimpleDateFormat... dateFormats){
    for (SimpleDateFormat format in dateFormats){
      try{
        return format.parse(dateString)
      }
      catch (Exception e){
        continue
      }
    }
    return null
  }


  /**
   * find : Query the Elasticsearch index --
   * @param params : Elasticsearch query params
   * @param context : Overrides default url path
   **/
  def find(params, def context = null, def user = null) {
    def result = [result: 'OK']
    def search_action = null
    SearchResponse searchResponse = null
    def errors = [:]
    log.debug("find :: ${params}")

    try {
      def unknown_fields = []
      def component_type = null
      if (params.componentType){
        component_type = deriveComponentType(params.componentType)
      }

      QueryBuilder exactQuery = QueryBuilders.boolQuery()

      filterByComponentType(exactQuery, component_type, params)
      addStatusQuery(exactQuery, errors, params.status)
      addDateQueries(exactQuery, errors, params)
      processNestedFields(exactQuery, errors, params)
      processGenericFields(exactQuery, errors, params)
      if(params.name && ["ids","identifier","identifiers"].any { String identifierKey -> params.keySet().contains(identifierKey) }) {
        QueryBuilder nameIdentifierQuery = QueryBuilders.boolQuery()
        nameIdentifierQuery.should(QueryBuilders.wildcardQuery('name', "*${params.name.toLowerCase()}*"))
        addIdentifierQuery(nameIdentifierQuery, errors, params, true)
        exactQuery.must(nameIdentifierQuery).boost(2)
      }
      else {
        processNameFields(exactQuery, errors, params)
        addIdentifierQuery(exactQuery, errors, params, false)
      }
      specifyQueryWithParams(params, exactQuery, errors, unknown_fields)

      if(unknown_fields.size() > 0){
        errors['unknown_params'] = unknown_fields
      }

      if( !errors && exactQuery.hasClauses() ) {
        RestHighLevelClient esclient = ESWrapperService.getClient()

        try {

          SearchRequest searchRequest = new SearchRequest(grailsApplication.config.wekb.es.searchApi.indices as String[])
          SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()

          searchSourceBuilder.query(exactQuery)
          searchRequest.source(searchSourceBuilder)

          /* SearchRequestBuilder es_request =  esclient.prepareSearch()
        //println exactQuery
        es_request.setIndices(grailsApplication.config.wekb.es.searchApi.indices as String[])
        es_request.setTypes(grailsApplication.config.wekb.es.searchApi.types)
        es_request.setQuery(exactQuery)*/

          checkInt(result, errors, params.max, 'max')
          checkInt(result, errors, params.from, 'offset')
          checkInt(result, errors, params.offset, 'offset')

          if (params.max) {
            searchSourceBuilder.size(result.max)
          } else {
            result.max = 10
          }

          if (params.offset || params.from) {
            searchSourceBuilder.from(result.offset)
          } else {
            result.offset = 0
          }

          if (params.sort && params.sort instanceof String) {
            def sortBy = params.sort

            if (sortBy == "name") {
              sortBy = "sortname"
            }

            if (ESWrapperService.mapping.properties[sortBy]?.type == 'text') {
              errors['sort'] = "Unable to sort by text field ${sortBy}!"
            } else {
              FieldSortBuilder sortQry = new FieldSortBuilder(sortBy)
              SortOrder order = SortOrder.ASC

              if (params.order) {
                if (params.order.toUpperCase() in ['ASC', 'DESC']) {
                  order = SortOrder.valueOf(params.order?.toUpperCase())
                } else {
                  errors['order'] = "Unknown sort order value '${params.order}'!"
                }
              }

              sortQry.order(order)

              searchSourceBuilder.sort(sortQry)
            }
          }

          if (!errors) {
            searchRequest.source(searchSourceBuilder)
            searchResponse = esclient.search(searchRequest, RequestOptions.DEFAULT)
          }
        }
          finally {
            try {
              esclient.close()
            }
            catch ( Exception e ) {
              log.error("Problem by Close ES Client",e)
            }
          }
      }
      else if ( !exactQuery.hasClauses() ){
        errors['params'] = "No valid parameters found"
      }

      if (searchResponse) {
        SearchHits hits = searchResponse.getHits()


        /*if(hits.maxScore == Float.NaN) { //we cannot parse NaN to json so set to zero...
          hits.maxScore = 0
        }*/


        result.count = hits.getTotalHits().value ?: 0
        result.records = []

        hits.each { r ->
          def response_record = [:]

          if (!params.skipDomainMapping) {
            response_record = mapEsToDomain(r, params)
          }
          else {
            response_record.id = r.id

            if (response_record.score && response_record.score != Float.NaN) {
              response_record.score = r.score
            }

            r.getSourceAsMap().each { field, val ->
              response_record."${field}" = val
            }

            response_record = response_record.sort {it.key}
          }

          result.records.add(response_record)
        }

        if (!params.skipDomainMapping) {
          def contextPath = "/entities"

          if(context) {
            contextPath = context
          }
          else if (component_type) {
            def obj_cls = Class.forName("wekb.${component_type}").newInstance()
            contextPath = obj_cls.restPath
          }

          convertEsLinks(result, params, contextPath)
        }
      }
    } catch (Exception se) {
      se.printStackTrace()
      result = [:]
      result.result = "ERROR"
      result.errors = ['unknown': "There has been an unknown error processing the search request!"]
    } finally {
      if (errors) {
        result = [:]
        result.result = "ERROR"
        result.errors = errors
      }
    }

    result
  }

  private void specifyQueryWithParams(params, QueryBuilder exactQuery, errors, unknown_fields){
    def platformParam = null
    params.each{ k, v ->
      if (requestMapping.generic && k in requestMapping.generic){
        def final_val = v

        if (k == 'id' && params.int('id')) {
          final_val = KBComponent.get(params.int('id'))?.getLogEntityId()
        }

        exactQuery.must(QueryBuilders.matchQuery(k, final_val))
      }
      else if (requestMapping.simpleMap?.containsKey(k)){
        exactQuery.must(QueryBuilders.matchQuery(requestMapping.simpleMap[k], v.replaceAll('ampersand', '&')))
      }
      else if (requestMapping.linked?.containsKey(k)){
        processLinkedField(exactQuery, requestMapping.linked[k], v)
      }
      else if (requestMapping.refdata && k in requestMapping.refdata){
        QueryBuilder subQuery = QueryBuilders.boolQuery()
        List<String> values = params.list(k)
        values.each { String value ->
          if(value == "null") {
            subQuery.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(k)))
          }
          else subQuery.should(QueryBuilders.matchQuery(k, value))
        }
        subQuery
        exactQuery.must(subQuery)
      }
      else if (k.contains('platform') || k.contains('Platform')){
        if (!platformParam){
          platformParam = k
          addPlatformQuery(exactQuery, errors, v)
        }
        else{
          errors[k] = "Platform filter has already been defined by parameter '${platformParam}'!"
        }
      }
      else if (requestMapping.dates && k in requestMapping.dates){
        log.debug("Processing date param ${k}")
      }
      else if (requestMapping.complex && k in requestMapping.complex){
        log.debug("Processing complex param ${k}")
      }
      else if (requestMapping.ignore && k in requestMapping.ignore){
        log.debug("Processing unmapped param ${k}")
      }
      else{
        unknown_fields.add(k)
      }
    }
  }


  private void filterByComponentType(BoolQueryBuilder exactQuery, component_type, params){
    if (params.componentType){
      if (component_type){
        exactQuery.must(QueryBuilders.termQuery('componentType', component_type))
      }
      log.debug("Using component type ${component_type}")
    }
    else{
      log.debug("Not filtering by component type..")
    }
  }


  /**
   *  mapEsToDomain : Maps an ES record to its final REST serialization.
   * @param record : An ES record map
   * @param params : Request params
   */

  private Map mapEsToDomain(record, params) {
    def domainMapping = [:]
    def base = grailsApplication.config.serverUrl + "/rest"
    def linkedObjects = [:]
    def embed_active = params['_embed']?.split(',') ?: []
    def include_list = params['_include']?.split(',') ?: null
    def exclude_list = params['_exclude']?.split(',') ?: null
    Integer rec_id = genericOIDService.oidToId(record.id)
    def esMapping = [
        'lastUpdatedDisplay': 'lastUpdated',
        'sortname': false,
        'updater': false,
        'identifiers': false,
        'altname': false,
        'roles': false,
        'curatoryGroups': false
    ]

    def obj_cls = Class.forName("wekb.${record.source.componentType}").newInstance()

    if (obj_cls) {

      if (obj_cls.hasProperty('jsonMapping') && obj_cls.jsonMapping.es) {
        esMapping << obj_cls.jsonMapping.es
        log.debug("Found es mapping for class.")
      }
      else {
        log.debug("No es mapping found for class ${obj_cls} ..")
      }

      if (obj_cls.hasProperty('restPath')) {
        domainMapping['_links'] = ['self': ['href': base + obj_cls.restPath + "/${rec_id}"]]
      }

      domainMapping['_embedded'] = [:]

      domainMapping['id'] = rec_id

      record.source.each { field, val ->
        def toSkip = (include_list && !include_list.contains(field)) || (exclude_list?.contains(field))

        if (field == "curatoryGroups" && !toSkip) {
          mapCuratoryGroups(domainMapping, val)
        }
        else if (field == "altname" && !toSkip) {
          domainMapping['_embedded']['variantNames'] = val
        }
        else if (field == "identifiers" && !toSkip) {
          domainMapping['_embedded']['ids'] = mapIdentifiers(val)
        }
        else if (!toSkip && (field == "status")) {
          domainMapping[field] = [id: RefdataCategory.lookup("KBComponent.${field}", val).id, name: val]
        }
        else if (esMapping[field] == false) {
          log.debug("Skipping field ${field}!")
        }
        else if (esMapping[field] == "refdata" && !toSkip) {
          if (val) {
            def cat = classExaminationService.deriveCategoryForProperty("wekb.${record.source.componentType}", field)
            def rdv = RefdataCategory.lookup(cat, val)
            domainMapping[field] = [id: rdv.id, name:rdv.value]
          }
          else {
            domainMapping[field] = null
          }
        }
        else if ( esMapping[field] && !toSkip ) {
          log.debug("Field ${esMapping[field]}")
          def fieldPath = esMapping[field].split("\\.")
          def isNull = false
          log.debug("FieldPath: ${fieldPath}")

          if (fieldPath.size() == 2) {
            if (!linkedObjects[fieldPath[0]]) {
              linkedObjects[fieldPath[0]] = [:]
            }
            if (fieldPath[1] == 'id') {
              def id_val = genericOIDService.oidToId(val)

              if (id_val) {
                linkedObjects[fieldPath[0]][fieldPath[1]] = genericOIDService.oidToId(val)
              }
              else {
                isNull = true
              }
            }
            else {
              linkedObjects[fieldPath[0]][fieldPath[1]] = val
            }
          } else {
            domainMapping[fieldPath[0]] = val
          }
        }
        else if (!toSkip) {
          log.debug("Transfering unmapped field ${field}:${val}")
          if (val) {
            domainMapping[field] =  val
          }
          else {
            domainMapping[field] = null
          }
        }
      }

      linkedObjects.each { field, val ->
        if (val.id) {
          domainMapping[field] = val
        }
        else {
          domainMapping[field] = null
        }
      }
    }

    log.debug("${domainMapping}")
    return domainMapping
  }

  /**
   *  convertEsLinks : Converts Elasticsearch response layout to conform with REST mapping.
   * @param es_result : The result object
   * @param params : Request parameters
   * @param component_endpoint : Possible URL path override
   */

  private def convertEsLinks(es_result, params, component_endpoint) {
    def base = grailsApplication.config.serverUrl + "/rest" + "${component_endpoint}"

    es_result['_links'] = [:]
    es_result['data'] = es_result.records
    es_result.remove('records')
    es_result.remove('result')
    es_result['_pagination'] = [
        offset: es_result.offset,
        limit: es_result.max,
        total: es_result.count
    ]

    def selfLink = new URIBuilder(base)
    selfLink.addQueryParams(params)

    params.each { p, vals ->
      log.debug("handling param ${p}: ${vals}")
      if (vals instanceof String[]) {
        selfLink.removeQueryParam(p)
        vals.each { val ->
          if (val?.trim()) {
            log.debug("Val: ${val} -- ${val.class.name}")
            selfLink.addQueryParam(p, val)
          }
        }
        log.debug("${selfLink.toString()}")
      }
    }
    if(params.controller) {
      selfLink.removeQueryParam('controller')
    }
    if (params.action) {
      selfLink.removeQueryParam('action')
    }
    if (params.componentType) {
      selfLink.removeQueryParam('componentType')
    }
    es_result['_links']['self'] = [href: selfLink.toString()]


    if (es_result.count > es_result.offset+es_result.max) {
      def nextLink = selfLink

      if(nextLink.query.offset){
        nextLink.removeQueryParam('offset')
      }

      nextLink.addQueryParam('offset', "${es_result.offset + es_result.max}")
      es_result['_links']['next'] = ['href': (nextLink.toString())]
    }
    if (es_result.offset > 0) {
      def prevLink = selfLink

      if(prevLink.query.offset){
        prevLink.removeQueryParam('offset')
      }

      prevLink.addQueryParam('offset', "${(es_result.offset - es_result.max) > 0 ? es_result.offset - es_result.max : 0}")
      es_result['_links']['prev'] = ['href': prevLink.toString()]
    }
    es_result.remove("offset")
    es_result.remove("max")
    es_result.remove("count")
    //println(es_result)

    es_result
  }

  private def mapIdentifiers(ids) {
    def idmap = []
    ids.each { id ->
      def ns = IdentifierNamespace.findByValueIlike(id.namespace)
      idmap << [namespace : [value: id.namespace, name: ns.name, id: ns.id], value: id.value ]
    }
    idmap
  }

  /**
   *  mapCuratoryGroups : Generates an embed object for curatoryGroups listed in ES.
   * @param domainMapping : The current domainMapping object
   * @param cgs : The array of names of connected curatory groups
   */

  private def mapCuratoryGroups(domainMapping, cgs) {
    def base = grailsApplication.config.serverUrl + "/rest"

    domainMapping['_embedded']['curatoryGroups'] = []
    cgs.each { cg ->
      def cg_obj = CuratoryGroup.findByName(cg)
      domainMapping['_embedded']['curatoryGroups'] << [
          'links': [
              'self': [
                  'href': base + cg_obj.getRestPath()+"/" + cg_obj.uuid
              ]
          ],
          'name': cg_obj.name,
          'id': cg_obj.id,
          'uuid': cg_obj.uuid
      ]
    }
  }

  /**
   *  deriveComponentType : Selects the actual componentType of a ES record.
   * @param typeString : The componentType parameter of the request
   */

  private def deriveComponentType(String typeString) {
    def result = null
    def defined_types = [
        "Package",
        "Org",
        "Platform",
        "TitleInstancePackagePlatform",
        "TIPP",
    ]
    def final_type = typeString.capitalize()

    if (final_type in defined_types) {

      if (final_type== 'TIPP') {
        final_type = 'TitleInstancePackagePlatform'
      }
      result = final_type
    }
    result
  }

  private def addIdQueries(params) {

    QueryBuilder idQuery = QueryBuilders.boolQuery()

    if(params.containsKey('identifiers.namespace')) {
      idQuery.must(QueryBuilders.termQuery('identifiers.namespace', params['identifiers.namespace']))
      idQuery.must(QueryBuilders.wildcardQuery('identifiers.value', params['identifiers.value']).caseInsensitive(true))
    }
    else {
      params.each { k, v ->
        idQuery.must(QueryBuilders.termQuery(k, v))
      }
    }

    return idQuery
  }

  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy")
  }


  /**
   * Tunnels the full query string to Elasticsearch and returns the full Elasticsearch result. Only GET operations
   * are possible. The purpose of this endpoint is not to need to open the Elasticsearch port (usually 9200) for the
   * outside world, in order to prevent non-GET operations.
   * @param params The params necessary for this operation.
   * @param params.q The query string for Elasticsearch
   * @param params.size Optional parameter: The maximum size of the result.
   * @return The exact Json response of the Elasticsearch GET operation.
   * @throws Exception Any exception occuring.
   */
  def getApiTunnel(def params) throws Exception{
    if (!params || !params.q){
      return null
    }

    params.q = URLEncoder.encode(params.q, "UTF-8")

    int port = grailsApplication.config.wekb.es.searchApi.port
    def indices = grailsApplication.config.wekb.es.searchApi.indices
    String host = grailsApplication.config.wekb.es.host
    String url = "http://${host}:${port}/${indices.join(',')}/_search?q=${params.q}"
    if (params.size){
      url = url + "&size=${params.size}"
    }
    HTTPBuilder httpBuilder = new HTTPBuilder(url)
    httpBuilder.request(Method.GET){ req ->
      response.success = { resp, html ->
        return html
      }
      response.failure = { resp ->
        return [
            'error': "Could not process Elasticsearch request.",
            'status': resp.statusLine,
            'message': resp.message
        ]
      }
    }
  }

}

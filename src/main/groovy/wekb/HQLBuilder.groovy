package wekb

import wekb.helper.BeanStore
import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.util.GrailsClassUtils
import groovy.util.logging.Slf4j

@Slf4j
public class HQLBuilder {

  /**
   *  Accept a qbetemplate of the form
   *  [
   *		baseclass:'Fully.Qualified.Class.Name.To.Search',
   *		title:'Title Of Search',
   *		qbeConfig:[
   *			// For querying over associations and joins, here we will need to set up scopes to be referenced in the qbeForm config
   *			// Until we need them tho, they are omitted. qbeForm entries with no explicit scope are at the root object.
   *			qbeForm:[
   *				[
   *					prompt:'Name or Title',
   *					qparam:'qp_name',
   *					placeholder:'Name or title of item',
   *					contextTree:['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'name']
   *				],
   *				[
   *					prompt:'ID',
   *					qparam:'qp_id',
   *					placeholder:'ID of item',
   *					contextTree:['ctxtp':'qry', 'comparator' : 'eq', 'prop':'id', 'type' : 'java.lang.Long']
   *				],
   *				[
   *					prompt:'SID',
   *					qparam:'qp_sid',
   *					placeholder:'SID for item',
   *					contextTree:['ctxtp':'qry', 'comparator' : 'eq', 'prop':'ids.value']
   *				],
   *			],
   *			qbeResults:[
   *				[heading:'Type', property:'class.simpleName'],
   *				[heading:'Name/Title', property:'name', link:[controller:'resource',action:'show',id:'x.r.class.name+\':\'+x.r.id'] ]
   *			]
   *		]
   *	]
   *
   *
   */
  public static def build(grailsApplication, 
                          qbetemplate, 
                          params,
                          result, 
                          target_class, 
                          genericOIDService,
                          returnObjectsOrRows='objects') {
    // select o from Clazz as o where 

    // log.debug("build ${params}");

    // Step 1 : Walk through all the properties defined in the template and build a list of criteria
    def criteria = []

    qbetemplate.qbeConfig.qbeForm.each { query_prop_def ->
      if ( ( params[query_prop_def.qparam] != null ) && ( params[query_prop_def.qparam] instanceof String && params[query_prop_def.qparam].length() > 0 ) ) {
        criteria.add([defn:query_prop_def, value:params[query_prop_def.qparam]]);
      }
      if ( ( params[query_prop_def.qparam] != null ) && ( params[query_prop_def.qparam] instanceof ArrayList && params[query_prop_def.qparam].size() > 0 ) ) {
        params[query_prop_def.qparam].each {
          if( ( it != null ) )
          criteria.add([defn:query_prop_def, value:it])
        }
      }
    }

    List availableSortFields = []
    qbetemplate.qbeConfig.qbeResults.each { qbeResult ->
      if(qbeResult.sort){
        availableSortFields << qbeResult.sort
      }
    }

    def hql_builder_context = new java.util.HashMap();
    hql_builder_context.declared_scopes = [:];
    hql_builder_context.query_clauses = []
    hql_builder_context.bindvars = new java.util.HashMap();
    hql_builder_context.genericOIDService = genericOIDService;

    if(params.sort && params.sort in availableSortFields){
      hql_builder_context.sort = params.sort
    }else {
      hql_builder_context.sort = ( qbetemplate.containsKey('defaultSort') ? qbetemplate.defaultSort : null )
    }

    if(params.order && params.order in ['desc', 'asc']){
      hql_builder_context.order = params.order
    }else {
      hql_builder_context.order = ( qbetemplate.containsKey('defaultOrder') ? qbetemplate.defaultOrder : null )
    }



    def baseclass = target_class.getClazz()
    criteria.each { crit ->
      log.debug("Processing crit: ${crit}");
      processProperty(hql_builder_context,crit,baseclass)
      // List props = crit.def..split("\\.")
    }

    // log.debug("At end of build, ${hql_builder_context}");
    hql_builder_context.declared_scopes.each { ds ->
      // log.debug("Scope: ${ds}");
    }

    hql_builder_context.query_clauses.each { qc ->
      // log.debug("QueryClause: ${qc}");
    }

    String hql = outputHqlWithoutSort(hql_builder_context, qbetemplate)
    // log.debug("HQL: ${hql}");
    // log.debug("BindVars: ${hql_builder_context.bindvars}");

    String count_hql = null; //"select count (distinct o) ${hql}"
    if ( qbetemplate.useDistinct == true ) {
      count_hql = "select count (distinct o.id) ${hql}".toString()
    }
    else {
      count_hql = "select count (*) ${hql}".toString()
    }

    def fetch_hql = null
    if ( returnObjectsOrRows=='objects' ) {
      fetch_hql = "select ${qbetemplate.useDistinct == true ? 'distinct' : ''} o ${hql}".toString()
    }
    else {
      fetch_hql = "select ${buildFieldList(qbetemplate.qbeConfig.qbeResults)} ${hql}".toString()
    }

    // Many SQL variants freak out if you order by on a count(*) query, so only order by for the actual fetch
    if ( hql_builder_context.containsKey('sort' ) && 
         ( hql_builder_context.get('sort') != null ) && 
         ( hql_builder_context.get('sort').length() > 0 ) ) {
      log.debug("Setting sort order to ${hql_builder_context.sort}")

      if(hql_builder_context.sort == 'currentTippCount'){
        fetch_hql = fetch_hql.replaceFirst(" o ", " o, (select count(t.id) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_CURRENT.id}) as currentTippCount ")
        fetch_hql += " order by currentTippCount ${hql_builder_context.order}";
      }
      else if(hql_builder_context.sort == 'deletedTippCount'){
        fetch_hql = fetch_hql.replaceFirst(" o ", " o, (select count(t.id) from  org.gokb.cred.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_DELETED.id}) as deletedTippCount ")
        fetch_hql += " order by deletedTippCount ${hql_builder_context.order}";
      }
      else if(hql_builder_context.sort == 'retiredTippCount'){
        fetch_hql = fetch_hql.replaceFirst(" o ", " o, (select count(t.id) from  org.gokb.cred.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_RETIRED.id}) as retiredTippCount ")
        fetch_hql += " order by retiredTippCount ${hql_builder_context.order}";
      }
      else if(hql_builder_context.sort == 'expectedTippCount'){
        fetch_hql = fetch_hql.replaceFirst(" o ", " o, (select count(t.id) from  org.gokb.cred.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_EXPECTED.id}) as expectedTippCount ")
        fetch_hql += " order by expectedTippCount ${hql_builder_context.order}";
      }
      else {
        fetch_hql += " order by o.${hql_builder_context.sort} ${hql_builder_context.order}";
      }
    }


    log.info("Attempt count qry ${count_hql}");
    log.info("Attempt qry ${fetch_hql}");
    log.info("Bindvars ${hql_builder_context.bindvars}");
    def count_start_time = System.currentTimeMillis();
    result.reccount = baseclass.executeQuery(count_hql, hql_builder_context.bindvars,[readOnly:true])[0]

    log.info("Count completed (${result.reccount}) after ${System.currentTimeMillis() - count_start_time} ms");

    def query_params = [:]
    if ( result.max )
      query_params.max = result.max;
    if ( result.offset )
      query_params.offset = result.offset

    query_params.readOnly = true;

    def query_start_time = System.currentTimeMillis();
    // log.debug("Get data rows..");
    result.recset = baseclass.executeQuery(fetch_hql, hql_builder_context.bindvars,query_params);
    // log.debug("Returning..");
    log.info("Fetch completed after ${System.currentTimeMillis() - query_start_time} ms");
  }

  static def processProperty(hql_builder_context,crit,baseclass) {
    // log.debug("processProperty ${hql_builder_context}, ${crit}");
    switch ( crit.defn.contextTree.ctxtp ) {
      case 'qry':
        processQryContextType(hql_builder_context,crit,baseclass)
        break;
      case 'filter':
        processQryContextType(hql_builder_context,crit,baseclass)
        break;
      default:
        log.error("Unhandled property context type ${crit}");
        break;
    }
  }

  static def processQryContextType(hql_builder_context,crit, baseclass) {
    List l =  crit.defn.contextTree.prop.split("\\.")
    processQryContextType(hql_builder_context, crit, l, 'o', baseclass, baseclass)
  }

  static def processQryContextType(hql_builder_context,crit, proppath, parent_scope, the_class, baseclass) {

    // log.debug("processQryContextType.... ${proppath}");

    if ( proppath.size() > 1 ) {
      
      def head = proppath.remove(0)
      def newscope = parent_scope+'_'+head
      if ( hql_builder_context.declared_scopes.containsKey(newscope) ) {
        // Already established scope for this context
        // log.debug("${newscope} already a declared contest");
      }
      else {
        // log.debug("Intermediate establish scope - ${head} :: ${proppath}");
        // We're looking at an intermediate property which needs to add some bind scopes. The property can be a simple 
        // standard association.
        Class target_class = GrailsClassUtils.getPropertyType(the_class, head)
          
          // Standard association, just make a bind variable..
          establishScope(hql_builder_context, parent_scope, head, newscope)
          processQryContextType(hql_builder_context,crit, proppath, newscope, target_class, baseclass)
      }
    }
    else {
      log.debug("head prop...");
      // If this is an ordinary property, add the operation. If it's a special, the make the extra joins
      log.debug("Standard property ${proppath}...");
      // The property is a standard property
      addQueryClauseFor(crit,hql_builder_context,parent_scope+'.'+proppath[0], baseclass)
    }
  }

  static def establishScope(hql_builder_context, parent_scope, property_to_join, newscope_name) {
    // log.debug("Establish scope ${newscope_name} as a child of ${parent_scope} property ${property_to_join}");
    hql_builder_context.declared_scopes[newscope_name] = "${parent_scope}.${property_to_join} as ${newscope_name}" 
  }

  static def addQueryClauseFor(crit, hql_builder_context, scoped_property, baseclass) {
    switch ( crit.defn.contextTree.comparator ) {
      case 'eq':
        hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate?'not ':''}${scoped_property} = :${crit.defn.qparam}");
        if ( crit.defn.type=='lookup' ) {
          def value = hql_builder_context.genericOIDService.resolveOID(crit.value)
          value = (crit.defn.propType == 'Boolean') ? (value == RDStore.YN_YES ? true : false) : value
          hql_builder_context.bindvars[crit.defn.qparam] = value
        }
        else {
          switch ( crit.defn.contextTree.type ) {
            case 'java.lang.Long':
              hql_builder_context.bindvars[crit.defn.qparam] = Long.parseLong(crit.value)
              break;
            case 'java.lang.Object':
              hql_builder_context.bindvars[crit.defn.qparam] = crit.value
              break;
            default:
              hql_builder_context.bindvars[crit.defn.qparam] = crit.value.toString().trim();
              break;
          }
        }
        break;

      case 'ilike':
        hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate?'not ':''} lower(${scoped_property}) like :${crit.defn.qparam}");
        def base_value = crit.value.toLowerCase().trim()
        if ( crit.defn.contextTree.normalise == true ) {
          base_value = TextUtils.norm2(base_value)
        }
        hql_builder_context.bindvars[crit.defn.qparam] = ( ( crit.defn.contextTree.wildcard=='L' || crit.defn.contextTree.wildcard=='B') ? '%' : '') +
                                                         base_value +
                                                         ( ( crit.defn.contextTree.wildcard=='R' || crit.defn.contextTree.wildcard=='B') ? '%' : '')
        break;

      case 'like':
        hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate?'not ':''}${scoped_property} like :${crit.defn.qparam}");
        def base_value = crit.value.trim()
        if ( crit.defn.contextTree.normalise == true ) {
          base_value = TextUtils.norm2(base_value)
        }
        hql_builder_context.bindvars[crit.defn.qparam] = ( ( crit.defn.contextTree.wildcard=='L' || crit.defn.contextTree.wildcard=='B') ? '%' : '') +
                                                         base_value +
                                                         ( ( crit.defn.contextTree.wildcard=='R' || crit.defn.contextTree.wildcard=='B') ? '%' : '')
        break;

      case 'exists':
        if ( crit.defn.type=='lookup') {

          if(crit.defn.baseClass == 'wekb.RefdataValue') {
            def value = hql_builder_context.genericOIDService.resolveOID(crit.value)
            if(value && value.class.getSimpleName() == 'RefdataValue') {
              hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select ${crit.defn.qparam} from ${scoped_property} as ${crit.defn.qparam} where ${crit.defn.qparam} = :${crit.defn.qparam} ) ");
              hql_builder_context.bindvars[crit.defn.qparam] = value
            }
          }
          if(crit.defn.baseClass == 'wekb.CuratoryGroup') {
            def value = CuratoryGroup.get(crit.value)
            if(value) {
              if(baseclass.toString() == 'class wekb.Org') {
                hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate ? 'not ' : ''} exists ( select cgo from CuratoryGroupOrg cgo where cgo.org = o and cgo.curatoryGroup = :curGroup) ");
                hql_builder_context.bindvars.curGroup = value
              }else if(baseclass.toString() == 'class wekb.Platform') {
                hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgp from CuratoryGroupPlatform cgp where cgp.platform = o and cgp.curatoryGroup = :curGroup) ");
                hql_builder_context.bindvars.curGroup = value
              }else if(baseclass.toString() == 'class wekb.Package') {
                hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgp from CuratoryGroupPackage cgp where cgp.pkg = o and cgp.curatoryGroup = :curGroup) ");
                hql_builder_context.bindvars.curGroup = value
              }else if(baseclass.toString() == 'class wekb.User') {
                hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgu from CuratoryGroupUser cgu where cgu.user = o and cgu.curatoryGroup = :curGroup) ");
                hql_builder_context.bindvars.curGroup = value
              }else if(baseclass.toString() == 'class wekb.KbartSource') {
                hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgk from CuratoryGroupKbartSource cgk where cgk.kbartSource = o and cgk.curatoryGroup = :curGroup) ");
                hql_builder_context.bindvars.curGroup = value
              }else if(baseclass.toString() == 'class wekb.TitleInstancePackagePlatform') {
                hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgp from CuratoryGroupPackage cgp where cgp.pkg = o.pkg and cgp.curatoryGroup = :curGroup) ");
                hql_builder_context.bindvars.curGroup = value
              }
            }
          }
        }
        break;

      default:
        log.error("Unhandled comparator '${crit.defn.contextTree.comparator}'. crit: ${crit}");
    }
  }

  static def outputHqlWithoutSort(hql_builder_context, qbetemplate) {
    StringWriter sw = new StringWriter()
    sw.write(" from ${qbetemplate.baseclass} as o\n")

    hql_builder_context.declared_scopes.each { scope_name,ds ->
      sw.write(" join ${ds}\n");
    }
    
    if ( hql_builder_context.query_clauses.size() > 0 ) {
      sw.write(" where");
      boolean conjunction=false
      hql_builder_context.query_clauses.each { qc ->
        if ( conjunction ) {
          // output and on second and subsequent clauses
          sw.write(" AND");
        }
        else {  
          conjunction=true
        }
        sw.write(" ");
        sw.write(qc);
      }
    }

    // if ( ( hql_builder_context.sort != null ) && ( hql_builder_context.sort.length() > 0 ) ) {
    //   sw.write(" order by o.${hql_builder_context.sort} ${hql_builder_context.order}");
    // }

    // Return the toString of the writer
    sw.toString();
  }

  static def buildFieldList(defns) {
    def result = new java.io.StringWriter()
    result.write('o.id');
    result.write(',o.class');
    defns.each { defn ->
      result.write(",o.");
      result.write(defn.property);
    }
    result.toString();
  }

  // If a value begins __ then it's a special value and needs to be interpreted, otherwise just return the value
  static def interpretGlobalValue(grailsApplication,prop) {
    // log.debug("interpretGlobalValue(ctx,${value})");
    def result=null;
    if ( prop.cat && prop.cat.size() > 0) {
      def rdc = RefdataCategory.findByDesc(prop.cat)
      
      if ( rdc ) {
        result = RefdataValue.findByOwnerAndValue(rdc, prop.value)
      }else{
        log.error("Could not resolve RefdataCategory for filtering!")
      }
    }
    else {
      switch(prop.value?.toString()) {
        case '__USERID':
          def springSecurityService = BeanStore.getSpringSecurityService()
          result=''+springSecurityService?.currentUser?.id;
          break;
        default:
          result=prop.value;
          break;
      }
    }
    // log.debug("Returning ${result} ${result.class.name}");
    return result;
  }
}

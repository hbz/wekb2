package wekb

import grails.core.GrailsApplication
import wekb.helper.BeanStore
import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.util.GrailsClassUtils
import groovy.util.logging.Slf4j

import java.text.SimpleDateFormat

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
                          target_class,
                          returnObjectsOrRows='objects') {
    // select o from Clazz as o where 

    // log.debug("build ${params}");

    // Step 1 : Walk through all the properties defined in the template and build a list of criteria
    def criteria = []

    qbetemplate.qbeConfig.qbeForm.each { query_prop_def ->
      if ( ( ( params[query_prop_def.qparam] != null ) && ( params[query_prop_def.qparam] instanceof String && params[query_prop_def.qparam].length() > 0 ) ) || ( params[query_prop_def.qparam] instanceof Boolean ) ) {
        criteria.add([defn:query_prop_def, value:params[query_prop_def.qparam]]);
      }
      if ( ( params[query_prop_def.qparam] != null ) && ( params[query_prop_def.qparam] instanceof ArrayList && params[query_prop_def.qparam].size() > 0 ) ) {
        if(query_prop_def.contextTree.comparator == "in") {
          criteria.add([defn:query_prop_def, value:params[query_prop_def.qparam]])
        }
        else {
          params[query_prop_def.qparam].eachWithIndex { def value, int index ->
            if( ( value != null ) ){
              LinkedHashMap cloneOfQuery_Prop_Def = query_prop_def.clone()
              cloneOfQuery_Prop_Def.qparam = (index > 0) ? cloneOfQuery_Prop_Def.qparam+"_${index}" : cloneOfQuery_Prop_Def.qparam
              criteria.add([defn:cloneOfQuery_Prop_Def, value:value, disjunction: true])
            }

          }
        }
      }
    }

    List availableSortFields = []
    qbetemplate.qbeConfig.qbeResults.each { qbeResult ->
      if(qbeResult.sort){
        availableSortFields << qbeResult.sort
      }
    }

    qbetemplate.qbeConfig.qbeSortFields.each { qbeSortField ->
      if(qbeSortField.sort){
        availableSortFields << qbeSortField.sort
      }
    }

    def hql_builder_context = new java.util.HashMap();
    hql_builder_context.declared_scopes = [:];
    hql_builder_context.query_clausesWithAnd = []
    hql_builder_context.query_clausesWithOr = []
    hql_builder_context.bindvars = new java.util.HashMap();

    if(params.sort && params.sort in availableSortFields){
      hql_builder_context.sort = params.sort
    }else {
      hql_builder_context.sort = ( qbetemplate.containsKey('defaultSort') ? qbetemplate.defaultSort : null )
    }
    String sort = hql_builder_context.sort

    if(params.order && params.order in ['desc', 'asc']){
      hql_builder_context.order = params.order
    }else {
      hql_builder_context.order = ( qbetemplate.containsKey('defaultOrder') ? qbetemplate.defaultOrder : null )
    }
    String order = hql_builder_context.order


    def baseclass = target_class.getClazz()
    criteria.each { crit ->
      log.debug("Processing crit: ${crit}");
      processProperty(hql_builder_context,crit,baseclass, grailsApplication)
      // List props = crit.def..split("\\.")
    }

    // log.debug("At end of build, ${hql_builder_context}");
    hql_builder_context.declared_scopes.each { ds ->
      // log.debug("Scope: ${ds}");
    }

    hql_builder_context.query_clausesWithAnd.each { qc ->
      // log.debug("QueryClause: ${qc}");
    }

    String hql = outputHqlWithoutSort(hql_builder_context, qbetemplate)
    log.debug("HQL: ${hql}");
    log.debug("BindVars: ${hql_builder_context.bindvars}");


    if(qbetemplate.baseclass in ["wekb.CuratoryGroup", "wekb.KbartSource", "wekb.Org", "wekb.Package", "wekb.Platform", "wekb.TitleInstancePackagePlatform"]) {
      if (!hql_builder_context.bindvars.qp_status && !hql_builder_context.bindvars.status) {

        if (hql.contains('where')) {
          hql = hql + " and o.status != ${RDStore.KBC_STATUS_REMOVED.id}"
        } else {
          hql = hql + " where o.status != ${RDStore.KBC_STATUS_REMOVED.id}"
        }

      }
    }

    String count_hql = null; //"select count (distinct o) ${hql}"
    if ( qbetemplate.useDistinct == true ) {
      count_hql = "select count (distinct o.id) ${hql}".toString()
    }
    else {
      count_hql = "select count (*) ${hql}".toString()
    }

    String fetch_hql, count_clause = null
    if ( returnObjectsOrRows=='objects' ) {
      if(qbetemplate.useDistinct == true)
        fetch_hql = "select distinct o ${hql}".toString()
      else
        fetch_hql = "select o.id ${hql}".toString()
    }
    else {
      fetch_hql = "select ${buildFieldList(qbetemplate.qbeConfig.qbeResults)} ${hql}".toString()
    }

    // Many SQL variants freak out if you order by on a count(*) query, so only order by for the actual fetch
    String order_clause = " order by o.${hql_builder_context.sort} ${hql_builder_context.order}"
    if ( hql_builder_context.containsKey('sort' ) && 
         ( hql_builder_context.get('sort') != null ) && 
         ( hql_builder_context.get('sort').length() > 0 ) ) {
      log.debug("Setting sort order to ${hql_builder_context.sort}")
      switch(hql_builder_context.sort) {
        case 'titleCount':
              fetch_hql = fetch_hql.replaceFirst(" o.id ", " o.id, (select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id) as titleCount ")
              count_clause = "(select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id) as titleCount"
              order_clause = " order by titleCount ${hql_builder_context.order}"
              break
        case 'currentTippCount':
              fetch_hql = fetch_hql.replaceFirst(" o.id ", " o.id, (select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_CURRENT.id}) as currentTippCount ")
              count_clause = "(select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_CURRENT.id}) as currentTippCount"
              order_clause = " order by currentTippCount ${hql_builder_context.order}"
              break
        case 'deletedTippCount':
              fetch_hql = fetch_hql.replaceFirst(" o.id ", " o.id, (select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_DELETED.id}) as deletedTippCount ")
              count_clause = "(select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_DELETED.id}) as deletedTippCount"
              order_clause = " order by deletedTippCount ${hql_builder_context.order}"
              break
        case 'retiredTippCount':
              fetch_hql = fetch_hql.replaceFirst(" o.id ", " o.id, (select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_RETIRED.id}) as retiredTippCount ")
              count_clause = "(select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_RETIRED.id}) as retiredTippCount"
              order_clause = " order by retiredTippCount ${hql_builder_context.order}"
              break
        case 'expectedTippCount':
              fetch_hql = fetch_hql.replaceFirst(" o.id ", " o.id, (select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_EXPECTED.id}) as expectedTippCount ")
              count_clause = "(select count(*) from  wekb.TitleInstancePackagePlatform as t where t.pkg = o.id and t.status = ${RDStore.KBC_STATUS_EXPECTED.id}) as expectedTippCount"
              order_clause = " order by expectedTippCount ${hql_builder_context.order}"
              break
          case 'lastTryDate':
              fetch_hql = fetch_hql.replaceFirst(" o.id ", " o.id, (select max(upi.endTime) from wekb.UpdatePackageInfo as upi where upi.endTime is not null and upi.pkg = o.id) as updateSuccessDate ")
              count_clause = "(select max(upi.endTime) from wekb.UpdatePackageInfo as upi where upi.endTime is not null and upi.pkg = o.id) as updateSuccessDate"
              order_clause = " order by updateSuccessDate ${hql_builder_context.order}"
              break
      }
      fetch_hql += order_clause
    }

      return [count_hql: count_hql, fetch_hql: fetch_hql, count_clause: count_clause, params_hql: hql_builder_context.bindvars, sort: sort, order: order, order_clause: order_clause]
  }

  static def processProperty(hql_builder_context,crit,baseclass, grailsApplication) {
    // log.debug("processProperty ${hql_builder_context}, ${crit}");
    switch ( crit.defn.contextTree.ctxtp ) {
      case 'qry':
        processQryContextType(hql_builder_context,crit,baseclass, grailsApplication)
        break;
      case 'filter':
        processQryContextType(hql_builder_context,crit,baseclass, grailsApplication)
        break;
      default:
        log.error("Unhandled property context type ${crit}");
        break;
    }
  }

  static def processQryContextType(hql_builder_context,crit, baseclass, grailsApplication) {
    List l = []
    //no extra join when just the foreign key is being requested
    if(crit.defn.contextTree.prop.contains('.id'))
      l << crit.defn.contextTree.prop
    else
      l.addAll(crit.defn.contextTree.prop.split("\\."))
    processQryContextType(hql_builder_context, crit, l, 'o', baseclass, baseclass, grailsApplication)
  }

  static def processQryContextType(hql_builder_context,crit, proppath, parent_scope, the_class, baseclass, grailsApplication) {

    // log.debug("processQryContextType.... ${proppath}");

    if ( proppath.size() > 1 ) {
      
      def head = proppath.remove(0)
      def newscope = parent_scope+'_'+head
      if ( hql_builder_context.declared_scopes.containsKey(newscope) ) {
        // Already established scope for this context
        log.debug("${newscope} already a declared contest");
          Class target_class = GrailsClassUtils.getPropertyType(the_class, head)
          processQryContextType(hql_builder_context,crit, proppath, newscope, target_class, baseclass, grailsApplication)
      }
      else {
        // log.debug("Intermediate establish scope - ${head} :: ${proppath}");
        // We're looking at an intermediate property which needs to add some bind scopes. The property can be a simple 
        // standard association.
        Class target_class = GrailsClassUtils.getPropertyType(the_class, head)
          
          // Standard association, just make a bind variable..
          establishScope(hql_builder_context, parent_scope, head, newscope)
          processQryContextType(hql_builder_context,crit, proppath, newscope, target_class, baseclass, grailsApplication)
      }
    }
    else {
      log.debug("head prop...");
      // If this is an ordinary property, add the operation. If it's a special, the make the extra joins
      log.debug("Standard property ${proppath}...");
      // The property is a standard property
      addQueryClauseFor(crit,hql_builder_context,parent_scope+'.'+proppath[0], baseclass, grailsApplication)
    }
  }

  static def establishScope(hql_builder_context, parent_scope, property_to_join, newscope_name) {
    // log.debug("Establish scope ${newscope_name} as a child of ${parent_scope} property ${property_to_join}");
    hql_builder_context.declared_scopes[newscope_name] = "${parent_scope}.${property_to_join} as ${newscope_name}" 
  }

  static def addQueryClauseFor(crit, hql_builder_context, scoped_property, baseclass, grailsApplication) {
    String addToQuery = (crit.disjunction ? 'query_clausesWithOr' : 'query_clausesWithAnd')


    switch ( crit.defn.contextTree.comparator ) {
      case 'eq':
        hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate?'not ':''}${scoped_property} = :${crit.defn.qparam}");
        if ( crit.defn.type=='lookup' || crit.defn.type=='dropDown' || crit.defn.type=='dropDownGroup' ) {
          def value =  getValueFromBaseClass(grailsApplication, crit.defn.baseClass, crit.value)
          value = (crit.defn.propType == 'Boolean') ? (value == RDStore.YN_YES ? true : false) : value
          hql_builder_context.bindvars[crit.defn.qparam] = value
        }
        else {
          switch ( crit.defn.contextTree.type ) {
            case 'java.lang.Long':
              hql_builder_context.bindvars[crit.defn.qparam] = crit.value instanceof String ? Long.parseLong(crit.value) : crit.value
              break;
            case ['java.lang.Object', 'boolean']:
              hql_builder_context.bindvars[crit.defn.qparam] = crit.value
              break;
            default:
              hql_builder_context.bindvars[crit.defn.qparam] = crit.value.toString().trim();
              break;
          }
        }
        break;
      case 'in':
        hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate?'not ':''}${scoped_property} in (:${crit.defn.qparam})")
        if ( crit.defn.type=='lookup' || crit.defn.type=='dropDown' || crit.defn.type=='dropDownGroup' ) {
          List values = [], parsedValues = []
          if(crit.value instanceof String)
            values << crit.value
          else values.addAll(crit.value)
          values.each { String rawVal ->
            def value = getValueFromBaseClass(grailsApplication, crit.defn.baseClass, rawVal)
            value = (crit.defn.propType == 'Boolean') ? (value == RDStore.YN_YES ? true : false) : value
            parsedValues << value
          }
          hql_builder_context.bindvars[crit.defn.qparam] = parsedValues
        }else if (crit.defn.type=='dropDownMultiple'){
            List values = [], parsedValues = []
            if(crit.value instanceof String)
                values = crit.value.split(',')
            else
                values.addAll(crit.value)

            values.each { String rawVal ->
                def value = getValueFromBaseClass(grailsApplication, crit.defn.baseClass, rawVal)
                value = (crit.defn.propType == 'Boolean') ? (value == RDStore.YN_YES ? true : false) : value
                parsedValues << value
            }

            hql_builder_context.bindvars[crit.defn.qparam] = parsedValues
        }
        else {
          hql_builder_context.bindvars[crit.defn.qparam] = crit.value
        }
        break

      case 'ilike':
        hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate?'not ':''} lower(${scoped_property}) like :${crit.defn.qparam}");
        def base_value = crit.value.toLowerCase().trim()
        if ( crit.defn.contextTree.normalise == true ) {
          base_value = TextUtils.norm2(base_value)
        }
        hql_builder_context.bindvars[crit.defn.qparam] = ( ( crit.defn.contextTree.wildcard=='L' || crit.defn.contextTree.wildcard=='B') ? '%' : '') +
                                                         base_value +
                                                         ( ( crit.defn.contextTree.wildcard=='R' || crit.defn.contextTree.wildcard=='B') ? '%' : '')
        break;

      case 'like':
        hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate?'not ':''}${scoped_property} like :${crit.defn.qparam}");
        def base_value = crit.value.trim()
        if ( crit.defn.contextTree.normalise == true ) {
          base_value = TextUtils.norm2(base_value)
        }
        hql_builder_context.bindvars[crit.defn.qparam] = ( ( crit.defn.contextTree.wildcard=='L' || crit.defn.contextTree.wildcard=='B') ? '%' : '') +
                                                         base_value +
                                                         ( ( crit.defn.contextTree.wildcard=='R' || crit.defn.contextTree.wildcard=='B') ? '%' : '')
        break;

      case 'exists':
        if ( crit.defn.type=='lookup' || crit.defn.type=='dropDown' || crit.defn.type=='dropDownGroup' ) {

          if(crit.defn.baseClass == 'wekb.RefdataValue') {
            def value = getValueFromBaseClass(grailsApplication, crit.defn.baseClass, crit.value)
            if(value && value.class.getSimpleName() == 'RefdataValue') {
              hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select ${crit.defn.qparam} from ${scoped_property} as ${crit.defn.qparam} where ${crit.defn.qparam} = :${crit.defn.qparam} ) ");
              hql_builder_context.bindvars[crit.defn.qparam] = value
            }
          }
          if(crit.defn.baseClass == 'wekb.CuratoryGroup') {
            def value = CuratoryGroup.get(crit.value)
            if(value) {
              if(baseclass.toString() == 'class wekb.Org') {
                hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} exists ( select cgo from CuratoryGroupOrg cgo where cgo.org = o and cgo.curatoryGroup = :${crit.defn.qparam}) ");
                hql_builder_context.bindvars[crit.defn.qparam] = value
              }else if(baseclass.toString() == 'class wekb.Platform') {
                hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgp from CuratoryGroupPlatform cgp where cgp.platform = o and cgp.curatoryGroup = :${crit.defn.qparam}) ");
                hql_builder_context.bindvars[crit.defn.qparam] = value
              }else if(baseclass.toString() == 'class wekb.Package') {
                hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgp from CuratoryGroupPackage cgp where cgp.pkg = o and cgp.curatoryGroup = :${crit.defn.qparam}) ");
                hql_builder_context.bindvars[crit.defn.qparam] = value
              }else if(baseclass.toString() == 'class wekb.User') {
                hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgu from CuratoryGroupUser cgu where cgu.user = o and cgu.curatoryGroup = :${crit.defn.qparam}) ");
                hql_builder_context.bindvars[crit.defn.qparam] = value
              }else if(baseclass.toString() == 'class wekb.KbartSource') {
                hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgk from CuratoryGroupKbartSource cgk where cgk.kbartSource = o and cgk.curatoryGroup = :${crit.defn.qparam}) ");
                hql_builder_context.bindvars[crit.defn.qparam] = value
              }else if(baseclass.toString() == 'class wekb.TitleInstancePackagePlatform') {
                hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgp from CuratoryGroupPackage cgp where cgp.pkg = o.pkg and cgp.curatoryGroup = :${crit.defn.qparam}) ");
                hql_builder_context.bindvars[crit.defn.qparam] = value
              }else if(baseclass.toString() == 'class wekb.UpdatePackageInfo') {
                hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgp from CuratoryGroupPackage cgp where cgp.pkg = o.pkg and cgp.curatoryGroup = :${crit.defn.qparam}) ");
                hql_builder_context.bindvars[crit.defn.qparam] = value
              }else if(baseclass.toString() == 'class wekb.Vendor') {
                hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} exists (select cgk from CuratoryGroupVendor cgk where cgk.vendor = o and cgk.curatoryGroup = :${crit.defn.qparam}) ");
                hql_builder_context.bindvars[crit.defn.qparam] = value
              }
            }
          }
          if(crit.defn.baseClass == 'wekb.Vendor') {
            def value = Vendor.get(crit.value)
            if(value) {
              if(baseclass.toString() == 'class wekb.Org') {
                hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate ? 'not ' : ''} o in (select p.provider from Package as p join p.vendors as vendor_pkg where vendor_pkg.vendor = :${crit.defn.qparam}) ");
                hql_builder_context.bindvars[crit.defn.qparam] = value
              }
            }
          }
        }
        break;

      case 'eqYear':
        hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate?'not ':''}YEAR(${scoped_property}) = :${crit.defn.qparam}");
        int base_value = Integer.parseInt(crit.value)
        hql_builder_context.bindvars[crit.defn.qparam] = base_value
        break;

      case 'greater':
        hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate?'not ':''} ${scoped_property} >= :${crit.defn.qparam}");
        switch ( crit.defn.contextTree.type ) {
          case 'java.util.Date':
            hql_builder_context.bindvars[crit.defn.qparam] = parseDate(crit.value.toString(), [new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd")])
            break;
          default:
            hql_builder_context.bindvars[crit.defn.qparam] = crit.value.toString().trim();
            break;
        }
        break;

      case 'smaller':
        hql_builder_context."${addToQuery}".add("${crit.defn.contextTree.negate?'not ':''} ${scoped_property} <= :${crit.defn.qparam}");
        switch ( crit.defn.contextTree.type ) {
          case 'java.util.Date':
            hql_builder_context.bindvars[crit.defn.qparam] = parseDate(crit.value.toString(), [new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd")])
            break;
          default:
            hql_builder_context.bindvars[crit.defn.qparam] = crit.value.toString().trim();
            break;
        }
        break;

      case 'ilike_Combine_Name_And_VariantNames':
        String query = " (lower(o.name) like :${crit.defn.qparam} OR exists (select cvn from ComponentVariantName as cvn where lower(cvn.variantName) like :${crit.defn.qparam} AND (cvn.org = o OR cvn.pkg = o )))"
        def base_value = crit.value.toLowerCase().trim()
        hql_builder_context."${addToQuery}".add(query);
        hql_builder_context.bindvars[crit.defn.qparam] = ( ( crit.defn.contextTree.wildcard=='L' || crit.defn.contextTree.wildcard=='B') ? '%' : '') +
                base_value +
                ( ( crit.defn.contextTree.wildcard=='R' || crit.defn.contextTree.wildcard=='B') ? '%' : '')
        break;

      case 'ilike_Combine_Name_And_VariantNames_And_AbbreviatedName_Org':
        String query = " (lower(o.name) like :${crit.defn.qparam} OR lower(o.abbreviatedName) like :${crit.defn.qparam} OR exists (select cvn from ComponentVariantName as cvn where lower(cvn.variantName) like :${crit.defn.qparam} AND (cvn.org = o)))"
        def base_value = crit.value.toLowerCase().trim()
        hql_builder_context."${addToQuery}".add(query);
        hql_builder_context.bindvars[crit.defn.qparam] = ( ( crit.defn.contextTree.wildcard=='L' || crit.defn.contextTree.wildcard=='B') ? '%' : '') +
                base_value +
                ( ( crit.defn.contextTree.wildcard=='R' || crit.defn.contextTree.wildcard=='B') ? '%' : '')
        break;

      case 'ilike_Combine_Name_And_VariantNames_And_AbbreviatedName_Provider_Pkg':
        String query = " (lower(o.provider.name) like :${crit.defn.qparam} OR lower(o.provider.abbreviatedName) like :${crit.defn.qparam} OR exists (select cvn from ComponentVariantName as cvn where lower(cvn.variantName) like :${crit.defn.qparam} AND (cvn.org = o.provider)))"
        def base_value = crit.value.toLowerCase().trim()
        hql_builder_context."${addToQuery}".add(query);
        hql_builder_context.bindvars[crit.defn.qparam] = ( ( crit.defn.contextTree.wildcard=='L' || crit.defn.contextTree.wildcard=='B') ? '%' : '') +
                base_value +
                ( ( crit.defn.contextTree.wildcard=='R' || crit.defn.contextTree.wildcard=='B') ? '%' : '')
        break;
      case 'ilike_Combine_Name_And_Anbieter_Produkt_ID_And_ZDB':
        String query = " (lower(o.name) like :${crit.defn.qparam} OR exists (select i from Identifier i where lower(i.value) like :${crit.defn.qparam} AND i.namespace.value = 'Anbieter_Produkt_ID' AND i.pkg = o)" +
                " OR exists (select i from Identifier i where lower(i.value) like :${crit.defn.qparam} AND i.namespace.value = 'zdb' AND i.pkg = o))"
        def base_value = crit.value.toLowerCase().trim()
        hql_builder_context."${addToQuery}".add(query);
        hql_builder_context.bindvars[crit.defn.qparam] = ( ( crit.defn.contextTree.wildcard=='L' || crit.defn.contextTree.wildcard=='B') ? '%' : '') +
                base_value +
                ( ( crit.defn.contextTree.wildcard=='R' || crit.defn.contextTree.wildcard=='B') ? '%' : '')
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

    if ( hql_builder_context.query_clausesWithAnd.size() > 0 || hql_builder_context.query_clausesWithOr.size() > 0) {
      sw.write(" where");
    }

    boolean conjunction=false
    if ( hql_builder_context.query_clausesWithOr.size() > 0 ) {
      sw.write(" ( ");
      hql_builder_context.query_clausesWithOr.each { qc ->
        if ( conjunction ) {
          // output and on second and subsequent clauses
          sw.write(" OR");
        }
        else {
          conjunction=true
        }
        sw.write(" ");
        sw.write(qc);
      }
      sw.write(" ) ");
    }

    if ( hql_builder_context.query_clausesWithAnd.size() > 0 ) {
      hql_builder_context.query_clausesWithAnd.each { qc ->
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
    //result.write(',o.class');
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

  private static Date parseDate(String dateString, def dateFormats){
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

    static def getValueFromBaseClass(GrailsApplication grailsApplication, String baseClass, String oid){
        def value
        def domain_class = grailsApplication.getArtefact('Domain', baseClass)
        if (domain_class) {
            Long objectID
            if(oid.contains(':')){
                List oid_components = oid.split(':')
                objectID = Long.parseLong(oid_components[1])
            }
            else {
                objectID = Long.parseLong(oid)
            }

            value = domain_class.getClazz().get(objectID)
        }
        else {
            log.debug("getValueFromBaseClass -> Unable to locate domain class ${baseClass} or not readable");
            value = null
        }
        return value
    }

}

package wekb

import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.plugins.orm.auditable.AuditEventType
import grails.plugins.orm.auditable.Auditable
import grails.util.GrailsNameUtils
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import wekb.KBComponentLanguage
import wekb.auth.User

import javax.persistence.Transient

/**
 * Abstract base class for Components.
 */

@Slf4j
@grails.gorm.dirty.checking.DirtyCheck
abstract class KBComponent implements Auditable{

  static final String STATUS_CURRENT = "Current"
  static final String STATUS_DELETED = "Deleted"
  static final String STATUS_EXPECTED = "Expected"
  static final String STATUS_RETIRED = "Retired"


  static final String CURRENT_PRICE_HQL = '''
    select cp
    from ComponentPrice as cp
    where cp.owner = :c
      and cp.priceType.value = :t
      and ( ( startDate is null OR startDate <= :d ) and ( endDate is null OR endDate > :d ) )
  '''

  private static refdataDefaults = [
      "status"    : STATUS_CURRENT
  ]

  private static final Map fullDefaultsForClass = [:]

  @Override
  Collection<AuditEventType> getLogIgnoreEvents(){
    [AuditEventType.INSERT, AuditEventType.DELETE]
  }

  @Override
  Collection<String> getLogExcluded(){
    [
        'version',
        'lastUpdated',
        'lastUpdatedBy',
        'bucketHash',
        'componentHash',
        'componentDiscriminator',
        'normname',
        'lastSeen',
        'shortcode',
        'systemComponent',
        'insertBenchmark',
        'componentHash',
        'incomingCombos',
        'outgoingCombos'
    ]
  }

  String getLogEntityId(){
    "${this.class.name}:${id}"
  }

  @Transient
  def springSecurityService

  @Transient
  def accessService

  @Transient
  protected grails.core.GrailsApplication grailsApplication

  /*@Transient
  public setSpringSecurityService(sss){
    this.springSecurityService = sss
  }*/

  @Transient
  setGrailsApplication(ga){
    this.grailsApplication = ga
  }



  @Transient
  private ensureDefaults(){
    try{
      // Metaclass
      ExpandoMetaClass metaclass_of_this_component = this.metaClass
      // First get or build up the full static map of defaults
      final Class rootClass = metaclass_of_this_component.getTheClass()
      Map defaultsForThis = fullDefaultsForClass.get(rootClass.getName())
      if (defaultsForThis == null){
        defaultsForThis = [:]
        // Default to the root.
        Class theClass = rootClass
        // Try and get the map.
        Map classMap
        while (theClass){
          try{
            // Read the classMap
            classMap = metaclass_of_this_component.getProperty(
                rootClass,
                theClass,
                "refdataDefaults",
                false,
                true
            )
          }
          catch (MissingPropertyException e){
            // Catch the error and just set to null.
            // log.error("MissingPropertyExceptiono - clearing out classMap",e)
            classMap = null
          }
          // If we have values then add.
          if (classMap){
            // Add using the class simple name.
            defaultsForThis[theClass.getSimpleName()] = classMap
          }
          // Get the superclass.
          theClass = theClass.getSuperclass()
        }
        // Once we have added each map to our map add to the global map.
        fullDefaultsForClass[rootClass.getName()] = defaultsForThis
      }
      // Check we have some defaults.
      if (defaultsForThis){
        // Create a pointer to this so that we can access within the closures below.
        KBComponent thisComponent = this
        // DomainClassArtefactHandler for this class
        PersistentEntity dClass = grailsApplication.mappingContext.getPersistentEntity(thisComponent.class.name)
        defaultsForThis.each{ String className, defaults ->
          // Add each property and value to the properties in the defaults.
          defaults.each{ String property, values ->
            def lone_property = property
            def rdc = null
            if (lone_property.contains('.')){
              def split_prop = lone_property.split("\\.")
              rdc = split_prop[0]
              lone_property = split_prop[1]
            }
            if (lone_property?.length() > 0 && thisComponent."${lone_property}" == null){
              // Get the type defined against the class.
              PersistentProperty propertyDef = dClass.getPropertyByName(lone_property)
              String propType = propertyDef?.getType()?.getName()
              if (propType){
                log.debug("Setting prop ${propType} ${lone_property}")
                switch (propType){
                  case RefdataValue.class.getName():
                    final String ucProp = GrailsNameUtils.getClassName(lone_property)
                    final String key = "${rdc ?: className}.${ucProp}"
                    log.debug("Final config: ${key}:${values}")
                    if (values instanceof Collection){
                      values.each{ val ->
                        def v = RefdataCategory.lookupOrCreate(key, val)
                        // log.debug("lookupOrCreate-1(${key},${val}) - ${v.id}")
                        thisComponent."addTo${ucProp}"(v)
                      }
                    }
                    else{
                      // Set the default.
                      def v = RefdataCategory.lookupOrCreate(key, values)
                      // log.debug("lookupOrCreate-2(${key},${values}) - ${v.id}")
                      thisComponent."${lone_property}" = v
                    }
                    break
                  default:
                    // Just treat as a normal prop
                    thisComponent."${lone_property}" = values
                    break
                }
              }
              else{
                log.debug("Could not find property ${lone_property} for class ${dClass.getName()}")
              }
            }
          }
        }
      }
    }
    catch (Exception e){
      log.error("Problem initializing defaults", e)
    }
  }


  /**
   * UUID
   */
  String uuid

  /**
   * Generic name for the component. For packages, package name, for journals the journal title. Try to follow DC-Title style naming
   * conventions when trying to decide what to map to this property in a subclass. The name should be a string that reasonably identifies this
   * object when placed in a list of other components.
   */
  String name

  /**
   * The normalised name of this component. Lower-case, strip diacritics
   */
  String normname

  /**
   * A URI style shortcode for the component referenced. Used to create unique but human readable URIs for this item.
   */
  String shortcode

  /**
   * A description (DC.description)
   */
  String description

  /**
   * Component Status. Linked to refdata table.
   */
  RefdataValue status

  /**
   *  Provenance
   */
  String provenance

  /**
   * Reference
   */
  String reference

  /**
   * Last updated by
   * TODO: remove
   */
  User lastUpdatedBy

  /**
   * The source for the record (Whatever it is)
   */
  Source source

  /**
   * Component languages. Linked to refdata table. Only applicable for TitleInstance and TitleInstancePackagePlatform.
   */
  Set languages
  String lastUpdateComment

  List additionalProperties = []
  Set outgoingCombos = []
  Set incomingCombos = []
  Set variantNames = []

  // Timestamps
  Date dateCreated
  Date lastUpdated

  Long lastSeen
  Long insertBenchmark
  Long updateBenchmark

  // Read only flag should be honoured in the UI
  boolean systemComponent = false


  // MD5 Hash of the comparable title for the component. This hash is used to group
  // candidate duplicate works together. It is the means by which we group possible duplicates
  // for more meaningful comparisons. As such, it needs to be coarse and as widely encompassing
  // as possible.
  String bucketHash

  // MD5 Hash specific to class of component that is used for deduplication - EG Title + Edition == Instance level hashing
  String componentHash

  // A discriminator which can be added to the hash above to explicitly
  // discriminate items who's hash would otherwise be (Correctly) the same.
  String componentDiscriminator



  static mappedBy = [
      outgoingCombos      : 'fromComponent',
      incomingCombos      : 'toComponent',
      additionalProperties: 'fromComponent',
      variantNames        : 'owner',
      prices              : 'owner'
  ]


  static hasMany = [
      outgoingCombos      : Combo,
      incomingCombos      : Combo,
      additionalProperties: KBComponentAdditionalProperty,
      variantNames        : KBComponentVariantName,
      prices              : ComponentPrice,
      languages           : KBComponentLanguage
  ]


  static mapping = {
    tablePerHierarchy false
    id column: 'kbc_id'
    uuid column: 'kbc_uuid', type: 'text', index: 'kbc_uuid_idx'
    version column: 'kbc_version'
    name column: 'kbc_name', type: 'text', index: 'kbc_name_idx'
    // Removed auto creation of norm_id_value_idx from here and identifier - MANUALLY CREATE
    // create index norm_id_value_idx on kbcomponent(kbc_normname(64),id_namespace_fk)
    normname column: 'kbc_normname', type: 'text', index: 'kbc_normname_idx'
    description column: 'kbc_description', type: 'text'
    source column: 'kbc_source_fk'
    status column: 'kbc_status_rv_fk', index: 'kbc_status_idx'
    shortcode column: 'kbc_shortcode', index: 'kbc_shortcode_idx'
    dateCreated column: 'kbc_date_created', index: 'kbc_date_created_idx'
    lastUpdated column: 'kbc_last_updated', index: 'kbc_last_updated_idx'
    lastSeen column: 'kbc_last_seen'
    insertBenchmark column: 'kbc_insert_benchmark'
    updateBenchmark column: 'kbc_update_benchmark'
    lastUpdateComment column: 'kbc_last_update_comment'
    componentHash column: 'kbc_component_hash', index: 'kbc_component_hash_idx'
    bucketHash column: 'kbc_bucket_hash', index: 'kbc_bucket_hash_idx'
    componentDiscriminator column: 'kbc_component_descriminator'
    incomingCombos batchSize: 10
    outgoingCombos batchSize: 10
    variantNames cascade: "all,delete-orphan", lazy: false
    //dateCreatedYearMonth formula: "DATE_FORMAT(kbc_date_created, '%Y-%m')"
    //lastUpdatedYearMonth formula: "DATE_FORMAT(kbc_last_updated, '%Y-%m')"
  }


  static constraints = {
    uuid(nullable: true, unique: true, blank: false, maxSize: 2048)
    name(nullable: true, blank: false, maxSize: 2048)
    shortcode(nullable: true, blank: false, maxSize: 128)
    description(nullable: true, blank: false)
    normname(nullable: true, blank: false, maxSize: 2048)
    status(nullable: true, blank: false)
    source(nullable: true, blank: false)
    lastSeen(nullable: true, blank: false)
    lastUpdateComment(nullable: true, blank: false)
    insertBenchmark(nullable: true, blank: false)
    updateBenchmark(nullable: true, blank: false)
    bucketHash(nullable: true, blank: false)
    componentDiscriminator(nullable: true, blank: false)
    componentHash(nullable: true, blank: false)
  }


  /**
   * Defined parameter-less method to allow for overrides in classes, wishing to define
   * their own way of generating a shortcode.
   * @return
   */
  protected def generateShortcode(){
    if (!shortcode && name){
      // Generate the short code.
      shortcode = generateShortcode(name)
    }
  }


  protected def generateUuid(){
    if (!uuid){
      uuid = UUID.randomUUID().toString()
    }
  }


  static def generateShortcode(String text){
    def candidate = text.trim().replaceAll(" ", "_")

    if (candidate.length() > 100){
      candidate = candidate.substring(0, 100)
    }

    return incUntilUnique(candidate)
  }


  static def incUntilUnique(name){
    def result = name
    def l = KBComponent.executeQuery('select id from KBComponent where shortcode = :n', [n: name])
    // if ( KBComponent.findWhere([shortcode : (name)]) ) {
    if (l.size() > 0){
      // There is already a shortcode for that identfier
      int i = 2
     // while ( KBComponent.findWhere([shortcode : "${name}_${i}"]) ) {
      while (KBComponent.executeQuery('select id from KBComponent where shortcode = :n', [n: "${name}_${i}"]).size() > 0){
        i++
      }
      result = "${name}_${i}"
    }
    result
  }

  @Transient
  static KBComponent lookupByIO(String idtype, String idvalue){
    // println("lookupByIO(${idtype},${idvalue})")
    // Component(ids) -> (fromComponent) Combo (toComponent) -> (identifiedComponents) Identifier
    def result = null
    def crit = KBComponent.createCriteria()
    def db_results = crit.list{
      createAlias('outgoingCombos', 'ogc')
      createAlias('ogc.type', 'ogcType')
      createAlias('ogcType.owner', 'ogcOwner')
      createAlias('ogc.toComponent', 'tc')
      createAlias('tc.namespace', 'tcNamespace')
      and{
        eq 'ogcOwner.desc', RCConstants.COMBO_TYPE
        eq 'ogcType.value', 'KBComponent.Ids'
        eq 'tc.value', idvalue
        eq 'tcNamespace.value', idtype
      }
      projections{
        distinct 'id'
      }
    }
    switch (db_results.size()){
      case 1:
        result = KBComponent.get(db_results[0])
        break
      // case {it > 1} :
      //   // Error. Should only match 1...
      //   break
    }
    result
  }



  /**
   *  refdataFind generic pattern needed by inplace edit taglib to provide reference data to typedowns and other UI components.
   *  objects implementing this method can be easily located and listed / selected
   */
  static def refdataFind(params){
    def result = []
    def status_deleted = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, KBComponent.STATUS_DELETED)
    def ql = null

    params.sort = 'name'

    ql = Class.forName(params.baseClass).findAllByNameIlikeAndStatusNotEqual("${params.q}%", status_deleted, params)
    //    ql = KBComponent.findAllByNameIlike("${params.q}%",params)
    if (ql){
      ql.each{ t ->
        if (!params.filter1 || t.status?.value == params.filter1){
          result.add([id: "${t.class.name}:${t.id}", text: "${t.name}", status: "${t.status?.value}"])
        }
      }
    }
    result
  }


  /** Added here so that everyone who wants a normalised component name can
   call this function, then we have a single place to call or change to pivot the norm rules */
  static def generateNormname(str_to_norm){
    def r = GOKbTextUtils.norm2(str_to_norm)
    if (r.length() == 0){
      r = null
    }
    return r
  }


  protected def generateNormname(){
    log.debug("checking for normname")
    this.normname = generateNormname(name)
  }


  protected def generateComponentHash(){
    // Default component hash generation -- Override in subclasses
    // To try and find instances
    this.componentHash = GOKbTextUtils.generateComponentHash([normname, componentDiscriminator])
    // To find works
    this.bucketHash = GOKbTextUtils.generateComponentHash([normname])
  }


  def beforeInsert(){
    // Generate any necessary values.
    generateShortcode()
    generateNormname()
    //generateComponentHash()
    generateUuid()
    // Ensure any defaults defined get set.
    ensureDefaults()
  }


  def afterInsert(){

  }


  def afterUpdate(){
  }


  def afterDelete(){
  }


  def beforeUpdate(){
    log.debug("beforeUpdate for ${this}")
    if (name){
      if (!shortcode){
        this.shortcode = generateShortcode(name)
      }
      generateNormname()
      //generateComponentHash()
    }
    if (!uuid){
      generateUuid()
    }
    def user = springSecurityService?.currentUser
    if (user != null){
      this.lastUpdatedBy = user
    }
  }


  @Transient
  List getOtherIncomingCombos(){
    def combs = null
    // Only run this query id this is not a transient object. This must have an ID for this method to work
    if (this.id){
      Set comboPropTypes = getAllComboTypeValuesFor(this.getClass())
      combs = Combo.createCriteria().list{
        and{
          eq("toComponent", this)
          type{
            and{
              owner{
                eq("desc", RCConstants.COMBO_TYPE)
              }
              not{ 'in'("value", comboPropTypes) }
            }

          }
        }
      }
    }
    else{
      combs = []
    }
    combs
  }


  @Transient
  List getOtherOutgoingCombos(){
    def combs = null
    if (this.id != null){
      Set comboPropTypes = getAllComboTypeValuesFor(this.getClass())
      combs = Combo.createCriteria().list{
        and{
          eq("fromComponent", this)
          type{
            and{
              owner{
                eq("desc", RCConstants.COMBO_TYPE)
              }
              not{ 'in'("value", comboPropTypes) }
            }
          }
        }
      }
    }
    else{
      combs = null
    }
    combs
  }


  void deleteSoft(context){
    // Set the status to deleted.
    setStatus(RDStore.KBC_STATUS_DELETED)
    save(flush: true, failOnError: true)
  }


  void retire(def context = null){
    log.debug("KBComponent::retire")
    // Set the status to retired.
    setStatus(RDStore.KBC_STATUS_RETIRED)
    save(flush: true, failOnError: true)
  }


  void setActive(context){
    setStatus(RDStore.KBC_STATUS_CURRENT)
    save(flush: true, failOnError: true)
  }


  void setExpected(context){
    setStatus(RDStore.KBC_STATUS_EXPECTED)
    save(flush: true, failOnError: true)
  }


  @Transient
  boolean isRetired(){
    return (getStatus() == RDStore.KBC_STATUS_RETIRED)
  }


  @Transient
  boolean isDeleted(){
    return (getStatus() == RDStore.KBC_STATUS_DELETED)
  }


  @Transient
  boolean isCurrent(){
    return (getStatus() == RDStore.KBC_STATUS_CURRENT)
  }


  @Transient
  boolean isExpected(){
    return (getStatus() == RDStore.KBC_STATUS_EXPECTED)
  }


  /**
   *  Return the combos pertaining to a specific property (Rather than the components linked).
   *  Needed for editing start/end dates. Initially on publisher, but probably on other things too later on.
   */
  @Transient
  List<Combo> getCombosByPropertyName(propertyName){
    return getCombosByPropertyNameAndStatus(propertyName, null)
  }


  @Transient
  List<Combo> getCombosByPropertyNameAndStatus(propertyName, status){
    // log.debug("KBComponent::getCombosByPropertyNameAndStatus::${propertyName}|${status}")
    def combos
    def status_ref
    def hql_query
    def hql_params = []
    if (this.getId() != null){
      // Unsaved components can't have combo relations
      RefdataValue type = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, getComboTypeValue(propertyName))
      if (status && status != "null") status_ref = RefdataCategory.lookupOrCreate(RCConstants.COMBO_STATUS, status)
      hql_query = "from Combo where type=? "
      hql_params += type
      if (isComboReverse(propertyName)){
        hql_query += " and toComponent=?"
        hql_params += this
      }
      else{
        hql_query += " and fromComponent=?"
        hql_params += this
      }
      if (status_ref){
        hql_query += " and status=?"
        hql_params += status_ref
      }
      combos = Combo.executeQuery(hql_query, hql_params)
      //       log.debug("Qry: ${hql_query}, Params:${hql_params} : result.size=${combos?.size()}")
    }
    else{
      log.debug("This.id == null")
    }
    return combos
  }


  @Transient
  String getDerivedName(){
    return name
  }


  @Override
  boolean equals(Object obj){
    Object o = KBComponent.deproxy(obj)
    if (o != null){
      // Deproxy the object first to ensure it isn't a hibernate proxy.
      boolean r = (this.getClassName() == o.getClass().name) && (this.getId() == o.getId())
      return r
    }

    // Return false if we get here.
    false
  }


  void appendToAdditionalProperty(String prop_name, String val){
    // Only need to add if a name and value has been supplied.
    if (prop_name && val){
      // Find the KBComponentAdditionalProperty
      KBComponentAdditionalProperty prop = additionalProperties.find{ KBComponentAdditionalProperty prop ->
        prop.getPropertyDefn().getPropertyName() == prop_name &&
            prop.getApValue()?.equalsIgnoreCase(val)
      }
      // Only add a prop if we haven't already got one matching the value and definition.
      if (!prop){
        // Add a new property.
        def prop_defn = AdditionalPropertyDefinition.findByPropertyName(prop_name) ?: new AdditionalPropertyDefinition(propertyName: prop_name).save(failOnError: true)
        // Add to the additional properties.
        addToAdditionalProperties(
            new KBComponentAdditionalProperty(
                propertyDefn: prop_defn,
                apValue: val
            )
        )
      }
    }
  }


  String toString(){
    //"${name ?: ''} (${getNiceName()} ${this.id})".toString()
    "${name ?: ''}".toString()
  }


  String getNiceName(){
    "${this.class.getSimpleName()}"
  }


  /**
   * Get the list of all properties and there values.
   */
  @Transient
  Map getAllPropertiesAndVals(){
    // The list of property names that we are to ignore.
    // II: ToDo Steve - Please review this.
    // explanation :: I'm not fully sure what side effects this might have, but in local_props below deproxy is called
    // for each property. skippedTitles is a list of strings and was causing hell on earth in the A_Api which was unable to tell the
    // difference between f(x) and f([x]) closure called with a list containing one argument. Not been able to fully
    // wrap my head around A_Api yet, so added skippedTitles here as a stop-gap. Substantial changes already made to A_Api and don't
    // want to change any more yet.
    // added variantNames

    // SO: Think the issue was actually that deproxy was being called on the list of items when iterating [each (el in val)]
    // should have been called on el not val.
    def ignore_list = [
        'id',
        'outgoingCombos',
        'incomingCombos',
        'systemOnly',
        'additionalProperties',
//      'skippedTitles',
        'variantNames'
    ]

    // Get the domain class.
    def domainClass = grailsApplication.getDomainClass(this."class".name)
    // The map of current property names and values to be passed to the
    // new constructor.
    def props = [:]
    // Add combo and persisted properties to the list.
    def localProps = (domainClass?.persistentProperties?.collect{ it.name }) ?: []
    localProps += allComboPropertyNames
    localProps.each{ prop ->
      // Ignore the ones in the list.
      if (prop in ignore_list){
        return
      }
      // println("Deproxy ${prop}")
      def val = this."${prop}"
      switch (val){
        case { it instanceof Collection }:
          def newVals = []
          for (el in val){
            // Deproxy the item in the list and then add.
            def newVal = KBComponent.deproxy(el)
            if (grailsApplication.isDomainClass(newVal."class")){
              // Domain class.
              newVal = newVal.merge()
            }
            // Add to the list.
            newVals << newVal
          }
          props["${prop}"] = newVals
          break
        default:
          props["${prop}"] = KBComponent.deproxy(val)
      }
    }
    props
  }


  /**
   * Get the list of all properties and ids.
   */
  @Transient
  Map getAllPropertiesWithLinks(boolean addCombos = true){
    def ignore_list = [
        'id',
        'outgoingCombos',
        'incomingCombos',
        'systemOnly',
        'additionalProperties',
//      'skippedTitles',
//      'variantNames'
    ]
    // Get the domain class.
    def domainClass = grailsApplication.getDomainClass(this."class".name)
    // The map of current property names and values to be passed to the
    // new constructor.
    def props = [:]
    // Add combo and persisted properties to the list.
    def localProps = (domainClass?.persistentProperties?.collect{ it.name }) ?: []
    if (addCombos){
      localProps += allComboPropertyNames
    }
    localProps.each{ prop ->
      // Ignore the ones in the list.
      if (prop in ignore_list){
        return
      }
      // println("Deproxy ${prop}")
      def val = this."${prop}"
      switch (val){
        case { it instanceof Collection }:
          def newVals = []
          for (el in val){

            // Deproxy the item in the list and then add.
            def newVal = processPropVal(el)

            newVals << newVal
          }
          props["${prop}"] = newVals
          break
        default:
          props["${prop}"] = processPropVal(val)
      }
    }
    props
  }


  private def processPropVal(el){
    def newVal = KBComponent.deproxy(el)
    def result = null
    if (newVal && grailsApplication.isDomainClass(newVal."class")){
      def obj_label = null

      if (newVal.hasProperty('username')){
        obj_label = newVal.username
      }
      else if (newVal.hasProperty('name')){
        obj_label = newVal.name
      }
      else if (newVal.hasProperty('value')){
        obj_label = newVal.value
      }
      else if (newVal.hasProperty('variantName')){
        obj_label = newVal.variantName
      }
      result = ['oid': "${newVal.class.name}:${newVal.id}", 'label': obj_label]
      if (newVal.class.name == 'org.gokb.cred.RefdataValue'){
        result.category = newVal.owner.label
      }
    }
    else{
      result = newVal
    }
    result
  }


  /**
   * Creates a new component of the type of this class
   * and populates all the properties with the values of this one
   * with the exception of the id as this will be set on save.
   */
  @Transient
  <T extends KBComponent> T clone(){
    // Now we have a map of all properties and values we should create our new instance.
    T comp = this."class".newInstance()
    sync(comp)
  }


  /**
   * This method copies the values from this component to the supplied.
   */
  @Transient
  <T extends KBComponent> T sync(T to){
    if (to){
      T me = this
      // Update Master tipp.
      Map propVals = allPropertiesAndVals
      log.debug("Found ${propVals.size()} properties to synchronize.")
      int count = 1
      propVals.each{ p, v ->
        log.debug("(${count}) Attempting to copy '${p}' from component ${me.id} to ${to.id}...")
        if (v != null){
          def toHas = has(to, "${p}")

          if (toHas){
            log.debug("\t...sending value ${v.toString()}")
            to."${p}" = v
          }
          else{
            log.debug("\t...target doesn't support '${p}'")
          }
        }
        else{
          log.debug("\t...value is null")
        }
        count++
      }
    }
    // Return the supplied element.
    to
  }


  /**
   * Similar to the respondsTo method but checks for methods properties and combos.
   */
  @Transient
  static boolean has(Object ob, String op){
    // The flag value.
    boolean hasOp = false
    if (ob){
      // Check properties.
      hasOp = ob.hasProperty(op) ||
          (ob.respondsTo(op)?.size() > 0) ||
          (ob instanceof KBComponent && ob.allComboPropertyNames.contains(op))
    }
    hasOp
  }


  @Transient
  def ensureVariantName(String name){
    def result = null
    if (name.trim().size() != 0){
      def normname = generateNormname(name)
      // Check that name is not already a name or a variant, if so, add it.
      def existing_component = KBComponent.findByNormname(normname)
      if (existing_component == null){
        // Variant names use different normalisation method.
        normname = GOKbTextUtils.normaliseString(name)
        // not already a name
        // Make sure not already a variant name
        def existing_variants = KBComponentVariantName.findAllByNormVariantName(normname)
        if (existing_variants.size() == 0){
          result = new KBComponentVariantName(owner: this, variantName: name).save()
        }
        else{
          log.debug("Unable to add ${name} as an alternate name to ${id} - it's already an alternate name....")
        }
      }
      else{
        log.debug("Unable to add ${name} as an alternate name to ${id} - it's already name for ${existing_component.id}")
      }
    }
    else{
      log.error("No viable variant name supplied!")
    }
    result
  }


  @Transient
  String getDisplayName(){
    return name
  }


  @Transient
  def getNotes(){
    return Note.findAllByOwnerClassAndOwnerId(this.class.name, this.getId())
  }


  def expunge(){
    log.debug("Component expunge")
    def result = [deleteType: this.class.name, deleteId: this.id]
    log.debug("Removing all components")
    Combo.executeUpdate("delete from Combo as c where c.fromComponent=:component or c.toComponent=:component", [component: this])
    KBComponentVariantName.executeUpdate("delete from KBComponentVariantName as c where c.owner=:component", [component: this])

   if (this instanceof CuratoryGroup){
      User.withTransaction {
         this.users.each{ User user ->
            user.removeFromCuratoryGroups(this)
          user.save()
          }
        }
    }
    KBComponent.executeUpdate("delete from ComponentPrice where owner=:component", [component: this])
    this.delete(failOnError: true)
    result
  }

  @Deprecated
  static def expungeAll(List components){
    log.debug("Component bulk expunge")
    def result = [num_requested: components.size(), num_expunged: 0]
    log.debug("Expunging ${result.num_requested} components")
    def remaining = components

    while (remaining.size() > 0){
      def batch = remaining.take(50)
      remaining = remaining.drop(50)

      Combo.executeUpdate("delete from Combo as c where c.fromComponent.id IN (:component) or c.toComponent.id IN (:component)", [component: batch])
      KBComponentVariantName.executeUpdate("delete from KBComponentVariantName as c where c.owner.id IN (:component)", [component: batch])
      ComponentPrice.executeUpdate("delete from ComponentPrice as cp where cp.owner.id IN (:component)", [component: batch])
      result.num_expunged += KBComponent.executeUpdate("delete KBComponent as c where c.id IN (:component)", [component: batch])
    }
    result
  }


  @Transient
  def addCoreGOKbXmlFields(builder, attr) {
    String cName = this.class.name

    // Single props.
    builder.'name'(name)
    builder.'status'(status?.value)
    if (languages){
      builder.'languages'{
        languages.each{ lan ->
          builder.'language'(lan.language.value)
        }
      }
    }
    builder.'shortcode'(shortcode)

    // Variant Names
    if (variantNames){
      builder.'variantNames'{
        variantNames.each{ vn ->
          builder.'variantName'(vn.variantName)
        }
      }
    }
    if (additionalProperties){
      builder.'additionalProperties'{
        additionalProperties.each{ prop ->
          String pName = prop.propertyDefn?.propertyName
          if (pName && prop.apValue){
            builder.'additionalProperty'('name': pName, 'value': prop.apValue)
          }
        }
      }
    }
    if (source){
      builder.'source'{
        source.with{
          addCoreGOKbXmlFields(builder, attr)
          builder.'url'(url)
          builder.'defaultAccessURL'(defaultAccessURL)
          builder.'explanationAtSource'(explanationAtSource)
          builder.'contextualNotes'(contextualNotes)
          builder.'frequency'(frequency)
          builder.'ruleset'(ruleset)
          if (defaultSupplyMethod){
            builder.'defaultSupplyMethod'(defaultSupplyMethod.value)
          }
          if (defaultDataFormat){
            builder.'defaultDataFormat'(defaultDataFormat.value)
          }
          if (responsibleParty){
            builder.'responsibleParty'{
              builder.'name'(responsibleParty.name)
            }
          }
        }
      }
    }
  }

  // Given the type return a string such as "1.23 GBP" which represents the CURRENT
  // price for the type variant. Default type to "list" if null is passed in.
  String getPrice(String type){
    String result = null
    String price_type = type ?: 'list'
    Date now = new Date()
    def cpresult = ComponentPrice.executeQuery(CURRENT_PRICE_HQL, [t: price_type, c: this, d: now])
    if (cpresult.size() == 1){
      result = String.format('%.2f', cpresult.get(0).price)
      if (cpresult.get(0).currency != null){
        result += " ${cpresult.get(0).currency.value}"
      }
    }
    else if (cpresult.size() == 0){
      // No matches - return null
    }
    else{
      throw new RuntimeException("Multiple prices match for component ${this.id} price type ${price_type}")
    }
    return result
  }


  /**
   * Set a price formatted as "nnnn.nn" or "nnnn.nn CUR"
   */
  void setPrice(String type, String price, String currency, Date startDate = null, Date endDate = null){
    Float f = null
    RefdataValue rdv_type = null
    RefdataValue rdv_currency = null
    if (price){
      Date today = todayNoTime()
      Date start = startDate
      Date end = endDate
      f = Float.parseFloat(price)
      rdv_type = RefdataCategory.lookupOrCreate(RCConstants.PRICE_TYPE, type ?: 'list').save(flush: true, failOnError: true)

      if (currency) {
        rdv_currency = RefdataCategory.lookupOrCreate(RCConstants.CURRENCY, currency.trim()).save(flush: true, failOnError: true)
      }
      List<ComponentPrice> existPrices = ComponentPrice.findAllByOwnerAndPriceTypeAndCurrency(this, rdv_type, rdv_currency, [sort: 'lastUpdated', order: 'ASC'])
      if (existPrices.size() > 0){
        ComponentPrice existPrice = existPrices[0]
        if (start != null) {
          existPrice.startDate = start
        }
        if (start != null) {
          existPrice.endDate = end
        }
        if(existPrice.price != f) {
          existPrice.price = f
          existPrice.save()
        }
        save()
      }
      else {
        ComponentPrice cp = new ComponentPrice(
                owner: this,
                priceType: rdv_type,
                currency: rdv_currency,
                price: f,
                startDate: start ?: today,
                endDate: end)
        cp.save()
        /*ERMS-3813: Preishistory nicht mehr nötig in wekb
        // set the end date for the current price(s)
        ComponentPrice.executeUpdate('update ComponentPrice set endDate=:start where owner=:tipp and' +
            '(endDate is null or endDate>:start) and priceType=:type and currency=:currency' ,
            [start: cp.startDate, tipp: this, type: cp.priceType, currency:cp.currency])*/
        // enter the new price
        //prices << cp
        save()
      }
    }
  }


  @Transient
  userAvailableActions(){
    def user = springSecurityService.currentUser
    def allActions = []
    def result = []
    if (this.respondsTo('availableActions')){
      allActions = this.availableActions()
      allActions.each{ ao ->
        if (ao.perm == "delete" && !accessService.checkDeletable(this.class.name)){
        }
        else if (ao.perm == "admin" && !user.hasRole('ROLE_ADMIN')){
        }
        else if (ao.perm == "su" && !user.hasRole('ROLE_SUPERUSER')){
        }
        else{
          result.add(ao)
        }
      }
    }
    result
  }


  private static Date todayNoTime(){
    Calendar calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.getTime()
  }
}

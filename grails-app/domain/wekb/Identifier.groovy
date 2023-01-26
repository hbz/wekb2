package wekb


import groovy.util.logging.Slf4j

import javax.persistence.Transient

@Slf4j
class Identifier {


  def cascadingUpdateService

  IdentifierNamespace namespace
  String value
  String uuid

  // Timestamps
  Date dateCreated
  Date lastUpdated

  static belongsTo = [
          tipp: TitleInstancePackagePlatform,
          org: Org,
          platform: Platform,
          pkg: Package
  ]


  static constraints = {
    uuid(nullable: false, unique: false, blank: false, maxSize: 2048)
    tipp(nullable:true)
    org(nullable:true)
    platform(nullable:true)
    pkg(nullable:true)
    namespace(nullable: false, blank: false)
    value(validator: { val, obj ->
      if (!val || !val.trim()) {
        return ['notNull']
      }

      def pattern = obj.namespace.pattern ? ~"${obj.namespace.pattern}" : null
      if (pattern && !(val ==~ pattern)) {
        return ['illegalIdForm']
      }
    })
  }

  static mapping = {
    table("identifier_new")
    id column: 'id_id'
    version column: 'id_version'
    value column: 'id_value', index: 'id_value_idx'
    namespace column: 'id_namespace_fk', index: 'id_namespace_idx'
    uuid column: 'id_uuid', type: 'text', index: 'id_uuid_idx'
    tipp column: 'id_tipp_fk', index: 'id_tipp_idx'
    org column: 'id_org_fk', index: 'id_org_idx'
    platform column: 'id_platform_fk', index: 'id_platform_idx'
    pkg column: 'id_pkg_fk', index: 'id_pkg_idx'
    lastUpdated column: 'id_last_updated'
    dateCreated column: 'id_date_created'
  }

  protected def generateUuid(){
    if (!uuid){
      uuid = UUID.randomUUID().toString()
    }
  }

  def beforeValidate (){
    log.debug("beforeValidate for ${this}")
    generateUuid()
  }

  def afterInsert (){
    log.debug("afterSave for ${this}")
    def ref = this.getReference()
    if(ref instanceof KBComponent){
      cascadingUpdateService.update(this, dateCreated)
    }

  }

  def beforeDelete (){
    log.debug("beforeDelete for ${this}")
    cascadingUpdateService.update(this, lastUpdated)

  }

  def beforeUpdate(){
    log.debug("beforeUpdate for ${this}")
    if (!uuid){
      generateUuid()
    }
  }

  def afterUpdate(){
    log.debug("afterUpdate for ${this}")
    def ref = this.getReference()
    if(ref instanceof KBComponent){
      cascadingUpdateService.update(this, lastUpdated)
    }

  }
  public String getName() {
    return value
  }

  public String toString() {
    "${namespace.value}:${value} (Identifier ${id})".toString()
  }

  static String getAttributeName(def object) {
    String name

    name = object instanceof Org ?      'org' : name
    name = object instanceof Package ?  'pkg' : name
    name = object instanceof TitleInstancePackagePlatform ? 'tipp' : name
    name = object instanceof Platform ?      'platform' : name

    name
  }

  void setReference(def owner) {
    org  = owner instanceof Org ? owner : org
    pkg  = owner instanceof Package ? owner : pkg
    platform = owner instanceof Platform ?  owner : platform
    tipp = owner instanceof TitleInstancePackagePlatform ? owner : tipp
  }

  Object getReference() {
    int refCount = 0
    def ref

    List<String> fks = ['platform', 'org', 'pkg', 'tipp']
    fks.each { fk ->
      if (this."${fk}") {
        refCount++
        ref = this."${fk}"
      }
    }
    if (refCount == 1) {
      return ref
    }
    return null
  }

 /* static Identifier construct(Map<String, Object> map) {

    withTransaction {
      String value     = map.get('value')
      KBComponent kbcomponent = map.get('kbcomponent')
      def namespace    = map.get('namespace')
      def targetType    = map.get('targetType')

      if(targetType){
        targetType = RefdataValue.findByValueAndOwner(targetType, RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))
      }

      IdentifierNamespace ns
      if (namespace instanceof IdentifierNamespace) {
        ns = namespace
      }
      else {
        if (targetType){
          ns = IdentifierNamespace.findByValueAndTargetType(namespace?.trim(), RefdataValue.findByValueAndOwner(targetType, RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)))
        } else {
          ns = IdentifierNamespace.findByValue(namespace?.trim())
        }

        if(! ns) {
          if (targetType){
            ns = new IdentifierNamespace(value: namespace, name:  namespace, targetType: targetType)
          } else {
            ns = new IdentifierNamespace(value: namespace, name: namespace)
          }
          ns.save()
        }
      }

      String attr = Identifier.getAttributeName(reference)

      def ident = Identifier.executeQuery(
              'select ident from Identifier ident where ident.value = :val and ident.ns = :ns and ident.kbcomponent = :kbcomponent order by ident.id',
              [val: value, ns: ns, kbcomponent: kbcomponent]

      )
      if (ident){
        factoryResult.status += FactoryResult.STATUS_ERR_UNIQUE_BUT_ALREADY_EXISTS_IN_REFERENCE_OBJ
      }
      if (! ident.isEmpty()) {
        if (ident.size() > 1) {
          factoryResult.status += FactoryResult.STATUS_ERR_UNIQUE_BUT_ALREADY_SEVERAL_EXIST_IN_REFERENCE_OBJ
          static_logger.debug("WARNING: multiple matches found for ( ${value}, ${ns}, ${reference} )")
        }
        ident = ident.first()
      }

      if (! ident) {
        Identifier identifierInDB = Identifier.findByNsAndValue(ns, value)
        if (ns.isUnique && identifierInDB && value != "Unknown") {
          static_logger.debug("NO IDENTIFIER CREATED: multiple occurrences found for unique namespace ( ${value}, ${ns} )")
          factoryResult.status += FactoryResult.STATUS_ERR_UNIQUE_BUT_ALREADY_SEVERAL_EXIST_IN_REFERENCE_OBJ
//                factoryResult.result = identifierInDB
          ident = identifierInDB
        } else {
          static_logger.debug("INFO: no match found; creating new identifier for ( ${value}, ${ns}, ${reference.class} )")
          ident = new Identifier(ns: ns, value: value)
          if(parent)
            ident.instanceOf = parent
          ident.setReference(reference)
          ident.note = note
          boolean success = ident.save()
          if (success){
            factoryResult.status += FactoryResult.STATUS_OK
          } else {
            factoryResult.status += FactoryResult.STATUS_ERR
          }
        }
      }
    }
  }*/

  @Transient
  public String getDomainName() {
    return "Identifier"
  }

  public String getShowName() {
    return this.value
  }

}

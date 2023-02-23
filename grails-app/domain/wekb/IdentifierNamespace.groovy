package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.BeanStore
import wekb.helper.RCConstants

import javax.persistence.Transient

class IdentifierNamespace {

  String name
  String value

  @RefdataAnnotation(cat = RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)
  RefdataValue targetType

  String pattern
  String family

  Date dateCreated
  Date lastUpdated

  static mapping = {
    id column:'idns_id'
    version column:'idns_version'
    name column:'idns_name'
    value column:'idns_value'
    targetType column:'idns_targettype'
    family column:'idns_family'
    pattern column:'idns_pattern'
    dateCreated column:'idns_date_created'
    lastUpdated column:'idns_last_updated'
  }

  static constraints = {
    name (nullable:true)
    value (nullable:true, blank:false)
    family (nullable:true, blank:false)
    pattern (nullable:true, blank:false)
    targetType (nullable:true, blank:false)
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)

    value(unique: ['value', 'targetType'])
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null) {

      def dep = ClassUtils.deproxy(obj)
      if (dep instanceof IdentifierNamespace) {
        // Check the value attributes.
        return (this.value == dep.value)
      }
    }
    return false
  }

  static def refdataFind(params) {
    def result = [];
    def ql = null;
    if(params.filter1){
      if(params.filter1 == "all"){
        ql = IdentifierNamespace.executeQuery("from IdentifierNamespace as t order by t.value")
      }else {
        RefdataValue refdataValue = RefdataValue.findByValueAndOwner(params.filter1, RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))
        ql = IdentifierNamespace.executeQuery("from IdentifierNamespace as t where lower(t.value) like :value and (t.targetType is null or t.targetType = :targetType) order by t.value", [value: "${params.q?.toLowerCase()}%", targetType: refdataValue])
      }
    }else {
      ql = IdentifierNamespace.executeQuery("from IdentifierNamespace as t where lower(t.value) like :value and t.targetType is null order by t.value", [value: "${params.q?.toLowerCase()}%"])
    }
    if ( ql ) {
      ql.each { t ->
        result.add([id:"wekb.IdentifierNamespace:${t.id}",text:"${t.name} ${params.filter1 == 'all' ? ( t.targetType ? '(for '+t.targetType.value+')' : '' ) : ''}"])
      }
    }
    result
  }

  def beforeInsert() {
    value = value.toLowerCase()
  }

  public String toString() {
    "${name ?: value}".toString()
  }

  @Transient
  def getIdentifiersCount() {
    return Identifier.executeQuery("select count(value) from Identifier where namespace = :namespace", [namespace: this])[0]
  }

  @Transient
  public String getDomainName() {
    return "Identifier Namespace"
  }

  public String getShowName() {
    return this.name
  }

  String getOID(){
    "${this.class.name}:${id}"
  }
}

package wekb

import wekb.helper.BeanStore

class ComponentVariantName {

  Org org
  Package pkg


  RefdataValue variantType
  RefdataValue locale
  RefdataValue status

  String variantName
  String normVariantName

    Date dateCreated
    Date lastUpdated

  static mapping = {
        id column:'cvn_id'
        version column:'cvn_version'
        org column:'cvn_org_fk'
        pkg column:'cvn_pkg_fk'
        variantName column:'cvn_variant_name'
        normVariantName column:'cvn_norm_variant_name', index:'cvn_norm_variant_name_idx'
        variantType column:'cvn_type_rv_fk'
        locale column:'cvn_locale_rv_fk'
        status column:'cvn_status_rv_fk'

      dateCreated column:'cvn_date_created'
      lastUpdated column:'cvn_last_updated'
  }

  static constraints = {
        variantName (nullable:false, blank:false, maxSize:2048)
        normVariantName  (nullable:true, blank:true, maxSize:2048)
        variantType (nullable:true, blank:false)
        locale (nullable:true, blank:false)
        status (nullable:true, blank:false)

      dateCreated(nullable:true, blank:true)
      lastUpdated(nullable:true, blank:true)

      org(nullable:true, blank:false)
      pkg(nullable:true, blank:false)
  }

  String getOID() {
      "${this.class.name}:${id}"
  }

  static belongsTo = [org: Org,
                    pkg: Package]

  def beforeInsert() {
    // Generate the any necessary values.
    normVariantName = TextUtils.normaliseString(variantName);
  }

  def beforeUpdate() {
    normVariantName = TextUtils.normaliseString(variantName);
  }

    def afterInsert() {
        log.debug("afterSave for ${this}")
        BeanStore.getCascadingUpdateService().update(this, lastUpdated)

    }

    def beforeDelete (){
        log.debug("beforeDelete for ${this}")
        BeanStore.getCascadingUpdateService().update(this, lastUpdated)

    }

    def afterUpdate() {
        log.debug("afterUpdate for ${this}")
        BeanStore.getCascadingUpdateService().update(this, lastUpdated)

    }

    static String getAttributeName(def object) {
        String name

        name = object instanceof Org ?      'org' : name
        name = object instanceof Package ?  'pkg' : name

        name
    }

    void setReference(def owner) {
        org  = owner instanceof Org ? owner : org
        pkg  = owner instanceof Package ? owner : pkg
    }

    Object getReference() {
        int refCount = 0
        def ref

        List<String> fks = ['org', 'pkg']
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
}

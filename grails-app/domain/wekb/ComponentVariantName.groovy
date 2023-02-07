package wekb

class ComponentVariantName {


  def cascadingUpdateService

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
        cascadingUpdateService.update(this, dateCreated)

    }

    def beforeDelete (){
        log.debug("beforeDelete for ${this}")
        cascadingUpdateService.update(this, lastUpdated)

    }

    def afterUpdate() {
        log.debug("afterUpdate for ${this}")
        cascadingUpdateService.update(this, lastUpdated)

    }
}

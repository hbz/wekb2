package wekb

import grails.plugins.orm.auditable.Auditable
import groovy.util.logging.Slf4j
import wekb.base.AbstractBase
import wekb.helper.RCConstants
import wekb.helper.RDStore

import javax.persistence.Transient

@Slf4j
class Vendor extends AbstractBase implements Auditable {


  String name
  String abbreviatedName

  RefdataValue status

  // Timestamps
  Date dateCreated
  Date lastUpdated

  String homepage

  static mappedBy = [
  ]

  static hasMany = [
    roles: RefdataValue,
    contacts: Contact,
    curatoryGroups   : CuratoryGroupVendor,
    packages: PackageVendor
  ]

  static mapping = {
    id column: 'org_id'
    version column: 'org_version'

    uuid column: 'org_uuid'
    name column: 'org_name'
    abbreviatedName column: 'org_abbreviated_name'

    lastUpdated column: 'org_last_updated'
    dateCreated column: 'org_date_created'

    status column: 'org_status_rv_fk'

    homepage column: 'org_homepage'
  }

  static constraints = {
    abbreviatedName(nullable: true, blank: true)
    homepage(nullable: true, blank: true)
    name(validator: { val, obj ->
      if (obj.hasChanged('name')) {
        if (val && val.trim()) {
         /* def status_deleted = RDStore.YN_YES
          def dupes = Org.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);
          if (dupes?.size() > 0 && dupes.any { it != obj }) {
            return ['notUnique']
          }*/
        } else {
          return ['notNull']
        }
      }
    })
  }

  @Override
  def beforeInsert() {
    super.beforeInsertHandler()
  }

  @Override
  def beforeUpdate() {
    super.beforeUpdateHandler()
  }

  @Override
  def beforeDelete() {
    super.beforeDeleteHandler()
  }

  static def refdataFind(params) {
    def result = [];
    def status_deleted = RDStore.KBC_STATUS_DELETED
    def status_filter = null

    if (params.filter1) {
      status_filter = RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, params.filter1)
    }

    params.sort = 'name'

    def ql = null;
    ql = Vendor.findAllByNameIlikeAndStatusNotEqual("%${params.q}%", status_deleted, params)

    if (ql) {
      ql.each { t ->
        if (!status_filter || t.status == status_filter) {
          result.add([id: "${t.class.name}:${t.id}", text: "${t.name}", status: "${t.status?.value}"])
        }
      }
    }

    result
  }

  @Transient
  public String getDomainName() {
    return "Vendor"
  }

  public String getShowName() {
    return this.name
  }

  String toString(){
    "${name ?: ''}".toString()
  }

  def expunge(){
    log.info("Vendor expunge: "+this.id)

    Contact.executeUpdate("delete from Contact where org = :component", [component: this])
    CuratoryGroupVendor.executeUpdate("delete from CuratoryGroupVendor where vendor = :component", [component: this])
    PackageVendor.executeUpdate("delete from PackageVendor where vendor = :component", [component: this])

    def result = [deleteType: this.class.name, deleteId: this.id]
    this.delete(failOnError: true)
    result
  }

  @Transient
  public List<CuratoryGroup> getCuratoryGroupObjects() {
    List<CuratoryGroup> curatoryGroups
    if(this.curatoryGroups.size() > 0){
      curatoryGroups = this.curatoryGroups.curatoryGroup
    }
    return curatoryGroups
  }
}

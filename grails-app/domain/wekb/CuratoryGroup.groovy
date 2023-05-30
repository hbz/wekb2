package wekb

import grails.plugins.orm.auditable.Auditable
import wekb.annotations.RefdataAnnotation
import wekb.base.AbstractBase
import wekb.helper.RCConstants
import wekb.helper.RDStore

import javax.persistence.Transient

class CuratoryGroup extends AbstractBase implements Auditable {


  String name
  RefdataValue status

  // Timestamps
  Date dateCreated
  Date lastUpdated

  @RefdataAnnotation(cat = RCConstants.CURATORY_GROUP_TYPE)
  RefdataValue type

  static hasMany = [
          curatoryGroupUsers: CuratoryGroupUser,
          packages: CuratoryGroupPackage,
          platforms: CuratoryGroupPlatform,
          orgs: CuratoryGroupOrg,
          sources: CuratoryGroupKbartSource
  ]

  static mapping = {
    id column: 'cg_id'
    version column: 'cg_version'

    uuid column: 'cg_uuid'
    name column: 'cg_name'

    lastUpdated column: 'cg_last_updated'
    dateCreated column: 'cg_date_created'

    status column: 'cg_status_rv_fk'
    type column: 'cg_type_rv_fk'
  }

  static constraints = {
    name (validator: { val, obj ->
      if (obj.hasChanged('name')) {
        if (val && val.trim()) {
          def status_deleted = RDStore.KBC_STATUS_DELETED
          def dupes = CuratoryGroup.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);

          if (dupes?.size() > 0 && dupes.any { it != obj }) {
            return ['notUnique']
          }
        } else {
          return ['notNull']
        }
      }
    })
    type (nullable:true, blank:false)
  }

  static def refdataFind(params) {
    def result = [];
    def status_deleted = RDStore.KBC_STATUS_DELETED
    def ql = null;

    params.sort = 'name'

    ql = CuratoryGroup.findAllByNameIlikeAndStatusNotEqual("%${params.q}%", status_deleted ,params)

    ql.each { t ->
        result.add([id:"${t.class.name}:${t.id}", text:"${t.name}", status:"${t.status?.value}"])
    }

    result
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

  @Transient
  public String getDomainName() {
    return "Curatory Group"
  }

  public String getShowName() {
    return this.name
  }

  String toString(){
    "${name ?: ''}".toString()
  }

  def expunge(){
    log.info("CuratoryGroup expunge:"+ this.id)

    CuratoryGroupUser.executeUpdate("delete from CuratoryGroupUser where curatoryGroup = :component", [component: this])
    CuratoryGroupKbartSource.executeUpdate("delete from CuratoryGroupKbartSource where curatoryGroup = :component", [component: this])
    CuratoryGroupPlatform.executeUpdate("delete from CuratoryGroupPlatform where curatoryGroup = :component", [component: this])
    CuratoryGroupPackage.executeUpdate("delete from CuratoryGroupPackage where curatoryGroup = :component", [component: this])
    CuratoryGroupOrg.executeUpdate("delete from CuratoryGroupOrg where curatoryGroup = :component", [component: this])

    def result = [deleteType: this.class.name, deleteId: this.id]
    this.delete(failOnError: true)
    result
  }

  @Transient
  public getCurrentTippCount() {
    def refdata_current = RDStore.KBC_STATUS_CURRENT

    int result = 0
    if (getProvidedPackages()) {
      result = TitleInstancePackagePlatform.executeQuery("select count(t.id) from TitleInstancePackagePlatform as t where t.pkg in (:pkgs) and t.status = :status"
              , [pkgs: getProvidedPackages(), status: refdata_current])[0]
    }

    result
  }

  public getRetiredTippCount() {
    def refdata_current = RDStore.KBC_STATUS_RETIRED

    int result = 0
    if (getProvidedPackages()) {
      result = TitleInstancePackagePlatform.executeQuery("select count(t.id) from TitleInstancePackagePlatform as t where t.pkg in (:pkgs) and t.status = :status"
              , [pkgs: getProvidedPackages(), status: refdata_current])[0]
    }

    result
  }

  public getExpectedTippCount() {
    def refdata_current = RDStore.KBC_STATUS_EXPECTED

    int result = 0
    if (getProvidedPackages()) {
      result = TitleInstancePackagePlatform.executeQuery("select count(t.id) from TitleInstancePackagePlatform as t where t.pkg in (:pkgs) and t.status = :status"
              , [pkgs: getProvidedPackages(), status: refdata_current])[0]
    }

    result
  }

  public getDeletedTippCount() {
    def refdata_current = RDStore.KBC_STATUS_DELETED

    int result = 0
    if (getProvidedPackages()) {
      result = TitleInstancePackagePlatform.executeQuery("select count(t.id) from TitleInstancePackagePlatform as t where t.pkg in (:pkgs) and t.status = :status"
              , [pkgs: getProvidedPackages(), status: refdata_current])[0]
    }

    result
  }

  @Transient
  def getProvidedPackages(){
    Package.executeQuery('select p from Package as p where exists ( select cgp from CuratoryGroupPackage cgp where cgp.pkg = p and cgp.curatoryGroup = :curGroup)', [curGroup: this])
  }

  @Transient
  def getProvidedPlatforms(){
    Platform.executeQuery('select p from Platform as p where exists ( select cgp from CuratoryGroupPlatform cgp where cgp.platform = p and cgp.curatoryGroup = :curGroup)', [curGroup: this])
  }

  @Transient
  def getProvidedOrgs(){
    Platform.executeQuery('select o from Org as o where exists ( select cgo from CuratoryGroupOrg cgo where cgo.org = p and cgo.curatoryGroup = :curGroup)', [curGroup: this])
  }

}


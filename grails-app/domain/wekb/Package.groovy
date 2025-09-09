package wekb

import grails.plugins.orm.auditable.Auditable
import wekb.annotations.RefdataAnnotation
import wekb.base.AbstractBase
import wekb.helper.BeanStore
import wekb.helper.RCConstants
import wekb.helper.RDStore
import groovy.time.TimeCategory
import groovy.util.logging.Slf4j

import javax.persistence.Transient

@Slf4j
class Package  extends AbstractBase implements Auditable {

  String name
  String normname

  RefdataValue status

  // Timestamps
  Date dateCreated
  Date lastUpdated

  String description

  Org provider
  Platform nominalPlatform

  // Refdata
  @RefdataAnnotation(cat = RCConstants.PACKAGE_SCOPE)
  RefdataValue scope
  @RefdataAnnotation(cat = RCConstants.PACKAGE_CONTENT_TYPE)
  RefdataValue contentType
  @RefdataAnnotation(cat = RCConstants.PACKAGE_BREAKABLE)
  RefdataValue breakable
  @RefdataAnnotation(cat = RCConstants.PACKAGE_CONSISTENT)
  RefdataValue consistent
  @RefdataAnnotation(cat = RCConstants.PACKAGE_PAYMENT_TYPE)
  RefdataValue paymentType

  @RefdataAnnotation(cat = RCConstants.PACKAGE_OPEN_ACCESS)
  RefdataValue openAccess

  @RefdataAnnotation(cat = RCConstants.PACKAGE_FILE)
  RefdataValue file

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue freeTrial

  String freeTrialPhase

  String descriptionURL

  Set variantNames = []

  KbartSource kbartSource

  static mappedBy = [
  ]

  static hasMany = [
          nationalRanges : RefdataValue,
          regionalRanges : RefdataValue,
          ddcs : RefdataValue,
          paas : PackageArchivingAgency,
          ids: Identifier,
          updatePackageInfos: UpdatePackageInfo,
          tipps: TitleInstancePackagePlatform,
          variantNames        : ComponentVariantName,
          curatoryGroups   : CuratoryGroupPackage,
          vendors: PackageVendor
  ]

  static mapping = {
    id column: 'pkg_id'
    version column: 'pkg_version'

    uuid column: 'pkg_uuid'
    name column: 'pkg_name'

    lastUpdated column: 'pkg_last_updated'
    dateCreated column: 'pkg_date_created'

    status column: 'pkg_status_rv_fk'
    scope column: 'pkg_scope_rv_fk'
    breakable column: 'pkg_breakable_rv_fk'
    consistent column: 'pkg_consistent_rv_fk'
    paymentType column: 'pkg_payment_type_rv_fk'

    openAccess column: 'pkg_open_access_rv_fk'
    file column: 'pkg_file_rv_fk'
    contentType column: 'pkg_content_type_rv_fk'

    descriptionURL column: 'pkg_descr_url'

    freeTrial column: 'pkg_free_trial_rv_fk'

    freeTrialPhase column: 'pkg_free_trial_phase', type: 'text'
    description column: 'pkg_description', type: 'text'
    normname column: 'pkg_normname', type: 'text', index: 'pkg_normname_idx'

    ddcs             joinTable: [
            name:   'package_dewey_decimal_classification',
            key:    'package_fk',
            column: 'ddc_rv_fk', type:   'BIGINT'
    ], lazy: false

    nationalRanges             joinTable: [
            name:   'package_national_range',
            key:    'package_fk',
            column: 'national_range_rv_fk', type:   'BIGINT'
    ], lazy: false

    regionalRanges             joinTable: [
            name:   'package_regional_range',
            key:    'package_fk',
            column: 'regional_range_rv_fk', type:   'BIGINT'
    ], lazy: false

    variantNames cascade: "all,delete-orphan", lazy: false

      nominalPlatform column: 'pkg_platform_fk'
      provider column: 'pkg_provider_fk'

    kbartSource column: 'pkg_kbart_source_fk'
  }

  static constraints = {
    freeTrial(nullable: true)
    freeTrialPhase(nullable: true, blank: true)

    description(nullable: true, blank: true)
    scope(nullable: true)
    breakable(nullable: true)
    consistent(nullable: true)
    paymentType(nullable: true)
    openAccess (nullable: true)
    contentType (nullable: true)
    file (nullable: true)
    descriptionURL(nullable: true, blank: true)
    name(validator: { val, obj ->
      if (obj.hasChanged('name')) {
        if (val && val.trim()) {
         /* def status_deleted = RDStore.KBC_STATUS_DELETED
          def dupes = Package.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);

          if (dupes?.size() > 0 && dupes.any { it != obj }) {
            return ['notUnique']
          }*/
        }
        else {
          return ['notNull']
        }
      }
    })
    nationalRanges(nullable:true)
    regionalRanges(nullable:true)
    ddcs(nullable:true)
    paas(nullable:true)

    nominalPlatform (nullable: true)
    provider (nullable: true)
    kbartSource (nullable: true)

  }

  @Override
  def beforeInsert() {
    super.beforeInsertHandler()
    generateNormname()
  }

  @Override
  def beforeUpdate() {
    generateNormname()
    super.beforeUpdateHandler()
  }

  @Override
  def beforeDelete() {
    generateNormname()
    super.beforeDeleteHandler()
  }

  static def generateNormname(str_to_norm){
    def r = TextUtils.norm2(str_to_norm)
    if (r.length() == 0){
      r = null
    }
    return r
  }


  protected def generateNormname(){
    log.debug("checking for normname")
    this.normname = generateNormname(name)
  }

  static def refdataFind(params) {
    def result = [];
    def status_filter = null

    if (params.filter1) {
      status_filter = RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, params.filter1)
    }

    params.sort = 'name'

    def ql = null;
    ql = Package.findAllByNameIlikeAndStatusNotInList("%${params.q}%", [RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_REMOVED], params)

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
  public getCurrentTippCount() {
    def refdata_status = RDStore.KBC_STATUS_CURRENT
    int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as t where t.pkg = :pkg and t.status = :status"
      , [pkg: this, status: refdata_status])[0]

    result
  }

  @Transient
  public getTippCount() {
    int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as t where t.pkg = :pkg"
            , [pkg: this])[0]

    result
  }

  @Transient
  public getTippCountWithoutRemoved() {
    def refdata_status = RDStore.KBC_STATUS_REMOVED
    int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as t where t.pkg = :pkg and t.status != :status"
            , [pkg: this, status: refdata_status])[0]

    result
  }

  @Transient
  public getRetiredTippCount() {
    def refdata_status = RDStore.KBC_STATUS_RETIRED
    int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as t where t.pkg = :pkg and t.status = :status"
            , [pkg: this, status: refdata_status])[0]

    result
  }

  @Transient
  public getExpectedTippCount() {
    def refdata_status = RDStore.KBC_STATUS_EXPECTED
    int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as t where t.pkg = :pkg and t.status = :status"
            , [pkg: this, status: refdata_status])[0]

    result
  }

  @Transient
  public getDeletedTippCount() {
    def refdata_status = RDStore.KBC_STATUS_DELETED
    int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as t where t.pkg = :pkg and t.status = :status"
            , [pkg: this, status: refdata_status])[0]

    result
  }

  @Transient
  Map<String, Integer> getTippCountMap() {
    List rows = TitleInstancePackagePlatform.executeQuery("select new map(t.status as status, count(*) as count) from TitleInstancePackagePlatform t where t.pkg = :pkg group by t.status", [pkg: this])
    Map<String, Integer> result = rows.collectEntries { row -> [row.status.value, row.count] } as Map<RefdataValue, Integer>
    result.total = rows.sum { row -> row.count }
    result
  }


  public void deleteSoft(context) {
    def deleted_status =  RDStore.KBC_STATUS_DELETED
    Package.withTransaction {
      this.status = deleted_status
      if(this.kbartSource){
        this.kbartSource.status = deleted_status
        this.kbartSource.save()
      }
      this.save()
    }

    // Delete the tipps too as a TIPP should not exist without the associated,
    // package.
    Date now = new Date()
    TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform as t set t.status = :del, t.lastUpdated = :now where t.status != :del and t.pkg = :pkg", [del: deleted_status, pkg: this, now: now])
  }


  public void retireWithTipps(context) {
    log.debug("package::retireWithTipps");
    // Call the delete method on the superClass.
    log.debug("Updating package status to retired");
    def retired_status = RDStore.KBC_STATUS_RETIRED
    Package.withTransaction {
      this.status = retired_status
      if(this.kbartSource){
        this.kbartSource.status = retired_status
        this.kbartSource.save()
      }
      this.save()
    }

    log.debug("Retiring tipps")
    Date now = new Date()
    TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform as t set t.status = :ret, t.lastUpdated = :now where t.status != :ret and t.pkg = :pkg", [ret: retired_status, pkg: this, now: now])
  }

  public void removeWithTipps(context) {
    log.debug("package::removeWithTipps");
    log.debug("Updating package status to removed");
    def removedStatus = RDStore.KBC_STATUS_REMOVED
    Package.withTransaction {
      this.status = removedStatus
      if(this.kbartSource){
        this.kbartSource.status = removedStatus
        this.kbartSource.save()
      }
      this.save()
    }

    log.debug("removed tipps")

    Date now = new Date()
    TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform as t set t.status = :rev, t.lastUpdated = :now where t.status != :rev and t.pkg = :pkg", [rev: removedStatus, pkg: this, now: now])
  }

  public void removeOnlyTipps(context) {
    log.debug("package::removeOnlyTipps")
    Date now = new Date()
    def removedStatus = RDStore.KBC_STATUS_REMOVED
    Package.withTransaction {
      this.lastUpdated = now
      this.save()
    }

    log.debug("removed tipps")
    TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform as t set t.status = :rev, t.lastUpdated = :now where t.status != :rev and t.pkg = :pkg", [rev: removedStatus, pkg: this, now: now])
  }

  public void removeOnlyDeletedTipps(context) {
    log.debug("package::removeOnlyDeletedTipps")
    Date now = new Date()
    def removedStatus = RDStore.KBC_STATUS_REMOVED
    def deletedStatus = RDStore.KBC_STATUS_DELETED
    Package.withTransaction {
      this.lastUpdated = now
      this.save()
    }

    log.debug("removed tipps")
    TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform as t set t.status = :rev, t.lastUpdated = :now where t.status != :rev and t.pkg = :pkg and t.status = :deleted", [deleted: deletedStatus, rev: removedStatus, pkg: this, now: now])
  }

  public void currentWithTipps(context) {
    log.debug("package::currentWithTipps");
    log.debug("Updating package status to current");
    def currentStatus = RDStore.KBC_STATUS_CURRENT
    Package.withTransaction {
      this.status = currentStatus
      if(this.kbartSource){
        this.kbartSource.status = currentStatus
        this.kbartSource.save()
      }
      this.save()
    }

    Date now = new Date()
    TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform as t set t.status = :cur, t.lastUpdated = :now where t.status != :cur and t.pkg = :pkg", [cur: currentStatus, pkg: this, now: now])
  }


  @Transient
  private static getCoverageStatements(Long tipp_id) {
    def result = TIPPCoverageStatement.executeQuery("from TIPPCoverageStatement as tcs where tcs.tipp.id = :tipp", ['tipp': tipp_id], [readOnly: true])
    result
  }

  @Transient
  public getRecentActivity() {
    def result = [];

    if (this.id) {

      def changes = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp ' +
        'where tipp.pkg = :pkg ',
        [pkg: this]);

      use(TimeCategory) {
        changes.each {
          if (it.isDeleted()) {
            result.add([it, it.lastUpdated, 'Deleted (Status)'])
          }
          else if (it.isRetired()) {
            result.add([it, it.lastUpdated, it.accessEndDate ? "Retired (${it.accessEndDate})" : 'Retired (Status)'])
          }
          else if (it.lastUpdated <= it.dateCreated + 1.minute) {
            result.add([it, it.dateCreated, it.accessStartDate ? "Added (${it.accessStartDate})" : 'Newly Added'])
          }
          else {
            result.add([it, it.lastUpdated, 'Updated'])
          }
        }
      }

//       result.addAll(additions)
//       result.addAll(deletions)
      result.sort { it[1] }
      result = result.reverse();
      //result = result.take(n);
    }

    return result;
  }

  void createCoreIdentifiersIfNotExist(){
     boolean isChanged = false
      [IdentifierNamespace.PKG_ID].each{ coreNs ->
        if ( ! ids.find {it.namespace.value == coreNs}){
          addOnlySpecialIdentifiers(coreNs, 'Unknown')
          isChanged = true
        }
      }
      if (isChanged) refresh()
  }

  void addOnlySpecialIdentifiers(String ns, String value) {
    boolean found = false
    this.ids.each {
      if ( it.namespace?.value == ns && it.value == value ) {
        found = true
      }
    }

    if ( !found && value != '') {
      value = value?.trim()
      ns = ns.trim()
      RefdataCategory refdataCategory = RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)

      IdentifierNamespace namespace = IdentifierNamespace.findByValueIlikeAndTargetType(ns, RefdataValue.findByValueAndOwner('Package', refdataCategory))
      Identifier identifier = new Identifier(namespace: namespace, value: value, pkg: this)
      identifier.save()

    }
  }

  @Transient
  List<TitleInstancePackagePlatform> findTippDuplicatesByName() {

    List<TitleInstancePackagePlatform> tippsDuplicates = TitleInstancePackagePlatform.executeQuery("select tipp from TitleInstancePackagePlatform as tipp " +
            " where tipp.pkg = :pkg and tipp.status != :removed and" +
            " tipp.name in (select tipp2.name from TitleInstancePackagePlatform tipp2 where tipp2.pkg = :pkg and tipp2.status != :removed group by tipp2.name having count(tipp2.name) > 1)" +
            " order by tipp.name",
            [pkg: this, removed: RDStore.KBC_STATUS_REMOVED]) ?: []
  }

  @Transient
  List<TitleInstancePackagePlatform> findTippDuplicatesByURL() {

    List<TitleInstancePackagePlatform> tippsDuplicates = TitleInstancePackagePlatform.executeQuery("select tipp from TitleInstancePackagePlatform as tipp" +
            " where tipp.pkg = :pkg and tipp.status != :removed and" +
            " tipp.url in (select tipp2.url from TitleInstancePackagePlatform tipp2 where tipp2.pkg = :pkg and tipp2.status != :removed group by tipp2.url having count(tipp2.url) > 1)" +
            " order by tipp.url",
            [pkg: this, removed: RDStore.KBC_STATUS_REMOVED]) ?: []
  }

  @Transient
  List<TitleInstancePackagePlatform> findTippDuplicatesByTitleID() {

    IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

    if(identifierNamespace) {
      List<TitleInstancePackagePlatform> tippsDuplicates = TitleInstancePackagePlatform.executeQuery("select tipp from TitleInstancePackagePlatform as tipp join tipp.ids as ident" +
              " where tipp.pkg = :pkg and tipp.status != :removed " +
              " and ident.value in (select ident2.value FROM Identifier AS ident2, TitleInstancePackagePlatform as tipp2 WHERE ident2.namespace = :namespace and ident2.tipp = tipp2 and tipp2.pkg = :pkg and tipp2.status != :removed" +
              " group by ident2.value having count(ident2.value) > 1) order by ident.value",
              [pkg: this, namespace: identifierNamespace, removed: RDStore.KBC_STATUS_REMOVED]) ?: []
    }else {
      return []
    }
  }

  @Transient
  List<TitleInstancePackagePlatform> findTippByWithoutTitleID() {

    IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

    if(identifierNamespace) {
      List<TitleInstancePackagePlatform> tippsDuplicates = TitleInstancePackagePlatform.executeQuery("select tipp from TitleInstancePackagePlatform as tipp join tipp.ids as ident" +
              " where tipp.pkg = :pkg and tipp.status != :removed " +
              " and not exists(select id from Identifier id where id.tipp = tipp and id.namespace = :namespace)",
              [pkg: this, namespace: identifierNamespace, removed: RDStore.KBC_STATUS_REMOVED]) ?: []
    }else {
      return []
    }
  }

  @Transient
  Integer getTippDuplicatesByNameCount() {

    int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as tipp" +
            " where tipp.pkg = :pkg and tipp.status != :removed and" +
            " tipp.name in (select tipp2.name from TitleInstancePackagePlatform tipp2 where tipp2.pkg = :pkg and tipp2.status != :removed group by tipp2.name having count(tipp2.name) > 1)",
            [pkg: this, removed: RDStore.KBC_STATUS_REMOVED])[0]
    return result
  }

  @Transient
  Integer getTippDuplicatesByURLCount() {

    int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as tipp" +
            " where tipp.pkg = :pkg and tipp.status != :removed and " +
            " tipp.url in (select tipp2.url from TitleInstancePackagePlatform tipp2 where tipp2.pkg = :pkg and tipp2.status != :removed group by tipp2.url having count(tipp2.url) > 1)",
            [pkg: this, removed: RDStore.KBC_STATUS_REMOVED])[0]

    return result
  }

  @Transient
  Integer getTippDuplicatesByTitleIDCount() {
    IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

    if(identifierNamespace) {
      int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as tipp join tipp.ids as ident" +
              " where tipp.pkg = :pkg and tipp.status != :removed " +
              " and ident.value in (select ident2.value FROM Identifier AS ident2, TitleInstancePackagePlatform as tipp2 WHERE ident2.namespace = :namespace and ident2.tipp = tipp2 and tipp2.pkg = :pkg and tipp2.status != :removed" +
              " group by ident2.value having count(ident2.value) > 1)",
              [pkg: this, namespace: identifierNamespace, removed: RDStore.KBC_STATUS_REMOVED])[0]
      return result
    }else {
      return 0
    }
  }

  @Transient
  Integer getTippsByTitleIDCount() {
    IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

    if(identifierNamespace) {
      int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as tipp join tipp.ids as ident" +
              " where tipp.pkg = :pkg and tipp.status != :removed " +
              " and exists(select id from Identifier id where id.tipp = tipp and id.namespace = :namespace)",
              [pkg: this, namespace: identifierNamespace, removed: RDStore.KBC_STATUS_REMOVED])[0]
      return result
    }else {
      return 0
    }
  }

  @Transient
  Integer getTippsWithoutTitleIDCount() {
    IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

    if(identifierNamespace) {
      int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as tipp" +
              " where tipp.pkg = :pkg and tipp.status != :removed " +
              " and not exists(select id from Identifier id where id.tipp = tipp and id.namespace = :namespace)",
              [pkg: this, namespace: identifierNamespace, removed: RDStore.KBC_STATUS_REMOVED])[0]
      return result
    }else {
      return 0
    }
  }

  @Transient
  public String getDomainName() {
   return "Package"
  }

  public String getShowName() {
    return this.name
  }

  String toString(){
    "${name ?: ''}".toString()
  }

  @Transient
  public String getAnbieterProduktIDs() {
    IdentifierNamespace namespace = IdentifierNamespace.findByValueAndTargetType(IdentifierNamespace.PKG_ID, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_PACKAGE)
    List<String> identifiers = Identifier.executeQuery('select value from Identifier where namespace = :ns and value != :val and pkg = :pkg', [pkg: this, ns: namespace, val: 'Unknown'])

    if(identifiers.size() == 0){
      return ''
    }else {
      return identifiers.join(', ')
    }
  }

  @Transient
  public getLastSuccessfulAutoUpdateInfo() {
    UpdatePackageInfo updatePackageInfo = UpdatePackageInfo.executeQuery("from UpdatePackageInfo where pkg = :pkg and status = :status and automaticUpdate = true" +
            " order by lastUpdated desc", [pkg: this, status: RDStore.UPDATE_STATUS_SUCCESSFUL], [max: 1, offset: 0])[0]
    updatePackageInfo
  }

  @Transient
  public IdentifierNamespace getTitleIDNameSpace(){
    IdentifierNamespace identifierNamespace
    identifierNamespace = IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)


    return identifierNamespace
  }

  @Transient
  public getCountAutoUpdateInfos() {
    int result = UpdatePackageInfo.executeQuery("select count(*) from UpdatePackageInfo where pkg = :pkg and automaticUpdate = true", [pkg: this])[0]
    result
  }

  @Transient
  public getCountManualUpdateInfos() {
    int result = UpdatePackageInfo.executeQuery("select count(*) from UpdatePackageInfo where pkg = :pkg and automaticUpdate = false", [pkg: this])[0]
    result
  }

    @Transient
    public getLastSuccessfulManualUpdateInfo() {
        UpdatePackageInfo updatePackageInfo = UpdatePackageInfo.executeQuery("from UpdatePackageInfo where pkg = :pkg and status = :status and automaticUpdate = false" +
                " order by lastUpdated desc", [pkg: this, status: RDStore.UPDATE_STATUS_SUCCESSFUL], [max: 1, offset: 0])[0]
        updatePackageInfo
    }

  @Transient
  public getLastSuccessfulUpdateInfo() {
    UpdatePackageInfo updatePackageInfo = UpdatePackageInfo.executeQuery("from UpdatePackageInfo where pkg = :pkg and status = :status" +
            " order by lastUpdated desc", [pkg: this, status: RDStore.UPDATE_STATUS_SUCCESSFUL], [max: 1, offset: 0])[0]
    updatePackageInfo
  }

  @Transient
  public List<CuratoryGroup> getCuratoryGroupObjects() {
    List<CuratoryGroup> curatoryGroups
    if(this.curatoryGroups.size() > 0){
      curatoryGroups = this.curatoryGroups.curatoryGroup
    }
    return curatoryGroups
  }

  @Transient
  public boolean isPackageLinkedInLaser() {
    boolean linked = false
    int packageLinkedInLLaserCount = BeanStore.getLaserCleanUpService().packageLinkedInLaserCount(uuid)
    if (packageLinkedInLLaserCount > 0) {
      linked = true
    }
    return linked
  }

  @Transient
  public int packageLinkedInLaserCount() {
    int packageLinkedInLLaserCount = BeanStore.getLaserCleanUpService().packageLinkedInLaserCount(uuid)

    return packageLinkedInLLaserCount
  }

  @Transient
  public int tippsInLaserCount() {
    int tippsInLaserCount = BeanStore.getLaserCleanUpService().tippsInLaserCount(uuid)

    return tippsInLaserCount
  }


}

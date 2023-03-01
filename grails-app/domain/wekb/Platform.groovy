package wekb

import grails.plugins.orm.auditable.Auditable
import wekb.annotations.RefdataAnnotation
import wekb.base.AbstractBase
import wekb.helper.RCConstants
import groovy.util.logging.Slf4j
import wekb.helper.RDStore

import javax.persistence.Transient

@Slf4j
class Platform  extends AbstractBase implements Auditable {


  String name
  RefdataValue status

  // Timestamps
  Date dateCreated
  Date lastUpdated

  String primaryUrl

  IdentifierNamespace titleNamespace


  @RefdataAnnotation(cat = RCConstants.PLATFORM_IP_AUTH)
  RefdataValue ipAuthentication

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue shibbolethAuthentication

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue passwordAuthentication

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue openAthens

  @RefdataAnnotation(cat = RCConstants.PLATFORM_STATISTICS_FORMAT)
  RefdataValue statisticsFormat

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR3Supported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR4Supported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR5Supported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR4SushiApiSupported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR5SushiApiSupported

  String counterR4SushiServerUrl

  String counterR5SushiServerUrl
  String counterRegistryUrl

  String statisticsAdminPortalUrl

  @RefdataAnnotation(cat = RCConstants.PLATFORM_STATISTICS_UPDATE)
  RefdataValue statisticsUpdate

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue proxySupported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterCertified

  Date lastAuditDate

  Org provider

  String counterRegistryApiUuid

  static hasMany = [
          roles: RefdataValue,
          ids: Identifier,
          tipps: TitleInstancePackagePlatform,
          curatoryGroups   : CuratoryGroupPlatform,
          federations : PlatformFederation,
  ]

  static mapping = {
    id column: 'plat_id'
    version column: 'plat_version'

    uuid column: 'plat_uuid'
    name column: 'plat_name'

    lastUpdated column: 'plat_last_updated'
    dateCreated column: 'plat_date_created'

    status column: 'plat_status_rv_fk'

    primaryUrl column: 'plat_primary_url', index: 'platform_primary_url_idx'
    ipAuthentication column: 'plat_auth_by_ip_fk_rv'
    shibbolethAuthentication column: 'plat_auth_by_shib_fk_rv'
    openAthens column: 'plat_open_athens_fk_rv'
    passwordAuthentication column: 'plat_auth_by_pass_fk_rv'
    statisticsFormat column: 'plat_statistics_format_fk_rv'
    counterR3Supported column: 'plat_counter_r3_supported_fk_rv'
    counterR4Supported column: 'plat_counter_r4_supported_fk_rv'
    counterR5Supported column: 'plat_counter_r5_supported_fk_rv'
    counterR4SushiApiSupported column: 'plat_counter_r4_sushi_api_supported_fk_rv'
    counterR5SushiApiSupported column: 'plat_counter_r5_sushi_api_supported_fk_rv'
    counterR4SushiServerUrl column: 'plat_counter_r4_sushi_server_url'
    counterR5SushiServerUrl column: 'plat_counter_r5_sushi_server_url'
    counterRegistryUrl column: 'plat_counter_registry_url'
    counterCertified column: 'plat_counter_certified'
    statisticsAdminPortalUrl column: 'plat_statistics_admin_portal_url'
    statisticsUpdate column: 'plat_statistics_update_fk_rv'
    proxySupported column: 'plat_proxy_supported_fk_rv'
    titleNamespace column: 'plat_title_namespace_fk'
    lastAuditDate column: 'plat_last_audit_date'

    provider column: 'plat_provider_fk'

    counterRegistryApiUuid column: 'plat_counter_registry_api_uuid'

  }

  static constraints = {
    primaryUrl(url: true, nullable: true, blank: false)
    ipAuthentication(nullable: true, blank: false)
    shibbolethAuthentication(nullable: true, blank: false)
    openAthens (nullable: true, blank: false)
    passwordAuthentication(nullable: true, blank: false)
    name(validator: { val, obj ->
      if (obj.hasChanged('name')) {
        if (val && val.trim()) {
          def status_deleted = RDStore.KBC_STATUS_DELETED
          def dupes = Platform.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);
          if (dupes?.size() > 0 && dupes.any {it != obj}) {
            return ['notUnique']
          }
        } else {
          return ['notNull']
        }
      }
    })
    statisticsFormat(nullable: true, blank: false)
    counterR3Supported(nullable: true, blank: false)
    counterR4Supported(nullable: true, blank: false)
    counterR5Supported(nullable: true, blank: false)
    counterR4SushiApiSupported(nullable: true, blank: false)
    counterR5SushiApiSupported(nullable: true, blank: false)
    counterR4SushiServerUrl(nullable: true, blank: false)
    counterR5SushiServerUrl(nullable: true, blank: false)
    counterRegistryUrl(nullable: true, blank: false)
    counterCertified(nullable: true, blank: false)
    statisticsAdminPortalUrl(nullable: true, blank: false)
    statisticsUpdate(nullable: true, blank: false)
    proxySupported(nullable: true, blank: false)
    titleNamespace(nullable: true)
    lastAuditDate (nullable: true)

    counterRegistryApiUuid(nullable: true, blank: false)

    provider(nullable: true, blank: false)
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
    ql = Platform.findAllByNameIlikeAndStatusNotEqual("${params.q}%", status_deleted, params)

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
    def refdata_current = RDStore.KBC_STATUS_CURRENT
    int result = TitleInstancePackagePlatform.executeQuery("select count(t.id) from TitleInstancePackagePlatform as t where t.hostPlatform = :plt and t.status = :status"
            , [plt: this, status: refdata_current])[0]

    result
  }

  @Transient
  public getPackagesCount() {
    int result = Package.executeQuery('select count(p.id) from Package as p where nominalPlatform = :nominalPlatform', [nominalPlatform: this])[0]
    result
  }

  @Transient
  public String getDomainName() {
    return "Platform"
  }

  public String getShowName() {
    return this.name
  }

  String toString(){
    "${name ?: ''}".toString()
  }


  @Transient
  def  getHostedPackages(){
    Package.executeQuery('select p from Package as p where nominalPlatform = :nominalPlatform', [nominalPlatform: this])
  }

  def expunge(){
    log.info("Platform expunge: "+ this.id)
    def result = [deleteType: this.class.name, deleteId: this.id]
    log.debug("Removing all components")
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

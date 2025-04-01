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

  @RefdataAnnotation(cat = RCConstants.PLATFORM_IP_AUTH)
  RefdataValue ipAuthentication

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue shibbolethAuthentication

  //sub-fields for Shibboleth; to use iff shibbolethAuthentication == true
  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue refedsSupport

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue dpfParticipation

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue sccSupport

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue passwordAuthentication

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue openAthens

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue mailDomain

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue referrerAuthentification

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue ezProxy

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue hanServer

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue otherProxies

  @RefdataAnnotation(cat = RCConstants.PLATFORM_STATISTICS_FORMAT)
  RefdataValue statisticsFormat

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR4Supported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR5Supported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR4CounterApiSupported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR5CounterApiSupported

  String counterR4CounterServerUrl

  String counterR5CounterServerUrl
  String counterRegistryUrl

  String statisticsAdminPortalUrl

  @RefdataAnnotation(cat = RCConstants.PLATFORM_STATISTICS_UPDATE)
  RefdataValue statisticsUpdate

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterCertified

  @RefdataAnnotation(cat = RCConstants.PLATFORM_COUNTER_API_AUTH_METHOD)
  RefdataValue counterApiAuthenticationMethod

  String centralApiKey
  String counterR5CounterPlatform

  Date lastAuditDate
  String counterRegistryApiUuid

  String internLabelForCustomerID
  String internLabelForRequestorKey

  Org provider

  //Barrierefrei / Barrier-free
  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue accessPlatform

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue viewerForPdf

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue viewerForEpub

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue playerForAudio

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue playerForVideo

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue accessEPub

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue onixMetadata

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue accessPdf

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue accessAudio

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue accessVideo

  @RefdataAnnotation(cat = RCConstants.UYNP)
  RefdataValue accessDatabase

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue accessibilityStatementAvailable

  String accessibilityStatementUrl

  //additional services
  String platformBlogUrl
  String rssUrl

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue individualDesignLogo

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue fullTextSearch

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue forwardingUsageStatistcs

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
    refedsSupport column: 'plat_refeds_support_fk_rv'
    dpfParticipation column: 'plat_dpf_participation_fk_rv'
    sccSupport column: 'plat_scc_support_fk_rv'
    passwordAuthentication column: 'plat_auth_by_pass_fk_rv'
    openAthens column: 'plat_open_athens_fk_rv'
    mailDomain column: 'plat_mail_domain_fk_rv'
    referrerAuthentification column: 'plat_referrer_authentification_fk_rv'
    ezProxy column: 'plat_ex_proxy_fk_rv'
    hanServer column: 'plat_han_server_fk_rv'
    otherProxies column: 'plat_other_proxies_fk_rv'
    statisticsFormat column: 'plat_statistics_format_fk_rv'
    counterR4Supported column: 'plat_counter_r4_supported_fk_rv'
    counterR5Supported column: 'plat_counter_r5_supported_fk_rv'
    counterR4CounterApiSupported column: 'plat_counter_r4_counter_api_supported_fk_rv'
    counterR5CounterApiSupported column: 'plat_counter_r5_counter_api_supported_fk_rv'
    counterR4CounterServerUrl column: 'plat_counter_r4_counter_server_url'
    counterR5CounterServerUrl column: 'plat_counter_r5_counter_server_url'
    counterRegistryUrl column: 'plat_counter_registry_url'
    counterCertified column: 'plat_counter_certified'
    statisticsAdminPortalUrl column: 'plat_statistics_admin_portal_url'
    statisticsUpdate column: 'plat_statistics_update_fk_rv'
    counterRegistryApiUuid column: 'plat_counter_registry_api_uuid'
    counterApiAuthenticationMethod column: 'plat_counter_api_authentication_method'
    centralApiKey column: 'plat_central_api_key', type: 'text'
    counterR5CounterPlatform column: 'plat_counter_r5_counter_platform', type: 'text'
    internLabelForCustomerID column: 'plat_intern_label_for_customer_id', type: 'text'
    internLabelForRequestorKey column: 'plat_intern_label_for_requestor_key', type: 'text'
    lastAuditDate column: 'plat_last_audit_date'

    provider column: 'plat_provider_fk'

    accessPlatform column: 'plat_access_platform_fk_rv'
    viewerForPdf column: 'plat_viewer_for_pdf_fk_rv'
    viewerForEpub column: 'plat_viewer_for_epub_fk_rv'
    playerForAudio column: 'plat_player_for_audio_fk_rv'
    playerForVideo column: 'plat_player_for_video_fk_rv'
    accessEPub column: 'plat_access_epub_fk_rv'
    onixMetadata column: 'plat_onix_metadata_fk_rv'
    accessPdf column: 'plat_access_pdf_fk_rv'
    accessAudio column: 'plat_access_audio_fk_rv'
    accessVideo column: 'plat_access_video_fk_rv'
    accessDatabase column: 'plat_access_database_fk_rv'

    accessibilityStatementAvailable column: 'plat_accessibility_statement_available_fk_rv'
    accessibilityStatementUrl column: 'plat_accessibility_statement_url', type: 'text'

    platformBlogUrl column: 'plat_platform_blog_url', type: 'text'
    rssUrl column: 'plat_rss_url', type: 'text'
    individualDesignLogo column: 'plat_individual_design_logo_fk_rv'
    fullTextSearch column: 'plat_full_text_search_fk_rv'

    forwardingUsageStatistcs column: 'plat_forwarding_usage_statistcs_fk_rv'

  }

  static constraints = {
    primaryUrl(url: true, nullable: true, blank: false)
    ipAuthentication(nullable: true, blank: false)
    shibbolethAuthentication(nullable: true, blank: false)
    refedsSupport(nullable: true, blank: false)
    dpfParticipation(nullable: true, blank: false)
    sccSupport(nullable: true, blank: false)
    mailDomain(nullable: true, blank: false)
    referrerAuthentification(nullable: true, blank: false)
    ezProxy(nullable: true, blank: false)
    hanServer(nullable: true, blank: false)
    otherProxies(nullable: true, blank: false)
    passwordAuthentication(nullable: true, blank: false)
    openAthens (nullable: true, blank: false)
    name(validator: { val, obj ->
      if (obj.hasChanged('name')) {
        if (val && val.trim()) {
         /* def status_deleted = RDStore.KBC_STATUS_DELETED
          def dupes = Platform.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);
          if (dupes?.size() > 0 && dupes.any {it != obj}) {
            return ['notUnique']
          }*/
        } else {
          return ['notNull']
        }
      }
    })
    statisticsFormat(nullable: true, blank: false)
    counterR4Supported(nullable: true, blank: false)
    counterR5Supported(nullable: true, blank: false)
    counterR4CounterApiSupported(nullable: true, blank: false)
    counterR5CounterApiSupported(nullable: true, blank: false)
    counterR4CounterServerUrl(nullable: true, blank: false)
    counterR5CounterServerUrl(nullable: true, blank: false)
    counterRegistryUrl(nullable: true, blank: false)
    counterCertified(nullable: true, blank: false)
    statisticsAdminPortalUrl(nullable: true, blank: false)
    statisticsUpdate(nullable: true, blank: false)
    counterApiAuthenticationMethod (nullable: true, blank: false)
    centralApiKey(nullable: true, blank: true)
    counterR5CounterPlatform(nullable: true, blank: false)
    internLabelForCustomerID(nullable: true, blank: true)
    internLabelForRequestorKey(nullable: true, blank: true)
    lastAuditDate (nullable: true)

    counterRegistryApiUuid(nullable: true, blank: false)

    provider(nullable: true, blank: false)

    accessPlatform (nullable: true, blank: false)
    viewerForPdf (nullable: true, blank: false)
    viewerForEpub (nullable: true, blank: false)
    playerForAudio (nullable: true, blank: false)
    playerForVideo (nullable: true, blank: false)
    accessEPub (nullable: true, blank: false)
    onixMetadata (nullable: true, blank: false)
    accessPdf (nullable: true, blank: false)
    accessAudio (nullable: true, blank: false)
    accessVideo (nullable: true, blank: false)
    accessDatabase (nullable: true, blank: false)
    accessibilityStatementAvailable (nullable: true, blank: false)
    accessibilityStatementUrl (nullable: true, blank: true)
    platformBlogUrl (nullable: true, blank: true)
    rssUrl (nullable: true, blank: true)
    individualDesignLogo (nullable: true, blank: false)
    fullTextSearch (nullable: true, blank: false)
    forwardingUsageStatistcs(nullable: true, blank: false)
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
    def status_filter = null

    if (params.filter1) {
      status_filter = RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, params.filter1)
    }

    params.sort = 'name'

    def ql = null;
    ql = Platform.findAllByNameIlikeAndStatusNotInList("%${params.q}%", [RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_REMOVED], params)

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
    int result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as t where t.hostPlatform = :plt and t.status = :status"
            , [plt: this, status: refdata_current])[0]

    result
  }

  @Transient
  public getPackagesCount() {
    int result = Package.executeQuery('select count(*) from Package as p where nominalPlatform = :nominalPlatform', [nominalPlatform: this])[0]
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

  @Transient
  public List<CuratoryGroup> getCuratoryGroupObjects() {
    List<CuratoryGroup> curatoryGroups
    if(this.curatoryGroups.size() > 0){
      curatoryGroups = this.curatoryGroups.curatoryGroup
    }
    return curatoryGroups
  }
}

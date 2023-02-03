package wekb

import wekb.helper.RCConstants
import groovy.util.logging.Slf4j
import wekb.helper.RDStore

import javax.persistence.Transient

@Slf4j
class Org extends KBComponent {

  RefdataValue mission
  String homepage

  String metadataDownloaderURL
  String kbartDownloaderURL

  Set variantNames = []

  def availableActions() {
    [
     /* [code: 'org::deprecateReplace', label: 'Replace Publisher With...'],
     */
      [code: 'method::deleteSoft', label: 'Delete Provider', perm: 'delete'],
      [code: 'method::retire', label: 'Retire Provider', perm: 'admin'],
      [code: 'method::setCurrent', label: 'Set Provider Current'],
      [code: 'setStatus::Removed', label: 'Remove Provider', perm: 'delete'],
    ]
  }

/*  static mappedByCombo = [
    providedPackages : 'provider',
    providedPlatforms: 'provider'
  ]*/

  static mappedBy = [
          variantNames        : 'owner'
  ]

  static hasMany = [
    roles: RefdataValue,
    contacts: Contact,
    ids: Identifier,
    variantNames        : ComponentVariantName,
    curatoryGroups   : CuratoryGroupOrg
  ]

  static mapping = {
    includes KBComponent.mapping
    mission column: 'org_mission_fk_rv'
    homepage column: 'org_homepage'
    metadataDownloaderURL column: 'org_metadata_downloader_url', type: 'text'
    kbartDownloaderURL column: 'org_kbart_downloader_url', type: 'text'

    variantNames cascade: "all,delete-orphan", lazy: false
  }

  static constraints = {
    mission(nullable: true, blank: true)
    homepage(nullable: true, blank: true)
    metadataDownloaderURL(nullable: true, blank: true)
    kbartDownloaderURL(nullable: true, blank: true)
    name(validator: { val, obj ->
      if (obj.hasChanged('name')) {
        if (val && val.trim()) {
          def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
          def dupes = Org.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);
          if (dupes?.size() > 0 && dupes.any { it != obj }) {
            return ['notUnique']
          }
        } else {
          return ['notNull']
        }
      }
    })
  }

  static def refdataFind(params) {
    def result = [];
    def status_deleted = RDStore.KBC_STATUS_DELETED
    def status_filter = null

    if (params.filter1) {
      status_filter = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, params.filter1)
    }

    params.sort = 'name'

    def ql = null;
    ql = Org.findAllByNameIlikeAndStatusNotEqual("${params.q}%", status_deleted, params)

    if (ql) {
      ql.each { t ->
        if (!status_filter || t.status == status_filter) {
          result.add([id: "${t.class.name}:${t.id}", text: "${t.name}", status: "${t.status?.value}"])
        }
      }
    }

    result
  }

  @Override
  public String getNiceName() {
    return "Provider";
  }

  @Transient
  public String getDomainName() {
    return "Provider"
  }

  public String getShowName() {
    return this.name
  }

    @Transient
    public getCurrentTippCount() {
        def refdata_current = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Current')

        int result = 0
        if (getProvidedPackages()) {
            result = TitleInstancePackagePlatform.executeQuery("select count(t.id) from TitleInstancePackagePlatform as t where t.pkg in (:pkgs) and t.status = :status"
                    , [pkgs: getProvidedPackages(), status: refdata_current])[0]
        }

        result
    }

  @Transient
  def getProvidedPackages(){
    Package.executeQuery('select p from Package as p where provider = :provider', [provider: this])
  }

  @Transient
  def getProvidedPlatforms(){
    Platform.executeQuery('select p from Platform as p where provider = :provider', [provider: this])
  }
}

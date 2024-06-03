package wekb

import grails.plugins.orm.auditable.Auditable
import wekb.base.AbstractBase
import wekb.helper.RCConstants
import groovy.util.logging.Slf4j
import wekb.helper.RDStore

import javax.persistence.Transient

@Slf4j
class Org extends AbstractBase implements Auditable {


  String name
  String abbreviatedName

  RefdataValue status

  // Timestamps
  Date dateCreated
  Date lastUpdated

  String homepage

  String metadataDownloaderURL
  String kbartDownloaderURL

  String description

  boolean paperInvoice  = false
  boolean managementOfCredits  = false
  boolean processingOfCompensationPayments  = false
  boolean individualInvoiceDesign  = false

  boolean invoicingYourself  = false

  Set variantNames = []

  static mappedBy = [
  ]

  static hasMany = [
    roles: RefdataValue,
    contacts: Contact,
    ids: Identifier,
    variantNames        : ComponentVariantName,
    curatoryGroups   : CuratoryGroupOrg,
    electronicBillings: ProviderElectronicBilling,
    invoiceDispatchs: ProviderInvoiceDispatch,
    invoicingVendors: ProviderInvoicingVendor,
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
    metadataDownloaderURL column: 'org_metadata_downloader_url', type: 'text'
    kbartDownloaderURL column: 'org_kbart_downloader_url', type: 'text'

    variantNames cascade: "all,delete-orphan", lazy: false

    paperInvoice column: 'org_paper_invoice'
    managementOfCredits column: 'org_management_of_credits'
    processingOfCompensationPayments column: 'org_pro_of_com_pay'
    individualInvoiceDesign column: 'org_ind_invoice_design'

    description column: 'org_description', type: 'text'

    invoicingYourself column: 'org_invoicing_yourself'
  }

  static constraints = {
    abbreviatedName(nullable: true, blank: true)
    homepage(nullable: true, blank: true)
    metadataDownloaderURL(nullable: true, blank: true)
    kbartDownloaderURL(nullable: true, blank: true)
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

    description(nullable: true, blank: true)
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

  def afterUpdate() {
    if(this.curatoryGroups.size() == 1){
      String newName = this.getProperty('name')
      CuratoryGroup curatoryGroup = this.curatoryGroups.curatoryGroup[0]
      if(newName && newName != '' && curatoryGroup && newName != curatoryGroup.name){
        CuratoryGroup.executeUpdate("update CuratoryGroup as c set c.name = :newName where c = :curatoryGroup", [newName: newName, curatoryGroup: curatoryGroup])
      }

    }
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
    ql = Org.findAllByNameIlikeAndStatusNotEqual("%${params.q}%", status_deleted, params)

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
    return "Provider"
  }

  public String getShowName() {
    return this.name
  }

  String toString(){
    "${name ?: ''}".toString()
  }

    @Transient
    public getCurrentTippCount() {
        def refdata_current = RDStore.KBC_STATUS_CURRENT

        int result = 0
        if (getProvidedPackages()) {
            result = TitleInstancePackagePlatform.executeQuery("select count(*) from TitleInstancePackagePlatform as t where t.pkg in (:pkgs) and t.status = :status"
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

  @Transient
  public List<CuratoryGroup> getCuratoryGroupObjects() {
    List<CuratoryGroup> curatoryGroups
    if(this.curatoryGroups.size() > 0){
      curatoryGroups = this.curatoryGroups.curatoryGroup
    }
    return curatoryGroups
  }
}

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

  boolean webShopOrders
  boolean xmlOrders
  boolean ediOrders

  boolean paperInvoice
  boolean managementOfCredits
  boolean processingOfCompensationPayments
  boolean individualInvoiceDesign

  boolean technicalSupport
  boolean shippingMetadata
  boolean forwardingUsageStatisticsFromPublisher
  boolean activationForNewReleases
  boolean exchangeOfIndividualTitles
  String researchPlatformForEbooks

  boolean prequalificationVOL
  String prequalificationVOLInfo



  static mappedBy = [
  ]

  static hasMany = [
    roles: RefdataValue,
    contacts: Contact,
    curatoryGroups   : CuratoryGroupVendor,
    packages: PackageVendor,
    supportedLibrarySystems: VendorLibrarySystem,
    electronicBillings: VendorElectronicBilling,
    invoiceDispatchs: VendorInvoiceDispatch,
    electronicDeliveryDelays: VendorElectronicDeliveryDelay
  ]

  static mapping = {
    id column: 'ven_id'
    version column: 'ven_version'

    uuid column: 'ven_uuid'
    name column: 'ven_name'
    abbreviatedName column: 'ven_abbreviated_name'

    lastUpdated column: 'ven_last_updated'
    dateCreated column: 'ven_date_created'

    status column: 'ven_status_rv_fk'

    homepage column: 'ven_homepage'

    xmlOrders column: 'ven_xml_orders'
    ediOrders column: 'ven_edi_orders'
    webShopOrders column: 'web_shop_orders'

    paperInvoice column: 'ven_paper_invoice'
    managementOfCredits column: 'ven_management_of_credits'
    processingOfCompensationPayments column: 'ven_pro_of_com_pay'
    individualInvoiceDesign column: 'ven_ind_invoice_design'

    technicalSupport column: 'ven_technical_support'
    shippingMetadata column: 'ven_shipping_metadata'
    forwardingUsageStatisticsFromPublisher column: 'ven_forw_usage_stati_fr_pub'
    activationForNewReleases column: 'ven_activation_for_new_releases'
    exchangeOfIndividualTitles column: 'ven_exchange_of_ind_titles'
    researchPlatformForEbooks column: 'ven_research_platform_for_ebooks'

    prequalificationVOL column: 'ven_prequalification_vol'
    prequalificationVOLInfo column: 'ven_prequalification_vol_info', type: 'text'
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

    paperInvoice (nullable: true, blank: true)
    managementOfCredits (nullable: true, blank: true)
    processingOfCompensationPayments (nullable: true, blank: true)
    individualInvoiceDesign (nullable: true, blank: true)

    technicalSupport (nullable: true, blank: true)
    shippingMetadata (nullable: true, blank: true)
    forwardingUsageStatisticsFromPublisher (nullable: true, blank: true)
    activationForNewReleases (nullable: true, blank: true)
    exchangeOfIndividualTitles (nullable: true, blank: true)
    researchPlatformForEbooks (nullable: true, blank: true)

    xmlOrders (nullable: true, blank: true)
    ediOrders (nullable: true, blank: true)

    prequalificationVOL (nullable: true, blank: true)
    prequalificationVOLInfo (nullable: true, blank: true)

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

  @Transient
  int getProvidersCount(){

    Vendor.executeQuery("select count(*) from Org as o where o in (select p.provider from Package as p join p.vendors as vendor_pkg where vendor_pkg.vendor = :vendor)", [vendor: this])[0]

  }
}

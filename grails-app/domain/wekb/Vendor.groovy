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

  boolean webShopOrders  = false
  boolean xmlOrders  = false
  boolean ediOrders  = false
  boolean emailOrders  = false

  boolean paperInvoice  = false
  boolean managementOfCredits  = false
  boolean processingOfCompensationPayments  = false
  boolean individualInvoiceDesign  = false

  boolean technicalSupport  = false
  boolean shippingMetadata  = false
  boolean forwardingUsageStatisticsFromPublisher  = false
  boolean activationForNewReleases  = false
  boolean exchangeOfIndividualTitles  = false
  String researchPlatformForEbooks

  boolean prequalification = false
  String prequalificationInfo



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
    webShopOrders column: 'ven_web_shop_orders'
    emailOrders column: 'ven_email_orders'

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

    prequalification column: 'ven_prequalification'
    prequalificationInfo column: 'ven_prequalification_info', type: 'text'
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

    researchPlatformForEbooks (nullable: true, blank: true)
    prequalificationInfo (nullable: true, blank: true)

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
    ql = Vendor.findAllByNameIlikeAndStatusNotInList("%${params.q}%", [RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_REMOVED], params)

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
    return "Library Supplier"
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

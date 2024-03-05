package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants

class VendorElectronicBilling {

    Vendor vendor

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.VENDOR_ELECTRONIC_BILLING)
    RefdataValue electronicBilling

    static mapping = {
        id          column:'veb_id'
        version     column:'veb_version'
        electronicBilling     column:'veb_electronic_billing_rv_fk'
        vendor column:'veb_vendor_fk', index: 'veb_vendor_idx'

        dateCreated column: 'veb_date_created'
        lastUpdated column: 'veb_last_updated'
    }

    static constraints = {
        electronicBilling (nullable:true)
    }

    String getOID(){
        "${this.class.name}:${id}"
    }
}

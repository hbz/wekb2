package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants

class VendorElectronicDeliveryDelay {

    Vendor vendor

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.VENDOR_ELECTRONIC_DELIVERY_DELAY)
    RefdataValue electronicDeliveryDelay

    static mapping = {
        id          column:'vedd_id'
        version     column:'vedd_version'
        electronicDeliveryDelay     column:'vedd_electronic_delivery_delay_rv_fk'
        vendor column:'vedd_vendor_fk', index: 'vedd_vendor_idx'

        dateCreated column: 'vedd_date_created'
        lastUpdated column: 'vedd_last_updated'
    }

    static constraints = {
        electronicDeliveryDelay (nullable:true)
    }

    String getOID(){
        "${this.class.name}:${id}"
    }
}

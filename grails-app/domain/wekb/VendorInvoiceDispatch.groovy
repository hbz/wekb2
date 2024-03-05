package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants

class VendorInvoiceDispatch {

    Vendor vendor

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.VENDOR_INVOICE_DISPATCH)
    RefdataValue invoiceDispatch

    static mapping = {
        id          column:'vid_id'
        version     column:'vid_version'
        invoiceDispatch     column:'vid_invoice_dispatch_rv_fk'
        vendor column:'vid_vendor_fk', index: 'vid_vendor_idx'

        dateCreated column: 'vid_date_created'
        lastUpdated column: 'vid_last_updated'
    }

    static constraints = {
        invoiceDispatch (nullable:true)
    }

    String getOID(){
        "${this.class.name}:${id}"
    }
}

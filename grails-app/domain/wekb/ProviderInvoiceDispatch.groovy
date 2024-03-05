package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants

class ProviderInvoiceDispatch {

    Org provider

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.VENDOR_INVOICE_DISPATCH)
    RefdataValue invoiceDispatch

    static mapping = {
        id          column:'pid_id'
        version     column:'pid_version'
        invoiceDispatch     column:'pid_invoice_dispatch_rv_fk'
        provider column:'pid_provider_fk', index: 'pid_provider_idx'

        dateCreated column: 'pid_date_created'
        lastUpdated column: 'pid_last_updated'
    }

    static constraints = {
        invoiceDispatch (nullable:true)
        provider  (nullable:false)
    }

    String getOID(){
        "${this.class.name}:${id}"
    }
}

package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants

class ProviderElectronicBilling {

    Org provider

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.VENDOR_ELECTRONIC_BILLING)
    RefdataValue electronicBilling

    static mapping = {
        id          column:'peb_id'
        version     column:'peb_version'
        electronicBilling     column:'peb_electronic_billing_rv_fk'
        provider column:'peb_provider_fk', index: 'peb_provider_idx'

        dateCreated column: 'peb_date_created'
        lastUpdated column: 'peb_last_updated'
    }

    static constraints = {
        electronicBilling (nullable:true)
        provider  (nullable:false)
    }

    String getOID(){
        "${this.class.name}:${id}"
    }
}

package wekb

import wekb.helper.BeanStore

class ProviderInvoicingVendor {

    Org provider
    Vendor vendor

    // Timestamps
    Date dateCreated
    Date lastUpdated

    static mapping = {
        id column: 'piv_id'
        version column: 'piv_version'

        lastUpdated column: 'piv_last_updated'
        dateCreated column: 'piv_date_created'

        provider column: 'piv_provider_fk'
        vendor column: 'piv_vendor_fk'

    }

    static constraints = {
    }

    String getOID() {
        "${this.class.name}:${id}"
    }

    def afterInsert (){
        log.debug("afterSave for ${this}")
        BeanStore.getCascadingUpdateService().update(provider, lastUpdated)
    }

    def afterUpdate (){
        log.debug("afterUpdate for ${this}")
        BeanStore.getCascadingUpdateService().update(provider, lastUpdated)
    }
}

package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants

class VendorLibrarySystem {

    Vendor vendor

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.VENDOR_SUPPORTED_LIB_SYSTEM)
    RefdataValue supportedLibrarySystem

    static mapping = {
        id          column:'vls_id'
        version     column:'vls_version'
        supportedLibrarySystem     column:'vls_supported_library_system_rv_fk'
        vendor column:'vls_vendor_fk', index: 'vls_vendor_idx'

        dateCreated column: 'vls_date_created'
        lastUpdated column: 'vls_last_updated'
    }

    static constraints = {
        supportedLibrarySystem (nullable:true)
    }

    String getOID(){
        "${this.class.name}:${id}"
    }
}

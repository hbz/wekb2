package wekb

class PackageVendor {

    Package pkg
    Vendor vendor

    // Timestamps
    Date dateCreated
    Date lastUpdated

    static mapping = {
        id column: 'pv_id'
        version column: 'pv_version'

        lastUpdated column: 'pv_last_updated'
        dateCreated column: 'pv_date_created'

        pkg column: 'pv_pkg_fk'
        vendor column: 'pv_vendor_fk'

    }

    static constraints = {
    }

    String getOID() {
        "${this.class.name}:${id}"
    }
}

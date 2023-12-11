package wekb

class CuratoryGroupVendor {


    Vendor vendor
    CuratoryGroup curatoryGroup

    // Timestamps
    Date dateCreated
    Date lastUpdated


    static constraints = {
    }

    static mapping = {
        id column: 'cgv_id'
        version column: 'cgv_version'
        vendor column: 'cgv_vendor_fk', index: 'cgv_vendor_idx'
        curatoryGroup column: 'cgv_curatory_group_fk', index: 'cgv_curatory_group_idx'
        lastUpdated column: 'cgv_last_updated'
        dateCreated column: 'cgv_date_created'
    }


    public String getName() {
        return id
    }

    public String toString() {
        "(CuratoryGroupVendor ${id})".toString()
    }
}

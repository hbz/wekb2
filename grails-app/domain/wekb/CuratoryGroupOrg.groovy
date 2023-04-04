package wekb

class CuratoryGroupOrg {


    Org org
    CuratoryGroup curatoryGroup

    // Timestamps
    Date dateCreated
    Date lastUpdated


    static constraints = {
    }

    static mapping = {
        id column: 'cgo_id'
        version column: 'cgo_version'
        org column: 'cgo_org_fk', index: 'cgo_org_idx'
        curatoryGroup column: 'cgo_curatory_group_fk', index: 'cgo_curatory_group_idx'
        lastUpdated column: 'cgo_last_updated'
        dateCreated column: 'cgo_date_created'
    }


    public String getName() {
        return id
    }

    public String toString() {
        "(CuratoryGroupOrg ${id})".toString()
    }
}

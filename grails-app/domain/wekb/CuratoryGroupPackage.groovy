package wekb

class CuratoryGroupPackage {


    Package pkg
    CuratoryGroup curatoryGroup

    // Timestamps
    Date dateCreated
    Date lastUpdated


    static constraints = {
    }

    static mapping = {
        id column: 'cgp_id'
        version column: 'cgp_version'
        pkg column: 'cgp_pkg_fk', index: 'cgp_pkg_idx'
        curatoryGroup column: 'cgp_curatory_group_fk', index: 'cgp_curatory_group_idx'
        lastUpdated column: 'cgp_last_updated'
        dateCreated column: 'cgp_date_created'
    }


    public String getName() {
        return id
    }

    public String toString() {
        "(CuratoryGroupPackage ${id})".toString()
    }
}

package wekb

class CuratoryGroupPlatform {


    Platform platform
    CuratoryGroup curatoryGroup

    // Timestamps
    Date dateCreated
    Date lastUpdated


    static constraints = {
    }

    static mapping = {
        id column: 'cgpl_id'
        version column: 'cgpl_version'
        platform column: 'cgpl_platform_fk', index: 'cgpl_platform_idx'
        curatoryGroup column: 'cgpl_curatory_group_fk', index: 'cgpl_curatory_group_idx'
        lastUpdated column: 'cgpl_last_updated'
        dateCreated column: 'cgpl_date_created'
    }


    public String getName() {
        return id
    }

    public String toString() {
        "(CuratoryGroupPlatform ${id})".toString()
    }
}

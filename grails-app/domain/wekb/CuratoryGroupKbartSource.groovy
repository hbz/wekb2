package wekb

class CuratoryGroupKbartSource {


    KbartSource kbartSource
    CuratoryGroup curatoryGroup

    // Timestamps
    Date dateCreated
    Date lastUpdated


    static constraints = {
    }

    static mapping = {
        id column: 'cgks_id'
        version column: 'cgks_version'
        kbartSource column: 'cgks_kbart_source_fk', index: 'cgks_kbart_source_idx'
        curatoryGroup column: 'cgks_curatory_group_fk', index: 'cgks_curatory_group_idx'
        lastUpdated column: 'cgks_last_updated'
        dateCreated column: 'cgks_date_created'
    }


    public String getName() {
        return id
    }

    public String toString() {
        "(CuratoryGroupKbartSource ${id})".toString()
    }
}

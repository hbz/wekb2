package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants

class DeletedKBComponent {

    String uuid

    String name

    String componentType

    @RefdataAnnotation(cat = RCConstants.DELETED_COMPONENT_STATUS)
    RefdataValue status

    // Timestamps
    Date dateCreated
    Date lastUpdated

    Date oldDateCreated
    Date oldLastUpdated

    Long oldId


    static mapping = {
        id column: 'del_kbc_id'
        uuid column: 'del_kbc_uuid', type: 'text', index: 'del_kbc_uuid_idx'
        version column: 'del_kbc_version'
        name column: 'del_kbc_name', type: 'text', index: 'del_kbc_name_idx'
        status column: 'del_kbc_status_rv_fk', index: 'del_kbc_status_idx'
        dateCreated column: 'del_kbc_date_created'
        lastUpdated column: 'del_kbc_last_updated'
        oldDateCreated column: 'del_kbc_old_date_created'
        oldLastUpdated column: 'del_kbc_old_last_updated'
        oldId column: 'del_kbc_old_id'
        componentType column: 'del_kbc_component_type'

    }


    static constraints = {
        uuid(nullable: true, unique: true, blank: false, maxSize: 2048)
        name(nullable: true, blank: false, maxSize: 2048)
        status(nullable: true, blank: false)
    }

}

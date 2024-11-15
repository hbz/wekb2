package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants

class ContactLanguage {

    Contact contact

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.COMPONENT_LANGUAGE)
    RefdataValue language

    static mapping = {
        id          column:'cl_id'
        version     column:'cl_version'
        language     column:'cl_language_rv_fk'
        contact column:'cl_contact_fk', index: 'cl_contact_idx'

        dateCreated column: 'cl_date_created'
        lastUpdated column: 'cl_last_updated'
    }

    static constraints = {
    }

    String getOID(){
        "${this.class.name}:${id}"
    }
}

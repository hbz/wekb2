package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants

class PlatformFederation {

    Platform platform

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.PLATFORM_FEDERATION)
    RefdataValue federation

    static mapping = {
        id          column:'pf_id'
        version     column:'pf_version'
        federation     column:'pf_federation_rv_fk'
        platform column:'pf_platform_fk', index: 'pf_platform_idx'

        dateCreated column: 'pf_date_created'
        lastUpdated column: 'pf_last_updated'
    }

    static constraints = {
        federation (nullable:true)
        platform  (nullable:false)
    }

    String getOID(){
        "${this.class.name}:${id}"
    }
}

package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.BeanStore
import wekb.helper.RCConstants

class PackageArchivingAgency {

    Package pkg

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.PAA_ARCHIVING_AGENCY)
    RefdataValue archivingAgency

    @RefdataAnnotation(cat = RCConstants.PAA_OPEN_ACCESS)
    RefdataValue openAccess

    @RefdataAnnotation(cat = RCConstants.PAA_POST_CANCELLATION_ACCESS)
    RefdataValue postCancellationAccess

    static mapping = {
        id          column:'paa_id'
        version     column:'paa_version'
        archivingAgency     column:'paa_archiving_agency_rv_fk'
        openAccess column:'paa_open_access_rv_fk'
        postCancellationAccess  column:'paa_pca_rv_fk'
        pkg column:'paa_pkg_fk', index: 'paa_pkg_idx'

        dateCreated column: 'paa_date_created'
        lastUpdated column: 'paa_last_updated'
    }

    static constraints = {
        archivingAgency     (nullable:false)
        openAccess (nullable:true)
        postCancellationAccess (nullable:true)
        pkg  (nullable:false)
    }

    def afterInsert (){
        log.debug("afterSave for ${this}")
        BeanStore.getCascadingUpdateService().update(this, lastUpdated)

    }

    def beforeDelete (){
        log.debug("beforeDelete for ${this}")
        BeanStore.getCascadingUpdateService().update(this, lastUpdated)

    }

    def afterUpdate(){
        log.debug("afterUpdate for ${this}")
        BeanStore.getCascadingUpdateService().update(this, lastUpdated)

    }

    String getOID(){
        "${this.class.name}:${id}"
    }
}

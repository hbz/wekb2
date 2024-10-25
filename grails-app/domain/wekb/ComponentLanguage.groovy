package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.BeanStore
import wekb.helper.RCConstants

class ComponentLanguage {

    @RefdataAnnotation(cat = RCConstants.COMPONENT_LANGUAGE)
    RefdataValue language

    Date dateCreated
    Date lastUpdated

    static belongsTo = [
            tipp: TitleInstancePackagePlatform
    ]

    static constraints = {
        tipp(unique: ['tipp', 'language'])
        dateCreated (nullable: true)
        lastUpdated (nullable: true)
    }

    static mapping = {
        id                    column: 'cl_id'
        version               column: 'cl_version'
        language              column: 'cl_rv_fk' , index: 'cl_language_idx'
        tipp           column: 'cl_tipp_fk', index: 'cl_tipp_idx'
        dateCreated           column: 'cl_date_created'
        lastUpdated           column: 'cl_last_updated'
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

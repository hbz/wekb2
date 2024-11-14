package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.BeanStore
import wekb.helper.RCConstants

class TippLanguage {

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
        id                    column: 'tl_id'
        version               column: 'tl_version'
        language              column: 'tl_rv_fk' , index: 'tl_language_idx'
        tipp           column: 'tl_tipp_fk', index: 'tl_tipp_idx'
        dateCreated           column: 'tl_date_created'
        lastUpdated           column: 'tl_last_updated'
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

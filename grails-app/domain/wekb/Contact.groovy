package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants
import groovy.util.logging.Slf4j
import org.apache.commons.logging.LogFactory

@Slf4j
class Contact{


    def cascadingUpdateService

    String content
    Org org

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.CONTACT_CONTENT_TYPE)
    RefdataValue contentType

    @RefdataAnnotation(cat = RCConstants.CONTACT_TYPE)
    RefdataValue type

    @RefdataAnnotation(cat = RCConstants.KBCOMPONENT_LANGUAGE)
    RefdataValue language
    
    static mapping = {
        id          column:'ct_id'
        version     column:'ct_version'
        content     column:'ct_content'
        contentType column:'ct_content_type_rv_fk'
        type        column:'ct_type_rv_fk'
        org         column:'ct_org_fk', index: 'ct_org_idx'
        language    column:'ct_language_rv_fk'

        dateCreated column: 'ct_date_created'
        lastUpdated column: 'ct_last_updated'
    }
    
    static constraints = {
        content     (nullable:true)
        contentType (nullable:true)
        org         (nullable:false)
        language    (nullable:true)
    }
    
    @Override
    String toString() {
        contentType?.value + ', ' + content + ' (' + id + '); ' + type?.value
    }

    static Contact lookup(String content, RefdataValue contentType, RefdataValue type, Org organisation, RefdataValue language) {

        Contact contact
        List<Contact>  check = Contact.findAllWhere(
                content: content ?: null,
                contentType: contentType,
                type: type,
                org: organisation,
                language: language
        ).sort({id: 'asc'})

        if (check.size() > 0) {
            contact = check.get(0)
        }
        contact
    }

    static Contact lookupOrCreate(String content, RefdataValue contentType, RefdataValue type, Org organisation, RefdataValue language) {

        withTransaction {
            Contact result
            String info = "saving new contact: ${content} ${contentType} ${type} ${language}"

            if (!content) {
                LogFactory.getLog(this).debug(info + " > ignored; empty content")
                return
            }

            Contact check = Contact.lookup(content, contentType, type, organisation, language)
            if (check) {
                result = check
                info += " > ignored/duplicate"
            }
            else {
                result = new Contact(
                        content: content,
                        contentType: contentType,
                        type: type,
                        org: organisation,
                        language: language
                )

                if (! result.save()) {
                    result.errors.each { println it }
                }
                else {
                    info += " > OK"
                }
            }

            LogFactory.getLog(this).debug(info)
            result
        }
    }

    def afterInsert (){
        log.debug("afterSave for ${this}")
        cascadingUpdateService.update(this, dateCreated)

    }

    def beforeDelete (){
        log.debug("beforeDelete for ${this}")
        cascadingUpdateService.update(this, lastUpdated)

    }

    def afterUpdate(){
        log.debug("afterUpdate for ${this}")
        cascadingUpdateService.update(this, lastUpdated)

    }

}

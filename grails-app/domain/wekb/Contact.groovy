package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.BeanStore
import wekb.helper.RCConstants
import groovy.util.logging.Slf4j
import org.apache.commons.logging.LogFactory

@Slf4j
class Contact{

    String content
    Org org
    Vendor vendor

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.CONTACT_CONTENT_TYPE)
    RefdataValue contentType

    @RefdataAnnotation(cat = RCConstants.CONTACT_TYPE)
    RefdataValue type
    
    static mapping = {
        id          column:'ct_id'
        version     column:'ct_version'
        content     column:'ct_content'
        contentType column:'ct_content_type_rv_fk'
        type        column:'ct_type_rv_fk'
        org         column:'ct_org_fk', index: 'ct_org_idx'
        vendor         column:'ct_vendor_fk', index: 'ct_vendor_idx'

        dateCreated column: 'ct_date_created'
        lastUpdated column: 'ct_last_updated'
    }
    
    static constraints = {
        content     (nullable:true)
        contentType (nullable:true)
        org         (nullable:true)
        vendor      (nullable:true)
    }

    static hasMany = [
            languages           : ContactLanguage

    ]
    
    @Override
    String toString() {
        contentType?.value + ', ' + content + ' (' + id + '); ' + type?.value
    }

    static Contact lookup(String content, RefdataValue contentType, RefdataValue type, Org organisation) {

        Contact contact
        List<Contact>  check = Contact.findAllWhere(
                content: content ?: null,
                contentType: contentType,
                type: type,
                org: organisation
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

            Contact check = Contact.lookup(content, contentType, type, organisation)
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

                    new ContactLanguage(contact: result, language: language).save()
                }
            }

            LogFactory.getLog(this).debug(info)
            result
        }
    }

    static Contact lookup(String content, RefdataValue contentType, RefdataValue type, Vendor ven) {

        Contact contact
        List<Contact>  check = Contact.findAllWhere(
                content: content ?: null,
                contentType: contentType,
                type: type,
                vendor: ven
        ).sort({id: 'asc'})

        if (check.size() > 0) {
            contact = check.get(0)
        }
        contact
    }

    static Contact lookupOrCreate(String content, RefdataValue contentType, RefdataValue type, Vendor ven, RefdataValue language) {

        withTransaction {
            Contact result
            String info = "saving new contact: ${content} ${contentType} ${type} ${language}"

            if (!content) {
                LogFactory.getLog(this).debug(info + " > ignored; empty content")
                return
            }

            Contact check = Contact.lookup(content, contentType, type, ven)
            if (check) {
                result = check
                info += " > ignored/duplicate"
            }
            else {
                result = new Contact(
                        content: content,
                        contentType: contentType,
                        type: type,
                        vendor: ven
                )

                if (! result.save()) {
                    result.errors.each { println it }
                }
                else {
                    info += " > OK"
                    new ContactLanguage(contact: result, language: language).save()
                }
            }

            LogFactory.getLog(this).debug(info)
            result
        }
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

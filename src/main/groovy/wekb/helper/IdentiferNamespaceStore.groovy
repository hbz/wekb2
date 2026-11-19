package wekb.helper

import org.grails.orm.hibernate.cfg.GrailsHibernateUtil
import wekb.IdentifierNamespace
import wekb.RefdataValue

class IdentiferNamespaceStore {

    public static final TITLE_ID_NAMESPACE             = getIdentifierNamespace('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)


    static IdentifierNamespace getIdentifierNamespace(String value, RefdataValue targetTyp) {

        IdentifierNamespace namespace = IdentifierNamespace.findByValueAndTargetType(value, targetTyp)

        if (!namespace) {
            println "WARNING: No IdentifierNamespace found by IdentiferNamespaceStore for value:'${value}', targetTyp:'${targetTyp}'"
        }
        (IdentifierNamespace) GrailsHibernateUtil.unwrapIfProxy( namespace)
    }

}

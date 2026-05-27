package wekb.utils

import grails.core.GrailsClass
import grails.util.Holders
import groovy.util.logging.Slf4j

import java.lang.reflect.Field

@Slf4j
class CodeUtils {

    static Set<Class> getDomainClassesByAttributeAnnotation(Class annotationClass) {
        Set<Class> classes = []
        getAllDomainClasses().each { Class clazz ->
            clazz.declaredFields.each { Field field ->
                if(field.getAnnotation(annotationClass))
                    classes << clazz
            }
        }
        classes
    }

    static Set<Class> getAllDomainClasses() {
        getAllDomainArtefacts().collect { GrailsClass c -> c.clazz }
    }

    static Set<GrailsClass> getAllDomainArtefacts() {
        Set<GrailsClass> artefacts = []
        artefacts.addAll(Holders.grailsApplication.getArtefacts('Domain'))
        artefacts.sort { GrailsClass c -> c.clazz.simpleName }
    }

}

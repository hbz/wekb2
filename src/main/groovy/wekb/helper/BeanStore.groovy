package wekb.helper


import grails.gsp.PageRenderer
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugins.mail.MailService
import grails.util.Holders
import grails.web.mapping.UrlMappingsHolder
import groovy.transform.CompileStatic
import org.grails.plugins.web.taglib.ApplicationTagLib
import org.springframework.context.MessageSource
import wekb.AccessService
import wekb.CascadingUpdateService
import wekb.ESWrapperService
import wekb.GenericOIDService
import wekb.LaserService

import javax.sql.DataSource

@CompileStatic
class BeanStore {

    static def get(String bean) {
        Holders.grailsApplication.mainContext.getBean(bean)
    }

    // -- Grails --

    static ApplicationTagLib getApplicationTagLib() {
        Holders.grailsApplication.mainContext.getBean(ApplicationTagLib)
    }
    static DataSource getDataSource() {
        Holders.grailsApplication.mainContext.getBean('dataSource') as DataSource
    }
    static PageRenderer getGroovyPageRenderer() {
        Holders.grailsApplication.mainContext.getBean('groovyPageRenderer') as PageRenderer
    }
    static MessageSource getMessageSource() {
        MessageSource messageSource = Holders.grailsApplication.mainContext.getBean('messageSource') as MessageSource
        messageSource
    }
    static SpringSecurityService getSpringSecurityService() {
        Holders.grailsApplication.mainContext.getBean('springSecurityService') as SpringSecurityService
    }
    static UrlMappingsHolder getUrlMappingsHolder() {
        Holders.grailsApplication.mainContext.getBean('grailsUrlMappingsHolder') as UrlMappingsHolder
    }

    // -- wekb --

    static AccessService getAccessService() {
        Holders.grailsApplication.mainContext.getBean('accessService') as AccessService
    }

    static CascadingUpdateService getCascadingUpdateService() {
        Holders.grailsApplication.mainContext.getBean('cascadingUpdateService') as CascadingUpdateService
    }
    static ESWrapperService getESWrapperService() {
        Holders.grailsApplication.mainContext.getBean('ESWrapperService') as ESWrapperService
    }
    static GenericOIDService getGenericOIDService() {
        Holders.grailsApplication.mainContext.getBean('genericOIDService') as GenericOIDService
    }
    static MailService getMailService() {
        Holders.grailsApplication.mainContext.getBean('mailService') as MailService
    }

    static LaserService getLaserService() {
        Holders.grailsApplication.mainContext.getBean('laserService') as LaserService
    }
}

package wekb

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import wekb.tenant.TenantContext


class TenantInterceptor {

    SpringSecurityService springSecurityService

    TenantInterceptor() {
        match(controller: 'search')
        match(controller: 'public', action: 'index')
        match(controller: 'package', action: 'packageChangeHistory')
        match(controller: 'group')
        match(controller: 'admin')
    }


    boolean before() {
        def user = springSecurityService?.currentUser
        String tenantRole = ""

        String tenantWithLogin = Holders.grailsApplication.config.getProperty('wekb.tenantWithLogin', String)
        String tenantWithOutLogin = Holders.grailsApplication.config.getProperty('wekb.tenantWithOutLogin', String)

        if (!user && tenantWithOutLogin)
        {
            tenantRole = tenantWithOutLogin
        }
        else if(user && tenantWithLogin)
        {
            tenantRole = tenantWithLogin
        }else
        {
            log.warn("TenantInterceptor: Fallback -> No config for wekb.tenantWithLogin and wekb.tenantWithOutLogin set!")
            //Fallback
            Map dataSource = grailsApplication.config.getProperty('dataSource', Map) as Map
            tenantRole = dataSource.username
        }

        TenantContext.setTenant(tenantRole)

        return true
    }

    boolean after() { true }

    void afterView() {
        TenantContext.clear()
    }
}



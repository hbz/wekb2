import grails.plugin.springsecurity.SpringSecurityUtils
import wekb.custom.CustomMigrationCallbacks
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy

import wekb.custom.CustomAuthFailureHandler


// Place your Spring DSL code here
beans = {


    localeResolver(SessionLocaleResolver) {
        defaultLocale= new Locale('en')
    }


    // [ user counter ..
    sessionRegistry( SessionRegistryImpl )

    registerSessionAuthenticationStrategy( RegisterSessionAuthenticationStrategy, ref('sessionRegistry') )

    sessionFixationProtectionStrategy( SessionFixationProtectionStrategy )

    concurrentSessionControlAuthenticationStrategy( ConcurrentSessionControlAuthenticationStrategy, ref('sessionRegistry') ){
        maximumSessions = -1
        // exceptionIfMaximumExceeded = true
    }

    sessionAuthenticationStrategy( CompositeSessionAuthenticationStrategy, [
            ref('concurrentSessionControlAuthenticationStrategy'),
            ref('sessionFixationProtectionStrategy'),
            ref('registerSessionAuthenticationStrategy')
    ])

    /// after 2023-01

    // [ database migration plugin ..
    migrationCallbacks( CustomMigrationCallbacks ) {
        grailsApplication = ref('grailsApplication')
    }

    authenticationFailureHandler( CustomAuthFailureHandler ) {
        ConfigObject conf = SpringSecurityUtils.securityConfig

        redirectStrategy                = ref('redirectStrategy')
        defaultFailureUrl               = conf.failureHandler.defaultFailureUrl
        useForward                      = conf.failureHandler.useForward
        allowSessionCreation            = conf.failureHandler.allowSessionCreation
    }



}

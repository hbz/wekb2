
import wekb.custom.CustomMigrationCallbacks
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy


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

    // [ database migration plugin ..
    migrationCallbacks( CustomMigrationCallbacks ) {
        grailsApplication = ref('grailsApplication')
    }



}

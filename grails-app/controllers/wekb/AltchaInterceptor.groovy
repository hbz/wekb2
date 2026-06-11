package wekb

import grails.plugin.springsecurity.SpringSecurityService
import groovy.util.logging.Slf4j
import wekb.system.AltchaClient

@Slf4j
class AltchaInterceptor implements grails.artefact.Interceptor {

    SpringSecurityService springSecurityService

    /**
     * defines which controller calls should be caught up, in this case every controller
     */
    AltchaInterceptor() {
        matchAll()
            .excludes(uri: '/')
            .excludes(uri: '/public/aboutWekb')
            .excludes(uri: '/public/wcagPlainEnglish')
            .excludes(uri: '/public/wekbNews')
            .excludes(uri: '/robots.txt')
            .excludes(controller: 'altcha')
            .excludes(controller: 'api2')
            .excludes(controller: 'login')
            .excludes(controller: 'logout')
    }

    /**
     * Performs global checks in order to determine whether the call is valid or not
     * @return true if the request is valid, false otherwise
     */
    boolean before() {
//        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate")
//        response.setHeader("Pragma", "no-cache")
//        response.setHeader("Expires", "0")

        if (springSecurityService.isLoggedIn()) {
            // TODO
            // Implement if client is added with a login
            // see LogoutController

            // AltchaClient.renewClient(request)
        }
        else if (! AltchaClient.isValid(request)) {
            String origin = request.getRequestURI() + (request.getQueryString() ? ('?' + request.getQueryString()) : '')
            redirect(controller: 'altcha', action: 'prompt', params: [origin: origin])
            return false
        }
        return true
    }

    /**
     * Dummy method stub implementing abstract methods
     * @return true
     */
    boolean after() {
        return true
    }
}

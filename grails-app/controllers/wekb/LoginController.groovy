package wekb

/*
 * copy from springsecurity
 */


import grails.converters.JSON
import grails.core.GrailsClass
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugins.mail.MailService
import grails.util.Holders
import grails.web.Action
import grails.web.mapping.UrlMappingInfo
import grails.web.mapping.UrlMappingsHolder
import org.springframework.context.MessageSource
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.AuthenticationTrustResolver
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import org.springframework.security.web.savedrequest.DefaultSavedRequest
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import wekb.auth.User
import wekb.helper.BeanStore
import wekb.utils.PasswordUtils

import javax.servlet.http.HttpServletResponse

@Secured('permitAll')
class LoginController {

    MailService mailService

    /** Dependency injection for the authenticationTrustResolver. */
    AuthenticationTrustResolver authenticationTrustResolver

    /** Dependency injection for the springSecurityService. */
    def springSecurityService

    /** Dependency injection for the messageSource. */
    MessageSource messageSource

    /** Default action; redirects to 'defaultTargetUrl' if logged in, /login/auth otherwise. */
    def index() {
        if (springSecurityService.isLoggedIn()) {
            redirect uri: conf.successHandler.defaultTargetUrl
        }
        else {
            redirect action: 'auth', params: params
        }
    }

    /** Show the login page. */
    def auth() {

        def conf = getConf()

        if (springSecurityService.isLoggedIn()) {
            redirect uri: conf.successHandler.defaultTargetUrl
            return
        }

        DefaultSavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response) as DefaultSavedRequest
        if (savedRequest) {
            log.info 'The original request was saved as: ' + savedRequest.getRequestURL()

            boolean fuzzyCheck = _fuzzyCheck(savedRequest)
            if (!fuzzyCheck) {
                String url = savedRequest.getRequestURL() + (savedRequest.getQueryString() ? '?' + savedRequest.getQueryString() : '')
                log.warn 'Login failed from ' + request.getRemoteAddr() + ' for ' + url + ' ---> ' + savedRequest.getHeaderNames().findAll{
                    it in ['host', 'referer', 'cookie', 'user-agent']
                }.collect{it + ': ' + savedRequest.getHeaderValues( it ).join(', ')}

            }
        }

        String postUrl = request.contextPath + conf.apf.filterProcessesUrl
        render view: 'auth', model: [postUrl: postUrl,
                                     rememberMeParameter: conf.rememberMe.parameter,
                                     usernameParameter: conf.apf.usernameParameter,
                                     passwordParameter: conf.apf.passwordParameter,
                                     gspLayout: conf.gsp.layoutAuth]
    }

    /** The redirect action for Ajax requests. */
    def authAjax() {
        response.setHeader 'Location', conf.auth.ajaxLoginFormUrl
        render(status: HttpServletResponse.SC_UNAUTHORIZED, text: 'Unauthorized')
    }

    /** Show denied page. */
    def denied() {
        if (springSecurityService.isLoggedIn() && authenticationTrustResolver.isRememberMe(authentication)) {
            // have cookie but the page is guarded with IS_AUTHENTICATED_FULLY (or the equivalent expression)
            redirect action: 'full', params: params
            return
        }

        [gspLayout: conf.gsp.layoutDenied]
    }

    /** Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page. */
    def full() {
        def conf = getConf()
        render view: 'auth', params: params,
                model: [hasCookie: authenticationTrustResolver.isRememberMe(authentication),
                        postUrl: request.contextPath + conf.apf.filterProcessesUrl,
                        rememberMeParameter: conf.rememberMe.parameter,
                        usernameParameter: conf.apf.usernameParameter,
                        passwordParameter: conf.apf.passwordParameter,
                        gspLayout: conf.gsp.layoutAuth]
    }

    /** Callback after a failed login. Redirects to the auth page with a warning message. */
    def authfail() {

        String msg = ''
        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
        if (exception) {
            if (exception instanceof AccountExpiredException) {
                msg = messageSource.getMessage('springSecurity.errors.login.expired', null, "Account Expired", request.locale)
            } else if (exception instanceof CredentialsExpiredException) {
                msg = messageSource.getMessage('springSecurity.errors.login.passwordExpired', null, "Password Expired", request.locale)
            } else if (exception instanceof DisabledException) {
                msg = messageSource.getMessage('springSecurity.errors.login.disabled', null, "Account Disabled", request.locale)
            } else if (exception instanceof LockedException) {
                msg = messageSource.getMessage('springSecurity.errors.login.locked', null, "Account Locked", request.locale)
            } else if (exception instanceof SessionAuthenticationException) {
                msg = messageSource.getMessage('springSecurity.errors.login.max.sessions.exceeded', null, "Sorry, you have exceeded your maximum number of open sessions.", request.locale)
            } else {
                msg = messageSource.getMessage('springSecurity.errors.login.fail', null, "Authentication Failure", request.locale)
            }
        }

        if (springSecurityService.isAjax(request)) {
            render([error: msg] as JSON)
        } else {
            flash.message = msg
            redirect action: 'auth', params: params
        }
    }

    /** The Ajax success redirect url. */
    def ajaxSuccess() {
        render([success: true, username: authentication.name] as JSON)
    }

    /** The Ajax denied redirect url. */
    def ajaxDenied() {
        render([error: 'access denied'] as JSON)
    }

    protected Authentication getAuthentication() {
        SecurityContextHolder.context?.authentication
    }

    protected ConfigObject getConf() {
        SpringSecurityUtils.securityConfig
    }

    @Transactional
    def resetForgottenPassword() {
        boolean emailSent = false
        if(!params.fgp_username) {
            flash.error = g.message(code:'forgottenPassword.userMissing') as String
        }
        else {
            User user = User.findByUsername(params.fgp_username)
            if (user) {
                if(!user.email) {
                    flash.error = g.message(code:'forgottenPassword.noEmail') as String
                }else{
                    String newPassword = PasswordUtils.getRandomUserPassword()
                    user.password = newPassword
                    user.accountLocked = false
                    user.invalidLoginAttempts = 0
                    if (user.save()) {
                        try {
                            mailService.sendMail {
                                to user.email
                                from 'wekb@hbz-nrw.de'
                                subject grailsApplication.config.getProperty('systemId', String) + ' - Password Reset'
                                body (view: '/mailTemplate/text/resetPassword', model: [newPassword: newPassword])
                            }
                            emailSent = true
                        }
                        catch (Exception e) {
                            emailSent = false
                            println "Unable to perform email due to exception ${e.message}"
                            flash.error = g.message(code:'forgottenPassword.userError') as String
                        }
                    }
                }
            }
            else flash.error = g.message(code:'forgottenPassword.userError') as String
        }
        redirect action: 'forgotPassword', params: [emailSent: emailSent]
    }

    def forgotPassword() {
        def result = [:]
        result.emailSent = params.emailSent

        result
    }

    private boolean _fuzzyCheck(DefaultSavedRequest savedRequest) {

        if (!savedRequest) {
            return true
        }
        boolean validRequest = false


        UrlMappingsHolder urlMappingsHolder = BeanStore.getUrlMappingsHolder()
        List<UrlMappingInfo> mappingInfo = urlMappingsHolder.matchAll(savedRequest.getRequestURI())
        if (mappingInfo) {
            GrailsClass controller = Holders.grailsApplication.getArtefacts('Controller').toList().sort{ it.clazz.simpleName }.find {
                it.clazz.simpleName == mappingInfo.first().getControllerName().capitalize() + 'Controller'
            }
            if (controller) {
                if (controller.clazz.declaredMethods.find { it.getAnnotation(Action) && it.name == mappingInfo.first().getActionName() }) {
                    validRequest = true
                }
            }
        }
        validRequest
    }
}

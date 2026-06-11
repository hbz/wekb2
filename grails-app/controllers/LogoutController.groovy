import grails.plugin.springsecurity.SpringSecurityUtils
import wekb.annotations.AltchaAnnotation
import wekb.system.AltchaClient

@AltchaAnnotation(comment = AltchaAnnotation.ACCESS_ALLOWED)
class LogoutController {

	/**
	 * Index action. Redirects to the Spring security logout uri.
	 */
	def index = {
		// TODO
		// Implement if client is added with a login
		// see AltchaInterceptor

//		String ch = AltchaClient.getClientHash(request)
//		AltchaClient ac = AltchaClient.findByClient(ch)
//		AltchaClient.removeExpiredClient(ac)

		redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl // '/j_spring_security_logout'
	}
}

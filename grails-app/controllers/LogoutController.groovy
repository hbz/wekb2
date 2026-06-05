import grails.plugin.springsecurity.SpringSecurityUtils
import wekb.system.AltchaClient

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

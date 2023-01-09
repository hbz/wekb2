package gokb



import org.springframework.security.access.annotation.Secured


@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
class FrontendController {


  def index() {
    log.debug("frontend::${params}")
    def result = [:]
    result
  }
  def tabExample() {
    render template: "/frontend/loremIpsum"
  }
}

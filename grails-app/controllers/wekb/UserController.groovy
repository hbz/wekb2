package wekb

import org.springframework.security.access.annotation.Secured
import wekb.auth.User

@Secured(['IS_AUTHENTICATED_FULLY'])
class UserController {

  UserProfileService userProfileService

  @Secured(['ROLE_ADMIN'])
  def delete() {
    log.debug("Deleting user ${params.id} ..")
    userProfileService.delete(User.get(params.id))
    redirect(controller: 'user', action: 'search')
  }
}

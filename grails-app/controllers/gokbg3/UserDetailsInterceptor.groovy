package gokbg3

import org.gokb.cred.*
import wekb.GlobalSearchTemplatesService;

class UserDetailsInterceptor {

  GlobalSearchTemplatesService globalSearchTemplatesService

  public UserDetailsInterceptor() {
    match(controller: 'admin')
    match(controller: 'component')
    match(controller: 'create')
    match(controller: 'fwk')
    match(controller: 'group')
    match(controller: 'home')
    match(controller: 'packages')
    match(controller: 'resource')
    match(controller: 'savedItems')
    match(controller: 'search')
    match(controller: 'user')
    match(controller: 'workflow')
  }


  def springSecurityService
  def displayTemplateService

    boolean before() { 

      log.debug("User details filter...");

      def user = springSecurityService.getCurrentUser()

      log.debug("UserDetailsInterceptor::before(${params}) ${request.getRequestURL()}")

      if (user) {
        log.debug("User details filter... User present");
        request.user = user

        // Get user curatorial groups
        if ( ! session.curatorialGroups ) {
          session.curatorialGroups = [];
          request.user.curatoryGroups.each { cg ->
            session.curatorialGroups.add([id:cg.id, name:cg.name]);
          }
        }

        if ( session.userPereferences == null ) {
          //log.debug("Set up user prefs");
          session.userPereferences = request.user.getUserPreferences()
        }
        else {
        }
      }
      else {
        log.debug("No user present..")
      }
      log.debug("Return true");
      true 
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}

package wekb.utils

import grails.gorm.transactions.Transactional
import wekb.auth.User

class LoginService {

    @Transactional
    void updateLastLogin(String username) {

        User.withNewTransaction  {

            User user = User.findByUsername(username)

            if (!user) {
                log.warn("User not found: ${username}")
                return
            }

            user.lastLogin = new Date()
            user.invalidLoginAttempts = 0
            user.save(flush: true, failOnError: true)

            log.debug("Last login updated for ${username}")
        }
    }
}

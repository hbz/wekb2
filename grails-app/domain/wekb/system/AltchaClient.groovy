package wekb.system

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import javax.servlet.http.HttpServletRequest
import java.time.LocalDate

class AltchaClient {

  public static final String CLIENT_SALT  = "CLIENT_SALT"
  public static final int KEEP_ALIVE      = 24 // hours

  static Log static_logger = LogFactory.getLog(AltchaClient)

  String client
  Long expiry

  Date dateCreated
  Date lastUpdated

  static mapping = {
    id          column: 'ac_id'
    version     column: 'ac_version'
    client      column: 'ac_client'
    expiry      column: 'ac_expiry'

    dateCreated column: 'ac_date_created'
    lastUpdated column: 'ac_last_updated'
  }

  static constraints = {
    client      unique: true
  }

  static void addNewClient(HttpServletRequest request) {
    static_logger.debug('addNewClient')

    withTransaction {
      AltchaClient ac = new AltchaClient(
              client: getClientHash(request),
              expiry: new Date().getTime() + (24 * 3600 * 1000)
      )
      ac.save()
    }
  }
  static void removeExpiredClient(AltchaClient ac) {
    static_logger.debug('removeExpiredClient')

    withTransaction {
      if (ac) {
        ac.delete()
      }
    }
  }

  static boolean isValid(HttpServletRequest request) {
    String ch = getClientHash(request)
    AltchaClient ac = findByClient(ch)

    if (ac) {
      if (ac.expiry > new Date().getTime()) {
        static_logger.debug('client is valid: ' + ch)
        return true
      }
      else {
        static_logger.debug('client expired: ' + ch)
        removeExpiredClient(ac)
        return false
      }
    }

    static_logger.debug('no client found')
    return false
  }

  static String getClientHash(HttpServletRequest request) {
    (CLIENT_SALT + request.getRemoteAddr()).md5().toUpperCase()
  }

  static String getWidgetToken(HttpServletRequest request) {
    (CLIENT_SALT + request.getRemoteAddr() + LocalDate.now().getDayOfYear()).md5().toUpperCase() // TODO
  }
}


package wekb.auth


import groovy.util.logging.Slf4j
import wekb.RefdataValue

import javax.persistence.Transient
import java.lang.reflect.Field

@Slf4j
class User {

  // Used in user import to bypass password encoding - used to directly load hashes instead of password
  transient direct_password = false

  String username
  String password
  String email
  boolean enabled
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired
  Long defaultPageSize = new Long(10)

  // When did the alerting system last check things on behalf of this user
  Date last_alert_check

  // seconds user wants between checks - System only checks daily, so values < 24*60*60 don't make much sense at the moment
  Long alert_check_frequency

  RefdataValue send_alert_emails
  RefdataValue showQuickView
  RefdataValue showInfoIcon

  // used by @gokbg3.RestMappingService.selectJsonLabel
  public static final String jsonLabel = "username"

  static hasMany = [

  ]

  static mappedBy = [curatoryGroups: "users"]

  static constraints = {
    username(blank: false, unique: true)
    password(blank: false)
    showQuickView(blank: true, nullable:true)
    email(blank: true, nullable:true)
    defaultPageSize(blank: true, nullable:true)
    last_alert_check(blank: false, nullable:true)
    alert_check_frequency(blank: false, nullable:true)
    send_alert_emails(blank: false, nullable:true)
  }

  static mapping = {
    password column: '`password`'
  }

  String getLogEntityId() {
      "${this.class.name}:${id}"
  }

  Set<Role> getAuthorities() {
    UserRole.findAllByUser(this).collect { it.role } as Set
  }

  public transient boolean hasRole (String roleName) {

    Role role = Role.findByAuthority("${roleName}")

    if (role != null) {
      return getAuthorities().contains(role)
    } else {
      log.error( "Error loading admin role (${role})" )
    }

    // Default to false.
    false
  }

  transient boolean isAdmin() {
    Role adminRole = Role.findByAuthority("ROLE_ADMIN")

    if (adminRole != null) {
      return getAuthorities().contains(adminRole)
    } else {
      log.error( "Error loading admin role (ROLE_ADMIN)" )
    }
    false
  }

  transient boolean getAdminStatus() {
    Role role = Role.findByAuthority("ROLE_ADMIN")

    if (role != null) {
      return getAuthorities().contains(role)
    } else {
      log.error( "Error loading admin role (ROLE_EDITOR)" )
    }
    false
  }

  transient boolean getSuperUserStatus() {
    Role role = Role.findByAuthority("ROLE_SUPERUSER")

    if (role != null) {
      return getAuthorities().contains(role)
    } else {
      log.error( "Error loading admin role (ROLE_SUPERUSER)" )
    }
    false
  }

  transient boolean getEditorStatus() {
    Role role = Role.findByAuthority("ROLE_EDITOR")

    if (role != null) {
      return getAuthorities().contains(role)
    } else {
      log.error( "Error loading admin role (ROLE_EDITOR)" )
    }
    false
  }

  transient boolean getContributorStatus() {
    Role role = Role.findByAuthority("ROLE_CONTRIBUTOR")

    if (role != null) {
      return getAuthorities().contains(role)
    } else {
      log.error( "Error loading admin role (ROLE_CONTRIBUTOR)" )
    }
    false
  }

  transient boolean getApiUserStatus() {
    Role role = Role.findByAuthority("ROLE_API")

    if (role != null) {
      return getAuthorities().contains(role)
    } else {
      log.error( "Error loading admin role (ROLE_API)" )
    }
    false
  }

  def beforeInsert() {
  }

  def beforeUpdate() {
  }

//   @Override
//   public boolean equals(Object obj) {
//
//     log.debug("USER::equals ${obj?.class?.name} :: ${obj}")
//     if ( obj != null ) {
//
//       def o = obj
//
//       if ( o instanceof HibernateProxy) {
//         o = User.deproxy(o)
//       }
//
//       if ( o instanceof User ) {
//         return getId() == obj.getId()
//       }
//     }
//
//     // Return false if we get here.
//     false
//   }

  transient def getUserPreferences() {
    def userPrefs = [:]

    // Use the available meta methods to get a list of all the properties against the user.
    // If they are of type refdata/and are set then we add here. If they are null then we should omit.
    def props = User.declaredFields.grep { !it.synthetic }
    for (Field p : props) {
      if (p.type == RefdataValue.class) {
        // Let's get the value.

        def val = this."${p.name}"
        if (val) {
          userPrefs["${p.name}"] = val.value?.equalsIgnoreCase("Yes") ? true : false
        }
      }
    }

    // Return the prefs.
    userPrefs
  }

  static def refdataFind(params) {
    def result = [];
    def ql = null;
    // ql = RefdataValue.findAllByValueIlikeOrDescriptionIlike("%${params.q}%","%${params.q}%",params)
    // ql = RefdataValue.findWhere("%${params.q}%","%${params.q}%",params)

    def query = "from User as u where (lower(u.username) like ? or lower(u.displayName) like ?) and enabled is true"
    def query_params = ["%${params.q.toLowerCase()}%","%${params.q.toLowerCase()}%"]

    ql = User.findAll(query, query_params, params)

    if ( ql ) {
      ql.each { id ->
        result.add([id:"${id.class.name}:${id.id}",text:"${id.username}${id.displayName && id.displayName.size() > 0 ? ' / '+ id.displayName : ''}"])
      }
    }

    result
  }

  public String toString() {
    return "${username}${displayName && displayName.size() > 0 ? ' / '+ displayName : ''}".toString();
  }

  public String getNiceName() {
    return "User";
  }

  int getDefaultPageSizeAsInteger() {
    long value = defaultPageSize ?: 10
    return value.intValue()
  }

  @Transient
  public String getDomainName() {
    return "User"
  }

}

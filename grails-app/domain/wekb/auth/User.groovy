package wekb.auth


import groovy.util.logging.Slf4j
import wekb.CuratoryGroup
import wekb.CuratoryGroupUser
import wekb.KBComponent
import wekb.RefdataValue
import wekb.helper.BeanStore
import wekb.system.SavedSearch

import javax.persistence.Transient
import java.lang.reflect.Field

@Slf4j
class User {

  // Timestamps
  Date dateCreated
  Date lastUpdated

  String displayName
  String username
  String password
  String email


  boolean enabled
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired

  Long defaultPageSize = new Long(10)

  static hasMany      = [ roles: UserRole,
                          curatoryGroupUsers : CuratoryGroupUser]

  static mappedBy     = [ roles: 'user',  curatoryGroupUsers: 'user' ]


  static constraints = {
    username(blank: false, unique: true)
    password(blank: false)
    email(blank: true, nullable:true)
    defaultPageSize(blank: true, nullable:true)
    displayName (blank: true, nullable:true)
  }

  static mapping = {
    table           name: '`user`'
    password        column: '`password`'

    id              column: 'usr_id'
    version         column: 'usr_version'

    accountExpired  column: 'usr_account_expired'
    accountLocked   column: 'usr_account_locked'
    displayName         column: 'usr_display_name'
    email           column: 'usr_email'
    enabled         column: 'usr_enabled'
    password        column: 'usr_password'
    passwordExpired column: 'usr_password_expired'
    username        column: 'usr_username'

    defaultPageSize column: 'usr_default_page_size'

    lastUpdated     column: 'usr_last_updated'
    dateCreated     column: 'usr_date_created'

  }

  String getOID() {
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

  transient boolean getUserStatus() {
    Role role = Role.findByAuthority("ROLE_USER")

    if (role != null) {
      return getAuthorities().contains(role)
    } else {
      log.error( "Error loading admin role (ROLE_USER)" )
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

  void beforeInsert() {
    _encodePassword()
  }

  void beforeUpdate() {
    if (isDirty('password')) {
      _encodePassword()
    }
  }

  /**
   * Encodes the submitted password
   */
  private void _encodePassword() {
    password = BeanStore.getSpringSecurityService().encodePassword(password)
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

  public String getShowName() {
    return this.username
  }

  boolean deleteUser(){
    log.debug("Deleting user ${this.id} ..")

      SavedSearch.executeUpdate("delete from SavedSearch where owner = :utd", [utd: this])
      UserRole.removeAll(this)

      log.debug("Deleting user object ..")
      this.delete()

      log.debug("Done")

  }

}

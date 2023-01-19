package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants
import wekb.auth.User
import wekb.helper.RDStore

import javax.persistence.Transient

class CuratoryGroup extends KBComponent {

  static belongsTo = User

  User owner

  @RefdataAnnotation(cat = RCConstants.CURATORY_GROUP_TYPE)
  RefdataValue type

  static hasMany = [
    users: User
  ]

  static mapping = {
    includes KBComponent.mapping
    type column: 'cg_type_rv_fk'
  }

  static manyByCombo = [
    packages: Package,
    platforms: Platform,
    orgs: Org,
    sources: Source
  ]

  static mappedByCombo = [
    packages: 'curatoryGroups',
    platforms: 'curatoryGroups',
    orgs: 'curatoryGroups',
    sources: 'curatoryGroups'
  ]

  static constraints = {
    owner (nullable:true, blank:false)
    name (validator: { val, obj ->
      if (obj.hasChanged('name')) {
        if (val && val.trim()) {
          def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
          def dupes = CuratoryGroup.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);

          if (dupes?.size() > 0 && dupes.any { it != obj }) {
            return ['notUnique']
          }
        } else {
          return ['notNull']
        }
      }
    })
    type (nullable:true, blank:false)
  }

  @Override
  public String getNiceName() {
    return "Curatory Group";
  }

  static def refdataFind(params) {
    def result = [];
    def status_deleted = RDStore.KBC_STATUS_DELETED
    def ql = null;

    params.sort = 'name'

    ql = CuratoryGroup.findAllByNameIlikeAndStatusNotEqual("${params.q}%", status_deleted ,params)

    ql.each { t ->
        result.add([id:"${t.class.name}:${t.id}", text:"${t.name}", status:"${t.status?.value}"])
    }

    result
  }

  def beforeInsert() {
    def user = springSecurityService?.currentUser
    this.owner = user

    this.generateShortcode()
    this.generateNormname()
    //this.generateComponentHash()
    this.generateUuid()
    this.ensureDefaults()
  }

  @Transient
  def availableActions() {
    [
            [code: 'method::deleteSoft', label: 'Delete Curatory Group', perm: 'delete'],
    ]
  }

  public void deleteSoft(context) {
    // Call the delete method on the superClass.
    super.deleteSoft(context)
  }

  @Transient
  public String getDomainName() {
    return "Curatory Group"
  }

}


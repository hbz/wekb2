package wekb.system


import groovy.json.JsonSlurper
import wekb.auth.User

class SavedSearch {

  String name
  User owner
  String searchDescriptor

  Date dateCreated
  Date lastUpdated

  static constraints = {
    name blank: false, nullable:false
    owner blank: false, nullable: false
    searchDescriptor blank: false, nullable:false
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }

  static mapping = {
    id column: 'ss_id'
    version column: 'ss_version'
    name column: 'ss_name'
    owner column: 'ss_owner_fk'
    searchDescriptor column: 'ss_search_descriptor', type:'text'

    dateCreated column: 'ss_date_created'
    lastUpdated column: 'ss_last_updated'
  }

  public def toParam() {
    def jsonSlurper = new JsonSlurper().parseText(searchDescriptor)
    return jsonSlurper
  }

}

package wekb

class ComponentStatistic {

  String componentType
  int year
  int month
  Long numTotal
  Long numNew

  Date dateCreated
  Date lastUpdated

  static constraints = {
    componentType(nullable:false, blank:false)
    year(nullable:false, blank:false)
    month(nullable:false, blank:false)
    numTotal(nullable:false, blank:true)
    numNew(nullable:false, blank:true)

    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }

  static mapping = {
    id  column:'cs_id'
    version column:'cs_version'

    componentType column:'cs_component_type'
    year column:'cs_year'
    month column:'cs_month'

    numTotal column:'cs_num_total'
    numNew column:'cs_num_new'

    dateCreated column:'cs_date_created'
    lastUpdated column:'cs_last_updated'
  }

  String getOID(){
    "${this.class.name}:${id}"
  }

}

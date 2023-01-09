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
    dateCreated column:'cs_date_created'
    lastUpdated column:'cs_last_updated'
  }

}

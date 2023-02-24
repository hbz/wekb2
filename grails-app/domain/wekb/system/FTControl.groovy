package wekb.system;

class FTControl {

  String domainClassName
  String activity
  Long lastTimestamp
  Long lastId

  static constraints = {
    lastId (nullable:true, blank:false)
  }

  String getOID(){
    "${this.class.name}:${id}"
  }
}


package wekb.system

import grails.converters.JSON
import wekb.CuratoryGroup
import wekb.KBComponent
import wekb.RefdataValue

import javax.persistence.Transient

class JobResult {

  public static final String STATUS_OK        = 'OK'
  public static final String STATUS_ERROR     = 'ERROR'
  public static final String STATUS_SUCCESS   = 'SUCCESS'
  public static final String STATUS_FAIL      = 'FAIL'

  String uuid
  String description
  String statusText
  String resultObject
  RefdataValue type
  Long ownerId
  Long groupId
  Date startTime
  Date endTime
  Long linkedItemId

  Date dateCreated
  Date lastUpdated


  static mapping = {
    uuid column: 'jr_uuid'
    description column: 'jr_description', type: 'text'
    statusText column: 'jr_status_text'
    resultObject column: 'jr_result_json', type: 'text'
    type column: 'jr_type_rv_fk'
    ownerId column: 'jr_owner_fk'
    groupId column: 'jr_group_fk'
    startTime column: 'jr_start_time'
    endTime column: 'jr_end_time'
    linkedItemId column: 'jr_linked_item_fk'

    dateCreated column: 'jr_date_created'
    lastUpdated column: 'jr_last_updated'
  }

  static constraints = {
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }

  def afterInsert() {
    if (!uuid) {
      uuid = UUID.randomUUID().toString()
    }
  }

  def getResultJson() {
    def result = null
    if (resultObject && resultObject.length() > 0 ) {
      result = JSON.parse(resultObject);
    }
    result;
  }

  CuratoryGroup getCuratoryGroup() {
    CuratoryGroup curatoryGroup = null
    if (groupId) {
      curatoryGroup = CuratoryGroup.get(groupId)
    }

    return curatoryGroup
  }

  KBComponent getLinkedItem() {
    KBComponent kbComponent = null
    if (linkedItemId) {
      kbComponent = KBComponent.get(linkedItemId)
    }

    return kbComponent
  }

  @Transient
  public String getDomainName() {
    return "Job Info"
  }

  public String getShowName() {
    return this.id
  }
}

package wekb

import wekb.annotations.RefdataAnnotation
import wekb.base.AbstractBase
import wekb.helper.BeanStore
import wekb.helper.RCConstants

class TIPPCoverageStatement extends AbstractBase {

  TitleInstancePackagePlatform tipp

  Date startDate
  Date endDate
  String startVolume
  String startIssue
  String endVolume
  String endIssue

  String embargo
  String coverageNote

  Date dateCreated
  Date lastUpdated

  @RefdataAnnotation(cat = RCConstants.TIPP_COVERAGE_DEPTH)
  RefdataValue coverageDepth

  static belongsTo = [
    tipp: TitleInstancePackagePlatform
  ]

  static mapping = {
    id column: 'tcs_id'
    version column: 'tcs_version'
    uuid column: 'tcs_uuid'
    tipp column: 'tcs_tipp_fk', index: 'tcs_tipp_idx'
    startDate column:'tcs_start_date', index: 'tcs_start_date_idx'
    startVolume column:'tcs_start_volume'
    startIssue column:'tcs_start_issue'
    endDate column:'tcs_end_date', index: 'tcs_end_date_idx'
    endVolume column:'tcs_end_volume'
    endIssue column:'tcs_end_issue'
    embargo column:'tcs_embargo'
    coverageNote column:'tcs_note',type: 'text'
    coverageDepth column:'tcs_depth'

    dateCreated column:'tcs_date_created'
    lastUpdated column:'tcs_last_updated'
  }

  static constraints = {
    startDate (nullable:true, blank:true)
    startVolume (nullable:true, blank:true)
    startIssue (nullable:true, blank:true)
    endDate (validator: { val, obj ->
      if(obj.startDate && val && (obj.hasChanged('endDate') || obj.hasChanged('startDate')) && obj.startDate > val) {
        return ['endDate.endPriorToStart']
      }
    }, nullable:true, blank:true)
    endVolume (nullable:true, blank:true)
    endIssue (nullable:true, blank:true)
    embargo (nullable:true, blank:true)
    coverageNote (nullable:true, blank:true)
    coverageDepth (nullable:true, blank:true)

    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)

    tipp (nullable:true, blank:false)
  }

  def afterInsert (){
    log.debug("afterSave for ${this}")
    BeanStore.getCascadingUpdateService().update(this, dateCreated)

  }

  @Override
  def beforeInsert() {
    super.beforeInsertHandler()
  }

  @Override
  def beforeUpdate() {
    super.beforeUpdateHandler()
  }

  def beforeDelete (){
    log.debug("beforeDelete for ${this}")
    BeanStore.getCascadingUpdateService().update(this, lastUpdated)

  }

  def afterUpdate(){
    log.debug("afterUpdate for ${this}")
    BeanStore.getCascadingUpdateService().update(this, lastUpdated)

  }

  String getOID(){
    "${this.class.name}:${id}"
  }

}

package wekb

import wekb.annotations.KbartAnnotation
import wekb.annotations.RefdataAnnotation
import wekb.base.AbstractBase
import wekb.helper.BeanStore
import wekb.helper.RCConstants

class TIPPCoverageStatement extends AbstractBase {

  @KbartAnnotation(kbartField = 'date_first_issue_online' , type='serials')
  Date startDate

  @KbartAnnotation(kbartField = 'date_last_issue_online' , type='serials')
  Date endDate

  @KbartAnnotation(kbartField = 'num_first_vol_online' , type='serials')
  String startVolume

  @KbartAnnotation(kbartField = 'num_first_issue_online' , type='serials')
  String startIssue

  @KbartAnnotation(kbartField = 'num_last_vol_online' , type='serials')
  String endVolume

  @KbartAnnotation(kbartField = 'num_last_issue_online  ' , type='serials')
  String endIssue

  @KbartAnnotation(kbartField = 'embargo_info' , type='serials')
  String embargo

  @KbartAnnotation(kbartField = 'notes ' , type='serials')
  String coverageNote

  Date dateCreated
  Date lastUpdated

  @KbartAnnotation(kbartField = 'coverage_depth' , type='serials')
  @RefdataAnnotation(cat = RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH)
  RefdataValue coverageDepth

  @KbartAnnotation(kbartField = 'access_type' , type='serials')
  @RefdataAnnotation(cat = RCConstants.TIPP_ACCESS_TYPE)
  RefdataValue accessType

  static belongsTo = [
    tipp: TitleInstancePackagePlatform
  ]

  static mapping = {
    id column: 'tcs_id'
    version column: 'tcs_version'
    uuid column: 'tcs_uuid', index: 'tcs_uuid_idx'
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
    accessType column: 'tcs_access_typ_fk'

    dateCreated column:'tcs_date_created'
    lastUpdated column:'tcs_last_updated'
  }

  static constraints = {
    startDate (nullable:true, blank:true)
    startVolume (nullable:true, blank:true)
    startIssue (nullable:true, blank:true)
    endDate ( nullable:true, blank:true)
    endVolume (nullable:true, blank:true)
    endIssue (nullable:true, blank:true)
    embargo (nullable:true, blank:true)
    coverageNote (nullable:true, blank:true)
    coverageDepth (nullable:true, blank:true)
    accessType (nullable:true, blank:false)

    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)

    tipp(unique: ['startDate', 'startVolume', 'startIssue', 'endDate', 'endVolume', 'endIssue'])
  }

  def afterInsert (){
    log.debug("afterSave for ${this}")
    BeanStore.getCascadingUpdateService().update(this, lastUpdated)

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

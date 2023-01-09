package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants




class TIPPCoverageStatement {


  def cascadingUpdateService

  TitleInstancePackagePlatform owner

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
    owner: TitleInstancePackagePlatform
  ]

  static mapping = {
    owner column: 'owner_id', index: 'tipp_owner_idx' //TODO adapt to naming convention
    startDate column:'tipp_start_date', index: 'tipp_start_date_idx'
    startVolume column:'tipp_start_volume'
    startIssue column:'tipp_start_issue'
    endDate column:'tipp_end_date', index: 'tipp_end_date_idx'
    endVolume column:'tipp_end_volume'
    endIssue column:'tipp_end_issue'
    embargo column:'tipp_embargo'
    coverageNote column:'tipp_coverage_note',type: 'text'
    coverageDepth column:'tipp_coverage_depth'

    dateCreated column:'tipp_coverage_date_created'
    lastUpdated column:'tipp_coverage_last_updated'
  }

  static constraints = {
    startDate (nullable:true, blank:true)
    startVolume (nullable:true, blank:true)
    startIssue (nullable:true, blank:true)
    endDate (validator: { val, obj ->
      if(obj.startDate && val && (obj.hasChanged('endDate') || obj.hasChanged('startDate')) && obj.startDate > val) {
        return ['endDate.endPriorToStart']
      }
    })
    endVolume (nullable:true, blank:true)
    endIssue (nullable:true, blank:true)
    embargo (nullable:true, blank:true)
    coverageNote (nullable:true, blank:true)
    coverageDepth (nullable:true, blank:true)

    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }

  def afterInsert (){
    log.debug("afterSave for ${this}")
    this.owner?.lastUpdateComment = "Coverage Statement ${this.id} created"
    cascadingUpdateService.update(this, dateCreated)

  }

  def beforeDelete (){
    log.debug("beforeDelete for ${this}")
    cascadingUpdateService.update(this, lastUpdated)

  }

  def afterUpdate(){
    log.debug("afterUpdate for ${this}")
    this.owner?.lastUpdateComment = "Coverage Statement ${this.id} created"
    cascadingUpdateService.update(this, lastUpdated)

  }

}

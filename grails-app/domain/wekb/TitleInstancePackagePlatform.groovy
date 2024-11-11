package wekb

import grails.plugins.orm.auditable.Auditable
import wekb.annotations.HbzKbartAnnotation
import wekb.annotations.KbartAnnotation
import wekb.annotations.RefdataAnnotation
import wekb.base.AbstractBase
import wekb.helper.BeanStore
import wekb.helper.RCConstants
import groovy.util.logging.Slf4j
import wekb.helper.RDStore

import javax.persistence.Transient
import java.text.SimpleDateFormat

@Slf4j
class TitleInstancePackagePlatform  extends AbstractBase implements Auditable {

  String name
  RefdataValue status

  // Timestamps
  Date dateCreated
  Date lastUpdated

  Package pkg
  Platform hostPlatform

  @HbzKbartAnnotation(kbartField = 'oa_type' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_OPEN_ACCESS)
  RefdataValue openAccess

  @HbzKbartAnnotation(kbartField = 'subject_area' , type='all')
  String subjectArea

  @HbzKbartAnnotation(kbartField = 'monograph_parent_collection_title' , type='monographs')
  String series

  @KbartAnnotation(kbartField = 'publisher_name' , type='all')
  String publisherName

  @KbartAnnotation(kbartField = 'title_url' , type='all')
  String url

  @HbzKbartAnnotation(kbartField = 'access_start_date' , type='all')
  Date accessStartDate

  @HbzKbartAnnotation(kbartField = 'access_end_date' , type='all')
  Date accessEndDate

  @HbzKbartAnnotation(kbartField = 'last_changed' , type='all')
  Date lastChangedExternal

  @KbartAnnotation(kbartField = 'first_author' , type='monographs')
  String firstAuthor

  @KbartAnnotation(kbartField = 'first_editor' , type='monographs')
  String firstEditor

  @KbartAnnotation(kbartField = 'parent_publication_title_id' , type='monographs')
  String parentPublicationTitleId

  @KbartAnnotation(kbartField = 'preceding_publication_title_id' , type='serials')
  String precedingPublicationTitleId

  @HbzKbartAnnotation(kbartField = 'superseding_publication_title_id' , type='all')
  String supersedingPublicationTitleId

  @KbartAnnotation(kbartField = 'notes' , type='all')
  String note

  @KbartAnnotation(kbartField = 'monograph_volume' , type='monographs')
  String volumeNumber

  @KbartAnnotation(kbartField = 'monograph_edition' , type='monographs')
  String editionStatement

  @KbartAnnotation(kbartField = 'date_monograph_published_print' , type='monographs')
  Date dateFirstInPrint

  @KbartAnnotation(kbartField = 'date_monograph_published_online' , type='monographs')
  Date dateFirstOnline

  @KbartAnnotation(kbartField = 'access_type' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_ACCESS_TYPE)
  RefdataValue accessType

  @KbartAnnotation(kbartField = 'medium' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_MEDIUM)
  RefdataValue medium

  @KbartAnnotation(kbartField = 'publication_type' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_PUBLICATION_TYPE)
  RefdataValue publicationType

  boolean fromKbartImport = false

  static transients = [ "kbartImportRunning" ]
  boolean kbartImportRunning = false

  Set languages


  private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd")

  static hasMany = [
    coverageStatements: TIPPCoverageStatement,
    ddcs: RefdataValue,
    ids: Identifier,
    updateTippInfos: UpdateTippInfo,
    prices              : TippPrice,
    languages           : TippLanguage

  ]

  static mappedBy = [
    coverageStatements: 'tipp',
    prices              : 'tipp'
  ]

  def getPersistentId() {
    "${uuid ?: 'wekb:TIPP:' + this.id + ':' + pkg?.id + ':' + hostPlatform?.id}"
  }

  static mapping = {
    id column: 'tipp_id'
    version column: 'tipp_version'

    uuid column: 'tipp_uuid'
    name column: 'tipp_name', type: 'text', index: 'tipp_name_idx'

    lastUpdated column: 'tipp_last_updated', index: 'tipp_last_updated_idx'
    dateCreated column: 'tipp_date_created', index: 'tipp_date_created_idx'

    status column: 'tipp_status_rv_fk', index: 'tipp_status_idx'

    note column: 'tipp_note', type: 'text'
    accessType column: 'tipp_access_type_rv_fk'
    accessStartDate column: 'tipp_access_start_date', index: 'tipp_access_start_date_idx'
    accessEndDate column: 'tipp_access_end_date', index: 'tipp_access_end_date_idx'
    firstAuthor column: 'tipp_first_author', type: 'text', index: 'tipp_first_author_idx'
    publicationType column: 'tipp_publication_type_rv_fk', index: 'tipp_publication_type_idx'
    volumeNumber column: 'tipp_volume_number'
    editionStatement column: 'tipp_edition_statement'
    firstEditor column: 'tipp_first_editor', type: 'text'
    parentPublicationTitleId column: 'tipp_parent_publication_title_id', index: 'tipp_parent_publication_type_idx'
    precedingPublicationTitleId column: 'tipp_preceding_publication_title_id', index: 'tipp_preceding_publication_type_idx'
    supersedingPublicationTitleId column: 'tipp_superseding_publication_title_id', index: 'tipp_superseding_publication_type_idx'
    lastChangedExternal column: 'tipp_last_change_ext', index: 'tipp_last_changed_ext_idx'
    medium column: 'tipp_medium_rv_fk', index: 'tipp_medium_idx'
    series column: 'tipp_series', type: 'text'
    url column: 'tipp_url', type: 'text', index: 'tipp_url_idx'
    subjectArea column: 'tipp_subject_area', type: 'text', index: 'tipp_subject_area_idx'
    openAccess column: 'tipp_open_access_rv_fk', index: 'tipp_open_access_idx'

    publisherName column: 'tipp_publisher_name'
    dateFirstOnline column: 'tipp_date_first_online'
    dateFirstInPrint column: 'tipp_date_first_in_print'

    pkg column: 'tipp_pkg_fk', index: 'tipp_pkg_idx'
    hostPlatform column: 'tipp_host_platform_fk', index: 'tipp_host_platform_idx'

    fromKbartImport column: 'tipp_from_kbart_import'

    ddcs             joinTable: [
            name:   'tipp_dewey_decimal_classification',
            key:    'tipp_fk',
            column: 'ddc_rv_fk', type:   'BIGINT'
    ], lazy: false
  }

  @Override
  def beforeInsert() {
    super.beforeInsertHandler()
  }

  @Override
  def beforeUpdate() {
    super.beforeUpdateHandler()
  }

  @Override
  def beforeDelete() {
    super.beforeDeleteHandler()
    BeanStore.getCascadingUpdateService().update(this, lastUpdated)
  }

  static constraints = {
    note(nullable: true, blank: true)
    accessType (nullable: true, blank: true)
    accessStartDate(nullable: true, blank: false)
/*    accessEndDate(validator: { val, obj ->
      if (obj.accessStartDate && val && (obj.hasChanged('accessEndDate') || obj.hasChanged('accessStartDate')) && obj.accessStartDate > val) {
        return ['accessEndDate.endPriorToStart']
      }
    })*/
    url(nullable: true, blank: true)
    firstAuthor(nullable: true, blank: true)
    publicationType(nullable: true, blank: true)
    volumeNumber(nullable: true, blank: true)
    editionStatement(nullable: true, blank: true)
    firstEditor(nullable: true, blank: true)
    parentPublicationTitleId(nullable: true, blank: true)
    precedingPublicationTitleId(nullable: true, blank: true)
    supersedingPublicationTitleId (nullable: true, blank: true)
    lastChangedExternal(nullable: true, blank: true)
    medium(nullable: true, blank: true)
    ddcs(nullable: true)
    dateFirstInPrint (nullable: true, blank: false)
    dateFirstOnline (nullable: true, blank: false)
    publisherName (nullable: true, blank: false)
    series (nullable: true, blank: false)
    subjectArea (nullable: true, blank: false)
    accessStartDate (nullable: true, blank: false)
    accessEndDate (nullable: true, blank: false)
    fromKbartImport (nullable: true, blank: false)
    openAccess (nullable: true, blank: false)
    hostPlatform (nullable: true, blank: false)
    pkg (nullable: true, blank: false)
  }


  String getTitleType() {
    if (publicationType) {
      switch (publicationType) {
        case RDStore.TIPP_PUBLIC_TYPE_SERIAL:
          return "serial"
          break;
        case RDStore.TIPP_PUBLIC_TYPE_MONO:
          return "monograph"
          break;
        case RDStore.TIPP_PUBLIC_TYPE_DB:
          return "database"
          break;
        case RDStore.TIPP_PUBLIC_TYPE_OTHER:
          return "other"
          break;
        default:
          return "title"
          break;
      }
    }
    else {
      return "Title"
    }
  }

  @Transient
  public String getTitleID(){
    String result = null
    result = getIdentifierValue('title_id')
    return result
  }

  @Transient
  public String getPrintIdentifier(){
    ids?.findAll{ it.namespace.value.toLowerCase() in ["issn", "isbn"]}?.value.join(';')
  }

  @Transient
  public String getOnlineIdentifier(){
    ids?.findAll{ it.namespace.value.toLowerCase() in ["eissn", "eisbn"]}?.value.join(';')
  }

  @Transient
  public String getListPriceInEUR(){
    RefdataValue listType = RDStore.PRICE_TYPE_LIST

    RefdataValue currency = RDStore.CURRENCY_EUR

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String getListPriceInUSD(){
    RefdataValue listType = RDStore.PRICE_TYPE_LIST

    RefdataValue currency = RDStore.CURRENCY_USD

    return retrievePriceOfCategory(listType, currency)
  }


  @Transient
  public String getListPriceInGBP(){
    RefdataValue listType = RDStore.PRICE_TYPE_LIST

    RefdataValue currency = RDStore.CURRENCY_GBP

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String getOAAPCPriceInEUR(){
    RefdataValue listType = RDStore.PRICE_TYPE_OA_APC

    RefdataValue currency = RDStore.CURRENCY_EUR

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String getOAAPCPriceInUSD(){
    RefdataValue listType = RDStore.PRICE_TYPE_OA_APC

    RefdataValue currency = RDStore.CURRENCY_USD

    return retrievePriceOfCategory(listType, currency)
  }


  @Transient
  public String getOAAPCPriceInGBP(){
    RefdataValue listType = RDStore.PRICE_TYPE_OA_APC

    RefdataValue currency = RDStore.CURRENCY_GBP

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String retrievePriceOfCategory(RefdataValue listType, RefdataValue currency){
    String result = null

    TippPrice existPrice = TippPrice.findWhere(tipp: this, priceType: listType, currency: currency)

    if(existPrice){
      result = existPrice.price.toString()
    }
    return result

  }

  @Transient
  String getIdentifierValue(idtype){
    // Null returned if no match.
    ids?.find{ it.namespace.value.toLowerCase() == idtype.toLowerCase() }?.value
  }

  def afterInsert (){
    log.debug("afterSave for ${this}")
    BeanStore.getCascadingUpdateService().update(this, lastUpdated)

  }

  def afterUpdate(){
    log.debug("afterUpdate for ${this}")
      BeanStore.getCascadingUpdateService().update(this, lastUpdated)

  }

  @Transient
  public String getDomainName() {
    return "Title"
  }

  public String getShowName() {
    return this.name
  }

  String toString(){
    "${name ?: ''}".toString()
  }

    @Transient
    public getCountAutoUpdateTippInfos() {
        int result = UpdateTippInfo.executeQuery("select count(*) from UpdateTippInfo where tipp = :tipp and updatePackageInfo.automaticUpdate = true", [tipp: this])[0]
        result
    }

    @Transient
    public getCountManualUpdateTippInfos() {
        int result = UpdateTippInfo.executeQuery("select count(*) from UpdateTippInfo where tipp = :tipp and updatePackageInfo.automaticUpdate = false", [tipp: this])[0]
        result
    }

}

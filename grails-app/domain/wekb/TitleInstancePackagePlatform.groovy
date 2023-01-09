package wekb

import wekb.annotations.HbzKbartAnnotation
import wekb.annotations.KbartAnnotation
import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants
import groovy.util.logging.Slf4j

import javax.persistence.Transient
import java.text.SimpleDateFormat

@Slf4j
class TitleInstancePackagePlatform extends KBComponent {

  def cascadingUpdateService

  Package pkg
  Platform hostPlatform

  @Deprecated
  String hybridOAUrl

  @Deprecated
  String coverageNote

  @Deprecated
  @RefdataAnnotation(cat = RCConstants.TIPP_PRIMARY)
  RefdataValue primary

  @Deprecated
  @RefdataAnnotation(cat = RCConstants.TIPP_HYBRIDA_OA)
  RefdataValue hybridOA

  @Deprecated
  @RefdataAnnotation(cat = RCConstants.TIPP_DELAYED_OA)
  RefdataValue delayedOA

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

  @Deprecated
  @KbartAnnotation(kbartField = 'coverage_depth' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_COVERAGE_DEPTH)
  RefdataValue coverageDepth

  @KbartAnnotation(kbartField = 'medium' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_MEDIUM)
  RefdataValue medium

  @KbartAnnotation(kbartField = 'publication_type' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_PUBLICATION_TYPE)
  RefdataValue publicationType

  boolean fromKbartImport = false

  static transients = [ "kbartImportRunning" ]
  boolean kbartImportRunning = false


  private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd")

/*  private static refdataDefaults = [
    "delayedOA"    : "Unknown",
    "hybridOA"     : "Unknown",
    "primary"      : "No",
    "coverageDepth": "Fulltext"
  ]*/

  static jsonMapping = [
    'ignore'       : [
      'accessType',
      'delayedOA',
      'hybridOA',
      'primary',
      'coverageDepth',
      'description',
      'hybridOAUrl'
    ],
    'es'           : [
      'hostPlatformUuid'      : "hostPlatform.uuid",
      'hostPlatformName'      : "hostPlatform.name",
      'hostPlatform'          : "hostPlatform.id",
      'tippPackageUuid'       : "pkg.uuid",
      'tippPackageName'       : "pkg.name",
      'tippPackage'           : "pkg.id",
      'titleType'             : "niceName",
      'coverage'              : "coverageStatements",
      'publisherName'         : "publisherName",
      'dateFirstInPrint'      : "dateFirstInPrint",
      'dateFirstOnline'       : "dateFirstOnline",
      'firstAuthor'           : "firstAuthor",
      'publicationType'       : "publicationType",
      'volumeNumber'          : "volumeNumber",
      'editionStatement'      : "editionStatement",
      'firstEditor'           : "firstEditor",
      'parentPublicationTitleId'   : "parentPublicationTitleId",
      'precedingPublicationTitleId': "precedingPublicationTitleId",
      'supersedingPublicationTitleId':"supersedingPublicationTitleId",
      'lastChangedExternal'   : "lastChangedExternal",
      'medium'                : "medium",
      'languages'              : "languages",
      'accessType'              : "accessType",
      'openAccess'              : "openAccess"
    ],
    'defaultLinks' : [
      'pkg',
      'hostPlatform'
    ],
    'defaultEmbeds': [
      'coverageStatements'
    ]
  ]

  static hasMany = [
    coverageStatements: TIPPCoverageStatement,
    ddcs: RefdataValue,
    ids: Identifier,
    updateTippInfos: UpdateTippInfo

  ]

  static mappedBy = [
    coverageStatements: 'owner'
  ]

  def getPersistentId() {
    "${uuid ?: 'wekb:TIPP:' + this.id + ':' + pkg?.id + ':' + hostPlatform?.id}"
  }

  static mapping = {
    includes KBComponent.mapping
    coverageDepth column: 'tipp_coverage_depth'
    coverageNote column: 'tipp_coverage_note', type: 'text'
    note column: 'tipp_note', type: 'text'
    delayedOA column: 'tipp_delayed_oa'
    hybridOA column: 'tipp_hybrid_oa'
    hybridOAUrl column: 'tipp_hybrid_oa_url'
    primary column: 'tipp_primary'
    accessType column: 'tipp_access_type'
    accessStartDate column: 'tipp_access_start_date', index: 'tipp_access_start_date_idx'
    accessEndDate column: 'tipp_access_end_date', index: 'tipp_access_end_date_idx'
    firstAuthor column: 'tipp_first_author', type: 'text', index: 'tipp_first_author_idx'
    publicationType column: 'tipp_publication_type_rv_fk', index: 'tipp_publication_type_idx'
    volumeNumber column: 'tipp_volume_number'
    editionStatement column: 'tipp_edition_statement'
    firstEditor column: 'tipp_first_editor', type: 'text'
    parentPublicationTitleId column: 'tipp_parent_publication_id', index: 'tipp_parent_publication_type_idx'
    precedingPublicationTitleId column: 'tipp_preceding_publication_id', index: 'tipp_preceding_publication_type_idx'
    supersedingPublicationTitleId column: 'tipp_superseding_publication_title_id', index: 'tipp_superseding_publication_type_idx'
    lastChangedExternal column: 'tipp_last_change_ext', index: 'tipp_last_changed_ext_idx'
    medium column: 'tipp_medium_rv_fk', index: 'tipp_medium_idx'
    series column: 'series', type: 'text'
    url column: 'url', type: 'text', index: 'tipp_url_idx'
    subjectArea column: 'subject_area', type: 'text', index: 'tipp_subject_area_idx'
    openAccess column: 'tipp_open_access_rv_fk', index: 'tipp_open_access_idx'

    pkg column: 'tipp_pkg_fk', index: 'tipp_pkg_idx'
    hostPlatform column: 'tipp_host_platform_fk', index: 'tipp_host_platform_idx'

    fromKbartImport column: 'tipp_from_kbart_import'

    ddcs             joinTable: [
            name:   'tipp_dewey_decimal_classification',
            key:    'tipp_fk',
            column: 'ddc_rv_fk', type:   'BIGINT'
    ], lazy: false
  }

  static constraints = {
    coverageDepth(nullable: true, blank: true)
    coverageNote(nullable: true, blank: true)
    note(nullable: true, blank: true)
    delayedOA(nullable: true, blank: true)
    hybridOA(nullable: true, blank: true)
    hybridOAUrl(nullable: true, blank: true)
    primary(nullable: true, blank: true)
    accessType (nullable: true, blank: true)
    accessStartDate(nullable: true, blank: false)
    accessEndDate(validator: { val, obj ->
      if (obj.accessStartDate && val && (obj.hasChanged('accessEndDate') || obj.hasChanged('accessStartDate')) && obj.accessStartDate > val) {
        return ['accessEndDate.endPriorToStart']
      }
    })
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
  }

  public static final String restPath = "/package-titles"

  def availableActions() {
    [[code: 'setStatus::Retired', label: 'Mark the title as retired'],
     /*[code: 'tipp::retire', label: 'Retire (with Date)'],*/
     [code: 'setStatus::Deleted', label: 'Mark the title as deleted', perm: 'delete'],
     [code: 'setStatus::Removed', label: 'Remove the title', perm: 'delete'],
     [code: 'setStatus::Expected', label: 'Mark the title as expected'],
     [code: 'setStatus::Current', label: 'Mark the title as current'],
     /*[code: 'tipp::move', label: 'Move TIPP']*/
    ]
  }

  @Override
  String getNiceName() {
    if (publicationType) {
      switch (publicationType) {
        case RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Serial"):
          return "Journal"
          break;
        case RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Monograph"):
          return "Book"
          break;
        case RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Database"):
          return "Database"
          break;
        case RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Other"):
          return "Other"
          break;
        default:
          return "Title"
          break;
      }
    }
    else {
      return "Title"
    }
  }

  @Override
  @Transient
  String getDisplayName() {
    return name ?: "${pkg?.name} / ${this.name} / ${hostPlatform?.name}"
  }

  @Transient
  public String getTitleID(){
      String result = null
      if(pkg.source && pkg.source.targetNamespace){
        result = getIdentifierValue(pkg.source.targetNamespace.value)
      }else if(hostPlatform.titleNamespace){
        result = getIdentifierValue(hostPlatform.titleNamespace.value)
      }
    return result
  }

  @Transient
  public String getPrintIdentifier(){
    ids?.findAll{ it.namespace.value.toLowerCase() in ["issn", "pisbn"]}?.value.join(';')
  }

  @Transient
  public String getOnlineIdentifier(){
    ids?.findAll{ it.namespace.value.toLowerCase() in ["eissn", "isbn"]}?.value.join(';')
  }

  @Transient
  public String getListPriceInEUR(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'list')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'EUR')

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String getListPriceInUSD(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'list')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'USD')

    return retrievePriceOfCategory(listType, currency)
  }


  @Transient
  public String getListPriceInGBP(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'list')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'GBP')

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String getOAAPCPriceInEUR(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'open access apc')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'EUR')

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String getOAAPCPriceInUSD(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'open access apc')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'USD')

    return retrievePriceOfCategory(listType, currency)
  }


  @Transient
  public String getOAAPCPriceInGBP(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'open access apc')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'GBP')

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String retrievePriceOfCategory(RefdataValue listType, RefdataValue currency){
    String result = null

    ComponentPrice existPrice = ComponentPrice.findWhere(owner: this, priceType: listType, currency: currency)

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
    cascadingUpdateService.update(this, dateCreated)

  }

  def beforeDelete (){
    log.debug("beforeDelete for ${this}")
    cascadingUpdateService.update(this, lastUpdated)

  }

  def afterUpdate(){
    log.debug("afterUpdate for ${this}")
    cascadingUpdateService.update(this, lastUpdated)

  }

  @Transient
  public String getDomainName() {
    return "Title"
  }

    @Transient
    public getCountAutoUpdateTippInfos() {
        int result = UpdateTippInfo.executeQuery("select count(id) from UpdateTippInfo where tipp = :tipp and updatePackageInfo.automaticUpdate = true", [tipp: this])[0]
        result
    }

    @Transient
    public getCountManualUpdateTippInfos() {
        int result = UpdateTippInfo.executeQuery("select count(id) from UpdateTippInfo where tipp = :tipp and updatePackageInfo.automaticUpdate = false", [tipp: this])[0]
        result
    }

}

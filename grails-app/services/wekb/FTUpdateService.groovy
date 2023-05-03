package wekb

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.elasticsearch.action.admin.indices.flush.FlushRequest
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.xcontent.XContentType
import org.hibernate.Session
import org.hibernate.SessionFactory
import wekb.helper.RDStore
import wekb.system.FTControl

import java.text.Normalizer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class FTUpdateService {

  ESWrapperService ESWrapperService
  SessionFactory sessionFactory
  DateFormatService dateFormatService
  Future activeFuture
  ExecutorService executorService

  public static boolean running = false


  /**
   * Update ES.
   * The caller is responsible for running this function in a task if needed. This method
   * is responsible for ensuring only 1 FT index task runs at a time. It's a simple mutex.
   * see https://async.grails.org/latest/guide/index.html
   */
  def updateFTIndexes() {
    log.info("updateFTIndexes")
    if (running == false) {
      if(!(activeFuture) || activeFuture.isDone()) {
        activeFuture = executorService.submit({
          Thread.currentThread().setName("FTUpdateServiceUpdateFTIndexes")
          doFTUpdate()
        })
        log.info("updateFTIndexes returning")
      }else{
        log.info("FT update already running")
        return false
      }
    }
    else {
      log.info("FT update already running")
      return false
    }
  }

  boolean doFTUpdate() {
    log.info("Execute IndexUpdateJob starting at ${new Date()}")

    synchronized(this) {
      if ( running ) {
        log.info("Exiting FT update - one already running");
        return false
      }
      else {
        running = true;
      }
    }

    def start_time = System.currentTimeMillis()
    try {
      updateES(wekb.Package.class) { wekb.Package kbc ->
        def result = null
        result = [:]
        result.recid = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name

        result.description = kbc.description
        result.descriptionURL = kbc.descriptionURL
        result.sortname = generateSortName(kbc.name)
        result.altname = []

        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        kbc.variantNames.each { vn ->
          result.altname.add(vn.variantName)
        }
        result.updater = 'pkg'
        result.titleCount = kbc.currentTippCount
        result.cpname = kbc.provider?.name
        result.provider = kbc.provider ? kbc.provider.getOID() : ""
        result.providerName = kbc.provider?.name ?: ""
        result.providerUuid = kbc.provider?.uuid ?: ""
        result.nominalPlatform = kbc.nominalPlatform ? kbc.nominalPlatform.getOID() : ""
        result.nominalPlatformName = kbc.nominalPlatform?.name ?: ""
        result.nominalPlatformUuid = kbc.nominalPlatform?.uuid ?: ""
        result.scope = kbc.scope ? kbc.scope.value : ""
        result.breakable = kbc.breakable ? kbc.breakable.value : ""
        result.paymentType = kbc.paymentType ? kbc.paymentType.value : ""
        result.openAccess = kbc.openAccess?.value
        result.file = kbc.file?.value
        result.contentType = kbc.contentType?.value

        result.freeTrial = kbc.freeTrial?.value
        result.freeTrialPhase = kbc.freeTrialPhase

        if (kbc.kbartSource) {
          result.source = [
            id              : kbc.kbartSource.id,
            name            : kbc.kbartSource.name,
            automaticUpdates: kbc.kbartSource.automaticUpdates,
            url             : kbc.kbartSource.url,
            frequency       : kbc.kbartSource.frequency?.value,
          ]
          if (kbc.kbartSource.lastRun){
            result.source.lastRun = dateFormatService.formatIsoTimestamp(kbc.kbartSource.lastRun)
          }
        }

        if(kbc.hasProperty('curatoryGroups')) {
          result.curatoryGroups = []
          kbc.curatoryGroups?.each {
            result.curatoryGroups.add([name: it.curatoryGroup.name,
                                       type: it.curatoryGroup.type?.value,
                                       curatoryGroup: it.curatoryGroup.getOID()])
          }
        }

        result.status = kbc.status?.value
        result.identifiers = []
        kbc.ids.each { idc ->
          result.identifiers.add([namespace    : idc.namespace.value,
                                  value        : idc.value,
                                  namespaceName: idc.namespace.name])
        }
        result.componentType = kbc.class.simpleName

        result.nationalRanges = []
        kbc.nationalRanges.each { nationalRange ->
          result.nationalRanges.add([value     : nationalRange.value,
                                    value_de  : nationalRange.value_de,
                                    value_en  : nationalRange.value_en])
        }

        result.regionalRanges = []
        kbc.regionalRanges.each { regionalRange ->
          result.regionalRanges.add([value     : regionalRange.value,
                                    value_de  : regionalRange.value_de,
                                    value_en  : regionalRange.value_en])
        }

        result.ddcs = []
        kbc.ddcs.each { ddc ->
          result.ddcs.add([value     : ddc.value,
                           value_de  : ddc.value_de,
                           value_en  : ddc.value_en])
        }

        result.packageArchivingAgencies = []
        kbc.paas.each { PackageArchivingAgency paa ->
          result.packageArchivingAgencies.add([archivingAgency: paa.archivingAgency?.value,
                                               openAccess  : paa.openAccess?.value,
                                               postCancellationAccess  : paa.postCancellationAccess?.value])
        }


        result
      }

      updateES(wekb.Org.class) { wekb.Org kbc ->
        def result = [:]
        result.recid = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.abbreviatedName = kbc.abbreviatedName
        result.sortname = generateSortName(kbc.name)
        result.altname = []
        result.updater = 'org'
        kbc.variantNames.each { vn ->
          result.altname.add(vn.variantName)
        }
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        result.roles = []
        kbc.roles.each { role ->
          result.roles.add(role.value)
        }

        if(kbc.hasProperty('curatoryGroups')) {
          result.curatoryGroups = []
          kbc.curatoryGroups?.each {
            result.curatoryGroups.add([name: it.curatoryGroup.name,
                                       type: it.curatoryGroup.type?.value,
                                       curatoryGroup: it.curatoryGroup.getOID()])
          }
        }

        result.status = kbc.status?.value
        result.identifiers = []
        kbc.ids.each { idc ->
          result.identifiers.add([namespace    : idc.namespace.value,
                                  value        : idc.value,
                                  namespaceName: idc.namespace.name])
        }
        result.componentType = kbc.class.simpleName

        result.contacts = []
        kbc.contacts.each { Contact contact ->
          result.contacts.add([  content: contact.content,
                                 contentType: contact.contentType?.value,
                                 type: contact.type?.value,
                                 language: contact.language?.value])
        }

        result.kbartDownloaderURL = kbc.kbartDownloaderURL
        result.metadataDownloaderURL = kbc.metadataDownloaderURL
        result.homepage = kbc.homepage

        result
      }

      updateES(wekb.Platform.class) { wekb.Platform kbc ->
        def result = [:]
        result.recid = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.sortname = generateSortName(kbc.name)
        result.updater = 'platform'
        result.cpname = kbc.provider?.name
        result.provider = kbc.provider ? kbc.provider.getOID() : ""
        result.providerName = kbc.provider ? kbc.provider.name : ""
        result.providerUuid = kbc.provider ? kbc.provider.uuid : ""
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)

        if(kbc.hasProperty('curatoryGroups')) {
          result.curatoryGroups = []
          kbc.curatoryGroups?.each {
            result.curatoryGroups.add([name: it.curatoryGroup.name,
                                       type: it.curatoryGroup.type?.value,
                                       curatoryGroup: it.curatoryGroup.getOID()])
          }
        }

        result.updater = 'platform'
        result.primaryUrl = kbc.primaryUrl
        result.status = kbc.status?.value
        result.identifiers = []
        kbc.ids.each { idc ->
          result.identifiers.add([namespace    : idc.namespace.value,
                                  value        : idc.value,
                                  namespaceName: idc.namespace.name])
        }
        result.componentType = kbc.class.simpleName

        result.titleNamespace = kbc.titleNamespace?.value

        result.lastAuditDate = kbc.lastAuditDate ? dateFormatService.formatIsoTimestamp(kbc.lastAuditDate) : null

        result.ipAuthentication = kbc.ipAuthentication?.value

        result.shibbolethAuthentication = kbc.shibbolethAuthentication?.value

        result.openAthens = kbc.openAthens?.value

        result.passwordAuthentication = kbc.passwordAuthentication?.value

        result.statisticsFormat = kbc.statisticsFormat?.value
        result.counterR3Supported = kbc.counterR3Supported?.value
        result.counterR4Supported = kbc.counterR4Supported?.value
        result.counterR5Supported = kbc.counterR5Supported?.value
        result.counterR4SushiApiSupported = kbc.counterR4SushiApiSupported?.value
        result.counterR5SushiApiSupported = kbc.counterR5SushiApiSupported?.value
        result.counterR4SushiServerUrl = kbc.counterR4SushiServerUrl
        result.counterR5SushiServerUrl = kbc.counterR5SushiServerUrl
        result.counterRegistryUrl = kbc.counterRegistryUrl
        result.counterCertified = kbc.counterCertified?.value
        result.statisticsAdminPortalUrl = kbc.statisticsAdminPortalUrl
        result.statisticsUpdate = kbc.statisticsUpdate?.value
        result.proxySupported = kbc.proxySupported?.value

        result.counterRegistryApiUuid = kbc.counterRegistryApiUuid

        result.federations = []
        kbc.federations.each { PlatformFederation platformFederation ->
          result.federations.add([ federation: platformFederation.federation?.value])
        }

        result
      }


      updateES(wekb.TitleInstancePackagePlatform.class) { wekb.TitleInstancePackagePlatform kbc ->

        def result = [:]
        result.recid = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.componentType = kbc.class.simpleName

        result.curatoryGroups = []
        kbc.pkg?.curatoryGroups?.each {
          result.curatoryGroups.add([name: it.curatoryGroup.name,
                                     type: it.curatoryGroup.type?.value,
                                       curatoryGroup: it.curatoryGroup.getOID()])
        }

        result.titleType = kbc.getTitleType() ?: 'Unknown'
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        result.url = kbc.url
        if (kbc.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
          result.coverage = []
          ArrayList<TIPPCoverageStatement> coverage_src = kbc.coverageStatements
          coverage_src.each { TIPPCoverageStatement tcs ->
            def cst = [:]
            if (tcs.startDate) cst.startDate = dateFormatService.formatIsoTimestamp(tcs.startDate)
            cst.startVolume = tcs.startVolume ?: ""
            cst.startIssue = tcs.startIssue ?: ""
            if (tcs.endDate) cst.endDate = dateFormatService.formatIsoTimestamp(tcs.endDate)
            cst.endVolume = tcs.endVolume ?: ""
            cst.endIssue = tcs.endIssue ?: ""
            cst.embargo = tcs.embargo ?: ""
            cst.coverageNote = tcs.coverageNote ?: ""
            cst.coverageDepth = tcs.coverageDepth ? tcs.coverageDepth.value : ""
            result.coverage.add(cst)
          }
        }
/*        if (kbc.niceName == 'Book') {
          // edition for eBooks
          def edition = [:]
          if (kbc.title?.editionNumber) {
            edition.number = kbc.title.editionNumber
          }
          if (kbc.title?.editionDifferentiator) {
            edition.differentiator = kbc.title.editionDifferentiator
          }
          if (kbc.title?.editionStatement) {
            edition.statement = kbc.title.editionStatement
          }
          if (!edition.isEmpty()) {
            result.titleEdition = edition
          }
          // simple eBook fields
          result.titleVolumeNumber = kbc.title?.volumeNumber ?: ""
          if (kbc.title?.dateFirstInPrint) result.titleDateFirstInPrint = dateFormatService.formatIsoTimestamp(kbc.title.dateFirstInPrint)
          if (kbc.title?.dateFirstOnline) result.titleDateFirstOnline = dateFormatService.formatIsoTimestamp(kbc.title.dateFirstOnline)
          result.titleFirstEditor = kbc.title?.firstEditor ?: ""
          result.titleFirstAuthor = kbc.title?.firstAuthor ?: ""
        }*/

        if (kbc.pkg) {
          result.tippPackage = kbc.pkg.getOID()
          result.tippPackageName = kbc.pkg.name
          result.tippPackageUuid = kbc.pkg.uuid
        }

        if (kbc.hostPlatform) {
          result.hostPlatform = kbc.hostPlatform.getOID()
          result.hostPlatformName = kbc.hostPlatform.name
          result.hostPlatformUuid = kbc.hostPlatform.uuid
        }

        // title history
        result.titleHistory = []
        // publishers
        result.titlePublishers = []

/*        if (kbc.title) {
          result.tippTitle = kbc.title.getOID()
          result.tippTitleName = kbc.title.name
          result.tippTitleUuid = kbc.title.uuid
          result.tippTitleMedium = kbc.title.medium.value
          kbc.title.titleHistory?.each { he ->
            if (he.date) {
              def event = [:]
              event.date = dateFormatService.formatIsoTimestamp(he.date)
              event.from = []
              if (he.from) {
                event.from.addAll(he.from.collect { fe -> [id: fe?.id, uuid: fe?.uuid, name: fe?.name] })
              }
              event.to = []
              if (he.to) {
                event.to.addAll(he.to.collect { te -> [id: te?.id, uuid: te?.uuid, name: te?.name] })
              }
              event.id = he.id ?: ""
              result.titleHistory.add(event)
            }
          }
          kbc.title.publisher?.each { pub ->
            def publisher = [:]
            publisher.name = pub.name ?: ""
            publisher.id = pub.id ?: ""
            publisher.uuid = pub.uuid ?: ""
            result.titlePublishers.add(publisher)
          }
          kbc.title.variantNames.each { vn ->
            result.altname.add(vn.variantName)
          }
        }*/
        if (kbc.medium) result.medium = kbc.medium.value
        if (kbc.status) result.status = kbc.status.value
        if (kbc.publicationType) result.publicationType = kbc.publicationType.value
        if (kbc.openAccess) result.openAccess = kbc.openAccess.value
        if (kbc.accessType) result.accessType = kbc.accessType.value

        result.identifiers = []
        kbc.ids.each { idc ->
          result.identifiers.add([namespace    : idc.namespace.value,
                                  value        : idc.value,
                                  namespaceName: idc.namespace.name])
        }
        if (kbc.dateFirstOnline) result.dateFirstOnline = dateFormatService.formatIsoTimestamp(kbc.dateFirstOnline)
        if (kbc.dateFirstInPrint) result.dateFristInPrint = dateFormatService.formatIsoTimestamp(kbc.dateFirstInPrint)
        if (kbc.accessStartDate) result.accessStartDate = dateFormatService.formatIsoTimestamp(kbc.accessStartDate)
        if (kbc.accessEndDate) result.accessEndDate = dateFormatService.formatIsoTimestamp(kbc.accessEndDate)
        if (kbc.lastChangedExternal) result.lastChangedExternal = dateFormatService.formatIsoTimestamp(kbc.lastChangedExternal)

        if (kbc.publisherName) result.publisherName = kbc.publisherName
        if (kbc.subjectArea) result.subjectArea = kbc.subjectArea
        if (kbc.series) result.series = kbc.series
        if (kbc.volumeNumber) result.volumeNumber = kbc.volumeNumber
        if (kbc.editionStatement) result.editionStatement = kbc.editionStatement
        if (kbc.firstAuthor) result.firstAuthor = kbc.firstAuthor
        if (kbc.firstEditor) result.firstEditor = kbc.firstEditor
        if (kbc.parentPublicationTitleId) result.parentPublicationTitleId = kbc.parentPublicationTitleId
        if (kbc.precedingPublicationTitleId) result.precedingPublicationTitleId = kbc.precedingPublicationTitleId
        if (kbc.supersedingPublicationTitleId) result.supersedingPublicationTitleId = kbc.supersedingPublicationTitleId
        if (kbc.note) result.note = kbc.note

        // prices
        result.prices = []
        kbc.prices?.each { p ->
          def price = [:]
          price.type = p.priceType?.value ?: ""
          price.amount = String.valueOf(p.price) ?: ""
          price.currency = p.currency?.value ?: ""
          if (p.startDate){
            price.startDate = dateFormatService.formatIsoTimestamp(p.startDate)
          }
          if (p.endDate){
            price.endDate = dateFormatService.formatIsoTimestamp(p.endDate)
          }
          result.prices.add(price)
        }

        result.ddcs = []
        kbc.ddcs.each { ddc ->
          result.ddcs.add([value     : ddc.value,
                           value_de  : ddc.value_de,
                           value_en  : ddc.value_en])
        }

        result.languages = []
        kbc.languages.each { ComponentLanguage kbl ->
          result.languages.add([value     : kbl.language.value,
                                value_de  : kbl.language.value_de,
                                value_en  : kbl.language.value_en])
        }

        result
      }

      updateES(wekb.DeletedKBComponent.class) { wekb.DeletedKBComponent deletedKBComponent ->

        def result = [:]
        result.recid = "${deletedKBComponent.class.name}:${deletedKBComponent.id}"
        result.uuid = deletedKBComponent.uuid
        result.name = deletedKBComponent.name
        result.componentType = deletedKBComponent.componentType
        result.status = deletedKBComponent.status.value
        result.dateCreated = dateFormatService.formatIsoTimestamp(deletedKBComponent.dateCreated)
        result.lastUpdated = dateFormatService.formatIsoTimestamp(deletedKBComponent.lastUpdated)
        result.oldDateCreated = dateFormatService.formatIsoTimestamp(deletedKBComponent.oldDateCreated)
        result.oldLastUpdated = dateFormatService.formatIsoTimestamp(deletedKBComponent.oldLastUpdated)
        result.oldId = deletedKBComponent.oldId
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(deletedKBComponent.lastUpdated)

        result
      }
    }
    catch (Exception e) {
      log.error("Problem", e)
    }

    def elapsed = System.currentTimeMillis() - start_time;
    log.info("FTUpdate completed in ${elapsed}ms at ${new Date()} ")

    running = false
    return true
  }


  def updateES(domain, recgen_closure) {
    RestHighLevelClient esclient = ESWrapperService.getClient()
    def count = 0
    long currentTimestamp = 0

    try {
      log.info("updateES - ${domain.name}")
      def latest_ft_record = null
      def highest_id = 0

      latest_ft_record = FTControl.findByDomainClassNameAndActivity(domain.name, 'ESIndex')
      log.debug("result of findByDomain: ${domain} ${latest_ft_record}")
      if (!latest_ft_record) {
        FTControl.withTransaction {
          latest_ft_record =
                  new FTControl(domainClassName: domain.name, activity: 'ESIndex', lastTimestamp: 0, lastId: 0)
                          .save()
        }
        log.debug("Create new FT control record, as none available for ${domain.name}")
      } else {
        log.debug("Got existing ftcontrol record for ${domain.name} max timestamp is ${latest_ft_record.lastTimestamp} which is " +
                "${new Date(latest_ft_record.lastTimestamp)}")
      }

      log.info("updateES ${domain.name} since ${latest_ft_record.lastTimestamp}")

      def total = 0
      Date from = new Date(latest_ft_record.lastTimestamp)
      def countq = domain.executeQuery("select count(o.id) from " + domain.name +
              " as o where (( o.lastUpdated > :ts ) OR ( o.dateCreated > :ts )) ", [ts: from], [readonly: true])[0]
      log.info("Will process ${countq} records")
      List<Long> idList = domain.executeQuery("select o.id from " + domain.name +
              " as o where ((o.lastUpdated > :ts ) OR ( o.dateCreated > :ts )) order by o.lastUpdated, o.id", [ts: from],
              [readonly: true])
      log.debug("Query completed.. processing rows...")

      currentTimestamp = System.currentTimeMillis()

      boolean processFail = false

      BulkRequest bulkRequest = new BulkRequest()
      Date last_LastUpdated
      // while (results.next()) {
      FTControl.withTransaction {

        List<Long> todoList = idList.take(3000000)
        List<List<Long>> bulks = todoList.collate(5000)

        bulks.eachWithIndex { List<Long> bulk, int i ->
          for (domain_id in bulk) {
            if (Thread.currentThread().isInterrupted()) {
              log.debug("Job cancelling ..")
              running = false
              break
            }
            Object r = domain.get(domain_id)
            if (ESWrapperService.indicesPerType.get(r.class.simpleName)) {
              log.debug("${r.id} ${domain.name} -- (rects)${r.lastUpdated} > (from)${from}")
              def idx_record = recgen_closure(r)
              def es_index = ESWrapperService.indicesPerType.get(r.class.simpleName)

              if (idx_record != null) {
                def recid = idx_record['recid'].toString()
                idx_record.remove('recid')

                IndexRequest request = new IndexRequest(es_index)
                request.id(recid)
                String jsonString = idx_record as JSON
                //String jsonString = JsonOutput.toJson(idx_record)
                //println(jsonString)
                request.source(jsonString, XContentType.JSON)

                bulkRequest.add(request)
              }
              highest_id = r.id
              last_LastUpdated = r.lastUpdated
              count++
              total++
            }
          }
          if (bulkRequest.numberOfActions()) {
            log.info("interim:: processed ${total} out of ${countq} records (${domain.name})")
            BulkResponse bulkResponse = esclient.bulk(bulkRequest, RequestOptions.DEFAULT)

            if (bulkResponse.hasFailures()) {
              for (BulkItemResponse bulkItemResponse : bulkResponse) {
                if (bulkItemResponse.isFailed()) {
                  BulkItemResponse.Failure failure = bulkItemResponse.getFailure()
                  log.error("updateES ${domain.name}: ES Bulk operation has failure -> ${failure}")
                  processFail = true
                }
              }
            }
          }else {
            log.info( "updateES: ignored empty bulk")
          }

          FTControl.withTransaction {
            latest_ft_record = FTControl.get(latest_ft_record.id)
            latest_ft_record.lastTimestamp = last_LastUpdated.getTime()
            latest_ft_record.lastId = highest_id
            latest_ft_record.save(flush: true)
          }
          bulkRequest = new BulkRequest()
        }

        if(!processFail) {
          // update timestamp
          FTControl.withTransaction {
            latest_ft_record = FTControl.get(latest_ft_record.id)
            latest_ft_record.lastTimestamp = currentTimestamp
            latest_ft_record.lastId = highest_id
            latest_ft_record.save()
          }
        }
        log.info("final:: Processed ${total} out of ${countq} records for ${domain.name}.")
      }
    }
    catch (Exception e) {
      log.error("Problem with FT index", e)
    }
    finally {
      FlushRequest request = new FlushRequest(ESWrapperService.indicesPerType.get(domain.name))
      FlushResponse flushResponse = esclient.indices().flush(request, RequestOptions.DEFAULT)
      esclient.close()
      log.info("Completed processing on ${domain.name} - saved ${count} records")
    }
  }


  def cleanUpGorm() {
    log.debug("Clean up GORM")
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
  }


  def clearDownAndInitES() {
    if (running == false) {
      log.info("Remove existing FTControl ..")
      FTControl.withTransaction {
        def res = FTControl.executeUpdate("delete FTControl c")
        log.debug("Result: ${res}")
      }
      updateFTIndexes()
    }
    else {
      log.error("FTUpdate already running")
      return "Job cancelled – FTUpdate was already running!"
    }
  }


  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy")
  }

  private String generateSortName(String input_title) {
    if (!input_title) return null
    String s1 = Normalizer.normalize(input_title, Normalizer.Form.NFKD).trim().toLowerCase()


    return s1.trim()

  }
}

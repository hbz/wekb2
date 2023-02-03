package wekb


import wekb.ConcurrencyManagerService.Job
import wekb.auth.User
import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.core.CountRequest
import org.elasticsearch.client.core.CountResponse
import org.elasticsearch.client.indices.GetIndexRequest
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.security.access.annotation.Secured
import wekb.system.FTControl

import java.text.SimpleDateFormat
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutorService

@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
class AdminController {

  ComponentStatisticService componentStatisticService
  ConcurrencyManagerService concurrencyManagerService
  CleanupService cleanupService
  AutoUpdatePackagesService autoUpdatePackagesService
  def ESWrapperService
  SpringSecurityService springSecurityService
  FTUpdateService FTUpdateService
  SessionFactory sessionFactory
  GenericOIDService genericOIDService
  ExecutorService executorService

  static Map typePerIndex = [
          "wekbtipps": "TitleInstancePackagePlatform",
          "wekborgs": "Org" ,
          "wekbpackages": "Package",
          "wekbplatforms": "Platform",
          "wekbdeletedcomponents": "DeletedKBComponent"
  ]

  def ensureUuids() {

    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.ensureUuids(j)
    }.startOrQueue()

    j.description = "Ensure UUIDs for components"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'EnsureUUIDs')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');

  }

  def updateTextIndexes() {
    log.debug("Call to update indexe");

    FTUpdateService.updateFTIndexes();

    redirect(controller: 'admin', action: 'manageFTControl');
  }

  def resetTextIndexes() {
    log.debug("Call to update indexe")

    FTUpdateService.clearDownAndInitES()

    redirect(controller: 'admin', action: 'manageFTControl');
  }

  def jobs() {
    log.debug("Jobs");
    def result = [:]
    log.debug("Sort");
    result.jobs = concurrencyManagerService.jobs.sort { a, b -> b.value.startTime <=> a.value.startTime }
    log.debug("concurrency manager service");
    result.cms = concurrencyManagerService

    result.jobs.each { k, j ->
      if (j.isDone() && !j.endTime) {

        try {
          def job_res = j.get()

          if (job_res && job_res instanceof Date) {
            j.endTime = j.get()
          }
        }
        catch (CancellationException e) {
          log.debug("Cancelled")
        }
        catch (Exception e) {
          log.debug("${e}")

          if (j.messages?.size() == 0) {
            j.message("There has been an exception processing this job! Please check the logs!")
          }
        }
      }
    }

    log.debug("Render");
    if (request.format == 'JSON') {
      log.debug("JSON Render");
      render result as JSON
    }

    log.debug("Return");
    result
  }

  def cleanJobList() {
    log.debug("clean job list..")
    def jobs = concurrencyManagerService.jobs
    def maxId = jobs.max { it.key }.key

    jobs.each { k, j ->
      if (j.isDone()) {
        jobs.remove(k)
      }
    }
    redirect(url: request.getHeader('referer'))
  }

  def cancelJob() {
    Job j = concurrencyManagerService.getJob(params.id)

    j?.forceCancel()
    redirect(controller: 'admin', action: 'jobs');
  }


  def expungeRemovedComponents() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.expungeRemovedComponents(j)
    }.startOrQueue()

    log.debug "Triggering cleanup task. Started job #${j.uuid}"

    j.description = "Cleanup Removed Components"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'CleanupRemovedComponents')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def cleanupPlatforms() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.deleteNoUrlPlatforms(j)
    }.startOrQueue()

    log.debug("Triggering cleanup task. Started job #${j.uuid}")

    j.description = "Platform Cleanup"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'PlatformCleanup')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def recalculateStats() {
    Job j = concurrencyManagerService.createJob {
      componentStatisticService.updateCompStats(12, 0, true)
    }.startOrQueue()

    log.debug "Triggering statistics rewrite, job #${j.uuid}"
    j.description = "Recalculate Statistics"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'RecalculateStatistics')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  @Secured(['ROLE_SUPERUSER'])
  def autoUpdatePackages() {
      log.debug("autoUpdatePackages: Beginning scheduled auto update packages job.")
    executorService.execute({
      Thread.currentThread().setName('autoUpdatePackages_OnlyLastChanged')
      autoUpdatePackagesService.findPackageToUpdateAndUpdate(true)
    })

    log.info("autoUpdatePackages: auto update packages job completed.")

    redirect(controller: 'search', action: 'componentSearch', params: [qbe: 'g:updatePackageInfos'])
  }

  @Secured(['ROLE_SUPERUSER'])
  def autoUpdatePackagesAllTitles() {
    log.debug("autoUpdatePackagesAllTitles: Beginning scheduled auto update packages job.")
    executorService.execute({
      Thread.currentThread().setName('autoUpdatePackages_AllTitles')
      autoUpdatePackagesService.findPackageToUpdateAndUpdate(false)
    })

    log.info("autoUpdatePackagesAllTitles: auto update packages job completed.")

    redirect(controller: 'search', action: 'componentSearch', params: [qbe: 'g:updatePackageInfos'])
  }

  @Secured(['ROLE_SUPERUSER'])
  def manageFTControl() {
    Map<String, Object> result = [:]
    log.debug("manageFTControl ...")
    result.ftControls = FTControl.list()
    result.ftUpdateService = [:]
    result.editable = true

    /*Client esclient = ESWrapperService.getClient()

    result.indices = []
    def esIndices = grailsApplication.config.wekb.es.indices?.values()

    esIndices.each{ String indexName ->
      Map indexInfo = [:]
      indexInfo.name = indexName
      indexInfo.type = typePerIndex.get(indexName)

      SearchResponse response = esclient.prepareSearch(indexName)
              .setSize(0)
              .execute().actionGet()

      SearchHits hits = response.getHits()
      indexInfo.countIndex = hits.getTotalHits()

      String query = "select count(id) from ${typePerIndex.get(indexName)}"
      indexInfo.countDB = FTControl.executeQuery(query)[0]
      indexInfo.countDeletedInDB = FTControl.executeQuery(query+ " where status = :status", [status: status_deleted]) ? FTControl.executeQuery(query+ " where status = :status", [status: status_deleted])[0] : 0
      result.indices << indexInfo
    }*/

    RestHighLevelClient esclient = ESWrapperService.getClient()

    result.indices = []
    def esIndices = ESWrapperService.es_indices
    esIndices.each{ def indice ->
      Map indexInfo = [:]
      indexInfo.name = indice.value
      indexInfo.type = indice.key

      GetIndexRequest request = new GetIndexRequest(indice.value)

      if (esclient.indices().exists(request, RequestOptions.DEFAULT)) {
        CountRequest countRequest = new CountRequest(indice.value)
        CountResponse countResponse = esclient.count(countRequest, RequestOptions.DEFAULT)
        indexInfo.countIndex = countResponse ? countResponse.getCount().toInteger() : 0
      }else {
        indexInfo.countIndex = ""
      }

      String query = "select count(id) from ${typePerIndex.get(indice.value)}"
      indexInfo.countDB = FTControl.executeQuery(query)[0]
      indexInfo.countDeletedInDB = FTControl.executeQuery(query+ " where status = :status", [status: RDStore.KBC_STATUS_DELETED])[0]
      indexInfo.countRemovedInDB = FTControl.executeQuery(query+ " where status = :status", [status: RDStore.KBC_STATUS_REMOVED])[0]
      result.indices << indexInfo
    }

    try {
      esclient.close()
    }
    catch (Exception e) {
      log.error("Problem by Close ES Client", e)
    }

    result
  }


  @Secured(['ROLE_SUPERUSER'])
  def deleteIndex() {
    String indexName = params.name
    List deletedKBComponentList = []
    if (indexName) {
      ESWrapperService.deleteIndex(indexName)
      ESWrapperService.createIndex(indexName)

      FTControl.withTransaction {
        String domainClassName = typePerIndex.get(indexName)
        if(indexName == 'wekbdeletedcomponents'){
          domainClassName = 'wekb.DeletedKBComponent'
        }
        else {
          domainClassName = "wekb.${domainClassName}"
        }
        def res = FTControl.executeUpdate("delete FTControl c where c.domainClassName = ${domainClassName}")
        log.info("Result: ${res}")
      }
     /* if (typePerIndex.get(indexName) == DeletedKBComponent.class.simpleName) {

        DeletedKBComponent.getAll().each { DeletedKBComponent deletedKBComponent ->
          Map idx_record = [:]
          idx_record.recid = "${deletedKBComponent.class.name}:${deletedKBComponent.id}"
          idx_record.uuid = deletedKBComponent.uuid
          idx_record.name = deletedKBComponent.name
          idx_record.componentType = deletedKBComponent.componentType
          idx_record.status = deletedKBComponent.status.value
          idx_record.dateCreated = dateFormatService.formatIsoTimestamp(deletedKBComponent.dateCreated)
          idx_record.lastUpdated = dateFormatService.formatIsoTimestamp(deletedKBComponent.lastUpdated)
          idx_record.oldDateCreated = dateFormatService.formatIsoTimestamp(deletedKBComponent.oldDateCreated)
          idx_record.oldLastUpdated = dateFormatService.formatIsoTimestamp(deletedKBComponent.oldLastUpdated)
          idx_record.oldId = deletedKBComponent.oldId

          deletedKBComponentList << idx_record
        }

      }

      Job j = concurrencyManagerService.createJob {

        log.info("deleteIndex ${indexName} ...")
        Client esclient = ESWrapperService.getClient()
        IndicesAdminClient adminClient = esclient.admin().indices()

        if (adminClient.prepareExists(indexName).execute().actionGet().isExists()) {
          DeleteIndexRequestBuilder deleteIndexRequestBuilder = adminClient.prepareDelete(indexName)
          DeleteIndexResponse deleteIndexResponse = deleteIndexRequestBuilder.execute().actionGet()
          if (deleteIndexResponse.isAcknowledged()) {
            log.info("Index ${indexName} successfully deleted!")
          } else {
            log.info("Index deletetion failed: ${deleteIndexResponse}")
          }
        }
        log.info("ES index ${indexName} did not exist, creating..")
        CreateIndexRequestBuilder createIndexRequestBuilder = adminClient.prepareCreate(indexName)
        log.info("Adding index settings..")
        createIndexRequestBuilder.setSettings(ESWrapperService.getSettings().get("settings"))
        log.info("Adding index mappings..")
        createIndexRequestBuilder.addMapping("component", ESWrapperService.getMapping())

        CreateIndexResponse createIndexResponse = createIndexRequestBuilder.execute().actionGet()
        if (createIndexResponse.isAcknowledged()) {
          log.info("Index ${indexName} successfully created!")
          if (typePerIndex.get(indexName) == DeletedKBComponent.class.simpleName) {
            deletedKBComponentList.each {
              String recid = it.recid
              it.remove('recid')
              IndexResponse indexResponse = esclient.prepareIndex("wekbdeletedcomponents", 'component', recid).setSource(it).get()
              if (indexResponse.getResult() != DocWriteResponse.Result.CREATED) {
                log.error("Error on record DeletedKBComponent in Index 'wekbdeletedcomponents'")
              }

            }

          } else {
            FTControl.withTransaction {
              def res = FTControl.executeUpdate("delete FTControl c where c.domainClassName = :deleteFT", [deleteFT: "wekb.${typePerIndex.get(indexName)}"])
              log.info("Result: ${res}")
            }
            FTUpdateService.updateFTIndexes()
          }
        } else {
          log.info("Index creation failed: ${createIndexResponse}")
        }
      }.startOrQueue()
      j.description = "Delete index ${params.name}"
      j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'ResetFreeTextIndexes')
      j.startTime = new Date()*/
    }

    redirect(action: 'manageFTControl')
  }


  def packagesChanges() {
    log.debug("packageChanges::${params}")
    def result = [:]

    User user = springSecurityService.getCurrentUser()

    String query = 'from Package as pkg where pkg.status != :status'

    RefdataValue status_deleted = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Deleted')

    params.offset = params.offset ?: 0
    params.max = params.max ? Integer.parseInt(params.max) : user.defaultPageSizeAsInteger

    result.packagesCount = Package.executeQuery('select count(pkg) ' + query, [status: status_deleted])[0]
    result.packages = Package.executeQuery('select pkg ' + query + " order by pkg.lastUpdated desc", [status: status_deleted], params)

    result
  }


  def frontend() {
    log.debug("frontend::${params}")
    def result = [:]
    result
  }

  def findTippDuplicatesByPkg() {
    log.debug("findTippDuplicates::${params}")
    def result = [:]

    Package aPackage = Package.findByUuid(params.id)

    List<TitleInstancePackagePlatform> tippsDuplicatesByName = aPackage.findTippDuplicatesByName()
    List<TitleInstancePackagePlatform> tippsDuplicatesByUrl = aPackage.findTippDuplicatesByURL()
    List<TitleInstancePackagePlatform> tippsDuplicatesByTitleID = aPackage.findTippDuplicatesByTitleID()

    result.offsetByName = params.papaginateByName ? Integer.parseInt(params.offset) : 0
    result.maxByName = params.papaginateByName ? Integer.parseInt(params.max) : 100

    result.offsetByUrl = params.papaginateByUrl ? Integer.parseInt(params.offset) : 0
    result.maxByUrl = params.papaginateByUrl ? Integer.parseInt(params.max) : 100

    result.offsetByTitleID = params.papaginateByTitleID ? Integer.parseInt(params.offset) : 0
    result.maxByTitleID = params.papaginateByTitleID ? Integer.parseInt(params.max) : 100

    result.totalCountByName = tippsDuplicatesByName.size()
    result.totalCountByUrl = tippsDuplicatesByUrl.size()
    result.totalCountByTitleID = tippsDuplicatesByTitleID.size()

    result.tippsDuplicatesByName = tippsDuplicatesByName.drop((int) result.offsetByName).take((int) result.maxByName)
    result.tippsDuplicatesByUrl = tippsDuplicatesByUrl.drop((int) result.offsetByUrl).take((int) result.maxByUrl)
    result.tippsDuplicatesByTitleID = tippsDuplicatesByTitleID.drop((int) result.offsetByTitleID).take((int) result.maxByTitleID)

    result
  }

  def findPackagesWithTippDuplicates() {
    log.debug("findPackagesWithTippDuplicates::${params}")
    def result = [:]

    List pkgs = []
    List<KbartSource> sourceList = KbartSource.findAllByAutomaticUpdatesAndTargetNamespaceIsNotNull(true)

    Package.findAllByStatus(RDStore.KBC_STATUS_CURRENT, [sort: 'name']).eachWithIndex { Package aPackage, int index ->
      Integer tippDuplicatesByNameCount = aPackage.getTippDuplicatesByNameCount()
      Integer tippDuplicatesByUrlCount = aPackage.getTippDuplicatesByURLCount()
      Integer tippDuplicatesByTitleIDCount = aPackage.getTippDuplicatesByTitleIDCount()
      log.debug("Package ${aPackage.name} : ${index}")

      if(tippDuplicatesByNameCount > 0 || tippDuplicatesByUrlCount > 0 || tippDuplicatesByTitleIDCount > 0){
        pkgs << [pkg: aPackage, tippDuplicatesByNameCount: tippDuplicatesByNameCount, tippDuplicatesByUrlCount: tippDuplicatesByUrlCount, tippDuplicatesByTitleIDCount: tippDuplicatesByTitleIDCount]
      }
    }

    //result.offset = params.offset ? Integer.parseInt(params.offset) : 0
    //result.max = params.max ? Integer.parseInt(params.max) : 250

    result.totalCount = pkgs.size()

    if (params.sort == 'tippDuplicatesByNameCount') {
      result.pkgs = pkgs.sort {
        it.tippDuplicatesByNameCount
      }
      result.pkgs = result.pkgs.reverse()
    } else if (params.sort == 'tippDuplicatesByUrlCount') {
      result.pkgs = pkgs.sort {
        it.tippDuplicatesByUrlCount
      }
      result.pkgs = result.pkgs.reverse()
    } else if (params.sort == 'tippDuplicatesByTitleIDCount') {
      result.pkgs = pkgs.sort {
        it.tippDuplicatesByTitleIDCount
      }
      result.pkgs = result.pkgs.reverse()
    } else {
      result.pkgs = pkgs
    }

    //result.pkgs = result.pkgs.drop((int) result.offset).take((int) result.max)
    result
  }

  def removeTippDuplicatesByUrl(){
    Package aPackage = Package.findByUuid(params.id)

    List<TitleInstancePackagePlatform> tippsDuplicatesByUrl = aPackage.findTippDuplicatesByURL()

    int countRemoved = 0
    tippsDuplicatesByUrl.groupBy {it.url}.each {String key, List<TitleInstancePackagePlatform> tipps ->
      //println(key)
      //println(tipps.sort {it.lastUpdated}.reverse().lastUpdated)
      List list = tipps.sort {it.lastUpdated}.reverse()
      list.eachWithIndex { TitleInstancePackagePlatform titleInstancePackagePlatform, int index ->
        if(index != 0){
          titleInstancePackagePlatform.status = RDStore.KBC_STATUS_REMOVED
          titleInstancePackagePlatform.save(flush: true)
          countRemoved++
        }
      }

    }

    flash.message = "Tipps ${countRemoved} set to removed because of tipp duplicates by url"

    redirect(action: 'findTippDuplicatesByPkg', params: [id: params.id, papaginateByUrl: true, max: 100, offset: 0] )

  }

  def cleanupTippIdentifersWithSameNamespace() {
    /*Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.cleanupTippIdentifersWithSameNamespace(j)
    }.startOrQueue()

    log.debug("Cleanup Tipp Identifers with same namespace #${j.uuid}")

    j.description = "Cleanup Tipp Identifers with same namespace"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'TIPPCleanup')
    j.startTime = new Date()
*/
    //cleanupService.cleanupTippIdentifersWithSameNamespace()
    redirect(controller: 'admin', action: 'jobs');
  }

  def tippIdentifiersWithSameNameSpace(){
      log.debug("tippIdentifiersWithSameNameSpace")

      List<IdentifierNamespace> identifierNamespaces = IdentifierNamespace.findAllByTargetType(RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP, [sort: 'value'])

      String countQuery = "SELECT count(tipp.id) FROM TitleInstancePackagePlatform AS tipp WHERE " +
              "tipp.id in (select ident.tipp FROM Identifier AS ident WHERE ident.namespace.id = :namespace group by ident.tipp having count(ident.tipp) > 1)"

      String idQuery = "SELECT tipp.id FROM TitleInstancePackagePlatform AS tipp WHERE " +
              "tipp.id in (select ident.tipp FROM Identifier AS ident WHERE ident.namespace.id = :namespace group by ident.tipp having count(ident.tipp) > 1)"


      Integer total = 0
      Map result = [:]
      List namespaces = []
      identifierNamespaces.each {IdentifierNamespace identifierNamespace ->
        Integer tippCount = TitleInstancePackagePlatform.executeQuery(countQuery, [namespace: identifierNamespace.id])[0]
        total = total + tippCount
        Map namespace = [:]

        List<Long> tippIds = TitleInstancePackagePlatform.executeQuery(idQuery, [namespace: identifierNamespace.id])

        if(tippIds.size() > 0) {
          namespace.name = identifierNamespace.value
          namespace.family = identifierNamespace.family
          namespace.count = tippCount
          namespace.namespaceID = identifierNamespace.id
          namespaces << namespace

        }
      }
      result.namespaces = namespaces
      result.total = total

      log.debug("cleanupTippIdentifersWithSameNamespace: count ${total}")

      result
  }

  def tippIdentifiersWithSameNameSpaceByNameSpace(){
    log.debug("tippIdentifiersWithSameNameSpaceByNameSpace")

    String countQuery = "SELECT count(tipp.id) FROM TitleInstancePackagePlatform AS tipp WHERE " +
            "tipp.id in (select ident.tipp FROM Identifier AS ident WHERE ident.namespace.id = :namespace group by ident.tipp having count(ident.tipp) > 1)"

    String query = "SELECT tipp FROM TitleInstancePackagePlatform AS tipp WHERE " +
            "tipp.id in (select ident.tipp FROM Identifier AS ident WHERE ident.namespace.id = :namespace group by ident.tipp having count(ident.tipp) > 1)"

    Map result = [:]
    IdentifierNamespace identifierNamespace = IdentifierNamespace.findById(params.id)
    Integer tippCount = TitleInstancePackagePlatform.executeQuery(countQuery, [namespace: identifierNamespace.id])[0]
    List<TitleInstancePackagePlatform> tipps = TitleInstancePackagePlatform.executeQuery(query, [namespace: identifierNamespace.id])

    result.namespace = identifierNamespace.value
    result.count = tippCount
    result.namespaceID = identifierNamespace.id

    if (tipps.size() > 0) {

      result.tipps = tipps
    }

    result
  }

  def setTippsWithoutUrlToDeleted(){
    log.debug("setTippsWithoutUrlToDeleted")

    List<Long> tippsIds = TitleInstancePackagePlatform.executeQuery("select id from TitleInstancePackagePlatform where (url is null or url = '') and status != :removed", [deleted: RDStore.KBC_STATUS_REMOVED])

    Integer tippsToRemoved = tippsIds ? KBComponent.executeUpdate("update KBComponent set status = :removed, lastUpdated = :currentDate where id in (:tippIds) and status != :removed", [removed: RDStore.KBC_STATUS_REMOVED, tippIds: tippsIds, currentDate: new Date()]) : 0

    flash.message = "Tipp without Url: ${tippsIds.size()}, Set tipps to removed: ${tippsToRemoved}"

    redirect(controller: 'admin', action: 'jobs')
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def about() {
    def result = [:]
    def dbmQuery = (sessionFactory.currentSession.createSQLQuery(
            'SELECT filename, id, dateexecuted from databasechangelog order by orderexecuted desc limit 1'
    )).list()
    result.dbmVersion = dbmQuery.size() > 0 ? dbmQuery.first() : ['unkown', 'unkown', 'unkown']
    result

  }

  def findPackagesNeedsAutoUpdates() {
    log.debug("findPackagesWithTippDuplicates::${params}")
    def result = [:]

    List pkgs = []

    CuratoryGroup curatoryGroupFilter = params.curatoryGroup ? genericOIDService.resolveOID(params.curatoryGroup) : null

    params.sort = params.sort ?: 'p.name'

    params.order = params.order ?: 'asc'

    Package.executeQuery(
            "from Package p " +
                    "where p.kbartSource is not null and " +
                    "p.kbartSource.automaticUpdates = true " +
                    "and (p.kbartSource.lastRun is null or (p.kbartSource.lastRun < current_date)) order by ${params.sort} ${params.order}").each { Package p ->
      if (p.kbartSource.needsUpdate()) {
        if(curatoryGroupFilter){
         if(p.curatoryGroups && curatoryGroupFilter in p.curatoryGroups.curatoryGroup) {
           pkgs << p
         }
        }else {
          pkgs << p
        }
      }
    }

    result.pkgs = pkgs

    result
  }

  def findPackagesAutoUpdatesTippsDiff() {
    log.debug("findPackagesAutoUpdatesTippsDiff::${params}")
    def result = [:]

    List pkgs = []

    CuratoryGroup curatoryGroupFilter = params.curatoryGroup ? genericOIDService.resolveOID(params.curatoryGroup) : null

    params.sort = params.sort ?: 'p.name'

    params.order = params.order ?: 'asc'

    Package.executeQuery("from Package p " +
                    "where p.status != ${RDStore.KBC_STATUS_DELETED.id} and p.kbartSource is not null and " +
                    "p.kbartSource.automaticUpdates = true " +
                    " order by ${params.sort} ${params.order}").each { Package p ->

      UpdatePackageInfo updatePackageInfo = p.getLastSuccessfulAutoUpdateInfo()
      if ((updatePackageInfo && updatePackageInfo.countKbartRows > p.tippCount) || !updatePackageInfo || (p.currentTippCount < p.deletedTippCount)) {
        if (curatoryGroupFilter) {
          if(p.curatoryGroups && curatoryGroupFilter in p.curatoryGroups.curatoryGroup) {
            pkgs << p
          }
        } else {
          pkgs << p
        }
      }
    }

    result.pkgs = pkgs

    result
  }

  def autoUpdatesFails() {
    log.debug("autoUpdatesFails::${params}")
    def result = [:]

    List<UpdatePackageInfo> autoUpdates = UpdatePackageInfo.executeQuery("from UpdatePackageInfo where automaticUpdate = true and status = :status and dateCreated > (CURRENT_DATE-1) order by dateCreated desc", [status: RDStore.UPDATE_STATUS_FAILED])

    result.autoUpdates = autoUpdates

    result
  }

  def processTippsNotIndex() {
    log.info("Beginning process tipps not index.")
    executorService.execute({

      int countsTipps = TitleInstancePackagePlatform.executeQuery("select count(tipp.id) from TitleInstancePackagePlatform as tipp")[0]
      int max = 20000
      int count = 0
      List<String> tippUuidsNotInIndex = []
      RestHighLevelClient esclient = ESWrapperService.getClient()

      Set<String> result = []
      try {
        Map<String, Object> recordBatch = ESSearchService.scroll([component_type: 'TitleInstancePackagePlatform'])
        boolean more = true
        while (more) {
          result.addAll(recordBatch.records.collect { record -> record.uuid })
          more = recordBatch.hasMoreRecords
          log.info("more: $more")
          log.info("Size: " + result.size())
          if (more)
            recordBatch = ESSearchService.scroll([component_type: 'TitleInstancePackagePlatform', scrollId: recordBatch.scrollId])
        }

        /*result.each {
            println(it)
        }*/

        log.info("EndSize: " + result.size())

      }
      finally {
        try {
          esclient.close()
        }
        catch (Exception e) {
          log.error("Problem by Close ES Client", e)
        }
      }

      TitleInstancePackagePlatform.withSession { Session sess ->
        for (int offset = 0; offset < countsTipps; offset += max) {
          List<String> tippUuids = TitleInstancePackagePlatform.executeQuery("select tipp.uuid from TitleInstancePackagePlatform as tipp " +
                  " order by id desc", [max: max, offset: offset])

          tippUuids.each {
            count++
            log.info("Process $count of $countsTipps:")
            String uuid = it

            if (!(uuid in result)) {
              tippUuidsNotInIndex << uuid
            }

          }
          sess.flush()
          sess.clear()
        }
      }

      log.info("tipp uuids not in index count: " + tippUuidsNotInIndex.size())

      String fPath = '/tmp/wekb/'

      File file = new File("${fPath}/tippsNotInIndex")
      file.withWriter { out ->
        tippUuidsNotInIndex.each { out.println it }
      }

      String pattern = "yyyy-MM-dd HH:mm:ss"
      String currentDateString = new Date().format("yyyy-MM-dd HH:mm:ss").toString()
      Date currentDate = new SimpleDateFormat(pattern).parse(currentDateString)
      int changeLastUpdated = 0
      max = 20000
      TitleInstancePackagePlatform.withSession { Session sess ->
        for (int offset = 0; offset < tippUuidsNotInIndex.size(); offset += max) {

          List tippUuidsToProcess = tippUuidsNotInIndex.drop(offset).take(max)
            def res = KBComponent.executeUpdate("update KBComponent set lastUpdated = :currentDate where uuid IN (:uuidList)", [uuidList: tippUuidsToProcess, currentDate: currentDate])
            log.info("Updated lastUpdated of ${res} components")
          }
          sess.flush()
          sess.clear()
      }

      log.info("tipp uuids not in index -> change lastUpdated: " + changeLastUpdated)
    })

    log.info("End process tipps not index.")

    redirect(url: request.getHeader('referer'))
  }

}

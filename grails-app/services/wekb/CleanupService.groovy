package wekb


import org.hibernate.SessionFactory
import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.elasticsearch.action.DocWriteResponse
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.xcontent.XContentType
import wekb.ConcurrencyManagerService.Job

@Transactional
class CleanupService {
  SessionFactory sessionFactory
  ESWrapperService ESWrapperService
  DateFormatService dateFormatService
  GenericOIDService genericOIDService

  private def expungeByOIds ( oids, Job j = null ) {

    def result = [report: []]
    def esclient = ESWrapperService.getClient()
    def idx = 0

    for (component_oid in oids) {

      if ( Thread.currentThread().isInterrupted() ) {
        log.debug("Job cancelling ..")
        j.endTime = new Date()
        break;
      }

      idx++

      try {
          log.debug("Expunging ${component_oid}")
          def component = genericOIDService.resolveOID(component_oid)
          String c_id = "${component.class.name}:${component.id}"

          if(recordDeletedKBComponent(component)) {
            def expunge_result = component.expunge()
            log.debug("${expunge_result}");
            DeleteRequest request = new DeleteRequest(
                    ESWrapperService.indicesPerType.get(component.class.simpleName),
                    c_id)
            DeleteResponse deleteResponse = esclient.delete(
                    request, RequestOptions.DEFAULT);
            if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
              log.debug("ES doc not found ${c_id}")
            }
            log.debug("ES deleteResponse: ${deleteResponse}")
            result.report.add(expunge_result)
          }
        j?.setProgress(idx,ids.size())
      }
      catch ( Throwable t ) {
        log.error("problem",t);
        j?.message("Problem expunging component with id ${component_oid}".toString())
      }

    }

    try {
      esclient.close()
    }
    catch (Exception e) {
      log.error("Problem by Close ES Client", e)
    }

    j?.message("Finished deleting ${idx} components.")
    return result
  }


  @Transactional
  def expungeRemovedComponents(Job j = null) {

    log.debug("Process remove candidates");

    def status_removed = RDStore.KBC_STATUS_REMOVED

    List removed_candidates = []

    CuratoryGroup.findAllByStatus(status_removed).each {
      removed_candidates << it.getOID()
    }

    KbartSource.findAllByStatus(status_removed).each {
      removed_candidates << it.getOID()
    }

    Org.findAllByStatus(status_removed).each {
      removed_candidates << it.getOID()
    }

    Package.findAllByStatus(status_removed).each {
      removed_candidates << it.getOID()
    }

    Platform.findAllByStatus(status_removed).each {
      removed_candidates << it.getOID()
    }

    TitleInstancePackagePlatform.findAllByStatus(status_removed).each {
      removed_candidates << it.getOID()
    }

    def result = expungeByOIds(removed_candidates, j)

    log.debug("Done")
    j.endTime = new Date()

    return result
  }

  def cleanUpGorm() {
    log.debug("Clean up GORM");
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
  }

  private boolean recordDeletedKBComponent(def kbComponent){

    DeletedKBComponent deletedKBComponent
    DeletedKBComponent.withTransaction {
      deletedKBComponent = new DeletedKBComponent(uuid: kbComponent.uuid,
              name: kbComponent.name,
              oldDateCreated: kbComponent.dateCreated,
              oldLastUpdated: kbComponent.lastUpdated,
              oldId: kbComponent.id,
              componentType: kbComponent.class.simpleName,
              status: RefdataCategory.lookup(RCConstants.DELETED_COMPONENT_STATUS, "Permanently Deleted"))

      if(!deletedKBComponent.save()){
        return false
      }
    }

    def esclient = ESWrapperService.getClient()

    String recid = "${deletedKBComponent.class.name}:${deletedKBComponent.id}"

    Map idx_record = [:]

    idx_record.uuid = deletedKBComponent.uuid
    idx_record.name = deletedKBComponent.name
    idx_record.componentType = deletedKBComponent.componentType
    idx_record.status = deletedKBComponent.status.value
    idx_record.dateCreated = deletedKBComponent.dateCreated ? dateFormatService.formatIsoTimestamp(deletedKBComponent.dateCreated) : null
    idx_record.lastUpdated = deletedKBComponent.lastUpdated ? dateFormatService.formatIsoTimestamp(deletedKBComponent.lastUpdated) : null
    idx_record.oldDateCreated = deletedKBComponent.oldDateCreated ? dateFormatService.formatIsoTimestamp(deletedKBComponent.oldDateCreated) : null
    idx_record.oldLastUpdated = deletedKBComponent.oldLastUpdated ? dateFormatService.formatIsoTimestamp(deletedKBComponent.oldLastUpdated) : null
    idx_record.oldId = deletedKBComponent.oldId

    IndexRequest request = new IndexRequest("wekbdeletedcomponents")
    request.id(recid)
    String jsonString = idx_record as JSON
    request.source(jsonString, XContentType.JSON)

    IndexResponse indexResponse = esclient.index(request, RequestOptions.DEFAULT)

    try {
        esclient.close()
      }
      catch (Exception e) {
        log.error("Problem by Close ES Client", e)
      }

    if (indexResponse.getResult() != DocWriteResponse.Result.CREATED) {
      DeletedKBComponent.withTransaction {
        deletedKBComponent.delete()
      }
      return false
    }
    return true
  }

  @Transactional
  def cleanupTippIdentifersWithSameNamespace(Job j = null) {
    log.debug("Cleanup Tipp Identifers with same namespace")

    List<IdentifierNamespace> identifierNamespaces = IdentifierNamespace.findAllByTargetType(RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

    String countQuery = "SELECT count(tipp.id) FROM TitleInstancePackagePlatform AS tipp WHERE " +
            "tipp.id in (select ident.tipp.id FROM Identifier AS ident WHERE ident.namespace.id = :namespace group by ident.tipp having count(ident.tipp) > 1)"

    String idQuery = "SELECT tipp.id FROM TitleInstancePackagePlatform AS tipp WHERE " +
            "tipp.id in (select ident.tipp.id FROM Identifier AS ident WHERE ident.namespace.id = :namespace group by ident.tipp having count(ident.tipp) > 1)"


    Integer count = 0
    identifierNamespaces.each {IdentifierNamespace identifierNamespace ->
       Integer tippCount = TitleInstancePackagePlatform.executeQuery(countQuery, [namespace: identifierNamespace.id])[0]
        count = count + tippCount

      List<Long> tippIds = TitleInstancePackagePlatform.executeQuery(idQuery, [namespace: identifierNamespace.id])

      if(tippIds.size() > 0) {
        tippIds.each {Long tippId ->
          println(Identifier.executeQuery("select id from Identifier where tipp.id = :tipp and namespace.id = :namespace order by lastUpdated", [tipp: tippId, namespace: identifierNamespace.id]))
        }

      }
      log.debug("${identifierNamespace.value}")
      log.debug("${count.toString()}")
      log.debug("${tippCount.toString()}")
    }

  log.debug("cleanupTippIdentifersWithSameNamespace: count ${count}")

   // j.endTime = new Date();
  }
}

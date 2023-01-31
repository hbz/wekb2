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

class CleanupService {
  SessionFactory sessionFactory
  ESWrapperService ESWrapperService
  DateFormatService dateFormatService

  private def expungeByIds ( ids, Job j = null ) {

    def result = [report: []]
    def esclient = ESWrapperService.getClient()
    def idx = 0

    for (component_id in ids) {

      if ( Thread.currentThread().isInterrupted() ) {
        log.debug("Job cancelling ..")
        j.endTime = new Date()
        break;
      }

      idx++

      try {
        KBComponent.withNewTransaction {
          log.debug("Expunging ${component_id}");
          KBComponent component = KBComponent.get(component_id);
          String c_id = "${component.class.name}:${component.id}"

          if(recordDeletedKBComponent(component)) {
            def expunge_result = component.expunge();
            log.debug("${expunge_result}");
            DeleteRequest request = new DeleteRequest(
                    ESSearchService.indicesPerType.get(component.class.simpleName),
                    c_id)
            DeleteResponse deleteResponse = esclient.delete(
                    request, RequestOptions.DEFAULT);
            if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
              log.debug("ES doc not found ${c_id}")
            }
            log.debug("ES deleteResponse: ${deleteResponse}")
            result.report.add(expunge_result)
          }
        }
        j?.setProgress(idx,ids.size())
      }
      catch ( Throwable t ) {
        log.error("problem",t);
        j?.message("Problem expunging component with id ${component_id}".toString())
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

    def status_removed = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Removed')

    def removed_candidates = KBComponent.executeQuery('select kbc.id from KBComponent as kbc where kbc.status=:removedStatus',[removedStatus: status_removed])

    def result = expungeByIds(removed_candidates, j)

    log.debug("Done")
    j.endTime = new Date()

    return result
  }

  @Transactional
  def deleteNoUrlPlatforms(Job j = null) {
    log.debug("Delete platforms without URL")

    def status_deleted = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
    def status_current = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Current')

    def delete_candidates = Platform.executeQuery('from Platform as plt where plt.primaryUrl IS NULL and plt.status <> ?', [status_deleted])

    delete_candidates.each { ptr ->
      def repl_crit = Platform.createCriteria()
      def orig_plt = repl_crit.list () {
        isNotNull('primaryUrl')
        eq ('name', ptr.name)
        eq ('status', status_current)
      }

      if ( orig_plt?.size() == 1 ) {
        log.debug("Found replacement platform for ${ptr}")
        def new_plt = orig_plt[0]

        def old_from_combos = Combo.executeQuery("from Combo where fromComponent = :fromCom", [fromCom: ptr])
        def old_to_combos = Combo.executeQuery("from Combo where toComponent = :toCom", [toCom: ptr])

        old_from_combos.each { oc ->
          def existing_new = Combo.executeQuery("from Combo where type = :type and fromComponent = :fromCom and toComponent = :toCom",[type: oc.type, fromCom: new_plt,toCom:  oc.toComponent])

          if (existing_new?.size() == 0 && oc.toComponent != new_plt) {
            oc.fromComponent = new_plt
            oc.save(flush:true)
          }
          else {
            log.debug("New Combo already exists, or would link item to itself.. deleting instead!")
            oc.status = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_DELETED)
            oc.save(flush:true)
          }
        }

        old_to_combos.each { oc ->
          def existing_new = Combo.executeQuery("from Combo where  = :type and fromComponent = :fromCom and toComponent = :toCom",[type: oc.type,toCom: new_plt,fromCom: oc.fromComponent])

          if (existing_new?.size() == 0 && oc.fromComponent != new_plt) {
            oc.toComponent = new_plt
            oc.save(flush:true)
          }
          else {
            log.debug("New Combo already exists, or would link item to itself.. deleting instead!")
            oc.status = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_DELETED)
            oc.save(flush:true)
          }
        }

        ptr.name = "${ptr.name} DELETED"
        ptr.deleteSoft()
      }
      else {
        log.debug("Could not find a valid replacement for platform ${ptr}")
      }

    }
    j.endTime = new Date();
  }

  @Transactional
  def ensureUuids(Job j = null)  {
    log.debug("GOKb missing uuid check..")
    def ctr = 0
    def skipped = []
    KBComponent.withNewSession {
      KBComponent.executeQuery("select kbc.id from KBComponent as kbc where kbc.id is not null and kbc.uuid is null").each { kbc_id ->
        try {
          KBComponent comp = KBComponent.get(kbc_id)
          log.debug("Repair component with no uuid.. ${comp.class.name} ${comp.id} ${comp.name}")
          comp.generateUuid()
          comp.markDirty('uuid')
          log.debug("Generated ${comp.uuid}")
          comp.save(flush:true, failOnError:true)
          comp.discard()
          ctr++
        }
        catch(grails.validation.ValidationException ve){
          log.error("ensureUuids :: Skip component id ${kbc_id} because of validation")
          log.error("${ve.errors}")
          skipped.add(kbc_id)
          skipped++
        }
        catch(Exception e){
          log.error("ensureUuids :: Skip component id ${kbc_id}")
          log.error("${e}")
          skipped.add(kbc_id)
          skipped++
        }
      }
    }
    log.debug("ensureUuids :: ${ctr} components updated with uuid");

    j.message("Finished adding missing uuids (total: ${ctr}, skipped: ${skipped.size()})".toString())

    if (skipped > 0) log.error("ensureUuids :: ${skipped.size()} components skipped when updating with uuid");

    j.endTime = new Date()
  }

  def cleanUpGorm() {
    log.debug("Clean up GORM");
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
  }

  def expungeAll(List components, Job j = null) {
    log.debug("Component bulk expunge");
    def result = [num_requested: components.size(), num_expunged: 0]
    log.debug("Expunging ${result.num_requested} components")
    def esclient = ESWrapperService.getClient()
    def remaining = components
    try {
      while (remaining.size() > 0) {
        def batch = remaining.take(50)
        remaining = remaining.drop(50)

        Combo.executeUpdate("delete from Combo as c where c.fromComponent.id IN (:component) or c.toComponent.id IN (:component)", [component: batch])
        ComponentVariantName.executeUpdate("delete from ComponentVariantName as c where c.owner.id IN (:component)", [component: batch]);

        TippPrice.executeUpdate("delete from TippPrice as cp where cp.tipp.id IN (:component)", [component: batch])

        batch.each {
          KBComponent kbc = KBComponent.get(it)
          def oid = "${kbc.class.name}:${it}"

          if(ESSearchService.indicesPerType.get(kbc.class.simpleName)) {
            DeleteRequest request = new DeleteRequest(
                    ESSearchService.indicesPerType.get(kbc.class.simpleName),
                    oid)
            DeleteResponse deleteResponse = esclient.delete(
                    request, RequestOptions.DEFAULT);
            if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
              log.debug("ES doc not found ${oid}")
            }
            log.debug("ES deleteResponse: ${deleteResponse}")
          }

        }
        result.num_expunged += KBComponent.executeUpdate("delete KBComponent as c where c.id IN (:component)", [component: batch])
        j?.setProgress(result.num_expunged, result.num_requested)
      }
    }
    finally {
      try {
        esclient.close()
      }
      catch (Exception e) {
        log.error("Problem by Close ES Client", e)
      }
    }
    result
  }

  private boolean recordDeletedKBComponent(KBComponent kbComponent){

    DeletedKBComponent deletedKBComponent
    DeletedKBComponent.withTransaction {
      deletedKBComponent = new DeletedKBComponent(uuid: kbComponent.uuid,
              name: kbComponent.name,
              oldDateCreated: kbComponent.dateCreated,
              oldLastUpdated: kbComponent.lastUpdated,
              oldId: kbComponent.id,
              componentType: kbComponent.class.simpleName,
              status: RefdataCategory.lookup(RCConstants.DELETED_KBCOMPONENT_STATUS, "Permanently Deleted"))

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

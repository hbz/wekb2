package wekb

import org.hibernate.SessionFactory
import wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import wekb.ConcurrencyManagerService.Job


class CleanupService {
  GenericOIDService genericOIDService
  SessionFactory sessionFactory

  def cleanUpGorm() {
    log.debug("Clean up GORM");
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
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

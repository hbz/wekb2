package gokb

import de.wekb.helper.RCConstants
import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.gokb.cred.*
import org.springframework.security.access.annotation.Secured

@Slf4j
@Secured(['IS_AUTHENTICATED_FULLY'])
class ComponentController {

  def springSecurityService
  def sessionFactory
  def genericOIDService

  def index() { }

  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def identifierConflicts() {
    log.debug("identifierConflicts :: ${params}")

    def result = [result:'OK', dispersedIds: [], singleTitles: []]
    User user = springSecurityService.currentUser
    def max = params.int('max') ?: user.defaultPageSize
    def offset = params.int('offset') ?: 0
    def components = []
    def dupe_ids = []

    result.max = max

    if (params.id) {
      IdentifierNamespace ns = genericOIDService.resolveOID(params.id)

      if (ns) {
        KBComponent.withNewSession { session ->
          log.debug("fetching results for ${ns} ..")

          RefdataValue status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')

          if (params.ctype == 'st') {

            final String query = '''SELECT kb.kbc_id FROM kbcomponent AS kb WHERE
             kb.kbc_id in (select id_new.id_tipp_fk FROM identifier_new AS id_new WHERE id_new.id_namespace_fk = :namespace group by id_new.id_tipp_fk having count(id_new.id_tipp_fk) > 1)
             and kb.kbc_status_rv_fk != :status
              order by kb.kbc_name limit :limit offset :offset ;
            '''

/*            final String query = '''SELECT ti.kbc_id FROM title_instance_package_platform AS ti NATURAL JOIN kbcomponent as kbc WHERE kbc.kbc_status_rv_fk <> :deleted
              AND (SELECT count(c.combo_id) FROM combo AS c JOIN identifier AS id ON (c.combo_to_fk = id.kbc_id) WHERE
                c.combo_from_fk = kbc.kbc_id
                AND id.id_namespace_fk = :namespace
                AND EXISTS (select kt.kbc_id from title_instance_package_platform as kt where kt.kbc_id = c.combo_from_fk)) > 1
              order by ti.kbc_id limit :limit offset :offset ;
            '''*/

            final String cqry = '''SELECT count(kb.kbc_id) FROM kbcomponent AS kb WHERE
              kb.kbc_id in (select id_new.id_tipp_fk FROM identifier_new AS id_new WHERE id_new.id_namespace_fk = :namespace group by id_new.id_tipp_fk having count(id_new.id_tipp_fk) > 1)
              and kb.kbc_status_rv_fk != :status
            '''

            final singleTitlesCount = session.createSQLQuery(cqry)
              .setParameter('namespace', ns.id)
              .setParameter('status', status_deleted.id)
              .list()

            result.titleCount = singleTitlesCount[0]

            final singleTitles = session.createSQLQuery(query)
              .setParameter('namespace', ns.id)
              .setParameter('status', status_deleted.id)
              .setParameter('limit', max)
              .setParameter('offset', offset)
              .list()

            components = singleTitles
          }

          if (params.ctype == 'di') {
            final String dquery = '''SELECT id.id_id FROM identifier_new AS id WHERE id.id_namespace_fk = :namespace
              AND id.id_value in (select id_new.id_value FROM identifier_new AS id_new WHERE id_new.id_namespace_fk = :namespace group by id_new.id_value having count(id_new.id_value) > 1)
              order by id.id_value limit :limit offset :offset ;
            '''

            final String dcqry = '''SELECT count(id.id_id) FROM identifier_new AS id WHERE id.id_namespace_fk = :namespace
              AND id.id_value in (select id_new.id_value FROM identifier_new AS id_new WHERE id_new.id_namespace_fk = :namespace group by id_new.id_value having count(id_new.id_value) > 1);
            '''

            final dispersedIdsCount = session.createSQLQuery(dcqry)
              .setParameter('namespace', ns.id)
              .list()

            result.idsCount = dispersedIdsCount[0]

            final dispersedIds = session.createSQLQuery(dquery)
              .setParameter('namespace', ns.id)
              .setParameter('limit', max)
              .setParameter('offset', offset)
              .list()

            dupe_ids = dispersedIds
          }
        }

        result.namespace = ns
        result.ctype = params.ctype

        components.each {
          result.singleTitles.add(KBComponent.get(it))
        }

        dupe_ids.each {
          result.dispersedIds.add(Identifier.get(it))
        }
      }
    }

    withFormat {
      html { result }
      json { render result as JSON }
    }
  }
}

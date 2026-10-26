package wekb

import grails.gorm.transactions.Transactional
import org.hibernate.SessionFactory
import wekb.tenant.TenantContext

@Transactional
class TenantSwitchService {

    SessionFactory sessionFactory

    def withTenantRole(Closure c) {

        String tenant = TenantContext.getTenant()

        Map<String, String> tenantTimeouts = [
                'wekb_without_login': '120s',
                'wekb_with_login'   : '0'
        ]

        if (!tenantTimeouts.containsKey(tenant)) {
            throw new IllegalArgumentException(
                    "Unsupported tenant role: ${tenant}"
            )
        }

        def session = sessionFactory.currentSession

        return session.doReturningWork { conn ->

            conn.createStatement().withCloseable { stmt ->
                stmt.execute("SET LOCAL ROLE ${tenant}")
                stmt.execute(
                        "SET LOCAL statement_timeout = '${tenantTimeouts[tenant]}'"
                )
            }

            return c.call()
        }
    }
}
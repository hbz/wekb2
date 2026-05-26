package wekb

import grails.gorm.transactions.Transactional
import org.hibernate.SessionFactory
import wekb.tenant.TenantContext

@Transactional
class TenantSwitchService {

    SessionFactory sessionFactory

    def withTenantRole(Closure c) {
        def tenant = TenantContext.getTenant()

        if (!tenant) {
            throw new IllegalStateException('No tenant role set in TenantContext')
        }

        def conn = sessionFactory.currentSession.connection()

        try {
            conn.createStatement().execute("SET ROLE ${tenant}")
            return c.call()
        } finally {
            conn.createStatement().execute("RESET ROLE")
        }
    }


}

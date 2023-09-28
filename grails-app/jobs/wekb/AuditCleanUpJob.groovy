package wekb

import grails.core.GrailsApplication
import grails.util.Holders
import groovy.sql.Sql

import javax.sql.DataSource

class AuditCleanUpJob {

    GrailsApplication grailsApplication

    // Allow only one run at a time.
    static concurrent = false

    static triggers = {
        // Cron timer.
        cron name: 'AuditCleanUpJob', cronExpression: "0 0 6 * * ? *" // daily at 6:00 am
    }

    def execute() {
        if (grailsApplication.config.getProperty('wekb.auditCleanUpJob.enabled', Boolean)) {
            log.debug("Beginning scheduled Audit CleanUp Job.")

            Sql sql = new Sql(Holders.grailsApplication.mainContext.getBean('dataSource') as DataSource)
            sql.execute('''DELETE FROM audit_log WHERE date_created <= now() - INTERVAL '30 DAYS' ''')


            log.info("Audit CleanUp Job completed.")
        } else {
            log.info("Audit CleanUp Job is not enabled - set config.wekb.auditCleanUpJob.enabled = true");
        }
    }
}

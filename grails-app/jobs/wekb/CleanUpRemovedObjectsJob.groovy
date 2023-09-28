package wekb

import grails.core.GrailsApplication
import grails.plugins.mail.MailService
import wekb.helper.RDStore
import wekb.system.FTControl
import wekb.utils.ServerUtils

class CleanUpRemovedObjectsJob {

    MailService mailService
    GrailsApplication grailsApplication
    CleanupService cleanupService

    static triggers = {
        // Cron timer.
        cron name: 'CleanUpRemovedObjects', cronExpression: "0 0 7 ? * FRI *" // ever Friday at 07:00
    }

    def execute() {
        if (grailsApplication.config.getProperty('wekb.cleanUpRemovedObjects', Boolean)) {
            log.debug("Beginning scheduled cleanUp removed objects job.")
            cleanUpRemovedObjects()
            log.info("cleanUp removed objects job completed.")
        } else {
            log.info("cleanUp removed objects job is not enabled - set config.wekb.cleanUpRemovedObjects = true");
        }
    }

    private cleanUpRemovedObjects() {

        List removedDomainClassObjects = ['CuratoryGroup',
                                          'KbartSource',
                                          'Org',
                                          'Package',
                                          'Platform',
                                          'TitleInstancePackagePlatform'
        ]

        Map result = [:]
        result.dbEntries = [:]

        removedDomainClassObjects.each{ String domainClassName ->
            result.dbEntries[domainClassName] = [:]

            String query = "select count(*) from ${domainClassName}"
            result.dbEntries[domainClassName].countBeforeRemovedInDB = FTControl.executeQuery(query+ " where status = :status", [status: RDStore.KBC_STATUS_REMOVED])[0]

        }


        cleanupService.expungeRemovedComponents()

        removedDomainClassObjects.each{ String domainClassName ->
            String query = "select count(*) from ${domainClassName}"
            result.dbEntries[domainClassName].countAfterRemovedInDB = FTControl.executeQuery(query+ " where status = :status", [status: RDStore.KBC_STATUS_REMOVED])[0]

        }

        String currentServer = ServerUtils.getCurrentServer()
        String subjectSystemPraefix = (currentServer == ServerUtils.SERVER_PROD) ? "" : (ServerUtils.getCurrentServerSystemId() + " - ")
        String mailSubject = subjectSystemPraefix + "we:kb cleanUp Removed Objects Job"
        String currentSystemId = ServerUtils.getCurrentServerSystemId()

        try {
            mailService.sendMail {
                to "moetez.djebeniani@hbz-nrw.de"
                from "wekb Server <wekb-cleanUpRemovedObjectsJob@wekbServer>"
                subject mailSubject
                html(view: "/mailTemplate/html/removedObjectsJobMail", model: [dbEntries: result.dbEntries])
            }
        } catch (Exception e) {
            String eMsg = e.message
            log.error("cleanUpRemovedObjectsJob - cleanUpRemovedObjects() :: Unable to perform email due to exception ${eMsg}")
        }
    }
}

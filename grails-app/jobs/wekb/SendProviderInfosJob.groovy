package wekb

import grails.core.GrailsApplication
import grails.plugins.mail.MailService
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.core.CountRequest
import org.elasticsearch.client.core.CountResponse
import org.elasticsearch.client.indices.GetIndexRequest
import wekb.helper.RDStore
import wekb.system.FTControl
import wekb.utils.ServerUtils

class SendProviderInfosJob {

    MailService mailService
    GrailsApplication grailsApplication
    def ESWrapperService

    static triggers = {
        // Cron timer.
        cron name: 'SendProviderInfos', cronExpression: "0 0 7 * * ? *" // daily at 07:00
    }

    def execute() {
        if (grailsApplication.config.getProperty('wekb.sendProviderInfosJob', Boolean)) {
            log.debug("Beginning scheduled send job infos job.")
            sendPackageUpdateInfosOfProvider()
            log.info("send job infos job completed.")
        } else {
            log.info("automatic send job infos Job is not enabled - set config.wekb.sendProviderInfosJob = true");
        }
    }

    private sendPackageUpdateInfosOfProvider() {

        List<UpdatePackageInfo> autoUpdates = UpdatePackageInfo.executeQuery("from UpdatePackageInfo where automaticUpdate = true and status = :status and dateCreated > (CURRENT_DATE-1) and pkg.status != :pkgStatus order by dateCreated desc", [pkgStatus: RDStore.KBC_STATUS_DELETED, status: RDStore.UPDATE_STATUS_FAILED])

        String currentServer = ServerUtils.getCurrentServer()
        String subjectSystemPraefix = (currentServer == ServerUtils.SERVER_PROD) ? "" : (ServerUtils.getCurrentServerSystemId() + " - ")
        String mailSubject = subjectSystemPraefix + "we:kb Manage Package Update Jobs"
        String currentSystemId = ServerUtils.getCurrentServerSystemId()

        try {
            mailService.sendMail {
                to "laser@hbz-nrw.de", "moetez.djebeniani@hbz-nrw.de"
                from "wekb Server <wekb-managePackageUpdateJobs@wekbServer>"
                subject mailSubject
                html(view: "/mailTemplate/html/packageUpdateJobsMail", model: [autoUpdates: autoUpdates])
            }
        } catch (Exception e) {
            String eMsg = e.message
            log.error("SendProviderInfos - sendPackageUpdateInfosOfProvider() :: Unable to perform email due to exception ${eMsg}")
        }
    }
}

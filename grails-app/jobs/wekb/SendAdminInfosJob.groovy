package wekb

import grails.core.GrailsApplication
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.core.CountRequest
import org.elasticsearch.client.core.CountResponse
import org.elasticsearch.client.indices.GetIndexRequest
import wekb.helper.RDStore
import wekb.system.FTControl
import wekb.utils.ServerUtils
import grails.plugins.mail.MailService

import java.time.LocalDateTime
import java.time.ZoneId

class SendAdminInfosJob {

    MailService mailService
    GrailsApplication grailsApplication
    def ESWrapperService

    static triggers = {
        // Cron timer.
        cron name: 'SendAdminInfos', cronExpression: "0 0 7 * * ? *" // daily at 07:00
    }

    def execute() {
        if (grailsApplication.config.getProperty('wekb.sendJobInfosJob', Boolean)) {
            log.debug("Beginning scheduled send job infos job.")
            sendPackageUpdateInfos()
            //sendEsIndexCheck()
            log.info("send job infos job completed.")
        } else {
            log.info("automatic send job infos Job is not enabled - set config.wekb.sendJobInfosJob = true");
        }
    }

    private sendPackageUpdateInfos() {

        List<UpdatePackageInfo> autoUpdates = UpdatePackageInfo.executeQuery("from UpdatePackageInfo where automaticUpdate = true and status = :status and dateCreated > (CURRENT_DATE-1) and pkg.status != :pkgStatus order by dateCreated desc", [pkgStatus: RDStore.KBC_STATUS_DELETED, status: RDStore.UPDATE_STATUS_FAILED])

        List<UpdatePackageInfo> filteredAutoUpdates = []
        LocalDateTime today = LocalDateTime.now()
        LocalDateTime yesterday = today.minusDays(1)
        autoUpdates.each { UpdatePackageInfo updatePackageInfo ->
            if(updatePackageInfo.dateCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() > yesterday){
                filteredAutoUpdates << updatePackageInfo
            }
        }

        String currentServer = ServerUtils.getCurrentServer()
        String subjectSystemPraefix = (currentServer == ServerUtils.SERVER_PROD) ? "" : (ServerUtils.getCurrentServerSystemId() + " - ")
        String mailSubject = subjectSystemPraefix + "we:kb Manage Package Update Jobs"
        String currentSystemId = ServerUtils.getCurrentServerSystemId()

        try {
            mailService.sendMail {
                to "laser@hbz-nrw.de", "moetez.djebeniani@hbz-nrw.de"
                from "wekb Server <wekb-managePackageUpdateJobs@wekbServer>"
                subject mailSubject
                html(view: "/mailTemplate/html/packageUpdateJobsMail", model: [autoUpdates: filteredAutoUpdates])
            }
        } catch (Exception e) {
            String eMsg = e.message
            log.error("SendAdminInfosJob - sendPackageUpdateInfos() :: Unable to perform email due to exception ${eMsg}")
        }
    }

    private sendEsIndexCheck() {
        String currentServer = ServerUtils.getCurrentServer()
        String subjectSystemPraefix = (currentServer == ServerUtils.SERVER_PROD) ? "" : (ServerUtils.getCurrentServerSystemId() + " - ")
        String mailSubject = subjectSystemPraefix + "we:kb ES Index Alert"
        String currentSystemId = ServerUtils.getCurrentServerSystemId()

        RestHighLevelClient esclient = ESWrapperService.getClient()

        boolean sendMail = false

        List indices = []
        def esIndices = ESWrapperService.es_indices
        esIndices.each { def indice ->
            Map indexInfo = [:]
            indexInfo.name = indice.value
            indexInfo.type = indice.key

            GetIndexRequest request = new GetIndexRequest(indice.value)

            if (esclient.indices().exists(request, RequestOptions.DEFAULT)) {
                CountRequest countRequest = new CountRequest(indice.value)
                CountResponse countResponse = esclient.count(countRequest, RequestOptions.DEFAULT)
                indexInfo.countIndex = countResponse ? countResponse.getCount().toInteger() : 0
            } else {
                indexInfo.countIndex = 0
            }

            String query = "select count(id) from ${ESWrapperService.typePerIndex.get(indice.value)}"
            indexInfo.countDB = FTControl.executeQuery(query)[0]

            if ((indexInfo.countDB - indexInfo.countIndex) > 100) {
                sendMail = true
            }

            indices << indexInfo
        }

        try {
            esclient.close()
        }
        catch (Exception e) {
            log.error("Problem by Close ES Client", e)
        }

        if (sendMail) {

            try {
                mailService.sendMail {
                    to "laser@hbz-nrw.de","moetez.djebeniani@hbz-nrw.de"
                    from "wekb Server <wekb-esIndexCheckJob@wekbServer>"
                    subject mailSubject
                    html(view: "/mailTemplate/html/indexAlert", model: [indices: indices])
                }
            } catch (Exception e) {
                String eMsg = e.message
                log.error("SendAdminInfosJob - sendEsIndexCheck() :: Unable to perform email due to exception ${eMsg}")
            }
        }
    }
}

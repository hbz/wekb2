package wekb

import grails.core.GrailsApplication

class AutoUpdateCounterSourcesJob {

    AutoUpdatePlatformsService autoUpdatePlatformsService
    GrailsApplication grailsApplication

    // Allow only one run at a time.
    static concurrent = false

    static triggers = {
        // Cron timer.
        cron name: 'AutoUpdateCounterSourcesTrigger', cronExpression: "0 30 19 * * ? *" // daily at 7:30 pm
        //cron name: 'AutoUpdateCounterSourcesTrigger', cronExpression: "0 0/5 * * * ? *" // for testing: every 5 minutes
    }

    def execute() {
        if (grailsApplication.config.getProperty('wekb.counterSourceUpdate.enabled', Boolean)) {
            log.info("Beginning scheduled auto update COUNTER sources job.")
            autoUpdatePlatformsService.updatePlatformCounterSources()
            log.info("auto update COUNTER sources job completed.")
        } else {
            log.info("automatic COUNTER source update is not enabled - set config.wekb.counterSourceUpdate_enabled = true");
        }
    }

}

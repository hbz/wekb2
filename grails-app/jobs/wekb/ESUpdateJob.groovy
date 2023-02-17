package wekb

import grails.core.GrailsApplication

class ESUpdateJob {

  // Allow only one run at a time.
  static concurrent = false

  def FTUpdateService
  GrailsApplication grailsApplication

  static triggers = {
    // Cron timer.
    cron name: 'ESUpdateTrigger', cronExpression: "0 0/5 * * * ?", startDelay:500000
  }

  def execute() {
    if ( grailsApplication.config.getProperty('wekb.ftupdate_enabled', Boolean) ) {
      log.info ("Beginning scheduled es update job.")
      FTUpdateService.updateFTIndexes()
      log.info ("ESUpdateJob completed.")
    }
    else {
      log.info("FTUpdate is not enabled - set config.ftupdate_enabled = true in config to enable")
    }
  }
}

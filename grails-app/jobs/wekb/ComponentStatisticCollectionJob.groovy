package wekb

import grails.core.GrailsApplication

class ComponentStatisticCollectionJob {

  static concurrent = false

  ComponentStatisticService componentStatisticService
  GrailsApplication grailsApplication

  static triggers = {
    cron name: 'ComponentStatisticCollectionJobTrigger', cronExpression: "0 0/30 * * * ?", startDelay:700000
  }

  def execute() {
    log.info("Beginning scheduled statistics update job.")
    if (grailsApplication.config.getProperty('wekb.enable_statsrewrite', Boolean)) {
      log.info("Also updating existing stats.")
      componentStatisticService.updateCompStats(12,0,true)
    }
    else{
      log.info("Not updating existing stats.")
      componentStatisticService.updateCompStats()
    }
  }
}

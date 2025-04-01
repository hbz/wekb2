package wekb

import grails.core.GrailsApplication
import wekb.AutoUpdatePackagesService

import java.util.concurrent.ExecutorService

class AutoUpdatePackagesJob {

  AutoUpdatePackagesService autoUpdatePackagesService
  GrailsApplication grailsApplication

  ExecutorService executorService
  // Allow only one run at a time.
  static concurrent = false

  static triggers = {
    // Cron timer.
    cron name: 'AutoUpdatePackageTrigger', cronExpression: "0 0 20 * * ? *" // daily at 8:00 pm
// for testing: every 5 minutes   cron name: 'AutoUpdatePackageTrigger', cronExpression: "0 1/5 * * * ? *" // daily at 6:00 am
  }

  def execute() {
    if (grailsApplication.config.getProperty('wekb.packageUpdate.enabled', Boolean)) {
      log.debug("Beginning scheduled auto update packages job.")

        autoUpdatePackagesService.findPackageToUpdateAndUpdate(true)
      //autoUpdatePackagesService.findPackageToUpdateAndUpdate(false)

      log.info("auto update packages job completed.")
    } else {
      log.info("automatic package update is not enabled - set config.wekb.packageUpdate_enabled = true");
    }
  }
}

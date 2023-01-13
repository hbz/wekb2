package wekb.utils


import grails.util.Environment
import grails.util.Holders

class ServerUtils {

    static final SERVER_LOCAL = 'SERVER_LOCAL'
    static final SERVER_DEV   = 'SERVER_DEV'
    static final SERVER_QA    = 'SERVER_QA'
    static final SERVER_PROD  = 'SERVER_PROD'



    static String getCurrentServer() {

        if (! Environment.isDevelopmentMode()) {

            switch (Holders.grailsApplication.config.systemId) {
                case 'we:kb-Dev':
                    return SERVER_DEV
                    break
                case 'we:kb-Qa':
                    return SERVER_QA
                    break
                case 'we:kb-Prod':
                    return SERVER_PROD
                    break
            }
        }

        return SERVER_LOCAL
    }

    static String getCurrentServerSystemId() {

        if (! Environment.isDevelopmentMode()) {

            switch (Holders.grailsApplication.config.systemId) {
                case 'we:kb-Dev':
                    return 'we:kb-Dev'
                    break
                case 'we:kb-Qa':
                    return 'we:kb-Qa'
                    break
                case 'we:kb-Prod':
                    return 'we:kb-Prod'
                    break
            }
        }

        return "we:kb-Local"
    }
}

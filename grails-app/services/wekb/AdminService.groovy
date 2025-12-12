package wekb

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import wekb.helper.RDStore
import wekb.tools.UrlToolkit

@Transactional
class AdminService {

    def sessionRegistry
    SpringSecurityService springSecurityService
    FtpConnectService ftpConnectService
    ExportService exportService
    FileCheckService fileCheckService

    int setKbartFileHashs(Date lastRun) {
        int kbartFileHashs = 0
        KbartSource.findAllByAutomaticUpdates(true).each { KbartSource kbartSource ->
            if(kbartSource.lastRun > lastRun) {
                if (kbartSource && (kbartSource.lastUpdateUrl || kbartSource.url)) {
                    if ((UrlToolkit.containsDateStamp(kbartSource.url) || UrlToolkit.containsDateStampPlaceholder(kbartSource.url)) && kbartSource.lastUpdateUrl) {
                        File file = exportService.kbartFromUrl(kbartSource.lastUpdateUrl)
                        if (file) {
                            String newHash = fileCheckService.computeHash(file)
                            kbartSource.kbartFileHash = newHash
                            kbartSource.save()
                            kbartFileHashs++
                        }
                    } else {
                        File file = exportService.kbartFromUrl(kbartSource.url)
                        if (file) {
                            String newHash = fileCheckService.computeHash(file)
                            kbartSource.kbartFileHash = newHash
                            kbartSource.save()
                            kbartFileHashs++
                        }
                    }

                } else if (kbartSource.ftpServerUrl) {
                    File file = ftpConnectService.ftpConnectAndGetFile(kbartSource)
                    if (file) {
                        String newHash = fileCheckService.computeHash(file)
                        kbartSource.kbartFileHash = newHash
                        kbartSource.save()
                        kbartFileHashs++
                    }
                }
            }
        }
        return kbartFileHashs

    }


    int getNumberOfActiveUsers() {
        getActiveUsers( (1000 * 60 * 10) ).size() // 10 minutes
    }

    List getActiveUsers(long ms) {
        List result = []

        sessionRegistry.getAllPrincipals().each { user ->
            List lastAccessTimes = []

            sessionRegistry.getAllSessions(user, false).each { userSession ->
                if (user.username == springSecurityService.getCurrentUser()?.username) {
                    userSession.refreshLastRequest()
                }
                lastAccessTimes << userSession.getLastRequest().getTime()
            }
            if (lastAccessTimes.max() > System.currentTimeMillis() - ms) {
                result.add(user)
            }
        }
        result
    }


    int removeTippDuplicatesByTitleIDOfPkg(Package aPackage){
        List<TitleInstancePackagePlatform> tippsDuplicatesByTitleID = aPackage.findTippDuplicatesByTitleIDWithOutRemoved()

        int countRemoved = 0
        tippsDuplicatesByTitleID.eachWithIndex{ TitleInstancePackagePlatform sourceTipp, int i ->
            if(sourceTipp.status == RDStore.KBC_STATUS_CURRENT) {
                List tipps
                if (sourceTipp.getTippDuplicatesByTitleIDWithoutRemovedCount() > 0) {
                    tipps = sourceTipp.findTippDuplicatesByTitleIDWithoutRemoved()
                }

                tipps.each { TitleInstancePackagePlatform targetTipp ->
                    if (targetTipp.status != RDStore.KBC_STATUS_CURRENT && targetTipp.status != RDStore.KBC_STATUS_REMOVED) {
                        targetTipp.status = RDStore.KBC_STATUS_REMOVED
                        targetTipp.save()
                        countRemoved++
                    }
                }

            }
        }

        if(countRemoved > 0){
            aPackage.lastUpdated = new Date()
            aPackage.save()
        }

        return countRemoved

    }

    int removeTippDuplicatesByUrlOfPkg(Package aPackage){

        List<TitleInstancePackagePlatform> tippsDuplicatesByUrl = aPackage.findTippDuplicatesByURL()

        int countRemoved = 0
        tippsDuplicatesByUrl.each {TitleInstancePackagePlatform sourceTipp, int i ->
            if(sourceTipp.status == RDStore.KBC_STATUS_CURRENT) {
                List tipps
                if (sourceTipp.getTippDuplicatesByURLCount() > 0) {
                    tipps = sourceTipp.findTippDuplicatesByURL()
                }

                tipps.each { TitleInstancePackagePlatform targetTipp ->
                    if (targetTipp.status != RDStore.KBC_STATUS_CURRENT && targetTipp.status != RDStore.KBC_STATUS_REMOVED) {
                        targetTipp.status = RDStore.KBC_STATUS_REMOVED
                        targetTipp.save()
                        countRemoved++
                    }
                }

            }

        }

        if(countRemoved > 0){
            aPackage.lastUpdated = new Date()
            aPackage.save()
        }

        return countRemoved

    }
}

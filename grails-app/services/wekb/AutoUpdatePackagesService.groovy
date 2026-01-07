package wekb

import grails.core.GrailsApplication
import grails.plugins.mail.MailService
import org.apache.commons.io.FileUtils
import org.apache.commons.validator.routines.UrlValidator
import wekb.tools.UrlToolkit
import wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import org.apache.commons.lang.StringUtils
import wekb.utils.ServerUtils

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService

import groovyx.gpars.GParsPool

import java.util.concurrent.Executors
import java.util.regex.Matcher
import java.util.regex.Pattern

@Transactional
class AutoUpdatePackagesService {

    ExecutorService executorService
    FtpConnectService ftpConnectService
    FileCheckService fileCheckService
    KbartProcessService kbartProcessService
    GrailsApplication grailsApplication
    MailService mailService
    ExportService exportService

    static final THREAD_POOL_SIZE = 4
    //private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)
    private static boolean started = false

    void findPackageToUpdateOnAutoUpdate(boolean onlyRowsWithLastChanged = false) {
        //Thread.currentThread().name = "AutoUpdate-All"
        if (!started) {
            //started = true
            List packageNeedsUpdate = []
            def updPacks = Package.executeQuery(
                    "from Package p " +
                            "where p.kbartSource is not null and " +
                            "p.kbartSource.automaticUpdates = true " +
                            "and (p.kbartSource.lastRun is null or p.kbartSource.lastRun < current_date) and p.kbartSource.status not in (:status) order by p.kbartSource.lastRun", [status:  [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED]])
            updPacks.each { Package p ->
                if (p.kbartSource.needsUpdate()) {
                    packageNeedsUpdate << p
                }else if (!p.kbartSource.frequency){
                    if (!(p.status in [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED])) {
                        UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: p, startTime: new Date(), endTime: new Date(), status: RDStore.UPDATE_STATUS_FAILED, description: "Source frequency is not set. Update for this package is not starting.", onlyRowsWithLastChanged: onlyRowsWithLastChanged, automaticUpdate: true, kbartHasWekbFields: false, lastRun: p.kbartSource.lastRun, lastUpdateUrl: p.kbartSource.lastUpdateUrl)
                        updatePackageInfo.save()
                    }
                }
            }
            log.info("findPackageToUpdateOnAutoUpdate: Package with KbartSource and lastRun < currentDate (${packageNeedsUpdate.size()})")
            if (packageNeedsUpdate.size() > 0) {
                GParsPool.withPool(THREAD_POOL_SIZE) { pool ->
                    packageNeedsUpdate.anyParallel { aPackage ->
                        try {
                            Thread.currentThread().name = "AutoUpdate-${aPackage.id}"
                            startAutoPackageUpdate(aPackage, onlyRowsWithLastChanged)
                        }catch (Exception exception) {
                            log.error("Error by findPackageToUpdateAndUpdate (${aPackage.id} -> ${aPackage.name}): ${exception.message}")
                            //exception.printStackTrace()

                            if (grailsApplication.config.getProperty('grails.mail.disabled', Boolean)) {
                                log.warn 'findPackageToUpdateOnAutoUpdate mail failed due grails.mail.disabled = true'

                            }else {

                                String currentServer = ServerUtils.getCurrentServer()
                                String subjectSystemPraefix = (currentServer == ServerUtils.SERVER_PROD) ? "" : (ServerUtils.getCurrentServerSystemId() + " - ")
                                String mailSubject = subjectSystemPraefix + "we:kb Auto Update Packages Job"
                                String currentSystemId = ServerUtils.getCurrentServerSystemId()


                                try {
                                    mailService.sendMail {
                                        to "wekb@hbz-nrw.de", "moetez.djebeniani@hbz-nrw.de"
                                        from "wekb Server <wekb-autoUpdatePackagesJob@wekbServer>"
                                        subject mailSubject
                                        text "Error by Package to Update on Auto Update (${aPackage.id} -> ${aPackage.name}: ${exception.message})"
                                    }
                                } catch (Exception e) {
                                    String eMsg = e.message
                                    log.error("autoUpdatePackagesJob - findPackageToUpdateOnAutoUpdate :: Unable to perform email due to exception ${eMsg}")
                                }
                            }
                        }
                    }
                }



                /*packageNeedsUpdate.eachWithIndex { aPackage, i ->
                    executorService.submit {
                        Thread.currentThread().name = "AutoUpdate-${i}"

                        try {
                            startAutoPackageUpdate(aPackage, onlyRowsWithLastChanged)
                        }catch (Exception exception) {
                            log.error("Error by findPackageToUpdateOnAutoUpdate (${aPackage.id} -> ${aPackage.name}): ${exception.message}")
                            //exception.printStackTrace()

                            if (grailsApplication.config.getProperty('grails.mail.disabled', Boolean)) {
                                log.warn 'findPackageToUpdateOnAutoUpdate mail failed due grails.mail.disabled = true'

                            }else {

                                String currentServer = ServerUtils.getCurrentServer()
                                String subjectSystemPraefix = (currentServer == ServerUtils.SERVER_PROD) ? "" : (ServerUtils.getCurrentServerSystemId() + " - ")
                                String mailSubject = subjectSystemPraefix + "we:kb Auto Update Packages Job"
                                String currentSystemId = ServerUtils.getCurrentServerSystemId()


                                try {
                                    mailService.sendMail {
                                        to "wekb@hbz-nrw.de", "moetez.djebeniani@hbz-nrw.de"
                                        from "wekb Server <wekb-autoUpdatePackagesJob@wekbServer>"
                                        subject mailSubject
                                        text "Error by Package to Update on Auto Update (${aPackage.id} -> ${aPackage.name})"
                                    }
                                } catch (Exception e) {
                                    String eMsg = e.message
                                    log.error("autoUpdatePackagesJob - findPackageToUpdateOnAutoUpdate :: Unable to perform email due to exception ${eMsg}")
                                }
                            }

                        }
                    }
                }*/
            }
            //started = false
            //executorService.shutdown()
        } else {
            log.info("AutoUpdate running!")
        }

    }


    static List<URL> getUpdateUrls(String url, Date lastProcessingDate, Date packageCreationDate) {
        String newUrl = url ? url.replace(' ', '%20') : ''
        if (lastProcessingDate == null) {
            lastProcessingDate = packageCreationDate
        }
        if (StringUtils.isEmpty(newUrl) || lastProcessingDate == null) {
            return new ArrayList<URL>()
        }
        if (UrlToolkit.containsDateStamp(newUrl) || UrlToolkit.containsDateStampPlaceholder(newUrl)) {
            return UrlToolkit.getUpdateUrlList(newUrl, lastProcessingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
        } else {
            return Arrays.asList(new URL(newUrl))
        }
    }

    void startAutoPackageUpdate(Package pkg, boolean onlyRowsWithLastChanged = false) {
        log.info("Begin startAutoPackageUpdate Package ($pkg.name)")
        List kbartRows = []
        String lastUpdateURL = pkg.kbartSource.lastUpdateUrl ?: pkg.kbartSource.url
        Date startTime = new Date()
        if (pkg.status in [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED]) {
            UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: pkg, startTime: startTime, endTime: new Date(), status: RDStore.UPDATE_STATUS_SUCCESSFUL, description: "Package status is ${pkg.status.value}. Update for this package is not starting.", onlyRowsWithLastChanged: onlyRowsWithLastChanged, automaticUpdate: true, kbartHasWekbFields: false, lastRun: pkg.kbartSource.lastRun, frequency: pkg.kbartSource.frequency)
            updatePackageInfo.save()
        }else if (!pkg.nominalPlatform) {
            UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: pkg, startTime: startTime, endTime: new Date(), status: RDStore.UPDATE_STATUS_SUCCESSFUL, description: "No nominal platform is set for this package! Please put a nominal platform on the package level.", onlyRowsWithLastChanged: onlyRowsWithLastChanged, automaticUpdate: true, kbartHasWekbFields: false, lastRun: pkg.kbartSource.lastRun, frequency: pkg.kbartSource.frequency)
            updatePackageInfo.save()
        } else {
            UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: pkg, startTime: startTime, status: RDStore.UPDATE_STATUS_SUCCESSFUL, description: "Starting Update package.", onlyRowsWithLastChanged: onlyRowsWithLastChanged, automaticUpdate: true, lastRun: pkg.kbartSource.lastRun, frequency: pkg.kbartSource.frequency).save()
            try {
                if (pkg.kbartSource) {
                    if (pkg.kbartSource.defaultSupplyMethod == RDStore.KS_DSMETHOD_FTP) {
                        updatePackageInfo.updateFromFTP = true
                        updatePackageInfo.lastUpdateUrl = null
                        updatePackageInfo.updateUrl = null
                        updatePackageInfo.save()
                        if (pkg.kbartSource.ftpServerUrl) {
                            File file = ftpConnectService.ftpConnectAndGetFile(pkg.kbartSource)


                            if (file) {
                                if(fileCheckService.hasFileChanged(pkg.kbartSource, file)){
                                    kbartRows = kbartProcessService.kbartProcess(file, "", updatePackageInfo)

                                    if (kbartRows.size() > 0) {
                                        updatePackageInfo = kbartProcessService.kbartImportProcess(kbartRows, pkg, "", updatePackageInfo, onlyRowsWithLastChanged)

                                        if(updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED) {
                                            String newHash = fileCheckService.computeHash(file)
                                            KbartSource src = KbartSource.get(pkg.kbartSource.id)
                                            src.kbartFileHash = newHash
                                            src.save()
                                        }
                                    } else {
                                        if(updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED) {
                                            UpdatePackageInfo.withTransaction {
                                                updatePackageInfo.description = "The KBART File is empty!"
                                                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                                updatePackageInfo.endTime = new Date()
                                                updatePackageInfo.updateUrl = lastUpdateURL
                                                updatePackageInfo.save()
                                            }
                                        }
                                    }

                                }else {
                                    if(updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED) {
                                        UpdatePackageInfo.withTransaction {
                                            updatePackageInfo.description = "No changes in File. Is same File from last update!"
                                            updatePackageInfo.status = RDStore.UPDATE_STATUS_INFO
                                            updatePackageInfo.endTime = new Date()
                                            updatePackageInfo.save()
                                        }

                                        KbartSource src = KbartSource.get(pkg.kbartSource.id)
                                        src.lastRun = new Date()
                                        src.save()
                                    }
                                }

                            } else {
                                if(updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED) {
                                    UpdatePackageInfo.withTransaction {
                                        updatePackageInfo.description = "No KBART File found by FTP Server!"
                                        updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                        updatePackageInfo.endTime = new Date()
                                        updatePackageInfo.save()
                                    }
                                }
                            }

                        } else {
                            UpdatePackageInfo.withTransaction {
                                //UpdatePackageInfo updatePackageFail = new UpdatePackageInfo()
                                updatePackageInfo.description = "No FTP server url define in the source of the package."
                                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                updatePackageInfo.startTime = startTime
                                updatePackageInfo.endTime = new Date()
                                updatePackageInfo.pkg = pkg
                                updatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                                updatePackageInfo.automaticUpdate = true
                                updatePackageInfo.save()
                            }
                        }
                    }else if (pkg.kbartSource.defaultSupplyMethod == RDStore.KS_DSMETHOD_HTTP_URL) {
                        updatePackageInfo.updateFromURL = true
                        updatePackageInfo.lastUpdateUrl = lastUpdateURL
                        updatePackageInfo.save()
                        if (pkg.kbartSource.url) {
                            List<URL> updateUrls
                            if (pkg.getTippCount() <= 0 || pkg.kbartSource.lastRun == null) {
                                updateUrls = new ArrayList<>()
                                if(UrlToolkit.containsDateStamp(pkg.kbartSource.url)){
                                    Matcher urlMatcher = Pattern.compile("(.*[\\W_])([\\d]{4}-[\\d]{2}-[\\d]{2})([\\W_].*)").matcher(pkg.kbartSource.url)
                                    if (!urlMatcher.matches()){
                                        urlMatcher = Pattern.compile("(.*[\\W_])([\\d]{4}_[\\d]{2}_[\\d]{2})([\\W_].*)").matcher(pkg.kbartSource.url)
                                    }
                                    if (urlMatcher.matches()) {
                                        String dateString = urlMatcher.group(2)

                                        if (dateString) {
                                            LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
                                            Date dateFromUrl = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                                            updateUrls = getUpdateUrls(pkg.kbartSource.url, dateFromUrl, pkg.dateCreated)
                                        } else {
                                            updateUrls.add(new URL(pkg.kbartSource.url.replace(' ', '%20')))
                                        }
                                    }else{
                                        updateUrls.add(new URL(pkg.kbartSource.url.replace(' ', '%20')))
                                    }

                                } else {
                                    updateUrls.add(new URL(pkg.kbartSource.url.replace(' ', '%20')))
                                }
                            } else {
                                // this package had already been filled with data
                                if ((UrlToolkit.containsDateStamp(pkg.kbartSource.url) || UrlToolkit.containsDateStampPlaceholder(pkg.kbartSource.url)) && pkg.kbartSource.lastUpdateUrl) {
                                    updateUrls = getUpdateUrls(pkg.kbartSource.lastUpdateUrl, pkg.kbartSource.lastRun, pkg.dateCreated)
                                } else {
                                    updateUrls = getUpdateUrls(pkg.kbartSource.url, pkg.kbartSource.lastRun, pkg.dateCreated)
                                }
                            }
                            log.info("Got updateUrls: ${updateUrls}")

                            File file
                            if (updateUrls.size() > 0) {
                                updateUrls = updateUrls.reverse()
                                updateUrls.each {
                                    println(it)
                                }

                                for(URL url in updateUrls){
                                    lastUpdateURL = url.toString()
                                    try {
                                        file = exportService.kbartFromUrl(lastUpdateURL, updatePackageInfo)
                                        if(file.size() > 0){
                                            updatePackageInfo.status = RDStore.UPDATE_STATUS_SUCCESSFUL
                                            updatePackageInfo.save()
                                            log.info("Found File by URL: ${lastUpdateURL}")
                                            break
                                        }
                                    }
                                    catch (Exception e) {
                                        log.error("Exception by get kbartFromUrl: ${e.message}")
                                        //e.printStackTrace()
                                    }
                                }
                                if(updatePackageInfo.status == RDStore.UPDATE_STATUS_FAILED) {
                                    updateUrls = []
                                    if(pkg.kbartSource.lastUpdateUrl){
                                        updateUrls << new URL(pkg.kbartSource.lastUpdateUrl)
                                    }

                                    for(URL url in updateUrls){
                                        lastUpdateURL = url.toString()
                                        try {
                                            file = exportService.kbartFromUrl(lastUpdateURL, updatePackageInfo)
                                            if(file.size() > 0){
                                                updatePackageInfo.status = RDStore.UPDATE_STATUS_SUCCESSFUL
                                                updatePackageInfo.save()
                                                log.info("Found File by URL: ${lastUpdateURL}")
                                                break
                                            }
                                        }
                                        catch (Exception e) {
                                            log.error("Exception by get kbartFromUrl: ${e.message}")
                                            //e.printStackTrace()
                                        }
                                    }
                                }
                                updatePackageInfo.updateUrl = lastUpdateURL
                                updatePackageInfo.save()

                                if(updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED) {

                                    boolean isZipFile = file && lastUpdateURL.contains('.zip')
                                    if (isZipFile) {
                                        file = storeZipContentToFile(file)
                                    }

                                    if (file) {
                                        if(fileCheckService.hasFileChanged(pkg.kbartSource, file)){
                                            kbartRows = kbartProcessService.kbartProcess(file, lastUpdateURL, updatePackageInfo)

                                            if (kbartRows.size() > 0) {
                                                updatePackageInfo = kbartProcessService.kbartImportProcess(kbartRows, pkg, lastUpdateURL, updatePackageInfo, onlyRowsWithLastChanged)

                                                if(updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED) {
                                                    String newHash = fileCheckService.computeHash(file)

                                                    KbartSource src = KbartSource.get(pkg.kbartSource.id)
                                                    src.kbartFileHash = newHash
                                                    src.save()

                                                }

                                            } else {
                                                if(updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED) {
                                                    UpdatePackageInfo.withTransaction {
                                                        updatePackageInfo.description = "The KBART File is empty!"
                                                        updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                                        updatePackageInfo.endTime = new Date()
                                                        updatePackageInfo.updateUrl = lastUpdateURL
                                                        updatePackageInfo.save()
                                                    }
                                                }
                                            }
                                        }else {
                                            if(updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED) {
                                                UpdatePackageInfo.withTransaction {
                                                    updatePackageInfo.description = "No changes in File. Is same File from last update!"
                                                    updatePackageInfo.status = RDStore.UPDATE_STATUS_INFO
                                                    updatePackageInfo.endTime = new Date()
                                                    updatePackageInfo.save()
                                                }

                                                KbartSource src = KbartSource.get(pkg.kbartSource.id)
                                                src.lastRun = new Date()
                                                src.save()
                                            }
                                        }



                                    } else {
                                        if(updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED) {
                                            UpdatePackageInfo.withTransaction {
                                                updatePackageInfo.description = isZipFile ? "No txt-File found in Zip-File!" : "No KBART File found by URL: ${lastUpdateURL}!"
                                                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                                updatePackageInfo.endTime = new Date()
                                                updatePackageInfo.updateUrl = lastUpdateURL
                                                updatePackageInfo.save()
                                            }
                                        }
                                    }
                                }
                            }else {
                                UpdatePackageInfo.withTransaction {
                                    updatePackageInfo.description = "No KBART File found by URL"
                                    updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                    updatePackageInfo.endTime = new Date()
                                    updatePackageInfo.updateUrl = lastUpdateURL
                                    updatePackageInfo.save()
                                }
                            }

                        } else {
                            UpdatePackageInfo.withTransaction {
                                //UpdatePackageInfo updatePackageFail = new UpdatePackageInfo()
                                updatePackageInfo.description = "No url define in the source of the package."
                                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                updatePackageInfo.startTime = startTime
                                updatePackageInfo.endTime = new Date()
                                updatePackageInfo.pkg = pkg
                                updatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                                updatePackageInfo.automaticUpdate = true
                                updatePackageInfo.save()
                            }
                        }
                    }else {
                        UpdatePackageInfo.withTransaction {
                            //UpdatePackageInfo updatePackageFail = new UpdatePackageInfo()
                            updatePackageInfo.description = "Default Supply Method not set in source! Please set Default Supply Method!"
                            updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                            updatePackageInfo.startTime = startTime
                            updatePackageInfo.endTime = new Date()
                            updatePackageInfo.pkg = pkg
                            updatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                            updatePackageInfo.automaticUpdate = true
                            updatePackageInfo.save()
                        }
                    }
                }

            } catch (Exception exception) {
                log.error("Error by startAutoPackageUapdate: ${exception.message}")
                exception.printStackTrace()
                UpdatePackageInfo.withTransaction {
                    //UpdatePackageInfo updatePackageFail = new UpdatePackageInfo()
                    updatePackageInfo.description = "An error occurred while processing the KBART file. More information can be seen in the system log. File from URL: ${lastUpdateURL}"
                    updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                    updatePackageInfo.startTime = startTime
                    updatePackageInfo.endTime = new Date()
                    updatePackageInfo.pkg = pkg
                    updatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                    updatePackageInfo.automaticUpdate = true
                    updatePackageInfo.updateUrl = lastUpdateURL
                    updatePackageInfo.save()
                }
            }
        }
        log.info("End startAutoPackageUpdate Package ($pkg.name)")
    }

    File storeZipContentToFile(File zipFIle) {
        File folder = new File("/tmp/wekb/kbartExport")
        File file
        def zf = new java.util.zip.ZipFile(zipFIle)
        zf.entries().findAll { !it.directory }.each {
            log.debug("storeZipContentToFile: fileName -> "+it.name)
            if(it.name.contains('.txt')){
                String fileName = folder.absolutePath.concat(File.separator)
                String safeFileName = it.name.replaceAll("[\\\\/:*?\"<>|]", "_")
                file = new File(fileName.concat(safeFileName))
                byte[] content = exportService.getByteContent(zf.getInputStream(it))
                FileUtils.copyInputStreamToFile(new ByteArrayInputStream(content), file)
            }
        }
        return file
    }


}

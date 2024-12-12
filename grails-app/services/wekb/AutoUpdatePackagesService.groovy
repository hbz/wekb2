package wekb

import org.apache.commons.io.FileUtils
import wekb.tools.UrlToolkit
import wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import org.apache.commons.lang.StringUtils
import wekb.system.JobResult

import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.ExecutorService

import groovyx.gpars.GParsPool
import java.util.concurrent.Future

@Transactional
class AutoUpdatePackagesService {

    static final THREAD_POOL_SIZE = 2
    public static boolean running = false;
    Map result = [result: JobResult.STATUS_SUCCESS]
    ExportService exportService
    ExecutorService executorService
    Future activeFuture
    FtpConnectService ftpConnectService

    KbartProcessService kbartProcessService

    void findPackageToUpdateAndUpdate(boolean onlyRowsWithLastChanged = false) {
        List packageNeedsUpdate = []
        def updPacks = Package.executeQuery(
                "from Package p " +
                        "where p.kbartSource is not null and " +
                        "p.kbartSource.automaticUpdates = true " +
                        "and (p.kbartSource.lastRun is null or p.kbartSource.lastRun < current_date) order by p.kbartSource.lastRun")
        updPacks.each { Package p ->
            if (p.kbartSource.needsUpdate()) {
                packageNeedsUpdate << p
            }
        }
        log.info("findPackageToUpdateAndUpdate: Package with KbartSource and lastRun < currentDate (${packageNeedsUpdate.size()})")
        if (packageNeedsUpdate.size() > 0) {
            /*  packageNeedsUpdate.eachWithIndex { Package aPackage, int idx ->
                  while(!(activeFuture) || activeFuture.isDone() || idx == 0) {
                      activeFuture = executorService.submit({
                          Package pkg = Package.get(aPackage.id)
                          Thread.currentThread().setName('startAutoPackageUpdate' + aPackage.id)
                          startAutoPackageUpdate(pkg, onlyRowsWithLastChanged)
                      })
                      println("Wait")
                  }
                  println("Test:"+aPackage.name)
              }*/
            GParsPool.withPool(THREAD_POOL_SIZE) { pool ->
                packageNeedsUpdate.anyParallel { aPackage ->
                    try {
                        startAutoPackageUpdate(aPackage, onlyRowsWithLastChanged)
                    }catch (Exception exception) {
                        log.error("Error by findPackageToUpdateAndUpdate (${aPackage.id} -> ${aPackage.name}): ${exception.message}")
                        exception.printStackTrace()
                    }
                }
            }
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
        String lastUpdateURL = ""
        Date startTime = new Date()
        if (pkg.status in [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED]) {
            UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: pkg, startTime: startTime, endTime: new Date(), status: RDStore.UPDATE_STATUS_SUCCESSFUL, description: "Package status is ${pkg.status.value}. Update for this package is not starting.", onlyRowsWithLastChanged: onlyRowsWithLastChanged, automaticUpdate: true, kbartHasWekbFields: false)
            updatePackageInfo.save()
        }else if (!pkg.nominalPlatform) {
            UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: pkg, startTime: startTime, endTime: new Date(), status: RDStore.UPDATE_STATUS_SUCCESSFUL, description: "No nominal platform is set for this package! Please put a nominal platform on the package level.", onlyRowsWithLastChanged: onlyRowsWithLastChanged, automaticUpdate: true, kbartHasWekbFields: false)
            updatePackageInfo.save()
        } else {
            UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: pkg, startTime: startTime, status: RDStore.UPDATE_STATUS_SUCCESSFUL, description: "Starting Update package.", onlyRowsWithLastChanged: onlyRowsWithLastChanged, automaticUpdate: true).save()
            try {
                if (pkg.kbartSource) {
                    if (pkg.kbartSource.defaultSupplyMethod == RDStore.KS_DSMETHOD_FTP) {
                        updatePackageInfo.updateFromFTP = true
                        updatePackageInfo.save()
                        if (pkg.kbartSource.ftpServerUrl) {
                            File file = ftpConnectService.ftpConnectAndGetFile(pkg.kbartSource)


                            if (file) {
                                kbartRows = kbartProcessService.kbartProcess(file, "", updatePackageInfo)
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

                            if (kbartRows.size() > 0) {
                                updatePackageInfo = kbartProcessService.kbartImportProcess(kbartRows, pkg, "", updatePackageInfo, onlyRowsWithLastChanged)
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
                        updatePackageInfo.save()
                        if (pkg.kbartSource.url) {
                            List<URL> updateUrls
                            if (pkg.getTippCount() <= 0 || pkg.kbartSource.lastRun == null) {
                                updateUrls = new ArrayList<>()
                                updateUrls.add(new URL(pkg.kbartSource.url.replace(' ', '%20')))
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
                                        e.printStackTrace()
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
                                        kbartRows = kbartProcessService.kbartProcess(file, lastUpdateURL, updatePackageInfo)

                                        if (kbartRows.size() > 0) {
                                            updatePackageInfo = kbartProcessService.kbartImportProcess(kbartRows, pkg, lastUpdateURL, updatePackageInfo, onlyRowsWithLastChanged)
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
                String fileName = folder.absolutePath.concat(File.separator).concat(it.name+'.txt')
                file = new File(fileName)
                byte[] content = exportService.getByteContent(zf.getInputStream(it))
                FileUtils.copyInputStreamToFile(new ByteArrayInputStream(content), file)
            }
        }
        return file
    }


}

package wekb

import grails.core.GrailsApplication
import grails.gorm.transactions.NotTransactional
import grails.plugins.mail.MailService
import org.apache.commons.io.FileUtils
import wekb.kbart.KbartProcessService
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

import java.util.regex.Matcher

@Transactional
class AutoUpdatePackagesService {
    
    FtpConnectService ftpConnectService
    FileCheckService fileCheckService
    KbartProcessService kbartProcessService
    GrailsApplication grailsApplication
    MailService mailService
    ExportService exportService

    static final THREAD_POOL_SIZE = 4
    private static boolean started = false

    @NotTransactional
    void findPackageToUpdateOnAutoUpdate() {
        long startedAt = System.currentTimeMillis()
        log.info('findPackageToUpdateOnAutoUpdate: start')

        List<Long> ids = Package.withNewTransaction {
            Package.executeQuery('''select p.id from Package p where p.kbartSource is not null
                and p.kbartSource.automaticUpdates = true
                and (p.kbartSource.lastRun is null or p.kbartSource.lastRun < current_date)
                order by p.kbartSource.lastRun''') as List<Long>
        }
       
        GParsPool.withPool(THREAD_POOL_SIZE) {
            ids.eachParallel { Long pkgID ->
                Thread.currentThread().name = "AutoUpdate-${pkgID}"
                try {
                    Map packageRun = Package.withNewTransaction {
                        Package p = Package.get(pkgID)
                        if (!p.kbartSource)
                            return [run: false, message: 'No Source set. Update is not starting.', status: RDStore.UPDATE_STATUS_FAILED]
                        if (p.kbartSource.status in [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED]) {
                            return [run: false, message: 'Source status is deleted or removed. Update is not starting.', status: RDStore.UPDATE_STATUS_FAILED]
                        }
                        if (p.status in [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED]) {
                            return [run: false, message: 'Package status is deleted or removed. Update is not starting.', status: RDStore.UPDATE_STATUS_INFO]
                        }
                        if (!p.kbartSource.frequency) {
                            return [run: false, message: 'Source frequency is not set. Update is not starting.', status: RDStore.UPDATE_STATUS_FAILED]
                        }
                        [run: p.kbartSource.needsUpdate()]
                    }
                    if (packageRun.run) {
                        startAutoPackageUpdate(pkgID)
                    } else if (packageRun.message) {
                        createAutoUpdateInfo(pkgID, packageRun.status, packageRun.message, true)
                    }
                } catch (Exception e) {
                    log.error("Automatic KBART update failed for package ${pkgID}", e)
                    try {
                        if (!grailsApplication.config.getProperty('grails.mail.disabled', Boolean)) {
                            String prefix = ServerUtils.getCurrentServer() == ServerUtils.SERVER_PROD ? '' : "${ServerUtils.getCurrentServerSystemId()} - "
                            mailService.sendMail {
                                to 'wekb@hbz-nrw.de', 'moetez.djebeniani@hbz-nrw.de'
                                from 'wekb Server <wekb-autoUpdatePackagesJob@wekbServer>'
                                subject "${prefix}we:kb Auto Update Packages Job"
                                text "Error updating package ${pkgID}: ${e.message}"
                            }
                        }
                    } catch (Exception mailError) {
                        log.error("Unable to send automatic update failure email for package ${pkgID}", mailError)
                    }
                }
            }
        }
        log.info("findPackageToUpdateOnAutoUpdate: finished after ${System.currentTimeMillis() - startedAt} ms")
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

    /**
     * Background entry point: use IDs across thread/transaction boundaries.
     */
    @NotTransactional
    void startAutoPackageUpdate(Package pkg) {
        if (!pkg?.id) throw new IllegalArgumentException('Package must be persisted')
        startAutoPackageUpdate(pkg.id as Long)
    }

    @NotTransactional
    void startAutoPackageUpdate(Long pkgID) {
        log.info("Begin startAutoPackageUpdate Package ID=${pkgID}")
        Date startTime = new Date()
        Long infoID = null
        String lastUpdateURL = ''

        try {
            Map state = Package.withNewTransaction {
                Package p = Package.get(pkgID)
                KbartSource source = p.kbartSource
                if (source.status in [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED]) {
                    return [error: "Source status is ${source.status?.value}. Update is not starting.", status: RDStore.UPDATE_STATUS_INFO]
                }
                if (!p.nominalPlatform) return [error: 'No nominal platform is set for this package.', status: RDStore.UPDATE_STATUS_FAILED]
                [method: source.defaultSupplyMethod,
                 ftpServerUrl: source.ftpServerUrl,
                 url: source.url,
                 lastUpdateUrl: source.lastUpdateUrl,
                 lastRun: source.lastRun,
                 sourceID: source.id,
                 sourceDate: p.dateCreated,
                 tippCount: p.getTippCount()]
            }

            if (state.error) {
                createAutoUpdateInfo(pkgID, state.status, state.error, true)
                return
            }

            infoID = createAutoUpdateInfo(pkgID, RDStore.UPDATE_STATUS_SUCCESSFUL, 'Starting package update.', false, startTime)
            lastUpdateURL = state.lastUpdateUrl ?: state.url ?: ''

            File file = null
            boolean fromFTP = state.method == RDStore.KS_DSMETHOD_FTP
            boolean fromURL = state.method == RDStore.KS_DSMETHOD_HTTP_URL

            if (fromFTP) {
                updateAutoInfo(infoID, [updateFromFTP: true, lastUpdateUrl: null, updateUrl: null])
                if (!state.ftpServerUrl) {
                    finishAutoInfo(infoID, RDStore.UPDATE_STATUS_FAILED, 'No FTP server URL defined in the package source.', lastUpdateURL)
                    return
                }
                KbartSource source = KbartSource.withNewTransaction { KbartSource.get(state.sourceID) }
                file = ftpConnectService.ftpConnectAndGetFile(source)
                if (!file) {
                    finishAutoInfo(infoID, RDStore.UPDATE_STATUS_FAILED, 'No KBART file found on FTP server.', lastUpdateURL)
                    return
                }
            } else if (fromURL) {
                updateAutoInfo(infoID, [updateFromURL: true, lastUpdateUrl: lastUpdateURL])
                if (!state.url) {
                    finishAutoInfo(infoID, RDStore.UPDATE_STATUS_FAILED, 'No URL defined in the package source.', lastUpdateURL)
                    return
                }

                List<URL> updateUrls
                if (state.tippCount == 0 || !state.lastRun) {
                    Date dateFromUrl = getDateFromUrl(state.url)
                    updateUrls = dateFromUrl ? getUpdateUrls(state.url, dateFromUrl, state.sourceDate) : [new URL(state.url.replace(' ', '%20'))]
                } else {
                    String baseUrl = ((UrlToolkit.containsDateStamp(state.url) || UrlToolkit.containsDateStampPlaceholder(state.url)) && state.lastUpdateUrl) ? state.lastUpdateUrl : state.url
                    updateUrls = getUpdateUrls(baseUrl, state.lastRun, state.sourceDate)
                }

                List<URL> candidates = (updateUrls ?: []).reverse()
                if (state.lastUpdateUrl && !candidates.any { it.toString() == state.lastUpdateUrl }) {
                    candidates << new URL(state.lastUpdateUrl.replace(' ', '%20'))
                }
                for (URL candidate : candidates) {
                    lastUpdateURL = candidate.toString()
                    try {
                        UpdatePackageInfo info = UpdatePackageInfo.withNewTransaction { UpdatePackageInfo.get(infoID) }
                        file = exportService.kbartFromUrl(lastUpdateURL, info)
                        if (file?.length() > 0) break
                    } catch (Exception e) {
                        log.warn("Cannot download KBART file from ${lastUpdateURL}: ${e.message}")
                    }
                }
                updateAutoInfo(infoID, [lastUpdateUrl: lastUpdateURL])
                if (!file || file.length() == 0) {
                    finishAutoInfo(infoID, RDStore.UPDATE_STATUS_FAILED, 'No KBART file found by URL.', lastUpdateURL)
                    return
                }
                if (lastUpdateURL.toLowerCase().contains('.zip')) {
                    file = storeZipContentToFile(file)
                    if (!file) {
                        finishAutoInfo(infoID, RDStore.UPDATE_STATUS_FAILED, 'No text file found in ZIP archive.', lastUpdateURL)
                        return
                    }
                }
            } else {
                finishAutoInfo(infoID, RDStore.UPDATE_STATUS_FAILED, 'Default supply method is not configured.', lastUpdateURL)
                return
            }

            KbartSource source = KbartSource.withNewTransaction { KbartSource.get(state.sourceID) }
            if (!fileCheckService.hasFileChanged(source, file)) {
                finishAutoInfo(infoID, RDStore.UPDATE_STATUS_INFO, 'No changes in file; same file as last update.', lastUpdateURL)
                // Preserve the existing no-change scheduling behavior.
                KbartSource.withNewTransaction {
                    KbartSource processedSource = KbartSource.get(state.sourceID)
                    if (processedSource) {
                        processedSource.lastRun = new Date()
                        processedSource.save(failOnError: true)
                    }
                }
                return
            }

            UpdatePackageInfo info = UpdatePackageInfo.withNewTransaction { UpdatePackageInfo.get(infoID) }
            List rows = kbartProcessService.kbartProcess(file, lastUpdateURL, info)
            if (!rows) {
                UpdatePackageInfo current = UpdatePackageInfo.withNewTransaction { UpdatePackageInfo.get(infoID) }
                if (current?.status != RDStore.UPDATE_STATUS_FAILED) {
                    finishAutoInfo(infoID, RDStore.UPDATE_STATUS_FAILED, 'The KBART file contains no rows.', lastUpdateURL)
                }
                return
            }

            Package pkg = Package.withNewTransaction { Package.get(pkgID) }
            UpdatePackageInfo result = kbartProcessService.kbartImportProcess(rows, pkg, lastUpdateURL, info)
            if (!result || result.status == RDStore.UPDATE_STATUS_FAILED) {
                log.warn("KBART import failed for package ${pkgID}; source hash will not be updated")
                return
            }

            String newHash = fileCheckService.computeHash(file)
            KbartSource.withNewTransaction {
                KbartSource processedSource = KbartSource.get(state.sourceID)
                if (!processedSource) throw new IllegalStateException("KBART source not found: ${state.sourceID}")
                processedSource.kbartFileHash = newHash
                processedSource.save(failOnError: true)
            }
        } catch (Exception e) {
            log.error("Error by startAutoPackageUpdate for package ${pkgID}", e)
            if (infoID) {
                try {
                    finishAutoInfo(infoID, RDStore.UPDATE_STATUS_FAILED,
                            "KBART update failed: ${e.message}", lastUpdateURL)
                } catch (Exception loggingError) {
                    log.error("Cannot save failure status for UpdatePackageInfo ${infoID}", loggingError)
                }
            }
            throw e
        } finally {
            try {
                Package.withNewTransaction {
                    Package processedPkg = Package.get(pkgID)
                    if (processedPkg) kbartProcessService.cleanUpUpdateInfosTables(processedPkg)
                }
            } catch (Exception cleanupError) {
                log.error("Cannot clean up update logs for package ${pkgID}", cleanupError)
            }
            log.info("End startAutoPackageUpdate Package ID=${pkgID}")
        }
    }

    private Long createAutoUpdateInfo(Long pkgID, def status, String description, boolean completed, Date startTime = new Date()) {
        UpdatePackageInfo.withNewTransaction {
            Package p = Package.get(pkgID)
            if (!p) throw new IllegalStateException("Package not found: ${pkgID}")
            KbartSource source = p.kbartSource
            UpdatePackageInfo info = new UpdatePackageInfo(
                    pkg: p,
                    startTime: startTime,
                    endTime: completed ? new Date() : null,
                    status: status,
                    description: description,
                    onlyRowsWithLastChanged: false,
                    automaticUpdate: true,
                    kbartHasWekbFields: false,
                    lastRun: source?.lastRun,
                    frequency: source?.frequency)
            fillAutoUpdateCounts(info, p)
            info.save(flush: true, failOnError: true)
            info.id as Long
        }
    }

    private void fillAutoUpdateCounts(UpdatePackageInfo info, Package pkg) {
        info.countPreviouslyTippsInWekb = pkg.getTippCountWithoutRemoved()
        info.countNowTippsInWekb = pkg.getTippCountWithoutRemoved()
        info.countCurrentTipps = pkg.getCurrentTippCount()
        info.countDeletedTipps = pkg.getDeletedTippCount()
    }

    private void updateAutoInfo(Long infoID, Map changes) {
        UpdatePackageInfo.withNewTransaction {
            UpdatePackageInfo info = UpdatePackageInfo.get(infoID)
            if (!info) throw new IllegalStateException("UpdatePackageInfo not found: ${infoID}")
            changes.each { String key, Object value -> info."${key}" = value }
            info.save(failOnError: true)
        }
    }

    private void finishAutoInfo(Long infoID, def status, String description, String url) {
        UpdatePackageInfo.withNewTransaction {
            UpdatePackageInfo info = UpdatePackageInfo.get(infoID)
            if (!info) throw new IllegalStateException("UpdatePackageInfo not found: ${infoID}")
            Package pkg = Package.get(info.pkg.id)
            info.status = status
            info.description = description
            info.endTime = new Date()
            info.updateUrl = url
            fillAutoUpdateCounts(info, pkg)
            info.save(failOnError: true)
        }
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

    void lookupForSuccessfullUpdate(Package pkg) {

        if (!pkg.kbartSource?.url) {
            return
        }

        List<URL> updateUrls = getUrlsForUpdate(pkg)

        log.info("Got updateUrls: ${updateUrls}")

        String successfulUrl = findSuccessfulUpdateUrl(updateUrls)

        // Fallback: letzte erfolgreiche URL erneut probieren
        if (!successfulUrl && pkg.kbartSource.lastUpdateUrl) {
            successfulUrl = findSuccessfulUpdateUrl([new URL(pkg.kbartSource.lastUpdateUrl)])
        }

        if (!successfulUrl) {
            log.warn("No working update URL found for package ${pkg.id}")
            return
        }

        Long pkgID = pkg.id as Long
        KbartSource.withNewTransaction {
            Package processedPkg = Package.get(pkgID)
            if (!processedPkg?.kbartSource) {
                throw new IllegalStateException("KBART source not found for package ${pkgID}")
            }
            processedPkg.kbartSource.lastUpdateUrl = successfulUrl
            processedPkg.kbartSource.kbartFileHash = null
            processedPkg.kbartSource.save(failOnError: true)
        }

        startAutoPackageUpdate(pkgID)
    }


    List<URL> getUrlsForUpdate(Package pkg) {

        String url = pkg.kbartSource.url

        // Paket wurde noch nie erfolgreich aktualisiert
        if (pkg.getTippCount() == 0 || !pkg.kbartSource.lastRun) {

            Date dateFromUrl = getDateFromUrl(url)

            if (dateFromUrl) {
                return getUpdateUrls(url, dateFromUrl, pkg.dateCreated)
            }

            return [new URL(url.replace(' ', '%20'))]
        }

        // Paket enthält bereits Daten
        String updateUrl = url

        if ((UrlToolkit.containsDateStamp(url) || UrlToolkit.containsDateStampPlaceholder(url)) && pkg.kbartSource.lastUpdateUrl) {
            updateUrl = pkg.kbartSource.lastUpdateUrl
        }

        Date dateFromUrl = getDateFromUrl(updateUrl)

        return getUpdateUrls(updateUrl, dateFromUrl, pkg.dateCreated)
    }


    String findSuccessfulUpdateUrl(List<URL> urls) {

        if (!urls) {
            return null
        }

        // neueste URL zuerst
        for (URL url : urls.reverse()) {

            try {
                File file = exportService.kbartFromUrl(url.toString())

                if (file?.size() > 0) {
                    log.info("Found file by URL: ${url}")
                    return url.toString()
                }

            } catch (Exception e) {
                log.error(
                        "Exception while loading KBART URL ${url}: ${e.message}"
                )
            }
        }

        return null
    }


    Date getDateFromUrl(String url) {

        if (!url || !UrlToolkit.containsDateStamp(url)) {
            return null
        }

        Matcher matcher = (url =~ /(\d{4}[-_]\d{2}[-_]\d{2})/)

        if (!matcher.find()) {
            return null
        }

        try {
            String dateString = matcher.group(1).replace('_', '-')

            LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)

            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        } catch (Exception e) {
            log.warn("Could not parse date from URL '${url}': ${e.message}")
            return null
        }
    }


}

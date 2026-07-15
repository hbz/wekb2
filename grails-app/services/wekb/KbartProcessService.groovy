package wekb

/*import org.apache.tika.parser.txt.CharsetDetector
import org.apache.tika.parser.txt.CharsetMatch*/
import wekb.tools.DateToolkit
import wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import grails.util.Holders
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.StatelessSession
import org.hibernate.Transaction
import org.mozilla.universalchardet.UniversalDetector

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.ZoneId
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord

@Transactional
class KbartProcessService {

    KbartImportValidationService kbartImportValidationService
    KbartImportService kbartImportService
    CleanupService cleanupService
    SessionFactory sessionFactory
    LaserService laserService


    Set<String> supportedHeaders = [
            "publication_title",
            "print_identifier",
            "online_identifier",
            "date_first_issue_online",
            "num_first_vol_online",
            "num_first_issue_online",
            "date_last_issue_online",
            "num_last_vol_online",
            "num_last_issue_online",
            "title_url",
            "first_author",
            "title_id",
            "embargo_info",
            "coverage_depth",
            "notes",
            "publisher_name",
            "publication_type",
            "date_monograph_published_print",
            "date_monograph_published_online",
            "monograph_volume",
            "monograph_edition",
            "first_editor",
            "parent_publication_title_id",
            "preceding_publication_title_id",
            "superseding_publication_title_id",
            "access_type",

            // WEKB
            "oa_type",
            "ddc",
            "medium",
            "doi_identifier",
            "subject_area",
            "language",
            "package_name",
            "package_id",
            "access_start_date",
            "access_end_date",
            "last_changed",
            "status",
            "listprice_eur",
            "listprice_usd",
            "listprice_gbp",
            "monograph_parent_collection_title",
            "zdb_id",
            "ezb_id",
            "package_ezb_anchor",
            "oa_gold",
            "oa_hybrid",
            "oa_apc_eur",
            "oa_apc_usd",
            "oa_apc_gbp",
            "package_isil",
            "package_isci",
            "ill_indicator",
            "title_gokb_uuid",
            "package_gokb_uuid",
            "title_wekb_uuid",
            "package_wekb_uuid"
    ] as Set<String>

    Set<String> allowedEncodings = [
            "UTF-8",
            "UTF8",
            "WINDOWS-1252",
            "CP1252",
            "US-ASCII",
            "ASCII"
    ] as Set<String>

    void kbartImportManual(Package pkg, File tsvFile, Boolean onlyRowsWithLastChanged){
        log.info("Beginn kbartImportManual ${pkg.name}")
        List kbartRows = []
        String lastUpdateURL = ""
        Date startTime = new Date()
        UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(
                pkg: pkg,
                startTime: startTime,
                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                description: "Starting Update package.",
                onlyRowsWithLastChanged: onlyRowsWithLastChanged,
                automaticUpdate: false,
                kbartHasWekbFields: false,
                updateFromFileUpload: true).save(flush: true)

        updatePackageInfo.countPreviouslyTippsInWekb = updatePackageInfo.pkg.getTippCountWithoutRemoved()
        updatePackageInfo.countNowTippsInWekb = updatePackageInfo.pkg.getTippCountWithoutRemoved()
        updatePackageInfo.countCurrentTipps = updatePackageInfo.pkg.getCurrentTippCount()
        updatePackageInfo.countDeletedTipps = updatePackageInfo.pkg.getDeletedTippCount()

        try {
            kbartRows = kbartProcess(tsvFile, lastUpdateURL, updatePackageInfo)

            if (kbartRows.size() > 0) {
                String fPathSource = '/tmp/wekb/kbartImportTmp'
                String fPathTarget = Holders.grailsApplication.config.getProperty('wekb.kbartImportStorageLocation', String) ?: '/tmp/wekb/kbartImport'

                File folder = new File("${fPathTarget}")
                if (!folder.exists()) {
                    folder.mkdirs()
                }

                String packageName = "${pkg.id}"

                Path source = new File("${fPathSource}/${packageName}").toPath()
                Path target = new File("${fPathTarget}/${packageName}").toPath()
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)

                updatePackageInfo = kbartImportProcess(kbartRows, pkg, lastUpdateURL, updatePackageInfo, onlyRowsWithLastChanged)
            }

            cleanUpUpdateInfosTables(pkg)

        } catch (Exception exception) {
            log.error("Error by kbartImportManual: ${exception.message}")

            exception.printStackTrace()

            UpdatePackageInfo.withTransaction {
                //UpdatePackageInfo updatePackageFail = new UpdatePackageInfo()
                updatePackageInfo.description = "An error occurred while processing the KBART file. More information can be seen in the system log."
                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                updatePackageInfo.startTime = startTime
                updatePackageInfo.endTime = new Date()
                updatePackageInfo.pkg = pkg
                updatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                updatePackageInfo.automaticUpdate = false
                updatePackageInfo.updateFromFileUpload = true
                updatePackageInfo.countPreviouslyTippsInWekb = updatePackageInfo.pkg.getTippCountWithoutRemoved()
                updatePackageInfo.countNowTippsInWekb = updatePackageInfo.pkg.getTippCountWithoutRemoved()
                updatePackageInfo.countCurrentTipps = updatePackageInfo.pkg.getCurrentTippCount()
                updatePackageInfo.countDeletedTipps = updatePackageInfo.pkg.getDeletedTippCount()
                updatePackageInfo.save()
            }
        }
        log.info("End kbartImportManual ${pkg.name}")
    }

    UpdatePackageInfo kbartImportProcess(List kbartRows, Package pkg, String lastUpdateURL, UpdatePackageInfo updatePackageInfo, Boolean onlyRowsWithLastChanged) {
        log.info("Begin kbartImportProcess Package ($pkg.name)")

        boolean processFailed = false
        String processFailedText = "An error occurred while processing the KBART file. More information can be seen in the system log. "

        RefdataValue status_current = RDStore.KBC_STATUS_CURRENT
        RefdataValue status_deleted = RDStore.KBC_STATUS_DELETED
        RefdataValue status_retired = RDStore.KBC_STATUS_RETIRED
        RefdataValue status_expected = RDStore.KBC_STATUS_EXPECTED

        List listStatus = [status_current, status_expected, status_deleted, status_retired]

        Map firstRow = kbartRows[0]

        //kbartRows.remove(0)

        //Needed if kbart not wekb standard
        boolean setAllTippsNotInKbartToDeleted = true
        boolean kbartHasWekbFields = false


       if(kbartRows.size() > 0){
            if (firstRow.containsKey("status")) {
                log.info("kbart has status field and is wekb standard")
                setAllTippsNotInKbartToDeleted = false
                kbartHasWekbFields = true
                //listStatus = [status_current, status_expected, status_deleted, status_retired]
            }
        }

        int previouslyTipps = TitleInstancePackagePlatform.executeQuery(
                "select count(*) from TitleInstancePackagePlatform tipp where " +
                        "tipp.status in :status and " +
                        "tipp.pkg = :package",
                [package: pkg, status: listStatus])[0]

        LinkedHashMap tippsWithCoverage = [:]
        HashSet<Long> tippDuplicates = new HashSet<Long>()
        HashSet<Long> setTippsNotToDeleted = []
        Map errors = [global: [], tipps: []]

        HashSet<Long> tippsFound = new HashSet<Long>()
        HashSet<Long> newtippIdList = new HashSet<Long>()
        List invalidKbartRowsForTipps = []
        int removedTipps = 0
        int newTipps = 0
        int changedTipps = 0
        int countInvalidKbartRowsForTipps = 0
        int countExistingTippsAfterImport = 0
        int idx = 0
        int countDeletedTippsByProcess = 0

        int kbartRowsCount = kbartRows.size()

        List kbartRowsToCreateTipps = []

        Date lastChangedInKbart = pkg.kbartSource ? pkg.kbartSource.lastChangedInKbart : null
        List<LocalDate> lastChangedDates = []

        Platform plt = pkg.nominalPlatform

        boolean checkAllTitles = true

        try {

            log.info("Matched package has ${previouslyTipps} TIPPs")

            if(onlyRowsWithLastChanged){
                if(firstRow.containsKey("last_changed") && (pkg.kbartSource)) {
                    LocalDate currentLastChangedInKbart = convertToLocalDateViaInstant(lastChangedInKbart)
                    LocalDate lastUpdated = convertToLocalDateViaInstant(pkg.kbartSource.lastRun)
                    if(lastUpdated && currentLastChangedInKbart && currentLastChangedInKbart.isBefore(lastUpdated)){
                        lastUpdated = currentLastChangedInKbart
                    }

                    log.info("onlyRowsWithLastChanged is set! before process only last changed rows: ${kbartRowsCount}")
                    List newKbartRows = []
                    kbartRows.eachWithIndex { Object entry, int i ->
                        if (entry.containsKey("last_changed") && entry.last_changed != null && entry.last_changed != "") {
                            LocalDate lastChanged = DateToolkit.getAsLocalDate(entry.last_changed)
                            if (lastChanged == null || lastUpdated == null || !lastChanged.isBefore(lastUpdated)) {
                                newKbartRows << entry
                            }

                            if(lastChanged){
                                lastChangedDates << lastChanged
                            }
                        } else {
                            newKbartRows << entry
                        }
                    }
                    kbartRows = newKbartRows
                    checkAllTitles = false
                    log.info("onlyRowsWithLastChanged is set! after process only last changed rows: ${newKbartRows.size()}")
                }else {
                }
            }

            if(lastChangedDates.size() > 0) {
                LocalDate maxDate = lastChangedDates.max()
                lastChangedInKbart = Date.from(maxDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
            }

            UpdatePackageInfo.withTransaction {
                updatePackageInfo.kbartHasWekbFields = kbartHasWekbFields

                if(lastChangedInKbart){
                    updatePackageInfo.lastChangedInKbart = lastChangedInKbart
                }
                updatePackageInfo.updateUrl = lastUpdateURL
                updatePackageInfo.save()
            }

            int max = 500
            String kbartImportProcessInfo = onlyRowsWithLastChanged ? "${kbartRows.size()} (Total: $kbartRowsCount)" : "$kbartRowsCount"
            TitleInstancePackagePlatform.withSession { Session sess ->
                for (int offset = 0; offset < kbartRows.size(); offset += max) {

                    List kbartRowsToProcess = kbartRows.drop(offset).take(max)
                    for (def kbartRow : kbartRowsToProcess) {
                        idx++
                        try {
                            def currentTippError = [index: idx]
                            log.info("kbartImportProcess (#$idx of $kbartImportProcessInfo): title ${kbartRow.publication_title}")
                            if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {

                                kbartRow.pkg = pkg
                                kbartRow.nominalPlatform = plt
                                try {

                                    Map tippErrorMap = [:]
                                    def validation_result = kbartImportValidationService.tippValidateForKbart(kbartRow)
                                    if (!validation_result.valid) {
                                        if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {
                                            invalidKbartRowsForTipps << kbartRow.rowIndex

                                            UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                                    description: validation_result.errorMessage+" (Row: "+kbartRow.rowIndex+")",
                                                    tipp: null,
                                                    startTime: new Date(),
                                                    endTime: new Date(),
                                                    status: RDStore.UPDATE_STATUS_FAILED,
                                                    type: RDStore.UPDATE_TYPE_INVAILD_TITLE,
                                                    oldValue: '',
                                                    newValue: '',
                                                    tippProperty: '',
                                                    updatePackageInfo: updatePackageInfo
                                            ).save()
                                        }
                                        log.error("TIPP Validation failed on ${kbartRow.publication_title}")
                                        /* def tipp_error = [
                                             message: validation_result.errorMessage,
                                             baddata: kbartRow
                                     ]
                                     tippErrorMap = tipp_error*/
                                    } else {
                                        /*if (validation_result.errors?.size() > 0) {
                                                                tippErrorMap.putAll(validation_result.errors)
                                                            }*/
                                        //println('tippDuplicates'+tippDuplicates.size())
                                        Map findTipp = kbartImportService.findTheCorrectTipp(kbartRow, tippDuplicates)
                                        TitleInstancePackagePlatform updateTipp = findTipp.tipp ?: null
                                        try {

                                            if(updateTipp == null){
                                                    def trimmed_url = kbartRow.title_url ? kbartRow.title_url.trim() : null
                                                    log.debug("push in map to create new TIPP..")
                                                    def tmap = [
                                                            'pkg'         : pkg,
                                                            'hostPlatform': pkg.nominalPlatform,
                                                            'url'         : trimmed_url,
                                                            'status'      : (kbartRow.status ?: 'Current'),
                                                            'name'        : (kbartRow.publication_title ?: null),
                                                            'type'        : (kbartRow.publication_type ?: null),
                                                            'medium'    : (kbartRow.medium ?: null),
                                                            'kbartRowMap': kbartRow
                                                    ]
                                                    kbartRowsToCreateTipps << tmap


                                            }else if(updateTipp && !(updateTipp.id in tippsFound)) {
                                                tippDuplicates = findTipp.tippDuplicates

                                                Map autoUpdateResultTipp = kbartImportService.tippImportForUpdate(updateTipp, kbartRow, tippsWithCoverage, updatePackageInfo)

                                                tippsWithCoverage = autoUpdateResultTipp.tippsWithCoverage


                                                if (autoUpdateResultTipp.updatePackageInfo) {
                                                    updatePackageInfo = autoUpdateResultTipp.updatePackageInfo
                                                }


                                                    updateTipp = autoUpdateResultTipp.tippObject

                                                    if (autoUpdateResultTipp.removedTipp) {
                                                        removedTipps++
                                                    }

                                                    if (autoUpdateResultTipp.changedTipp) {
                                                        changedTipps++
                                                    }

                                                    if (!kbartHasWekbFields && updateTipp && updateTipp.status != RDStore.KBC_STATUS_CURRENT) {
                                                        updateTipp.status = RDStore.KBC_STATUS_CURRENT
                                                        setTippsNotToDeleted << updateTipp.id
                                                    }
                                                    if (updateTipp && (autoUpdateResultTipp.removedTipp || autoUpdateResultTipp.changedTipp)) {
                                                        updateTipp.lastUpdated = new Date()
                                                    }

                                                    if (updateTipp) {
                                                        updateTipp = updateTipp.save()
                                                        tippsFound.add(updateTipp.id)
                                                    }
                                            }

                                        }
                                        catch (grails.validation.ValidationException ve) {
                                            if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {
                                                if (updateTipp) {
                                                    invalidKbartRowsForTipps << kbartRow.rowIndex
                                                    UpdateTippInfo.withNewTransaction {
                                                        updatePackageInfo.refresh()
                                                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                                                description: "An error occurred while processing the title: ${kbartRow.publication_title}. Check KBART row of this title.",
                                                                tipp: updateTipp,
                                                                startTime: new Date(),
                                                                endTime: new Date(),
                                                                status: RDStore.UPDATE_STATUS_FAILED,
                                                                type: RDStore.UPDATE_TYPE_FAILED_TITLE,
                                                                oldValue: '',
                                                                newValue: '',
                                                                tippProperty: '',
                                                                updatePackageInfo: updatePackageInfo
                                                        ).save()
                                                    }
                                                    updateTipp.discard()
                                                }
                                            }
                                            log.error("ValidationException attempting to cross reference the title: ${kbartRow.publication_title} with TIPP ${updateTipp?.id}: " + ve.message)
                                            processFailed = true
                                            //ve.printStackTrace()
                                            /*tippErrorMap.putAll(messageService.processValidationErrors(ve.errors))*/
                                        }
                                        catch (Exception ge) {
                                            if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {
                                                if (updateTipp) {
                                                    invalidKbartRowsForTipps << kbartRow.rowIndex
                                                    UpdateTippInfo.withNewTransaction {
                                                        updatePackageInfo.refresh()
                                                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                                                description: "An error occurred while processing the title: ${kbartRow.publication_title}. Check KBART row of this title.",
                                                                tipp: updateTipp,
                                                                startTime: new Date(),
                                                                endTime: new Date(),
                                                                status: RDStore.UPDATE_STATUS_FAILED,
                                                                type: RDStore.UPDATE_TYPE_FAILED_TITLE,
                                                                oldValue: '',
                                                                newValue: '',
                                                                tippProperty: '',
                                                                updatePackageInfo: updatePackageInfo
                                                        ).save()
                                                    }
                                                    updateTipp.discard()
                                                }
                                            }
                                            log.error("Exception attempting to cross reference TIPP: "+ge.message)
                                            processFailed = true
                                            ge.printStackTrace()
                                            /*                                      def tipp_error = [
                                                                                      message: messageService.resolveCode('crossRef.package.tipps.error', [kbartRow.publication_title], Locale.ENGLISH),
                                                                                      baddata: kbartRow,
                                                                                      errors : [message: ge.toString()]
                                                                              ]
                                                                              tippErrorMap = tipp_error*/
                                        }
                                    }

                                    /*if (tippErrorMap.size() > 0) {
                                    currentTippError.put('tipp', tippErrorMap)
                                }*/
                                }
                                catch (Exception ge) {
                                    log.error("Exception attempting to cross reference the title: ${kbartRow.publication_title}: "+ ge.message)
                                    processFailed = true
                                    //ge.printStackTrace()
                                }
                            }

                            /*if (currentTippError.size() > 1) {
                            errors.tipps.add(currentTippError)
                        }*/

                            if (idx % 100 == 0) {
                                log.info("Clean up");
                                cleanupService.cleanUpGorm()
                            }
                        } catch (Exception ge) {
                            log.error("Error: kbartImportProcess (#$idx of $kbartImportProcessInfo): title ${kbartRow.publication_title}: " + ge.message)
                            //ge.printStackTrace()
                            processFailed = true
                        }

                    }
                    sess.flush()
                    sess.clear()
                }
            }

            if(kbartRowsToCreateTipps.size() > 0){
                newtippIdList = kbartImportService.createTippBatch(kbartRowsToCreateTipps, updatePackageInfo)
                log.info("kbartRowsToCreateTipps: newTippList size -> "+newtippIdList.size())
                newTipps = newtippIdList.size()

            }

            if(checkAllTitles && tippDuplicates.size() > 0){
                log.info("remove tippDuplicates -> ${tippDuplicates.size()}")

                int maxDuplicates = 20000
                int idxDuplicates = 0
                int tippDuplicatesCount = tippDuplicates.size()

                for (int offset = 0; offset < tippDuplicatesCount; offset += maxDuplicates) {
                    HashSet<Long> tippDuplicatesTippsFromWekbToProcess = tippDuplicates.drop(offset).take(maxDuplicates)

                    tippDuplicatesTippsFromWekbToProcess.each { tippID ->
                        if(!(tippID in tippsFound)){
                            idxDuplicates++
                            log.info("removeTippsFromWekb cause tippDuplicates (#$idxDuplicates of $tippDuplicatesCount): tippID ${tippID}")

                            TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform set status = :removed, lastUpdated = :currentDate where id = (:tippId) and status != :removed", [removed: RDStore.KBC_STATUS_REMOVED, tippId: tippID, currentDate: new Date()])

                            StatelessSession session = sessionFactory.openStatelessSession()
                            Transaction tx = session.beginTransaction()

                            TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(tippID)

                            /*String title_ID = tipp.getTitleID()
                            TitleInstancePackagePlatform correctTipp
                            if (title_ID) {
                                List tippDublic = kbartImportService.tippsMatchingByTitleIDAutoUpdate(title_ID, pkg)
                                tippDublic = tippDublic.sort { it.lastUpdated }
                                tippDublic.each {
                                    println(it.lastUpdated)
                                }
                                tippDublic.reverse(true)
                                if (tippDublic.size() > 1 && tippDublic[0].id in tippsFound) {
                                    correctTipp = tippDublic[0]
                                    println(correctTipp.lastUpdated)

                                    laserCleanUpService.cleanUpLaserTipp(tipp.uuid, correctTipp.uuid)
                                }
                            }*/

                            UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                    description: "Remove Title '${tipp.name}' because is a duplicate in wekb!",
                                    tipp: tipp,
                                    startTime: new Date(),
                                    endTime: new Date(),
                                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                    type: RDStore.UPDATE_TYPE_REMOVED_TITLE,
                                    oldValue: tipp.status.value,
                                    newValue: 'Removed',
                                    tippProperty: 'status',
                                    kbartProperty: 'status',
                                    updatePackageInfo: updatePackageInfo,
                                    lastUpdated: new Date(),
                                    dateCreated: new Date(),
                                    uuid: UUID.randomUUID().toString()
                            )
                            session.insert(updateTippInfo)

                            tx.commit()
                            session.close()

                            removedTipps++
                        }
                    }
                }

            }

/*            if (invalidKbartRowsForTipps.size() > 0) {
                String msg = messageService.resolveCode('crossRef.package.tipps.ignored', [invalidKbartRowsForTipps.size()], Locale.ENGLISH)
                log.warn(msg)
                errors.global.add([message: msg, baddata: pkg.name])
            }*/

            countInvalidKbartRowsForTipps = invalidKbartRowsForTipps.size()

            if (kbartRows.size() > 0 && countInvalidKbartRowsForTipps > 0 && countInvalidKbartRowsForTipps > kbartRows.size()) {
                log.info("imported Package $pkg.name contains no valid TIPPs")
                processFailed = true
                processFailedText = "The Kbart contains no valid Titles. Please check the titles on rows: ${invalidKbartRowsForTipps.join(', ')} ."
            }


            /*if (checkAllTitles && setAllTippsNotInKbartToDeleted) {

                List<Long> tippsIds = setTippsNotToDeleted ? TitleInstancePackagePlatform.executeQuery("select tipp.id from TitleInstancePackagePlatform tipp where " +
                        "tipp.status in (:status) and " +
                        "tipp.pkg = :package and tipp.id not in (:setTippsNotToDeleted)",
                        [package: pkg, status: [status_current, status_expected, status_retired], setTippsNotToDeleted: setTippsNotToDeleted]) : []

                int deletedCount = tippsIds.size()
                int idxDeleted = 0
                if(deletedCount > 0) {
                    int maxDeleted = 30000
                    for (int offset = 0; offset < deletedCount; offset += maxDeleted) {
                        List deleteTippsFromWekbToProcess = tippsIds.drop(offset).take(maxDeleted)
                        TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform set status = :deleted, lastUpdated = :currentDate where id in (:tippIDs) and status != :deleted", [deleted: RDStore.KBC_STATUS_DELETED, tippIDs: deleteTippsFromWekbToProcess, currentDate: new Date()])
                    }

                    StatelessSession session = sessionFactory.openStatelessSession()
                    Transaction tx = session.beginTransaction()
                    tippsIds.each { tippID ->
                        idxDeleted++
                        log.info("setAllTippsNotInKbartToDeleted (#$idxDeleted of $deletedCount): tippID ${tippID}")
                        TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(tippID)
                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                description: "Delete Title '${tipp.name}' because is not in KBART!",
                                tipp: tipp,
                                startTime: new Date(),
                                endTime: new Date(),
                                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                                oldValue: tipp.status.value,
                                newValue: 'Deleted',
                                tippProperty: 'status',
                                kbartProperty: 'status',
                                updatePackageInfo: updatePackageInfo,
                                lastUpdated: new Date(),
                                dateCreated: new Date(),
                                uuid: UUID.randomUUID().toString()
                        )

                        session.insert(updateTippInfo)
                    }
                    tx.commit()
                    session.close()
                }

                log.info("kbart is not wekb standard. set title to deleted. Found tipps: ${tippsIds.size()}, Set tipps to deleted: ${idxDeleted}")
            }*/

            log.info("tippsWithCoverage: ${tippsWithCoverage.size()}")

            tippsWithCoverage.each {
                TitleInstancePackagePlatform titleInstancePackagePlatform = TitleInstancePackagePlatform.get(it.key)

                if (titleInstancePackagePlatform) {
                    kbartImportService.createOrUpdateCoverageForTipp(titleInstancePackagePlatform, it.value)
                }

            }

            countExistingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                    "select count(*) from TitleInstancePackagePlatform tipp where " +
                            "tipp.status in (:status) and " +
                            "tipp.pkg = :package",
                    [package: pkg, status: listStatus])[0]


            //TODO: countExistingTippsAfterImport > (kbartRowsCount-countInvalidKbartRowsForTipps) ??? nötig noch
            log.info("before deleteTipps from wekb -------------------------------------------------------------------------------------")

            if(checkAllTitles && tippsFound.size() > 0 && kbartRowsCount > 0 && countExistingTippsAfterImport > 0){

                List<Long> existingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                        "select tipp.id from TitleInstancePackagePlatform tipp where " +
                                "tipp.status in (:status) and " +
                                "tipp.pkg = :package",
                        [package: pkg, status: listStatus])


                HashSet<Long> existingTippsAfterImportHashSet = new HashSet<Long>(existingTippsAfterImport)

                if(newtippIdList){
                    tippsFound = tippsFound+newtippIdList
                }

                HashSet<Long> deleteTippsFromWekb = existingTippsAfterImportHashSet - tippsFound


                log.info("deleteTippsFromWekb: ${deleteTippsFromWekb.size()}")
                if(deleteTippsFromWekb.size() > 0){

                    int maxDeleted = 30000
                    int idxDeleted = 0
                    int deletedCount = deleteTippsFromWekb.size()

                    Date currentDate = new Date()

                    for (int offset = 0; offset < deletedCount; offset += maxDeleted) {
                        def deleteTippsFromWekbToProcess = deleteTippsFromWekb.drop(offset).take(maxDeleted)
                        TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform set status = :deleted, lastUpdated = :currentDate where id in (:tippIDs) and status != :deleted", [deleted: RDStore.KBC_STATUS_DELETED, tippIDs: deleteTippsFromWekbToProcess, currentDate: currentDate])
                    }

                    StatelessSession session = sessionFactory.openStatelessSession()
                    Transaction tx = session.beginTransaction()
                    deleteTippsFromWekb.each {tippID ->
                        TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(tippID)
                        if(currentDate == tipp.lastUpdated) {
                            idxDeleted++
                            log.info("deleteTippsFromWekb (#$idxDeleted of $deletedCount): tippID ${tippID}")
                            UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                    description: "Delete Title '${tipp.name}' because is not in KBART!",
                                    tipp: tipp,
                                    startTime: currentDate,
                                    endTime: currentDate,
                                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                    type: RDStore.UPDATE_TYPE_DELETED_TITLE,
                                    oldValue: tipp.status.value,
                                    newValue: 'Deleted',
                                    tippProperty: 'status',
                                    kbartProperty: 'status',
                                    updatePackageInfo: updatePackageInfo,
                                    lastUpdated: currentDate,
                                    dateCreated: currentDate,
                                    uuid: UUID.randomUUID().toString()
                            )
                            countDeletedTippsByProcess++
                            session.insert(updateTippInfo)
                        }
                    }
                    tx.commit()
                    session.close()

                    log.info("Rows in KBART is not same with titles in wekb. DeleteTippsFromWekb: ${deleteTippsFromWekb.size()}, Set tipps to deleted: ${idxDeleted}")
                }

            }
            
            if(!processFailed) {
                String description = "Package Update: (KbartLines: ${kbartRowsCount}, " +
                        "Processed Titles in this run: ${idx}, All Titles previously: ${previouslyTipps}, All Titles now: ${countExistingTippsAfterImport}, Removed Titles: ${removedTipps}, New All Titles: ${newTipps}, Changed All Titles: ${changedTipps}), Deleted All Titles: ${countDeletedTippsByProcess})"

              /*  UpdatePackageInfo.executeUpdate("update UpdatePackageInfo set countKbartRows = ${kbartRowsCount}, " +
                        "countChangedTipps = ${changedTipps}, " +
                        "countNowTippsInWekb = ${countExistingTippsAfterImport}, " +
                        "countPreviouslyTippsInWekb = ${previouslyTipps}, " +
                        "countNewTipps = ${newTipps}, " +
                        "countRemovedTipps = ${removedTipps}, " +
                        "countInValidTipps = ${countInvalidKbartRowsForTipps}, " +
                        "countProcessedKbartRows = ${idx}, " +
                        "endTime = :currentDate, " +
                        "description = ${description}, " +
                        "lastUpdated = :currentDate " +
                        "where id = ${updatePackageInfo.id}", [currentDate: new Date()])*/

                UpdatePackageInfo.withTransaction {

                    Package aPackage = Package.get(updatePackageInfo.pkg.id)

                    aPackage.lastUpdated = new Date()
                    aPackage.save()
                    updatePackageInfo.refresh()
                    updatePackageInfo.endTime = new Date()
                    updatePackageInfo.description = description
                    updatePackageInfo.countChangedTipps = changedTipps > 0 ? changedTipps : 0
                    updatePackageInfo.countDeletedTippsByProcess = countDeletedTippsByProcess > 0 ? countDeletedTippsByProcess : 0
                    updatePackageInfo.countInValidTipps = countInvalidKbartRowsForTipps > 0 ? countInvalidKbartRowsForTipps : 0
                    updatePackageInfo.countKbartRows = kbartRowsCount > 0 ? kbartRowsCount : 0
                    updatePackageInfo.countNewTipps = newTipps > 0 ? newTipps : 0
                    updatePackageInfo.countNowTippsInWekb = countExistingTippsAfterImport > 0 ? countExistingTippsAfterImport : 0
                    updatePackageInfo.countPreviouslyTippsInWekb = previouslyTipps > 0 ? previouslyTipps : 0
                    updatePackageInfo.countRemovedTipps = removedTipps > 0 ? removedTipps : 0
                    updatePackageInfo.countProcessedKbartRows = idx > 0 ? idx : 0
                    updatePackageInfo.countCurrentTipps = updatePackageInfo.pkg.getCurrentTippCount()
                    updatePackageInfo.countDeletedTipps = updatePackageInfo.pkg.getDeletedTippCount()
                    updatePackageInfo.save()


                    if (aPackage.kbartSource && updatePackageInfo.automaticUpdate) {
                        KbartSource src = KbartSource.get(aPackage.kbartSource.id)
                        src.kbartHasWekbFields = kbartHasWekbFields
                        src.lastRun = new Date()
                        src.lastUpdateUrl = (src.defaultSupplyMethod == RDStore.KS_DSMETHOD_HTTP_URL ? lastUpdateURL : "")
                        src.lastChangedInKbart = lastChangedInKbart
                        src.save()
                    }
                }
            }
            /* log.debug("final flush");
             cleanupService.cleanUpGorm()*/

        } catch (Exception e) {
            log.error("Error by kbartImportProcess: "+ e.message)
            processFailed = true
            e.printStackTrace()

        }
        if(processFailed){
            UpdatePackageInfo.withTransaction {

                Package aPackage = Package.get(updatePackageInfo.pkg.id)
                //UpdatePackageInfo newUpdatePackageInfo = new UpdatePackageInfo(pkg: aPackage, startTime: updatePackageInfo.startTime, automaticUpdate: updatePackageInfo.automaticUpdate, kbartHasWekbFields:  updatePackageInfo.kbartHasWekbFields, lastRun:  updatePackageInfo.lastRun)
                updatePackageInfo.endTime = new Date()
                String description2 = processFailedText
                if(lastUpdateURL && updatePackageInfo.automaticUpdate){
                    description2 = description2+ "File from URL: ${lastUpdateURL}"
                }
                updatePackageInfo.description = description2
                updatePackageInfo.countChangedTipps = changedTipps > 0 ? changedTipps : 0
                updatePackageInfo.countDeletedTippsByProcess = countDeletedTippsByProcess > 0 ? countDeletedTippsByProcess : 0
                updatePackageInfo.countInValidTipps = countInvalidKbartRowsForTipps > 0 ? countInvalidKbartRowsForTipps : 0
                updatePackageInfo.countKbartRows = kbartRowsCount > 0 ? kbartRowsCount : 0
                updatePackageInfo.countNewTipps = newTipps > 0 ? newTipps : 0
                updatePackageInfo.countNowTippsInWekb = countExistingTippsAfterImport > 0 ? countExistingTippsAfterImport : 0
                updatePackageInfo.countPreviouslyTippsInWekb = previouslyTipps > 0 ? previouslyTipps : 0
                updatePackageInfo.countRemovedTipps = removedTipps > 0 ? removedTipps : 0
                updatePackageInfo.countProcessedKbartRows = idx > 0 ? idx : 0
                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                updatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                updatePackageInfo.updateUrl = lastUpdateURL
                updatePackageInfo.updateFromURL = lastUpdateURL ? true : false
                updatePackageInfo.updateFromFTP = lastUpdateURL ? false : true
                updatePackageInfo.updateFromFileUpload = false
                updatePackageInfo.frequency = aPackage.kbartSource.frequency
                updatePackageInfo.lastUpdateUrl = aPackage.kbartSource.lastUpdateUrl
                updatePackageInfo.lastChangedInKbart = lastChangedInKbart
                updatePackageInfo.kbartHasWekbFields = kbartHasWekbFields
                updatePackageInfo.countCurrentTipps = updatePackageInfo.pkg.getCurrentTippCount()
                updatePackageInfo.countDeletedTipps = updatePackageInfo.pkg.getDeletedTippCount()
                updatePackageInfo.save()
            }
        }

        /*if(errors.global.size() > 0 || errors.tipps.size() > 0){
            log.error("Error map by kbartImportProcess: ")
        }*/
        log.info("End kbartImportProcess Package ($pkg.name)")
        return updatePackageInfo
    }

    List kbartProcess(File tsvFile, String lastUpdateURL, UpdatePackageInfo updatePackageInfo) {
        log.info("Begin KbartProcess, transmitted: ${tsvFile?.length() ?: 0}")

        int countRows = 0
        List<Map<String, Object>> result = []

        Closure markImportAsFailed = { String description ->
            UpdatePackageInfo.withTransaction {
                //updatePackageInfo.refresh()

                if (lastUpdateURL && updatePackageInfo.automaticUpdate) {
                    description += "File from URL: ${lastUpdateURL}"
                }

                updatePackageInfo.description = description
                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                updatePackageInfo.endTime = new Date()
                updatePackageInfo.updateUrl = lastUpdateURL
                updatePackageInfo.countPreviouslyTippsInWekb = updatePackageInfo.pkg.getTippCountWithoutRemoved()
                updatePackageInfo.countNowTippsInWekb = updatePackageInfo.pkg.getTippCountWithoutRemoved()
                updatePackageInfo.countCurrentTipps = updatePackageInfo.pkg.getCurrentTippCount()
                updatePackageInfo.countDeletedTipps = updatePackageInfo.pkg.getDeletedTippCount()

                updatePackageInfo.save()
            }
        }

        if (tsvFile == null || !tsvFile.exists() || !tsvFile.isFile()) {
            log.warn("KBART file does not exist or is not a file: ${tsvFile}")

            markImportAsFailed("KBART file does not exist or is not a valid file. ")

            log.info("End KbartProcess with ${countRows} rows")
            return result
        }

        if (tsvFile.length() == 0) {
            log.warn("KBART file is empty: ${lastUpdateURL}")

            markImportAsFailed("KBART file is empty. ")

            log.info("End KbartProcess with ${countRows} rows")
            return result
        }

        Set<String> minimumKbartStandard = [
                "publication_title",
                "title_url",
                "title_id"
        ] as Set<String>

        String encoding = null

        try {
            boolean asciiFile

            tsvFile.withInputStream { InputStream inputStream ->
                String content = inputStream.getText(StandardCharsets.US_ASCII.name())
                asciiFile = StandardCharsets.US_ASCII.newEncoder().canEncode(content)
            }

            if (asciiFile) {
                encoding = StandardCharsets.US_ASCII.name()
            } else {
                tsvFile.withInputStream { InputStream inputStream ->
                    encoding = UniversalDetector.detectCharset(inputStream)
                }
            }

            String normalizedEncoding = encoding?.trim()?.toUpperCase(Locale.ROOT)

            if (!normalizedEncoding || !allowedEncodings.contains(normalizedEncoding)) {
                log.warn(
                        "Encoding of file is wrong. File encoding is: ${encoding}"
                )

                markImportAsFailed("Encoding of KBART file is wrong. File encoding was: ${encoding}. AllowedEncodings: ${allowedEncodings}"
                )
                log.info("End KbartProcess with ${countRows} rows")
                return result
            }

            if (normalizedEncoding in ["WINDOWS-1252", "CP1252"]) {
                encoding = "windows-1252"
            } else if (normalizedEncoding in ["UTF-8", "UTF8"]) {
                encoding = "UTF-8"
            } else {
                encoding = "US-ASCII"
            }

        } catch (Exception encodingException) {
            log.error(
                    "Could not determine KBART encoding: " +
                            "${encodingException.message}",
                    encodingException
            )

            markImportAsFailed("The encoding of the KBART file could not be determined. AllowedEncodings: ${allowedEncodings}")

            log.info("End KbartProcess with ${countRows} rows")
            return result
        }

        Reader reader = null
        CSVParser parser = null

        try {

            String firstLine

            tsvFile.withReader(encoding) { BufferedReader bufferedReader ->
                String currentLine

                while ((currentLine = bufferedReader.readLine()) != null) {
                    if (!currentLine.trim().isEmpty()) {
                        firstLine = currentLine
                        break
                    }
                }
            }

            if (!firstLine) {
                log.warn("KBART file has no header: ${lastUpdateURL}")

                markImportAsFailed("KBART file is empty or does not contain a header. ")

                return result
            }

            String delimiter = getDelimiter(firstLine)

            if (!delimiter) {
                log.warn("No delimiter recognized: ${lastUpdateURL}")

                markImportAsFailed("Separator for the KBART file was not recognized. " +
                                "The following separators are recognized: " +
                                "tab, comma and semicolon. ")

                return result
            }

            char delimiterChar = resolveKbartDelimiter(delimiter)

            CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter(delimiterChar)
                    .setQuote(null)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(false)
                    .build()

            reader = tsvFile.newInputStream().newReader(encoding)
            parser = format.parse(reader)

            Map<String, Integer> columnIndexes = [:]

            parser.headerMap.each {
                String originalHeader,
                Integer columnIndex ->

                    String normalizedHeader = normalizeKbartHeader(originalHeader)

                    if (!normalizedHeader) {
                        log.debug(
                                "Ignoring empty KBART header at column ${columnIndex}"
                        )
                        return
                    }

                    if (columnIndexes.containsKey(normalizedHeader)) {
                        log.warn(
                                "Duplicate KBART header '${normalizedHeader}' " +
                                        "at column ${columnIndex}; " +
                                        "the first occurrence will be used"
                        )
                        return
                    }

                    columnIndexes[normalizedHeader] = columnIndex
            }

            Set<String> missingMinimumHeaders = [] as Set<String>

            minimumKbartStandard.each { String requiredHeader ->
                String normalizedRequiredHeader =
                        normalizeKbartHeader(requiredHeader)

                if (!columnIndexes.containsKey(normalizedRequiredHeader)) {
                    missingMinimumHeaders << normalizedRequiredHeader
                }
            }

            if (!missingMinimumHeaders.isEmpty()) {
                log.warn("KBART file does not have required headers: " + "${missingMinimumHeaders}")

                markImportAsFailed("KBART file does not have the required headers: " +
                                "${missingMinimumHeaders.join(', ')}. ")

                return result
            }

            columnIndexes.keySet().each { String normalizedHeader ->
                if (!supportedHeaders.contains(normalizedHeader)) {
                    log.info("Unhandled parameter type ${normalizedHeader}, ignoring ...")
                }
            }

            int expectedColumnCount = parser.headerMap.size()
            int importedRows = 0
            int skippedRows = 0

            parser.each { CSVRecord record ->
                countRows++

                if (record.size() != expectedColumnCount) {
                    log.warn("KBART row ${record.recordNumber} has an invalid column count: expected ${expectedColumnCount}, found ${record.size()}")
                }

                Map<String, Object> rowMap = [:]

                columnIndexes.each {
                    String normalizedHeader,
                    Integer columnIndex ->

                        if (!supportedHeaders.contains(normalizedHeader)) {
                            return
                        }

                        if (columnIndex == null ||
                                columnIndex < 0 ||
                                columnIndex >= record.size()) {

                            log.debug("Column '${normalizedHeader}' at index ${columnIndex} is missing in KBART row ${record.recordNumber}")

                            return
                        }

                        String value = cleanKbartValue(record.get(columnIndex))

                        if (value != null) {
                            rowMap[normalizedHeader] = value
                        }
                }

                if (rowMap.publication_title) {
                    rowMap.rowIndex = record.recordNumber

                    result << rowMap
                    importedRows++
                } else {
                    skippedRows++

                    log.warn(
                            "Ignoring KBART row ${record.recordNumber}: " +
                                    "publication_title is empty or missing"
                    )
                }
            }

            if (countRows == 0) {
                log.warn("KBART file contains no data rows: ${lastUpdateURL}")

                markImportAsFailed("KBART file is empty. ")

                return result
            }

            log.info(
                    "KBART processing completed: " +
                            "${countRows} rows processed, " +
                            "${importedRows} rows imported, " +
                            "${skippedRows} rows skipped"
            )

        } catch (Exception exception) {
            log.error(
                    "Error by KbartProcess: ${exception.message}",
                    exception
            )

            markImportAsFailed("An error occurred while processing the KBART file. More information can be seen in the system log. ")

        } finally {
            if (parser != null) {
                try {
                    parser.close()
                } catch (Exception closeException) {
                    log.warn(
                            "Could not close KBART CSV parser: " +
                                    "${closeException.message}"
                    )
                }
            } else if (reader != null) {
                try {
                    reader.close()
                } catch (Exception closeException) {
                    log.warn(
                            "Could not close KBART reader: " +
                                    "${closeException.message}"
                    )
                }
            }
        }

        log.info("End KbartProcess with ${countRows} rows")

        return result
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert ? dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null
    }

    private String getDelimiter(String line) {
        log.debug("Getting delimiter for line: ${line}")
        int maxCount = 0
        String delimiter
        if (line) {

            if (line.startsWith("\uFEFF")) {
                line = line.substring(1)
            }

            for (String prop : ['comma', 'semicolon', 'tab']) {
                int num = line.count(resolver.get(prop).toString())
                if (maxCount < num) {
                    maxCount = num
                    delimiter = prop
                }
            }

        }
        log.debug("delimiter is: ${delimiter}")

        if(delimiter){
            delimiter = resolver.get(delimiter)
        }

        return delimiter
    }

    static def resolver = [
            'comma'      : ',',
            'semicolon'  : ';',
            'tab'        : '\t',
    ]

    def cleanUpUpdateInfosTables(Package pkg){
        log.info("cleanUpUpdateInfosTables for ${pkg.name} (${pkg.id})")

        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -24)
        int updateInfosCount = UpdatePackageInfo.executeQuery("select count(*) from UpdatePackageInfo upi where upi.pkg = :pkg", [pkg: pkg])[0]

        if(updateInfosCount > 25) {
            updateInfosCount = UpdatePackageInfo.executeQuery("select count(*) from UpdatePackageInfo upi where upi.pkg = :pkg and upi.dateCreated < :cal", [pkg: pkg, cal: cal.getTime()])[0]
            List<UpdatePackageInfo> updatePackageInfoList = UpdatePackageInfo.executeQuery("select upi from UpdatePackageInfo upi where upi.pkg = :pkg and upi.dateCreated < :cal", [pkg: pkg, cal: cal.getTime()])

            log.info("Count of UpdatePackageInfos for 24 Months: ${updateInfosCount}")

            int updateTippInfosCount = UpdateTippInfo.executeQuery("select count(*) from UpdateTippInfo uti where uti.updatePackageInfo in :upi", [upi: updatePackageInfoList])[0]
            List<UpdateTippInfo> updateTippInfoList = UpdateTippInfo.executeQuery("select uti from UpdateTippInfo uti where uti.updatePackageInfo in :upi", [upi: updatePackageInfoList])

            log.info("Count of UpdateTippInfosCount for 24 Months: ${updateTippInfosCount}")

            updateTippInfoList.each {
                it.delete()
            }

            updatePackageInfoList.each {
                it.delete()
            }

        }else {
            log.info("No UpdatePackageInfos to clean. Leave the last ${updateInfosCount} UpdatePackageInfos")
        }

    }

    private static char resolveKbartDelimiter(String delimiter) {
        if (!delimiter) {
            throw new IllegalArgumentException(
                    "Delimiter must not be null or empty"
            )
        }

        if (delimiter == "\\t" || delimiter == "\t") {
            return '\t' as char
        }

        return delimiter.charAt(0)
    }

    private static String normalizeKbartHeader(String header) {
        if (header == null) {
            return null
        }

        String normalized = header
                .replaceFirst(/^\uFEFF/, "")
                .replace("\u0000", "")
                .trim()
                .toLowerCase(Locale.ROOT)

        if (normalized == "superceding_publication_title_id") {
            return "superseding_publication_title_id"
        }

        return normalized ?: null
    }

    private static String cleanKbartValue(String value) {
        if (value == null) {
            return null
        }

        String cleaned = value
                .replace("\u0000", "")
                .replace("\r", "")
                .trim()

        return cleaned ?: null
    }

}

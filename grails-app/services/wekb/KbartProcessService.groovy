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

@Transactional
class KbartProcessService {

    KbartImportValidationService kbartImportValidationService
    KbartImportService kbartImportService
    CleanupService cleanupService
    SessionFactory sessionFactory

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

        } catch (Exception exception) {
            log.error("Error by kbartImportManual: ${exception.message}")
            //exception.printStackTrace()
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
                updatePackageInfo.save()
            }
        }
        log.info("End kbartImportManual ${pkg.name}")
    }

    UpdatePackageInfo kbartImportProcess(List kbartRows, Package pkg, String lastUpdateURL, UpdatePackageInfo updatePackageInfo, Boolean onlyRowsWithLastChanged) {
        log.info("Begin kbartImportProcess Package ($pkg.name)")
        boolean addOnly = false //Thing about it where to set or to change

        RefdataValue status_current = RDStore.KBC_STATUS_CURRENT
        RefdataValue status_deleted = RDStore.KBC_STATUS_DELETED
        RefdataValue status_retired = RDStore.KBC_STATUS_RETIRED
        RefdataValue status_expected = RDStore.KBC_STATUS_EXPECTED

        List listStatus = [status_current]

        Map headerOfKbart = kbartRows[0]

        //println("Header = ${headerOfKbart}")

        kbartRows.remove(0)

        //Needed if kbart not wekb standard
        boolean setAllTippsNotInKbartToDeleted = true


        if(kbartRows.size() > 0){
            if (headerOfKbart.containsKey("status")) {
                log.info("kbart has status field and is wekb standard")
                setAllTippsNotInKbartToDeleted = false
                listStatus = [status_current, status_expected, status_deleted, status_retired]
            }
        }

        if(addOnly){
            setAllTippsNotInKbartToDeleted = false
        }

        List<Long> existing_tipp_ids = TitleInstancePackagePlatform.executeQuery(
                "select tipp.id from TitleInstancePackagePlatform tipp where " +
                        "tipp.status in :status and " +
                        "tipp.pkg = :package",
                [package: pkg, status: listStatus])

        int previouslyTipps = existing_tipp_ids.size()

        LinkedHashMap tippsWithCoverage = [:]
        HashSet<Long> tippDuplicates = new HashSet<Long>()
        List setTippsNotToDeleted = []
        Map errors = [global: [], tipps: []]

        HashSet<Long> tippsFound = new HashSet<Long>()
        List invalidKbartRowsForTipps = []
        int removedTipps = 0
        int newTipps = 0
        int changedTipps = 0

        int kbartRowsCount = kbartRows.size()

        List kbartRowsToCreateTipps = []

        Date lastChangedInKbart = pkg.kbartSource ? pkg.kbartSource.lastChangedInKbart : null
        List<LocalDate> lastChangedDates = []

        Platform plt = pkg.nominalPlatform

        boolean checkAllTitles = true

        try {

            log.info("Matched package has ${previouslyTipps} TIPPs")

            int idx = 0

            if(onlyRowsWithLastChanged){
                if(headerOfKbart.containsKey("last_changed") && (pkg.kbartSource)) {
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
                if(!setAllTippsNotInKbartToDeleted){
                    updatePackageInfo.kbartHasWekbFields = true
                }

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
                                                    description: validation_result.errorMessage,
                                                    tipp: null,
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
                                        log.debug("TIPP Validation failed on ${kbartRow.publication_title}")
                                        /* def tipp_error = [
                                             message: validation_result.errorMessage,
                                             baddata: kbartRow
                                     ]
                                     tippErrorMap = tipp_error*/
                                    } else {
                                        /*if (validation_result.errors?.size() > 0) {
                                                                tippErrorMap.putAll(validation_result.errors)
                                                            }*/
                                        TitleInstancePackagePlatform updateTipp = null
                                        try {
                                            Map autoUpdateResultTipp = kbartImportService.tippImportForUpdate(kbartRow, tippsWithCoverage, tippDuplicates, updatePackageInfo, kbartRowsToCreateTipps)

                                            kbartRowsToCreateTipps = autoUpdateResultTipp.kbartRowsToCreateTipps
                                            tippsWithCoverage = autoUpdateResultTipp.tippsWithCoverage
                                            tippDuplicates = autoUpdateResultTipp.tippDuplicates

                                            if (autoUpdateResultTipp.updatePackageInfo) {
                                                updatePackageInfo = autoUpdateResultTipp.updatePackageInfo
                                            }

                                            if (!autoUpdateResultTipp.newTipp) {
                                                updateTipp = autoUpdateResultTipp.tippObject

                                                if (autoUpdateResultTipp.removedTipp) {
                                                    removedTipps++
                                                }

                                                if (autoUpdateResultTipp.changedTipp) {
                                                    changedTipps++
                                                }

                                                if (setAllTippsNotInKbartToDeleted && updateTipp && updateTipp.status != RDStore.KBC_STATUS_CURRENT) {
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
                                                    UpdateTippInfo.withTransaction {
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
                                            //ve.printStackTrace()
                                            /*tippErrorMap.putAll(messageService.processValidationErrors(ve.errors))*/
                                        }
                                        catch (Exception ge) {
                                            if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {
                                                if (updateTipp) {
                                                    invalidKbartRowsForTipps << kbartRow.rowIndex
                                                    UpdateTippInfo.withTransaction {
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
                                            //ge.printStackTrace()
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
                        }

                    }
                    sess.flush()
                    sess.clear()
                }
            }

            if(kbartRowsToCreateTipps.size() > 0){
                List newTippList = kbartImportService.createTippBatch(kbartRowsToCreateTipps, updatePackageInfo)
                newTipps = newTippList.size()
                log.debug("kbartRowsToCreateTipps: newTippList size -> "+newTippList.size())

                /*  Package pkgTipp = pkg
                  Platform platformTipp = plt

                  newTippList.eachWithIndex{ Map newTippMap, int i ->
                      TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(newTippMap.tippID)
                      if(tipp){
                          long start = System.currentTimeMillis()
                          log.info("kbartRowsToCreateTipps: update tipp ${i+1} of ${newTipps}")
                          try {
                              LinkedHashMap result = [newTipp: true]
                              result = kbartImportService.updateTippWithKbart(result, tipp, newTippMap.kbartRowMap, newTippMap.updatePackageInfo, tippsWithCoverage, pkgTipp, platformTipp)
                              tippsWithCoverage = result.tippsWithCoverage
                              updatePackageInfo = result.updatePackageInfo

                          }catch (Exception e) {
                              log.error("kbartRowsToCreateTipps: -> ${newTippMap.kbartRowMap}:" + e.toString())
                          }

                          log.debug("kbartRowsToCreateTipps processed at: ${System.currentTimeMillis()-start} msecs")
                          if (i % 100 == 0) {
                              log.info("Clean up")
                              cleanupService.cleanUpGorm()
                          }
                      }
                  }*/

            }

            if(checkAllTitles && tippDuplicates.size() > 0){
                log.info("remove tippDuplicates -> ${tippDuplicates.size()}")

                int maxDuplicates = 20000
                int idxDuplicates = 0
                int tippDuplicatesCount = tippDuplicates.size()

                for (int offset = 0; offset < tippDuplicatesCount; offset += maxDuplicates) {
                    def tippDuplicatesTippsFromWekbToProcess = tippDuplicates.drop(offset).take(maxDuplicates)

                    tippDuplicatesTippsFromWekbToProcess.each { tippID ->
                        if(!(tippID in tippsFound)){
                            idxDuplicates++
                            log.info("removeTippsFromWekb cause tippDuplicates (#$idxDuplicates of $tippDuplicatesCount): tippID ${tippID}")

                            TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform set status = :removed, lastUpdated = :currentDate where id = (:tippId) and status != :removed", [removed: RDStore.KBC_STATUS_REMOVED, tippId: tippID, currentDate: new Date()])

                            StatelessSession session = sessionFactory.openStatelessSession()
                            Transaction tx = session.beginTransaction()

                            TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(tippID)
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

            int countInvalidKbartRowsForTipps = invalidKbartRowsForTipps.size()

            if (kbartRows.size() > 0 && kbartRows.size() > countInvalidKbartRowsForTipps) {
            } else {
                log.info("imported Package $pkg.name contains no valid TIPPs")
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

            int countExistingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                    "select count(*) from TitleInstancePackagePlatform tipp where " +
                            "tipp.status in (:status) and " +
                            "tipp.pkg = :package",
                    [package: pkg, status: listStatus])[0]


            //TODO: countExistingTippsAfterImport > (kbartRowsCount-countInvalidKbartRowsForTipps) ??? nötig noch
            log.info("before deleteTipps from wekb -------------------------------------------------------------------------------------")
            if(checkAllTitles && tippsFound.size() > 0 && kbartRowsCount > 0 && countExistingTippsAfterImport > (kbartRowsCount-countInvalidKbartRowsForTipps)){

                List<Long> existingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                        "select tipp.id from TitleInstancePackagePlatform tipp where " +
                                "tipp.status in (:status) and " +
                                "tipp.pkg = :package",
                        [package: pkg, status: listStatus])


                HashSet<Long> existingTippsAfterImportHashSet = new HashSet<Long>(existingTippsAfterImport)

                HashSet<Long> deleteTippsFromWekb = existingTippsAfterImportHashSet - tippsFound


                log.info("deleteTippsFromWekb: ${deleteTippsFromWekb.size()}")
                if(deleteTippsFromWekb.size() > 0){

                    int maxDeleted = 30000
                    int idxDeleted = 0
                    int deletedCount = deleteTippsFromWekb.size()

                    for (int offset = 0; offset < deletedCount; offset += maxDeleted) {
                        def deleteTippsFromWekbToProcess = deleteTippsFromWekb.drop(offset).take(maxDeleted)
                        TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform set status = :deleted, lastUpdated = :currentDate where id in (:tippIDs) and status != :deleted", [deleted: RDStore.KBC_STATUS_DELETED, tippIDs: deleteTippsFromWekbToProcess, currentDate: new Date()])
                    }

                    StatelessSession session = sessionFactory.openStatelessSession()
                    Transaction tx = session.beginTransaction()
                    deleteTippsFromWekb.each {tippID ->
                        idxDeleted++
                        log.info("deleteTippsFromWekb (#$idxDeleted of $deletedCount): tippID ${tippID}")
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
                        changedTipps++
                        session.insert(updateTippInfo)
                    }
                    tx.commit()
                    session.close()

                    log.info("Rows in KBART is not same with titles in wekb. DeleteTippsFromWekb: ${deleteTippsFromWekb.size()}, Set tipps to deleted: ${idxDeleted}")
                }

            }

            String description = "Package Update: (KbartLines: ${kbartRowsCount}, " +
                    "Processed Titles in this run: ${idx}, Titles in we:kb previously: ${previouslyTipps}, Titles in we:kb now: ${countExistingTippsAfterImport}, Removed Titles: ${removedTipps}, New Titles in we:kb: ${newTipps}, Changed Titles in we:kb: ${changedTipps})"

            UpdatePackageInfo.executeUpdate("update UpdatePackageInfo set countKbartRows = ${kbartRowsCount}, " +
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
                    "where id = ${updatePackageInfo.id}", [currentDate: new Date()])

            UpdatePackageInfo.withTransaction {

                Package aPackage = Package.get(updatePackageInfo.pkg.id)

                aPackage.lastUpdated = new Date()
                aPackage.lastUpdateComment = "Updated package with ${kbartRowsCount} Title. (Titles in we:kb previously: ${previouslyTipps}, Titles in we:kb now: ${countExistingTippsAfterImport}, Removed Titles: ${removedTipps}, New Titles in we:kb: ${newTipps})"
                aPackage.save()


                if (aPackage.kbartSource && updatePackageInfo.automaticUpdate) {
                    KbartSource src = KbartSource.get(aPackage.kbartSource.id)
                    src.kbartHasWekbFields = !setAllTippsNotInKbartToDeleted
                    src.lastRun = new Date()
                    src.lastUpdateUrl = (src.defaultSupplyMethod == RDStore.KS_DSMETHOD_HTTP_URL ? lastUpdateURL : "")
                    src.lastChangedInKbart = lastChangedInKbart
                    src.save()
                }
            }
            /* log.debug("final flush");
             cleanupService.cleanUpGorm()*/

        } catch (Exception e) {
            log.error("Error by kbartImportProcess: "+ e.message)
            //e.printStackTrace()
            UpdatePackageInfo.withTransaction {
                updatePackageInfo.refresh()
                updatePackageInfo.endTime = new Date()
                String description = "An error occurred while processing the KBART file. More information can be seen in the system log. "
                if(lastUpdateURL && updatePackageInfo.automaticUpdate){
                    description = description+ "File from URL: ${lastUpdateURL}"
                }
                updatePackageInfo.description = description
                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                updatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                updatePackageInfo.updateUrl = lastUpdateURL
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
        log.info("Begin KbartProcess, transmitted: ${tsvFile.length()}")
        boolean encodingPass
        int countRows = 0
        List result = []
        String encoding
        if(StandardCharsets.US_ASCII.newEncoder().canEncode(tsvFile.newInputStream().text)) {
            encodingPass = true
        }
        else {
            encoding = UniversalDetector.detectCharset(tsvFile)

  /*          if(encoding == null){
                CharsetMatch[] charsetMatches= new CharsetDetector().setText(tsvFile.newInputStream()).detectAll()
                log.debug("charsetMatches -> "+charsetMatches)
               charsetMatches.eachWithIndex{ CharsetMatch entry, int i ->
                   if(entry.name == "UTF-8" && entry.confidence > 5){
                       log.debug("set encoding after match in charsetMatches: confidence -> "+entry.confidence)
                       encoding = "UTF-8"
                   }
               }
            }*/
            encodingPass = encoding in ["UTF-8", "WINDOWS-1252", "US-ASCII"]
        }
        if(!encodingPass) {
            log.warn("Encoding of file is wrong. File encoding is: ${encoding}")
            UpdatePackageInfo.withTransaction {
                String description = "Encoding of KBART file is wrong. File encoding was: ${encoding}. "
                if(lastUpdateURL && updatePackageInfo.automaticUpdate){
                    description = description+ "File from URL: ${lastUpdateURL}"
                }
                updatePackageInfo.description = description
                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                updatePackageInfo.endTime = new Date()
                updatePackageInfo.updateUrl = lastUpdateURL
                updatePackageInfo.save()
            }
        }
        else {
            List minimumKbartStandard = ['publication_title',
                                         'title_url',
                                         'title_id',
                                         'publication_type']
            int countMinimumKbartStandard = 0

            try {



                List<String> rows = tsvFile.newInputStream().text.split('\n')
                if(rows.size() > 1){
                    Map<String, Integer> colMap = [:]

                    String delimiter = getDelimiter(rows[0])
                    if(delimiter) {
                        rows[0].split(delimiter).eachWithIndex { String headerCol, int c ->
                            if (headerCol.startsWith("\uFEFF"))
                                headerCol = headerCol.substring(1)
                            //println("headerCol: ${headerCol}")
                            switch (headerCol.toLowerCase().trim()) {
                                case "publication_title": colMap.publication_title = c
                                    countMinimumKbartStandard++
                                    break
                                case "print_identifier": colMap.print_identifier = c
                                    break
                                case "online_identifier": colMap.online_identifier = c
                                    break
                                case "date_first_issue_online": colMap.date_first_issue_online = c
                                    break
                                case "num_first_vol_online": colMap.num_first_vol_online = c
                                    break
                                case "date_last_issue_online": colMap.date_last_issue_online = c
                                    break
                                case "num_first_issue_online": colMap.num_first_issue_online = c
                                    break
                                case "num_last_vol_online": colMap.num_last_vol_online = c
                                    break
                                case "num_last_issue_online": colMap.num_last_issue_online = c
                                    break
                                case "title_url": colMap.title_url = c
                                    countMinimumKbartStandard++
                                    break
                                case "first_author": colMap.first_author = c
                                    break
                                case "title_id": colMap.title_id = c
                                    countMinimumKbartStandard++
                                    break
                                case "embargo_info": colMap.embargo_info = c
                                    break
                                case "coverage_depth": colMap.coverage_depth = c
                                    break
                                case "notes": colMap.notes = c
                                    break
                                case "publisher_name": colMap.publisher_name = c
                                    break
                                case "publication_type": colMap.publication_type = c
                                    countMinimumKbartStandard++
                                    break
                                case "date_monograph_published_print": colMap.date_monograph_published_print = c
                                    break
                                case "date_monograph_published_online": colMap.date_monograph_published_online = c
                                    break
                                case "monograph_volume": colMap.monograph_volume = c
                                    break
                                case "monograph_edition": colMap.monograph_edition = c
                                    break
                                case "first_editor": colMap.first_editor = c
                                    break
                                case "parent_publication_title_id": colMap.parent_publication_title_id = c
                                    break
                                case "preceding_publication_title_id": colMap.preceding_publication_title_id = c
                                    break
                                case "superseding_publication_title_id": colMap.superseding_publication_title_id = c
                                    break
                                case "access_type": colMap.access_type = c
                                    break

                                    //beginn with headercolumn spec for wekb
                                case "oa_type": colMap.oa_type = c
                                    break
                                case "ddc": colMap.ddc = c
                                    break
                                case "medium": colMap.medium = c
                                    break
                                case "doi_identifier": colMap.doi_identifier = c
                                    break
                                case "subject_area": colMap.subject_area = c
                                    break
                                case "language": colMap.language = c
                                    break
                                case "package_name": colMap.package_name = c
                                    break
                                case "package_id": colMap.package_id = c
                                    break
                                case "access_start_date": colMap.access_start_date = c
                                    break
                                case "access_end_date": colMap.access_end_date = c
                                    break
                                case "last_changed": colMap.last_changed = c
                                    break
                                case "status": colMap.status = c
                                    break
                                case "listprice_eur": colMap.listprice_eur = c
                                    break
                                case "listprice_usd": colMap.listprice_usd = c
                                    break
                                case "listprice_gbp": colMap.listprice_gbp = c
                                    break
                                case "monograph_parent_collection_title": colMap.monograph_parent_collection_title = c
                                    break
                                case "zdb_id": colMap.zdb_id = c
                                    break
                                case "ezb_id": colMap.ezb_id = c
                                    break
                                case "package_ezb_anchor": colMap.package_ezb_anchor = c
                                    break
                                case "oa_gold": colMap.oa_gold = c
                                    break
                                case "oa_hybrid": colMap.oa_hybrid = c
                                    break
                                case "oa_apc_eur": colMap.oa_apc_eur = c
                                    break
                                case "oa_apc_usd": colMap.oa_apc_usd = c
                                    break
                                case "oa_apc_gbp": colMap.oa_apc_gbp = c
                                    break
                                case "package_isil": colMap.package_isil = c
                                    break
                                case "package_isci": colMap.package_isci = c
                                    break
                                case "ill_indicator": colMap.ill_indicator = c
                                    break
                                case "title_gokb_uuid": colMap.title_gokb_uuid = c
                                    break
                                case "package_gokb_uuid": colMap.package_gokb_uuid = c
                                    break
                                case "title_wekb_uuid": colMap.title_wekb_uuid = c
                                    break
                                case "package_wekb_uuid": colMap.package_wekb_uuid = c
                                    break
                                default: log.info("unhandled parameter type ${headerCol}, ignoring ...")
                                    break
                            }
                        }

                        if (minimumKbartStandard.size() != countMinimumKbartStandard) {
                            log.warn("KBART file does not have one or any of the headers: ${minimumKbartStandard}")
                            UpdatePackageInfo.withTransaction {
                                String description = "KBART file does not have one or any of the headers: ${minimumKbartStandard.join(', ')}. "
                                if(lastUpdateURL && updatePackageInfo.automaticUpdate){
                                    description = description+ "File from URL: ${lastUpdateURL}"
                                }
                                updatePackageInfo.description = description
                                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                updatePackageInfo.endTime = new Date()
                                updatePackageInfo.updateUrl = lastUpdateURL
                                updatePackageInfo.save()
                            }


                        } else {
                            //Don't delete the header
                            //rows.remove(0)
                            countRows = rows.size() - 1
                            Set<String> filteredRows = []
                            filteredRows.addAll(rows)
                            //log.debug("Begin kbart processing rows ${countRows}")
                            filteredRows.eachWithIndex { row, Integer r ->
                                //log.debug("now processing entry ${row}")
                                List<String> cols = row.split(delimiter)
                                Map rowMap = [:]
                                colMap.eachWithIndex { def entry, int i ->
                                    if (cols[entry.value] && !cols[entry.value].isEmpty()) {
                                        rowMap."${entry.key}" = cols[entry.value].replace("\r", "")
                                    }
                                    // With rowMap."${entry.key}".replaceAll(/\"/, "") come not in titles with  "A" Force
                                    //rowMap."${entry.key}" = rowMap."${entry.key}" ? rowMap."${entry.key}".replaceAll(/\"/, "") : rowMap."${entry.key}"
                                    rowMap."${entry.key}" = rowMap."${entry.key}" ? rowMap."${entry.key}".replaceAll("\\x00", "") : rowMap."${entry.key}"
                                }
                                if(rowMap.publication_title != null) {
                                    rowMap.rowIndex = r
                                    result << rowMap
                                }
                            }
                            //log.debug("End kbart processing rows ${countRows}")
                        }
                    }else {
                        log.warn("no delimiter $delimiter: ${lastUpdateURL}")
                        UpdatePackageInfo.withTransaction {
                            String description = "Separator for the KBART was not recognized. The following separators are recognized: Tab, comma, semicolons. "
                            if(lastUpdateURL && updatePackageInfo.automaticUpdate){
                                description = description+ "File from URL: ${lastUpdateURL}"
                            }
                            updatePackageInfo.description = description
                            updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                            updatePackageInfo.endTime = new Date()
                            updatePackageInfo.updateUrl = lastUpdateURL
                            updatePackageInfo.save()
                        }
                    }
                }else {
                    log.warn("KBART file is empty:  ${lastUpdateURL}")
                    UpdatePackageInfo.withTransaction {
                        String description = "KBART file is empty. "
                        if(lastUpdateURL && updatePackageInfo.automaticUpdate){
                            description = description+ "File from URL: ${lastUpdateURL}"
                        }
                        updatePackageInfo.description = description
                        updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                        updatePackageInfo.endTime = new Date()
                        updatePackageInfo.updateUrl = lastUpdateURL
                        updatePackageInfo.save()
                    }
                }
            } catch (Exception e) {
                log.error("Error by KbartProcess: ${e.message}")
                //e.printStackTrace()
                UpdatePackageInfo.withTransaction {
                    updatePackageInfo.refresh()
                    String description = "An error occurred while processing the KBART file. More information can be seen in the system log. "
                    if(lastUpdateURL && updatePackageInfo.automaticUpdate){
                        description = description+ "File from URL: ${lastUpdateURL}"
                    }
                    updatePackageInfo.description = description
                    updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                    updatePackageInfo.endTime = new Date()
                    updatePackageInfo.updateUrl = lastUpdateURL
                    updatePackageInfo.save()
                }

            }
        }
        log.info("End KbartProcess with ${countRows} rows")

        result
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
}

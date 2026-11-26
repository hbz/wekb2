package wekb.kbart

import grails.gorm.transactions.NotTransactional
import org.hibernate.Session
import org.hibernate.SessionFactory
import wekb.Identifier
import wekb.IdentifierNamespace
import wekb.KbartSource
import wekb.Package
import wekb.Platform
import wekb.TitleInstancePackagePlatform
import wekb.UpdatePackageInfo
import wekb.UpdateTippInfo
import wekb.helper.IdentiferNamespaceStore
import wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import grails.util.Holders
import org.mozilla.universalchardet.UniversalDetector
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction
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

    SessionFactory sessionFactory

    KbartCoverageSyncService kbartCoverageSyncService
    KbartImportValidationService kbartImportValidationService
    KbartImportService kbartImportService

    static final String PROGRESS_VALIDATING = 'Validating Titles'
    static final String PROGRESS_UPDATING = 'Updating Titles'
    static final String PROGRESS_CREATING = 'Creating Titles'
    static final String PROGRESS_DUPLICATES = 'Removing Duplicates'
    static final String PROGRESS_DELETING = 'Deleting missing Titles'
    static final String PROGRESS_COMPLETED = 'Completed'
    static final String PROGRESS_FAILED = 'Failed'


    Set<String> supportedHeadersKbartV2 = [
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
            "WINDOWS-1252",
            "UTF-16LE",
            "UTF-16BE",
    ] as Set<String>

    @NotTransactional
    void kbartImportManual(Package pkg, File tsvFile){
        log.info("Beginn kbartImportManual ${pkg.name}")
        List kbartRows = []
        String lastUpdateURL = ""
        Long pkgID = pkg.id

        Long updatePackageInfoID = UpdatePackageInfo.withNewTransaction {

            Package processedPkg = Package.get(pkgID)

            if (!processedPkg) {
                throw new IllegalStateException("Package not found: ${pkgID}")
            }

            UpdatePackageInfo info = new UpdatePackageInfo(
                    pkg: processedPkg,
                    startTime: new Date(),
                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                    description: "Starting package update.",
                    onlyRowsWithLastChanged: false,
                    automaticUpdate: false,
                    kbartHasWekbFields: false,
                    updateFromFileUpload: true
            )

            info.countPreviouslyTippsInWekb = processedPkg.getTippCountWithoutRemoved()
            info.countNowTippsInWekb = processedPkg.getTippCountWithoutRemoved()
            info.countCurrentTipps = processedPkg.getCurrentTippCount()
            info.countDeletedTipps = processedPkg.getDeletedTippCount()
            info.save(failOnError: true, flush: true)

            info.id as Long
        }

        UpdatePackageInfo updatePackageInfo = UpdatePackageInfo.get(updatePackageInfoID)

        if (!updatePackageInfo) {
            throw new IllegalStateException(
                    "UpdatePackageInfo not found: ${updatePackageInfoID}"
            )
        }

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

                updatePackageInfo = kbartImportProcess(kbartRows, pkg, lastUpdateURL, updatePackageInfo)
            }

            if (updatePackageInfo.status != RDStore.UPDATE_STATUS_FAILED)
                cleanUpUpdateInfosTables(pkg)

        } catch (Exception exception) {
            log.error("Error by kbartImportManual: ${exception.message}")

            exception.printStackTrace()

            UpdatePackageInfo.withNewTransaction {

                UpdatePackageInfo info = UpdatePackageInfo.get(updatePackageInfoID)

                if (!info) {
                    throw new IllegalStateException(
                            "UpdatePackageInfo not found: ${updatePackageInfoID}",
                            exception
                    )
                }

                info.status = RDStore.UPDATE_STATUS_FAILED
                info.endTime = new Date()
                info.description = "An error occurred while processing the KBART file. More information can be seen in the system log."
                info.save(failOnError: true, flush: true)
            }
        }
        log.info("End kbartImportManual ${pkg.name}")
    }

    @NotTransactional
    UpdatePackageInfo kbartImportProcess(List kbartRows, Package pkg, String lastUpdateURL, UpdatePackageInfo updatePackageInfo) {
        if (!pkg?.id || !updatePackageInfo?.id) {
            throw new IllegalArgumentException('Package and UpdatePackageInfo must be persisted before starting the import')
        }

        final Long pkgID = pkg.id
        final Long platformID = pkg.nominalPlatform?.id
        if (!platformID) throw new IllegalArgumentException('Package has no nominal platform')

        final Long updateInfoID = updatePackageInfo.id

        final List<Map> rows = (kbartRows ?: []).collect { Map row -> new LinkedHashMap(row) }
        final boolean hasWekbStatus = rows && rows.first().containsKey('status')
        boolean failed = false
        int processedRows = 0
        int created = 0
        int changed = 0
        int removed = 0
        int deleted = 0
        int invalid = 0
        int rowIndex = 0


        int previouslyTipps = TitleInstancePackagePlatform.executeQuery(
                "select count(*) from TitleInstancePackagePlatform tipp where " +
                        "tipp.status in :status and " +
                        "tipp.pkg = :package",
                [package: pkg, status: [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_EXPECTED]])[0]

        Set<Long> seenTippIDs = new HashSet<>()
        Set<Long> tippDuplicates = new LinkedHashSet<>()

        Map<String, List<Map>> newGroups = new LinkedHashMap<>()

        List<Map> importErrors = []
        int failedErrorRecords = 0

        if (!rows) {
            failed = true
            importErrors << [titleID: null, tippID: null, rows: [],
                             message: 'KBART file contains no title rows',
                             type: RDStore.UPDATE_TYPE_FAILED_TITLE]
        }


        Map<String, List<Map>> groups = new LinkedHashMap<>()
        rows.eachWithIndex { Map row, int index ->
            String titleID = row.title_id?.toString()?.trim()
            String key = titleID ? "title:${titleID}" : "missing:${index}"
            if (!groups.containsKey(key)) groups[key] = []
            groups[key] << row
        }

        int validatedGroups = 0
        int totalGroups = groups.size()

        updateImportProgress(updateInfoID, 0, PROGRESS_VALIDATING)

        groups.each { String key, List<Map> titleRows ->
            String titleID = titleRows.first().title_id?.toString()?.trim()

            List<Integer> rowNumbers = titleRows.collect { Map row ->
                row.rowIndex != null ?
                        row.rowIndex as Integer :
                        rows.indexOf(row) + 1
            }

            if (!titleID) {

                failed = true
                invalid += titleRows.size()

                importErrors << [
                        titleID: null,
                        tippID: null,
                        rows: rowNumbers,
                        message: 'Missing title_id',
                        type: RDStore.UPDATE_TYPE_INVAILD_TITLE
                ]
                return
            }

            titleRows.each { Map row ->
                try {
                    def validation = kbartImportValidationService.tippValidateForKbart(row)
                    if (!validation.valid) {
                        failed = true
                        invalid++
                        importErrors << [titleID: titleID, tippID: null,
                                         rows: [row.rowIndex != null ? (row.rowIndex as Integer) + 1 : rows.indexOf(row) + 1],
                                         message: "Validation failed: ${validation.errorMessage}",
                                         type: RDStore.UPDATE_TYPE_INVAILD_TITLE]
                    }
                } catch (Exception e) {
                    failed = true
                    invalid++
                    importErrors << [titleID: titleID, tippID: null,
                                     rows: [row.rowIndex != null ? (row.rowIndex as Integer) + 1 : rows.indexOf(row) + 1],
                                     message: "Validation exception: ${e.message}",
                                     type: RDStore.UPDATE_TYPE_FAILED_TITLE]
                    log.error("KBART validation failed for title_id=${titleID} in row ${row.rowIndex}", e)
                }
            }

            validatedGroups++

            if (validatedGroups % 100 == 0 || validatedGroups == totalGroups) {

                int percent = totalGroups > 0 ? Math.round(validatedGroups * 10.0 / totalGroups) as int : 10

                updateImportProgress(updateInfoID, percent, PROGRESS_VALIDATING, validatedGroups, totalGroups)
            }
        }

        if (!failed) {

            int processedGroups = 0
            int totalUpdateGroups = groups.size()

            groups.each { String key, List<Map> titleRows ->
                String titleID = titleRows.first().title_id.toString().trim()
                try {
                    Map outcome = TitleInstancePackagePlatform.withNewSession {

                        Map transactionOutcome = TitleInstancePackagePlatform.withNewTransaction {

                            Package processedPkg = Package.get(pkgID)
                            Platform processedPlatform = Platform.get(platformID)
                            UpdatePackageInfo processedUpdateInfo = UpdatePackageInfo.get(updateInfoID)

                            List<TitleInstancePackagePlatform> matches = kbartImportService.tippsMatchingByTitleIDAutoUpdateV2(titleID, processedPkg)

                            if (!matches) {
                                return [
                                        isNew: true,
                                ]
                            }

                            TitleInstancePackagePlatform tipp = matches.first()
                            List<Long> duplicateIDs = matches.drop(1).collect { it.id as Long }

                            Map updateResult = kbartImportService.tippImportForUpdate(tipp, titleRows.first(), processedUpdateInfo, processedPkg, processedPlatform)

                            tipp = updateResult.tippObject
                            if (!tipp) throw new IllegalStateException("TIPP update returned no TIPP for title_id=${titleID}")

                            boolean isChanged = updateResult.changedTipp == true
                            if (!hasWekbStatus && tipp.status != RDStore.KBC_STATUS_CURRENT) {
                                tipp.status = RDStore.KBC_STATUS_CURRENT
                                isChanged = true
                            }
                            tipp.save(failOnError: true)

                            if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                                kbartCoverageSyncService.createOrUpdateCoverageForTipp(tipp, buildCoverageMaps(titleRows))
                            }

                            return [
                                    isNew     : false,
                                    id        : tipp.id,
                                    changed   : isChanged,
                                    removed   : updateResult.removedTipp == true,
                                    duplicates: duplicateIDs
                            ]
                        }
                        return transactionOutcome
                    }

                    if (outcome.isNew) {
                        newGroups[titleID] = titleRows
                    } else {
                        seenTippIDs.add(outcome.id as Long)
                        tippDuplicates.addAll(outcome.duplicates as Collection<Long>)
                        processedRows += titleRows.size()
                        if (outcome.changed) changed++
                        if (outcome.removed) removed++
                    }
                } catch (Exception e) {
                    failed = true
                    importErrors << [titleID: titleID, tippID: null,
                                     rows: titleRows.collect { Map r ->
                                         r.rowIndex != null ? (r.rowIndex as Integer) + 1 : rows.indexOf(r) + 1
                                     },
                                     message: "Title processing failed: ${e.message}",
                                     type: RDStore.UPDATE_TYPE_FAILED_TITLE]
                    log.error("KBART import: failed to process title group, title_id=${titleID}", e)
                }
                finally {
                    processedGroups++

                    if (processedGroups % 100 == 0 || processedGroups == totalUpdateGroups) {

                        int percent = 10 + Math.round(processedGroups * 55.0 / totalUpdateGroups) as int

                        updateImportProgress(updateInfoID, percent, PROGRESS_UPDATING, processedGroups, totalUpdateGroups
                        )
                    }
                }
            }
        }

        if (!failed && newGroups) {

            updateImportProgress(updateInfoID, 65, PROGRESS_CREATING, 0, newGroups.size())

            try {
                Map<String, Long> createdByTitleID = TitleInstancePackagePlatform.withNewTransaction {
                    UpdatePackageInfo processedUpdateInfo = UpdatePackageInfo.get(updateInfoID)
                    List<Map> oneRowPerTitle = newGroups.values().collect { List<Map> group -> group.first() }

                    Set<Long> newIDs = kbartImportService.createTippBatch(oneRowPerTitle, processedUpdateInfo, pkgID, platformID)
                    if (newIDs == null || newIDs.size() != newGroups.size()) {
                        throw new IllegalStateException(
                                "TIPP batch creation returned ${newIDs?.size() ?: 0} IDs; " +
                                        "expected ${newGroups.size()}")
                    }

                    IdentifierNamespace namespace = IdentiferNamespaceStore.TITLE_ID_NAMESPACE
                    if (!namespace) throw new IllegalStateException("Identifier namespace 'title_id' was not found")

                    List idPairs = Identifier.executeQuery('''
                    select distinct ident.tipp.id, ident.value
                    from Identifier ident
                    where ident.tipp.id in (:ids) and ident.namespace = :namespace''', [ids: newIDs as List<Long>, namespace: namespace])
                    Map<String, Long> result = [:]
                    idPairs.each { pair ->
                        String idValue = pair[1]?.toString()?.trim()
                        if (!newGroups.containsKey(idValue) || result.containsKey(idValue)) {
                            throw new IllegalStateException(
                                    "Unexpected or duplicate title_id in created TIPP batch: ${idValue}")
                        }
                        result[idValue] = pair[0] as Long
                    }
                    if (result.keySet() != newGroups.keySet()) {
                        throw new IllegalStateException(
                                "Created TIPP identifiers do not match the requested title_id values")
                    }

                    result.each { String titleID, Long tippID ->
                        TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(tippID)
                        if (!tipp || tipp.pkg?.id != pkgID) {
                            throw new IllegalStateException(
                                    "Created TIPP ${tippID} not found in package for title_id=${titleID}")
                        }
                        if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                            kbartCoverageSyncService.createOrUpdateCoverageForTipp(tipp, buildCoverageMaps(newGroups[titleID]))
                        }
                    }
                    result
                }
                seenTippIDs.addAll(createdByTitleID.values())
                processedRows += newGroups.values().sum { it.size() } as int
                created += createdByTitleID.size()
            } catch (Exception e) {
                failed = true

                newGroups.each { String titleID, List<Map> titleRows ->
                    importErrors << [titleID: titleID, tippID: null,
                                     rows: titleRows.collect { Map r ->
                                         r.rowIndex != null ? (r.rowIndex as Integer) + 1 : (rows.indexOf(r) + 1)
                                     },
                                     message: "TIPP batch creation failed: ${e.message}",
                                     type: RDStore.UPDATE_TYPE_FAILED_TITLE]
                }
                log.error('KBART import: TIPP batch creation failed', e)
            }

            updateImportProgress(updateInfoID, 80, PROGRESS_CREATING, newGroups.size(), newGroups.size())

        }

        if (!failed && tippDuplicates) {

            updateImportProgress(updateInfoID, 85, PROGRESS_DUPLICATES)

            try {
                TitleInstancePackagePlatform.withNewTransaction {
                    UpdatePackageInfo processedUpdateInfo = UpdatePackageInfo.get(updateInfoID)
                    tippDuplicates.findAll { Long id -> !seenTippIDs.contains(id) }.each { Long id ->
                        TitleInstancePackagePlatform duplicate = TitleInstancePackagePlatform.get(id)
                        if (duplicate && duplicate.pkg.id == pkgID && duplicate.status != RDStore.KBC_STATUS_REMOVED) {
                            String previousStatus = duplicate.status.value
                            duplicate.status = RDStore.KBC_STATUS_REMOVED
                            duplicate.save(failOnError: true)
                            Date now = new Date()
                            new UpdateTippInfo(
                                    uuid: UUID.randomUUID().toString(),
                                    description: "Remove Title '${duplicate.name}' because it is a duplicate in WEKB!",
                                    tipp: duplicate,
                                    startTime: now,
                                    endTime: now,
                                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                    type: RDStore.UPDATE_TYPE_REMOVED_TITLE,
                                    oldValue: previousStatus,
                                    newValue: 'Removed',
                                    tippProperty: 'status',
                                    kbartProperty: 'status',
                                    updatePackageInfo: processedUpdateInfo
                            ).save(failOnError: true)
                            removed++
                        }
                    }
                }
            } catch (Exception e) {
                failed = true
                importErrors << [titleID: null, tippID: null, rows: [],
                                 message: "Duplicate cleanup failed: ${e.message}",
                                 type: RDStore.UPDATE_TYPE_FAILED_TITLE]
                log.error('KBART import: duplicate cleanup failed', e)
            }
            updateImportProgress(updateInfoID, 90, PROGRESS_DUPLICATES)
        }

        if (!failed && !hasWekbStatus) {

            updateImportProgress(updateInfoID, 91, PROGRESS_DELETING)

            try {
                int deletedInCleanup = TitleInstancePackagePlatform.withNewTransaction {
                    Package processedPkg = Package.get(pkgID)
                    UpdatePackageInfo processedUpdateInfo = UpdatePackageInfo.get(updateInfoID)
                    List statuses = [RDStore.KBC_STATUS_CURRENT,
                                     RDStore.KBC_STATUS_EXPECTED,
                                     RDStore.KBC_STATUS_RETIRED]
                    List<Long> existing = TitleInstancePackagePlatform.executeQuery(
                            'select t.id from TitleInstancePackagePlatform t ' +
                                    'where t.pkg = :pkg and t.status in (:statuses)',
                            [pkg: processedPkg, statuses: statuses])

                    Set<Long> protectedIDs = new HashSet<Long>(seenTippIDs)
                    protectedIDs.addAll(tippDuplicates)
                    List<Long> missing = existing.findAll { Long id -> !protectedIDs.contains(id) }
                    int deletedCount = 0


                    missing.each { Long id ->
                        TitleInstancePackagePlatform missingTipp = TitleInstancePackagePlatform.get(id)
                        if (missingTipp && missingTipp.pkg?.id == pkgID &&
                                missingTipp.status in statuses) {
                            String oldStatus = missingTipp.status?.value
                            missingTipp.status = RDStore.KBC_STATUS_DELETED
                            missingTipp.save(failOnError: true)
                            Date now = new Date()
                            new UpdateTippInfo(
                                    uuid: UUID.randomUUID().toString(),
                                    description: "Delete title '${missingTipp.name}' because it is missing in KBART-File.",
                                    tipp: missingTipp,
                                    startTime: now, endTime: now,
                                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                    type: RDStore.UPDATE_TYPE_DELETED_TITLE,
                                    oldValue: oldStatus,
                                    newValue: 'Deleted',
                                    tippProperty: 'status',
                                    kbartProperty: 'status',
                                    updatePackageInfo: processedUpdateInfo
                            ).save(failOnError: true)
                            deletedCount++
                        }
                    }
                    deletedCount
                }
                deleted += deletedInCleanup
            } catch (Exception e) {
                failed = true
                importErrors << [titleID: null, tippID: null, rows: [],
                                 message: "Final cleanup failed: ${e.message}",
                                 type: RDStore.UPDATE_TYPE_FAILED_TITLE]
                log.error('KBART import: final cleanup failed', e)
            }

            updateImportProgress(updateInfoID, 95, PROGRESS_DELETING)
        }


        importErrors.each { Map issue ->
            try {
                List<Integer> affectedRows = (issue.rows ?: []) as List<Integer>
                List<List<Integer>> rowChunks = affectedRows ? affectedRows.collate(100) : [[]]
                rowChunks.each { List<Integer> chunk ->
                    UpdateTippInfo.withNewTransaction {
                        UpdatePackageInfo processedUpdateInfo = UpdatePackageInfo.get(updateInfoID)
                        TitleInstancePackagePlatform processedTipp = issue.tippID ? TitleInstancePackagePlatform.get(issue.tippID as Long) : null
                        String location = issue.titleID ? "title_id=${issue.titleID}" : ''
                        String rowText = chunk ? "KBART rows: ${chunk.join(', ')}" : ''
                        Date now = new Date()
                        new UpdateTippInfo(
                                uuid: UUID.randomUUID().toString(),
                                description: "${issue.message} (${location}${rowText})".toString(),
                                tipp: processedTipp,
                                startTime: now,
                                endTime: now,
                                status: RDStore.UPDATE_STATUS_FAILED,
                                type: issue.type ?: RDStore.UPDATE_TYPE_FAILED_TITLE,
                                oldValue: '',
                                newValue: '',
                                tippProperty: '',
                                updatePackageInfo: processedUpdateInfo
                        ).save(failOnError: true)
                    }
                    failedErrorRecords++
                }
            } catch (Exception loggingError) {
                log.error("Unable to persist KBART error details for title_id=${issue.titleID}", loggingError)
            }
        }

        updateImportProgress(updateInfoID, 100, PROGRESS_COMPLETED)

        int countExistingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                "select count(*) from TitleInstancePackagePlatform tipp where " +
                        "tipp.status in (:status) and " +
                        "tipp.pkg = :package",
                [package: pkg, status: [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_EXPECTED]])[0]


        UpdatePackageInfo.withNewTransaction {
            UpdatePackageInfo info = UpdatePackageInfo.get(updateInfoID)
            Package processedPkg = Package.get(pkgID)
            info.endTime = new Date()
            info.kbartHasWekbFields = hasWekbStatus
            info.updateUrl = lastUpdateURL
            info.onlyRowsWithLastChanged = false
            info.countKbartRows = rows.size()
            info.countProcessedKbartRows = processedRows
            info.countNewTipps = created
            info.countChangedTipps = changed
            info.countRemovedTipps = removed
            info.countDeletedTippsByProcess = deleted
            info.countInValidTipps = invalid
            info.countNowTippsInWekb = countExistingTippsAfterImport
            info.countPreviouslyTippsInWekb = previouslyTipps
            info.countCurrentTipps = info.pkg.getCurrentTippCount()
            info.countDeletedTipps = info.pkg.getDeletedTippCount()
            info.description = failed ?
                    "Package Update failed: ${failedErrorRecords} error records saved. See Update Title Infos for details." :
                    "Package Update: " +
                            "(KbartLines: ${rows.size()}, Processed Titles in this run: ${info.countProcessedKbartRows}, " +
                            "All Titles previously: ${info.countPreviouslyTippsInWekb}, All Titles now: ${info.countCurrentTipps}, Removed Titles: 0, New All Titles: ${info.countNewTipps}, Changed All Titles: ${info.countChangedTipps}), Deleted All Titles: ${info.pkg.getDeletedTippCount()}, Invalid Titles: ${info.countInValidTipps}) "
            if (failed) info.status = RDStore.UPDATE_STATUS_FAILED
            info.save(failOnError: true)

            if (!failed) {
                processedPkg.lastUpdated = new Date()
                processedPkg.save(failOnError: true)
                if (processedPkg.kbartSource && info.automaticUpdate) {
                    KbartSource source = KbartSource.get(processedPkg.kbartSource.id)
                    source.lastRun = new Date()
                    source.lastUpdateUrl = lastUpdateURL ?: ''
                    source.kbartHasWekbFields = hasWekbStatus
                    source.save(failOnError: true)
                }
            }
        }
        return UpdatePackageInfo.get(updateInfoID)
    }


    @NotTransactional
    List kbartProcess(File tsvFile, String lastUpdateURL, UpdatePackageInfo updatePackageInfo) {
        log.info("Begin KbartProcess, transmitted: ${tsvFile?.length() ?: 0}")

        int countRows = 0
        List<Map<String, Object>> result = []

        Closure markImportAsFailed = { String description ->

            final Long updateInfoID = updatePackageInfo?.id

            if (!updateInfoID) {
                throw new IllegalStateException(
                        "Cannot mark KBART import as failed: Update Package Info has no ID"
                )
            }

            UpdatePackageInfo.withNewTransaction {

                UpdatePackageInfo info = UpdatePackageInfo.get(updateInfoID)

                if (!info) {
                    throw new IllegalStateException(
                            "Cannot mark KBART import as failed: " +
                                    "UpdatePackageInfo ID=${updateInfoID} not found"
                    )
                }

                Package processedPkg = info.pkg

                if (!processedPkg) {
                    throw new IllegalStateException(
                            "Cannot mark KBART import as failed: " +
                                    "Package not found for UpdatePackageInfo ID=${updateInfoID}"
                    )
                }

                String errorDescription = description ?: "Unknown KBART import error."

                if (lastUpdateURL && info.automaticUpdate) {
                    errorDescription += " File from URL: ${lastUpdateURL}"
                }

                info.description = errorDescription
                info.status = RDStore.UPDATE_STATUS_FAILED
                info.endTime = new Date()
                info.updateUrl = lastUpdateURL
                info.countPreviouslyTippsInWekb = processedPkg.getTippCountWithoutRemoved()
                info.countNowTippsInWekb = processedPkg.getTippCountWithoutRemoved()
                info.countCurrentTipps = processedPkg.getCurrentTippCount()
                info.countDeletedTipps = processedPkg.getDeletedTippCount()

                info.save(failOnError: true, flush: true)
            }

            log.warn("KBART import marked as failed: " + "UpdatePackageInfo ID=${updateInfoID}, reason=${description}")
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

            encoding = determineEncoding(tsvFile)

            if (!encoding || !allowedEncodings.contains(encoding)) {

                log.warn("Encoding of file is wrong. File encoding is: ${encoding}")

                markImportAsFailed(
                        "Encoding of KBART file is wrong. " +
                                "File encoding was: ${encoding}. " +
                                "AllowedEncodings: ${allowedEncodings}"
                )

                log.info("End KbartProcess with ${countRows} rows")
                return result
            }

            log.debug("Detected KBART encoding: ${encoding}")

        } catch (Exception encodingException) {

            log.error(
                    "Could not determine KBART encoding: ${encodingException.message}",
                    encodingException
            )

            markImportAsFailed(
                    "The encoding of the KBART file could not be determined. " +
                            "AllowedEncodings: ${allowedEncodings}"
            )

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
                if (!supportedHeadersKbartV2.contains(normalizedHeader)) {
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

                        if (!supportedHeadersKbartV2.contains(normalizedHeader)) {
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

                rowMap.rowIndex = record.recordNumber
                result << rowMap
                importedRows++


               /* if (rowMap.publication_title) {
                    rowMap.rowIndex = record.recordNumber

                    result << rowMap
                    importedRows++
                } else {
                    skippedRows++

                    log.warn("Ignoring KBART row ${record.recordNumber}: publication_title is empty or missing")

                    UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                            description: "Ignoring KBART row ${record.recordNumber}: publication_title is empty or missing",
                            tipp: null,
                            startTime: new Date(),
                            endTime: new Date(),
                            status: RDStore.UPDATE_STATUS_FAILED,
                            type: RDStore.UPDATE_TYPE_INVAILD_TITLE,
                            oldValue: '',
                            newValue: '',
                            tippProperty: 'name',
                            kbartProperty: 'publication_title',
                            updatePackageInfo: updatePackageInfo
                    ).save()
                }*/
            }

            if (countRows == 0) {
                log.warn("KBART file contains no data rows: ${lastUpdateURL}")

                markImportAsFailed("KBART file is empty. ")

                return result
            }

            log.info("KBART processing completed: " +
                            "${countRows} rows processed, " +
                            "${importedRows} rows imported, " +
                            "${skippedRows} rows skipped")

        } catch (Exception exception) {
            log.error("Error by KbartProcess: ${exception.message}", exception)
            markImportAsFailed("An error occurred while processing the KBART file. More information can be seen in the system log. ")

        } finally {
            if (parser != null) {
                try {
                    parser.close()
                } catch (Exception closeException) {
                    log.warn("Could not close KBART CSV parser: ${closeException.message}")
                }
            } else if (reader != null) {
                try {
                    reader.close()
                } catch (Exception closeException) {
                    log.warn("Could not close KBART reader: ${closeException.message}")
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


    static String determineEncoding(File file) {

        // BOM prüfen
        byte[] bom = new byte[4]

        int bytesRead = file.withInputStream { InputStream inputStream ->
            inputStream.read(bom)
        }

        // UTF-8 BOM: EF BB BF
        if (bytesRead >= 3 &&
                (bom[0] & 0xFF) == 0xEF &&
                (bom[1] & 0xFF) == 0xBB &&
                (bom[2] & 0xFF) == 0xBF) {

            return "UTF-8"
        }

        // UTF-16 LE BOM: FF FE
        if (bytesRead >= 2 &&
                (bom[0] & 0xFF) == 0xFF &&
                (bom[1] & 0xFF) == 0xFE) {

            return "UTF-16LE"
        }

        // UTF-16 BE BOM: FE FF
        if (bytesRead >= 2 &&
                (bom[0] & 0xFF) == 0xFE &&
                (bom[1] & 0xFF) == 0xFF) {

            return "UTF-16BE"
        }


        if (isValidUtf8(file)) {
            return "UTF-8"
        }


        // Fallback: UniversalDetector
        String detectedEncoding

        file.withInputStream { InputStream inputStream ->
            detectedEncoding = UniversalDetector.detectCharset(inputStream)
        }

        return normalizeEncoding(detectedEncoding)
    }


    private static boolean isValidUtf8(File file) {

        def decoder = StandardCharsets.UTF_8.newDecoder()

        decoder.onMalformedInput(CodingErrorAction.REPORT)
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT)

        try {
            decoder.decode(ByteBuffer.wrap(file.bytes))
            return true
        }
        catch (CharacterCodingException ignored) {
            return false
        }
    }


    private static String normalizeEncoding(String encoding) {

        if (!encoding) {
            return null
        }

        String normalized = encoding.trim().toUpperCase(Locale.ROOT)

        switch (normalized) {

            case "UTF8":
            case "UTF-8":
                return "UTF-8"
            case "ASCII":
            case "US-ASCII":
                return "US-ASCII"

            case "CP1252":
            case "WINDOWS-1252":
                return "WINDOWS-1252"

            case "UTF16LE":
            case "UTF-16LE":
                return "UTF-16LE"

            case "UTF16BE":
            case "UTF-16BE":
                return "UTF-16BE"

            default:
                return normalized
        }
    }

    private List<Map> buildCoverageMaps(List<Map> titleRows) {
        titleRows.findAll { Map row ->
            row.date_first_issue_online || row.date_last_issue_online ||
                    row.num_first_vol_online || row.num_first_issue_online ||
                    row.num_last_vol_online || row.num_last_issue_online ||
                    row.embargo_info || row.coverage_depth || row.notes || row.access_type
        }.collect { Map row ->
            [startDate: row.date_first_issue_online,
             endDate: row.date_last_issue_online,
             startVolume: row.num_first_vol_online,
             startIssue: row.num_first_issue_online,
             endVolume: row.num_last_vol_online,
             endIssue: row.num_last_issue_online,
             embargo: row.embargo_info,
             coverageDepth: row.coverage_depth,
             coverageNote: row.notes,
             accessType: row.access_type]
        }
    }

    private void updateImportProgress(Long updateInfoID, int percent, String phase, int processed = 0, int total = 0) {
        UpdatePackageInfo.withNewTransaction {

            UpdatePackageInfo info = UpdatePackageInfo.get(updateInfoID)

            if (!info) {
                throw new IllegalStateException(
                        "UpdatePackageInfo not found: ${updateInfoID}"
                )
            }

            if (info.status == RDStore.UPDATE_STATUS_FAILED) {
                return
            }

            int safePercent = Math.max(0, Math.min(99, percent))

            info.description = "Package update in progress: ${safePercent}% - ${phase}" + (total > 0 ? " (${processed}/${total})" : "")

            info.save(failOnError: true, flush: true)
        }
    }

}

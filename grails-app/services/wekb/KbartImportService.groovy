package wekb

import org.springframework.context.MessageSource
import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovy.sql.Sql
import org.grails.web.json.JSONArray
import org.hibernate.Session

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Transactional
class KbartImportService {

    CleanupService cleanupService

    DateFormatService dateFormatService
    MessageSource messageSource

    LinkedHashMap tippImportForUpdate(TitleInstancePackagePlatform tipp, Map tippMap, LinkedHashMap tippsWithCoverage, UpdatePackageInfo updatePackageInfo) {
        LinkedHashMap result = [newTipp: false, removedTipp: false, tippObject: null, updatePackageInfo: updatePackageInfo,
                                tippsWithCoverage: tippsWithCoverage]
        log.info("Begin tippImportForUpdate")
        long start = System.currentTimeMillis()
        Package pkg = tippMap.pkg
        Platform plt = tippMap.nominalPlatform

        if (pkg && plt) {

            if (tipp) {
                tipp.kbartImportRunning = true
                tipp.fromKbartImport = true
                result = updateTippWithKbart(result, tipp, tippMap, updatePackageInfo, result.tippsWithCoverage)
                tipp = result.tipp
            }

            result.tippObject = tipp
        } else {
            log.debug("Not able to reference TIPP: ${tippMap}")
        }

        log.debug("processed tippImportForUpdate at: ${System.currentTimeMillis()-start} msecs")
        log.info("End tippImportForUpdate: TIPP UUID -> ${result.tippObject?.uuid}")

        result.updatePackageInfo = updatePackageInfo

        result
    }

    List<TitleInstancePackagePlatform> tippsMatchingByTitleIDAutoUpdate(String titleID, Package aPackage) {
        List<TitleInstancePackagePlatform> tippList = []
        log.debug("tippsMatchingByTitleID provider internal identifier matching by ")

        IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

        if(titleID && identifierNamespace) {
            tippList = Identifier.executeQuery('select i.tipp from Identifier i, TitleInstancePackagePlatform tipp where ' +
                    'i.tipp = tipp and ' +
                    'tipp.status != :status and ' +
                    'tipp.pkg = :package and ' +
                    'i.namespace = :namespaceValue and ' +
                    'i.value = :value and ' +
                    'i.tipp is not null', [namespaceValue: identifierNamespace, value: titleID, package: aPackage, status: RDStore.KBC_STATUS_REMOVED])

            if (tippList.size() > 0) {
                log.debug("tippsMatchingByTitleID provider internal identifier matching by " + tippList.size() + ": " + tippList.id)
                return tippList
            }
        }
        return tippList
    }

    /**
     * Create a new TIPP
     */
    TitleInstancePackagePlatform tippCreate(tipp_fields = [:]) {

        RefdataValue tipp_status = tipp_fields.status ? RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, tipp_fields.status) : null

        RefdataValue tipp_medium = null
        if (tipp_fields.medium) {
            tipp_medium = determineMediumRef(tipp_fields.medium)
        }
        RefdataValue tipp_publicationType = null
        if (tipp_fields.type) {
            tipp_publicationType = determinePublicationType(tipp_fields.type)
        }else{
            tipp_publicationType = RDStore.TIPP_PUBLIC_TYPE_NOSET
        }
        long start = System.currentTimeMillis()
        TitleInstancePackagePlatform result = new TitleInstancePackagePlatform(
                uuid: tipp_fields.uuid ?: UUID.randomUUID().toString(),
                status: tipp_status,
                name: tipp_fields.name,
                medium: tipp_medium,
                publicationType: tipp_publicationType,
                url: tipp_fields.url,
                pkg: tipp_fields.pkg,
                hostPlatform: tipp_fields.pkg.nominalPlatform)

        result.save()

        if (!result) {
            log.error("TIPP creation failed!")
        }
        log.debug("processed at: ${System.currentTimeMillis()-start} msecs")

        result
    }

    static RefdataValue determinePublicationType(String type) {
        if (type) {
            switch (type.trim()) {
                case "serials":
                case "serial":
                case "Serial":
                case "Journal":
                case "journal":
                    return RDStore.TIPP_PUBLIC_TYPE_SERIAL
                    break;
                case "monographs":
                case "monograph":
                case "Monograph":
                case "Book":
                case "book":
                    return RDStore.TIPP_PUBLIC_TYPE_MONO
                    break;
                case "Database":
                case "database":
                    return RDStore.TIPP_PUBLIC_TYPE_DB
                    break;
                case "Other":
                case "other":
                    return RDStore.TIPP_PUBLIC_TYPE_OTHER
                    break;
                default:
                    return RDStore.TIPP_PUBLIC_TYPE_NOSET
                    break;
            }
        }
        else {
            return RDStore.TIPP_PUBLIC_TYPE_NOSET
        }
    }

    static RefdataValue determineMediumRef(String medium) {
        if (medium) {
            switch (medium.toLowerCase().trim()) {
                case "a & i database":
                case "abstract- & indexdatenbank":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "A & I Database")
                case "audio":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Audio")
                case "database":
                case "fulltext database":
                case "Volltextdatenbank":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Database")
                case "dataset":
                case "datenbestand":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Dataset")
                case "film":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Film")
                case "image":
                case "bild":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Image")
                case "journal":
                case "zeitschrift":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Journal")
                case "book":
                case "buch":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Book")
                case "published score":
                case "musiknoten":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Published Score")
                case "article":
                case "artikel":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Article")
                case "software":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Software")
                case "statistics":
                case "statistiken":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Statistics")
                case "market data":
                case "marktdaten":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Market Data")
                case "standards":
                case "normen":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Standards")
                case "biography":
                case "biografie":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Biography")
                case "legal text":
                case "gesetzestext/urteil":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Legal Text")
                case "cartography":
                case "kartenwerk":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Cartography")
                case "miscellaneous":
                case "sonstiges":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Miscellaneous")
                case "other":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Other")
                default:
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, medium)
            }
        }
        else {
            return null
        }
    }

    boolean createOrUpdateIdentifierForTipp(Map result, TitleInstancePackagePlatform tipp, String namespace_val, String identifierValue, String kbartProperty, UpdatePackageInfo updatePackageInfo){
        boolean identifierChanged = false
        String newValue = identifierValue.trim()
        String oldValue = ''
        Identifier identifier
        IdentifierNamespace ns = IdentifierNamespace.findByValueAndTargetType(namespace_val, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

        //tipp = tipp.refresh()
        if(ns) {
            try {
                def pattern = ns.pattern ? ~"${ns.pattern}" : null
                if (pattern && !(newValue ==~ pattern)) {
                    UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                            description: "$kbartProperty has wrong format to import. "+messageSource.getMessage("identifier.create.pattern.fail.${ns.value.replaceAll("\\s", "").toLowerCase()}", null, Locale.ENGLISH),
                            tipp: tipp,
                            startTime: new Date(),
                            endTime: new Date(),
                            status: RDStore.UPDATE_STATUS_FAILED,
                            type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                            updatePackageInfo: updatePackageInfo,
                            kbartProperty: kbartProperty,
                            tippProperty: "identifiers[${namespace_val}]",
                            oldValue: oldValue,
                            newValue: newValue
                    ).save()
                } else {
                    LinkedHashSet<Identifier> identifiersWithSameNamespace = tipp.ids.findAll { it.namespace.value == namespace_val }

                    switch (identifiersWithSameNamespace.size()) {
                        case 0:
                            identifier = new Identifier(namespace: ns, value: newValue, tipp: tipp)
                            identifier.save()
                            identifierChanged = true
                            break
                        case 1:
                            if (identifiersWithSameNamespace[0].value != newValue) {
                                identifierChanged = true
                                oldValue = identifiersWithSameNamespace[0].value
                                identifiersWithSameNamespace[0].value = newValue
                                identifiersWithSameNamespace[0].save()
                                identifier = identifiersWithSameNamespace[0]
                            }
                            break
                        default:
                            List toDeletedIdentifier = []
                            identifiersWithSameNamespace.each { Identifier tippIdentifier ->
                                toDeletedIdentifier << tippIdentifier.id
                            }

                            toDeletedIdentifier.each {
                                //Identifier.executeUpdate("delete from Identifier where id_id = :id", [id: it])
                                tipp.removeFromIds(Identifier.get(it))
                            }
                            identifierChanged = true
                            identifier = new Identifier(namespace: ns, value: newValue, tipp: tipp)
                            identifier.save()
                            //println(identifier.errors)
                            break
                    }
                    if (identifier && identifierChanged && !result.newTipp) {
                        //updatePackageInfo = updatePackageInfo.refresh()
                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                description: "Create or update identifier of title '${tipp.name}'",
                                tipp: tipp,
                                startTime: new Date(),
                                endTime: new Date(),
                                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                                updatePackageInfo: updatePackageInfo,
                                kbartProperty: kbartProperty,
                                tippProperty: "identifiers[${namespace_val}]",
                                oldValue: oldValue,
                                newValue: newValue
                        ).save()
                    }
                }
            }
            catch (Exception e) {
                log.error("createOrUpdateIdentifierForTipp: -> ${kbartProperty} ${newValue}:" + e.message)
                //e.printStackTrace()
            }
        }


        return result.changedTipp ?: identifierChanged
    }

    @Deprecated
    void createOrUpdateIdentifierForTipp(TitleInstancePackagePlatform tipp, String namespace_val, String identifierValue){
        Identifier identifier
        IdentifierNamespace ns = IdentifierNamespace.findByValueAndTargetType(namespace_val, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

        //tipp = tipp.refresh()

        LinkedHashSet<Identifier> identifiersWithSameNamespace = tipp.ids.findAll{it.namespace.value == namespace_val}

        switch (identifiersWithSameNamespace.size()) {
            case 0:
                identifier = new Identifier(namespace: ns, value: identifierValue, tipp: tipp)
                identifier.save()
                break
            case 1:
                identifiersWithSameNamespace[0].value = identifierValue
                identifiersWithSameNamespace[0].save()
                identifier = identifiersWithSameNamespace[0]
                break
            default:
                List toDeletedIdentifier = []
                identifiersWithSameNamespace.each{Identifier tippIdentifier ->
                    toDeletedIdentifier << tippIdentifier.id
                }

                toDeletedIdentifier.each{
                    //Identifier.executeUpdate("delete from Identifier where id_id = :id", [id: it])
                    tipp.removeFromIds(Identifier.get(it))
                }

                identifier = new Identifier(namespace: ns, value: identifierValue, tipp: tipp)
                identifier.save()
                break
        }

    }

    void createOrUpdateCoverageForTipp(TitleInstancePackagePlatform tipp, List coverage){
        log.debug("createOrUpdateCoverageForTipp Beginn")
        //tipp = tipp.refresh()
        tipp.kbartImportRunning = true
        Integer countNewCoverages = coverage.size()
        Integer countTippCoverages = tipp.coverageStatements ? tipp.coverageStatements.size() : 0

        if(countNewCoverages == 1 && countTippCoverages == 1){
            RefdataValue cov_depth = null

            boolean coverageChanged = false

            if (coverage[0].coverageDepth instanceof String) {
                cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, coverage[0].coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
            }

            def parsedStart = TextUtils.completeDateString(coverage[0].startDate)
            def parsedEnd = TextUtils.completeDateString(coverage[0].endDate, false)

            coverageChanged = ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'startIssue', coverage[0].startIssue)
            coverageChanged = ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'endIssue', coverage[0].endIssue)
            coverageChanged = ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'startVolume', coverage[0].startVolume)
            coverageChanged = ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'endVolume', coverage[0].endVolume)
            coverageChanged = ClassUtils.setDateIfPresent(parsedStart, tipp.coverageStatements[0], 'startDate', true)
            coverageChanged = ClassUtils.setDateIfPresent(parsedEnd, tipp.coverageStatements[0], 'endDate', true)
            coverageChanged = ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'embargo', coverage[0].embargo)
            coverageChanged = ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'coverageNote', coverage[0].coverageNote)
            if(cov_depth) {
                coverageChanged = ClassUtils.setRefdataIfDifferent(cov_depth.value, tipp.coverageStatements[0], 'coverageDepth', RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, true)
            }

            if(coverageChanged) {
                tipp.lastUpdated = new Date()
            }
        }
        else if(countNewCoverages == 0 && countTippCoverages > 0){
            def cStsIDs = tipp.coverageStatements.id.clone()
            cStsIDs.each {
                TIPPCoverageStatement.executeUpdate('delete from TIPPCoverageStatement cs where cs.id = :csID',[csID: it])
            }
            tipp.lastUpdated = new Date()
            if (!tipp.save()) {
                log.error("Tipp save error: ")
                tipp.errors.allErrors.each {
                    println it
                }
            }

        }else if(countNewCoverages > 0 && countTippCoverages == 0){

            List filtered = coverage.unique(false) { a, b ->
                a.startDate <=> b.startDate ?:
                        a.startVolume <=> b.startVolume ?:
                                a.startIssue <=> b.startIssue ?:
                                        a.endDate <=> b.endDate ?:
                                                a.endVolume <=> b.endVolume ?:
                                                        a.endIssue <=> b.endIssue
            }

            filtered.each { c ->
                def parsedStart = TextUtils.completeDateString(c.startDate)
                def parsedEnd = TextUtils.completeDateString(c.endDate, false)
                def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
                def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)

                RefdataValue cov_depth = null

                if (c.coverageDepth instanceof String) {
                    cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, c.coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
                }

                tipp.addToCoverageStatements(
                        'startVolume': c.startVolume,
                        'startIssue': c.startIssue,
                        'endVolume': c.endVolume,
                        'endIssue': c.endIssue,
                        'embargo': c.embargo,
                        'coverageDepth': cov_depth,
                        'coverageNote': c.coverageNote,
                        'startDate': startAsDate,
                        'endDate': endAsDate,
                        'uuid': UUID.randomUUID().toString()
                )
                tipp.lastUpdated = new Date()
            }
        }else if(countNewCoverages > 1 && countTippCoverages >= 1) {
            def cStsIDs = tipp.coverageStatements.id.clone()
            cStsIDs.each {
                TIPPCoverageStatement.executeUpdate('delete from TIPPCoverageStatement cs where cs.id = :csID',[csID: it])
            }


            List filtered = coverage.unique(false) { a, b ->
                a.startDate <=> b.startDate ?:
                        a.startVolume <=> b.startVolume ?:
                                a.startIssue <=> b.startIssue ?:
                                        a.endDate <=> b.endDate ?:
                                                a.endVolume <=> b.endVolume ?:
                                                        a.endIssue <=> b.endIssue
            }

            filtered.each { c ->
                def parsedStart = TextUtils.completeDateString(c.startDate)
                def parsedEnd = TextUtils.completeDateString(c.endDate, false)
                def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
                def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)

                RefdataValue cov_depth = null

                if (c.coverageDepth instanceof String) {
                    cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, c.coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
                }

                try {

                    TIPPCoverageStatement tippCoverageStatement = new TIPPCoverageStatement(
                            startVolume: c.startVolume,
                            startIssue: c.startIssue,
                            endVolume: c.endVolume,
                            endIssue: c.endIssue,
                            embargo: c.embargo,
                            coverageDepth: cov_depth,
                            coverageNote: c.coverageNote,
                            startDate: startAsDate,
                            endDate: endAsDate,
                            uuid: UUID.randomUUID().toString(),
                            tipp: tipp
                    )
                    if(!tippCoverageStatement.save()) {
                        log.error("Tipp save error: ")
                        tipp.errors.allErrors.each {
                            println it
                        }
                    }


/*                    tipp.addToCoverageStatements(
                            'startVolume': c.startVolume,
                            'startIssue': c.startIssue,
                            'endVolume': c.endVolume,
                            'endIssue': c.endIssue,
                            'embargo': c.embargo,
                            'coverageDepth': cov_depth,
                            'coverageNote': c.coverageNote,
                            'startDate': startAsDate,
                            'endDate': endAsDate,
                            'uuid': UUID.randomUUID().toString()
                    )*/
                }
                catch (Exception e){
                    log.error("Failed to add coverage statement to tipp: " + e.message)
                }

            }
            tipp.lastUpdated = new Date()

        }

        /* coverage.each { c ->
             def parsedStart = TextUtils.completeDateString(c.startDate)
             def parsedEnd = TextUtils.completeDateString(c.endDate, false)

             //com.k_int.ClassUtils.setStringIfDifferent(tipp, 'coverageNote', c.coverageNote)
             //com.k_int.ClassUtils.setRefdataIfDifferent(c.coverageDepth, tipp, 'coverageDepth', RCConstants.TIPP_COVERAGE_DEPTH, true)

             def cs_match = false
             def conflict = false
             def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
             def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)
             def conflicting_statements = []



             Map cMap = ["startIssue": c.startIssue,
                           "endIssue": c.endIssue,
                           "startVolume": c.startVolume,
                           "endVolume": c.endVolume]

             println(cMap)

             tipp.coverageStatements?.each { TIPPCoverageStatement tcs ->
                 Map tcsMap = ["startIssue": tcs.startIssue,
                               "endIssue": tcs.endIssue,
                               "startVolume": tcs.startVolume,
                               "endVolume": tcs.endVolume]
                 println( tcsMap.equals(cMap))
                 println( tcsMap.toString() == cMap.toString())
                 println(tcsMap)
                 *//*if (!cs_match) {
                     if (!tcs.endDate && !endAsDate) {
                         conflict = true
                     } else if (tcs.toString() == c.toString()) {
                         log.debug("Matched CoverageStatement by Map")
                         cs_match = true
                     } else if (tcs.startVolume && tcs.startVolume == c.startVolume) {
                         log.debug("Matched CoverageStatement by startVolume")
                         cs_match = true
                     } else if (tcs.startDate && tcs.startDate == startAsDate) {
                         log.debug("Matched CoverageStatement by startDate")
                         cs_match = true
                     } else if (!tcs.startVolume && !tcs.startDate && !tcs.endVolume && !tcs.endDate) {
                         log.debug("Matched CoverageStatement with unspecified values")
                         cs_match = true
                     } else if (tcs.startDate && tcs.endDate) {
                         if (startAsDate && startAsDate > tcs.startDate && startAsDate < tcs.endDate) {
                             conflict = true
                         } else if (endAsDate && endAsDate > tcs.startDate && endAsDate < tcs.endDate) {
                             conflict = true
                         }
                     }

                     if (conflict) {
                         conflicting_statements.add(tcs)
                     } else if (cs_match) {
                         com.k_int.ClassUtils.setStringIfDifferent(tcs, 'startIssue', c.startIssue)
                         com.k_int.ClassUtils.setStringIfDifferent(tcs, 'endIssue', c.endIssue)
                         com.k_int.ClassUtils.setStringIfDifferent(tcs, 'startVolume', c.startVolume)
                         com.k_int.ClassUtils.setStringIfDifferent(tcs, 'endVolume', c.endVolume)
                         com.k_int.ClassUtils.setDateIfPresent(parsedStart, tcs, 'startDate', true)
                         com.k_int.ClassUtils.setDateIfPresent(parsedEnd, tcs, 'endDate', true)
                         com.k_int.ClassUtils.setStringIfDifferent(tcs, 'embargo', c.embargo)
                         com.k_int.ClassUtils.setStringIfDifferent(tcs, 'coverageNote', c.coverageNote)
                         com.k_int.ClassUtils.setRefdataIfDifferent(tcs.coverageDepth?.value, tcs, 'coverageDepth', RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, true)
                     }
                 } else {
                     log.debug("Matched new coverage ${c} on multiple existing coverages!")
                 }*//*
             }

             for (def cst : conflicting_statements) {
                 tipp.removeFromCoverageStatements(cst)
             }

             if (!cs_match) {

                 def cov_depth = null

                 if (c.coverageDepth instanceof String) {
                     cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, c.coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
                 } else if (c.coverageDepth instanceof Integer) {
                     cov_depth = RefdataValue.get(c.coverageDepth)
                 } else if (c.coverageDepth instanceof Map) {
                     if (c.coverageDepth.id) {
                         cov_depth = RefdataValue.get(c.coverageDepth.id)
                     } else {
                         cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, (c.coverageDepth.name ?: c.coverageDepth.value))
                     }
                 }

                 tipp.addToCoverageStatements(
                         'startVolume': c.startVolume,
                         'startIssue': c.startIssue,
                         'endVolume': c.endVolume,
                         'endIssue': c.endIssue,
                         'embargo': c.embargo,
                         'coverageDepth': cov_depth,
                         'coverageNote': c.coverageNote,
                         'startDate': startAsDate,
                         'endDate': endAsDate
                 )
             }
             // refdata setStringIfDifferent(tipp, 'coverageDepth', c.coverageDepth)
         }*/
        if (!tipp.save()) {
            log.error("Tipp save error: ")
            tipp.errors.allErrors.each {
                println it
            }
        }
        log.debug("createOrUpdateCoverageForTipp End")
    }



    boolean checkAndSetByChangedValue(Map result, TitleInstancePackagePlatform tipp, String dataType, UpdatePackageInfo updatePackageInfo, Map tippMap, String kbartProperty, String tippProperty, boolean acceptNullValue = true, String refdataCategory = null) {
        boolean valueChanged = false
        //log.debug("in check and set by changed value")
        if (tippMap.containsKey(kbartProperty)) {
            if (dataType == 'Date') {
                LocalDateTime ldt = null
                if (acceptNullValue && (tippMap[kbartProperty] == null || tippMap[kbartProperty] == "") && (tipp[tippProperty] != null && tipp[tippProperty] != "")) {
                    if (!result.newTipp) {
                        valueChanged = true
                        String oldValue = renderObjectValue(tipp[tippProperty])
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty])
                    }
                    tipp[tippProperty] = null

                }else if (tippMap[kbartProperty] && tippMap[kbartProperty].toString().trim()) {
                    String oldValue = renderObjectValue(tipp[tippProperty])
                    String newValue = tippMap[kbartProperty]
                    try {
                        if ( newValue.trim() ) {
                            ldt = TextUtils.completeDateString(newValue)
                        }
                    }
                    catch (Exception e) {
                        log.error("Parse date fail. Date to parse was -> ${newValue}:" + e.message)
                    }

                    if (ldt) {
                        Date instant = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
                        if (instant != tipp[tippProperty]) {
                            if (!result.newTipp) {
                                valueChanged = true
                                createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty])
                            }
                            tipp[tippProperty] = instant
                        }
                    }else {
                        createUpdateTippInfoByTippChangeFail(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty], "The Date '${tippMap[kbartProperty]}' could not be parsed.")
                    }
                }
            } else if (dataType == 'RefDataValue') {
                RefdataValue v = null
                if (acceptNullValue && (tippMap[kbartProperty] == null || tippMap[kbartProperty] == "") && (tipp[tippProperty] != null && tipp[tippProperty] != "")) {
                    if (!result.newTipp) {
                        valueChanged = true
                        String oldValue = renderObjectValue(tipp[tippProperty])
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty])
                    }
                    tipp[tippProperty] = null

                } else if (tippMap[kbartProperty] && tippMap[kbartProperty].trim() && refdataCategory) {
                    String oldValue = renderObjectValue(tipp[tippProperty])
                    if(kbartProperty == 'medium'){
                        v = determineMediumRef(tippMap[kbartProperty])
                    }else if(kbartProperty == 'publication_type'){
                        v = determinePublicationType(tippMap[kbartProperty])
                    }else {
                        v = RefdataCategory.lookup(refdataCategory, tippMap[kbartProperty])
                    }
                    if (v) {
                        String newValue = renderObjectValue(v)
                        if (oldValue.toLowerCase() != newValue.toLowerCase()) {
                            if (!result.newTipp) {
                                valueChanged = true
                                createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                            }
                            tipp[tippProperty] = v
                        }
                    }else {
                        createUpdateTippInfoByTippChangeFail(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty], "The value ${tippMap[kbartProperty]} not found in RefdatValues.")
                    }
                }
            } else if (dataType == 'String') {
                String oldValue = renderObjectValue(tipp[tippProperty])
                String newValue = renderObjectValue(tippMap[kbartProperty])
                if (oldValue.toLowerCase() != newValue.toLowerCase()) {
                    if (!result.newTipp) {
                        valueChanged = true
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                    }
                    if(tippMap[kbartProperty] == ""){
                        tipp[tippProperty] = null
                    }else {
                        if (kbartProperty == 'title_url') {
                            tipp[tippProperty] = tippMap[kbartProperty].trim()
                        } else {
                            tipp[tippProperty] = tippMap[kbartProperty]
                        }
                    }
                }
            }
        }else {
            if(acceptNullValue) {
                if (!result.newTipp) {
                    String oldValue = renderObjectValue(tipp[tippProperty])
                    if (!(oldValue == "" || oldValue == null)) {
                        valueChanged = true
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, '')
                    }
                }
                tipp[tippProperty] = null
            }
        }

        if(valueChanged){
            if (!tipp.save()) {
                log.error("Tipp save error: ")
                tipp.errors.allErrors.each {
                    println it
                }
            }
        }

        return  result.changedTipp ?: valueChanged
    }

    void createUpdateTippInfoByTippChange(TitleInstancePackagePlatform tipp, UpdatePackageInfo updatePackageInfo, String kbartProperty, String tippProperty, String oldValue, String newValue){
        //updatePackageInfo = updatePackageInfo.refresh()
        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                description: "Changes in Title '${tipp.name}'",
                tipp: tipp,
                startTime: new Date(),
                endTime: new Date(),
                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                updatePackageInfo: updatePackageInfo,
                kbartProperty: kbartProperty,
                tippProperty: tippProperty,
                oldValue: oldValue,
                newValue: newValue
        ).save()
    }

    void createUpdateTippInfoByTippChangeFail(TitleInstancePackagePlatform tipp, UpdatePackageInfo updatePackageInfo, String kbartProperty, String tippProperty, String oldValue, String newValue, String description){
        //updatePackageInfo = updatePackageInfo.refresh()
        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                description: description,
                tipp: tipp,
                startTime: new Date(),
                endTime: new Date(),
                status: RDStore.UPDATE_STATUS_FAILED,
                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                updatePackageInfo: updatePackageInfo,
                kbartProperty: kbartProperty,
                tippProperty: tippProperty,
                oldValue: oldValue,
                newValue: newValue
        ).save()
    }

    String renderObjectValue(value) {
        String result = ''
        if (value != null) {
            switch (value.class) {
                case wekb.RefdataValue.class:
                    result = value.value
                    break
                case Boolean.class:
                    result = (value == true ? 'Yes' : 'No')
                    break
                case Date.class:
                    def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd')
                    result = sdf.format(value).toString()
                    break
                default:
                    result = value.toString();
            }
        }
        result
    }

    boolean createOrUpdatePrice(Map result, TitleInstancePackagePlatform tipp, RefdataValue priceType, RefdataValue currency, String price, String kbartProperty, UpdatePackageInfo updatePackageInfo){
        boolean priceChanged = false
        Float f = null
        String newValue = price
        String oldValue = ''
        TippPrice cp = null
        boolean priceUpdateFail = false
        if (price && priceType && currency) {
            try {
                String uniformedThousandSeparator = price.replaceAll("[,.](\\d{3})", '$1')
                uniformedThousandSeparator = uniformedThousandSeparator.replaceAll(",", ".")
                f = Float.parseFloat(uniformedThousandSeparator)
                if (!f.isNaN()) {
                    List<TippPrice> existPrices = TippPrice.findAllByTippAndPriceTypeAndCurrency(tipp, priceType, currency, [sort: 'lastUpdated', order: 'ASC'])
                    if (existPrices.size() == 1) {
                        cp = existPrices[0]
                        oldValue = cp.price.toString()
                        if (cp.price != f) {
                            cp.price = f
                            cp.save()
                            if (!tipp.save()) {
                                log.error("Tipp save error: ")
                                tipp.errors.allErrors.each {
                                    println it
                                }
                            }
                            priceChanged = true
                        }

                    } else if (existPrices.size() > 1) {
                        def pricesIDs = existPrices.id.clone()
                        TippPrice.withNewTransaction {
                            pricesIDs.each {
                                TippPrice tippPrice = TippPrice.get(it)
                                tipp.removeFromPrices(tippPrice)
                                tippPrice.delete()
                                //TippPrice.executeUpdate("delete from TippPrice id = ${it}")
                            }
                        }
                        if (!tipp.save()) {
                            log.error("Tipp save error: ")
                            tipp.errors.allErrors.each {
                                println it
                            }
                        }
                        tipp = tipp.refresh()
                        cp = new TippPrice(
                                tipp: tipp,
                                priceType: priceType,
                                currency: currency,
                                price: f)
                        cp.save()
                        if (!tipp.save()) {
                            log.error("Tipp save error: ")
                            tipp.errors.allErrors.each {
                                println it
                            }
                        }
                        priceChanged = true

                    } else {
                        cp = new TippPrice(
                                tipp: tipp,
                                priceType: priceType,
                                currency: currency,
                                price: f)
                        cp.save()
                        if (!tipp.save()) {
                            log.error("Tipp save error: ")
                            tipp.errors.allErrors.each {
                                println it
                            }
                        }
                        priceChanged = true
                    }
                } else {

                    List<TippPrice> existPrices = TippPrice.findAllByTippAndPriceTypeAndCurrency(tipp, priceType, currency, [sort: 'lastUpdated', order: 'ASC'])
                    //updatePackageInfo = updatePackageInfo.refresh()
                    UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                            description: "The value '${newValue}' can not parse to a price",
                            tipp: tipp,
                            startTime: new Date(),
                            endTime: new Date(),
                            status: RDStore.UPDATE_STATUS_FAILED,
                            type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                            updatePackageInfo: updatePackageInfo,
                            kbartProperty: kbartProperty,
                            tippProperty: "prices[${priceType.value}]",
                            oldValue: existPrices.size() > 0 ? existPrices.price.join(';') : '',
                            newValue: newValue
                    ).save()
                }
            } catch (Exception e) {
                log.error("createOrUpdatePrice -> kbartProperty ${newValue}:" + e.message)
                //e.printStackTrace()
                priceUpdateFail = true
            }

            if(!result.newTipp){
                if (!priceUpdateFail && cp && priceChanged) {
                    //updatePackageInfo = updatePackageInfo.refresh()
                    UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                            description: "Create or update price of title '${tipp.name}'",
                            tipp: tipp,
                            startTime: new Date(),
                            endTime: new Date(),
                            status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                            type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                            updatePackageInfo: updatePackageInfo,
                            kbartProperty: kbartProperty,
                            tippProperty: "prices[${priceType.value}]",
                            oldValue: oldValue,
                            newValue: newValue
                    ).save()
                }
                else if (priceUpdateFail || (cp == null && !priceChanged)) {
                    UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                            description: "Problem by create or update price of title '${tipp.name}'",
                            tipp: tipp,
                            startTime: new Date(),
                            endTime: new Date(),
                            status: RDStore.UPDATE_STATUS_FAILED,
                            type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                            updatePackageInfo: updatePackageInfo,
                            kbartProperty: kbartProperty,
                            tippProperty: "prices[${priceType.value}]",
                            oldValue: oldValue,
                            newValue: newValue
                    ).save()
                }
            }

        }else{
            if(!result.newTipp) {
                List<TippPrice> existPrices = TippPrice.findAllByTippAndPriceTypeAndCurrency(tipp, priceType, currency, [sort: 'lastUpdated', order: 'ASC'])
                if (existPrices.size() > 0) {
                    oldValue = existPrices.price.join('; ')
                    def pricesIDs = existPrices.id.clone()
                    TippPrice.withNewTransaction {
                        pricesIDs.each {
                            TippPrice tippPrice = TippPrice.get(it)
                            tipp.removeFromPrices(tippPrice)
                            tippPrice.delete()
                        }
                    }
                    if (!tipp.save()) {
                        log.error("Tipp save error: ")
                        tipp.errors.allErrors.each {
                            println it
                        }
                    }else {
                        tipp = tipp.refresh()
                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                description: "Delete price of title '${tipp.name}' because no price in kbart!",
                                tipp: tipp,
                                startTime: new Date(),
                                endTime: new Date(),
                                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                                updatePackageInfo: updatePackageInfo,
                                kbartProperty: kbartProperty,
                                tippProperty: "prices[${priceType.value}]",
                                oldValue: oldValue,
                                newValue: newValue
                        ).save()
                    }
                    priceChanged = true
                }
            }
        }

        return result.changedTipp ?: priceChanged
    }

    boolean checkAndSetByChangedCoverageValue(Map result, TitleInstancePackagePlatform tipp, TIPPCoverageStatement tippCoverageStatement, String dataType, UpdatePackageInfo updatePackageInfo = null, String kbartProperty, String tippProperty, String newValue, boolean acceptNullValue = true, String refdataCategory = null) {
        boolean valueChanged = false
        if (newValue) {
            if (dataType == 'Date') {
                LocalDateTime ldt = null
                if (acceptNullValue && (newValue == null || newValue == "") && (tipp[tippProperty] != null && tipp[tippProperty] != "")) {
                    if (!result.newTipp) {
                        valueChanged = true
                        String oldValue = renderObjectValue(tipp[tippProperty])
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                    }
                    tipp[tippProperty] = null
                }

                if (newValue && newValue.toString().trim()) {
                    String oldValue = renderObjectValue(tipp[tippProperty])
                    try {
                        if ( newValue ) {
                            ldt = TextUtils.completeDateString(newValue)
                        }
                    }
                    catch (Exception e) {
                        log.error("Parse date fail. Date to parse was -> ${newValue}:" + e.message)
                    }

                    if (ldt) {
                        Date instant = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
                        if (instant != tipp[tippProperty]) {
                            if (!result.newTipp) {
                                valueChanged = true
                                createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                            }
                            tipp[tippProperty] = instant
                        }
                    }else {
                        createUpdateTippInfoByTippChangeFail(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue, "The Date '${newValue}' could not be parsed.")
                    }
                }
            } else if (dataType == 'RefDataValue') {
                def v = null
                if (acceptNullValue && (newValue == null || newValue == "") && (tipp[tippProperty] != null && tipp[tippProperty] != "")) {
                    if (!result.newTipp) {
                        valueChanged = true
                        String oldValue = renderObjectValue(tipp[tippProperty])
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                    }
                    tipp[tippProperty] = null
                } else if (newValue && newValue.trim() && refdataCategory) {
                    String oldValue = renderObjectValue(tipp[tippProperty])
                    v = RefdataCategory.lookup(refdataCategory, newValue)
                    if (v) {
                        String renderNewValue = renderObjectValue(v)
                        if (oldValue.toLowerCase() != newValue.toLowerCase()) {
                            if (!result.newTipp) {
                                valueChanged = true
                                createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, renderNewValue)
                            }
                            tipp[tippProperty] = v
                        }
                    }else {
                        createUpdateTippInfoByTippChangeFail(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue, "The value ${newValue} not found in Refdata values.")
                    }
                }
            } else if (dataType == 'String') {
                String oldValue = renderObjectValue(tipp[tippProperty])
                String renderNewValue = renderObjectValue(newValue)
                if (oldValue.toLowerCase() != newValue.toLowerCase()) {
                    if (!result.newTipp) {
                        valueChanged = true
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                    }
                    tipp[tippProperty] = newValue
                }
            }
        }

        if(result.changedTipp){
            tippCoverageStatement.save()
        }

        return  result.changedTipp ?: valueChanged
    }

    LinkedHashMap updateTippWithKbart(LinkedHashMap result, TitleInstancePackagePlatform tipp, Map tippMap, UpdatePackageInfo updatePackageInfo, LinkedHashMap tippsWithCoverage) {
        log.debug("updateTippWithKbart: tipp.id: " + tipp.id)
        List identifierNameSpacesExistOnTipp = []
        //Kbart Fields to Ygor and then to wekb (siehe Wiki)

        // KBART -> status -> status -> status
        if (tipp.status != RDStore.KBC_STATUS_REMOVED && tippMap.status == "Removed") {
            ClassUtils.setRefdataIfDifferent(tippMap.status, tipp, 'status', RCConstants.COMPONENT_STATUS, false)
            result.removedTipp = true

            updatePackageInfo = updatePackageInfo.refresh()
            UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                    description: "Removed Title '${tippMap.publication_title}'",
                    tipp: tipp,
                    startTime: new Date(),
                    endTime: new Date(),
                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                    type: RDStore.UPDATE_TYPE_REMOVED_TITLE,
                    kbartProperty: 'status',
                    oldValue: tipp.status.value,
                    newValue: 'Removed',
                    tippProperty: 'status',
                    updatePackageInfo: updatePackageInfo
            ).save()
        } else {
            result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "status", "status", false, RCConstants.COMPONENT_STATUS)
        }
        log.debug("after save")
        // KBART -> publication_title -> name
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "publication_title", "name")
        //log.debug("1")
        // KBART -> first_author -> firstAuthor
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "first_author", "firstAuthor")
        //log.debug("2")
        // KBART -> first_editor -> firstEditor
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "first_editor", "firstEditor")
        //log.debug("3")
        // KBART -> publisher_name -> publisherName
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "publisher_name", "publisherName")
        //log.debug("4")
        // KBART -> publication_type -> publicationType
        if (tippMap.publication_type) {
            result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "publication_type", "publicationType", false, RCConstants.TIPP_PUBLICATION_TYPE)
        }else {
            String oldValue = renderObjectValue(tipp.publicationType)
            RefdataValue v = RDStore.TIPP_PUBLIC_TYPE_NOSET
            String newValue = renderObjectValue(v)
            if (oldValue.toLowerCase() != newValue.toLowerCase()) {
                if (!result.newTipp) {
                    result.changedTipp = true
                    createUpdateTippInfoByTippChange(tipp, updatePackageInfo, "publication_type", "publicationType", oldValue, newValue)
                }
                tipp.publicationType = v
            }
        }
        //log.debug("5")
        // KBART -> medium -> medium
        if (tippMap.medium) {
            result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "medium", "medium", false, RCConstants.TIPP_MEDIUM)
        }
        //log.debug("6")
        // KBART -> title_url -> url
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "title_url", "url")
        //log.debug("7")
        // KBART -> subject_area -> subjectArea
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "subject_area", "subjectArea")
        //log.debug("8")
        // KBART -> access_type -> accessType
        if (tippMap.access_type && tippMap.access_type.length() > 0) {
            //log.debug("8a")
            if (tippMap.access_type == 'P') {
                tippMap.access_type = 'Paid'
            } else if (tippMap.access_type == 'F') {
                tippMap.access_type = 'Free'
            }
            result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "access_type", "accessType", true, RCConstants.TIPP_ACCESS_TYPE)
        }
        //log.debug("9")
        // KBART -> access_start_date -> accessStartDate
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "access_start_date", "accessStartDate")
        //log.debug("10")
        // KBART -> access_end_date -> accessEndDate
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "access_end_date", "accessEndDate")
        //log.debug("11")
        // KBART -> last_changed -> lastChangedExternal
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "last_changed", "lastChangedExternal")
        //log.debug("12")
        // KBART -> notes -> note
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "notes", "note")
        //log.debug("13")
        // KBART -> date_monograph_published_print -> dateFirstInPrint
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "date_monograph_published_print", "dateFirstInPrint")
        //log.debug("14")
        // KBART -> date_monograph_published_online -> dateFirstOnline
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "date_monograph_published_online", "dateFirstOnline")
        //log.debug("15")
        // KBART -> monograph_volume -> volumeNumber
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "monograph_volume", "volumeNumber")
        //log.debug("16")
        // KBART -> monograph_edition -> editionStatement
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "monograph_edition", "editionStatement")
        //log.debug("17")
        // KBART -> monograph_parent_collection_title -> series
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "monograph_parent_collection_title", "series")
        //log.debug("18")
        // KBART -> parent_publication_title_id -> parentPublicationTitleId
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "parent_publication_title_id", "parentPublicationTitleId")
        //log.debug("19")
        // KBART -> oa_type -> openAccess
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "oa_type", "openAccess", true, RCConstants.TIPP_OPEN_ACCESS)
        //log.debug("before package_isil")
        // KBART -> package_isil -> identifiers['package_isil']
        if (tippMap.package_isil) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_isil", tippMap.package_isil, 'package_isil', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "package_isil"
        }

        // KBART -> package_isci -> identifiers['package_isci']
        if (tippMap.package_isci) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_isci", tippMap.package_isci, 'package_isci', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "package_isci"
        }

        // KBART -> ill_indicator -> identifiers['ill_indicator']
        if (tippMap.ill_indicator) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "ill_indicator", tippMap.ill_indicator, 'ill_indicator', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "ill_indicator"
        }


        // KBART -> preceding_publication_title_id -> precedingPublicationTitleId
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "preceding_publication_title_id", "precedingPublicationTitleId")

        // KBART -> superseding_publication_title_id -> supersedingPublicationTitleId
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "superseding_publication_title_id", "supersedingPublicationTitleId")

        //log.debug("before coverages")
        // KBART -> date_first_issue_online, date_last_issue_online,
        // num_first_vol_online, num_first_issue_online,
        // num_last_vol_online, num_last_issue_online
        // embargo_info, coverage_depth
        if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
            if (tippMap.date_first_issue_online || tippMap.date_last_issue_online || tippMap.num_first_vol_online ||
                    tippMap.num_first_issue_online || tippMap.num_last_vol_online || tippMap.num_last_issue_online) {
                Map coverageMap = [startDate    : tippMap.date_first_issue_online,
                                   endDate      : tippMap.date_last_issue_online,
                                   startVolume  : tippMap.num_first_vol_online,
                                   startIssue   : tippMap.num_first_issue_online,
                                   endVolume    : tippMap.num_last_vol_online,
                                   endIssue     : tippMap.num_last_issue_online,
                                   embargo      : tippMap.embargo_info,
                                   coverageDepth: tippMap.coverage_depth]

                if (tippsWithCoverage[tipp.id]) {
                    tippsWithCoverage[tipp.id] << coverageMap
                } else {
                    tippsWithCoverage[tipp.id] = [coverageMap]
                }
            }else{
                //To cleanup title without coverage
                if (tippsWithCoverage[tipp.id]) {
                    tippsWithCoverage[tipp.id] << []
                } else {
                    tippsWithCoverage[tipp.id] = []
                }
            }

        }
        //log.debug("before coverages II")
        if (tipp.publicationType != RDStore.TIPP_PUBLIC_TYPE_SERIAL && TIPPCoverageStatement.findByTipp(tipp)) {
            //log.debug("in coverage statements block")
            ClassUtils.setStringIfDifferent(tipp, 'note', tipp.coverageStatements[0].coverageNote)
            if (tipp.coverageStatements && tipp.coverageStatements.size() > 0) {
                def cStsIDs = tipp.coverageStatements.id.clone()
                TIPPCoverageStatement.withNewTransaction {
                    cStsIDs.each {
                        tipp.removeFromCoverageStatements(TIPPCoverageStatement.get(it))
                        TIPPCoverageStatement.get(it).delete()
                    }
                }
                if (!tipp.save()) {
                    log.error("Tipp save error: ")
                    tipp.errors.allErrors.each {
                        println it
                    }
                }
            }
        }
        //log.debug("before creating identifiers")
        // KBART -> package_ezb_anchor -> identifiers['package_ezb_anchor']
        if (tippMap.package_ezb_anchor) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_ezb_anchor", tippMap.package_ezb_anchor, 'package_ezb_anchor', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "package_ezb_anchor"
        }

    /*    // KBART -> zdb_id  -> identifiers['zdb']
        if (tippMap.zdb_id) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ZDB, tippMap.zdb_id, 'zdb_id', updatePackageInfo)
            identifierNameSpacesExistOnTipp << IdentifierNamespace.ZDB
        }*/

/*        // KBART -> ezb_id -> identifiers['ezb']
        if (tippMap.ezb_id) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EZB, tippMap.ezb_id, 'ezb_id', updatePackageInfo)
            identifierNameSpacesExistOnTipp << IdentifierNamespace.EZB
        }*/

        if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
            // KBART -> print_identifier-> identifiers
            if (tippMap.print_identifier) {
                result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISSN, tippMap.print_identifier, 'print_identifier', updatePackageInfo)
                identifierNameSpacesExistOnTipp << IdentifierNamespace.ISSN
            }

            // KBART -> online_identifier  -> identifiers
            if (tippMap.online_identifier) {
                result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISSN, tippMap.online_identifier, 'online_identifier', updatePackageInfo)
                identifierNameSpacesExistOnTipp << IdentifierNamespace.EISSN
            }
        } else if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_MONO) {
            // KBART -> print_identifier-> identifiers
            if (tippMap.print_identifier) {
                result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISBN, tippMap.print_identifier, 'print_identifier', updatePackageInfo)
                identifierNameSpacesExistOnTipp << IdentifierNamespace.ISBN
            }

            // KBART -> online_identifier  -> identifiers
            if (tippMap.online_identifier) {
                result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISBN, tippMap.online_identifier, 'online_identifier', updatePackageInfo)
                identifierNameSpacesExistOnTipp << IdentifierNamespace.EISBN
            }
        }

        // KBART -> title_id  -> identifiers
        if (tippMap.title_id) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, 'title_id', tippMap.title_id, 'title_id', updatePackageInfo)
            identifierNameSpacesExistOnTipp << 'title_id'
        }

        // KBART -> doi_identifier  -> identifiers
        if (tippMap.doi_identifier) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "doi", tippMap.doi_identifier, 'doi_identifier', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "doi"
        }

        //Cleanup Identifiers
        List<Long> deleteIdentifiers = []
        tipp.ids.each { Identifier identifier ->
            if (!(identifier.namespace.value in identifierNameSpacesExistOnTipp)) {
                deleteIdentifiers << identifier.id
            }
        }

        deleteIdentifiers.each {
            //Identifier.executeUpdate("delete from Identifier where id_id = :id", [id: it])
            tipp.removeFromIds(Identifier.get(it))
        }

        if(deleteIdentifiers.size() > 0){
            if (!tipp.save()) {
                log.error("Tipp save error: ")
                tipp.errors.allErrors.each {
                    println it
                }
            }
        }

        // KBART -> ddc -> ddcs
        if (tippMap.containsKey('ddc')) {

            if (tipp.ddcs) {
                def ddcsIDs = tipp.ddcs.id.clone()
                ddcsIDs.each {
                    tipp.removeFromDdcs(RefdataValue.get(it))
                }
                if (!tipp.save()) {
                    log.error("Tipp save error: ")
                    tipp.errors.allErrors.each {
                        println it
                    }
                }
                result.changedTipp = true
            }
            if (tippMap.ddc) {
                Set ddcs = tippMap.ddc.split(',')

                ddcs.each { String ddc ->
                    RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, ddc)
                    if (refdataValue && !(refdataValue in tipp.ddcs)) {
                        tipp.addToDdcs(refdataValue)
                    }
                }
                if(ddcs.size() > 0){
                    result.changedTipp = true
                }
            }

        }else {
            if (tipp.ddcs) {
                def ddcsIDs = tipp.ddcs.id.clone()
                ddcsIDs.each {
                    tipp.removeFromDdcs(RefdataValue.get(it))
                }
                if (!tipp.save()) {
                    log.error("Tipp save error: ")
                    tipp.errors.allErrors.each {
                        println it
                    }
                }
                result.changedTipp = true
            }
        }


        // KBART -> language -> language -> languages
        if (tippMap.containsKey('language')) {
            if (tipp.languages) {
                def langIDs = tipp.languages.id.clone()
                ComponentLanguage.withNewTransaction {
                    langIDs.each {
                        tipp.removeFromLanguages(ComponentLanguage.get(it))
                        ComponentLanguage.get(it).delete()
                    }
                }
                if (!tipp.save()) {
                    log.error("Tipp save error: ")
                    tipp.errors.allErrors.each {
                        println it
                    }
                }
                //ComponentLanguage.executeUpdate("delete from ComponentLanguage where tipp = :tipp", [tipp: tipp])
                result.changedTipp = true
            }
            if(tippMap.language) {
                Set languages = tippMap.language.split(',')
                languages.each { String lan ->
                    RefdataValue refdataValue
                    if (lan.size() == 2) {
                        refdataValue = RefdataValue.findByOwnerAndValueIlike(RefdataCategory.findByDesc(RCConstants.COMPONENT_LANGUAGE), lan.toLowerCase())
                    } else {
                        refdataValue = RefdataCategory.lookup(RCConstants.COMPONENT_LANGUAGE, lan)
                    }
                    if (refdataValue) {
                        if (!(tipp.languages && refdataValue in tipp.languages.language)) {
                            ComponentLanguage componentLanguage = new ComponentLanguage(tipp: tipp, language: refdataValue)
                            componentLanguage.save()
                        }
                    }
                }

                if (languages.size() > 0) {
                    result.changedTipp = true
                }

                if (!tipp.save()) {
                    log.error("Tipp save error: ")
                    tipp.errors.allErrors.each {
                        println it
                    }
                }
            }
            //tipp.refresh()
        }else {
            if (tipp.languages) {
                def langIDs = tipp.languages.id.clone()
                langIDs.each {
                    ComponentLanguage.withNewTransaction {
                        langIDs.each {
                            tipp.removeFromLanguages(ComponentLanguage.get(it))
                            ComponentLanguage.get(it).delete()
                        }
                    }
                }
                if (!tipp.save()) {
                    log.error("Tipp save error: ")
                    tipp.errors.allErrors.each {
                        println it
                    }
                }
                //ComponentLanguage.executeUpdate("delete from ComponentLanguage where tipp = :tipp", [tipp: tipp])
                result.changedTipp = true
            }
        }

        log.debug("before price section")
        // KBART -> listprice_eur -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_EUR, tippMap.listprice_eur, 'listprice_eur', updatePackageInfo)


        // KBART -> listprice_usd -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_USD, tippMap.listprice_usd, 'listprice_usd', updatePackageInfo)

        // KBART -> listprice_gbp -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_GBP, tippMap.listprice_gbp, 'listprice_gbp', updatePackageInfo)


        // KBART -> oa_apc_eur -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_EUR, tippMap.oa_apc_eur, 'oa_apc_eur', updatePackageInfo)


        // KBART -> oa_apc_usd -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_USD, tippMap.oa_apc_usd, 'oa_apc_usd', updatePackageInfo)


        // KBART -> oa_apc_gbp -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_GBP, tippMap.oa_apc_gbp, 'oa_apc_gbp', updatePackageInfo)


        log.debug("after price section")

        try {
            tipp = tipp.save(failOnError: true)

        } catch (Exception e) {
            log.error("KbartImportService tipp.save() error: " + e.message)
            //e.printStackTrace()
            /* updatePackageInfo = updatePackageInfo.refresh()
             UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                         description: "Changes in title fail. More information can be seen in the system log.",
                         tipp: tipp,
                         startTime: new Date(),
                         endTime: new Date(),
                         status: RDStore.UPDATE_STATUS_FAILED,
                         type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                         updatePackageInfo: updatePackageInfo,
                         kbartProperty: null,
                         tippProperty: null,
                         oldValue: null,
                         newValue: null
                 ).save()*/
        }

        result.tippsWithCoverage = tippsWithCoverage
        result.updatePackageInfo = updatePackageInfo
        result.tipp = tipp

        return result
    }

    HashSet<Long> createTippBatch(List tippMaps, UpdatePackageInfo updatePackageInfo) {
        HashSet<String> tippUrls = new HashSet<String>()
        int counterNewTippsToProcess = tippMaps.size()
        HashSet<Long> newtippIdList = new HashSet<Long>()

        //def dataSource = Holders.grailsApplication.mainContext.getBean('dataSource')
        //Sql sql = new Sql(dataSource)

        int max = 500
        int idx = 0
        TitleInstancePackagePlatform.withSession { Session sess ->
            for (int offset = 0; offset < tippMaps.size(); offset += max) {

                List tippMapsToProcess = tippMaps.drop(offset).take(max)
                for (Map tippMap : tippMapsToProcess) {
                    idx++

                    long start = System.currentTimeMillis()

                    try {

                        if(tippMap.url && tippMap.url in tippUrls){
                            log.info("createTippBatch (${idx} of $counterNewTippsToProcess) processed at: ${System.currentTimeMillis() - start} msecs")
                            log.info("Tipp not created because tipp already im system with createTippBatch!")
                        }else {

                            RefdataValue tipp_status = tippMap.status ? RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, tippMap.status) : RDStore.KBC_STATUS_CURRENT

                            RefdataValue tipp_medium = null
                            if (tippMap.medium) {
                                tipp_medium = determineMediumRef(tippMap.medium)
                            }
                            RefdataValue tipp_publicationType = null

                            tipp_publicationType = determinePublicationType(tippMap.publication_type)

                            RefdataValue tipp_accessType = null
                            if (tippMap.access_type && tippMap.access_type.length() > 0) {
                                if (tippMap.access_type == 'P') {
                                    tipp_accessType = RDStore.TIPP_ACCESS_TYPE_PAID
                                } else if (tippMap.access_type == 'F') {
                                    tipp_accessType = RDStore.TIPP_ACCESS_TYPE_FREE
                                }
                            }

                            RefdataValue oaType = tippMap.oa_type ? RefdataCategory.lookup(RCConstants.TIPP_OPEN_ACCESS, tippMap.oa_type) : null

                            Date access_start_date = parseDatebyCreateTipp(tippMap.access_start_date)
                            Date access_end_date = parseDatebyCreateTipp(tippMap.access_end_date)
                            Date date_monograph_published_print = parseDatebyCreateTipp(tippMap.date_monograph_published_print)
                            Date date_monograph_published_online = parseDatebyCreateTipp(tippMap.date_monograph_published_online)
                            Date last_changed = parseDatebyCreateTipp(tippMap.last_changed)

                            TitleInstancePackagePlatform tipp = new TitleInstancePackagePlatform(
                                    uuid: tippMap.uuid ?: UUID.randomUUID().toString(),
                                    status: tipp_status,
                                    name: tippMap.publication_title,
                                    medium: tipp_medium,
                                    publicationType: tipp_publicationType,
                                    url: tippMap.url,
                                    accessType: tipp_accessType,
                                    accessStartDate: access_start_date,
                                    accessEndDate: access_end_date,
                                    dateFirstInPrint: date_monograph_published_print,
                                    dateFirstOnline: date_monograph_published_online,
                                    editionStatement: tippMap.monograph_edition,
                                    firstAuthor: tippMap.first_author,
                                    firstEditor: tippMap.first_editor,
                                    lastChangedExternal: last_changed,
                                    note: tippMap.notes,
                                    openAccess: oaType,
                                    parentPublicationTitleId: tippMap.parent_publication_title_id,
                                    precedingPublicationTitleId: tippMap.preceding_publication_title_id,
                                    publisherName: tippMap.publisher_name,
                                    subjectArea: tippMap.subject_area,
                                    series: tippMap.monograph_parent_collection_title,
                                    supersedingPublicationTitleId: tippMap.superseding_publication_title_id,
                                    volumeNumber: tippMap.monograph_volume,
                                    fromKbartImport: true,
                                    pkg: tippMap.pkg,
                                    hostPlatform: tippMap.pkg.nominalPlatform
                            )
                            if (tipp) {
                                tipp.kbartImportRunning = true
                                if (!tipp.save()) {
                                    log.error("Tipp save error: ")
                                    tipp.errors.allErrors.each {
                                        println it
                                    }
                                }
                            } else {
                                log.error("TIPP creation failed!")
                            }

                            if (!tipp) {
                                log.error("TIPP creation failed!")
                                UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                        description: "Creating a new Title '${tippMap.publication_title}' failed",
                                        tipp: null,
                                        startTime: new Date(),
                                        endTime: new Date(),
                                        status: RDStore.UPDATE_STATUS_FAILED,
                                        type: RDStore.UPDATE_TYPE_NEW_TITLE,
                                        updatePackageInfo: updatePackageInfo
                                ).save()

                            } else {

                                // updatePackageInfo = updatePackageInfo.refresh()
                                UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                        description: "New Title '${tippMap.publication_title}'",
                                        tipp: tipp,
                                        startTime: new Date(),
                                        endTime: new Date(),
                                        status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                        type: RDStore.UPDATE_TYPE_NEW_TITLE,
                                        updatePackageInfo: updatePackageInfo
                                ).save()

                                Map result = [newTipp: true]

                                // KBART -> package_isil -> identifiers['package_isil']
                                if (tippMap.package_isil) {
                                    result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_isil", tippMap.package_isil, 'package_isil', updatePackageInfo)
                                }

                                // KBART -> package_isci -> identifiers['package_isci']
                                if (tippMap.package_isci) {
                                    result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_isci", tippMap.package_isci, 'package_isci', updatePackageInfo)
                                }

                                // KBART -> ill_indicator -> identifiers['ill_indicator']
                                if (tippMap.ill_indicator) {
                                    result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "ill_indicator", tippMap.ill_indicator, 'ill_indicator', updatePackageInfo)
                                }


                                // KBART -> date_first_issue_online, date_last_issue_online,
                                // num_first_vol_online, num_first_issue_online,
                                // num_last_vol_online, num_last_issue_online
                                // embargo_info, coverage_depth
                                if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                                    if (tippMap.date_first_issue_online || tippMap.date_last_issue_online || tippMap.num_first_vol_online ||
                                            tippMap.num_first_issue_online || tippMap.num_last_vol_online || tippMap.num_last_issue_online) {
                                        Map coverageMap = [startDate    : tippMap.date_first_issue_online,
                                                           endDate      : tippMap.date_last_issue_online,
                                                           startVolume  : tippMap.num_first_vol_online,
                                                           startIssue   : tippMap.num_first_issue_online,
                                                           endVolume    : tippMap.num_last_vol_online,
                                                           endIssue     : tippMap.num_last_issue_online,
                                                           embargo      : tippMap.embargo_info,
                                                           coverageDepth: tippMap.coverage_depth]

                                        createOrUpdateCoverageForTipp(tipp, [coverageMap])
                                    }

                                }

                                // KBART -> package_ezb_anchor -> identifiers['package_ezb_anchor']
                                if (tippMap.package_ezb_anchor) {
                                    result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_ezb_anchor", tippMap.package_ezb_anchor, 'package_ezb_anchor', updatePackageInfo)
                                }

                               /* // KBART -> zdb_id  -> identifiers['zdb']
                                if (tippMap.zdb_id) {
                                    result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ZDB, tippMap.zdb_id, 'zdb_id', updatePackageInfo)
                                }

                                // KBART -> ezb_id -> identifiers['ezb']
                                if (tippMap.ezb_id) {
                                    result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EZB, tippMap.ezb_id, 'ezb_id', updatePackageInfo)
                                }*/

                                if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                                    // KBART -> print_identifier-> identifiers
                                    if (tippMap.print_identifier) {
                                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISSN, tippMap.print_identifier, 'print_identifier', updatePackageInfo)
                                    }

                                    // KBART -> online_identifier  -> identifiers
                                    if (tippMap.online_identifier) {
                                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISSN, tippMap.online_identifier, 'online_identifier', updatePackageInfo)
                                    }
                                } else if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_MONO) {
                                    // KBART -> print_identifier-> identifiers
                                    if (tippMap.print_identifier) {
                                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISBN, tippMap.print_identifier, 'print_identifier', updatePackageInfo)
                                    }

                                    // KBART -> online_identifier  -> identifiers
                                    if (tippMap.online_identifier) {
                                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISBN, tippMap.online_identifier, 'online_identifier', updatePackageInfo)
                                    }
                                }

                                // KBART -> title_id  -> identifiers
                                if (tippMap.title_id) {
                                    result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, 'title_id', tippMap.title_id, 'title_id', updatePackageInfo)
                                }

                                // KBART -> doi_identifier  -> identifiers
                                if (tippMap.doi_identifier) {
                                    result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "doi", tippMap.doi_identifier, 'doi_identifier', updatePackageInfo)
                                }

                                // KBART -> ddc -> ddcs
                                if (tippMap.ddc) {
                                    Set ddcs = tippMap.ddc.split(',')

                                    ddcs.each { String ddc ->
                                        RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, ddc)
                                        if (refdataValue && !(refdataValue in tipp.ddcs)) {
                                            tipp.addToDdcs(refdataValue)
                                        }
                                    }
                                }

                                // KBART -> language -> language -> languages
                                if (tippMap.language) {
                                    Set languages = tippMap.language.split(',')
                                    List<Long> langsInTipp = []
                                    languages.each { String lan ->
                                        RefdataValue refdataValue
                                        if (lan.size() == 2) {
                                            refdataValue = RefdataValue.findByOwnerAndValueIlike(RefdataCategory.findByDesc(RCConstants.COMPONENT_LANGUAGE), lan)
                                        } else {
                                            refdataValue = RefdataCategory.lookup(RCConstants.COMPONENT_LANGUAGE, lan)
                                        }
                                        if (refdataValue) {
                                            if (!(refdataValue.id in langsInTipp)) {
                                                ComponentLanguage componentLanguage = new ComponentLanguage(tipp: tipp, language: refdataValue)
                                                componentLanguage.save()
                                                langsInTipp << refdataValue.id
                                            }
                                        }
                                    }

                                    if (!tipp.save()) {
                                        log.error("Tipp save error: ")
                                        tipp.errors.allErrors.each {
                                            println it
                                        }
                                    }
                                }
                                // KBART -> listprice_eur -> prices
                                if (tippMap.listprice_eur) {
                                    result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_EUR, tippMap.listprice_eur, 'listprice_eur', updatePackageInfo)
                                }

                                // KBART -> listprice_usd -> prices
                                if (tippMap.listprice_usd) {
                                    result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_USD, tippMap.listprice_usd, 'listprice_usd', updatePackageInfo)
                                }

                                // KBART -> listprice_gbp -> prices
                                if (tippMap.listprice_gbp) {
                                    result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_GBP, tippMap.listprice_gbp, 'listprice_gbp', updatePackageInfo)
                                }

                                // KBART -> oa_apc_eur -> prices
                                if (tippMap.oa_apc_eur) {
                                    result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_EUR, tippMap.oa_apc_eur, 'oa_apc_eur', updatePackageInfo)
                                }

                                // KBART -> oa_apc_usd -> prices
                                if (tippMap.oa_apc_usd) {
                                    result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_USD, tippMap.oa_apc_usd, 'oa_apc_usd', updatePackageInfo)
                                }

                                // KBART -> oa_apc_gbp -> prices
                                if (tippMap.oa_apc_gbp) {
                                    result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_GBP, tippMap.oa_apc_gbp, 'oa_apc_gbp', updatePackageInfo)
                                }

                                tipp.lastUpdated = new Date()
                                tipp = tipp.save()

                                newtippIdList << tipp.id

                                if(tipp.url)
                                    tippUrls.add(tipp.url)
                            }

                            log.info("createTippBatch (${idx} of $counterNewTippsToProcess) processed at: ${System.currentTimeMillis() - start} msecs")
                        }

                    } catch (Exception e) {
                        log.error("createTippBatch: -> ${tippMap}:" + e.message)
                        //e.printStackTrace()
                    }

                    /* if (idx % 250 == 0) {
                         log.info("Clean up");
                         cleanupService.cleanUpGorm()
                     }*/
                }
                sess.flush()
                sess.clear()
            }
        }

        return newtippIdList
    }

    Date parseDatebyCreateTipp(String value){
        LocalDateTime ldt = null
        Date instant = null

        if (value && value.trim()) {
            String newValue = value
            try {
                if ( newValue.trim() ) {
                    ldt = TextUtils.completeDateString(newValue)
                }
            }
            catch (Exception e) {
                log.error("Parse date fail. Date to parse was -> ${newValue}:" + e.message)
            }

            if (ldt) {
                instant = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
            }
        }
        return instant
    }

    LinkedHashMap findTheCorrectTipp(Map tippMap, tippDuplicates){
        LinkedHashMap result = [tippDuplicates: tippDuplicates]
        TitleInstancePackagePlatform tipp = null
        Package pkg = tippMap.pkg
        Platform plt = tippMap.nominalPlatform


        def trimmed_url = tippMap.title_url ? tippMap.title_url.trim() : null

        //TODO: Moe
        //def curator = pkg.curatoryGroups?.size() > 0 ? (user.adminStatus || user.curatoryGroups?.id.intersect(pkg.curatoryGroups?.id)) : false

        if (pkg && plt) {

            int countTipps = 0

            List<TitleInstancePackagePlatform> tipps = []

            /*if(tippMap.title_id) {

                tipps = tippsMatchingByTitleIDAutoUpdate(tippMap.title_id, pkg)
                countTipps = tipps.size()

                switch (countTipps) {
                    case 0:
                        log.debug("not found Tipp: [pkg: ${pkg}, platform: ${plt}, title_id: ${tippMap.title_id}]")
                        break
                    case 1:
                        log.debug("found tipp by direct match: Tipp = [pkg: ${pkg}, platform: ${plt}, title_id: ${tippMap.title_id}]")
                        tipp = tipps[0]
                        break
                    default:
                        tipps = tipps.sort { it.lastUpdated }
                        *//* tipps.each {
                             println(it.lastUpdated)
                         }*//*
                        tipps.reverse(true)
                        if (tipps.size() > 1) {
                            tipps.eachWithIndex { TitleInstancePackagePlatform titleInstancePackagePlatform, int index ->
                                if (index == 0) {
                                    tipp = titleInstancePackagePlatform
                                } else {
                                    //println("${tippMap.title_id}: "+titleInstancePackagePlatform.id)
                                    result.tippDuplicates.add(titleInstancePackagePlatform.id)
                                }
                            }
                        }
                        break
                }

            }*/

            /*countTipps = TitleInstancePackagePlatform.executeQuery('select count(*) from TitleInstancePackagePlatform as tipp ' +
                    'where tipp.pkg = :pkg and tipp.status != :removed and tipp.name = :tiDtoName ',
                    [pkg: pkg, tiDtoName: title, removed: RDStore.KBC_STATUS_REMOVED])[0]

            if (countTipps > 0) {
                tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp ' +
                        'where tipp.pkg = :pkg and tipp.status != :removed and tipp.name = :tiDtoName ' +
                        ' order by tipp.lastUpdated DESC',
                        [pkg: pkg, tiDtoName: title, removed: RDStore.KBC_STATUS_REMOVED])
            }*/



            if (!tipp) {
                if (countTipps == 0) {
                    if (trimmed_url && trimmed_url.size() > 0) {
                        log.debug("not found Tipp with title. research in pkg ${pkg} with url")
                        countTipps = TitleInstancePackagePlatform.executeQuery('select count(*) from TitleInstancePackagePlatform as tipp ' +
                                'where tipp.url = :url and tipp.status != :removed ' +
                                'and tipp.pkg = :pkg ',
                                [pkg: pkg, url: trimmed_url, removed: RDStore.KBC_STATUS_REMOVED])[0]
                        if (countTipps > 0) {
                            tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp ' +
                                    'where tipp.url = :url and tipp.status != :removed ' +
                                    'and tipp.pkg = :pkg order by tipp.lastUpdated DESC',
                                    [pkg: pkg, url: trimmed_url, removed: RDStore.KBC_STATUS_REMOVED])
                        }
                    }

                    if (countTipps == 0) {
                        log.debug("not found Tipp with title. research in pkg ${pkg} with tile_id")
                        tipps = tippsMatchingByTitleIDAutoUpdate(tippMap.title_id, pkg)
                        countTipps = tipps.size()
                    }

                }
                switch (countTipps) {
                    case 0:
                        log.debug("not found Tipp: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tippMap.publication_title}]")
                        break
                    case 1:
                        if (trimmed_url && trimmed_url.size() > 0) {
                            if (!tipps[0].url || tipps[0].url == trimmed_url) {
                                log.debug("found tipp by url")
                                tipp = tipps[0]
                            } else {

                                //if url changed find tipp over title id
                                List<TitleInstancePackagePlatform> tippsMatchedByTitleID = tippsMatchingByTitleIDAutoUpdate(tippMap.title_id, pkg)

                                tippsMatchedByTitleID.each { TitleInstancePackagePlatform tippByTitleID ->
                                    if (tippByTitleID.id == tipps[0].id) {
                                        log.debug("found tipp by title ID")
                                        tipp = tipps[0]
                                    } else {
                                        log.debug("not found Tipp because url changed: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tippMap.publication_title}, url: ${trimmed_url}]")
                                    }
                                }
                            }
                        } else {
                            log.debug("found tipp by direct match")
                            tipp = tipps[0]
                        }
                        break
                    default:
                        if (trimmed_url && trimmed_url.size() > 0) {
                            tipps = tipps.findAll { !it.url || it.url == trimmed_url }
                            log.debug("found ${tipps.size()} tipps for URL ${trimmed_url}")
                        }

                        if (tipps.size() > 0) {
                            log.warn("found ${tipps.size()} TIPPs with URL ${trimmed_url}!")
                            if (tipps.size() == 1) {
                                tipp = tipps[0]
                            } else {
                                List<TitleInstancePackagePlatform> tippsMatchedByTitleID = tippsMatchingByTitleIDAutoUpdate(tippMap.title_id, pkg)

                                if (tippsMatchedByTitleID.size() > 0) {
                                    List<TitleInstancePackagePlatform> tippsByUrlAndTitleID = tipps.findAll { it.id in tippsMatchedByTitleID.id }.sort { it.lastUpdated }

                                    tippsByUrlAndTitleID.reverse(true)

                                    if (tippsByUrlAndTitleID.size() > 1) {
                                        tippsByUrlAndTitleID.eachWithIndex { TitleInstancePackagePlatform titleInstancePackagePlatform, int index ->
                                            if (index == 0) {
                                                tipp = titleInstancePackagePlatform
                                            } else {
                                                result.tippDuplicates.add(titleInstancePackagePlatform.id)
                                            }
                                        }

                                    } else if (tippsByUrlAndTitleID.size() == 1) {
                                        tipp = tippsByUrlAndTitleID[0]
                                    } else {
                                        log.debug("not found Tipp after tipps and tippsMatchingByTitleID: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tippMap.publication_title}, url: ${trimmed_url}, title_id: ${tippMap.title_id}]")
                                    }
                                }
                            }
                        } else {
                            log.debug("None of the matched TIPPs are found!")
                        }
                        break
                }

            }
        }
        result.tipp = tipp
        result
    }
}
package wekb.kbart

import grails.gorm.transactions.Transactional
import org.hibernate.Session
import wekb.ClassUtils
import wekb.ComponentLanguage
import wekb.Identifier
import wekb.IdentifierNamespace
import wekb.Package
import wekb.Platform
import wekb.RefdataCategory
import wekb.RefdataValue
import wekb.TextUtils
import wekb.TippPrice
import wekb.TitleInstancePackagePlatform
import wekb.UpdatePackageInfo
import wekb.UpdateTippInfo
import wekb.helper.IdentiferNamespaceStore
import wekb.helper.RCConstants
import wekb.helper.RDStore
import java.time.LocalDateTime
import java.time.ZoneId
import java.text.SimpleDateFormat


@Transactional
class KbartImportService {

    LinkedHashMap tippImportForUpdate(TitleInstancePackagePlatform tipp, LinkedHashMap row, UpdatePackageInfo updatePackageInfo, Package pkg, Platform platform) {
        if (!tipp || !pkg || !platform) {
            throw new IllegalArgumentException("Missing TIPP, package or platform for title_id=${row?.title_id}")
        }
        tipp.kbartImportRunning = true
        tipp.fromKbartImport = true
        LinkedHashMap result = [newTipp: false, removedTipp: false, changedTipp: false]
        result = updateTippWithKbart(result, tipp, row, updatePackageInfo)
        if (!result?.tipp) {
            throw new IllegalStateException("TIPP update returned no TIPP for title_id=${row?.title_id}")
        }
        result.tippObject = result.tipp
        return result
    }

    List<TitleInstancePackagePlatform> tippsMatchingByTitleIDAutoUpdateV2(String titleID, Package pkg) {
        String value = titleID?.trim()
        if (!value || !pkg) return []
        IdentifierNamespace namespace = IdentiferNamespaceStore.TITLE_ID_NAMESPACE
        if (!namespace) throw new IllegalStateException("Identifier namespace 'title_id' not found")
        return TitleInstancePackagePlatform.executeQuery('''
            SELECT t FROM TitleInstancePackagePlatform t
            WHERE t.pkg = :pkg AND t.status != :removed
              AND EXISTS (SELECT 1 FROM Identifier i
                          WHERE i.tipp = t AND i.namespace = :namespace AND i.value = :value)
            ORDER BY CASE WHEN t.lastUpdated IS NULL THEN 1 ELSE 0 END ASC,
                     t.lastUpdated DESC, t.id DESC
        ''', [pkg: pkg, removed: RDStore.KBC_STATUS_REMOVED,
              namespace: namespace, value: value])
    }

    LinkedHashMap findTheCorrectTippV2(Map row, Collection<Long> duplicates, Package pkg) {
        String titleID = row?.title_id?.toString()?.trim()
        LinkedHashMap result = [tipp: null, tippDuplicates: duplicates]
        if (!pkg || !titleID) return result
        List<TitleInstancePackagePlatform> matches = tippsMatchingByTitleIDAutoUpdateV2(titleID, pkg)
        if (matches) {
            result.tipp = matches.first()
            matches.drop(1).each { TitleInstancePackagePlatform duplicate ->
                if (!duplicates.contains(duplicate.id)) duplicates.add(duplicate.id)
            }
        }
        return result
    }

    LinkedHashMap findTheCorrectTipp(Map row, Collection<Long> duplicates) {
        return findTheCorrectTippV2(row, duplicates, row?.pkg as Package)
    }

    static RefdataValue determinePublicationType(String type) {
        switch (type?.trim()?.toLowerCase(Locale.ROOT)) {
            case ['serials', 'serial', 'journal']: return RDStore.TIPP_PUBLIC_TYPE_SERIAL
            case ['monographs', 'monograph', 'book']: return RDStore.TIPP_PUBLIC_TYPE_MONO
            case 'database': return RDStore.TIPP_PUBLIC_TYPE_DB
            case 'other': return RDStore.TIPP_PUBLIC_TYPE_OTHER
            default: return RDStore.TIPP_PUBLIC_TYPE_NOSET
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

    boolean createOrUpdateIdentifierForTippOLD(Map result, TitleInstancePackagePlatform tipp, String namespace_val, String identifierValue, String kbartProperty, UpdatePackageInfo updatePackageInfo){
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

    String renderObjectValue(Object value) {
        if (value == null) return ''
        if (value instanceof RefdataValue) return value.value ?: ''
        if (value instanceof Boolean) return value ? 'Yes' : 'No'
        if (value instanceof Date) return new SimpleDateFormat('yyyy-MM-dd').format(value)
        return value.toString()
    }

    Date parseDatebyCreateTipp(String value) {
        if (!value?.trim()) return null
        try {
            LocalDateTime parsed = TextUtils.completeDateString(value.trim())
            if (!parsed) throw new IllegalArgumentException("Invalid date: ${value}")
            return Date.from(parsed.atZone(ZoneId.systemDefault()).toInstant())
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid KBART date '${value}'", e)
        }
    }

    void createUpdateTippInfoByTippChange(TitleInstancePackagePlatform tipp, UpdatePackageInfo updatePackageInfo, String source, String property, String oldValue, String newValue) {
        new UpdateTippInfo(description: "Changes in Title '${tipp.name}'",
                tipp: tipp,
                startTime: new Date(),
                endTime: new Date(),
                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                updatePackageInfo: updatePackageInfo,
                kbartProperty: source,
                tippProperty: property,
                oldValue: oldValue,
                newValue: newValue).save(failOnError: true)
    }

    void createUpdateTippInfoByTippChangeFail(TitleInstancePackagePlatform tipp, UpdatePackageInfo updatePackageInfo, String source, String property, String oldValue, String newValue, String description) {
        new UpdateTippInfo(description: description,
                tipp: tipp,
                startTime: new Date(),
                endTime: new Date(),
                status: RDStore.UPDATE_STATUS_FAILED,
                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                updatePackageInfo: updatePackageInfo,
                kbartProperty: source,
                tippProperty: property,
                oldValue: oldValue,
                newValue: newValue).save(failOnError: true)
    }

    boolean checkAndSetByChangedValue(Map result, TitleInstancePackagePlatform tipp, String type, UpdatePackageInfo update, Map row, String source, String property, boolean acceptNullValue = true, String category = null) {
        if (!row.containsKey(source) && !acceptNullValue) return result.changedTipp == true
        Object raw = row.containsKey(source) ? row[source] : null
        Object oldValue = tipp[property]
        Object newValue = null
        if (raw != null && raw.toString().trim()) {
            String text = raw.toString().trim()
            switch (type) {
                case 'Date': newValue = parseDatebyCreateTipp(text); break
                case 'RefDataValue':
                    if (source == 'medium') newValue = determineMediumRef(text)
                    else if (source == 'publication_type') newValue = determinePublicationType(text)
                    else newValue = RefdataCategory.lookup(category, text)
                    if (!newValue) throw new IllegalArgumentException("Unknown reference value '${text}' for ${source}")
                    break
                case 'String': newValue = (source == 'title_url') ? text : raw.toString(); break
                default: throw new IllegalArgumentException("Unsupported data type ${type}")
            }
        } else if (!acceptNullValue) {
            return result.changedTipp == true
        }
        boolean changed = (type == 'String') ?
                !renderObjectValue(oldValue).equalsIgnoreCase(renderObjectValue(newValue)) :
                oldValue != newValue
        if (changed) {
            if (result.newTipp != true) {
                createUpdateTippInfoByTippChange(tipp, update, source, property,
                        renderObjectValue(oldValue), renderObjectValue(newValue))
            }
            tipp[property] = newValue
        }
        return (result.changedTipp == true) || changed
    }

    boolean createOrUpdateIdentifierForTipp(Map result, TitleInstancePackagePlatform tipp, String namespaceValue, String value, String source, UpdatePackageInfo update) {
        String newValue = value?.trim()
        if (!newValue) throw new IllegalArgumentException("Empty identifier for namespace=${namespaceValue}")

        IdentifierNamespace namespace = IdentifierNamespace.findByValueAndTargetType(namespaceValue, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

        if (!namespace) throw new IllegalStateException("Unknown identifier namespace ${namespaceValue}")

        if (namespace.pattern && !(newValue ==~ java.util.regex.Pattern.compile(namespace.pattern))) {
            throw new IllegalArgumentException("Invalid identifier '${newValue}' for namespace=${namespaceValue}")
        }

        List<Identifier> existing = (tipp.ids ?: []).findAll { it.namespace?.id == namespace.id } as List<Identifier>
        if (existing.size() == 1 && existing.first().value == newValue)
            return result.changedTipp == true

        String oldValue = existing*.value.join('; ')
        existing.each { Identifier identifier ->
            tipp.removeFromIds(identifier)
            identifier.delete(failOnError: true)
        }
        Identifier created = new Identifier(namespace: namespace, value: newValue, tipp: tipp)
        created.save(failOnError: true)
        if (result.newTipp != true) {
            createUpdateTippInfoByTippChange(tipp, update, source, "identifiers[${namespaceValue}]", oldValue, newValue)
        }
        return true
    }

    boolean createOrUpdatePrice(Map result, TitleInstancePackagePlatform tipp, RefdataValue type, RefdataValue currency, String price, String source, UpdatePackageInfo update) {
        if (!type || !currency)
            throw new IllegalArgumentException("Price type or currency missing for ${source}")

        List<TippPrice> existing = TippPrice.findAllByTippAndPriceTypeAndCurrency(tipp, type, currency)
        String raw = price?.trim()
        if (!raw) {
            if (!existing) return result.changedTipp == true
            String oldValue = existing*.price.join('; ')
            existing.each { TippPrice entry ->
                tipp.removeFromPrices(entry)
                entry.delete(failOnError: true)
            }
            if (result.newTipp != true) {
                createUpdateTippInfoByTippChange(tipp, update, source,
                        "prices[${type.value}]", oldValue, '')
            }
            return true
        }

        String normalized = raw.replaceAll('[,.](\\d{3})(?=[,.]|$)', '$1').replace(',', '.')
        BigDecimal amount
        try { amount = new BigDecimal(normalized) }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid price '${raw}' for ${source}", e) }
        if (amount.signum() < 0) throw new IllegalArgumentException("Negative price '${raw}' for ${source}")
        if (existing.size() == 1 && new BigDecimal(existing.first().price.toString()).compareTo(amount) == 0) {
            return result.changedTipp == true
        }
        String oldValue = existing*.price.join('; ')
        existing.each { TippPrice entry ->
            tipp.removeFromPrices(entry)
            entry.delete(failOnError: true)
        }
        new TippPrice(tipp: tipp, priceType: type, currency: currency,
                price: amount).save(failOnError: true)
        if (result.newTipp != true) {
            createUpdateTippInfoByTippChange(tipp, update, source,
                    "prices[${type.value}]", oldValue, amount.toPlainString())
        }
        return true
    }

    LinkedHashMap updateTippWithKbart(LinkedHashMap result, TitleInstancePackagePlatform tipp, Map tippMap, UpdatePackageInfo updatePackageInfo) {
        log.debug("updateTippWithKbart: tipp.id: " + tipp.id)
        List identifierNameSpacesExistOnTipp = []
        result.changedTipp = (result.changedTipp == true)

        // KBART -> status -> status -> status
        if (tipp.status != RDStore.KBC_STATUS_REMOVED && tippMap.status == "Removed") {
            String previousStatus = tipp.status?.value
            ClassUtils.setRefdataIfDifferent(tippMap.status, tipp, 'status', RCConstants.COMPONENT_STATUS, false)
            result.removedTipp = true

            UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                    description: "Removed Title '${tippMap.publication_title}'",
                    tipp: tipp,
                    startTime: new Date(),
                    endTime: new Date(),
                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                    type: RDStore.UPDATE_TYPE_REMOVED_TITLE,
                    kbartProperty: 'status',
                    oldValue: previousStatus,
                    newValue: 'Removed',
                    tippProperty: 'status',
                    updatePackageInfo: updatePackageInfo
            ).save()
        } else {
            boolean changedPreviously1 = (result.changedTipp == true)
            boolean changedNow1 = (checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "status", "status", false, RCConstants.COMPONENT_STATUS) == true)
            result.changedTipp = changedPreviously1 || changedNow1
        }
        log.debug("after save")
        // KBART -> publication_title -> name
        boolean changedPreviously2 = (result.changedTipp == true)
        boolean changedNow2 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "publication_title", "name") == true)
        result.changedTipp = changedPreviously2 || changedNow2
        //log.debug("1")
        // KBART -> first_author -> firstAuthor
        boolean changedPreviously3 = (result.changedTipp == true)
        boolean changedNow3 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "first_author", "firstAuthor") == true)
        result.changedTipp = changedPreviously3 || changedNow3
        //log.debug("2")
        // KBART -> first_editor -> firstEditor
        boolean changedPreviously4 = (result.changedTipp == true)
        boolean changedNow4 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "first_editor", "firstEditor") == true)
        result.changedTipp = changedPreviously4 || changedNow4
        //log.debug("3")
        // KBART -> publisher_name -> publisherName
        boolean changedPreviously5 = (result.changedTipp == true)
        boolean changedNow5 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "publisher_name", "publisherName") == true)
        result.changedTipp = changedPreviously5 || changedNow5
        //log.debug("4")
        // KBART -> publication_type -> publicationType
        if (tippMap.publication_type) {
            boolean changedPreviously6 = (result.changedTipp == true)
            boolean changedNow6 = (checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "publication_type", "publicationType", false, RCConstants.TIPP_PUBLICATION_TYPE) == true)
            result.changedTipp = changedPreviously6 || changedNow6
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
            boolean changedPreviously7 = (result.changedTipp == true)
            boolean changedNow7 = (checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "medium", "medium", false, RCConstants.TIPP_MEDIUM) == true)
            result.changedTipp = changedPreviously7 || changedNow7
        }
        //log.debug("6")
        // KBART -> title_url -> url
        boolean changedPreviously8 = (result.changedTipp == true)
        boolean changedNow8 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "title_url", "url") == true)
        result.changedTipp = changedPreviously8 || changedNow8
        //log.debug("7")
        // KBART -> subject_area -> subjectArea
        boolean changedPreviously9 = (result.changedTipp == true)
        boolean changedNow9 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "subject_area", "subjectArea") == true)
        result.changedTipp = changedPreviously9 || changedNow9
        //log.debug("8")
        // KBART -> access_type -> accessType
        if (tipp.publicationType != RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
            if (tippMap.access_type && tippMap.access_type.length() > 0) {
                //log.debug("8a")
                if (tippMap.access_type == 'P') {
                    tippMap.access_type = 'Paid'
                } else if (tippMap.access_type == 'F') {
                    tippMap.access_type = 'Free'
                }
                boolean changedPreviously10 = (result.changedTipp == true)
                boolean changedNow10 = (checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "access_type", "accessType", true, RCConstants.TIPP_ACCESS_TYPE) == true)
                result.changedTipp = changedPreviously10 || changedNow10
            }
        }else{
            if (tipp.accessType){
                tipp.accessType = null
            }
        }
        //log.debug("9")
        // KBART -> access_start_date -> accessStartDate
        boolean changedPreviously11 = (result.changedTipp == true)
        boolean changedNow11 = (checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "access_start_date", "accessStartDate") == true)
        result.changedTipp = changedPreviously11 || changedNow11
        //log.debug("10")
        // KBART -> access_end_date -> accessEndDate
        boolean changedPreviously12 = (result.changedTipp == true)
        boolean changedNow12 = (checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "access_end_date", "accessEndDate") == true)
        result.changedTipp = changedPreviously12 || changedNow12
        //log.debug("11")
        // KBART -> last_changed -> lastChangedExternal
        boolean changedPreviously13 = (result.changedTipp == true)
        boolean changedNow13 = (checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "last_changed", "lastChangedExternal") == true)
        result.changedTipp = changedPreviously13 || changedNow13
        //log.debug("12")
        // KBART -> notes -> note
        if (tipp.publicationType != RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
        boolean changedPreviously14 = (result.changedTipp == true)
        boolean changedNow14 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "notes", "note") == true)
        result.changedTipp = changedPreviously14 || changedNow14
        }else{
            if (tipp.note){
                tipp.note = null
            }
        }
        //log.debug("13")
        // KBART -> date_monograph_published_print -> dateFirstInPrint
        boolean changedPreviously15 = (result.changedTipp == true)
        boolean changedNow15 = (checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "date_monograph_published_print", "dateFirstInPrint") == true)
        result.changedTipp = changedPreviously15 || changedNow15
        //log.debug("14")
        // KBART -> date_monograph_published_online -> dateFirstOnline
        boolean changedPreviously16 = (result.changedTipp == true)
        boolean changedNow16 = (checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "date_monograph_published_online", "dateFirstOnline") == true)
        result.changedTipp = changedPreviously16 || changedNow16
        //log.debug("15")
        // KBART -> monograph_volume -> volumeNumber
        boolean changedPreviously17 = (result.changedTipp == true)
        boolean changedNow17 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "monograph_volume", "volumeNumber") == true)
        result.changedTipp = changedPreviously17 || changedNow17
        //log.debug("16")
        // KBART -> monograph_edition -> editionStatement
        boolean changedPreviously18 = (result.changedTipp == true)
        boolean changedNow18 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "monograph_edition", "editionStatement") == true)
        result.changedTipp = changedPreviously18 || changedNow18
        //log.debug("17")
        // KBART -> monograph_parent_collection_title -> series
        boolean changedPreviously19 = (result.changedTipp == true)
        boolean changedNow19 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "monograph_parent_collection_title", "series") == true)
        result.changedTipp = changedPreviously19 || changedNow19
        //log.debug("18")
        // KBART -> parent_publication_title_id -> parentPublicationTitleId
        boolean changedPreviously20 = (result.changedTipp == true)
        boolean changedNow20 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "parent_publication_title_id", "parentPublicationTitleId") == true)
        result.changedTipp = changedPreviously20 || changedNow20
        //log.debug("19")
        // KBART -> oa_type -> openAccess
        boolean changedPreviously21 = (result.changedTipp == true)
        boolean changedNow21 = (checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "oa_type", "openAccess", true, RCConstants.TIPP_OPEN_ACCESS) == true)
        result.changedTipp = changedPreviously21 || changedNow21
        //log.debug("before package_isil")
        // KBART -> package_isil -> identifiers['package_isil']
        if (tippMap.package_isil) {
            boolean changedPreviously22 = (result.changedTipp == true)
            boolean changedNow22 = (createOrUpdateIdentifierForTipp(result, tipp, "package_isil", tippMap.package_isil, 'package_isil', updatePackageInfo) == true)
            result.changedTipp = changedPreviously22 || changedNow22
            identifierNameSpacesExistOnTipp << "package_isil"
        }

        // KBART -> package_isci -> identifiers['package_isci']
        if (tippMap.package_isci) {
            boolean changedPreviously23 = (result.changedTipp == true)
            boolean changedNow23 = (createOrUpdateIdentifierForTipp(result, tipp, "package_isci", tippMap.package_isci, 'package_isci', updatePackageInfo) == true)
            result.changedTipp = changedPreviously23 || changedNow23
            identifierNameSpacesExistOnTipp << "package_isci"
        }

        // KBART -> ill_indicator -> identifiers['ill_indicator']
        if (tippMap.ill_indicator) {
            boolean changedPreviously24 = (result.changedTipp == true)
            boolean changedNow24 = (createOrUpdateIdentifierForTipp(result, tipp, "ill_indicator", tippMap.ill_indicator, 'ill_indicator', updatePackageInfo) == true)
            result.changedTipp = changedPreviously24 || changedNow24
            identifierNameSpacesExistOnTipp << "ill_indicator"
        }


        // KBART -> preceding_publication_title_id -> precedingPublicationTitleId
        boolean changedPreviously25 = (result.changedTipp == true)
        boolean changedNow25 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "preceding_publication_title_id", "precedingPublicationTitleId") == true)
        result.changedTipp = changedPreviously25 || changedNow25

        // KBART -> superseding_publication_title_id -> supersedingPublicationTitleId
        boolean changedPreviously26 = (result.changedTipp == true)
        boolean changedNow26 = (checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "superseding_publication_title_id", "supersedingPublicationTitleId") == true)
        result.changedTipp = changedPreviously26 || changedNow26

        //log.debug("before creating identifiers")
        // KBART -> package_ezb_anchor -> identifiers['package_ezb_anchor']
        if (tippMap.package_ezb_anchor) {
            boolean changedPreviously27 = (result.changedTipp == true)
            boolean changedNow27 = (createOrUpdateIdentifierForTipp(result, tipp, "package_ezb_anchor", tippMap.package_ezb_anchor, 'package_ezb_anchor', updatePackageInfo) == true)
            result.changedTipp = changedPreviously27 || changedNow27
            identifierNameSpacesExistOnTipp << "package_ezb_anchor"
        }

        /*    // KBART -> zdb_id  -> identifiers['zdb']
            if (tippMap.zdb_id) {
                boolean changedPreviously28 = (result.changedTipp == true)
                boolean changedNow28 = (createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ZDB, tippMap.zdb_id, 'zdb_id', updatePackageInfo) == true)
                result.changedTipp = changedPreviously28 || changedNow28
                identifierNameSpacesExistOnTipp << IdentifierNamespace.ZDB
            }*/

/*        // KBART -> ezb_id -> identifiers['ezb']
        if (tippMap.ezb_id) {
            boolean changedPreviously29 = (result.changedTipp == true)
            boolean changedNow29 = (createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EZB, tippMap.ezb_id, 'ezb_id', updatePackageInfo) == true)
            result.changedTipp = changedPreviously29 || changedNow29
            identifierNameSpacesExistOnTipp << IdentifierNamespace.EZB
        }*/

        if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
            // KBART -> print_identifier-> identifiers
            if (tippMap.print_identifier) {
                boolean changedPreviously30 = (result.changedTipp == true)
                boolean changedNow30 = (createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISSN, tippMap.print_identifier, 'print_identifier', updatePackageInfo) == true)
                result.changedTipp = changedPreviously30 || changedNow30
                identifierNameSpacesExistOnTipp << IdentifierNamespace.ISSN
            }

            // KBART -> online_identifier  -> identifiers
            if (tippMap.online_identifier) {
                boolean changedPreviously31 = (result.changedTipp == true)
                boolean changedNow31 = (createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISSN, tippMap.online_identifier, 'online_identifier', updatePackageInfo) == true)
                result.changedTipp = changedPreviously31 || changedNow31
                identifierNameSpacesExistOnTipp << IdentifierNamespace.EISSN
            }
        } else if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_MONO) {
            // KBART -> print_identifier-> identifiers
            if (tippMap.print_identifier) {
                boolean changedPreviously32 = (result.changedTipp == true)
                boolean changedNow32 = (createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISBN, tippMap.print_identifier, 'print_identifier', updatePackageInfo) == true)
                result.changedTipp = changedPreviously32 || changedNow32
                identifierNameSpacesExistOnTipp << IdentifierNamespace.ISBN
            }

            // KBART -> online_identifier  -> identifiers
            if (tippMap.online_identifier) {
                boolean changedPreviously33 = (result.changedTipp == true)
                boolean changedNow33 = (createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISBN, tippMap.online_identifier, 'online_identifier', updatePackageInfo) == true)
                result.changedTipp = changedPreviously33 || changedNow33
                identifierNameSpacesExistOnTipp << IdentifierNamespace.EISBN
            }
        }

        // KBART -> title_id  -> identifiers
        if (tippMap.title_id) {
            boolean changedPreviously34 = (result.changedTipp == true)
            boolean changedNow34 = (createOrUpdateIdentifierForTipp(result, tipp, 'title_id', tippMap.title_id, 'title_id', updatePackageInfo) == true)
            result.changedTipp = changedPreviously34 || changedNow34
            identifierNameSpacesExistOnTipp << 'title_id'
        }

        // KBART -> doi_identifier  -> identifiers
        if (tippMap.doi_identifier) {
            boolean changedPreviously35 = (result.changedTipp == true)
            boolean changedNow35 = (createOrUpdateIdentifierForTipp(result, tipp, "doi", tippMap.doi_identifier, 'doi_identifier', updatePackageInfo) == true)
            result.changedTipp = changedPreviously35 || changedNow35
            identifierNameSpacesExistOnTipp << "doi"
        }

        //Cleanup Identifiers
        Set<String> managedNamespaces = ['title_id', 'package_isil', 'package_isci', 'ill_indicator',
                'package_ezb_anchor', IdentifierNamespace.ISSN,
                IdentifierNamespace.EISSN, IdentifierNamespace.ISBN,
                IdentifierNamespace.EISBN, 'doi'] as Set<String>

        List<Long> deleteIdentifiers = (tipp.ids ?: []).findAll { Identifier identifier ->
            identifier.namespace?.value in managedNamespaces &&
                    !(identifier.namespace.value in identifierNameSpacesExistOnTipp)
        }*.id

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
                langIDs.each { Long id ->
                    ComponentLanguage language = ComponentLanguage.get(id)
                    if (language) {
                        tipp.removeFromLanguages(language)
                        language.delete(failOnError: true)
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
        } else {
            if (tipp.languages) {
                def langIDs = tipp.languages*.id.toList()
                langIDs.each { Long languageId ->
                    ComponentLanguage entry = ComponentLanguage.get(languageId)
                    if (entry) {
                        tipp.removeFromLanguages(entry)
                        entry.delete(failOnError: true)
                    }
                }
                tipp.save(failOnError: true)
                result.changedTipp = true
            }
        }

        log.debug("before price section")
        // KBART -> listprice_eur -> prices
        boolean changedPreviously36 = (result.changedTipp == true)
        boolean changedNow36 = (createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_EUR, tippMap.listprice_eur, 'listprice_eur', updatePackageInfo) == true)
        result.changedTipp = changedPreviously36 || changedNow36


        // KBART -> listprice_usd -> prices
        boolean changedPreviously37 = (result.changedTipp == true)
        boolean changedNow37 = (createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_USD, tippMap.listprice_usd, 'listprice_usd', updatePackageInfo) == true)
        result.changedTipp = changedPreviously37 || changedNow37

        // KBART -> listprice_gbp -> prices
        boolean changedPreviously38 = (result.changedTipp == true)
        boolean changedNow38 = (createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_GBP, tippMap.listprice_gbp, 'listprice_gbp', updatePackageInfo) == true)
        result.changedTipp = changedPreviously38 || changedNow38


        // KBART -> oa_apc_eur -> prices
        boolean changedPreviously39 = (result.changedTipp == true)
        boolean changedNow39 = (createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_EUR, tippMap.oa_apc_eur, 'oa_apc_eur', updatePackageInfo) == true)
        result.changedTipp = changedPreviously39 || changedNow39


        // KBART -> oa_apc_usd -> prices
        boolean changedPreviously40 = (result.changedTipp == true)
        boolean changedNow40 = (createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_USD, tippMap.oa_apc_usd, 'oa_apc_usd', updatePackageInfo) == true)
        result.changedTipp = changedPreviously40 || changedNow40


        // KBART -> oa_apc_gbp -> prices
        boolean changedPreviously41 = (result.changedTipp == true)
        boolean changedNow41 = (createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_GBP, tippMap.oa_apc_gbp, 'oa_apc_gbp', updatePackageInfo) == true)
        result.changedTipp = changedPreviously41 || changedNow41


        log.debug("after price section")

        // Propagate persistence errors to the import caller.
        tipp = tipp.save(failOnError: true)

        result.updatePackageInfo = updatePackageInfo
        result.tipp = tipp

        return result
    }


    HashSet<Long> createTippBatch(List<Map> tippMaps, UpdatePackageInfo updatePackageInfo, Long pkgID, Long platformID) {
        log.debug('createTippBatch: Tipps to create -> '+tippMaps.size())
        HashSet<String> title_IDs = new HashSet<String>()
        int counterNewTippsToProcess = tippMaps.size()
        HashSet<Long> newtippIdList = new HashSet<Long>()


        Package pkg = Package.get(pkgID)
        Platform platform = Platform.get(platformID)

        int max = 500
        int idx = 0
        TitleInstancePackagePlatform.withSession { Session sess ->
            for (int offset = 0; offset < tippMaps.size(); offset += max) {

                List<Map> tippMapsToProcess = tippMaps.subList(offset, Math.min(offset + max, tippMaps.size()))
                for (Map tippMap : tippMapsToProcess) {
                    idx++

                    long start = System.currentTimeMillis()
                    try {
                        String coverageTitleID = tippMap.title_id?.toString()?.trim()
                        if(coverageTitleID in title_IDs){
                            log.debug("Existing title_id in batch: ${tippMap.title_id}")
                        } else {

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
                                    url: tippMap.title_url,
                                    accessType: tipp_publicationType != RDStore.TIPP_PUBLIC_TYPE_SERIAL ? tipp_accessType : null,
                                    accessStartDate: access_start_date,
                                    accessEndDate: access_end_date,
                                    dateFirstInPrint: date_monograph_published_print,
                                    dateFirstOnline: date_monograph_published_online,
                                    editionStatement: tippMap.monograph_edition,
                                    firstAuthor: tippMap.first_author,
                                    firstEditor: tippMap.first_editor,
                                    lastChangedExternal: last_changed,
                                    note: tipp_publicationType != RDStore.TIPP_PUBLIC_TYPE_SERIAL ? tippMap.notes : null,
                                    openAccess: oaType,
                                    parentPublicationTitleId: tippMap.parent_publication_title_id,
                                    precedingPublicationTitleId: tippMap.preceding_publication_title_id,
                                    publisherName: tippMap.publisher_name,
                                    subjectArea: tippMap.subject_area,
                                    series: tippMap.monograph_parent_collection_title,
                                    supersedingPublicationTitleId: tippMap.superseding_publication_title_id,
                                    volumeNumber: tippMap.monograph_volume,
                                    fromKbartImport: true,
                                    pkg: pkg,
                                    hostPlatform: platform
                            )
                            tipp.kbartImportRunning = true
                            tipp.save(failOnError: true)

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

                                    tipp.save(failOnError: true)
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
                                tipp = tipp.save(failOnError: true)

                                newtippIdList << tipp.id

                                if(tippMap.title_id)
                                    title_IDs.add(coverageTitleID)
                            }

                            log.info("createTippBatch (${idx} of $counterNewTippsToProcess) processed at: ${System.currentTimeMillis() - start} msecs")
                        }

                    } catch (Exception e) {
                        log.error("createTippBatch failed for title_id=${tippMap?.title_id}", e)
                        throw e // Niemals Teilfehler als erfolgreichen Import melden.
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

}

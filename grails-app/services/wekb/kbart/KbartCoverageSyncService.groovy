package wekb.kbart

import grails.gorm.transactions.Transactional
import wekb.RefdataCategory
import wekb.RefdataValue
import wekb.TIPPCoverageStatement
import wekb.TitleInstancePackagePlatform
import wekb.helper.RCConstants

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Transactional
class KbartCoverageSyncService {

    private static final String DEPTH_CATEGORY = RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH
    private static final String ACCESS_TYPE_CATEGORY = RCConstants.TIPP_ACCESS_TYPE

    private static final Set<String> VALID_DEPTHS = ['fulltext', 'selected articles', 'abstracts'] as Set

    private static final Set<String> VALID_ACCESS_TYPES = ['p', 'f', 'free', 'paid'] as Set

    private static final List<String> IDENTITY_FIELDS = [
            'startDate',
            'startVolume',
            'startIssue',
            'endDate',
            'endVolume',
            'endIssue'
    ]

    private static final List<String> FIELDS = [
            'startDate', 'endDate',
            'startVolume', 'endVolume',
            'startIssue', 'endIssue',
            'embargo', 'coverageNote', 'coverageDepth',
            'accessType'
    ]

    @Transactional
    Map createOrUpdateCoverageForTipp(TitleInstancePackagePlatform tipp, List coverage) {

        if (!tipp?.id) {
            throw new IllegalArgumentException('No Tipp')
        }
        if (coverage == null) {
            throw new IllegalArgumentException('No coverages')
        }

        List<Map> normalized = normalizeAll(coverage)
        if (normalized == null) {
            throw new IllegalStateException('Fail normalized coverage')
        }

        Map<List, Map> uniqueByKey = new LinkedHashMap<>()
        normalized.each { Map data ->
            List key = new ArrayList(identityKeyOf(data))
            if (!uniqueByKey.containsKey(key)) {
                uniqueByKey[key] = data
            }
        }

        TitleInstancePackagePlatform processedTipp = TitleInstancePackagePlatform.lock(tipp.id)
        if (!processedTipp) {
            throw new IllegalStateException("TIPP ${tipp.id} not found")
        }

        List<TIPPCoverageStatement> existing = TIPPCoverageStatement.findAllByTipp(processedTipp)

        Map<List, List<TIPPCoverageStatement>> existingByKey = existing.groupBy { TIPPCoverageStatement item ->
                    new ArrayList(identityKeyOf(item))
        }

        Set<Long> retainedIds = new HashSet<>()
        List<Map> toCreate = []
        int unchanged = 0
        int updated = 0

        uniqueByKey.each { List key, Map data ->
            List<TIPPCoverageStatement> matches = existingByKey[key]
            if (matches) {
                TIPPCoverageStatement match = matches.remove(0)
                retainedIds.add(match.id)

                if (keyOf(match) != keyOf(data)) {
                    copyFields(match, data)
                    match.save(failOnError: true)
                    updated++
                } else {
                    unchanged++
                }
            } else {
                toCreate.add(data)
            }
        }

        List<TIPPCoverageStatement> toDelete = existing.findAll { !retainedIds.contains(it.id) }

        int created = 0
        toCreate.each { Map data ->
            TIPPCoverageStatement statement = new TIPPCoverageStatement(
                    uuid: UUID.randomUUID().toString(),
                    tipp: processedTipp
            )
            copyFields(statement, data)
            statement.save(failOnError: true)
            created++
        }

        int deleted = 0
        toDelete.each { TIPPCoverageStatement statement ->
            statement.delete(flush: false)
            deleted++
        }

        if (created || deleted) {
            processedTipp.kbartImportRunning = true
            processedTipp.lastUpdated = new Date()
            processedTipp.save(failOnError: true)
        }

        TIPPCoverageStatement.withSession { session -> session.flush() }

        Map result = [
                tippId   : processedTipp.id,
                incoming : uniqueByKey.size(),
                unchanged: unchanged,
                created  : created,
                deleted  : deleted,
                updated: updated
        ]
        log.debug("KBART Coverage Sync successful: ${result}")
        return result
    }

    private void copyFields(TIPPCoverageStatement statement, Map data) {
        FIELDS.each { String field ->
            statement."${field}" = data[field]
        }
    }

    //--------------------------------------------------------------

    List<Map> normalizeAll(List rows) {
        if (rows == null) {
            throw new IllegalArgumentException(
                    'Coverage-Rows is null'
            )
        }

        List<Map> result = []
        Set<List> seen = new HashSet<>()

        rows.eachWithIndex { row, int index ->
            if (row == null) {
                throw new IllegalArgumentException(
                        "Coverage-Row ${index + 1} is null"
                )
            }

            Map normalized = normalize(row)
            List key = keyOf(normalized)

            if (seen.add(key)) {
                result << normalized
            }
        }

        return result
    }

    Map normalize(def row) {
        Map result = [
                startDate    : parseDate(field(row, 'startDate', 'date_first_issue_online'), false),
                endDate      : parseDate(field(row, 'endDate', 'date_last_issue_online'), true),

                startVolume  : clean(field(row, 'startVolume', 'num_first_vol_online')),
                endVolume    : clean(field(row, 'endVolume', 'num_last_vol_online')),

                startIssue   : clean(field(row, 'startIssue', 'num_first_issue_online')),
                endIssue     : clean(field(row, 'endIssue', 'num_last_issue_online')),

                embargo      : clean(field(row, 'embargo', 'embargo_info')),

                coverageNote : clean(field(row, 'coverageNote', 'notes')),

                coverageDepth: normalizeDepth(field(row, 'coverageDepth', 'coverage_depth')),
                accessType: normalizeAccessType(field(row, 'accessType', 'access_type'))
        ]

/*        if (result.startDate && result.endDate &&
                result.startDate.after(result.endDate)) {
            throw new IllegalArgumentException(
                    "Coverage-EndDate before start date"
            )
        }*/

        return result
    }

    private Object field(def row, String primary, String alternative) {
        if (row instanceof Map) {
            return row.containsKey(primary)
                    ? row[primary] : row[alternative]
        }

        def meta = row.metaClass

        if (meta.hasProperty(row, primary)) {
            return row."$primary"
        }
        if (meta.hasProperty(row, alternative)) {
            return row."$alternative"
        }

        return null
    }

    private String clean(Object value) {
        if (value == null) return null

        String text = value.toString().trim()
        return text.isEmpty() ? null : text
    }

    private Date parseDate(Object value, boolean end) {
        if (value == null) return null

        if (value instanceof LocalDate) {
            return toDate((LocalDate) value)
        }

        if (value instanceof Date) {
            return new Date(((Date) value).time)
        }

        String text = clean(value)
        if (!text) return null

        try {
            LocalDate date

            if (text ==~ /\d{4}/) {
                int year = Integer.parseInt(text)
                date = LocalDate.of(year, end ? 12 : 1, end ? 31 : 1)

            } else if (text ==~ /\d{4}-\d{2}/) {
                date = LocalDate.parse(text + '-01')

                if (end) {
                    date = date.withDayOfMonth(date.lengthOfMonth())
                }

            } else if (text ==~ /\d{4}-\d{2}-\d{2}/) {
                date = LocalDate.parse(text)

            } else if (text ==~ /\d{2}\.\d{2}\.\d{4}/) {
                date = LocalDate.parse(
                        text,
                        DateTimeFormatter.ofPattern('dd.MM.uuuu')
                )

            } else if (text ==~ /\d{2}\/\d{2}\/\d{4}/) {
                date = LocalDate.parse(
                        text,
                        DateTimeFormatter.ofPattern('dd/MM/uuuu')
                )

            } else {
                throw new IllegalArgumentException(
                        "No Valid KBART-Date: ${text}"
                )
            }

            return toDate(date)

        } catch (java.time.DateTimeException e) {
            throw new IllegalArgumentException("No Valid KBART-Date: ${text}", e)
        }
    }

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    private RefdataValue normalizeDepth(Object value) {
        if (value == null) return null
        if (value instanceof RefdataValue) return value

        String text = clean(value)
        if (!text) return null

        List<String> depths = text.split(';').collect { it.trim().toLowerCase(Locale.ROOT) }

        if (depths.any { !VALID_DEPTHS.contains(it) }) {
            throw new IllegalArgumentException(
                    "Unknow KBART-CoverageDepth: ${text}"
            )
        }

        String canonical = depths.unique().join(';')

        RefdataValue result = RefdataCategory.lookup(DEPTH_CATEGORY, canonical)

        if (!result && canonical == 'fulltext') {
            result = RefdataCategory.lookup(DEPTH_CATEGORY, 'Fulltext')
        }

        if (!result) {
            throw new IllegalArgumentException(
                    "CoverageDepth not in WEKB-Refdata: ${canonical}"
            )
        }

        return result
    }

    private RefdataValue normalizeAccessType(Object value) {
        if (value == null) return null
        if (value instanceof RefdataValue) return value

        String text = clean(value)

        if (text == 'P') {
            text = 'Paid'
        } else if (value == 'F') {
            text = 'Free'
        }

        if (!text) return null

        if (!(text.toLowerCase() in VALID_ACCESS_TYPES)) {
            throw new IllegalArgumentException(
                    "Unknow KBART-AccessType: ${text}"
            )
        }

        RefdataValue result = RefdataCategory.lookup(ACCESS_TYPE_CATEGORY, text)

        if (!result) {
            throw new IllegalArgumentException(
                    "AccessType not in WEKB-Refdata: ${text}"
            )
        }

        return result
    }

    List keyOf(def data) {
        return FIELDS.collect { String name ->
            def value = data."$name"

            if (value instanceof Date) {
                return value.time
            }
            if (value instanceof RefdataValue) {
                return value.id ?: value.value
            }
            return value
        }
    }

    private List identityKeyOf(def data) {
        IDENTITY_FIELDS.collect { String name ->
            def value = data."${name}"
            value instanceof Date ? value.time : value
        }
    }
}

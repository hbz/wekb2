package wekb

import grails.plugins.orm.auditable.Auditable
import wekb.base.AbstractBase
import wekb.helper.RCConstants
import wekb.helper.RDStore

import javax.persistence.Transient
import java.sql.Timestamp
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit

class KbartSource extends AbstractBase implements Auditable {

    String name
    String url

    Boolean automaticUpdates = false
    RefdataValue frequency
    RefdataValue defaultSupplyMethod
    RefdataValue defaultDataFormat
    IdentifierNamespace targetNamespace
    Date lastRun
    //Date lastTimestampInUrl
    String lastUpdateUrl

    Boolean kbartHasWekbFields = false
    Date lastChangedInKbart

    RefdataValue status

    // Timestamps
    Date dateCreated
    Date lastUpdated

    String ftpServerUrl
    String ftpDirectory
    String ftpFileName
    String ftpUsername
    String ftpPassword

    static hasMany = [
            curatoryGroups: CuratoryGroupKbartSource
    ]

    static mapping = {
        id column: 'ks_id'
        version column: 'ks_version'

        uuid column: 'ks_uuid'
        name column: 'ks_name'

        lastUpdated column: 'ks_last_updated'
        dateCreated column: 'ks_date_created'

        automaticUpdates column: 'ks_automatic_updates'
        lastRun column: 'ks_last_run'

        frequency column: 'ks_frequency_rv_fk'
        status column: 'ks_status_rv_fk'
        defaultSupplyMethod column: 'ks_default_supply_method_rv_fk'
        defaultDataFormat column: 'ks_default_data_format_rv_fk'
        targetNamespace column: 'ks_target_namespace_fk'

        url column: 'ks_url'
        lastUpdateUrl column: 'ks_last_update_url'
        kbartHasWekbFields column: 'ks_kbart_wekb_fields'
        lastChangedInKbart column: 'ks_last_changed_in_kbart'
        //lastTimestampInUrl column: 'ks_last_timestamp_in_url '

        ftpServerUrl column: 'ks_ftp_server_url'
        ftpFileName column: 'ks_ftp_file_name'
        ftpDirectory column: 'ks_ftp_directory'
        ftpUsername column: 'ks_ftp_username'
        ftpPassword column: 'ks_ftp_password'
    }

    static constraints = {
        dateCreated (nullable: true, blank: false)
        lastUpdated (nullable: true, blank: false)

        status (nullable: true, blank: false)

        url(nullable: true, blank: true)
        frequency(nullable: true, blank: true)
        defaultSupplyMethod(nullable: true, blank: true)
        defaultDataFormat(nullable: true, blank: true)
        targetNamespace(nullable: true, blank: true)
        lastRun(nullable: true, default: null)
        automaticUpdates(nullable: true, default: false)
        lastUpdateUrl(nullable: true, blank: true)
        //lastTimestampInUrl(nullable: true, blank: true)
        lastChangedInKbart(nullable: true, default: null)
        name(validator: { val, obj ->
            if (obj.hasChanged('name')) {
                if (val && val.trim()) {
                   /* def status_deleted = RDStore.KBC_STATUS_DELETED
                    def dupes = KbartSource.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);

                    if (dupes.size() > 0 && dupes.any { it != obj }) {
                        return ['notUnique']
                    }*/
                } else {
                    return ['notNull']
                }
            }
        })

        ftpServerUrl (nullable: true, blank: true)
        ftpFileName (nullable: true, blank: true)
        ftpUsername (nullable: true, blank: true)
        ftpPassword (nullable: true, blank: true)
        ftpDirectory (nullable: true, blank: true)
    }

    @Override
    def beforeInsert() {
        super.beforeInsertHandler()
    }

    @Override
    def beforeUpdate() {
        super.beforeUpdateHandler()
    }

    @Override
    def beforeDelete() {
        super.beforeDeleteHandler()
    }

    static def refdataFind(params) {
        def result = [];
        def status_filter = null

        if (params.filter1) {
            status_filter = RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, params.filter1)
        }

        params.sort = 'name'

        def ql = null;
        ql = KbartSource.findAllByNameIlikeAndStatusNotInList("%${params.q}%", [RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_REMOVED], params)

        if (ql) {
            ql.each { t ->
                if (!status_filter || t.status == status_filter) {
                    result.add([id: "${t.class.name}:${t.id}", text: "${t.name}", status: "${t.status?.value}"])
                }
            }
        }

        result
    }

    boolean needsUpdate() {
        if (lastRun == null) {
            return true
        }
        if (frequency != null) {
            LocalDate today = LocalDateTime.now().toLocalDate()
            def interval = intervals.get(frequency.value)
            if (interval != null) {
                //println("today: "+today)
                LocalDateTime due = getUpdateDay(interval, false)
                //println("due: "+due)
                if (due && today == due.toLocalDate()) {
                    //println('true')
                    return true
                }else {
                   /* long diffInMillies = Math.abs(due.getTime() - lastRun.getTime())
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
                    int diffToInterval = diff.toInteger()-interval
                    boolean lastUpdateDiff = diffToInterval > 0
                    return lastUpdateDiff*/
                }
            } else {
                log.debug("KbartSource ${this.id} needsUpdate(): Frequency (${frequency}) is not null but intervals is null")
            }
        } else {
            log.debug("KbartSource ${this.id} needsUpdate(): Frequency is null")
        }
        return false
    }


    LocalDateTime getUpdateDay(int interval, boolean setAutoUpdateTime) {
        LocalDateTime today = LocalDateTime.now()
        LocalDateTime nextUpdate
        List updateDates = []
        switch (interval){
            case '1':
                updateDates = getAllDaysInYear(setAutoUpdateTime)
                break
            case '7':
                updateDates = getAllFristDaysOfWeeklyYear(setAutoUpdateTime)
                break
            case '30':
                updateDates = getAllFristDaysOfMonthInYear(setAutoUpdateTime)
                break
            case '91':
                updateDates = getAllFristDaysOfQuarterInYear(setAutoUpdateTime)
                break
            case '365':
                updateDates = getAllFristDaysOfYears(setAutoUpdateTime)
                break
        }

        if(updateDates) {
            updateDates = updateDates.reverse()

            updateDates.each { LocalDateTime updateDate ->
                if (updateDate.isAfter(today)) {
                    nextUpdate = updateDate
                    return
                }

            }
        }

        return nextUpdate
    }

    List getUpdateDays(int interval, boolean setAutoUpdateTime) {

        switch (interval){
            case '1':
                return getAllDaysInYear(setAutoUpdateTime)
                break
            case '7':
                return getAllFristDaysOfWeeklyYear(setAutoUpdateTime)
                break
            case '30':
                return getAllFristDaysOfMonthInYear(setAutoUpdateTime)
                break
            case '91':
                return getAllFristDaysOfQuarterInYear(setAutoUpdateTime)
                break
            case '365':
                return getAllFristDaysOfYears(setAutoUpdateTime)
                break
        }
    }


    def intervals = [
            "Daily"    : 1,
            "Weekly"   : 7,
            "Monthly"  : 30,
            "Quarterly": 91,
            "Yearly"   : 365,
    ]

    public void deleteSoft() {
        setStatus(RDStore.KBC_STATUS_DELETED)
        save()

        Package.findAllByKbartSource(this).each {
            it.kbartSource = null
            it.save()
        }

    }

    @Transient
    LocalDateTime getNextUpdateTimestamp() {
        //20:00:00 is time of Cronjob in AutoUpdatePackagesJob
        if (automaticUpdates && lastRun == null) {
            return LocalDateTime.now()
        }
        if (automaticUpdates && frequency != null) {
            def interval = intervals.get(frequency.value)
            if (interval != null) {
                LocalDateTime due = getUpdateDay(interval, true)
                return due
            } else {
                log.debug("KbartSource ${this.id} getNextUpdateTimestamp(): Frequency (${frequency}) is not null but intervals is null")
            }
        } else {
            log.debug("KbartSource ${this.id} getNextUpdateTimestamp(): Frequency is null")
        }
        return null
    }

    @Transient
    List<LocalDateTime> getAllNextUpdateTimestamp() {
        //20:00:00 is time of Cronjob in AutoUpdatePackagesJob
        if (automaticUpdates && frequency != null) {
            def interval = intervals.get(frequency.value)
            if (interval != null && interval > 1) {
                List<LocalDateTime> dues = getUpdateDays(interval, true)
                /*List<Timestamp> timestampList = []
                due.each {
                    timestampList << it.toTimestamp()
                }*/
                return dues
            }
        }
        return null
    }

    @Transient
    public String getDomainName() {
        return "Source"
    }

    public String getShowName() {
        return this.name
    }

    @Transient
    public List<Package> getPackages() {
        def result = Package.findAllByKbartSource(this)
        result
    }

    String toString(){
        "${name ?: ''}".toString()
    }

    @Transient
    public List<CuratoryGroup> getCuratoryGroupObjects() {
        List<CuratoryGroup> curatoryGroups
        if(this.curatoryGroups.size() > 0){
            curatoryGroups = this.curatoryGroups.curatoryGroup
        }
        return curatoryGroups
    }

    List<LocalDateTime> getAllDaysInYear(boolean setAutoUpdateTime) {
        List<LocalDateTime> updateDays = []
        LocalDateTime todays
        if(setAutoUpdateTime){
            todays = LocalDate.now().atTime(20, 0, 0, 0)
        }else {
            todays = LocalDateTime.now()
        }

        LocalDateTime nextDay = todays.with(TemporalAdjusters.firstDayOfYear())
        LocalDateTime lastDayOfYear = todays.plusYears(2).with(TemporalAdjusters.firstDayOfYear())

        while (nextDay.isBefore(lastDayOfYear)) {
            updateDays << nextDay
            nextDay = nextDay.plusDays(1)
        }
        updateDays
    }

    List<LocalDateTime> getAllFristDaysOfWeeklyYear(boolean setAutoUpdateTime) {
        List<LocalDateTime> updateDays = []

        LocalDateTime todays
        if(setAutoUpdateTime){
            todays = LocalDate.now().atTime(20, 0, 0, 0)
        }else {
            todays = LocalDateTime.now()
        }

        LocalDateTime nextDay = todays.with(TemporalAdjusters.firstDayOfYear())
        LocalDateTime lastDayOfYear = todays.plusYears(2).with(TemporalAdjusters.firstDayOfYear())

        while (nextDay.isBefore(lastDayOfYear)) {
            updateDays << nextDay
            nextDay = nextDay.plusDays(7)
        }
        updateDays
    }

    List<LocalDateTime> getAllFristDaysOfMonthInYear(boolean setAutoUpdateTime) {
        List<LocalDateTime> updateDays = []
        LocalDateTime todays
        if(setAutoUpdateTime){
            todays = LocalDate.now().atTime(20, 0, 0, 0)
        }else {
            todays = LocalDateTime.now()
        }

        LocalDateTime firstDayofMonth = todays.with(TemporalAdjusters.firstDayOfYear())
        LocalDateTime lastDayOfYear = todays.plusYears(2).with(TemporalAdjusters.firstDayOfYear())

        while (firstDayofMonth.isBefore(lastDayOfYear)) {
            firstDayofMonth = firstDayofMonth.with(TemporalAdjusters.firstDayOfMonth()).plusDays(5)
            updateDays << firstDayofMonth
            firstDayofMonth = firstDayofMonth.plusMonths(1)
        }
        updateDays
    }

    List<LocalDateTime> getAllFristDaysOfQuarterInYear(boolean setAutoUpdateTime) {
        List<LocalDateTime> updateDays = []
        LocalDateTime todays
        if(setAutoUpdateTime){
            todays = LocalDate.now().atTime(20, 0, 0, 0)
        }else {
            todays = LocalDateTime.now()
        }

        LocalDateTime firstDayofQuarter = todays.with(TemporalAdjusters.firstDayOfYear())
        LocalDateTime lastDayOfYear = todays.plusYears(2).with(TemporalAdjusters.firstDayOfYear())

        while (firstDayofQuarter.isBefore(lastDayOfYear)) {
            firstDayofQuarter = firstDayofQuarter.with(IsoFields.DAY_OF_QUARTER, 1L)
            updateDays << firstDayofQuarter
            firstDayofQuarter = firstDayofQuarter.plusMonths(3)
        }
        updateDays
    }

    List<LocalDateTime> getAllFristDaysOfYears(boolean setAutoUpdateTime) {
        List<LocalDateTime> updateDays = []
        LocalDateTime todays
        if(setAutoUpdateTime){
            todays = LocalDate.now().atTime(20, 0, 0, 0)
        }else {
            todays = LocalDateTime.now()
        }

        LocalDateTime firstDayofYear= todays.with(TemporalAdjusters.firstDayOfYear())
        LocalDateTime lastDayOf4Year = todays.plusYears(4).with(TemporalAdjusters.firstDayOfYear())

        while (firstDayofYear.isBefore(lastDayOf4Year)) {
            firstDayofYear = firstDayofYear.with(TemporalAdjusters.firstDayOfYear())
            updateDays << firstDayofYear
            firstDayofYear = firstDayofYear.plusYears(1)
        }
        updateDays
    }

}

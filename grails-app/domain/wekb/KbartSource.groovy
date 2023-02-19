package wekb

import grails.plugins.orm.auditable.Auditable
import wekb.base.AbstractBase
import wekb.helper.RCConstants
import wekb.helper.RDStore

import javax.persistence.Transient
import java.sql.Timestamp

class KbartSource extends AbstractBase implements Auditable {

    String name
    String url

    Boolean automaticUpdates = false
    RefdataValue frequency
    RefdataValue defaultSupplyMethod
    RefdataValue defaultDataFormat
    IdentifierNamespace targetNamespace
    Date lastRun
    String lastUpdateUrl

    Boolean kbartHasWekbFields = false
    Date lastChangedInKbart

    RefdataValue status

    // Timestamps
    Date dateCreated
    Date lastUpdated

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
        lastChangedInKbart(nullable: true, default: null)
        name(validator: { val, obj ->
            if (obj.hasChanged('name')) {
                if (val && val.trim()) {
                    def status_deleted = RDStore.KBC_STATUS_DELETED
                    def dupes = KbartSource.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);

                    if (dupes.size() > 0 && dupes.any { it != obj }) {
                        return ['notUnique']
                    }
                } else {
                    return ['notNull']
                }
            }
        })
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
        def status_deleted = RDStore.KBC_STATUS_DELETED
        def status_filter = null

        if (params.filter1) {
            status_filter = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, params.filter1)
        }

        params.sort = 'name'

        def ql = null;
        ql = KbartSource.findAllByNameIlikeAndStatusNotEqual("${params.q}%", status_deleted, params)

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
            Date today = new Date()
            def interval = intervals.get(frequency.value)
            if (interval != null) {
                Date due = getUpdateDay(interval)
                if (today == due) {
                    return true
                }
            } else {
                log.info("KbartSource needsUpdate(): Frequency (${frequency}) is not null but intervals is null")
            }
        } else {
            log.info("KbartSource needsUpdate(): Frequency is null")
        }
        return false
    }


    def getUpdateDay(int interval) {
        Date today = new Date()
        // calculate from each first day of the year to not create a lag over the years
        Calendar cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR))
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 20)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        Date nextUpdate = cal.getTime()
        while (nextUpdate.before(today)) {
            cal.add(Calendar.DATE, interval)
            nextUpdate = cal.getTime()
        }
        return nextUpdate
    }

    List getUpdateDays(int interval) {
        Date today = new Date()
        List<Date> updateDays = []
        // calculate from each first day of the year to not create a lag over the years
        Calendar cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR))
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 20)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        Calendar cal2 = Calendar.getInstance()
        cal2.set(Calendar.YEAR, cal.get(Calendar.YEAR))
        cal2.set(Calendar.MONTH, 11) // 11 = december
        cal2.set(Calendar.DAY_OF_MONTH, 31) // new years eve
        Date nextUpdate = cal.getTime()
        Date lastUpdate = cal2.getTime()
        while (nextUpdate.before(lastUpdate)) {
            cal.add(Calendar.DATE, interval)
            nextUpdate = cal.getTime()
            updateDays << nextUpdate
        }
        return updateDays
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
    Timestamp getNextUpdateTimestamp() {
        //20:00:00 is time of Cronjob in AutoUpdatePackagesJob
        if (automaticUpdates && lastRun == null) {
            return new Date().toTimestamp()
        }
        if (automaticUpdates && frequency != null) {
            def interval = intervals.get(frequency.value)
            if (interval != null) {
                Date due = getUpdateDay(interval)
                return due.toTimestamp()
            } else {
                log.info("KbartSource needsUpdate(): Frequency (${frequency}) is not null but intervals is null")
            }
        } else {
            log.info("KbartSource needsUpdate(): Frequency is null")
        }
        return null
    }

    @Transient
    List<Timestamp> getAllNextUpdateTimestamp() {
        //20:00:00 is time of Cronjob in AutoUpdatePackagesJob
        if (automaticUpdates && frequency != null) {
            def interval = intervals.get(frequency.value)
            if (interval != null && interval > 1) {
                List<Date> due = getUpdateDays(interval)
                List<Timestamp> timestampList = []
                due.each {
                    timestampList << it.toTimestamp()
                }
                return timestampList
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

    def expunge(){
        log.debug("Component expunge")
        def result = [deleteType: this.class.name, deleteId: this.id]
        this.delete(failOnError: true)
        result
    }

    @Transient
    public List<CuratoryGroup> getCuratoryGroupObjects() {
        List<CuratoryGroup> curatoryGroups
        if(this.curatoryGroups.size() > 0){
            curatoryGroups = this.curatoryGroups.curatoryGroup
        }
        return curatoryGroups
    }

}

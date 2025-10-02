package wekb

import wekb.annotations.RefdataAnnotation
import wekb.helper.RCConstants
import wekb.helper.RDStore

import javax.persistence.Transient

class UpdatePackageInfo {

    String uuid

    String description

    Date startTime
    Date endTime

    Date dateCreated
    Date lastUpdated

    int countKbartRows = 0
    int countProcessedKbartRows = 0
    int countInValidTipps = 0
    int countChangedTipps = 0
    int countRemovedTipps = 0
    int countNewTipps = 0
    int countNowTippsInWekb = 0
    int countPreviouslyTippsInWekb = 0

    boolean onlyRowsWithLastChanged = false

    boolean kbartHasWekbFields = false

    boolean automaticUpdate = false

    boolean updateFromFTP = false
    boolean updateFromURL = false
    boolean updateFromFileUpload = false

    Date lastChangedInKbart

    String updateUrl
    String lastUpdateUrl
    Date lastRun
    @RefdataAnnotation(cat = RCConstants.SOURCE_FREQUENCY)
    RefdataValue frequency

    @RefdataAnnotation(cat = RCConstants.UPDATE_STATUS)
    RefdataValue status

    static hasMany = [updateTippInfos: UpdateTippInfo]

    static belongsTo = [pkg: Package]

    static mapping = {
        id          column:'upi_id'
        version     column:'upi_version'

        pkg         column: 'upi_pkg_fk', index: 'upi_pkg_idx'

        startTime         column: 'upi_start_time', index: 'upi_start_time_idx'
        endTime         column: 'upi_end_time', index: 'upi_start_time_idx'

        uuid column: 'upi_uuid', index: 'upi_uuid_idx'
        description column: 'upi_description', type: 'text'


        dateCreated column: 'upi_date_created'
        lastUpdated column: 'upi_last_updated'

        status column: 'upi_status_fk', index: 'upi_status_idx'

        countKbartRows column: 'upi_count_kbart_rows'
        countProcessedKbartRows column: 'upi_count_processed_kbart_rows'
        countInValidTipps column: 'upi_count_invalid_tipps'
        countChangedTipps column: 'upi_count_changed_tipps'
        countRemovedTipps column: 'upi_count_removed_tipps'
        countNewTipps column: 'upi_count_new_tipps'
        countNowTippsInWekb column: 'upi_count_now_tipps'
        countPreviouslyTippsInWekb column: 'upi_count_previously_tipps'

        onlyRowsWithLastChanged column: 'upi_only_rows_with_last_changed'

        kbartHasWekbFields column: 'upi_kbart_has_wekb_fields'

        lastChangedInKbart column: 'upi_last_changed_in_kbart'

        automaticUpdate column: 'upi_automatic_update'

        updateUrl column: 'upi_update_url'

        updateFromFTP column: 'upi_update_from_ftp'
        updateFromURL column: 'upi_update_from_url'
        updateFromFileUpload column: 'upi_update_from_file_upload'

        lastUpdateUrl column: 'upi_last_udpate_url'
        lastRun column: 'upi_last_run'
        frequency column: 'upi_frequency'
    }

    static constraints = {
        endTime     (nullable:true)

        //onlyRowsWithLastChanged  (nullable:true)
        lastChangedInKbart (nullable:true, default: null)
        updateUrl (nullable:true)
        lastUpdateUrl (nullable:true)
        lastRun (nullable:true)
        frequency (nullable:true)
    }

    def beforeValidate (){
        log.debug("beforeValidate for ${this}")
        generateUuid()
    }

    protected def generateUuid(){
        if (!uuid){
            uuid = UUID.randomUUID().toString()
        }
    }

    @Transient
    public String getDomainName() {
        return "Package Update Infos"
    }

    public String getShowName() {
        return this.pkg.name
    }

    @Transient
    public int getCountUpdateTippInfos() {
        int result = UpdateTippInfo.executeQuery("select count(*) from UpdateTippInfo where updatePackageInfo = :updatePackageInfo", [updatePackageInfo: this])[0]
        result
    }

    @Transient
    public int getCountChangedTitles() {
        int result = UpdateTippInfo.executeQuery("select count(*) from UpdateTippInfo where updatePackageInfo = :updatePackageInfo and type = :type group by tipp", [updatePackageInfo: this, type: RDStore.UPDATE_TYPE_CHANGED_TITLE]).size()
        result
    }

    @Transient
    public String getCountInfosAboutChangedTitles() {
        getCountChangedTitles()+" / " + countChangedTipps
    }

    String getOID(){
        "${this.class.name}:${id}"
    }


}

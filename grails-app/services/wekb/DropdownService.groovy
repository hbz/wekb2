package wekb

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import wekb.helper.RCConstants
import wekb.helper.RDStore

@Transactional
class DropdownService {

    GrailsApplication grailsApplication

    def selectedDropDown(String dropDownType, def object, def status = null, String q = null){
        if(object instanceof Platform && object.getHostedPackages()){
            selectedDropDown(dropDownType, object.getHostedPackages(), status)
        }
        else if (object instanceof Package){
            selectedDropDown(dropDownType, [object], status)
        }
        else if (object instanceof KbartSource && object.getPackages()){
            selectedDropDown(dropDownType, object.getPackages(), status)
        }else if (object instanceof Org && object.getProvidedPackages()){
            selectedDropDown(dropDownType, object.getProvidedPackages(), status)
        }else {
            []
        }
    }

    def selectedDropDown(String dropDownType, List<Package> pkgs = null, def status = null, String q = null){
        List listStatus = status ? [RefdataValue.get(Long.parseLong(status))] : [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_EXPECTED, RDStore.KBC_STATUS_RETIRED]

        switch (dropDownType) {
            case 'series':
                getAllPossibleSeries(pkgs, listStatus, q)
                break
            case 'subjectArea':
                getAllPossibleSubjectAreas(pkgs, listStatus, q)
                break
            case 'dateFirstOnlineYear':
                getAllPossibleDateFirstOnlineYear(pkgs, listStatus, q)
                break
            case 'ddc':
                getAllPossibleDdcs(pkgs, listStatus, q)
                break
            case 'ddcForPackages':
                getAllPossibleDdcsForPackages(q)
                break
            case 'language':
                getAllPossibleLanguages(pkgs, listStatus, q)
                break
            case 'publisher':
                getAllPossiblePublisher(pkgs, listStatus, q)
                break
            case 'accessEndDate':
                getAllPossibleAccessEndDateYear(pkgs, listStatus, q)
                break
            case 'accessStartDate':
                getAllPossibleAccessStartDateYear(pkgs, listStatus, q)
                break
            default:
                []
                break
        }
    }

    def componentsDropDown(String baseClass, String filter = null, String q = null){
        def domain_class = grailsApplication.getArtefact('Domain', baseClass)
        Set values = []
        if (domain_class) {
            def baseclass = domain_class.getClazz()
            String query, queryString = ""
            Map queryMap
            if(baseClass == RefdataValue.class.name){
                String order
                if(filter == RCConstants.DDC)
                    order = "rv.value"
                else
                    order = "rv.order, rv.value_en"
                queryMap = [desc: filter]
                if(q) {
                    queryString = "and rv.value_en like :queryStringFilter"
                    queryMap.queryStringFilter = "${q}%"
                }
                query = "select rv from ${baseClass} as rv where rv.owner.desc = :desc ${queryString} order by ${order}"
                RefdataValue.executeQuery(query, queryMap).each { t ->
                    if(filter == RCConstants.DDC)
                        values.add([id:"${RefdataValue.class.name}:${t.id}", text:"${t.value}"])
                    else
                        values.add([id:"${RefdataValue.class.name}:${t.id}", text:"${t.getI10n('value')}"])
                }

            }else {
                queryMap = [status: [RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_REMOVED]]
                if(q) {
                    queryString = "and o.name like :queryStringFilter"
                    queryMap.queryStringFilter = "${q}%"
                }
                query = "select o.id, o.name from ${baseClass} as o where o.status not in :status ${queryString} order by o.name"
                baseclass.executeQuery(query, queryMap).each { t ->
                    values.add([id:"${baseClass}:${t[0]}", text:"${t[1]}"])
                }
            }

        } else {
            log.error("selectedDropDown: Unable to locate domain class ${baseClass} or not readable");
            values = []
        }

        return values
    }


    Set<Map> getAllPossibleCoverageDepths(List<Package> pkgs, List listStatus) {
        Set values = []
        Set<RefdataValue> coverageDepths = []

        coverageDepths.addAll(RefdataValue.executeQuery("select rdv from RefdataValue rdv where rdv.value in (select tc.coverageDepth from TIPPCoverage tc join tc.tipp tipp where tc.coverageDepth is not null and tipp.pkg in (:pkgs) and tipp.status in (:status)) ", [pkgs: pkgs, status: listStatus]))

        coverageDepths.each { t ->
            values.add([id: "${RefdataValue.class.name}:${t.id}", text: "${t.getI10n('value')}"])
        }

        values
    }

    Set<Map> getAllPossibleSeries(List<Package> pkgs = null, List listStatus, String q) {
        Set values = []
        String baseQuery, filter = ""
        Map<String, Object> queryParams = [status: listStatus]
        if(q) {
            filter = "and series like :filter"
            queryParams.filter = "${q}%"
        }
        if(pkgs) {
            baseQuery = "select series from TitleInstancePackagePlatform where series is not null and pkg in (:pkgs) and status in (:status) ${filter} order by series"
            queryParams.pkgs = pkgs
        }else {
            baseQuery = "select series from TitleInstancePackagePlatform where series is not null and status in (:status) ${filter} order by series"
        }
        Set<String> series = TitleInstancePackagePlatform.executeQuery(baseQuery, queryParams)

        if(series.size() == 0){
            values << [id: "null", text: "No series found!"]
        }

        series.each { t ->
            values.add([id: "${t}", text: "${t}"])
        }

        values
    }

    Set<Map> getAllPossibleDdcs(List<Package> pkgs = null, List listStatus, String q) {
        Set values = []
        String baseQuery, filter = ""
        Map<String, Object> queryParams = [status: listStatus]
        if(q) {
            filter = "and series like :filter"
            queryParams.filter = "${q}%"
        }
        if(pkgs) {
            baseQuery = "select ddc from TitleInstancePackagePlatform tipp join tipp.ddcs ddc join tipp.pkg pkg where pkg in (:pkgs) and tipp.status in (:status) ${filter} order by ddc.value_en"
            queryParams.pkgs = pkgs
        }else {
            baseQuery = "select ddc from TitleInstancePackagePlatform tipp join tipp.ddcs ddc join tipp.pkg pkg where tipp.status in (:status) ${filter} order by ddc.value_en"
        }
        Set<RefdataValue> ddcs = TitleInstancePackagePlatform.executeQuery(baseQuery, queryParams)
        ddcs.each { t ->
            values.add([id: "${RefdataValue.class.name}:${t.id}", text: "${t.value_en}"])
        }

        values
    }

    Set<Map> getAllPossibleDdcsForPackages(String q) {
        Set values = []
        String baseQuery = "select ddc from Package pkg join pkg.ddcs ddc order by ddc.value_en"
        Map<String, Object> queryParams = null
        if(q) {
            baseQuery = "select ddc from Package pkg join pkg.ddcs ddc and series like :filter order by ddc.value_en"
            queryParams = [filter: "${q}%"]
        }
        Set<RefdataValue> ddcs
        if(queryParams) {
            ddcs = Package.executeQuery(baseQuery, queryParams)
        }
        else
            ddcs = Package.executeQuery(baseQuery)
        ddcs.each { t ->
            values.add([id: "${RefdataValue.class.name}:${t.id}", text: "${t.value_en}"])
        }

        values
    }

    Set<Map> getAllPossibleLanguages(List<Package> pkgs = null, List listStatus, String q) {
        Set values = []
        String baseQuery, filter = ""
        Map<String, Object> queryParams = [status: listStatus]
        if(q) {
            filter = "lang.language.value_en like :filter"
            queryParams.filter = "${q}%"
        }
        if(pkgs) {
            baseQuery = "select lang.language from Language lang join lang.tipp tipp join tipp.pkg pkg where pkg in (:pkgs) and tipp.status in (:status) ${filter} order by lang.language.value_en"
            queryParams.pkgs = pkgs
        }else {
            baseQuery = "select lang.language from Language lang join lang.tipp tipp join tipp.pkg pkg where tipp.status in (:status) ${filter} order by lang.language.value_en"
        }
        Set<RefdataValue> languages = TitleInstancePackagePlatform.executeQuery(baseQuery, queryParams)
        languages.each { t ->
            values.add([id: "${RefdataValue.class.name}:${t.id}", text: "${t.getI10n('value')}"])
        }

        values
    }


    Set<Map> getAllPossibleSubjectAreas(List<Package> pkgs = null, List listStatus, String q) {
        Set values = []

        String baseQuery, filter = ""
        Map<String, Object> queryParams = [status: listStatus]
        if(q) {
            filter = "and subjectArea like :filter"
            queryParams.filter = "${q}%"
        }
        if(pkgs) {
            baseQuery = "select subjectArea from TitleInstancePackagePlatform where subjectArea is not null and pkg in (:pkgs) and status in (:status) ${filter} order by subjectArea"
            queryParams.pkgs = pkgs
        }else {
            baseQuery = "select subjectArea from TitleInstancePackagePlatform where subjectArea is not null and status in (:status) ${filter} order by subjectArea"
        }
        Set<String> rawSubjects = TitleInstancePackagePlatform.executeQuery(baseQuery, queryParams)
        if(rawSubjects.size() == 0){
            values <<   [id: "null", text: "No subject area found!"]
        }

        rawSubjects.each { String t ->
            values.add([id: "${t}", text: "${t}"])
        }

        values
    }


    Set<Map> getAllPossibleDateFirstOnlineYear(List<Package> pkgs = null, List listStatus, String q) {
        Set values = []
        String baseQuery, filter = ""
        Map<String, Object> queryParams = [status: listStatus]
        if(q) {
            filter = "and to_char(dateFirstOnline, 'yyyy') like :filter"
            queryParams.filter = "${q}%"
        }
        if(pkgs) {
            baseQuery = "select Year(dateFirstOnline) from TitleInstancePackagePlatform where dateFirstOnline is not null and pkg in (:pkgs) and status in (:status) ${filter} order by YEAR(dateFirstOnline)"
            queryParams.pkgs = pkgs
        }else {
            baseQuery = "select Year(dateFirstOnline) from TitleInstancePackagePlatform where dateFirstOnline is not null and status in (:status) ${filter} order by YEAR(dateFirstOnline)"
        }
        Set<String> dateFirstOnlines = TitleInstancePackagePlatform.executeQuery(baseQuery, queryParams)
        if(dateFirstOnlines.size() == 0){
            dateFirstOnlines << "No date first online found!"
        }
        dateFirstOnlines.each { t ->
            values.add([id: "${t}", text: "${t}"])
        }

        values
    }

    Set<Map> getAllPossibleAccessStartDateYear(List<Package> pkgs = null, List listStatus, String q) {
        Set values = []
        String baseQuery, filter = ''
        Map<String, Object> queryParams = [status: listStatus]
        if(q) {
            filter = "and to_char(accessStartDate,'yyyy') like :filter"
            queryParams.filter = "${q}%"
        }
        if(pkgs) {
            baseQuery = "select Year(accessStartDate) from TitleInstancePackagePlatform where accessStartDate is not null and pkg in (:pkgs) and status in (:status) "+filter+" order by YEAR(accessStartDate)"
            queryParams.pkgs = pkgs
        }else {
            baseQuery = "select Year(accessStartDate) from TitleInstancePackagePlatform where accessStartDate is not null and status in (:status) "+filter+" order by YEAR(accessStartDate)"
        }
        Set<String> dates = TitleInstancePackagePlatform.executeQuery(baseQuery, queryParams)
        if(dates.size() == 0){
            dates << "No access start date found!"
        }

        dates.each { t ->
            values.add([id: "${t}", text: "${t}"])
        }
        values
    }

    Set<Map> getAllPossibleAccessEndDateYear(List<Package> pkgs = null, List listStatus, String q) {
        Set values = []
        String baseQuery, filter = ''
        Map<String, Object> queryParams = [status: listStatus]
        if(q) {
            filter = "and to_char(accessEndDate,'yyyy') like :filter"
            queryParams.filter = "${q}%"
        }
        if(pkgs) {
            baseQuery = "select Year(accessEndDate) from TitleInstancePackagePlatform where accessEndDate is not null and pkg in (:pkgs) and status in (:status) ${filter} order by YEAR(accessEndDate)"
            queryParams.pkgs = pkgs
        }else {
            baseQuery = "select distinct(Year(accessEndDate)) from TitleInstancePackagePlatform where accessEndDate is not null and status in (:status) ${filter} order by YEAR(accessEndDate)"
        }
        Set<String> dates = TitleInstancePackagePlatform.executeQuery(baseQuery, queryParams)
        if(dates.size() == 0){
            dates << "No access end date found!"
        }

        dates.each { t ->
            values.add([id: "${t}", text: "${t}"])
        }

        values
    }


    Set<Map> getAllPossiblePublisher(List<Package> pkgs = null, List listStatus, String q) {
        Set values = []
        String baseQuery, filter = ""
        Map<String, Object> queryParams = [status: listStatus]
        if(q) {
            filter = "and publisherName like :filter"
            queryParams.filter = "${q}%"
        }
        if(pkgs) {
            baseQuery = "select publisherName from TitleInstancePackagePlatform where publisherName is not null and pkg in (:pkgs) and status in (:status) ${filter} order by publisherName"
            queryParams.pkgs = pkgs
        }else{
            baseQuery = "select publisherName from TitleInstancePackagePlatform where publisherName is not null and status in (:status) ${filter} order by publisherName"
        }
        Set<String> publishers = TitleInstancePackagePlatform.executeQuery(baseQuery, queryParams)
        publishers.each { t ->
            values.add([id: "${t}", text: "${t}"])
        }

        values
    }
}

package wekb

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import wekb.helper.RDStore

@Transactional
class DropdownService {

    GrailsApplication grailsApplication

    def selectedDropDown(String dropDownType, def object, def status = null){
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

    def selectedDropDown(String dropDownType, List<Package> pkgs = null, def status = null){
        List listStatus = status ? [RefdataValue.get(Long.parseLong(status))] : [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_EXPECTED, RDStore.KBC_STATUS_RETIRED]

        switch (dropDownType) {
            case 'series':
                getAllPossibleSeries(pkgs, listStatus)
                break
            case 'subjectArea':
                getAllPossibleSubjectAreas(pkgs, listStatus)
                break
            case 'dateFirstOnlineYear':
                getAllPossibleDateFirstOnlineYear(pkgs, listStatus)
                break
            case 'ddc':
                getAllPossibleDdcs(pkgs, listStatus)
                break
            case 'language':
                getAllPossibleLanguages(pkgs, listStatus)
                break
            case 'publisher':
                getAllPossiblePublisher(pkgs, listStatus)
                break
            case 'accessEndDate':
                getAllPossibleAccessEndDateYear(pkgs, listStatus)
                break
            case 'accessStartDate':
                getAllPossibleAccessStartDateYear(pkgs, listStatus)
                break
            default:
                []
                break
        }
    }

    def componentsDropDown(String baseClass, String filter = null){
        def domain_class = grailsApplication.getArtefact('Domain', baseClass)
        List values = []
        if (domain_class) {
            def baseclass = domain_class.getClazz()
            String query
            Map queryMap
            if(baseClass == RefdataValue.class.name){
                query = "select rv from ${baseClass} as rv where rv.owner.desc = :desc order by rv.order, rv.value, rv.description"
                queryMap = [desc: filter]
                RefdataValue.executeQuery(query, queryMap).each { t ->
                    values.add([id:"${RefdataValue.class.name}:${t.id}", text:"${t.getI10n('value')}"])
                }

            }else {
                query = "select o.id, o.name from ${baseClass} as o where o.status not in :status order by o.name"
                queryMap = [status: [RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_REMOVED]]

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


    Set<RefdataValue> getAllPossibleCoverageDepths(List<Package> pkgs, List listStatus) {

        Set<RefdataValue> coverageDepths = []

        coverageDepths.addAll(RefdataValue.executeQuery("select rdv from RefdataValue rdv where rdv.value in (select tc.coverageDepth from TIPPCoverage tc join tc.tipp tipp where tc.coverageDepth is not null and tipp.pkg in (:pkgs) and tipp.status in (:status)) ", [pkgs: pkgs, status: listStatus]))

        coverageDepths
    }

    Set<String> getAllPossibleSeries(List<Package> pkgs = null, List listStatus) {
        Set<String> series = []

        if(pkgs) {
            series = TitleInstancePackagePlatform.executeQuery("select distinct(series) from TitleInstancePackagePlatform where series is not null and pkg in (:pkgs) and status in (:status) order by series", [pkgs: pkgs, status: listStatus])
        }else {
            series = TitleInstancePackagePlatform.executeQuery("select distinct(series) from TitleInstancePackagePlatform where series is not null and status in (:status) order by series", [status: listStatus])
        }

        if(series.size() == 0){
            series << "No series found!"
        }
        series
    }

    Set<RefdataValue> getAllPossibleDdcs(List<Package> pkgs = null, List listStatus) {

        Set<RefdataValue> ddcs = []

        if(pkgs) {
            ddcs.addAll(TitleInstancePackagePlatform.executeQuery("select ddc.ddc from DeweyDecimalClassification ddc join ddc.tipp tipp join tipp.pkg pkg where pkg in (:pkgs) and tipp.status in (:status) order by ddc.ddc.value_en", [pkgs: pkgs, status: listStatus]))
        }else {
            ddcs.addAll(TitleInstancePackagePlatform.executeQuery("select ddc.ddc from DeweyDecimalClassification ddc join ddc.tipp tipp join tipp.pkg pkg where tipp.status in (:status) order by ddc.ddc.value_en", [status: listStatus]))
        }

        ddcs
    }

    Set<RefdataValue> getAllPossibleLanguages(List<Package> pkgs = null, List listStatus) {

        Set<RefdataValue> languages = []

        if(pkgs) {
            languages.addAll(TitleInstancePackagePlatform.executeQuery("select lang.language from Language lang join lang.tipp tipp join tipp.pkg pkg where pkg in (:pkgs) and tipp.status in (:status) order by lang.language.value_en", [pkgs: pkgs, status: listStatus]))
        }else {
            languages.addAll(TitleInstancePackagePlatform.executeQuery("select lang.language from Language lang join lang.tipp tipp join tipp.pkg pkg where tipp.status in (:status) order by lang.language.value_en", [status: listStatus]))
        }

        languages
    }


    Set<String> getAllPossibleSubjectAreas(List<Package> pkgs = null, List listStatus) {

        SortedSet<String> subjects = new TreeSet<String>()
        List<String> rawSubjects
        if(pkgs) {
            rawSubjects = TitleInstancePackagePlatform.executeQuery("select distinct(subjectArea) from TitleInstancePackagePlatform where subjectArea is not null and pkg in (:pkgs) and status in (:status) order by subjectArea", [pkgs: pkgs, status: listStatus])
        }else {
            rawSubjects = TitleInstancePackagePlatform.executeQuery("select distinct(subjectArea) from TitleInstancePackagePlatform where subjectArea is not null and status in (:status) order by subjectArea", [status: listStatus])
        }

        if(rawSubjects.size() == 0){
            subjects << "No subject area found!"
        }
        else {
            rawSubjects.each { String rawSubject ->
                rawSubject.tokenize(',;|').each { String rs ->
                    subjects.add(rs.trim())
                }
            }
        }

        subjects
    }


    Set<String> getAllPossibleDateFirstOnlineYear(List<Package> pkgs = null, List listStatus) {

        Set<String> dateFirstOnlines = []
        if(pkgs) {
            dateFirstOnlines = TitleInstancePackagePlatform.executeQuery("select distinct(Year(dateFirstOnline)) from TitleInstancePackagePlatform where dateFirstOnline is not null and pkg in (:pkgs) and status in (:status) order by YEAR(dateFirstOnline)", [pkgs: pkgs, status: listStatus])
        }else {
            dateFirstOnlines = TitleInstancePackagePlatform.executeQuery("select distinct(Year(dateFirstOnline)) from TitleInstancePackagePlatform where dateFirstOnline is not null and status in (:status) order by YEAR(dateFirstOnline)", [status: listStatus])
        }
        if(dateFirstOnlines.size() == 0){
            dateFirstOnlines << "No date first online found!"
        }

        dateFirstOnlines
    }

    Set<String> getAllPossibleAccessStartDateYear(List<Package> pkgs = null, List listStatus) {

        Set<String> dates = []
        if(pkgs) {
            dates = TitleInstancePackagePlatform.executeQuery("select distinct(Year(accessStartDate)) from TitleInstancePackagePlatform where accessStartDate is not null and pkg in (:pkgs) and status in (:status) order by YEAR(accessStartDate)", [pkgs: pkgs, status: listStatus])
        }else {
            dates = TitleInstancePackagePlatform.executeQuery("select distinct(Year(accessStartDate)) from TitleInstancePackagePlatform where accessStartDate is not null and status in (:status) order by YEAR(accessStartDate)", [status: listStatus])
        }
        if(dates.size() == 0){
            dates << "No access start date found!"
        }

        dates
    }

    Set<String> getAllPossibleAccessEndDateYear(List<Package> pkgs = null, List listStatus) {

        Set<String> dates = []
        if(pkgs) {
            dates = TitleInstancePackagePlatform.executeQuery("select distinct(Year(accessEndDate)) from TitleInstancePackagePlatform where accessEndDate is not null and pkg in (:pkgs) and status in (:status) order by YEAR(accessEndDate)", [pkgs: pkgs, status: listStatus])
        }else {
            dates = TitleInstancePackagePlatform.executeQuery("select distinct(Year(accessEndDate)) from TitleInstancePackagePlatform where accessEndDate is not null and status in (:status) order by YEAR(accessEndDate)", [status: listStatus])
        }
        if(dates.size() == 0){
            dates << "No access end date found!"
        }

        dates
    }


    Set<String> getAllPossiblePublisher(List<Package> pkgs = null, List listStatus) {
        Set<String> publishers = []

        if(pkgs) {
            publishers.addAll(TitleInstancePackagePlatform.executeQuery("select distinct(publisherName) from TitleInstancePackagePlatform where publisherName is not null and pkg in (:pkgs) and status in (:status) order by publisherName", [pkgs: pkgs, status: listStatus]))
        }else{
            publishers.addAll(TitleInstancePackagePlatform.executeQuery("select distinct(publisherName) from TitleInstancePackagePlatform where publisherName is not null and status in (:status) order by publisherName", [status: listStatus]))
        }
        publishers
    }
}

package wekb

import grails.gorm.transactions.Transactional
import wekb.helper.RDStore

@Transactional
class DropdownService {


    def selectedDropDown(String dropDownType, Package pkg = null, def status){
        List listStatus = status ? [RefdataValue.get(Long.parseLong(status))] : [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_EXPECTED, RDStore.KBC_STATUS_RETIRED]

        switch (dropDownType) {
            case 'series':
                getAllPossibleSeriesByPackage(pkg, listStatus)
                break
            case 'subjectArea':
                getAllPossibleSubjectAreasByPackage(pkg, listStatus)
                break
            case 'dateFirstOnlineYear':
                getAllPossibleDateFirstOnlineYearByPackage(pkg, listStatus)
                break
            case 'ddc':
                getAllPossibleDdcsByPackage(pkg, listStatus)
                break
            case 'language':
                getAllPossibleLanguagesByPackage(pkg, listStatus)
                break
            case 'publisher':
                getAllPossiblePublisherByPackage(pkg, listStatus)
                break
            default:
                []
                break
        }
    }


    Set<RefdataValue> getAllPossibleCoverageDepthsByPackage(Package pkg, List listStatus) {

        Set<RefdataValue> coverageDepths = []

        coverageDepths.addAll(RefdataValue.executeQuery("select rdv from RefdataValue rdv where rdv.value in (select tc.coverageDepth from TIPPCoverage tc join tc.tipp tipp where tc.coverageDepth is not null and tipp.pkg = :pkg and tipp.status in (:status)) ", [pkg: pkg, status: listStatus]))

        coverageDepths
    }

    Set<String> getAllPossibleSeriesByPackage(Package pkg = null, List listStatus) {
        Set<String> series = []

        if(pkg) {
            series = TitleInstancePackagePlatform.executeQuery("select distinct(series) from TitleInstancePackagePlatform where series is not null and pkg = :pkg and status in (:status) order by series", [pkg: pkg, status: listStatus])
        }else {
            series = TitleInstancePackagePlatform.executeQuery("select distinct(series) from TitleInstancePackagePlatform where series is not null and status in (:status) order by series", [status: listStatus])
        }

        if(series.size() == 0){
            series << "No series found!"
        }
        series
    }

    Set<RefdataValue> getAllPossibleDdcsByPackage(Package pkg = null, List listStatus) {

        Set<RefdataValue> ddcs = []

        if(pkg) {
            ddcs.addAll(TitleInstancePackagePlatform.executeQuery("select ddc.ddc from DeweyDecimalClassification ddc join ddc.tipp tipp join tipp.pkg pkg where pkg = :pkg and tipp.status in (:status) order by ddc.ddc.value_en", [pkg: pkg, status: listStatus]))
        }else {
            ddcs.addAll(TitleInstancePackagePlatform.executeQuery("select ddc.ddc from DeweyDecimalClassification ddc join ddc.tipp tipp join tipp.pkg pkg where tipp.status in (:status) order by ddc.ddc.value_en", [status: listStatus]))
        }

        ddcs
    }

    Set<RefdataValue> getAllPossibleLanguagesByPackage(Package pkg = null, List listStatus) {

        Set<RefdataValue> languages = []

        if(pkg) {
            languages.addAll(TitleInstancePackagePlatform.executeQuery("select lang.language from Language lang join lang.tipp tipp join tipp.pkg pkg where pkg = :pkg and tipp.status in (:status) order by lang.language.value_en", [pkg: pkg, status: listStatus]))
        }else {
            languages.addAll(TitleInstancePackagePlatform.executeQuery("select lang.language from Language lang join lang.tipp tipp join tipp.pkg pkg where tipp.status in (:status) order by lang.language.value_en", [status: listStatus]))
        }

        languages
    }


    Set<String> getAllPossibleSubjectAreasByPackage(Package pkg = null, List listStatus) {

        SortedSet<String> subjects = new TreeSet<String>()
        List<String> rawSubjects
        if(pkg) {
            rawSubjects = TitleInstancePackagePlatform.executeQuery("select distinct(subjectArea) from TitleInstancePackagePlatform where subjectArea is not null and pkg = :pkg and status in (:status) order by subjectArea", [pkg: pkg, status: listStatus])
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


    Set<String> getAllPossibleDateFirstOnlineYearByPackage(Package pkg = null, List listStatus) {

        Set<String> dateFirstOnlines = []
        if(pkg) {
            dateFirstOnlines = TitleInstancePackagePlatform.executeQuery("select distinct(Year(dateFirstOnline)) from TitleInstancePackagePlatform where dateFirstOnline is not null and pkg = :pkg and status in (:status) order by YEAR(dateFirstOnline)", [pkg: pkg, status: listStatus])
        }else {
            dateFirstOnlines = TitleInstancePackagePlatform.executeQuery("select distinct(Year(dateFirstOnline)) from TitleInstancePackagePlatform where dateFirstOnline is not null and status in (:status) order by YEAR(dateFirstOnline)", [status: listStatus])
        }
        if(dateFirstOnlines.size() == 0){
            dateFirstOnlines << "No date first online found!"
        }

        dateFirstOnlines
    }


    Set<String> getAllPossiblePublisherByPackage(Package pkg = null, List listStatus) {
        Set<String> publishers = []

        if(pkg) {
            publishers.addAll(TitleInstancePackagePlatform.executeQuery("select distinct(publisherName) from TitleInstancePackagePlatform where publisherName is not null and pkg = :pkg and status in (:status) order by publisherName", [pkg: pkg, status: listStatus]))
        }else{
            publishers.addAll(TitleInstancePackagePlatform.executeQuery("select distinct(publisherName) from TitleInstancePackagePlatform where publisherName is not null and status in (:status) order by publisherName", [status: listStatus]))
        }
        publishers
    }
}

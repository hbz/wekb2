package wekb

import grails.gorm.transactions.Transactional
import wekb.helper.RDStore

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId

@Transactional
class CheckMyInfosService {

    List<Org> checkContacts(List<CuratoryGroup> curatoryGroups) {

        List<Org> orgListWithoutContacts = []
        curatoryGroups.each { CuratoryGroup curatoryGroup ->

            curatoryGroup.providedOrgs.each { Org org ->
                if(org.contacts.size() == 0){
                    orgListWithoutContacts << org
                }
            }

        }

        orgListWithoutContacts

    }

    List<Package> checkSourcesWithoutTitles(List<CuratoryGroup> curatoryGroups) {

        List<Package> packageWithoutTitle = []
        curatoryGroups.each { CuratoryGroup curatoryGroup ->

            curatoryGroup.providedPackages.each { Package pkg ->
                if(pkg.kbartSource && pkg.getTippCountWithoutRemoved() == 0){
                    packageWithoutTitle << pkg
                }
            }

        }

        packageWithoutTitle
    }

    List<Package> checkPackageWithoutSource(List<CuratoryGroup> curatoryGroups) {

        List<Package> packageWithoutSource = []
        curatoryGroups.each { CuratoryGroup curatoryGroup ->

            curatoryGroup.providedPackages.each { Package pkg ->
                if(!pkg.kbartSource){
                    packageWithoutSource << pkg
                }
            }

        }

        packageWithoutSource
    }

    List<Package> noChangesPackageLast30Days(List<CuratoryGroup> curatoryGroups) {
        Date dateNow = new Date()

        Date dateFor30Days = Date.from(LocalDate.now().minusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant())

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd")

        List status = [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_EXPECTED]
        String query = "select count(*) from TitleInstancePackagePlatform where dateCreated != lastUpdated and status in (:status) and lastUpdated >= :daysBefore and pkg = :pkg"

        List<Package> packageWithoutTitle = []
        curatoryGroups.each { CuratoryGroup curatoryGroup ->
            curatoryGroup.providedPackages.each { Package pkg ->
                if(pkg.tippCountWithoutRemoved > 0 && (TitleInstancePackagePlatform.executeQuery(query, [status: status, pkg: pkg, daysBefore: dateFor30Days])[0] == 0)){
                   packageWithoutTitle << pkg
                }
            }

        }

        packageWithoutTitle
    }

    List<Package> noChangesPackageLast30DaysAutoUpdate(List<CuratoryGroup> curatoryGroups) {
        Date dateNow = new Date()

        Date dateFor30Days = Date.from(LocalDate.now().minusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant())

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd")

        List status = [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_EXPECTED]
        String query = "select count(*) from TitleInstancePackagePlatform where dateCreated != lastUpdated and status in (:status) and lastUpdated >= :daysBefore and pkg = :pkg"

        List<Package> packageWithoutTitle = []
        curatoryGroups.each { CuratoryGroup curatoryGroup ->
            curatoryGroup.providedPackages.each { Package pkg ->
                if(pkg.kbartSource && pkg.kbartSource.automaticUpdates && pkg.tippCountWithoutRemoved > 0 && (TitleInstancePackagePlatform.executeQuery(query, [status: status, pkg: pkg, daysBefore: dateFor30Days])[0] == 0)){
                    packageWithoutTitle << pkg
                }
            }

        }

        packageWithoutTitle
    }

    List<Package> checkPackagesWithoutTitles(List<CuratoryGroup> curatoryGroups) {

        List<Package> packagesWithoutTitles = []
        curatoryGroups.each { CuratoryGroup curatoryGroup ->

            curatoryGroup.providedPackages.each { Package pkg ->
                if(pkg.getTippCountWithoutRemoved() == 0){
                    packagesWithoutTitles << pkg
                }
            }
        }

        packagesWithoutTitles
    }

    List<Package> checkPackagesWithoutProductID(List<CuratoryGroup> curatoryGroups) {

        List<Package> packages = []
        curatoryGroups.each { CuratoryGroup curatoryGroup ->

            curatoryGroup.providedPackages.each { Package pkg ->
                if(pkg.getAnbieterProduktIDs().isEmpty()){
                    packages << pkg
                }
            }
        }

        packages
    }

    List<Package> checkPackagesWithoutContentType(List<CuratoryGroup> curatoryGroups) {

        List<Package> packages = []
        curatoryGroups.each { CuratoryGroup curatoryGroup ->

            curatoryGroup.providedPackages.each { Package pkg ->
                if(pkg.contentType == null){
                    packages << pkg
                }
            }
        }

        packages
    }




}

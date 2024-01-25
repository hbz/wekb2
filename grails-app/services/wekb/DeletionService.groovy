package wekb

import grails.gorm.transactions.Transactional
import wekb.helper.RDStore

//@Transactional
class DeletionService {

    private boolean recordDeletedKBComponent(def component) {

        DeletedKBComponent deletedKBComponent
        DeletedKBComponent.withTransaction {
            deletedKBComponent = new DeletedKBComponent(uuid: component.uuid,
                    name: component.name,
                    oldDateCreated: component.dateCreated,
                    oldLastUpdated: component.lastUpdated,
                    oldId: component.id,
                    componentType: component.class.simpleName,
                    status: RDStore.DELETED_COMPONENT_STATUS_P_D)

            if (!deletedKBComponent.save()) {
                log.error("deletedKBComponent save error: ")
                deletedKBComponent.errors.allErrors.each {
                    println it
                }
                return false
            }
        }

        return true
    }

    void expungeRemovedComponents() {

        log.info("Process remove candidates")

        def status_removed = RDStore.KBC_STATUS_REMOVED
        int countTipps = 0
        int countTippsSuccess = 0

        int countPkgs = 0
        int countPkgsSuccess = 0

        int countOrgs = 0
        int countOrgsSuccess = 0

        int countKbartSources = 0
        int countcountKbartSourcesSuccess = 0

        int countPlatforms = 0
        int countPlatformsSuccess = 0

        int countCurs = 0
        int countCursSuccess = 0

        try {

            List<Long> tippIDs = TitleInstancePackagePlatform.executeQuery('select id from TitleInstancePackagePlatform where status = :status_removed', [status_removed: status_removed])
            countTipps = tippIDs.size()

            tippIDs.each { Long objectID ->
                if (expungeTipp(objectID)) {
                    countTippsSuccess++
                }
                log.info("tipp remove processing -> ${countTippsSuccess}/${countTipps}")
            }

            List<Long> platformIDs = Platform.executeQuery('select id from Platform where status = :status_removed', [status_removed: status_removed])
            countPlatforms = platformIDs.size()

            platformIDs.each { Long objectID ->
                if (expungePlatform(objectID)) {
                    countPlatformsSuccess++
                }
                log.info("Platform remove processing -> ${countPlatformsSuccess}/${countPlatforms}")
            }

            List<Long> kbartSourceIDs = KbartSource.executeQuery('select id from KbartSource where status = :status_removed', [status_removed: status_removed])
            countKbartSources = kbartSourceIDs.size()

            kbartSourceIDs.each { Long objectID ->
                if (expungeKbartSource(objectID)) {
                    countcountKbartSourcesSuccess++
                }
                log.info("KbartSource remove processing -> ${countcountKbartSourcesSuccess}/${countKbartSources}")
            }

            List<Long> orgIDs = Org.executeQuery('select id from Org where status = :status_removed', [status_removed: status_removed])
            countOrgs = orgIDs.size()

            orgIDs.each { Long objectID ->
                if (expungeOrg(objectID)) {
                    countOrgsSuccess++
                }
                log.info("Org remove processing -> ${countOrgsSuccess}/${countOrgs}")
            }


            List<Long> pkgIDs = Package.executeQuery('select id from Package where status = :status_removed', [status_removed: status_removed])
            countPkgs = pkgIDs.size()

            pkgIDs.each { Long objectID ->
                if (expungePkg(objectID)) {
                    countPkgsSuccess++
                }
                log.info("Package remove processing -> ${countPkgsSuccess}/${countPkgs}")
            }

            List<Long> curatoryGroupIDs = CuratoryGroup.executeQuery('select id from CuratoryGroup where status = :status_removed', [status_removed: status_removed])
            countCurs = curatoryGroupIDs.size()

            curatoryGroupIDs.each { Long objectID ->
                if (expungePkg(objectID)) {
                    countCursSuccess++
                }
                log.info("CuratoryGroup remove processing -> ${countCursSuccess}/${countCurs}")
            }

            log.info("TIPPs removed successful: ${countTippsSuccess}/${countTipps}")
            log.info("Platform removed successful -> ${countPlatformsSuccess}/${countPlatforms}")
            log.info("KbartSource removed successful -> ${countcountKbartSourcesSuccess}/${countKbartSources}")
            log.info("Org removed successful -> ${countOrgsSuccess}/${countOrgs}")
            log.info("Package removed successful -> ${countPkgsSuccess}/${countPkgs}")
            log.info("CuratoryGroup removed successful -> ${countCursSuccess}/${countCurs}")

        } catch (Exception e) {
            log.error 'expungeRemovedComponents: error while '
            e.printStackTrace()

        }
    }

    boolean expungeTipp(Long objectId) {
        try {
            log.info("TIPP expunge: " + objectId)
            TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(objectId)
            if (recordDeletedKBComponent(tipp)) {
                TitleInstancePackagePlatform.withTransaction {
                    Identifier.executeUpdate("delete from Identifier where tipp = :component", [component: tipp])
                    ComponentLanguage.executeUpdate("delete from ComponentLanguage where tipp = :component", [component: tipp])
                    TIPPCoverageStatement.executeUpdate("delete from TIPPCoverageStatement where tipp = :component", [component: tipp])
                    TippPrice.executeUpdate("delete from TippPrice where tipp = :component", [component: tipp])
                    UpdateTippInfo.executeUpdate("delete from UpdateTippInfo where tipp = :component", [component: tipp])
                    tipp.ddcs.clear()
                    tipp.refresh()
                    tipp.delete()
                }

                if (!TitleInstancePackagePlatform.get(objectId)) {
                    log.info("TIPP expunge success!")
                    return true
                } else {
                    log.info("TIPP expunge NOT success!")
                    return false
                }
            }
        } catch (Exception e) {
            log.error 'expungeTipp: error while '
            e.printStackTrace()

        }
    }

    boolean expungePkg(Long objectId) {
        try {
            log.info("Package expunge: " + objectId)
            Package pkg = Package.get(objectId)
            def removedStatus = RDStore.KBC_STATUS_REMOVED
            Date now = new Date()
            if (recordDeletedKBComponent(pkg)) {
                Package.withTransaction {
                    ComponentVariantName.executeUpdate("delete from ComponentVariantName as c where c.pkg = :component", [component: pkg])
                    CuratoryGroupPackage.executeUpdate("delete from CuratoryGroupPackage where pkg = :component", [component: pkg])
                    Identifier.executeUpdate("delete from Identifier where pkg = :component", [component: pkg])
                    PackageArchivingAgency.executeUpdate("delete from PackageArchivingAgency where pkg = :component", [component: pkg])
                    UpdateTippInfo.executeUpdate("delete from UpdateTippInfo where updatePackageInfo in (select upi.id from UpdatePackageInfo as upi where upi.pkg = :component)", [component: pkg])
                    UpdatePackageInfo.executeUpdate("delete from UpdatePackageInfo where pkg = :component", [component: pkg])

                    List<Long> tippIDs = TitleInstancePackagePlatform.executeQuery('select id from TitleInstancePackagePlatform where pkg = :pkg', [pkg: pkg])
                    int countTipps = tippIDs.size()
                    int countTippsSuccess = 0

                    tippIDs.each { Long objectID ->
                        if (expungeTipp(objectID)) {
                            countTippsSuccess++
                        }
                        log.info("pkg: tipp remove processing -> ${countTippsSuccess}/${countTipps}")
                    }

                    pkg.refresh()
                    pkg.delete()
                }

                if (!Package.get(objectId)) {
                    log.info("Package expunge success!")
                    return true
                } else {
                    log.info("Package expunge NOT success!")
                    return false
                }
            }
        } catch (Exception e) {
            log.error 'expungeTipp: error while '
            e.printStackTrace()

        }
    }

    boolean expungePlatform(Long objectId) {
        try {
            log.info("Platform expunge: " + objectId)
            Platform platform = Platform.get(objectId)
            if (recordDeletedKBComponent(platform)) {
                Platform.withTransaction {
                    CuratoryGroupPlatform.executeUpdate("delete from CuratoryGroupPlatform where platform = :component", [component: platform])
                    Identifier.executeUpdate("delete from Identifier where platform = :component", [component: platform])
                    PlatformFederation.executeUpdate("delete from PlatformFederation where platform = :component", [component: platform])
                    Package.executeUpdate("update Package set nominalPlatform = null, lastUpdated = now()  where nominalPlatform = :component", [component: platform])
                    platform.refresh()
                    platform.delete()
                }

                if (!Platform.get(objectId)) {
                    log.info("Platform expunge success!")
                    return true
                } else {
                    log.info("Platform expunge NOT success!")
                    return false
                }
            }
        } catch (Exception e) {
            log.error 'expungeTipp: error while '
            e.printStackTrace()

        }
    }

    boolean expungeOrg(Long objectId) {
        try {
            log.info("Org expunge: " + objectId)
            Org org = Org.get(objectId)
            if (recordDeletedKBComponent(org)) {
                Org.withTransaction {
                    ComponentVariantName.executeUpdate("delete from ComponentVariantName as c where c.org=:component", [component: org])
                    Contact.executeUpdate("delete from Contact where org = :component", [component: org])
                    CuratoryGroupOrg.executeUpdate("delete from CuratoryGroupOrg where org = :component", [component: org])
                    Identifier.executeUpdate("delete from Identifier where org = :component", [component: org])
                    org.roles.clear()
                    org.refresh()
                    org.delete()
                }

                if (!Org.get(objectId)) {
                    log.info("Org expunge success!")
                    return true
                } else {
                    log.info("Org expunge NOT success!")
                    return false
                }
            }
        } catch (Exception e) {
            log.error 'expungeTipp: error while '
            e.printStackTrace()

        }
    }

    boolean expungeKbartSource(Long objectId) {
        try {
            log.info("KbartSource expunge: " + objectId)
            KbartSource kbartSource = KbartSource.get(objectId)
            if (recordDeletedKBComponent(kbartSource)) {
                KbartSource.withTransaction {
                    CuratoryGroupKbartSource.executeUpdate("delete from CuratoryGroupKbartSource where kbartSource = :component", [component: kbartSource])
                    kbartSource.refresh()
                    kbartSource.delete()
                }

                if (!KbartSource.get(objectId)) {
                    log.info("KbartSource expunge success!")
                    return true
                } else {
                    log.info("KbartSource expunge NOT success!")
                    return false
                }
            }
        } catch (Exception e) {
            log.error 'expungeTipp: error while '
            e.printStackTrace()

        }
    }

    boolean expungeCuratoryGroup(Long objectId) {
        try {
            log.info("CuratoryGroup expunge: " + objectId)
            CuratoryGroup curatoryGroup = CuratoryGroup.get(objectId)
            if (recordDeletedKBComponent(curatoryGroup)) {
                CuratoryGroup.withTransaction {
                    CuratoryGroupUser.executeUpdate("delete from CuratoryGroupUser where curatoryGroup = :component", [component: curatoryGroup])
                    CuratoryGroupKbartSource.executeUpdate("delete from CuratoryGroupKbartSource where curatoryGroup = :component", [component: curatoryGroup])
                    CuratoryGroupPlatform.executeUpdate("delete from CuratoryGroupPlatform where curatoryGroup = :component", [component: curatoryGroup])
                    CuratoryGroupPackage.executeUpdate("delete from CuratoryGroupPackage where curatoryGroup = :component", [component: curatoryGroup])
                    CuratoryGroupOrg.executeUpdate("delete from CuratoryGroupOrg where curatoryGroup = :component", [component: curatoryGroup])
                    curatoryGroup.refresh()
                    curatoryGroup.delete()
                }

                if (!CuratoryGroup.get(objectId)) {
                    log.info("CuratoryGroup expunge success!")
                    return true
                } else {
                    log.info("CuratoryGroup expunge NOT success!")
                    return false
                }
            }
        } catch (Exception e) {
            log.error 'expungeTipp: error while '
            e.printStackTrace()

        }
    }
}

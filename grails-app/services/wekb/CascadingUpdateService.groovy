package wekb

import grails.gorm.transactions.Transactional

@Transactional
class CascadingUpdateService {

    void update(CuratoryGroup obj, Date lastUpdated) {
        CuratoryGroup.executeUpdate("update CuratoryGroup cg set cg.lastUpdated = :lastUpdated where cg = :obj", [
                lastUpdated: lastUpdated, obj: obj
        ])
    }

    void update(Org obj, Date lastUpdated) {
        Org.executeUpdate("update Org org set org.lastUpdated = :lastUpdated where org = :obj", [
                lastUpdated: lastUpdated, obj: obj
        ])
    }

    void update(Package obj, Date lastUpdated) {
        Package.executeUpdate("update Package pkg set pkg.lastUpdated = :lastUpdated where pkg = :obj", [
                lastUpdated: lastUpdated, obj: obj
        ])
    }

    void update(Platform obj, Date lastUpdated) {
        Platform.executeUpdate("update Platform plt set plt.lastUpdated = :lastUpdated where plt = :obj", [
                lastUpdated: lastUpdated, obj: obj
        ])
    }

    void update(TippPrice obj, Date lastUpdated) {
        if (obj.tipp) {
            if(obj.tipp instanceof TitleInstancePackagePlatform) {
                if (!obj.tipp.kbartImportRunning) {
                    update(obj.tipp, lastUpdated ?: new Date())
                }
            } else {
                update(obj.tipp, lastUpdated ?: new Date())
            }
        }
    }

    void update(Contact obj, Date lastUpdated) {
        if (obj.org) { update(obj.org, lastUpdated ?: new Date()) }
    }


    void update(Identifier obj, Date lastUpdated) {
        if (obj.org) { update(obj.org, lastUpdated ?: new Date()) }
        if (obj.pkg) { update(obj.pkg, lastUpdated ?: new Date()) }
        if (obj.tipp && !obj.tipp.kbartImportRunning) {
            update(obj.tipp, lastUpdated ?: new Date())
        }
    }

    void update(ComponentLanguage obj, Date lastUpdated) {
        if (obj.tipp) { update(obj.tipp, lastUpdated ?: new Date()) }
    }

    void update(ComponentVariantName obj, Date lastUpdated) {
        if (obj.pkg) { update(obj.pkg, lastUpdated ?: new Date()) }
        if (obj.org) { update(obj.org, lastUpdated ?: new Date()) }
    }

    void update(PackageArchivingAgency obj, Date lastUpdated) {
        if (obj.pkg) { update(obj.pkg, lastUpdated ?: new Date()) }
    }

    void update(TitleInstancePackagePlatform obj, Date lastUpdated) {
        if (obj.pkg && !obj.kbartImportRunning) {
            update(obj.pkg, lastUpdated ?: new Date())
            TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform tipp set tipp.lastUpdated = :lastUpdated where tipp = :obj", [
                    lastUpdated: lastUpdated, obj: obj
            ])
        }
    }

    void update(TIPPCoverageStatement obj, Date lastUpdated) {
        if (obj.tipp) { update(obj.tipp, lastUpdated ?: new Date()) }
    }
}

package wekb

import grails.gorm.transactions.Transactional

@Transactional
class CascadingUpdateService {

    void update(KBComponent obj, Date lastUpdated) {
        KBComponent.executeUpdate("update KBComponent kbc set kbc.lastUpdated = :lastUpdated where kbc = :obj", [
                lastUpdated: lastUpdated, obj: obj
        ])
    }

    void update(ComponentPrice obj, Date lastUpdated) {
        if (obj.owner) {
            if(obj.owner instanceof TitleInstancePackagePlatform) {
                if (!obj.owner.kbartImportRunning) {
                    update(obj.owner, lastUpdated)
                }
            } else {
                update(obj.owner, lastUpdated)
            }
        }
    }

    void update(Contact obj, Date lastUpdated) {
        if (obj.org) { update(obj.org, lastUpdated) }
    }


    void update(Identifier obj, Date lastUpdated) {
        if (obj.org) { update(obj.org, lastUpdated) }
        if (obj.pkg) { update(obj.pkg, lastUpdated) }
        if (obj.tipp && !obj.tipp.kbartImportRunning) {
            update(obj.tipp, lastUpdated)
        }
    }

    void update(KBComponentAdditionalProperty obj, Date lastUpdated) {
        if (obj.fromComponent) { update(obj.fromComponent, lastUpdated) }
    }

    void update(KBComponentLanguage obj, Date lastUpdated) {
        if (obj.kbcomponent) { update(obj.kbcomponent, lastUpdated) }
    }

    void update(KBComponentVariantName obj, Date lastUpdated) {
        if (obj.owner) { update(obj.owner, lastUpdated) }
    }

    void update(PackageArchivingAgency obj, Date lastUpdated) {
        if (obj.pkg) { update(obj.pkg, lastUpdated) }
    }

    void update(TitleInstancePackagePlatform obj, Date lastUpdated) {
        if (obj.pkg && !obj.kbartImportRunning) {
            update(obj.pkg, lastUpdated)
            KBComponent.executeUpdate("update KBComponent kbc set kbc.lastUpdated = :lastUpdated where kbc = :obj", [
                    lastUpdated: lastUpdated, obj: obj
            ])
        }
    }

    void update(TIPPCoverageStatement obj, Date lastUpdated) {
        if (obj.owner) { update(obj.owner, lastUpdated) }
    }
}

package wekb

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import groovy.sql.Sql


@Transactional
class LaserService {

    GrailsApplication grailsApplication

    int laserTippsCount() {
        Sql sql
        int count
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                count = sql.rows("select count(*) from title_instance_package_platform ")[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by laserTippsCount:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        count

    }

    int laserPackagesCount() {
        Sql sql
        int count
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                count = sql.rows("select count(*) from package ")[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by laserPackagesCount:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        count

    }

    int laserProviderCount() {
        Sql sql
        int count
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                count = sql.rows("select count(*) from provider ")[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by laserProviderCount:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        count

    }

    int laserPlaformCount() {
        Sql sql
        int count
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                count = sql.rows("select count(*) from platform ")[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by laserPlaformCount:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        count

    }

    int laserVendorCount() {
        Sql sql
        int count
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                count = sql.rows("select count(*) from vendor ")[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by laserVendorCount:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        count

    }


    int getLaserTipp(String wekbUuid) {
        Sql sql
        int tippCount
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                tippCount = sql.rows("select count(*) from title_instance_package_platform where tipp_gokb_id = :tipp_gokb_id", [tipp_gokb_id: wekbUuid])[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by getLaserTipp:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        tippCount

    }

    int tippsInLaserCount(String wekbUuid) {
        Sql sql
        int tippsInLaserCount
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                tippsInLaserCount = sql.rows("select count(*) from title_instance_package_platform left join public.package p on p.pkg_id = title_instance_package_platform.tipp_pkg_fk where pkg_gokb_id = :wekbUuid", [wekbUuid: wekbUuid])[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by tippsInLaserCount:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        tippsInLaserCount

    }

    int packageLinkedInLaserCount(String wekbUuid) {
        Sql sql
        int packageLinkedCount
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)


                packageLinkedCount = sql.rows("select count(*) from subscription_package left join public.package p on p.pkg_id = subscription_package.sp_pkg_fk where pkg_gokb_id = :wekbUuid", [wekbUuid: wekbUuid])[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by packageLinkedInLaserCount:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        packageLinkedCount

    }

    int listPackageLinkedInLaserCount(String wekbUuid) {
        Sql sql
        int packageLinkedCount
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                packageLinkedCount = sql.rows("select count(*) from subscription_package left join public.package p on p.pkg_id = subscription_package.sp_pkg_fk where pkg_gokb_id = :wekbUuid", [wekbUuid: wekbUuid])[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by listPackageLinkedInLaserCount:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        packageLinkedCount

    }



    void cleanUpLaserTipp(String wekbUuidOldTipp, String wekbUuidNewTipp) {
        Sql sql
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)


                Long oldLaserTippId
                Long newLaserTippId

                if (sql.rows("select count(*) from title_instance_package_platform where tipp_gokb_id = :tipp_gokb_id", [tipp_gokb_id: wekbUuidOldTipp])[0]['count'] > 0) {
                    oldLaserTippId = sql.rows("select tipp_id from title_instance_package_platform where tipp_gokb_id = :tipp_gokb_id", [tipp_gokb_id: wekbUuidOldTipp])[0]['tipp_id']
                }
                if (sql.rows("select count(*) from title_instance_package_platform where tipp_gokb_id = :tipp_gokb_id", [tipp_gokb_id: wekbUuidNewTipp])[0]['count'] > 0) {
                    newLaserTippId = sql.rows("select tipp_id from title_instance_package_platform where tipp_gokb_id = :tipp_gokb_id", [tipp_gokb_id: wekbUuidNewTipp])[0]['tipp_id']
                }

                if (oldLaserTippId && newLaserTippId) {
                    int ieCount = sql.rows("select count(*) from issue_entitlement where ie_tipp_fk = :oldTippId", [oldTippId: oldLaserTippId])[0]['count']
                    log.info("cleanUpLaserTipp: wekbUuidOldTipp -> ${wekbUuidOldTipp}: ieCount = ${ieCount}")
                    if (ieCount > 0) {
                        int updateCount = sql.executeUpdate("UPDATE issue_entitlement SET ie_tipp_fk = :newTippID, ie_last_updated = now() where ie_tipp_fk = :oldTippId", [newTippID: newLaserTippId, oldTippId: oldLaserTippId])
                        log.info("cleanUpLaserTipp: replace IE wekbUuidOldTipp -> ${wekbUuidOldTipp} with ${wekbUuidNewTipp}: ieCount = ${ieCount}, updateCount = ${updateCount} -> oldTippId -> ${oldLaserTippId} with ${newLaserTippId}")
                    }
                }
                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by cleanUpLaserTipp:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }
    }

    List<Package> packageLinkedInLaser() {
        Sql sql
        List<String> packageUUIDList
        List<Package> packages
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                packageUUIDList = sql.rows("select pkg_gokb_id from subscription_package left join public.package p on p.pkg_id = subscription_package.sp_pkg_fk where pkg_gokb_id = any(:wekbUuid)", [wekbUuid: sql.getConnection().createArrayOf('varchar', Package.findAll().uuid.toArray())])['pkg_gokb_id']
                packages = Package.findAllByUuidInList(packageUUIDList, [sort: 'name'])

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by packageLinkedInLaser:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        packages

    }

    List<Package> packageNotLinkedInLaser() {
        Sql sql
        List<String> packageUUIDList
        List<Package> packages
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                packageUUIDList = sql.rows("select pkg_gokb_id from subscription_package left join public.package p on p.pkg_id = subscription_package.sp_pkg_fk where pkg_gokb_id = any(:wekbUuid)", [wekbUuid: sql.getConnection().createArrayOf('varchar', Package.findAll().uuid.toArray())])['pkg_gokb_id']
                packages = Package.findAllByUuidNotInList(packageUUIDList, [sort: 'name'])

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by packageNotLinkedInLaser:", ex)
        }
        finally {
            try {
                if(sql)
                sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        packages

    }

    def linkedSubsInLaser(String wekbUuid) {
        Sql sql
        List linkedSubs
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                linkedSubs = sql.rows('''select pkg_gokb_id,
                                                   org_name,
                                                   org_id,
                                                   org_sortname,
                                                   sub_name,
                                                   sub_id,
                                                   rv.rdv_value_en as status,
                                                   sub_start_date,
                                                   sub_end_date,
                                                   sub_has_perpetual_access,
                                                   rv2.rdv_value_en as holding_selection
                                            from subscription_package
                                                     left join public.package p on p.pkg_id = subscription_package.sp_pkg_fk
                                                     left join subscription s on subscription_package.sp_sub_fk = s.sub_id
                                                     left join public.org_role o on s.sub_id = o.or_sub_fk
                                                     left join org oo on oo.org_id = o.or_org_fk
                                                     left join public.refdata_value rv on s.sub_status_rv_fk = rv.rdv_id
                                                     left join public.refdata_value rv2 on s.sub_holding_selection_rv_fk = rv2.rdv_id
                                            where pkg_gokb_id = :wekbUuid order by sub_id, org_sortname''', [wekbUuid: wekbUuid])

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by packageLinkedInLaser:", ex)
        }
        finally {
            try {
                if(sql)
                    sql.close()
            }
            catch (Exception e) {
                log.error("Problem by Close SQL Client:", e)
            }
        }

        linkedSubs

    }

    Map getConfig(){
        Map result = [:]
        result.laserDBUrl = grailsApplication.config.getProperty('laserDBUrl', String)
        result.laserDBUser = grailsApplication.config.getProperty('laserDBUser', String)
        result.laserDBPassword = grailsApplication.config.getProperty('laserDBPassword', String)
        result.laserDBDriver = 'org.postgresql.Driver'
        result
    }

    String getLaserURL() {
        grailsApplication.config.getProperty('laserUrl', String)
    }

    String getLaserSubURL() {
        getLaserURL() + '/subscription/show'
    }

    String getLaserOrgURL() {
        getLaserURL() + '/org/show'
    }
}

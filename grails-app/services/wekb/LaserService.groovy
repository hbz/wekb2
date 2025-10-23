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

                tippsInLaserCount = sql.rows("select count(*) from title_instance_package_platform left join package p on p.pkg_id = title_instance_package_platform.tipp_pkg_fk where pkg_gokb_id = :wekbUuid", [wekbUuid: wekbUuid])[0]['count']

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

    int tippsWithStatusInLaserCount(String wekbUuid, String status) {
        Sql sql
        int tippsInLaserCount
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                tippsInLaserCount = sql.rows('''select count(*) from title_instance_package_platform 
                                                    left join package p on p.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg_gokb_id = :wekbUuid 
                                                    and tipp_status_rv_fk = (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where rdv_value = :status and rdc_description = 'tipp.status')''', [wekbUuid: wekbUuid, status: status])[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by tippsWithStatusInLaserCount:", ex)
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

    String permanentTitlesWithStatusInLaserCount(String wekbUuid, String status) {
        Sql sql
        int tippsInLaserCount
        int ptInLaserCount
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                tippsInLaserCount = sql.rows('''SELECT COUNT(DISTINCT pt_tipp_fk)
                                                    FROM permanent_title 
                                                    where pt_tipp_fk in ( 
                                                    SELECT tipp_id FROM title_instance_package_platform
                                                    left join package p on p.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg_gokb_id = :wekbUuid 
                                                    and tipp_status_rv_fk = (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where rdv_value = :status and rdc_description = 'tipp.status'))''', [wekbUuid: wekbUuid, status: status])[0]['count']

                ptInLaserCount = sql.rows('''SELECT COUNT(pt_tipp_fk)
                                                    FROM permanent_title 
                                                    where pt_tipp_fk in ( 
                                                    SELECT tipp_id FROM title_instance_package_platform
                                                    left join package p on p.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg_gokb_id = :wekbUuid 
                                                    and tipp_status_rv_fk = (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where rdv_value = :status and rdc_description = 'tipp.status'))''', [wekbUuid: wekbUuid, status: status])[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by permanentTitlesWithStatusInLaserCount:", ex)
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

        return "$tippsInLaserCount ($ptInLaserCount)"

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

    List packageLinkedInLaser() {
        Sql sql
        List packages
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                packages = sql.rows('''select 
                                                pkg_gokb_id,
                                                (select count(*) from subscription_package left join package pkg on pkg.pkg_id = subscription_package.sp_pkg_fk where pkg.pkg_gokb_id = p.pkg_gokb_id) as linkedPackageCount
                                                from subscription_package left join package p on p.pkg_id = subscription_package.sp_pkg_fk where pkg_gokb_id = any(:wekbUuid) group by pkg_gokb_id''', [wekbUuid: sql.getConnection().createArrayOf('varchar', Package.findAll().uuid.toArray())])
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

                packageUUIDList = sql.rows("select pkg_gokb_id from subscription_package left join package p on p.pkg_id = subscription_package.sp_pkg_fk where pkg_gokb_id = any(:wekbUuid)", [wekbUuid: sql.getConnection().createArrayOf('varchar', Package.findAll().uuid.toArray())])['pkg_gokb_id']
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

    def linkedSubsInLaser(String wekbUuid, String status = null, String perpetualAccess = null) {
        Sql sql
        List linkedSubs
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                Map queryMap = [wekbUuid: wekbUuid]
                String orderBy = ' order by sub_id, org_sortname'
                String query = '''select pkg_gokb_id,
                                                   org_name,
                                                   org_id,
                                                   org_sortname,
                                                   sub_name,
                                                   sub_id,
                                                   rv.rdv_value_en as status,
                                                   sub_start_date,
                                                   sub_end_date,
                                                   sub_has_perpetual_access,
                                                   rv2.rdv_value_en as holding_selection,
                                                   rv3.rdv_value_en as sub_typ,
                                                   (SELECT COUNT(DISTINCT pt_tipp_fk)
                                                    FROM permanent_title 
                                                    where pt_subscription_fk = s.sub_id and pt_tipp_fk in ( 
                                                    SELECT tipp_id FROM title_instance_package_platform
                                                    left join package pkg on pkg.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg.pkg_gokb_id = p.pkg_gokb_id)) as tippCount,
                                                    (SELECT COUNT(pt_id)
                                                    FROM permanent_title 
                                                    where pt_subscription_fk = s.sub_id and pt_tipp_fk in ( 
                                                    SELECT tipp_id FROM title_instance_package_platform
                                                    left join package pkg on pkg.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg.pkg_gokb_id = p.pkg_gokb_id)) as ptCount
                                            from subscription_package
                                                     left join package p on p.pkg_id = subscription_package.sp_pkg_fk
                                                     left join subscription s on subscription_package.sp_sub_fk = s.sub_id
                                                     left join org_role o on s.sub_id = o.or_sub_fk
                                                     left join org oo on oo.org_id = o.or_org_fk
                                                     left join refdata_value rv on s.sub_status_rv_fk = rv.rdv_id
                                                     left join refdata_value rv2 on s.sub_holding_selection_rv_fk = rv2.rdv_id
                                            left join refdata_value rv3 on s.sub_type_rv_fk = rv3.rdv_id
                                            where pkg_gokb_id = :wekbUuid 
                                            and (sub_parent_sub_fk is not null and o.or_roletype_fk IN (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where (rdv_value = 'Subscriber_Consortial' or rdv_value ='Subscriber_Consortial_Hidden') and rdc_description = 'organisational.role')
                                                     or (sub_parent_sub_fk is null and o.or_roletype_fk IN (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where (rdv_value = 'Subscriber' or rdv_value = 'Subscription Consortia') and rdc_description = 'organisational.role')))      
                                           '''
                if(status){
                    query = query.replace('where pkg.pkg_gokb_id = p.pkg_gokb_id', '''where pkg.pkg_gokb_id = p.pkg_gokb_id
                                                    and tipp_status_rv_fk = (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where rdv_value = :status and rdc_description = 'tipp.status')''')
                    query = query + ''' and s.sub_id in (SELECT pt_subscription_fk
                                                    FROM permanent_title 
                                                    where pt_tipp_fk in ( 
                                                    SELECT tipp_id FROM title_instance_package_platform
                                                    left join package pkg2 on pkg2.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg2.pkg_gokb_id = p.pkg_gokb_id
                                                    and tipp_status_rv_fk = (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where rdv_value = :status and rdc_description = 'tipp.status'))) '''
                    queryMap.status = status
                }

                if(perpetualAccess){
                    if(perpetualAccess == 'true'){
                        query = query + ' and sub_has_perpetual_access = true '
                    }
                    if(perpetualAccess == 'false'){
                        query = query + ' and sub_has_perpetual_access = false '
                    }

                }

                query = query + orderBy

                linkedSubs = sql.rows(query, queryMap)

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

    def linkedPTOverSubInLaser(String wekbUuid, String status = null, Long subId) {
        Sql sql
        List linkedSubs
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                Map queryMap = [wekbUuid: wekbUuid, subId: subId]
                String orderBy = ' order by tipp_name, rv.rdv_value_en'
                String query = '''select tipp_name,
                                   rv.rdv_value_en as tipp_status,
                                   tipp_id,
                                   tipp_gokb_id,
                                   pt_ie_fk,
                                   rv2.rdv_value_en as ie_status
                                    from  permanent_title
                                              left join public.title_instance_package_platform tipp on tipp.tipp_id = permanent_title.pt_tipp_fk
                                              left join refdata_value rv on tipp.tipp_status_rv_fk = rv.rdv_id
                                                left join public.issue_entitlement ie on permanent_title.pt_ie_fk = ie.ie_id
                                              left join refdata_value rv2 on ie.ie_status_rv_fk = rv2.rdv_id
                                    where pt_subscription_fk in ( select sub_id
                                                                  from subscription_package
                                                                           left join package p on p.pkg_id = subscription_package.sp_pkg_fk
                                                                           left join subscription s on subscription_package.sp_sub_fk = s.sub_id
                                                                  where pkg_gokb_id = :wekbUuid and sub_id = :subId)'''
                if(status){
                    query = query + ''' and pt_tipp_fk in ( 
                                                    SELECT tipp_id FROM title_instance_package_platform
                                                    left join package pkg2 on pkg2.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg2.pkg_gokb_id = pkg_gokb_id
                                                    and tipp_status_rv_fk = (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where rdv_value = :status and rdc_description = 'tipp.status')) '''
                    queryMap.status = status
                }

                query = query + orderBy

                linkedSubs = sql.rows(query, queryMap)

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

    int packageLinkedInLaserCount(String wekbUuid) {
        Sql sql
        int packageLinkedCount
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)


                packageLinkedCount = sql.rows("select count(*) from subscription_package left join package p on p.pkg_id = subscription_package.sp_pkg_fk where p.pkg_gokb_id = :wekbUuid", [wekbUuid: wekbUuid])[0]['count']

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

    int packageLinkedWithPerpetualAccessInLaserCount(String wekbUuid) {
        Sql sql
        int packageLinkedCount
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)


                packageLinkedCount = sql.rows("select count(*) from subscription_package left join package p on p.pkg_id = subscription_package.sp_pkg_fk left join subscription s on subscription_package.sp_sub_fk = s.sub_id where p.pkg_gokb_id = :wekbUuid and s.sub_has_perpetual_access = true", [wekbUuid: wekbUuid])[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by packageLinkedWithPerpetualAccessInLaserCount:", ex)
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

    int packageLinkedWithOutPerpetualAccessInLaserCount(String wekbUuid) {
        Sql sql
        int packageLinkedCount
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)


                packageLinkedCount = sql.rows("select count(*) from subscription_package left join package p on p.pkg_id = subscription_package.sp_pkg_fk left join subscription s on subscription_package.sp_sub_fk = s.sub_id where p.pkg_gokb_id = :wekbUuid and s.sub_has_perpetual_access = false", [wekbUuid: wekbUuid])[0]['count']

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by packageLinkedWithOutPerpetualAccessInLaserCount:", ex)
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

    def linkedPackageWithPermanentTitlesInLaser(String status) {
        Sql sql
        List linked
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                linked = sql.rows('''select pkg.pkg_gokb_id as uuid,
                                (select count(*) from subscription_package left join package p on p.pkg_id = subscription_package.sp_pkg_fk where pkg.pkg_gokb_id = p.pkg_gokb_id) as packageLinkedInLaserCount,
                    
                                (select count(*) from title_instance_package_platform 
                                                    left join package p on p.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg.pkg_gokb_id = p.pkg_gokb_id
                                                    and tipp_status_rv_fk = (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where rdv_value = :status and rdc_description = 'tipp.status')) as tippCount,
                                                    
                                (SELECT COUNT(DISTINCT pt_tipp_fk)
                                                    FROM permanent_title 
                                                    where pt_tipp_fk in ( 
                                                    SELECT tipp_id FROM title_instance_package_platform
                                                    left join package p on p.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg.pkg_gokb_id = p.pkg_gokb_id
                                                    and tipp_status_rv_fk = (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where rdv_value = :status and rdc_description = 'tipp.status'))) as ptCount
                                                    from package pkg 
                                                    where pkg.pkg_gokb_id in (select pkg_gokb_id from subscription_package left join package p on p.pkg_id = subscription_package.sp_pkg_fk where pkg_gokb_id = any(:wekbUuid) group by pkg_gokb_id)
                                                    and 
                                                        (SELECT COUNT(DISTINCT pt_tipp_fk)
                                                    FROM permanent_title 
                                                    where pt_tipp_fk in ( 
                                                    SELECT tipp_id FROM title_instance_package_platform
                                                    left join package p on p.pkg_id = title_instance_package_platform.tipp_pkg_fk 
                                                    where pkg.pkg_gokb_id = p.pkg_gokb_id
                                                    and tipp_status_rv_fk = (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where rdv_value = :status and rdc_description = 'tipp.status'))) > 0                                                        
                                                    order by pkg.pkg_name''', [wekbUuid: sql.getConnection().createArrayOf('varchar', Package.findAll().uuid.toArray()), status: status])

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by linkedPackageWithPermanentTitlesInLaser:", ex)
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

        linked

    }

    String getLaserPackageId(String wekbUuid) {
        Sql sql
        String packageId
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                packageId = sql.rows('''select pkg.pkg_id from package pkg where pkg.pkg_gokb_id = :wekbUuid''', [wekbUuid: wekbUuid])['pkg_id'][0]

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by getLaserPackageId:", ex)
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

        packageId

    }

    Map subInfosFromLaser(Long laserID) {
        Sql sql
        Map subInfos
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                Map queryMap = [laserID: laserID]
                String query = '''select org_name,
                                                   org_id,
                                                   org_sortname,
                                                   sub_name,
                                                   sub_id,
                                                   rv.rdv_value_en as status,
                                                   sub_start_date,
                                                   sub_end_date,
                                                   sub_has_perpetual_access,
                                                   rv2.rdv_value_en as holding_selection,
                                                   rv3.rdv_value_en as sub_typ
                                            from subscription s
                                                     left join org_role o on s.sub_id = o.or_sub_fk
                                                     left join org oo on oo.org_id = o.or_org_fk
                                                     left join refdata_value rv on s.sub_status_rv_fk = rv.rdv_id
                                                     left join refdata_value rv2 on s.sub_holding_selection_rv_fk = rv2.rdv_id
                                            left join refdata_value rv3 on s.sub_type_rv_fk = rv3.rdv_id
                                            where sub_id = :laserID 
                                            and (sub_parent_sub_fk is not null and o.or_roletype_fk IN (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where (rdv_value = 'Subscriber_Consortial' or rdv_value ='Subscriber_Consortial_Hidden') and rdc_description = 'organisational.role')
                                                     or (sub_parent_sub_fk is null and o.or_roletype_fk IN (select rdv_id from refdata_value join refdata_category on rdv_owner = rdc_id where (rdv_value = 'Subscriber' or rdv_value = 'Subscription Consortia') and rdc_description = 'organisational.role')))      
                                           '''

                subInfos = sql.rows(query, queryMap)[0]

                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by subInfosFromLaser:", ex)
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

        subInfos

    }


    List<Map> platformDiff() {
        Sql sql
        List<Map> platforms
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                platforms = sql.rows('''select plat_name,
                                               rv.rdv_value_en as platform_status,
                                               plat_id,
                                               plat_gokb_id,
                                               prov_id,
                                               prov_name,
                                               rv2.rdv_value_en as prov_status,
                                            (select count(*) from title_instance_package_platform where tipp_plat_fk = platform.plat_id) as titles,
                                            (select count(*) from package where pkg_nominal_platform_fk = platform.plat_id) as packages,
                                            (select count(*) from subscription_package left join package p on p.pkg_id = subscription_package.sp_pkg_fk where p.pkg_id in (select pkg_id from package where pkg_nominal_platform_fk = platform.plat_id)) as linkedPackages
                                            from  platform
                                            left join refdata_value rv on platform.plat_status_rv_fk = rv.rdv_id
                                            left join provider p on platform.plat_provider_fk = p.prov_id
                                            left join refdata_value rv2 on prov_status_rv_fk = rv2.rdv_id
                                            where NOT (plat_gokb_id = any(:wekbUuid))
                                            order by plat_name, rv.rdv_value_en''', [wekbUuid: sql.getConnection().createArrayOf('varchar', Platform.findAll().uuid.toArray())])


                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by platformDiff:", ex)
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
        platforms
    }

    List<Map> packageDiff() {
        Sql sql
        List<Map> pkgs
        try {
            Map config = getConfig()

            if(config.laserDBUrl && config.laserDBUser && config.laserDBPassword) {
                sql = Sql.newInstance(config.laserDBUrl, config.laserDBUser, config.laserDBPassword, config.laserDBDriver)

                pkgs = sql.rows('''select pkg_name,
                                               rv.rdv_value_en as pkg_status,
                                               pkg_id,
                                               pkg_gokb_id,
                                               prov_id,
                                               prov_name,
                                               rv2.rdv_value_en as prov_status,
                                            (select count(*) from title_instance_package_platform where tipp_pkg_fk = package.pkg_id) as titles,
                                            (select count(*) from subscription_package left join package p on p.pkg_id = subscription_package.sp_pkg_fk where p.pkg_id = package.pkg_id) as linkedPackages
                                            from  package
                                            left join refdata_value rv on package.pkg_status_rv_fk = rv.rdv_id
                                            left join provider p on package.pkg_provider_fk = p.prov_id
                                            left join refdata_value rv2 on prov_status_rv_fk = rv2.rdv_id
                                            where NOT (pkg_gokb_id = any(:wekbUuid))
                                            order by pkg_name, rv.rdv_value_en''', [wekbUuid: sql.getConnection().createArrayOf('varchar', Package.findAll().uuid.toArray())])


                sql.close()
            }
        }catch (Exception ex){
            log.error("Problem by packageDiff:", ex)
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
        pkgs
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

    String getLaserSubPackageURL() {
        getLaserURL() + '/subscription/index'
    }

    String getLaserOrgURL() {
        getLaserURL() + '/org/show'
    }

    String getLaserProviderURL() {
        getLaserURL() + '/provider/show'
    }

    String getLaserPackageURL() {
        getLaserURL() + '/package/show'
    }

    String getLaserTippURL() {
        getLaserURL() + '/tipp/show'
    }

    String getLaserIeURL() {
        getLaserURL() + '/issueEntitlement/show'
    }

    String getLaserPlatformURL() {
        getLaserURL() + '/platform/show'
    }
}

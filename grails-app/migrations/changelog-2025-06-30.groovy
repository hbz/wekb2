databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded)", id: "1751292072308-1") {
        grailsChange {
            change {
                sql.execute('''INSERT INTO refdata_value (rdv_id, rdv_version, rdv_owner, rdv_value) VALUES ((select nextval ('hibernate_sequence')), 0, (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType'), 'Not Set');''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1751292072308-2") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_publication_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType') and rdv_value = 'Not Set')
                                            where tipp_publication_type_rv_fk is null;''')

                confirm("set tipp_publication_type_rv_fk = Not Set where is null of tipp: ${countUpdate}")
                changeSet.setComments("set tipp_publication_type_rv_fk = Not Set where is null of tipp: ${countUpdate}")
            }
            rollback {}
        }
    }

}

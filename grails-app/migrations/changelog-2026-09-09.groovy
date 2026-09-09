databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded)", id: "1788942114456-1") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''INSERT INTO refdata_value (rdv_id, rdv_version, rdv_owner, rdv_value, rdv_is_hard_data, rdv_value_de, rdv_value_en, rdv_date_created, rdv_last_updated) 
                VALUES ((select nextval ('hibernate_sequence')), 0, (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.ContentType'), 'Not Set', true, 'Not Set', 'Not Set', now(), now());''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1788942114456-2") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update package set pkg_content_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.ContentType') and rdv_value = 'Not Set')
                                            where pkg_content_type_rv_fk is null;''')

                confirm("set pkg_content_type_rv_fk = Not Set where is null of pkg: ${countUpdate}")
                changeSet.setComments("set pkg_content_type_rv_fk = Not Set where is null of pkg: ${countUpdate}")
            }
            rollback {}
        }
    }
}

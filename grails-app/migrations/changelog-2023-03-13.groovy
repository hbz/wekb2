databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded)", id: "1678726438276-1") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''delete from identifier where id_org_fk is not null and 
                id_namespace_fk = (select idns_id from identifier_namespace where
                idns_value = 'dnb' and  
                idns_targettype = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'Org'));''')

                confirm("delete identifier with namespace dnb: ${countUpdate}")
                changeSet.setComments("delete identifier with namespace dnb: ${countUpdate}")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1678726438276-2") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''delete from identifier where id_org_fk is not null and 
                id_namespace_fk = (select idns_id from identifier_namespace where
                idns_value = 'isil' and  
                idns_targettype = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'Org'));''')

                confirm("delete identifier with namespace isil: ${countUpdate}")
                changeSet.setComments("delete identifier with namespace isil: ${countUpdate}")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1678726438276-3") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''delete from identifier where id_org_fk is not null and 
                id_namespace_fk = (select idns_id from identifier_namespace where
                idns_value = 'zdb_ppn' and  
                idns_targettype = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'Org'));''')

                confirm("delete identifier with namespace zdb_ppn: ${countUpdate}")
                changeSet.setComments("delete identifier with namespace zdb_ppn: ${countUpdate}")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1678726438276-4") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''delete from identifier_namespace where
                idns_value = 'dnb' and  
                idns_targettype = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'Org');''')

                confirm("delete identifier_namespace with namespace zdb_ppn: ${countUpdate}")
                changeSet.setComments("delete identifier_namespace with namespace zdb_ppn: ${countUpdate}")

            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (hand-coded)", id: "1678726438276-5") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''delete from identifier_namespace where
                idns_value = 'isil' and  
                idns_targettype = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'Org');''')

                confirm("delete identifier_namespace with namespace zdb_ppn: ${countUpdate}")
                changeSet.setComments("delete identifier_namespace with namespace zdb_ppn: ${countUpdate}")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1678726438276-6") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''delete from identifier_namespace where
                idns_value = 'zdb_ppn' and  
                idns_targettype = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'Org');''')

                confirm("delete identifier_namespace with namespace zdb_ppn: ${countUpdate}")
                changeSet.setComments("delete identifier_namespace with namespace zdb_ppn: ${countUpdate}")

            }
            rollback {}
        }
    }
}

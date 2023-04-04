databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1664737653848-1") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_host_platform_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1664737653848-2") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_pkg_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1664737653848-3") {
        createIndex(indexName: "tipp_host_platform_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_host_platform_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1664737653848-4") {
        createIndex(indexName: "tipp_pkg_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_pkg_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1664737653848-5") {
        addForeignKeyConstraint(baseColumnNames: "tipp_pkg_fk", baseTableName: "title_instance_package_platform", constraintName: "FK9rad3hn4ct51x6d2nruxxcakq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1664737653848-6") {
        addForeignKeyConstraint(baseColumnNames: "tipp_host_platform_fk", baseTableName: "title_instance_package_platform", constraintName: "FKoiotwfahqljocmuksac3p5kov", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "platform")
    }


    changeSet(author: "djebeniani (modified)", id: "1664737653848-7") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_pkg_fk = (select combo_from_fk from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Package.Tipps') and combo_to_fk = kbc_id) where tipp_pkg_fk is null''')
                confirm("set tipp_pkg_fk from tipp: ${countUpdate}")
                changeSet.setComments("set tipp_pkg_fk from tipp: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1664737653848-8") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_host_platform_fk = (select combo_from_fk from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Platform.HostedTipps') and combo_to_fk = kbc_id) where tipp_host_platform_fk is null''')

                confirm("set tipp_host_platform_fk from tipp: ${countUpdate}")
                changeSet.setComments("set tipp_host_platform_fk from tipp: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1664737653848-9") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Package.Tipps')")

                confirm("delete combos with Package.Tipps type: ${countDelete}")
                changeSet.setComments("delete combos with Package.Tipps type: ${countDelete}")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1664737653848-10") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Platform.HostedTipps')")

                confirm("delete combos with Platform.HostedTipps' type: ${countDelete}")
                changeSet.setComments("delete combos with Platform.HostedTipps type: ${countDelete}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1664737653848-11") {
        dropColumn(columnName: "kbc_duplicate_of", tableName: "kbcomponent")
    }


}
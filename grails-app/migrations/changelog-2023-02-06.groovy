databaseChangeLog = {

    /*    changeSet(author: "djebeniani (generated)", id: "1675352641051-1") {
        alterSequence(sequenceName: "hibernate_sequence")
    }

    changeSet(author: "djebeniani (generated)", id: "1675352641051-2") {
        dropUniqueConstraint(constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1675352641051-3") {
        addUniqueConstraint(columnNames: "kbc_uuid", constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675352641051-4") {
        addColumn(tableName: "package") {
            column(name: "pkg_platform_fk", type: "int8") {
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675352641051-5") {
        addColumn(tableName: "package") {
            column(name: "pkg_provider_fk", type: "int8") {
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675352641051-6") {
        addColumn(tableName: "platform") {
            column(name: "plat_provider_fk", type: "int8") {
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675352641051-7") {
        addForeignKeyConstraint(baseColumnNames: "pkg_platform_fk", baseTableName: "package", constraintName: "FK5xmwuoqqyun14gk0qdjiop3g8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "platform", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675352641051-8") {
        addForeignKeyConstraint(baseColumnNames: "idns_targettype", baseTableName: "identifier_namespace", constraintName: "FK7csknbpyn4tb2wj3jg35a0b90", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675352641051-9") {
        addForeignKeyConstraint(baseColumnNames: "pkg_provider_fk", baseTableName: "package", constraintName: "FKmxamrrttlt0bq2eu1ioesegg2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675352641051-10") {
        addForeignKeyConstraint(baseColumnNames: "plat_provider_fk", baseTableName: "platform", constraintName: "FKqcrviq4q7s9yp2tkt9lge4udd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (modified)", id: "1675352641051-11") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_platform_fk = (select combo_to_fk from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Package.NominalPlatform') and combo_from_fk = kbc_id) where pkg_platform_fk is null''')
                confirm("set pkg_platform_fk from package: ${countUpdate}")
                changeSet.setComments("set pkg_platform_fk from package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675352641051-12") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_provider_fk = (select combo_to_fk from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Package.Provider') and combo_from_fk = kbc_id) where pkg_provider_fk is null''')

                confirm("set pkg_provider_fk from package: ${countUpdate}")
                changeSet.setComments("set pkg_provider_fk from package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675352641051-13") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update platform set plat_provider_fk = (select combo_to_fk from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Platform.Provider') and combo_from_fk = kbc_id and combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Platform.Provider')) where plat_provider_fk is null''')

                confirm("set plat_provider_fk from platform: ${countUpdate}")
                changeSet.setComments("set plat_provider_fk from platform: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675352641051-14") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Package.NominalPlatform')")

                confirm("delete combos with Package.NominalPlatform type: ${countDelete}")
                changeSet.setComments("delete combos with Package.NominalPlatform type: ${countDelete}")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675352641051-15") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Package.Provider')")

                confirm("delete combos with Package.Provider' type: ${countDelete}")
                changeSet.setComments("delete combos with Package.Provider type: ${countDelete}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675352641051-16") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Platform.Provider')")

                confirm("delete combos with Platform.Provider' type: ${countDelete}")
                changeSet.setComments("delete combos with Platform.Provider type: ${countDelete}")
            }
            rollback {}
        }
    }

}

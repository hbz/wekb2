databaseChangeLog = {

/*    changeSet(author: "djebeniani (generated)", id: "1675164942032-1") {
        alterSequence(sequenceName: "hibernate_sequence")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675164942032-2") {
        dropPrimaryKey(tableName: "component_statistic")
    }

 /*   changeSet(author: "djebeniani (generated)", id: "1675164942032-3") {
        addPrimaryKey(columnNames: "cs_id", constraintName: "component_statisticPK", tableName: "component_statistic")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675164942032-4") {
        dropUniqueConstraint(constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1675164942032-5") {
        addUniqueConstraint(columnNames: "kbc_uuid", constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")
    }*/

    changeSet(author: "djebeniani (modified)", id: "1675164942032-6") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table kbcomponent_language rename to "component_language";''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-7") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_language"
    rename column kbc_lang_id to cl_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-8") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_language"
    rename column kbc_lang_version to cl_version;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-9") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_language"
    rename column kbc_lang_kbc_fk to cl_tipp_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-10") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_language"
    rename column kbc_lang_date_created to cl_date_created;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-11") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_language"
    rename column kbc_lang_last_updated to cl_last_updated;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-12") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_language"
    rename column kbc_lang_rv_fk to cl_rv_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-13") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table kbcomponent_variant_name rename to "component_variant_name";''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675164942032-14") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_statistic"
    rename column id to cs_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-15") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_statistic"
    rename column version to cs_version;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-16") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_statistic"
    rename column month to cs_month;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-17") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_statistic"
    rename column num_new to cs_num_new;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-18") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_statistic"
    rename column num_total to cs_num_total;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-19") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_statistic"
    rename column year to cs_year;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675164942032-20") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "component_statistic"
    rename column component_type to cs_component_type;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675164942032-21") {
        addPrimaryKey(columnNames: "cs_id", constraintName: "component_statisticPK", tableName: "component_statistic")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675164942032-22") {
        addForeignKeyConstraint(baseColumnNames: "idns_targettype", baseTableName: "identifier_namespace", constraintName: "FK7csknbpyn4tb2wj3jg35a0b90", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675164942032-23") {
        addForeignKeyConstraint(baseColumnNames: "cl_tipp_fk", baseTableName: "component_language", constraintName: "FKfucdvncfc6aa65cesxvcameiy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675164942032-24") {
        addForeignKeyConstraint(baseColumnNames: "tp_tipp_fk", baseTableName: "tipp_price", constraintName: "FKh6onpwjyyawuss7fe0cnqnyj9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675164942032-25") {
        dropForeignKeyConstraint(baseTableName: "component_language", constraintName: "FKdt0rixmn9cfw4jdnh57km823f")
    }

    changeSet(author: "djebeniani (generated)", id: "1675164942032-26") {
        dropForeignKeyConstraint(baseTableName: "tipp_price", constraintName: "fk55l4ot2rxq9ehfg0j3b6mcju2")
    }

}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1737559948415-1") {
        addColumn(tableName: "org") {
            column(name: "org_alert_exchange_ebook_packages", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-2") {
        addColumn(tableName: "org") {
            column(name: "org_alert_new_ebook_packages", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-3") {
        addColumn(tableName: "org") {
            column(name: "org_collections", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-4") {
        addColumn(tableName: "org") {
            column(name: "org_drm", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-5") {
        addColumn(tableName: "org") {
            column(name: "org_forwarding_usage_statistcs", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-6") {
        addColumn(tableName: "org") {
            column(name: "org_perpetual_access", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-7") {
        addColumn(tableName: "org") {
            column(name: "org_pick_and_choose", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-8") {
        addColumn(tableName: "org") {
            column(name: "org_prepaid", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-9") {
        addColumn(tableName: "org") {
            column(name: "org_print_download_chapter", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-10") {
        addColumn(tableName: "org") {
            column(name: "org_quotes_by_copy_paste", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-11") {
        addColumn(tableName: "org") {
            column(name: "org_remote_access", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-12") {
        addColumn(tableName: "org") {
            column(name: "org_temporary_access", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-13") {
        addColumn(tableName: "org") {
            column(name: "org_upfront", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-14") {
        addColumn(tableName: "org") {
            column(name: "org_url_prist_lists", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-15") {
        addColumn(tableName: "org") {
            column(name: "org_url_title_lists", type: "varchar(255)")
        }
    }


    changeSet(author: "djebeniani (generated)", id: "1737559948415-16") {
        addForeignKeyConstraint(baseColumnNames: "org_prepaid", baseTableName: "org", constraintName: "FK3s8osqio0j44apsj2rlayy8g9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }


    changeSet(author: "djebeniani (generated)", id: "1737559948415-17") {
        addForeignKeyConstraint(baseColumnNames: "org_drm", baseTableName: "org", constraintName: "FKa8fpa00g6y64vgtaowhh3uoq4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-18") {
        addForeignKeyConstraint(baseColumnNames: "org_alert_exchange_ebook_packages", baseTableName: "org", constraintName: "FKc07d6louu6t5pmmcqn0xljfmb", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-19") {
        addForeignKeyConstraint(baseColumnNames: "org_temporary_access", baseTableName: "org", constraintName: "FKc0na7pco5vg0e6iwrxyhpjayt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-20") {
        addForeignKeyConstraint(baseColumnNames: "org_alert_new_ebook_packages", baseTableName: "org", constraintName: "FKetv1pdw38dth4vf65a1abreui", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-21") {
        addForeignKeyConstraint(baseColumnNames: "org_pick_and_choose", baseTableName: "org", constraintName: "FKfmbgu8glv61qvhv51f74fts6d", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-22") {
        addForeignKeyConstraint(baseColumnNames: "org_forwarding_usage_statistcs", baseTableName: "org", constraintName: "FKfw5v381rb12vwmcwudpcm19vu", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-23") {
        addForeignKeyConstraint(baseColumnNames: "org_collections", baseTableName: "org", constraintName: "FKjs61fmfonj5j9pwurfmg4c9nw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-24") {
        addForeignKeyConstraint(baseColumnNames: "org_remote_access", baseTableName: "org", constraintName: "FKjvner4d62uhcdo5gkr78tc0p5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-25") {
        addForeignKeyConstraint(baseColumnNames: "org_perpetual_access", baseTableName: "org", constraintName: "FKmehiyimq6l8rm6165lkxgnb25", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-26") {
        addForeignKeyConstraint(baseColumnNames: "org_print_download_chapter", baseTableName: "org", constraintName: "FKo1gwfa5pnp01sd93v2u6ft7bq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-27") {
        addForeignKeyConstraint(baseColumnNames: "org_upfront", baseTableName: "org", constraintName: "FKqwc5d3xskxffnos9fuq1nhhuq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-28") {
        addForeignKeyConstraint(baseColumnNames: "org_quotes_by_copy_paste", baseTableName: "org", constraintName: "FKsqbnooxl241ej7hk646462881", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-29") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "FK7afossud20vb74bb15dcd4q7b")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-30") {
        dropColumn(columnName: "plat_title_namespace_fk", tableName: "platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1737559948415-31") {
        addColumn(tableName: "org") {
            column(name: "org_url_to_training_materials", type: "varchar(255)")
        }
    }

}

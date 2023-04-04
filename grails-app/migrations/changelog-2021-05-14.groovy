databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1620990494087-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_r3_supported_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-2") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_r4_supported_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-3") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_r4_sushi_api_supported_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-4") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_r4_sushi_server_url", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-5") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_r5_supported_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-6") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_r5_sushi_api_supported_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-7") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_r5_sushi_server_url", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-8") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_registry_url", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-9") {
        addColumn(tableName: "platform") {
            column(name: "plat_proxy_supported_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-10") {
        addColumn(tableName: "platform") {
            column(name: "plat_statistics_admin_portal_url", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-11") {
        addColumn(tableName: "platform") {
            column(name: "plat_statistics_format_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-12") {
        addColumn(tableName: "platform") {
            column(name: "plat_statistics_update_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-13") {
        addForeignKeyConstraint(baseColumnNames: "plat_counter_r4_sushi_api_supported_fk_rv", baseTableName: "platform", constraintName: "FK3tddlt7eiqg1csfvggsrdyfix", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-14") {
        addForeignKeyConstraint(baseColumnNames: "plat_statistics_format_fk_rv", baseTableName: "platform", constraintName: "FK4xdwp91kqopn621ut03k8evnj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-15") {
        addForeignKeyConstraint(baseColumnNames: "plat_counter_r4_supported_fk_rv", baseTableName: "platform", constraintName: "FK5wqlhvu4hacgbr80kugx7tven", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-16") {
        addForeignKeyConstraint(baseColumnNames: "plat_statistics_update_fk_rv", baseTableName: "platform", constraintName: "FK9bx45b1divn14ctlueayvi7km", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-17") {
        addForeignKeyConstraint(baseColumnNames: "plat_counter_r5_sushi_api_supported_fk_rv", baseTableName: "platform", constraintName: "FKd5vvnl2vp8y9osw0hlthvevf2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-18") {
        addForeignKeyConstraint(baseColumnNames: "plat_proxy_supported_fk_rv", baseTableName: "platform", constraintName: "FKkjpoj7a4vnvgpt6jwkdc196wa", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-19") {
        addForeignKeyConstraint(baseColumnNames: "plat_counter_r3_supported_fk_rv", baseTableName: "platform", constraintName: "FKqiqqq6obmm4mj9eo6nfsbxxhm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1620990494087-20") {
        addForeignKeyConstraint(baseColumnNames: "plat_counter_r5_supported_fk_rv", baseTableName: "platform", constraintName: "FKrmfnbysa12qe3d86c9ioueog4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

}

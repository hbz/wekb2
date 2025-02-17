databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1739782955163-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_forwarding_usage_statistcs_fk_rv", type: "int8")
        }
    }


    changeSet(author: "djebeniani (generated)", id: "1739782955163-2") {
        addForeignKeyConstraint(baseColumnNames: "plat_forwarding_usage_statistcs_fk_rv", baseTableName: "platform", constraintName: "FKhtxe1lyrh9fvq4g3c9cp7nu8q", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }


}

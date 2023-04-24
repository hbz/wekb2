databaseChangeLog = {


    changeSet(author: "djebeniani (generated)", id: "1682325591871-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_central_api_key", type: "text")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1682325591871-2") {
        addColumn(tableName: "platform") {
            column(name: "plat_sushi_api_authentication_method", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1682325591871-3") {
        addForeignKeyConstraint(baseColumnNames: "plat_sushi_api_authentication_method", baseTableName: "platform", constraintName: "FKsacvyosox0sjywv13x9qow11y", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }
}

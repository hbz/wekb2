databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1749052030811-1") {
        addColumn(tableName: "component_variant_name") {
            column(name: "cvn_vendor_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1749052030811-2") {
        addColumn(tableName: "vendor") {
            column(name: "ven_description", type: "text")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1749052030811-3") {
        addForeignKeyConstraint(baseColumnNames: "cvn_vendor_fk", baseTableName: "component_variant_name", constraintName: "FK9e0gbygcigq59mrrc7mmyrts8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }

}

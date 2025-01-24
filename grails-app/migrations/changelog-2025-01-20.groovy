databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1737378850222-1") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_frequency", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737378850222-2") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_last_run", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737378850222-3") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_last_udpate_url", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1737378850222-4") {
        addForeignKeyConstraint(baseColumnNames: "upi_frequency", baseTableName: "update_package_info", constraintName: "FK4otasxfl9s2xtw0qwv72xrnck", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

}

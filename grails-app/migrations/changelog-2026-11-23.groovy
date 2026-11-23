databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1795440363445-1") {
        addColumn(tableName: "tippcoverage_statement") {
            column(name: "tcs_access_typ_fk", type: "int8")
        }
    }


    changeSet(author: "djebeniani (generated)", id: "1795440363445-2") {
        addForeignKeyConstraint(baseColumnNames: "tcs_access_typ_fk", baseTableName: "tippcoverage_statement", constraintName: "FKe6u61fpxux1jvfxxromwuxqcn", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }


}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1620142607226-1") {
        createTable(tableName: "kbc_language") {
            column(name: "kbc_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "kbc_language_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620142607226-2") {
        addForeignKeyConstraint(baseColumnNames: "kbc_language_rv_fk", baseTableName: "kbc_language", constraintName: "FKf0dr9xej9hx7vctd76ddq9ofc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1620142607226-3") {
        addForeignKeyConstraint(baseColumnNames: "kbc_fk", baseTableName: "kbc_language", constraintName: "FKhalvi3dx2ens2t0okp823n36i", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1620142607226-4") {
        dropForeignKeyConstraint(baseTableName: "kbcomponent", constraintName: "FKdr92uqpirxysmd2re5v62mcay")
    }

    changeSet(author: "djebeniani (generated)", id: "1620142607226-5") {
        dropColumn(columnName: "kbc_language_rv_fk", tableName: "kbcomponent")
    }
}

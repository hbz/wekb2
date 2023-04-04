databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1621433940041-1") {
        addColumn(tableName: "package") {
            column(name: "pkg_editing_status_rv_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621433940041-2") {
        addForeignKeyConstraint(baseColumnNames: "pkg_editing_status_rv_fk", baseTableName: "package", constraintName: "FKcwtvx7kcmqpuhv6f9c1hy0vel", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1621433940041-3") {
        dropForeignKeyConstraint(baseTableName: "package", constraintName: "fkf9lm2ut0mida41nje7qn7ujv3")
    }

    changeSet(author: "djebeniani (generated)", id: "1621433940041-4") {
        dropForeignKeyConstraint(baseTableName: "kbcomponent", constraintName: "fkmiqeinwf65eto4ahilogd7dpx")
    }

    changeSet(author: "djebeniani (generated)", id: "1621433940041-5") {
        dropColumn(columnName: "edit_status_id", tableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1621433940041-6") {
        dropColumn(columnName: "pkg_list_status_rv_fk", tableName: "package")
    }
}

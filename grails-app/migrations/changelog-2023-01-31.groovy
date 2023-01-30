databaseChangeLog = {

/*    changeSet(author: "djebeniani (generated)", id: "1675091854011-1") {
        alterSequence(sequenceName: "hibernate_sequence")
    }

    changeSet(author: "djebeniani (generated)", id: "1675091854011-2") {
        dropUniqueConstraint(constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1675091854011-3") {
        addUniqueConstraint(columnNames: "kbc_uuid", constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1675091854011-4") {
        addForeignKeyConstraint(baseColumnNames: "idns_targettype", baseTableName: "identifier_namespace", constraintName: "FK7csknbpyn4tb2wj3jg35a0b90", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675091854011-5") {
        dropColumn(columnName: "provenance", tableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1675091854011-6") {
        dropColumn(columnName: "reference", tableName: "kbcomponent")
    }
}

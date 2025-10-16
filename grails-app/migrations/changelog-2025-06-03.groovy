databaseChangeLog = {


    changeSet(author: "djebeniani (generated)", id: "1748975496045-1") {
        dropForeignKeyConstraint(baseTableName: "vendor_refdata_value", constraintName: "FK2d35mpgw7bwo3gu1wvcnyu8ie")
    }

    changeSet(author: "djebeniani (generated)", id: "1748975496045-2") {
        dropForeignKeyConstraint(baseTableName: "vendor_refdata_value", constraintName: "FKov0d0pao8du4p8byuwahe40uj")
    }

    changeSet(author: "djebeniani (generated)", id: "1748975496045-3") {
        dropTable(tableName: "vendor_refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1748975496045-4") {
        dropColumn(columnName: "kbc_last_update_comment", tableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1748975496045-5") {
        dropColumn(columnName: "pkg_last_update_comment", tableName: "package")
    }


}

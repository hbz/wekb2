databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1622548040636-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_last_audit_date", type: "timestamp")
        }
    }
}

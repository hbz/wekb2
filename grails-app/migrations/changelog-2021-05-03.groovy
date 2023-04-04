databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1620039937376-1") {
        addColumn(tableName: "source") {
            column(name: "source_last_update_url", type: "varchar(255)")
        }
    }
}

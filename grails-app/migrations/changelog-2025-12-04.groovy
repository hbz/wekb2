databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1764837343397-1") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_kbart_file_hash", type: "text")
        }
    }
}

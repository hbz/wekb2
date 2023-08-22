databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1692629953388-1") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_update_url", type: "varchar(255)")
        }
    }
}

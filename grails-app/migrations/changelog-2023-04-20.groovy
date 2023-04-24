databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1681985774580-1") {
        addColumn(tableName: "org") {
            column(name: "org_abbreviated_name", type: "varchar(255)")
        }
    }

}

databaseChangeLog = {

    changeSet(author: "bluoss (generated)", id: "1733235860027-1") {
        addColumn(tableName: "refdata_value") {
            column(name: "rdv_order", type: "int8")
        }
    }
}

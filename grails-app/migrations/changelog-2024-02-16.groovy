databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1708071848028-1") {
        addColumn(tableName: "vendor") {
            column(name: "ven_prequalification_vol", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1708071848028-2") {
        addColumn(tableName: "vendor") {
            column(name: "ven_prequalification_vol_info", type: "TEXT")
        }
    }
}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1781593833318-1") {
        addColumn(tableName: "identifier_namespace") {
            column(name: "idns_description_en", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1781593833318-2") {
        addColumn(tableName: "identifier_namespace") {
            column(name: "idns_hard_data", type: "boolean") {
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1781593833318-3") {
        grailsChange {
            change {
                sql.execute('update identifier_namespace set idns_hard_data = false')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1781593833318-4") {
        addColumn(tableName: "identifier_namespace") {
            column(name: "idns_url_prefix", type: "varchar(255)")
        }
    }

}

databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1639043741494-1") {
        createIndex(indexName: "cp_currency_idx", tableName: "component_price") {
            column(name: "cp_currency_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1639043741494-2") {
        createIndex(indexName: "cp_owner_component_idx", tableName: "component_price") {
            column(name: "cp_owner_component_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1639043741494-3") {
        createIndex(indexName: "cp_type_idx", tableName: "component_price") {
            column(name: "cp_type_fk")
        }
    }

}

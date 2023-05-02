databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1683027502848-1") {
        createIndex(indexName: "tipp_date_created_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_date_created")
        }
    }

    changeSet(author: "galffy (generated)", id: "1683027502848-2") {
        createIndex(indexName: "tipp_last_updated_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_last_updated")
        }
    }

    changeSet(author: "galffy (generated)", id: "1683027502848-3") {
        createIndex(indexName: "tipp_status_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_status_rv_fk")
        }
    }

    changeSet(author: "galffy (hand-coded)", id: "1683027502848-4") {
        createIndex(indexName: "tipp_ddc_idx", tableName: "tipp_dewey_decimal_classification") {
            column(name: "tipp_fk")
            column(name: "ddc_rv_fk")
        }
    }

}

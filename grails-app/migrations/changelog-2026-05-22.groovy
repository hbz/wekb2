databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1779450974677-1") {
        createIndex(indexName: "tipp_last_updated_status_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_last_updated")

            column(name: "tipp_status_rv_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1779450974677-2") {
        createIndex(indexName: "tipp_name_status_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_name")

            column(name: "tipp_status_rv_fk")
        }
    }
}

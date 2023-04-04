databaseChangeLog = {


    changeSet(author: "djebeniani (generated)", id: "1649345474768-1") {
        dropIndex(indexName: "id_namespace_idx", tableName: "identifier")
    }

    changeSet(author: "djebeniani (generated)", id: "1649345474768-2") {
        dropIndex(indexName: "id_value_idx", tableName: "identifier")
    }

    changeSet(author: "djebeniani (generated)", id: "1649345474768-3") {
        createIndex(indexName: "id_pkg_idx", tableName: "identifier_new") {
            column(name: "id_pkg_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1649345474768-4") {
        createIndex(indexName: "id_platform_idx", tableName: "identifier_new") {
            column(name: "id_platform_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1649345474768-5") {
        createIndex(indexName: "id_tipp_idx", tableName: "identifier_new") {
            column(name: "id_tipp_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1649345474768-6") {
        createIndex(indexName: "id_value_idx", tableName: "identifier_new") {
            column(name: "id_value")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1649345474768-7") {
        createIndex(indexName: "id_namespace_idx", tableName: "identifier_new") {
            column(name: "id_namespace_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1649345474768-8") {
        createIndex(indexName: "id_org_idx", tableName: "identifier_new") {
            column(name: "id_org_fk")
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1648154097625-13") {
        grailsChange {
            change {

                sql.execute("drop table if exists identifier;")
            }
            rollback {}
        }
    }

}

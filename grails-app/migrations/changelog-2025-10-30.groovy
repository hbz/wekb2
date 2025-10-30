databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1761813015536-1") {
        createIndex(indexName: "cg_uuid_idx", tableName: "curatory_group") {
            column(name: "cg_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1761813015536-2") {
        createIndex(indexName: "jr_uuid_idx", tableName: "job_result") {
            column(name: "jr_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1761813015536-3") {
        createIndex(indexName: "ks_uuid_idx", tableName: "kbart_source") {
            column(name: "ks_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1761813015536-4") {
        createIndex(indexName: "org_uuid_idx", tableName: "org") {
            column(name: "org_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1761813015536-5") {
        createIndex(indexName: "pkg_uuid_idx", tableName: "package") {
            column(name: "pkg_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1761813015536-6") {
        createIndex(indexName: "plat_counter_registry_api_uuid_idx", tableName: "platform") {
            column(name: "plat_counter_registry_api_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1761813015536-7") {
        createIndex(indexName: "plat_uuid_idx", tableName: "platform") {
            column(name: "plat_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1761813015536-8") {
        createIndex(indexName: "tcs_uuid_idx", tableName: "tippcoverage_statement") {
            column(name: "tcs_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1761813015536-9") {
        createIndex(indexName: "tipp_uuid_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1761813015536-10") {
        createIndex(indexName: "ven_uuid_idx", tableName: "vendor") {
            column(name: "ven_uuid")
        }
    }
}

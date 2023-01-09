databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1659505824422-1") {
        createIndex(indexName: "cp_end_date_idx", tableName: "component_price") {
            column(name: "cp_end_date")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-2") {
        createIndex(indexName: "cp_start_date_idx", tableName: "component_price") {
            column(name: "cp_start_date")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-3") {
        createIndex(indexName: "tipp_access_end_date_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_access_end_date")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-4") {
        createIndex(indexName: "tipp_access_start_date_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_access_start_date")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-5") {
        createIndex(indexName: "tipp_end_date_idx", tableName: "tippcoverage_statement") {
            column(name: "tipp_end_date")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-6") {
        createIndex(indexName: "tipp_first_author_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_first_author")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-7") {
        createIndex(indexName: "tipp_last_changed_ext_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_last_change_ext")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-8") {
        createIndex(indexName: "tipp_medium_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_medium_rv_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-9") {
        createIndex(indexName: "tipp_open_access_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_open_access_rv_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-10") {
        createIndex(indexName: "tipp_owner_idx", tableName: "tippcoverage_statement") {
            column(name: "owner_id")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-11") {
        createIndex(indexName: "tipp_parent_publication_type_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_parent_publication_id")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-12") {
        createIndex(indexName: "tipp_preceding_publication_type_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_preceding_publication_id")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-13") {
        createIndex(indexName: "tipp_publication_type_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_publication_type_rv_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-14") {
        createIndex(indexName: "tipp_start_date_idx", tableName: "tippcoverage_statement") {
            column(name: "tipp_start_date")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-15") {
        createIndex(indexName: "tipp_subject_area_idx", tableName: "title_instance_package_platform") {
            column(name: "subject_area")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-16") {
        createIndex(indexName: "tipp_superseding_publication_type_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_superseding_publication_title_id")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-17") {
        createIndex(indexName: "aupi_pkg_idx", tableName: "auto_update_package_info") {
            column(name: "aupi_pkg_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-18") {
        createIndex(indexName: "aupi_start_time_idx", tableName: "auto_update_package_info") {
            column(name: "aupi_end_time")

            column(name: "aupi_start_time")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-19") {
        createIndex(indexName: "aupi_status_idx", tableName: "auto_update_package_info") {
            column(name: "aupi_status_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-20") {
        createIndex(indexName: "aupi_uuid_idx", tableName: "auto_update_package_info") {
            column(name: "aupi_uuid")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-21") {
        createIndex(indexName: "auti_aupi_idx", tableName: "auto_update_tipp_info") {
            column(name: "auti_aupi_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-22") {
        createIndex(indexName: "auti_end_time_idx", tableName: "auto_update_tipp_info") {
            column(name: "auti_end_time")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-23") {
        createIndex(indexName: "auti_kbart_property_idx", tableName: "auto_update_tipp_info") {
            column(name: "auti_kbart_property")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-24") {
        createIndex(indexName: "auti_start_time_idx", tableName: "auto_update_tipp_info") {
            column(name: "auti_start_time")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-25") {
        createIndex(indexName: "auti_status_idx", tableName: "auto_update_tipp_info") {
            column(name: "auti_status_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-26") {
        createIndex(indexName: "auti_tipp_idx", tableName: "auto_update_tipp_info") {
            column(name: "auti_tipp_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-27") {
        createIndex(indexName: "auti_tipp_property_idx", tableName: "auto_update_tipp_info") {
            column(name: "auti_tipp_property")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-28") {
        createIndex(indexName: "auti_type_idx", tableName: "auto_update_tipp_info") {
            column(name: "auti_type_fk")
        }
    }

    changeSet(author: "galffy (generated)", id: "1659505824422-29") {
        createIndex(indexName: "auti_uuid_idx", tableName: "auto_update_tipp_info") {
            column(name: "auti_uuid")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1659505824422-30") {
        createIndex(indexName: "tipp_url_idx", tableName: "title_instance_package_platform") {
            column(name: "url")
        }
    }
}

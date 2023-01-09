databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1618476753914-1") {
        createTable(tableName: "package_dewey_decimal_classification") {
            column(name: "package_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ddc_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-2") {
        createTable(tableName: "tipp_dewey_decimal_classification") {
            column(name: "tipp_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ddc_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-3") {
        addForeignKeyConstraint(baseColumnNames: "tipp_fk", baseTableName: "tipp_dewey_decimal_classification", constraintName: "FK6if9ut9kn3xdlbumqp9xne3tg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-4") {
        addForeignKeyConstraint(baseColumnNames: "ddc_rv_fk", baseTableName: "tipp_dewey_decimal_classification", constraintName: "FKcgq768wq64i4in27437mr1l7m", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-5") {
        addForeignKeyConstraint(baseColumnNames: "ddc_rv_fk", baseTableName: "package_dewey_decimal_classification", constraintName: "FKkj3d1dypkeiuyx583lr47gdf5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-6") {
        addForeignKeyConstraint(baseColumnNames: "package_fk", baseTableName: "package_dewey_decimal_classification", constraintName: "FKs2lrf2p3exmxx7kx88naycciy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-59") {
        dropIndex(indexName: "apd_prop_name_idx", tableName: "additional_property_definition")

        createIndex(indexName: "apd_prop_name_idx", tableName: "additional_property_definition") {
            column(name: "apd_prop_name")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-60") {
        dropIndex(indexName: "combo_from_idx", tableName: "combo")

        createIndex(indexName: "combo_from_idx", tableName: "combo") {
            column(name: "combo_from_fk")

            column(name: "combo_type_rv_fk")

            column(name: "combo_status_rv_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-61") {
        dropIndex(indexName: "combo_full_idx", tableName: "combo")

        createIndex(indexName: "combo_full_idx", tableName: "combo") {
            column(name: "combo_to_fk")

            column(name: "combo_from_fk")

            column(name: "combo_type_rv_fk")

            column(name: "combo_status_rv_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-62") {
        dropIndex(indexName: "combo_to_idx", tableName: "combo")

        createIndex(indexName: "combo_to_idx", tableName: "combo") {
            column(name: "combo_to_fk")

            column(name: "combo_type_rv_fk")

            column(name: "combo_status_rv_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-63") {
        dropIndex(indexName: "cvn_norm_variant_name_idx", tableName: "kbcomponent_variant_name")

        createIndex(indexName: "cvn_norm_variant_name_idx", tableName: "kbcomponent_variant_name") {
            column(name: "cvn_norm_variant_name")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-64") {
        dropIndex(indexName: "doc_description_idx", tableName: "document")

        createIndex(indexName: "doc_description_idx", tableName: "document") {
            column(name: "doc_description")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-65") {
        dropIndex(indexName: "doc_fp_contents", tableName: "document")

        createIndex(indexName: "doc_fp_contents", tableName: "document") {
            column(name: "doc_fp_contents")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-66") {
        dropIndex(indexName: "id_namespace_idx", tableName: "identifier")

        createIndex(indexName: "id_namespace_idx", tableName: "identifier") {
            column(name: "id_namespace_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-67") {
        dropIndex(indexName: "id_value_idx", tableName: "identifier")

        createIndex(indexName: "id_value_idx", tableName: "identifier") {
            column(name: "id_value")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-68") {
        dropIndex(indexName: "kbc_bucket_hash_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_bucket_hash_idx", tableName: "kbcomponent") {
            column(name: "kbc_bucket_hash")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-69") {
        dropIndex(indexName: "kbc_component_hash_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_component_hash_idx", tableName: "kbcomponent") {
            column(name: "kbc_component_hash")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-70") {
        dropIndex(indexName: "kbc_date_created_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_date_created_idx", tableName: "kbcomponent") {
            column(name: "kbc_date_created")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-71") {
        dropIndex(indexName: "kbc_last_updated_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_last_updated_idx", tableName: "kbcomponent") {
            column(name: "kbc_last_updated")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-72") {
        dropIndex(indexName: "kbc_name_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_name_idx", tableName: "kbcomponent") {
            column(name: "kbc_name")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-73") {
        dropIndex(indexName: "kbc_normname_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_normname_idx", tableName: "kbcomponent") {
            column(name: "kbc_normname")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-74") {
        dropIndex(indexName: "kbc_shortcode_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_shortcode_idx", tableName: "kbcomponent") {
            column(name: "kbc_shortcode")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-75") {
        dropIndex(indexName: "kbc_status_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_status_idx", tableName: "kbcomponent") {
            column(name: "kbc_status_rv_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-77") {
        dropIndex(indexName: "or_org_rt_idx", tableName: "org_role")

        createIndex(indexName: "or_org_rt_idx", tableName: "org_role") {
            column(name: "or_roletype_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-78") {
        dropIndex(indexName: "platform_primary_url_idx", tableName: "platform")

        createIndex(indexName: "platform_primary_url_idx", tableName: "platform") {
            column(name: "plat_primary_url")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-79") {
        dropIndex(indexName: "rdc_description_idx", tableName: "refdata_category")

        createIndex(indexName: "rdc_description_idx", tableName: "refdata_category") {
            column(name: "rdc_description")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-80") {
        dropIndex(indexName: "rdv_entry_idx", tableName: "refdata_value")

        createIndex(indexName: "rdv_entry_idx", tableName: "refdata_value") {
            column(name: "rdv_owner")

            column(name: "rdv_value")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-81") {
        dropIndex(indexName: "ti_medium_idx", tableName: "title_instance")

        createIndex(indexName: "ti_medium_idx", tableName: "title_instance") {
            column(name: "medium_id")
        }
    }

    changeSet(author: "agalffy (hand-coded)", id: "1618476753914-82") {
        grailsChange {
            change {
                sql.execute('truncate table review_request_allocation_log;')
            }
            rollback {}
        }
    }

    changeSet(author: "agalffy (hand-coded)", id: "1618476753914-83") {
        grailsChange {
            change {
                sql.execute('truncate table allocated_review_group;')
            }
            rollback {}
        }
    }

    changeSet(author: "agalffy (hand-coded)", id: "1618476753914-84") {
        grailsChange {
            change {
                sql.execute('delete from review_request;')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618476753914-85") {
        addColumn(tableName: "curatory_group") {
            column(name: "cg_type_rv_fk", type: "int8")
        }
    }

}

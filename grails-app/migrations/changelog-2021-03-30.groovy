databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1617095504908-1") {
        addColumn(tableName: "package") {
            column(name: "pkg_file", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-2") {
        addForeignKeyConstraint(baseColumnNames: "pkg_file", baseTableName: "package", constraintName: "FKdgwi2nrck0bhovlb2sju6iwfp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1617095504908-3") {
        dropForeignKeyConstraint(baseTableName: "package", constraintName: "fkajnnn4oj0lv2ek3sq3ms6squ4")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1617095504908-4") {
        dropForeignKeyConstraint(baseTableName: "package", constraintName: "fkigjskhpyi7lvpbsq1tx36bh1w")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1617095504908-5") {
        dropForeignKeyConstraint(baseTableName: "package", constraintName: "fkkt416sp4afou253it51jbsbky")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1617095504908-6") {
        dropColumn(columnName: "list_verified_date", tableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-7") {
        dropColumn(columnName: "pkg_fixed_rv_fk", tableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-8") {
        dropColumn(columnName: "pkg_global_rv_fk", tableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-9") {
        dropColumn(columnName: "pkg_list_verifier", tableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-10") {
        dropColumn(columnName: "pkg_list_verifier_user_fk", tableName: "package")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1617095504908-11") {
        addDefaultValue(columnDataType: "bigint", columnName: "act_id", defaultValueComputed: "nextval('activity_act_id_seq'::regclass)", tableName: "activity")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-12") {
        addDefaultValue(columnDataType: "bigint", columnName: "apd_id", defaultValueComputed: "nextval('additional_property_definition_apd_id_seq'::regclass)", tableName: "additional_property_definition")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-13") {
        addDefaultValue(columnDataType: "bigint", columnName: "blc_id", defaultValueComputed: "nextval('bulk_loader_config_blc_id_seq'::regclass)", tableName: "bulk_loader_config")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-14") {
        addDefaultValue(columnDataType: "bigint", columnName: "cgw_id", defaultValueComputed: "nextval('curatory_group_watch_cgw_id_seq'::regclass)", tableName: "curatory_group_watch")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-15") {
        addDefaultValue(columnDataType: "bigint", columnName: "combo_id", defaultValueComputed: "nextval('combo_combo_id_seq'::regclass)", tableName: "combo")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-16") {
        addDefaultValue(columnDataType: "bigint", columnName: "cvn_id", defaultValueComputed: "nextval('kbcomponent_variant_name_cvn_id_seq'::regclass)", tableName: "kbcomponent_variant_name")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-17") {
        dropNotNullConstraint(columnDataType: "bigint", columnName: "cvn_kbc_fk", tableName: "kbcomponent_variant_name")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-18") {
        addDefaultValue(columnDataType: "bigint", columnName: "cw_id", defaultValueComputed: "nextval('component_watch_cw_id_seq'::regclass)", tableName: "component_watch")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-19") {
        addDefaultValue(columnDataType: "bigint", columnName: "doc_id", defaultValueComputed: "nextval('document_doc_id_seq'::regclass)", tableName: "document")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-20") {
        addDefaultValue(columnDataType: "bigint", columnName: "dsn_id", defaultValueComputed: "nextval('dsnote_dsn_id_seq'::regclass)", tableName: "dsnote")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-21") {
        addDefaultValue(columnDataType: "bigint", columnName: "fe_id", defaultValueComputed: "nextval('folder_entry_fe_id_seq'::regclass)", tableName: "folder_entry")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-22") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('acl_class_id_seq'::regclass)", tableName: "acl_class")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-23") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('acl_entry_id_seq'::regclass)", tableName: "acl_entry")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-24") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('acl_object_identity_id_seq'::regclass)", tableName: "acl_object_identity")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-25") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('acl_sid_id_seq'::regclass)", tableName: "acl_sid")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-26") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('annotation_id_seq'::regclass)", tableName: "annotation")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-27") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('audit_log_id_seq'::regclass)", tableName: "audit_log")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-28") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('batch_control_id_seq'::regclass)", tableName: "batch_control")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-29") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('component_history_event_id_seq'::regclass)", tableName: "component_history_event")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-30") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('component_history_event_participant_id_seq'::regclass)", tableName: "component_history_event_participant")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-31") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('component_ingestion_source_id_seq'::regclass)", tableName: "component_ingestion_source")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-32") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('component_person_id_seq'::regclass)", tableName: "component_person")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-33") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('component_price_id_seq'::regclass)", tableName: "component_price")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-34") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('component_statistic_id_seq'::regclass)", tableName: "component_statistic")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-35") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('component_subject_id_seq'::regclass)", tableName: "component_subject")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-36") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('dsapplied_criterion_id_seq'::regclass)", tableName: "dsapplied_criterion")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-37") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('dscategory_id_seq'::regclass)", tableName: "dscategory")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-38") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('dscriterion_id_seq'::regclass)", tableName: "dscriterion")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-39") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('ftcontrol_id_seq'::regclass)", tableName: "ftcontrol")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-40") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('identifier_namespace_id_seq'::regclass)", tableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-41") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('job_result_id_seq'::regclass)", tableName: "job_result")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-42") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('party_id_seq'::regclass)", tableName: "party")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-43") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('refine_operation_id_seq'::regclass)", tableName: "refine_operation")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-44") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('registration_code_id_seq'::regclass)", tableName: "registration_code")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-45") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('tippcoverage_statement_id_seq'::regclass)", tableName: "tippcoverage_statement")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-46") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('user_organisation_membership_id_seq'::regclass)", tableName: "user_organisation_membership")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-47") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('web_hook_id_seq'::regclass)", tableName: "web_hook")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-48") {
        addDefaultValue(columnDataType: "bigint", columnName: "id", defaultValueComputed: "nextval('web_hook_endpoint_id_seq'::regclass)", tableName: "web_hook_endpoint")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-49") {
        addDefaultValue(columnDataType: "bigint", columnName: "kbc_id", defaultValueComputed: "nextval('kbcomponent_kbc_id_seq'::regclass)", tableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-50") {
        addDefaultValue(columnDataType: "bigint", columnName: "kbcap_id", defaultValueComputed: "nextval('kbcomponent_additional_property_kbcap_id_seq'::regclass)", tableName: "kbcomponent_additional_property")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-51") {
        addDefaultValue(columnDataType: "bigint", columnName: "kbd_id", defaultValueComputed: "nextval('kbdomain_info_kbd_id_seq'::regclass)", tableName: "kbdomain_info")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-52") {
        addDefaultValue(columnDataType: "bigint", columnName: "like_id", defaultValueComputed: "nextval('component_like_like_id_seq'::regclass)", tableName: "component_like")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-53") {
        addDefaultValue(columnDataType: "bigint", columnName: "nm_id", defaultValueComputed: "nextval('note_mention_nm_id_seq'::regclass)", tableName: "note_mention")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-54") {
        addDefaultValue(columnDataType: "bigint", columnName: "note_id", defaultValueComputed: "nextval('note_note_id_seq'::regclass)", tableName: "note")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-55") {
        addDefaultValue(columnDataType: "bigint", columnName: "or_id", defaultValueComputed: "nextval('org_role_or_id_seq'::regclass)", tableName: "org_role")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-56") {
        addDefaultValue(columnDataType: "bigint", columnName: "rdc_id", defaultValueComputed: "nextval('refdata_category_rdc_id_seq'::regclass)", tableName: "refdata_category")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-57") {
        addDefaultValue(columnDataType: "bigint", columnName: "rdv_id", defaultValueComputed: "nextval('refdata_value_rdv_id_seq'::regclass)", tableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-58") {
        addDefaultValue(columnDataType: "bigint", columnName: "role_id", defaultValueComputed: "nextval('role_role_id_seq'::regclass)", tableName: "role")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-59") {
        addDefaultValue(columnDataType: "bigint", columnName: "rr_id", defaultValueComputed: "nextval('review_request_rr_id_seq'::regclass)", tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-60") {
        addDefaultValue(columnDataType: "bigint", columnName: "rral_id", defaultValueComputed: "nextval('review_request_allocation_log_rral_id_seq'::regclass)", tableName: "review_request_allocation_log")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-61") {
        addDefaultValue(columnDataType: "bigint", columnName: "rule_id", defaultValueComputed: "nextval('rule_rule_id_seq'::regclass)", tableName: "rule")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-62") {
        addDefaultValue(columnDataType: "bigint", columnName: "ss_id", defaultValueComputed: "nextval('saved_search_ss_id_seq'::regclass)", tableName: "saved_search")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-63") {
        addDefaultValue(columnDataType: "bigint", columnName: "ut_id", defaultValueComputed: "nextval('update_token_ut_id_seq'::regclass)", tableName: "update_token")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-64") {
        dropIndex(indexName: "apd_prop_name_idx", tableName: "additional_property_definition")

        createIndex(indexName: "apd_prop_name_idx", tableName: "additional_property_definition") {
            column(name: "apd_prop_name")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-65") {
        dropIndex(indexName: "combo_from_idx", tableName: "combo")

        createIndex(indexName: "combo_from_idx", tableName: "combo") {
            column(name: "combo_from_fk")

            column(name: "combo_type_rv_fk")

            column(name: "combo_status_rv_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-66") {
        dropIndex(indexName: "combo_full_idx", tableName: "combo")

        createIndex(indexName: "combo_full_idx", tableName: "combo") {
            column(name: "combo_to_fk")

            column(name: "combo_from_fk")

            column(name: "combo_type_rv_fk")

            column(name: "combo_status_rv_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-67") {
        dropIndex(indexName: "combo_to_idx", tableName: "combo")

        createIndex(indexName: "combo_to_idx", tableName: "combo") {
            column(name: "combo_to_fk")

            column(name: "combo_type_rv_fk")

            column(name: "combo_status_rv_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-68") {
        dropIndex(indexName: "cvn_norm_variant_name_idx", tableName: "kbcomponent_variant_name")

        createIndex(indexName: "cvn_norm_variant_name_idx", tableName: "kbcomponent_variant_name") {
            column(name: "cvn_norm_variant_name")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-69") {
        dropIndex(indexName: "doc_description_idx", tableName: "document")

        createIndex(indexName: "doc_description_idx", tableName: "document") {
            column(name: "doc_description")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-70") {
        dropIndex(indexName: "doc_fp_contents", tableName: "document")

        createIndex(indexName: "doc_fp_contents", tableName: "document") {
            column(name: "doc_fp_contents")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-71") {
        dropIndex(indexName: "id_namespace_idx", tableName: "identifier")

        createIndex(indexName: "id_namespace_idx", tableName: "identifier") {
            column(name: "id_namespace_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-72") {
        dropIndex(indexName: "id_value_idx", tableName: "identifier")

        createIndex(indexName: "id_value_idx", tableName: "identifier") {
            column(name: "id_value")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-73") {
        dropIndex(indexName: "kbc_bucket_hash_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_bucket_hash_idx", tableName: "kbcomponent") {
            column(name: "kbc_bucket_hash")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-74") {
        dropIndex(indexName: "kbc_component_hash_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_component_hash_idx", tableName: "kbcomponent") {
            column(name: "kbc_component_hash")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-75") {
        dropIndex(indexName: "kbc_date_created_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_date_created_idx", tableName: "kbcomponent") {
            column(name: "kbc_date_created")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-76") {
        dropIndex(indexName: "kbc_last_updated_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_last_updated_idx", tableName: "kbcomponent") {
            column(name: "kbc_last_updated")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-77") {
        dropIndex(indexName: "kbc_name_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_name_idx", tableName: "kbcomponent") {
            column(name: "kbc_name")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-78") {
        dropIndex(indexName: "kbc_normname_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_normname_idx", tableName: "kbcomponent") {
            column(name: "kbc_normname")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-79") {
        dropIndex(indexName: "kbc_shortcode_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_shortcode_idx", tableName: "kbcomponent") {
            column(name: "kbc_shortcode")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-80") {
        dropIndex(indexName: "kbc_status_idx", tableName: "kbcomponent")

        createIndex(indexName: "kbc_status_idx", tableName: "kbcomponent") {
            column(name: "kbc_status_rv_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-81") {
        dropUniqueConstraint(constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")

        addUniqueConstraint(columnNames: "kbc_uuid", constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-82") {
        dropIndex(indexName: "or_org_rt_idx", tableName: "org_role")

        createIndex(indexName: "or_org_rt_idx", tableName: "org_role") {
            column(name: "or_roletype_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-83") {
        dropIndex(indexName: "platform_primary_url_idx", tableName: "platform")

        createIndex(indexName: "platform_primary_url_idx", tableName: "platform") {
            column(name: "plat_primary_url")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-84") {
        dropIndex(indexName: "rdc_description_idx", tableName: "refdata_category")

        createIndex(indexName: "rdc_description_idx", tableName: "refdata_category") {
            column(name: "rdc_description")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-85") {
        dropIndex(indexName: "rdv_entry_idx", tableName: "refdata_value")

        createIndex(indexName: "rdv_entry_idx", tableName: "refdata_value") {
            column(name: "rdv_owner")

            column(name: "rdv_value")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617095504908-86") {
        dropIndex(indexName: "ti_medium_idx", tableName: "title_instance")

        createIndex(indexName: "ti_medium_idx", tableName: "title_instance") {
            column(name: "medium_id")
        }
    }*/

    changeSet(author: "djebeniani (modified)", id: "1617095504908-87") {
        grailsChange {
            change {

                sql.execute("update package set pkg_scope_rv_fk = null where pkg_scope_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Aggregator')")
                sql.execute("update package set pkg_scope_rv_fk = null where pkg_scope_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Unknown')")
                sql.execute("update package set pkg_scope_rv_fk = null where pkg_scope_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Back File')")
                sql.execute("update package set pkg_scope_rv_fk = null where pkg_scope_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Front File')")
                sql.execute("update package set pkg_scope_rv_fk = null where pkg_scope_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Master File')")


                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Aggregator'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Unknown'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Back File'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Front File'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Scope') and rdv_value = 'Master File'")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617095504908-88") {
        grailsChange {
            change {

                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.PaymentType') and rdv_value = 'Complimentary'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.PaymentType') and rdv_value = 'Limited Promotion'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.PaymentType') and rdv_value = 'Opt Out Promotion'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.PaymentType') and rdv_value = 'Aggregator'")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617095504908-89") {
        grailsChange {
            change {

                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Fixed') and rdv_value = 'No'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Fixed') and rdv_value = 'Yes'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Fixed') and rdv_value = 'Unknown'")
                sql.execute("delete from refdata_category where rdc_description = 'Package.Fixed'")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617095504908-90") {
        grailsChange {
            change {

                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Global') and rdv_value = 'Consortium'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Global') and rdv_value = 'Regional'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Global') and rdv_value = 'Global'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Package.Global') and rdv_value = 'Other'")
                sql.execute("delete from refdata_category where rdc_description = 'Package.Global'")
            }
            rollback {}
        }
    }
}

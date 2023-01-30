databaseChangeLog = {

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-1") {
        alterSequence(sequenceName: "hibernate_sequence")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-2") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "activity", tableName: "ftcontrol", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-3") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "authority", tableName: "role", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-4") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "cgu_curatory_group_fk", tableName: "curatory_group_user", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-5") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "combo_version", tableName: "combo", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-6") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "content_type_id", tableName: "package", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-7") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "ct_date_created", tableName: "contact", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-8") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "ct_last_updated", tableName: "contact", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-9") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "ct_type_rv_fk", tableName: "contact", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-10") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "ct_version", tableName: "contact", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-11") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "cvn_version", tableName: "kbcomponent_variant_name", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-12") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "date_created", tableName: "audit_log", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-13") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "date_created", tableName: "combo", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-14") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "date_first_in_print", tableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-15") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "date_first_online", tableName: "title_instance_package_platform", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-16") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "del_kbc_component_type", tableName: "deletedkbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-17") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "del_kbc_date_created", tableName: "deletedkbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-18") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "del_kbc_last_updated", tableName: "deletedkbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-19") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "del_kbc_old_date_created", tableName: "deletedkbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-20") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "del_kbc_old_id", tableName: "deletedkbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-21") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "del_kbc_old_last_updated", tableName: "deletedkbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-22") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "del_kbc_version", tableName: "deletedkbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-23") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "domain_class_name", tableName: "ftcontrol", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-24") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "id_date_created", tableName: "identifier_new", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-25") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "id_last_updated", tableName: "identifier_new", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-26") {
        addNotNullConstraint(columnDataType: "clob", columnName: "id_uuid", tableName: "identifier_new", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-27") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "id_value", tableName: "identifier_new", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-28") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "id_version", tableName: "identifier_new", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-29") {
        addNotNullConstraint(columnDataType: "clob", columnName: "jr_description", tableName: "job_result", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-30") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "jr_end_time", tableName: "job_result", validate: "true")
    }

  /*  changeSet(author: "djebeniani (generated)", id: "1675080208934-31") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "jr_group_fk", tableName: "job_result", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-32") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "jr_linked_item_fk", tableName: "job_result", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-33") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "jr_owner_fk", tableName: "job_result", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-34") {
        addNotNullConstraint(columnDataType: "clob", columnName: "jr_result_json", tableName: "job_result", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-35") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "jr_start_time", tableName: "job_result", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-36") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "jr_status_text", tableName: "job_result", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-37") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "jr_type_rv_fk", tableName: "job_result", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-38") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "jr_uuid", tableName: "job_result", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-39") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "kbc_date_created", tableName: "kbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-40") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "kbc_lang_rv_fk", tableName: "kbcomponent_language", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-41") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "kbc_lang_version", tableName: "kbcomponent_language", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-42") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "kbc_last_updated", tableName: "kbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-43") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "kbc_version", tableName: "kbcomponent", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-44") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "last_timestamp", tableName: "ftcontrol", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-45") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "last_updated", tableName: "audit_log", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-46") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "last_updated", tableName: "combo", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-47") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "last_updated_by_id", tableName: "kbcomponent", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-48") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "owner_id", tableName: "tippcoverage_statement", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-49") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "paa_date_created", tableName: "package_archiving_agency", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-50") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "paa_last_updated", tableName: "package_archiving_agency", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-51") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "paa_version", tableName: "package_archiving_agency", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-52") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "provenance", tableName: "kbcomponent", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-53") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "publisher_name", tableName: "title_instance_package_platform", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-54") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "rdc_desc_de", tableName: "refdata_category", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-55") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "rdc_desc_en", tableName: "refdata_category", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-56") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "rdc_description", tableName: "refdata_category", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-57") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "rdc_is_hard_data", tableName: "refdata_category", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-58") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "rdc_version", tableName: "refdata_category", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-59") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "rdv_is_hard_data", tableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-60") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "rdv_owner", tableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-61") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "rdv_value", tableName: "refdata_value", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-62") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "rdv_value_de", tableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-63") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "rdv_value_en", tableName: "refdata_value", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-64") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "rdv_version", tableName: "refdata_value", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-65") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "reference", tableName: "kbcomponent", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-66") {
        addNotNullConstraint(columnDataType: "clob", columnName: "series", tableName: "title_instance_package_platform", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-67") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "source_kbart_wekb_fields", tableName: "source", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-68") {
        addNotNullConstraint(columnDataType: "clob", columnName: "subject_area", tableName: "title_instance_package_platform", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-69") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "system_component", tableName: "kbcomponent", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-70") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "tipp_access_end_date", tableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-71") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "tipp_end_date", tableName: "tippcoverage_statement", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-72") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "tipp_from_kbart_import", tableName: "title_instance_package_platform", validate: "true")
    }*/

/*
    changeSet(author: "djebeniani (generated)", id: "1675080208934-73") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "tipp_host_platform_fk", tableName: "title_instance_package_platform", validate: "true")
    }
*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-74") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "tipp_open_access_rv_fk", tableName: "title_instance_package_platform", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-75") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "tipp_pkg_fk", tableName: "title_instance_package_platform", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-76") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "upi_automatic_update", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-77") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "upi_date_created", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-78") {
        addNotNullConstraint(columnDataType: "clob", columnName: "upi_description", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-79") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "upi_kbart_has_wekb_fields", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-80") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "upi_last_updated", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-81") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "upi_pkg_fk", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-82") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "upi_start_time", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-83") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "upi_status_fk", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-84") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "upi_uuid", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-85") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "upi_version", tableName: "update_package_info", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-86") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "uti_date_created", tableName: "update_tipp_info", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-87") {
        addNotNullConstraint(columnDataType: "clob", columnName: "uti_description", tableName: "update_tipp_info", validate: "true")
    }

/*
    changeSet(author: "djebeniani (generated)", id: "1675080208934-88") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "uti_last_updated", tableName: "update_tipp_info", validate: "true")
    }
*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-89") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "uti_start_time", tableName: "update_tipp_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-90") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "uti_status_fk", tableName: "update_tipp_info", validate: "true")
    }

/*
    changeSet(author: "djebeniani (generated)", id: "1675080208934-91") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "uti_tipp_fk", tableName: "update_tipp_info", validate: "true")
    }
*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-92") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "uti_type_fk", tableName: "update_tipp_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-93") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "uti_upi_fk", tableName: "update_tipp_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-94") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "uti_uuid", tableName: "update_tipp_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-95") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "uti_version", tableName: "update_tipp_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-96") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "component_price", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-97") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "component_statistic", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-98") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "ftcontrol", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-99") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "identifier_namespace", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-100") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "job_result", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-101") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "role", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-102") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "saved_search", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-103") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "tippcoverage_statement", validate: "true")
    }

/*
    changeSet(author: "djebeniani (generated)", id: "1675080208934-104") {
        dropUniqueConstraint(constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")
    }
*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-105") {
        addUniqueConstraint(columnNames: "kbc_uuid", constraintName: "UC_KBCOMPONENTKBC_UUID_COL", tableName: "kbcomponent")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-106") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "coverage_depth_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-107") {
        addPrimaryKey(columnNames: "cgu_curatory_group_fk, cgu_user_fk", constraintName: "curatory_group_userPK", tableName: "curatory_group_user")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-108") {
        addForeignKeyConstraint(baseColumnNames: "idns_targettype", baseTableName: "identifier_namespace", constraintName: "FK7csknbpyn4tb2wj3jg35a0b90", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675080208934-109") {
        addForeignKeyConstraint(baseColumnNames: "coverage_depth_id", baseTableName: "title_instance_package_platform", constraintName: "FKitj7wgy8762mc0x1rnj6xtdwg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675080208934-110") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "FK1w7ygkxctnlnkbl2q8vftc6a5")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-111") {
        dropForeignKeyConstraint(baseTableName: "title_instance_package_platform", constraintName: "fk1w561rdj0p7qvrlo4lv8l9dss")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-112") {
        dropForeignKeyConstraint(baseTableName: "source", constraintName: "fk3q8hb3f0r1ypmq167qtydv4o3")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-113") {
        dropForeignKeyConstraint(baseTableName: "acl_object_identity", constraintName: "fk4soxn7uid8qxltqps8kewftx7")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-114") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fk53crjhlw2e45klohjtpmjcj43")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-115") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "fk582gdl314v4s9e4x00wqfy7xi")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-116") {
        dropForeignKeyConstraint(baseTableName: "note", constraintName: "fk5tlpg4thrcrmvkqdj1dxn1sk2")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-117") {
        dropForeignKeyConstraint(baseTableName: "allocated_review_group", constraintName: "fk75eexndhelxqnxl9936nhva53")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-118") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "fk82i5phebhcm9l0878c9uew5ns")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-119") {
        dropForeignKeyConstraint(baseTableName: "kbcomponent_additional_property", constraintName: "fk9ho02d1tkw971j1bdk4b1vnhk")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-120") {
        dropForeignKeyConstraint(baseTableName: "acl_entry", constraintName: "fk9r4mj8ewa904g3wivff0tb5b0")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-121") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "fkao6wkfn4tt4sdbo3s4vm1nekl")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-122") {
        dropForeignKeyConstraint(baseTableName: "update_token", constraintName: "fkb703yjmo3anmgiakeva9aja3r")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-123") {
        dropForeignKeyConstraint(baseTableName: "component_watch", constraintName: "fkbf3ai2q0l9rkyt5rb308hdpgm")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-124") {
        dropForeignKeyConstraint(baseTableName: "acl_object_identity", constraintName: "fkc06nv93ck19el45a3g1p0e58w")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-125") {
        dropForeignKeyConstraint(baseTableName: "allocated_review_group", constraintName: "fkcj24jfdsgahsfh2m8a735ajvt")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-126") {
        dropForeignKeyConstraint(baseTableName: "user_organisation_membership", constraintName: "fkcm3mmoxy32c483pks9w40pq6f")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-127") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "fkcxeabgmm9p4l4h4afrxddreay")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-128") {
        dropForeignKeyConstraint(baseTableName: "org", constraintName: "fkdc3rb4rsbsp18qgxnjdrawc4w")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-129") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fkddq4cyit5bv5iciwyk0i0f5in")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-130") {
        dropForeignKeyConstraint(baseTableName: "note", constraintName: "fkedanv1mm2wecy6r3o4tdmno2v")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-131") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fkes1slhq1sbmothegvrpocgr9e")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-132") {
        dropForeignKeyConstraint(baseTableName: "user_organisation_membership", constraintName: "fkhgpxnxo67iwwfm340kadauftb")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-133") {
        dropForeignKeyConstraint(baseTableName: "user_organisation_membership", constraintName: "fki3kwo4pasx5ucd1ncubyxhsfs")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-134") {
        dropForeignKeyConstraint(baseTableName: "acl_object_identity", constraintName: "fkikrbtok3aqlrp9wbq6slh9mcw")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-135") {
        dropForeignKeyConstraint(baseTableName: "review_request_allocation_log", constraintName: "fkiy1ny6auwemdt3v3fl7t6by4f")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-136") {
        dropForeignKeyConstraint(baseTableName: "review_request_allocation_log", constraintName: "fkj9he52a1jjiuusbahlwurcoy4")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-137") {
        dropForeignKeyConstraint(baseTableName: "acl_entry", constraintName: "fkl39t1oqikardwghegxe0wdcpt")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-138") {
        dropForeignKeyConstraint(baseTableName: "update_token", constraintName: "fkl96ipchsdicm19og5k32vifj7")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-139") {
        dropForeignKeyConstraint(baseTableName: "component_history_event_participant", constraintName: "fklnxpa3kwxxnxovgr64cc7170p")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-140") {
        dropForeignKeyConstraint(baseTableName: "title_instance_package_platform", constraintName: "fknl36couyubeiyr7ditfaeu62i")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-141") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fknpmbxucn52ulosptk44bmffkg")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-142") {
        dropForeignKeyConstraint(baseTableName: "user_organisation_membership", constraintName: "fknva6rv4mevxywko9jq5pg25ux")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-143") {
        dropForeignKeyConstraint(baseTableName: "refdata_value", constraintName: "fkpk86botisjrgyfd5aqljbwla")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-144") {
        dropForeignKeyConstraint(baseTableName: "allocated_review_group", constraintName: "fkq036vo3op6pw19gu6qulnrf1f")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-145") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fkqf3s7eahha5frt9c3mgdr22m")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-146") {
        dropForeignKeyConstraint(baseTableName: "component_watch", constraintName: "fkr9ajxq25uq9ils1ikyp7ideqe")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-147") {
        dropForeignKeyConstraint(baseTableName: "title_instance_package_platform", constraintName: "fkrofja4rb7lhlfqp8eae1dm9qh")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-148") {
        dropForeignKeyConstraint(baseTableName: "kbcomponent_additional_property", constraintName: "fksukhkkysu31ylcj1nq63pqhl3")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-149") {
        dropForeignKeyConstraint(baseTableName: "title_instance_package_platform", constraintName: "fksxt9j6270a5mt3vehbghjtxtb")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-150") {
        dropForeignKeyConstraint(baseTableName: "component_history_event_participant", constraintName: "fkte2wkwjuhuyeadf4g22hp6wme")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-151") {
        dropUniqueConstraint(constraintName: "uk1781b9a084dff171b580608b3640", tableName: "acl_sid")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-152") {
        dropUniqueConstraint(constraintName: "uk56103a82abb455394f8c97a95587", tableName: "acl_object_identity")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-153") {
        dropUniqueConstraint(constraintName: "uk_iy7ua5fso3il3u3ymoc4uf35w", tableName: "acl_class")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-154") {
        dropUniqueConstraint(constraintName: "ukce200ed06800e5a163c6ab6c0c85", tableName: "acl_entry")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-155") {
        dropUniqueConstraint(constraintName: "unique_identifier_namespace", tableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-156") {
        dropTable(tableName: "acl_class")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-157") {
        dropTable(tableName: "acl_entry")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-158") {
        dropTable(tableName: "acl_object_identity")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-159") {
        dropTable(tableName: "acl_sid")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-160") {
        dropTable(tableName: "additional_property_definition")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-161") {
        dropTable(tableName: "allocated_review_group")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-162") {
        dropTable(tableName: "auto_update_package_info")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-163") {
        dropTable(tableName: "auto_update_tipp_info")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-164") {
        dropTable(tableName: "book_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-165") {
        dropTable(tableName: "component_history_event")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-166") {
        dropTable(tableName: "component_history_event_participant")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-167") {
        dropTable(tableName: "component_watch")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-168") {
        dropTable(tableName: "database_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-169") {
        dropTable(tableName: "journal_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-170") {
        dropTable(tableName: "kbcomponent_additional_property")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-171") {
        dropTable(tableName: "note")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-172") {
        dropTable(tableName: "other_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-173") {
        dropTable(tableName: "registration_code")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-174") {
        dropTable(tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-175") {
        dropTable(tableName: "review_request_allocation_log")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-176") {
        dropTable(tableName: "title_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-177") {
        dropTable(tableName: "update_token")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-178") {
        dropTable(tableName: "user_organisation_membership")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-179") {
        dropColumn(columnName: "contextual_notes", tableName: "source")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-180") {
        dropColumn(columnName: "default_accessurl", tableName: "source")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-181") {
        dropColumn(columnName: "explanation_at_source", tableName: "source")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-182") {
        dropColumn(columnName: "ezb_match", tableName: "source")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-183") {
        dropColumn(columnName: "package_namespace_id", tableName: "org")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-184") {
        dropColumn(columnName: "plat_authentication_fk_rv", tableName: "platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-185") {
        dropColumn(columnName: "rdv_use_instead", tableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-186") {
        dropColumn(columnName: "responsible_party_id", tableName: "source")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-187") {
        dropColumn(columnName: "source_ruleset", tableName: "source")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-188") {
        dropColumn(columnName: "tipp_coverage_depth", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-189") {
        dropColumn(columnName: "tipp_coverage_note", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-190") {
        dropColumn(columnName: "tipp_delayed_oa", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-191") {
        dropColumn(columnName: "tipp_hybrid_oa", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-192") {
        dropColumn(columnName: "tipp_hybrid_oa_url", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-193") {
        dropColumn(columnName: "tipp_primary", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1675080208934-194") {
        dropColumn(columnName: "zdb_match", tableName: "source")
    }
}

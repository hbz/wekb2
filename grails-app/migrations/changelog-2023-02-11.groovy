databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1675787881561-1") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table identifier_new rename to "identifier";''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-2") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "cg_date_created", tableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-3") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "cg_last_updated", tableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-4") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "cg_name", tableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-5") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "cg_status_rv_fk", tableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-6") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "cg_uuid", tableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-7") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "cg_version", tableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-8") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "org_date_created", tableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-9") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "org_last_updated", tableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-10") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "org_name", tableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-11") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "org_status_rv_fk", tableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-12") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "org_uuid", tableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-13") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "org_version", tableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-14") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "pkg_date_created", tableName: "package", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675787881561-15") {
        addNotNullConstraint(columnDataType: "clob", columnName: "pkg_description", tableName: "package", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675787881561-16") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "pkg_last_update_comment", tableName: "package", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675787881561-17") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "pkg_last_updated", tableName: "package", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-18") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "pkg_name", tableName: "package", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675787881561-19") {
        addNotNullConstraint(columnDataType: "clob", columnName: "pkg_normname", tableName: "package", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675787881561-20") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "pkg_status_rv_fk", tableName: "package", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-21") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "pkg_uuid", tableName: "package", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-22") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "pkg_version", tableName: "package", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-23") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "plat_date_created", tableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-24") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "plat_last_updated", tableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-25") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "plat_name", tableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-26") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "plat_status_rv_fk", tableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-27") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "plat_uuid", tableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-28") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "plat_version", tableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-29") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "tipp_date_created", tableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-30") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "tipp_last_updated", tableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-31") {
        addNotNullConstraint(columnDataType: "clob", columnName: "tipp_name", tableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-32") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "tipp_status_rv_fk", tableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-33") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "tipp_uuid", tableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-34") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "tipp_version", tableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-35") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "identifier_namespace"
    rename column id to idns_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-36") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "identifier_namespace"
    rename column version to idns_version;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-37") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column id to tcs_id;''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675787881561-38") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column version to tcs_version;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-39") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column owner_id to tcs_tipp_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-40") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_start_date to tcs_start_date;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-41") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_start_volume to tcs_start_volume;''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675787881561-42") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_start_issue to tcs_start_issue;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-43") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_end_date to tcs_end_date;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-44") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_end_volume to tcs_end_volume;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-45") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_end_issue to tcs_end_issue;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-46") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_embargo to tcs_embargo;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-47") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_coverage_note to tcs_note;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-48") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_coverage_depth to tcs_depth;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-49") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_coverage_date_created to tcs_date_created;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-50") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tippcoverage_statement"
    rename column tipp_coverage_last_updated to tcs_last_updated;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-51") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "title_instance_package_platform"
    rename column subject_area to tipp_subject_area;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-52") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "title_instance_package_platform"
    rename column series to tipp_series;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-53") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "title_instance_package_platform"
    rename column url to tipp_url;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-54") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "title_instance_package_platform"
    rename column publisher_name to tipp_publisher_name;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-55") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "title_instance_package_platform"
    rename column date_first_online to tipp_date_first_online;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-56") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "title_instance_package_platform"
    rename column date_first_in_print to tipp_date_first_in_print;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-57") {
        dropIndex(indexName: "tipp_subject_area_idx", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-58") {
        createIndex(indexName: "tipp_subject_area_idx", tableName: "title_instance_package_platform", unique: "false") {
            column(name: "tipp_subject_area")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-59") {
        dropIndex(indexName: "tipp_url_idx", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-60") {
        createIndex(indexName: "tipp_url_idx", tableName: "title_instance_package_platform", unique: "false") {
            column(name: "tipp_url")
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-61") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "saved_search"
    rename column version to ss_version;''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (generated)", id: "1675787881561-62") {
        addColumn(tableName: "tippcoverage_statement") {
            column(name: "tcs_uuid", type: "varchar(255)") {
            }
        }
    }

/*
    changeSet(author: "djebeniani (generated)", id: "1675787881561-63") {
        createIndex(indexName: "id_namespace_idx", tableName: "identifier") {
            column(name: "id_namespace_fk")
        }
    }
*/

/*    changeSet(author: "djebeniani (generated)", id: "1675787881561-64") {
        createIndex(indexName: "id_org_idx", tableName: "identifier") {
            column(name: "id_org_fk")
        }
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675787881561-65") {
        createIndex(indexName: "id_pkg_idx", tableName: "identifier") {
            column(name: "id_pkg_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-66") {
        createIndex(indexName: "id_platform_idx", tableName: "identifier") {
            column(name: "id_platform_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-67") {
        createIndex(indexName: "id_tipp_idx", tableName: "identifier") {
            column(name: "id_tipp_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-68") {
        createIndex(indexName: "id_uuid_idx", tableName: "identifier") {
            column(name: "id_uuid")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-69") {
        createIndex(indexName: "id_value_idx", tableName: "identifier") {
            column(name: "id_value")
        }
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675787881561-70") {
        createIndex(indexName: "pkg_normname_idx", tableName: "package") {
            column(name: "pkg_normname")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-71") {
        createIndex(indexName: "tcs_end_date_idx", tableName: "tippcoverage_statement") {
            column(name: "tcs_end_date")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-72") {
        createIndex(indexName: "tcs_start_date_idx", tableName: "tippcoverage_statement") {
            column(name: "tcs_start_date")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-73") {
        createIndex(indexName: "tcs_tipp_idx", tableName: "tippcoverage_statement") {
            column(name: "tcs_tipp_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-74") {
        createIndex(indexName: "tipp_name_idx", tableName: "title_instance_package_platform") {
            column(name: "tipp_name")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-75") {
        addForeignKeyConstraint(baseColumnNames: "tcs_depth", baseTableName: "tippcoverage_statement", constraintName: "FK28wvot4u30nnnhjx8aan6agt9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-76") {
        addForeignKeyConstraint(baseColumnNames: "id_platform_fk", baseTableName: "identifier", constraintName: "FK3jtenxbnxr3vad43xsenusfid", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "plat_id", referencedTableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-77") {
        addForeignKeyConstraint(baseColumnNames: "id_namespace_fk", baseTableName: "identifier", constraintName: "FK408vsgum5mrg1kfy18dmyxs6e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "idns_id", referencedTableName: "identifier_namespace", validate: "true")
    }

/*
    changeSet(author: "djebeniani (generated)", id: "1675787881561-78") {
        addForeignKeyConstraint(baseColumnNames: "plat_title_namespace_fk", baseTableName: "platform", constraintName: "FK7afossud20vb74bb15dcd4q7b", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "idns_id", referencedTableName: "identifier_namespace", validate: "true")
    }
*/

/*    changeSet(author: "djebeniani (generated)", id: "1675787881561-79") {
        addForeignKeyConstraint(baseColumnNames: "idns_targettype", baseTableName: "identifier_namespace", constraintName: "FK7csknbpyn4tb2wj3jg35a0b90", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675787881561-80") {
        addForeignKeyConstraint(baseColumnNames: "id_pkg_fk", baseTableName: "identifier", constraintName: "FK8gkhy8r42vkx4eq9fr2tmwjpp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-81") {
        addForeignKeyConstraint(baseColumnNames: "id_org_fk", baseTableName: "identifier", constraintName: "FKcbgi25k5i9lh52wkdvg5dt5k0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-82") {
        addForeignKeyConstraint(baseColumnNames: "id_tipp_fk", baseTableName: "identifier", constraintName: "FKfarw90eqawhlnbp0y1tscl8d3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-83") {
        addForeignKeyConstraint(baseColumnNames: "ks_target_namespace_fk", baseTableName: "kbart_source", constraintName: "FKgiwru7qiv20dura4wo9j8slh7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "idns_id", referencedTableName: "identifier_namespace", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675787881561-84") {
        addForeignKeyConstraint(baseColumnNames: "tcs_fk", baseTableName: "tippcoverage_statement", constraintName: "FKh5vxit43llkkxjxih8fp6tnng", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675787881561-85") {
        dropForeignKeyConstraint(baseTableName: "package", constraintName: "FKcwtvx7kcmqpuhv6f9c1hy0vel")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-86") {
        dropForeignKeyConstraint(baseTableName: "component_variant_name", constraintName: "fkep5tfgihadbepc1ibi9xce12r")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-87") {
        dropColumn(columnName: "cvn_kbc_fk", tableName: "component_variant_name")
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-88") {
        dropColumn(columnName: "pkg_editing_status_rv_fk", tableName: "package")
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-89") {
        grailsChange {
            change {
                sql.executeUpdate('''update tippcoverage_statement set tcs_uuid = (select gen_random_uuid()) where tcs_uuid is null;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675787881561-90") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "tcs_uuid", tableName: "tippcoverage_statement", validate: "true")
    }


    changeSet(author: "djebeniani (modified)", id: "1675787881561-91") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update component_variant_name set cvn_type_rv_fk = null where cvn_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'KBComponentVariantName.VariantType') and rdv_value = 'Authorized')''')
                confirm("set cvn_type_rv_fk from component_variant_name: ${countUpdate}")
                changeSet.setComments("set cvn_type_rv_fk from component_variant_name: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-92") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update component_variant_name set cvn_type_rv_fk = null where cvn_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'KBComponentVariantName.VariantType') and rdv_value = 'Minor Change')''')
                confirm("set cvn_type_rv_fk from component_variant_name: ${countUpdate}")
                changeSet.setComments("set cvn_type_rv_fk from component_variant_name: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-93") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update component_variant_name set cvn_type_rv_fk = null where cvn_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'KBComponentVariantName.VariantType') and rdv_value = 'Misspelling')''')
                confirm("set cvn_type_rv_fk from component_variant_name: ${countUpdate}")
                changeSet.setComments("set cvn_type_rv_fk from component_variant_name: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-94") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update component_variant_name set cvn_type_rv_fk = null where cvn_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'KBComponentVariantName.VariantType') and rdv_value = 'Nickname')''')
                confirm("set cvn_type_rv_fk from component_variant_name: ${countUpdate}")
                changeSet.setComments("set cvn_type_rv_fk from component_variant_name: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-95") {
        grailsChange {
            change {

                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'KBComponentVariantName.VariantType') and rdv_value = 'Authorized'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'KBComponentVariantName.VariantType') and rdv_value = 'Minor Change'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'KBComponentVariantName.VariantType') and rdv_value = 'Misspelling'")
                sql.execute("delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'KBComponentVariantName.VariantType') and rdv_value = 'Nickname'")

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-96") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from identifier where id_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_pkg_fk is null))''')
                confirm("delete identifier where (tipp_pkg_fk is null): ${countUpdate}")
                changeSet.setComments("delete identifier where (tipp_pkg_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }
    changeSet(author: "djebeniani (modified)", id: "1675787881561-97") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from tippcoverage_statement where tcs_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_pkg_fk is null))''')
                confirm("delete tippcoverage_statement where (tipp_pkg_fk is null): ${countUpdate}")
                changeSet.setComments("delete tippcoverage_statement where (tipp_pkg_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675787881561-98") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from component_language where cl_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_pkg_fk is null))''')
                confirm("delete component_language where (tipp_pkg_fk is null): ${countUpdate}")
                changeSet.setComments("delete component_language where (tipp_pkg_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-99") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from tipp_price where tp_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_pkg_fk is null))''')
                confirm("delete tipp_price where (tipp_pkg_fk is null): ${countUpdate}")
                changeSet.setComments("delete tipp_price where (tipp_pkg_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-100") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from update_tipp_info where uti_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_pkg_fk is null))''')
                confirm("delete update_tipp_info where (tipp_pkg_fk is null): ${countUpdate}")
                changeSet.setComments("delete update_tipp_info where (tipp_pkg_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-101") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from identifier where id_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_host_platform_fk is null))''')
                confirm("delete identifier where (tipp_host_platform_fk is null): ${countUpdate}")
                changeSet.setComments("delete identifier where (tipp_host_platform_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }
    changeSet(author: "djebeniani (modified)", id: "1675787881561-102") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from tippcoverage_statement where tcs_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_host_platform_fk is null))''')
                confirm("delete tippcoverage_statement where (tipp_host_platform_fk is null): ${countUpdate}")
                changeSet.setComments("delete tippcoverage_statement where (tipp_host_platform_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675787881561-103") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from component_language where cl_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_host_platform_fk is null))''')
                confirm("delete component_language where (tipp_host_platform_fk is null): ${countUpdate}")
                changeSet.setComments("delete component_language where (tipp_host_platform_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-104") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from tipp_price where tp_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_host_platform_fk is null))''')
                confirm("delete tipp_price where (tipp_host_platform_fk is null): ${countUpdate}")
                changeSet.setComments("delete tipp_price where (tipp_host_platform_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-105") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from update_tipp_info where uti_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_host_platform_fk is null))''')
                confirm("delete update_tipp_info where (tipp_host_platform_fk is null): ${countUpdate}")
                changeSet.setComments("delete update_tipp_info where (tipp_host_platform_fk is null): ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-106") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from identifier where id_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_url is null))''')
                confirm("delete identifier where (tipp_url is null): ${countUpdate}")
                changeSet.setComments("delete identifier where (tipp_url is null): ${countUpdate}")
            }
            rollback {}
        }
    }
    changeSet(author: "djebeniani (modified)", id: "1675787881561-107") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from tippcoverage_statement where tcs_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_url is null))''')
                confirm("delete tippcoverage_statement where (tipp_url is null): ${countUpdate}")
                changeSet.setComments("delete tippcoverage_statement where (tipp_url is null): ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675787881561-108") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from component_language where cl_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_url is null))''')
                confirm("delete component_language where (tipp_url is null): ${countUpdate}")
                changeSet.setComments("delete component_language where (tipp_url is null): ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-109") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from tipp_price where tp_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_url is null))''')
                confirm("delete tipp_price where (tipp_url is null): ${countUpdate}")
                changeSet.setComments("delete tipp_price where (tipp_url is null): ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-110") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from update_tipp_info where uti_tipp_fk in (select tipp_id from title_instance_package_platform where (tipp_url is null))''')
                confirm("delete update_tipp_info where (tipp_url is null): ${countUpdate}")
                changeSet.setComments("delete update_tipp_info where (tipp_url is null): ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675787881561-111") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from title_instance_package_platform where tipp_pkg_fk is null''')
                confirm("delete title_instance_package_platform where tipp_pkg_fk is null: ${countUpdate}")
                changeSet.setComments("delete title_instance_package_platform where tipp_pkg_fk is null: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-112") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from title_instance_package_platform where tipp_host_platform_fk is null''')
                confirm("delete title_instance_package_platform where tipp_host_platform_fk is null: ${countUpdate}")
                changeSet.setComments("delete title_instance_package_platform where tipp_host_platform_fk is null: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675787881561-113") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''delete from title_instance_package_platform where tipp_url is null''')
                confirm("delete title_instance_package_platform where tipp_url is null: ${countUpdate}")
                changeSet.setComments("delete title_instance_package_platform where tipp_url is null: ${countUpdate}")
            }
            rollback {}
        }
    }
}

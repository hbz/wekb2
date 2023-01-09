databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1665995678506-1") {
        createTable(tableName: "update_package_info") {
            column(autoIncrement: "true", name: "upi_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "update_package_infoPK")
            }

            column(name: "upi_version", type: "BIGINT")

            column(name: "upi_count_new_tipps", type: "INT")

            column(name: "upi_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "upi_end_time", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "upi_uuid", type: "VARCHAR(255)")

            column(name: "upi_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "upi_count_changed_tipps", type: "INT")

            column(name: "upi_pkg_fk", type: "BIGINT")

            column(name: "upi_only_rows_with_last_changed", type: "BOOLEAN")

            column(name: "upi_count_now_tipps", type: "INT")

            column(name: "upi_count_previously_tipps", type: "INT")

            column(name: "upi_count_removed_tipps", type: "INT")

            column(name: "upi_count_processed_kbart_rows", type: "INT")

            column(name: "upi_start_time", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "upi_count_invalid_tipps", type: "INT")

            column(name: "upi_status_fk", type: "BIGINT")

            column(name: "upi_last_changed_in_kbart", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "upi_kbart_has_wekb_fields", type: "BOOLEAN")

            column(name: "upi_count_kbart_rows", type: "INT")

            column(name: "upi_description", type: "TEXT")

            column(name: "upi_automatic_update", type: "BOOLEAN")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-2") {
        createTable(tableName: "update_tipp_info") {
            column(autoIncrement: "true", name: "uti_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "update_tipp_infoPK")
            }

            column(name: "uti_version", type: "BIGINT")

            column(name: "uti_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "uti_end_time", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "uti_uuid", type: "VARCHAR(255)")

            column(name: "uti_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "uti_new_value", type: "TEXT")

            column(name: "uti_kbart_property", type: "VARCHAR(255)")

            column(name: "uti_tipp_property", type: "VARCHAR(255)")

            column(name: "uti_upi_fk", type: "BIGINT")

            column(name: "uti_tipp_fk", type: "BIGINT")

            column(name: "uti_start_time", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "uti_type_fk", type: "BIGINT")

            column(name: "uti_status_fk", type: "BIGINT")

            column(name: "uti_old_value", type: "TEXT")

            column(name: "uti_description", type: "TEXT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-3") {
        createIndex(indexName: "upi_pkg_idx", tableName: "update_package_info") {
            column(name: "upi_pkg_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-4") {
        createIndex(indexName: "upi_start_time_idx", tableName: "update_package_info") {
            column(name: "upi_end_time")

            column(name: "upi_start_time")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-5") {
        createIndex(indexName: "upi_status_idx", tableName: "update_package_info") {
            column(name: "upi_status_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-6") {
        createIndex(indexName: "upi_uuid_idx", tableName: "update_package_info") {
            column(name: "upi_uuid")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-7") {
        createIndex(indexName: "uti_upi_idx", tableName: "update_tipp_info") {
            column(name: "uti_upi_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-8") {
        createIndex(indexName: "uti_end_time_idx", tableName: "update_tipp_info") {
            column(name: "uti_end_time")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-9") {
        createIndex(indexName: "uti_kbart_property_idx", tableName: "update_tipp_info") {
            column(name: "uti_kbart_property")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-10") {
        createIndex(indexName: "uti_start_time_idx", tableName: "update_tipp_info") {
            column(name: "uti_start_time")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-11") {
        createIndex(indexName: "uti_status_idx", tableName: "update_tipp_info") {
            column(name: "uti_status_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-12") {
        createIndex(indexName: "uti_tipp_idx", tableName: "update_tipp_info") {
            column(name: "uti_tipp_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-13") {
        createIndex(indexName: "uti_tipp_property_idx", tableName: "update_tipp_info") {
            column(name: "uti_tipp_property")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-14") {
        createIndex(indexName: "uti_type_idx", tableName: "update_tipp_info") {
            column(name: "uti_type_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-15") {
        createIndex(indexName: "uti_uuid_idx", tableName: "update_tipp_info") {
            column(name: "uti_uuid")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-16") {
        addForeignKeyConstraint(baseColumnNames: "uti_tipp_fk", baseTableName: "update_tipp_info", constraintName: "FKc22x4csc0cbqx9h8903vky6gg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-17") {
        addForeignKeyConstraint(baseColumnNames: "uti_upi_fk", baseTableName: "update_tipp_info", constraintName: "FKe1ixky734h1kqvova96y01ql4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "upi_id", referencedTableName: "update_package_info")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-18") {
        addForeignKeyConstraint(baseColumnNames: "upi_status_fk", baseTableName: "update_package_info", constraintName: "FKgg7c40esyv8nv6khuscyfcy6f", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-19") {
        addForeignKeyConstraint(baseColumnNames: "upi_pkg_fk", baseTableName: "update_package_info", constraintName: "FKqdhd3y5x2guk6aw8lx8iajpor", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-20") {
        addForeignKeyConstraint(baseColumnNames: "uti_type_fk", baseTableName: "update_tipp_info", constraintName: "FKt1vqs8cfqiel1rslo93ugrm7y", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-21") {
        addForeignKeyConstraint(baseColumnNames: "uti_status_fk", baseTableName: "update_tipp_info", constraintName: "FKte526hiip501ub1iooyu7qonl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-22") {
        dropForeignKeyConstraint(baseTableName: "auto_update_package_info", constraintName: "FK5r58teu4rnq1i6kx4e7ukf6ur")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-23") {
        dropForeignKeyConstraint(baseTableName: "auto_update_tipp_info", constraintName: "FK7qtbw6agcpnwo4du60pdyc0oe")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-24") {
        dropForeignKeyConstraint(baseTableName: "auto_update_tipp_info", constraintName: "FKee41takhwcs9h8dtyv63p846l")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-25") {
        dropForeignKeyConstraint(baseTableName: "auto_update_package_info", constraintName: "FKip6fdxpwvteqwglh1dqkq5i8n")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-26") {
        dropForeignKeyConstraint(baseTableName: "auto_update_tipp_info", constraintName: "FKmftinjtqsn9bqrjg1uhro84ga")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-27") {
        dropForeignKeyConstraint(baseTableName: "auto_update_tipp_info", constraintName: "FKmugs843gd2os8jiboi3xmb6da")
    }

    changeSet(author: "djebeniani (modified)", id: "1665995678506-28") {
        grailsChange {
            change {
                sql.executeUpdate('''INSERT INTO update_package_info (upi_id, 
    upi_version,
    upi_count_new_tipps,
    upi_date_created,
    upi_end_time,
    upi_uuid,
    upi_last_updated,
    upi_count_changed_tipps,
    upi_pkg_fk,
    upi_only_rows_with_last_changed,
    upi_count_removed_tipps,
    upi_count_processed_kbart_rows,
    upi_start_time,
    upi_count_invalid_tipps,
    upi_status_fk,
    upi_count_kbart_rows,
    upi_description,
    upi_kbart_has_wekb_fields,
    upi_count_now_tipps,
    upi_count_previously_tipps,
    upi_last_changed_in_kbart) 
    SELECT aupi_id, 
    aupi_version,
    aupi_count_new_tipps,
    aupi_date_created,
    aupi_end_time,
    aupi_uuid,
    aupi_last_updated,
    aupi_count_changed_tipps,
    aupi_pkg_fk,
    aupi_only_rows_with_last_changed,
    aupi_count_removed_tipps,
    aupi_count_processed_kbart_rows,
    aupi_start_time,
    aupi_count_invalid_tipps,
    aupi_status_fk,
    aupi_count_kbart_rows,
    aupi_description,
    aupi_kbart_has_wekb_fields,
    aupi_count_now_tipps,
    aupi_count_previously_tipps,
    aupi_last_changed_in_kbart FROM auto_update_package_info; ''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1665995678506-29") {
        grailsChange {
            change {
                sql.executeUpdate('''INSERT INTO update_tipp_info (uti_id,
    uti_version,
    uti_date_created,
    uti_end_time,
    uti_uuid,
    uti_last_updated,
    uti_new_value,
    uti_kbart_property,
    uti_tipp_property,
    uti_upi_fk,
    uti_tipp_fk,
    uti_start_time,
    uti_type_fk,
    uti_status_fk,
    uti_old_value,
    uti_description)
    SELECT auti_id,
    auti_version,
    auti_date_created,
    auti_end_time,
    auti_uuid,
    auti_last_updated,
    auti_new_value,
    auti_kbart_property,
    auti_tipp_property,
    auti_aupi_fk,
    auti_tipp_fk,
    auti_start_time,
    auti_type_fk,
    auti_status_fk,
    auti_old_value,
    auti_description FROM auto_update_tipp_info; ''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1665995678506-30") {
        grailsChange {
            change {
                sql.executeUpdate('''update update_package_info set upi_automatic_update = true where upi_automatic_update is null; ''')
            }
            rollback {}
        }
    }



    changeSet(author: "djebeniani (modified)", id: "1665995678506-31") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'Update.Status' where rdc_description = 'AutoUpdate.Status'; ''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1665995678506-32") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'Update.Type' where rdc_description = 'AutoUpdate.Type'; ''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1665995678506-33") {
        grailsChange {
            change {
                sql.executeUpdate('''update identifier_namespace set idns_name = 'Provider_Product_ID' where idns_name = 'Anbieter_Produkt_ID'; ''')
            }
            rollback {}
        }
    }

/*    changeSet(author: "djebeniani (generated)", id: "1665995678506-33") {
        dropTable(tableName: "auto_update_package_info")
    }

    changeSet(author: "djebeniani (generated)", id: "1665995678506-34") {
        dropTable(tableName: "auto_update_tipp_info")
    }*/
}

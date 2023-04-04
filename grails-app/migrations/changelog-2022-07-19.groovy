databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1658750304330-1") {
        createTable(tableName: "auto_update_package_info") {
            column(autoIncrement: "true", name: "aupi_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "auto_update_package_infoPK")
            }

            column(name: "aupi_version", type: "BIGINT")

            column(name: "aupi_count_new_tipps", type: "INT")

            column(name: "aupi_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "aupi_end_time", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "aupi_uuid", type: "VARCHAR(255)")

            column(name: "aupi_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "aupi_count_changed_tipps", type: "INT")

            column(name: "aupi_pkg_fk", type: "BIGINT")

            column(name: "aupi_only_rows_with_last_changed", type: "BOOLEAN")

            column(name: "aupi_count_removed_tipps", type: "INT")

            column(name: "aupi_count_processed_kbart_rows", type: "INT")

            column(name: "aupi_start_time", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "aupi_count_invalid_tipps", type: "INT")

            column(name: "aupi_status_fk", type: "BIGINT")

            column(name: "aupi_count_kbart_rows", type: "INT")

            column(name: "aupi_description", type: "TEXT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1658750304330-2") {
        createTable(tableName: "auto_update_tipp_info") {
            column(autoIncrement: "true", name: "auti_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "auto_update_tipp_infoPK")
            }

            column(name: "auti_version", type: "BIGINT")

            column(name: "auti_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "auti_end_time", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "auti_uuid", type: "VARCHAR(255)")

            column(name: "auti_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "auti_new_value", type: "TEXT")

            column(name: "auti_kbart_property", type: "VARCHAR(255)")

            column(name: "auti_tipp_property", type: "VARCHAR(255)")

            column(name: "auti_aupi_fk", type: "BIGINT")

            column(name: "auti_tipp_fk", type: "BIGINT")

            column(name: "auti_start_time", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "auti_type_fk", type: "BIGINT")

            column(name: "auti_status_fk", type: "BIGINT")

            column(name: "auti_old_value", type: "TEXT")

            column(name: "auti_description", type: "TEXT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1658750304330-3") {
        addForeignKeyConstraint(baseColumnNames: "aupi_status_fk", baseTableName: "auto_update_package_info", constraintName: "FK5r58teu4rnq1i6kx4e7ukf6ur", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1658750304330-4") {
        addForeignKeyConstraint(baseColumnNames: "auti_aupi_fk", baseTableName: "auto_update_tipp_info", constraintName: "FK7qtbw6agcpnwo4du60pdyc0oe", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "aupi_id", referencedTableName: "auto_update_package_info")
    }

    changeSet(author: "djebeniani (generated)", id: "1658750304330-5") {
        addForeignKeyConstraint(baseColumnNames: "auti_tipp_fk", baseTableName: "auto_update_tipp_info", constraintName: "FKee41takhwcs9h8dtyv63p846l", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1658750304330-6") {
        addForeignKeyConstraint(baseColumnNames: "aupi_pkg_fk", baseTableName: "auto_update_package_info", constraintName: "FKip6fdxpwvteqwglh1dqkq5i8n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1658750304330-7") {
        addForeignKeyConstraint(baseColumnNames: "auti_status_fk", baseTableName: "auto_update_tipp_info", constraintName: "FKmftinjtqsn9bqrjg1uhro84ga", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1658750304330-8") {
        addForeignKeyConstraint(baseColumnNames: "auti_type_fk", baseTableName: "auto_update_tipp_info", constraintName: "FKmugs843gd2os8jiboi3xmb6da", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }
}

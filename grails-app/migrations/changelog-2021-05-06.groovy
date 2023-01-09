databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1620300593203-1") {
        createTable(tableName: "deletedkbcomponent") {
            column(autoIncrement: "true", name: "del_kbc_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "deletedkbcomponentPK")
            }

            column(name: "del_kbc_version", type: "BIGINT")

            column(name: "del_kbc_old_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "del_kbc_old_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "del_kbc_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "del_kbc_uuid", type: "TEXT")

            column(name: "del_kbc_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "del_kbc_name", type: "TEXT")

            column(name: "del_kbc_old_id", type: "BIGINT")

            column(name: "del_kbc_status_rv_fk", type: "BIGINT")

            column(name: "del_kbc_component_type", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620300593203-2") {
        addUniqueConstraint(columnNames: "del_kbc_uuid", constraintName: "UC_DELETEDKBCOMPONENTDEL_KBC_UUID_COL", tableName: "deletedkbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1620300593203-3") {
        createIndex(indexName: "del_kbc_name_idx", tableName: "deletedkbcomponent") {
            column(name: "del_kbc_name")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620300593203-4") {
        createIndex(indexName: "del_kbc_status_idx", tableName: "deletedkbcomponent") {
            column(name: "del_kbc_status_rv_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1620300593203-5") {
        addForeignKeyConstraint(baseColumnNames: "del_kbc_status_rv_fk", baseTableName: "deletedkbcomponent", constraintName: "FK5pllr25irr0e6fdetr2obv9il", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }
}

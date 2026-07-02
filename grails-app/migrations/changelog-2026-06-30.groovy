databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1782808528285-1") {
        addColumn(tableName: "identifier") {
            column(name: "id_vendor_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1782808528285-2") {
        createIndex(indexName: "id_vendor_idx", tableName: "identifier") {
            column(name: "id_vendor_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1782808528285-3") {
        addForeignKeyConstraint(baseColumnNames: "id_vendor_fk", baseTableName: "identifier", constraintName: "FK1tgq7ydyacj2nvnfdh6b55kj2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }


    changeSet(author: "djebeniani (generated)", id: "1782808528285-4") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "idns_hard_data", tableName: "identifier_namespace", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1782808528285-5") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_count_deleted_tipps_by_process", type: "int4") {
            }
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1782808528285-6") {
        grailsChange {
            change {

                sql.executeUpdate('update update_package_info set upi_count_deleted_tipps_by_process = 0 where upi_count_deleted_tipps_by_process is null')

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1782808528285-7") {
        grailsChange {
            change {

                sql.executeUpdate('alter table update_package_info alter column upi_count_deleted_tipps_by_process set not null;')

            }
            rollback {}
        }
    }

}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1699951211924-1") {
        createTable(tableName: "vendor_electronic_delivery_delay") {
            column(autoIncrement: "true", name: "vedd_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "vendor_electronic_delivery_delayPK")
            }

            column(name: "vedd_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "vedd_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "vedd_electronic_delivery_delay_rv_fk", type: "BIGINT")

            column(name: "vedd_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "vedd_vendor_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1699951211924-2") {
        addColumn(tableName: "vendor") {
            column(name: "ven_edi_orders", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1699951211924-3") {
        addColumn(tableName: "vendor") {
            column(name: "ven_xml_orders", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1699951211924-4") {
        createIndex(indexName: "vedd_vendor_idx", tableName: "vendor_electronic_delivery_delay") {
            column(name: "vedd_vendor_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1699951211924-5") {
        addForeignKeyConstraint(baseColumnNames: "vedd_electronic_delivery_delay_rv_fk", baseTableName: "vendor_electronic_delivery_delay", constraintName: "FKllpybtn41148y8ugmld5a2c9v", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1699951211924-6") {
        addForeignKeyConstraint(baseColumnNames: "vedd_vendor_fk", baseTableName: "vendor_electronic_delivery_delay", constraintName: "FKt3m7hak2cbbt7lmkpi8l80l33", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }

}

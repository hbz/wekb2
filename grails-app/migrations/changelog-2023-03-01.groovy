databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1677668390583-1") {
        createTable(tableName: "platform_federation") {
            column(autoIncrement: "true", name: "pf_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "platform_federationPK")
            }

            column(name: "pf_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pf_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "pf_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "pf_platform_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pf_federation_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1677668390583-2") {
        createIndex(indexName: "pf_platform_idx", tableName: "platform_federation") {
            column(name: "pf_platform_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1677668390583-3") {
        addForeignKeyConstraint(baseColumnNames: "pf_platform_fk", baseTableName: "platform_federation", constraintName: "FK9nnrn7kviiva1m8sx5ehahwm7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "plat_id", referencedTableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1677668390583-4") {
        addForeignKeyConstraint(baseColumnNames: "pf_federation_rv_fk", baseTableName: "platform_federation", constraintName: "FKgcpblkg88utnfgba7r3d48j7s", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }


}

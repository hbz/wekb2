databaseChangeLog = {

    changeSet(author: "klober (generated)", id: "1780401472922-1") {
        createTable(tableName: "altcha_client") {
            column(autoIncrement: "true", name: "ac_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "altcha_clientPK")
            }

            column(name: "ac_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ac_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "ac_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "ac_expiry", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ac_client", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }
}
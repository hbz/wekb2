databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1646061248178-1") {
        createTable(tableName: "package_archiving_agency") {
            column(autoIncrement: "true", name: "paa_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "package_archiving_agencyPK")
            }

            column(name: "paa_version", type: "BIGINT")

            column(name: "paa_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "paa_pca_rv_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "paa_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "paa_archiving_agency_rv_fk", type: "BIGINT")

            column(name: "paa_pkg_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "paa_open_access_rv_fk", type: "BIGINT")

        }
    }

    changeSet(author: "djebeniani (generated)", id: "1646061248178-2") {
        createIndex(indexName: "paa_pkg_idx", tableName: "package_archiving_agency") {
            column(name: "paa_pkg_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1646061248178-3") {
        addForeignKeyConstraint(baseColumnNames: "paa_pkg_fk", baseTableName: "package_archiving_agency", constraintName: "FK2mg91xq0t2sdow7x3yya1go8m", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1646061248178-4") {
        addForeignKeyConstraint(baseColumnNames: "paa_open_access_rv_fk", baseTableName: "package_archiving_agency", constraintName: "FK5odk303yr61thjdo3hbuqkej1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1646061248178-5") {
        addForeignKeyConstraint(baseColumnNames: "paa_pca_rv_fk", baseTableName: "package_archiving_agency", constraintName: "FKbs76w9q22elrcutyo7iv9e8o3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1646061248178-6") {
        addForeignKeyConstraint(baseColumnNames: "paa_archiving_agency_rv_fk", baseTableName: "package_archiving_agency", constraintName: "FKl24853pj95r8e8maxqhkhfu4q", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }
}

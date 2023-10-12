databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1696409701699-1") {
        createTable(tableName: "curatory_group_vendor") {
            column(autoIncrement: "true", name: "cgv_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "curatory_group_vendorPK")
            }

            column(name: "cgv_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgv_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgv_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgv_curatory_group_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgv_vendor_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-2") {
        createTable(tableName: "vendor") {
            column(autoIncrement: "true", name: "ven_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "vendorPK")
            }

            column(name: "ven_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ven_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "ven_uuid", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ven_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "ven_homepage", type: "VARCHAR(255)")

            column(name: "ven_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ven_status_rv_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ven_abbreviated_name", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-3") {
        createTable(tableName: "vendor_refdata_value") {
            column(name: "vendor_roles_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "refdata_value_id", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-4") {
        addColumn(tableName: "contact") {
            column(name: "ct_vendor_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-5") {
        createIndex(indexName: "cgv_curatory_group_idx", tableName: "curatory_group_vendor") {
            column(name: "cgv_curatory_group_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-6") {
        createIndex(indexName: "cgv_vendor_idx", tableName: "curatory_group_vendor") {
            column(name: "cgv_vendor_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-7") {
        createIndex(indexName: "ct_vendor_idx", tableName: "contact") {
            column(name: "ct_vendor_fk")
        }
    }
    changeSet(author: "djebeniani (generated)", id: "1696409701699-8") {
        addForeignKeyConstraint(baseColumnNames: "vendor_roles_id", baseTableName: "vendor_refdata_value", constraintName: "FK2d35mpgw7bwo3gu1wvcnyu8ie", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-9") {
        addForeignKeyConstraint(baseColumnNames: "cgv_curatory_group_fk", baseTableName: "curatory_group_vendor", constraintName: "FKe9h583vsbmhabpvblm4isbt2i", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "cg_id", referencedTableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-10") {
        addForeignKeyConstraint(baseColumnNames: "ct_vendor_fk", baseTableName: "contact", constraintName: "FKjeayne70tjjc396rfl8lplcbi", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-11") {
        addForeignKeyConstraint(baseColumnNames: "refdata_value_id", baseTableName: "vendor_refdata_value", constraintName: "FKov0d0pao8du4p8byuwahe40uj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-12") {
        addForeignKeyConstraint(baseColumnNames: "cgv_vendor_fk", baseTableName: "curatory_group_vendor", constraintName: "FKs7rc7oaddflek2cmtcu5tlyyf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-13") {
        addForeignKeyConstraint(baseColumnNames: "ven_status_rv_fk", baseTableName: "vendor", constraintName: "FKt1vnswfqc0hp5vckvjkst5d2p", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-14") {
        dropNotNullConstraint(columnDataType: "bigint", columnName: "ct_org_fk", tableName: "contact")
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-15") {
        createTable(tableName: "package_vendor") {
            column(autoIncrement: "true", name: "pv_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "package_vendorPK")
            }

            column(name: "pv_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pv_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "pv_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "pv_vendor_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pv_pkg_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-16") {
        addForeignKeyConstraint(baseColumnNames: "pv_vendor_fk", baseTableName: "package_vendor", constraintName: "FK84qfewt22i3txkteckf92hhfq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1696409701699-17") {
        addForeignKeyConstraint(baseColumnNames: "pv_pkg_fk", baseTableName: "package_vendor", constraintName: "FKcqvcd2n3krb1ey80fhvflh66a", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }

}

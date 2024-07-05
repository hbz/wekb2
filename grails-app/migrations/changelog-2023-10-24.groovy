databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1698140415666-1") {
        addColumn(tableName: "vendor") {
            column(name: "ven_activation_for_new_releases", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-2") {
        addColumn(tableName: "vendor") {
            column(name: "ven_exchange_of_ind_titles", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-3") {
        addColumn(tableName: "vendor") {
            column(name: "ven_forw_usage_stati_fr_pub", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-4") {
        addColumn(tableName: "vendor") {
            column(name: "ven_ind_invoice_design", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-5") {
        addColumn(tableName: "vendor") {
            column(name: "ven_management_of_credits", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-6") {
        addColumn(tableName: "vendor") {
            column(name: "ven_paper_invoice", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-7") {
        addColumn(tableName: "vendor") {
            column(name: "ven_pro_of_com_pay", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-8") {
        addColumn(tableName: "vendor") {
            column(name: "ven_research_platform_for_ebooks", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-9") {
        addColumn(tableName: "vendor") {
            column(name: "ven_shipping_metadata", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-10") {
        addColumn(tableName: "vendor") {
            column(name: "ven_technical_support", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-11") {
        createTable(tableName: "vendor_electronic_billing") {
            column(autoIncrement: "true", name: "veb_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "vendor_electronic_billingPK")
            }

            column(name: "veb_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "veb_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "veb_electronic_billing_rv_fk", type: "BIGINT")

            column(name: "veb_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "veb_vendor_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-12") {
        createTable(tableName: "vendor_invoice_dispatch") {
            column(autoIncrement: "true", name: "vid_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "vendor_invoice_dispatchPK")
            }

            column(name: "vid_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "vid_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "vid_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "vid_invoice_dispatch_rv_fk", type: "BIGINT")

            column(name: "vid_vendor_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-13") {
        createTable(tableName: "vendor_library_system") {
            column(autoIncrement: "true", name: "vls_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "vendor_library_systemPK")
            }

            column(name: "vls_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "vls_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "vls_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "vls_vendor_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "vls_supported_library_system_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-14") {
        addColumn(tableName: "kbcomponent") {
            column(name: "last_updated_by_id", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-15") {
        createIndex(indexName: "veb_vendor_idx", tableName: "vendor_electronic_billing") {
            column(name: "veb_vendor_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-16") {
        createIndex(indexName: "vid_vendor_idx", tableName: "vendor_invoice_dispatch") {
            column(name: "vid_vendor_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-17") {
        createIndex(indexName: "vls_vendor_idx", tableName: "vendor_library_system") {
            column(name: "vls_vendor_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-18") {
        addForeignKeyConstraint(baseColumnNames: "vid_invoice_dispatch_rv_fk", baseTableName: "vendor_invoice_dispatch", constraintName: "FK1p8pe6ykjsersd77fk59iu4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-19") {
        addForeignKeyConstraint(baseColumnNames: "veb_electronic_billing_rv_fk", baseTableName: "vendor_electronic_billing", constraintName: "FK5bvx7ys7g88dp7j7a59vapepx", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-20") {
        addForeignKeyConstraint(baseColumnNames: "vls_supported_library_system_rv_fk", baseTableName: "vendor_library_system", constraintName: "FKiknnrk8h32277cfa569ojlm0c", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-21") {
        addForeignKeyConstraint(baseColumnNames: "vid_vendor_fk", baseTableName: "vendor_invoice_dispatch", constraintName: "FKk254sjs1fsllq521h5gv5ob66", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-22") {
        addForeignKeyConstraint(baseColumnNames: "veb_vendor_fk", baseTableName: "vendor_electronic_billing", constraintName: "FKkfe4yagajhp40jrxtaouvegus", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1698140415666-23") {
        addForeignKeyConstraint(baseColumnNames: "vls_vendor_fk", baseTableName: "vendor_library_system", constraintName: "FKws46elqh658lvvbj0h5cmuhc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }
}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1709566275930-1") {
        createTable(tableName: "provider_electronic_billing") {
            column(autoIncrement: "true", name: "peb_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "provider_electronic_billingPK")
            }

            column(name: "peb_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "peb_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "peb_electronic_billing_rv_fk", type: "BIGINT")

            column(name: "peb_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "peb_provider_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-2") {
        createTable(tableName: "provider_invoice_dispatch") {
            column(autoIncrement: "true", name: "pid_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "provider_invoice_dispatchPK")
            }

            column(name: "pid_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pid_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "pid_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "pid_invoice_dispatch_rv_fk", type: "BIGINT")

            column(name: "pid_provider_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-3") {
        grailsChange {
            change {
                sql.execute('''alter table org add org_ind_invoice_design boolean;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-4") {
        grailsChange {
            change {
                sql.execute('''update org set org_ind_invoice_design = false;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-5") {
        grailsChange {
            change {
                sql.execute('''alter table org alter column org_ind_invoice_design set not null;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-6") {
        grailsChange {
            change {
                sql.execute('''alter table org add org_management_of_credits boolean;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-7") {
        grailsChange {
            change {
                sql.execute('''update org set org_management_of_credits = false;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-8") {
        grailsChange {
            change {
                sql.execute('''alter table org alter column org_management_of_credits set not null;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-9") {
        grailsChange {
            change {
                sql.execute('''alter table org add org_paper_invoice boolean;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-10") {
        grailsChange {
            change {
                sql.execute('''update org set org_paper_invoice = false;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-11") {
        grailsChange {
            change {
                sql.execute('''alter table org alter column org_paper_invoice set not null;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-12") {
        grailsChange {
            change {
                sql.execute('''alter table org add org_pro_of_com_pay boolean;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-13") {
        grailsChange {
            change {
                sql.execute('''update org set org_pro_of_com_pay = false;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1709566275930-14") {
        grailsChange {
            change {
                sql.execute('''alter table org alter column org_pro_of_com_pay set not null;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-15") {
        createIndex(indexName: "peb_provider_idx", tableName: "provider_electronic_billing") {
            column(name: "peb_provider_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-16") {
        createIndex(indexName: "pid_provider_idx", tableName: "provider_invoice_dispatch") {
            column(name: "pid_provider_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-17") {
        addForeignKeyConstraint(baseColumnNames: "pid_provider_fk", baseTableName: "provider_invoice_dispatch", constraintName: "FK2vois5v9lxw5vugj14waq472y", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-18") {
        addForeignKeyConstraint(baseColumnNames: "peb_provider_fk", baseTableName: "provider_electronic_billing", constraintName: "FKcvbra7d49we71lut9stcqj8pi", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-19") {
        addForeignKeyConstraint(baseColumnNames: "peb_electronic_billing_rv_fk", baseTableName: "provider_electronic_billing", constraintName: "FKoelv8lta0fkdbvfnhcfp1gxin", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-20") {
        addForeignKeyConstraint(baseColumnNames: "pid_invoice_dispatch_rv_fk", baseTableName: "provider_invoice_dispatch", constraintName: "FKtpt8psjnk3y30lffqcgfkraid", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-21") {
        createTable(tableName: "provider_invoicing_vendor") {
            column(autoIncrement: "true", name: "piv_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "provider_invoicing_vendorPK")
            }

            column(name: "piv_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "piv_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "piv_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "piv_vendor_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "piv_provider_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-22") {
        addForeignKeyConstraint(baseColumnNames: "piv_provider_fk", baseTableName: "provider_invoicing_vendor", constraintName: "FK7v4meje1i2rk5fg7210dycwjq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1709566275930-23") {
        addForeignKeyConstraint(baseColumnNames: "piv_vendor_fk", baseTableName: "provider_invoicing_vendor", constraintName: "FK97kc4p3pih45r8hstjd6ceb3w", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ven_id", referencedTableName: "vendor", validate: "true")
    }

}

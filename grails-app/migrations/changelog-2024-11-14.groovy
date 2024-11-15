databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded))", id: "1731408479648-1") {
        grailsChange {
            change {
                sql.execute('''alter table component_language rename to tipp_language;''')
                sql.execute('''alter table tipp_language rename column cl_id to tl_id;
                                    alter table tipp_language rename column cl_version to tl_version;
                                    alter table tipp_language rename column cl_date_created to tl_date_created;
                                    alter table tipp_language rename column cl_last_updated to tl_last_updated;
                                    alter table tipp_language rename column cl_rv_fk to tl_rv_fk;
                                    alter table tipp_language rename column cl_tipp_fk to tl_tipp_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1731408479648-2") {
        createTable(tableName: "contact_language") {
            column(autoIncrement: "true", name: "cl_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "contact_languagePK")
            }

            column(name: "cl_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cl_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cl_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cl_language_rv_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cl_contact_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1731408479648-3") {
        createIndex(indexName: "cl_contact_idx", tableName: "contact_language") {
            column(name: "cl_contact_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1731408479648-4") {
        addForeignKeyConstraint(baseColumnNames: "cl_language_rv_fk", baseTableName: "contact_language", constraintName: "FKonuvshmg8lnmfbkt6xs5x2k4p", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1731408479648-5") {
        addForeignKeyConstraint(baseColumnNames: "cl_contact_fk", baseTableName: "contact_language", constraintName: "FKsvgwdeotk7yc26su0j936jyeg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ct_id", referencedTableName: "contact", validate: "true")
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1731408479648-6") {
        grailsChange {
            change {
                sql.execute('''alter table component_language rename to tipp_language;''')
                sql.execute('''alter table tipp_language rename column cl_id to tl_id;
                                    alter table tipp_language rename column cl_version to tl_version;
                                    alter table tipp_language rename column cl_date_created to tl_date_created;
                                    alter table tipp_language rename column cl_last_updated to tl_last_updated;
                                    alter table tipp_language rename column cl_rv_fk to tl_rv_fk;
                                    alter table tipp_language rename column cl_tipp_fk to tl_tipp_fk;''')
            }
            rollback {}
        }
    }

/*    changeSet(author: "djebeniani (generated)", id: "1731408479648-7") {
        dropForeignKeyConstraint(baseTableName: "contact", constraintName: "FKk7cibsotlcmfot0vilqt26926")
    }

    changeSet(author: "djebeniani (generated)", id: "1731408479648-8") {
        dropColumn(columnName: "ct_language_rv_fk", tableName: "contact")
    }*/


}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1675416844263-1") {
        createTable(tableName: "curatory_group_kbart_source") {
            column(autoIncrement: "true", name: "cgks_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "curatory_group_kbart_sourcePK")
            }

            column(name: "cgks_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgks_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgks_kbart_source_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgks_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgks_curatory_group_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-2") {
        createTable(tableName: "curatory_group_org") {
            column(autoIncrement: "true", name: "cgo_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "curatory_group_orgPK")
            }

            column(name: "cgo_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgo_org_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgo_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgo_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgo_curatory_group_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-3") {
        createTable(tableName: "curatory_group_package") {
            column(autoIncrement: "true", name: "cgp_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "curatory_group_packagePK")
            }

            column(name: "cgp_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgp_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgp_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgp_curatory_group_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgp_pkg_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-4") {
        createTable(tableName: "curatory_group_platform") {
            column(autoIncrement: "true", name: "cgpl_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "curatory_group_platformPK")
            }

            column(name: "cgpl_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgpl_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgpl_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "cgpl_curatory_group_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cgpl_platform_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-5") {
        createIndex(indexName: "cgo_curatory_group_idx", tableName: "curatory_group_org") {
            column(name: "cgo_curatory_group_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-6") {
        createIndex(indexName: "cgo_org_idx", tableName: "curatory_group_org") {
            column(name: "cgo_org_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-7") {
        createIndex(indexName: "cgp_curatory_group_idx", tableName: "curatory_group_package") {
            column(name: "cgp_curatory_group_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-8") {
        createIndex(indexName: "cgp_pkg_idx", tableName: "curatory_group_package") {
            column(name: "cgp_pkg_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-9") {
        createIndex(indexName: "cgpl_curatory_group_idx", tableName: "curatory_group_platform") {
            column(name: "cgpl_curatory_group_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-10") {
        createIndex(indexName: "cgpl_platform_idx", tableName: "curatory_group_platform") {
            column(name: "cgpl_platform_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-11") {
        createIndex(indexName: "cgks_curatory_group_idx", tableName: "curatory_group_kbart_source") {
            column(name: "cgks_curatory_group_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-12") {
        createIndex(indexName: "cgks_kbart_source_idx", tableName: "curatory_group_kbart_source") {
            column(name: "cgks_kbart_source_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-13") {
        addForeignKeyConstraint(baseColumnNames: "cgpl_platform_fk", baseTableName: "curatory_group_platform", constraintName: "FK2bu6nh8uipck8anm8dvbasrkv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-14") {
        addForeignKeyConstraint(baseColumnNames: "cgo_curatory_group_fk", baseTableName: "curatory_group_org", constraintName: "FK3x2ltdi9qax95yhc42dllc75e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-15") {
        addForeignKeyConstraint(baseColumnNames: "cgks_curatory_group_fk", baseTableName: "curatory_group_kbart_source", constraintName: "FK4wy40epl1ivjiwom4nmn4rs9o", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-16") {
        addForeignKeyConstraint(baseColumnNames: "cgo_org_fk", baseTableName: "curatory_group_org", constraintName: "FK69b076421hlbqrdda72m7shp9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-17") {
        addForeignKeyConstraint(baseColumnNames: "cgp_curatory_group_fk", baseTableName: "curatory_group_package", constraintName: "FKhn80wopnug4kipboh22cxvjls", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-18") {
        addForeignKeyConstraint(baseColumnNames: "cgpl_curatory_group_fk", baseTableName: "curatory_group_platform", constraintName: "FKk0whaf20dv9j5olknre3879eu", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "curatory_group", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-19") {
        addForeignKeyConstraint(baseColumnNames: "cgp_pkg_fk", baseTableName: "curatory_group_package", constraintName: "FKkil080hpctnbc9l7i2pouqo3n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "package", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675416844263-20") {
        addForeignKeyConstraint(baseColumnNames: "cgks_kbart_source_fk", baseTableName: "curatory_group_kbart_source", constraintName: "FKkr99ot0vriphaq1ukwax4etsu", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ks_id", referencedTableName: "kbart_source", validate: "true")
    }

    changeSet(author: "djebeniani (modified)", id: "1675416844263-21") {
        grailsChange {
            change {

                Integer countUpdate = 0

                def combos = sql.rows('''select combo_to_fk, combo_from_fk, date_created, last_updated from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Source.CuratoryGroups');''')

                combos.each {
                    countUpdate ++
                    sql.execute("""insert into curatory_group_kbart_source(cgks_id, cgks_version, cgks_date_created, cgks_last_updated, cgks_kbart_source_fk, cgks_curatory_group_fk) values
	                ((select nextval ('hibernate_sequence')), 0, ${it.date_created}, ${it.last_updated}, ${it.combo_from_fk}, ${it.combo_to_fk}) 
                    """)
                }

                confirm("insert combo into curatory_group_kbart_source: ${countUpdate}")
                changeSet.setComments("insert combo into curatory_group_kbart_source: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675416844263-22") {
        grailsChange {
            change {

                Integer countUpdate = 0

                def combos = sql.rows('''select combo_to_fk, combo_from_fk, date_created, last_updated from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Org.CuratoryGroups');''')

                combos.each {
                    countUpdate ++
                    sql.execute("""insert into curatory_group_org(cgo_id, cgo_version, cgo_date_created, cgo_last_updated, cgo_org_fk, cgo_curatory_group_fk) values
	                ((select nextval ('hibernate_sequence')), 0, ${it.date_created}, ${it.last_updated}, ${it.combo_from_fk}, ${it.combo_to_fk}) 
                    """)
                }

                confirm("insert combo into curatory_group_org: ${countUpdate}")
                changeSet.setComments("insert combo into curatory_group_org: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675416844263-23") {
        grailsChange {
            change {

                Integer countUpdate = 0

                def combos = sql.rows('''select combo_to_fk, combo_from_fk, date_created, last_updated from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Package.CuratoryGroups');''')

                combos.each {
                    countUpdate ++
                    sql.execute("""insert into curatory_group_package(cgp_id, cgp_version, cgp_date_created, cgp_last_updated, cgp_pkg_fk, cgp_curatory_group_fk) values
	                ((select nextval ('hibernate_sequence')), 0, ${it.date_created}, ${it.last_updated}, ${it.combo_from_fk}, ${it.combo_to_fk}) 
                    """)
                }

                confirm("insert combo into curatory_group_package: ${countUpdate}")
                changeSet.setComments("insert combo into curatory_group_package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675416844263-24") {
        grailsChange {
            change {

                Integer countUpdate = 0

                def combos = sql.rows('''select combo_to_fk, combo_from_fk, date_created, last_updated from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Platform.CuratoryGroups');''')

                combos.each {
                    countUpdate ++
                    sql.execute("""insert into curatory_group_platform(cgpl_id, cgpl_version, cgpl_date_created, cgpl_last_updated, cgpl_platform_fk, cgpl_curatory_group_fk) values
	                ((select nextval ('hibernate_sequence')), 0, ${it.date_created}, ${it.last_updated}, ${it.combo_from_fk}, ${it.combo_to_fk}) 
                    """)
                }

                confirm("insert combo into curatory_group_platform: ${countUpdate}")
                changeSet.setComments("insert combo into curatory_group_platform: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675416844263-25") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Source.CuratoryGroups')")

                confirm("delete combos with Source.CuratoryGroups type: ${countDelete}")
                changeSet.setComments("delete combos with Source.CuratoryGroups type: ${countDelete}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675416844263-26") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Org.CuratoryGroups')")

                confirm("delete combos with Org.CuratoryGroups type: ${countDelete}")
                changeSet.setComments("delete combos with Org.CuratoryGroups type: ${countDelete}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675416844263-27") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Package.CuratoryGroups')")

                confirm("delete combos with Package.CuratoryGroups type: ${countDelete}")
                changeSet.setComments("delete combos with Package.CuratoryGroups type: ${countDelete}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675416844263-28") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'Platform.CuratoryGroups')")

                confirm("delete combos with Platform.CuratoryGroups type: ${countDelete}")
                changeSet.setComments("delete combos with Platform.CuratoryGroups type: ${countDelete}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675416844263-29") {
        grailsChange {
            change {
                Integer countDelete = sql.executeUpdate("delete from combo where combo_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'KBComponent.Ids')")

                confirm("delete combos with KBComponent.Ids type: ${countDelete}")
                changeSet.setComments("delete combos with KBComponent.Ids type: ${countDelete}")
            }
            rollback {}
        }
    }
}

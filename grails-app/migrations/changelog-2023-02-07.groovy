import wekb.Package

databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1675376747887-1") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table source rename to "kbart_source";''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-2") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column kbc_id to ks_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-3") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column default_supply_method_id to ks_default_supply_method_rv_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-4") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column source_url to ks_url;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-5") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column default_data_format_id to ks_default_data_format_rv_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-6") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column last_run to ks_last_run;''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675376747887-7") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column automatic_updates to ks_automatic_updates;''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675376747887-8") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column target_namespace_id to ks_target_namespace_fk;''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675376747887-9") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column frequency_id to ks_frequency_rv_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-10") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column source_last_update_url to ks_last_update_url;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-11") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column source_kbart_wekb_fields to ks_kbart_wekb_fields;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-12") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "kbart_source"
    rename column source_last_changed_in_kbart to ks_last_changed_in_kbart;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-13") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_name", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-14") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_version", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-15") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-16") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-17") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_status_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-18") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_uuid", type: "TEXT")
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-19") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update kbart_source set ks_name = (select kbc_name from kbcomponent where kbc_id = ks_id) where ks_name is null''')

                confirm("set ks_name from kbart_source: ${countUpdate}")
                changeSet.setComments("set ks_name from kbart_source: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-20") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update kbart_source set ks_version = (select kbc_version from kbcomponent where kbc_id = ks_id) where ks_version is null''')

                confirm("set ks_version from kbart_source: ${countUpdate}")
                changeSet.setComments("set ks_version from kbart_source: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-21") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update kbart_source set ks_last_updated = (select kbc_last_updated from kbcomponent where kbc_id = ks_id) where ks_last_updated is null''')

                confirm("set ks_last_updated from kbart_source: ${countUpdate}")
                changeSet.setComments("set ks_last_updated from kbart_source: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-22") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update kbart_source set ks_date_created = (select kbc_date_created from kbcomponent where kbc_id = ks_id) where ks_date_created is null''')

                confirm("set ks_date_created from kbart_source: ${countUpdate}")
                changeSet.setComments("set ks_date_created from kbart_source: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-23") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update kbart_source set ks_status_rv_fk = (select kbc_status_rv_fk from kbcomponent where kbc_id = ks_id) where ks_status_rv_fk is null''')

                confirm("set ks_status_rv_fk from kbart_source: ${countUpdate}")
                changeSet.setComments("set ks_status_rv_fk from kbart_source: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-24") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update kbart_source set ks_uuid = (select kbc_uuid from kbcomponent where kbc_id = ks_id) where ks_uuid is null''')

                confirm("set ks_uuid from kbart_source: ${countUpdate}")
                changeSet.setComments("set ks_uuid from kbart_source: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-25") {
        addColumn(tableName: "package") {
            column(name: "pkg_kbart_source_fk", type: "TEXT")
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675376747887-26") {
        grailsChange {
            change {

                Integer countUpdate = 0

                def sourceIds = sql.rows('''select kbc_source_fk, kbc_id from kbcomponent where kbc_source_fk is not null;''')

                sourceIds.each {
                    countUpdate ++
                    sql.executeUpdate('''update package set pkg_kbart_source_fk = :sourceID where kbc_id = :kbcID''', [kbcID: it.kbc_id, sourceID: it.kbc_source_fk])
                }

                confirm("set pkg_kbart_source_fk from package: ${countUpdate}")
                changeSet.setComments("set pkg_kbart_source_fk from package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-27") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "ks_name", tableName: "kbart_source", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-28") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "ks_uuid", tableName: "kbart_source", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-29") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "ks_version", tableName: "kbart_source", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-30") {
        addForeignKeyConstraint(baseColumnNames: "ks_status_rv_fk", baseTableName: "kbart_source", constraintName: "FK2hig6gmiu8vadu2yap0ji9x0p", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-31") {
        addForeignKeyConstraint(baseColumnNames: "pkg_kbart_source_fk", baseTableName: "package", constraintName: "FKnxq0sr4gt78r22yme1a4p1gef", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ks_id", referencedTableName: "kbart_source", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-32") {
        dropForeignKeyConstraint(baseTableName: "kbcomponent", constraintName: "fkdcqidy0a8vve7weaha2woqu8u")
    }

    changeSet(author: "djebeniani (generated)", id: "1675376747887-33") {
        dropColumn(columnName: "kbc_source_fk", tableName: "kbcomponent")
    }

}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1676554743429-1") {
        dropForeignKeyConstraint(baseTableName: "org", constraintName: "fkl0ge7y25mt69enho54klpqs14")
    }

    changeSet(author: "djebeniani (generated)", id: "1676554743429-2") {
        dropColumn(columnName: "org_mission_fk_rv", tableName: "org")
    }

    changeSet(author: "djebeniani (generated)", id: "1676554743429-3") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_registry_api_uuid", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1676554743429-4") {
        dropColumn(columnName: "pkg_global_note", tableName: "package")
    }

    changeSet(author: "djebeniani (modified)", id: "1676554743429-5") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'Component.Language' where rdc_description = 'KBComponent.Language';''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1676554743429-6") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'Component.Status' where rdc_description = 'KBComponent.Status';''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1676554743429-7") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'ComponentVariantName.Locale' where rdc_description = 'KBComponentVariantName.Locale';''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1676554743429-8") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'ComponentVariantName.Status' where rdc_description = 'KBComponentVariantName.Status';''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1676554743429-9") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'ComponentVariantName.VariantType' where rdc_description = 'KBComponentVariantName.VariantType';''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1676554743429-10") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table kbcomponent drop column last_updated_by_id;''')
            }
            rollback {}
        }
    }


}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1675871287816-1") {
        addColumn(tableName: "package") {
            column(name: "pkg_free_trial_phase", type: "text") {
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675871287816-2") {
        addColumn(tableName: "package") {
            column(name: "pkg_free_trial_rv_fk", type: "int8") {
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675871287816-3") {
        addForeignKeyConstraint(baseColumnNames: "pkg_free_trial_rv_fk", baseTableName: "package", constraintName: "FKbgprs7uiew36urm4u2p9ffcup", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (modified)", id: "1675871287816-4") {
        grailsChange {
            change {

                sql.execute('''  alter table package
    alter column pkg_last_update_comment type text using pkg_last_update_comment::text;
''')
            }
            rollback {}
        }
    }


}

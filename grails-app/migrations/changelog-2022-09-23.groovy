databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1663965353615-1") {
        addColumn(tableName: "auto_update_package_info") {
            column(name: "aupi_count_now_tipps", type: "int4")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-2") {
        addColumn(tableName: "auto_update_package_info") {
            column(name: "aupi_count_previously_tipps", type: "int4")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-3") {
        dropTable(tableName: "activity")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-4") {
        dropTable(tableName: "annotation")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-5") {
        dropTable(tableName: "component_like")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-6") {
        dropTable(tableName: "curatory_group_watch")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-7") {
        dropForeignKeyConstraint(baseTableName: "dsapplied_criterion", constraintName: "fk37jwybh38m40xfotaa3ox4sis")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-8") {
        dropForeignKeyConstraint(baseTableName: "folder_entry", constraintName: "fk4sann6e8xej2j3tl3kb7vkarl")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-9") {
        dropForeignKeyConstraint(baseTableName: "dsapplied_criterion", constraintName: "fk5nsrwpcb6p46abqcj0glm4pxp")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-10") {
        dropForeignKeyConstraint(baseTableName: "dsapplied_criterion", constraintName: "fk81ttek8ss1pasiujldmlo0n8w")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-11") {
        dropForeignKeyConstraint(baseTableName: "folder_entry", constraintName: "fk8xdck8vgx7yb7ymmlm400kuga")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-12") {
        dropForeignKeyConstraint(baseTableName: "dscriterion", constraintName: "fkacyn2jqtodlvnogdu65iqvio1")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-13") {
        dropForeignKeyConstraint(baseTableName: "dsapplied_criterion", constraintName: "fkfj1cwdl76rjw9oavn0w9mj1xo")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-14") {
        dropForeignKeyConstraint(baseTableName: "dsnote", constraintName: "fkh4mk4a95bu8ney9tloewadbi8")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-15") {
        dropForeignKeyConstraint(baseTableName: "dscriterion", constraintName: "fkh75licju6txf8u09s7myjgwiu")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-16") {
        dropForeignKeyConstraint(baseTableName: "kbdomain_info", constraintName: "fko8c8vf8rxfsyye7kdhccuskyd")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-17") {
        dropForeignKeyConstraint(baseTableName: "office", constraintName: "fkrr2uet4tl16s5baep5skseuy0")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-18") {
        dropForeignKeyConstraint(baseTableName: "folder_entry", constraintName: "fkse1dy2yymgie3ucb2i2guo4pa")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-19") {
        dropTable(tableName: "dsapplied_criterion")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-20") {
        dropTable(tableName: "dscategory")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-21") {
        dropTable(tableName: "dscriterion")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-22") {
        dropTable(tableName: "dsnote")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-23") {
        dropTable(tableName: "folder_entry")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-24") {
        dropTable(tableName: "kbdomain_info")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-25") {
        dropTable(tableName: "office")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-26") {
        dropTable(tableName: "title_instance_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-27") {
        addColumn(tableName: "source") {
            column(name: "source_last_changed_in_kbart", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1663965353615-28") {
        grailsChange {
            change {

                sql.executeUpdate('update auto_update_package_info set aupi_count_now_tipps = 0 where aupi_count_now_tipps is null')

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1663965353615-29") {
        grailsChange {
            change {

                sql.executeUpdate('update auto_update_package_info set aupi_count_previously_tipps = 0 where aupi_count_previously_tipps is null')

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1663965353615-30") {
        addColumn(tableName: "auto_update_package_info") {
            column(name: "aupi_last_changed_in_kbart", type: "timestamp")
        }
    }
}

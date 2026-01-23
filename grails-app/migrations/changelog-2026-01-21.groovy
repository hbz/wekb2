databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1769014043074-1") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_count_current_tipps", type: "int4")
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1769014043074-2") {
        grailsChange {
            change {

                sql.executeUpdate('update update_package_info set upi_count_current_tipps = 0 where upi_count_current_tipps is null')

            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (generated)", id: "1769014043074-3") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_count_deleted_tipps", type: "int4")
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1769014043074-4") {
        grailsChange {
            change {

                sql.executeUpdate('update update_package_info set upi_count_deleted_tipps = 0 where upi_count_deleted_tipps is null')

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1769014043074-5") {
        grailsChange {
            change {

                sql.executeUpdate('alter table update_package_info alter column upi_count_current_tipps set not null;')

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1769014043074-6") {
        grailsChange {
            change {

                sql.executeUpdate('alter table update_package_info alter column upi_count_deleted_tipps set not null;')

            }
            rollback {}
        }
    }


}

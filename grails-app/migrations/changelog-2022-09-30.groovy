databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1664533136364-1") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_from_kbart_import", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1664533136364-2") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_from_kbart_import = false where tipp_from_kbart_import is null')

            }
            rollback {}
        }
    }
}
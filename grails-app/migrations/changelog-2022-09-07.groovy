
databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1662579394414-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_open_athens_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1662579394414-2") {
        addForeignKeyConstraint(baseColumnNames: "plat_open_athens_fk_rv", baseTableName: "platform", constraintName: "FKhyjognfgw6ne6nqvis5d4uw4y", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1662579394414-3") {
        addColumn(tableName: "source") {
            column(name: "source_kbart_wekb_fields", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1662579394414-4") {
        addColumn(tableName: "auto_update_package_info") {
            column(name: "aupi_kbart_has_wekb_fields", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1662579394414-5") {
        grailsChange {
            change {

                sql.executeUpdate('update source set source_kbart_wekb_fields = false where source_kbart_wekb_fields is null')

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1662579394414-6") {
        grailsChange {
            change {

                sql.executeUpdate('update auto_update_package_info set aupi_kbart_has_wekb_fields = false where aupi_kbart_has_wekb_fields is null')

            }
            rollback {}
        }
    }
}
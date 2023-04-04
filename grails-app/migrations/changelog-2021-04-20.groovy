databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1618908946625-1") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_note", type: "text")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618908946625-2") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_open_access_rv_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618908946625-3") {
        addForeignKeyConstraint(baseColumnNames: "tipp_open_access_rv_fk", baseTableName: "title_instance_package_platform", constraintName: "FKkcfxhr2ip15bs2cnui2s9xq4u", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1618908946625-4") {
        addForeignKeyConstraint(baseColumnNames: "cg_type_rv_fk", baseTableName: "curatory_group", constraintName: "FKpnsntg0nd3uwfi7mwdy442vsm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1618908946625-5") {
        dropForeignKeyConstraint(baseTableName: "title_instance_package_platform", constraintName: "fkiycsy4sxyiyhk8erilurc0rnt")
    }

    changeSet(author: "djebeniani (generated)", id: "1618908946625-6") {
        dropForeignKeyConstraint(baseTableName: "title_instance_package_platform", constraintName: "fkt73jy1as5ru24pa40xos6858b")
    }

    changeSet(author: "djebeniani (generated)", id: "1618908946625-7") {
        dropColumn(columnName: "tipp_delayed_oa_embargo", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1618908946625-8") {
        dropColumn(columnName: "tipp_format_rv_fk", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1618908946625-9") {
        dropColumn(columnName: "tipp_payment_type", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1618908946625-10") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_superseding_publication_title_id", type: "varchar(255)")
        }
    }
}

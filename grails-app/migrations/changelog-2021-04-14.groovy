databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1618406977971-1") {
        addColumn(tableName: "refdata_category") {
            column(name: "rdc_desc_de", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-2") {
        addColumn(tableName: "refdata_category") {
            column(name: "rdc_desc_en", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-3") {
        addColumn(tableName: "refdata_category") {
            column(name: "rdc_is_hard_data", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-4") {
        addColumn(tableName: "refdata_value") {
            column(name: "rdv_is_hard_data", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-5") {
        addColumn(tableName: "refdata_value") {
            column(name: "rdv_value_de", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-6") {
        addColumn(tableName: "refdata_value") {
            column(name: "rdv_value_en", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-7") {
        dropForeignKeyConstraint(baseTableName: "component_subject", constraintName: "fk134luljtxu9bftl702awfkhd5")
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-8") {
        dropForeignKeyConstraint(baseTableName: "component_subject", constraintName: "fk4dh8cq1haypa5gfvku7127phy")
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-9") {
        dropForeignKeyConstraint(baseTableName: "subject", constraintName: "fkg9h5bxj73ot48urj0xoqiw44b")
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-10") {
        dropTable(tableName: "component_subject")
    }

    changeSet(author: "djebeniani (generated)", id: "1618406977971-11") {
        dropTable(tableName: "subject")
    }
}

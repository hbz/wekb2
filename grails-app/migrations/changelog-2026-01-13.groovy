databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1768294412058-1") {
        addColumn(tableName: "org") {
            column(name: "org_agreement_model", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1768294412058-2") {
        addColumn(tableName: "org") {
            column(name: "org_license_based_eb_interlibrary_supported", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1768294412058-3") {
        addColumn(tableName: "org") {
            column(name: "org_range", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1768294412058-4") {
        addForeignKeyConstraint(baseColumnNames: "org_range", baseTableName: "org", constraintName: "FK16rn4mv7k669no04sl8h25fgu", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }


    changeSet(author: "djebeniani (generated)", id: "1768294412058-5") {
        addForeignKeyConstraint(baseColumnNames: "org_license_based_eb_interlibrary_supported", baseTableName: "org", constraintName: "FKe48g9yg95i7hgpwsxfmi0rg18", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1768294412058-6") {
        addForeignKeyConstraint(baseColumnNames: "org_agreement_model", baseTableName: "org", constraintName: "FKjm7pra38jsnfnp1vwynk5iws", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }


}

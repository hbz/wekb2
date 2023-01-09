databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1646901131691-1") {
        dropForeignKeyConstraint(baseTableName: "identifier_namespace", constraintName: "fk7csknbpyn4tb2wj3jg35a0b90")
    }

    changeSet(author: "djebeniani (generated)", id: "1646901131691-2") {
        dropColumn(columnName: "idns_datatype", tableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1646901131691-3") {
        dropUniqueConstraint(constraintName: "uk_ij3l54ly9dq3drsq5224xvo14", tableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1646901131691-4") {
        addUniqueConstraint(columnNames: "idns_value, idns_targettype", constraintName: "unique_identifier_namespace", tableName: "identifier_namespace")
    }

}

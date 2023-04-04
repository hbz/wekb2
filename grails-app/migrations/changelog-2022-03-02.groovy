databaseChangeLog = {


    changeSet(author: "djebeniani (generated)", id: "1646210248095-1") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "paa_archiving_agency_rv_fk", tableName: "package_archiving_agency")
    }

    changeSet(author: "djebeniani (generated)", id: "1646210248095-2") {
        dropNotNullConstraint(columnDataType: "bigint", columnName: "paa_pca_rv_fk", tableName: "package_archiving_agency")
    }

}

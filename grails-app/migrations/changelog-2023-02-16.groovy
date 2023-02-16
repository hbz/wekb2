databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1676554743429-1") {
        dropForeignKeyConstraint(baseTableName: "org", constraintName: "fkl0ge7y25mt69enho54klpqs14")
    }

    changeSet(author: "djebeniani (generated)", id: "1676554743429-2") {
        dropColumn(columnName: "org_mission_fk_rv", tableName: "org")
    }

}

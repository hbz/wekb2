databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1734474454540-1") {
        addColumn(tableName: "user") {
            column(name: "usr_invalid_login_attempts", type: "int4")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1734474454540-2") {
        addColumn(tableName: "user") {
            column(name: "usr_last_login", type: "timestamp")
        }
    }

}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1675680484888-1") {
        dropForeignKeyConstraint(baseTableName: "combo", constraintName: "fk26noxwwqejpm28iqmmikunvrj")
    }

    changeSet(author: "djebeniani (generated)", id: "1675680484888-2") {
        dropForeignKeyConstraint(baseTableName: "combo", constraintName: "fki9gqetad4umeu83v7l62j8awb")
    }

    changeSet(author: "djebeniani (generated)", id: "1675680484888-3") {
        dropForeignKeyConstraint(baseTableName: "combo", constraintName: "fkletguvojj9v9ucu34iuoxkov2")
    }

    changeSet(author: "djebeniani (generated)", id: "1675680484888-4") {
        dropForeignKeyConstraint(baseTableName: "combo", constraintName: "fkp6k1rfprw3o255ijut9gqr237")
    }

    changeSet(author: "djebeniani (generated)", id: "1675680484888-5") {
        dropTable(tableName: "combo")
    }
}

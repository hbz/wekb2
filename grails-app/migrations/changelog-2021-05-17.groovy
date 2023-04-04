databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1621236275129-1") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "fk587a50fb1p02dmn1pj74gsnqj")
    }

    changeSet(author: "djebeniani (generated)", id: "1621236275129-2") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "fkii8i4sqkfon5ymxbx484caxsb")
    }

    changeSet(author: "djebeniani (generated)", id: "1621236275129-3") {
        dropColumn(columnName: "plat_svc_fk_rv", tableName: "platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1621236275129-4") {
        dropColumn(columnName: "plat_sw_fk_rv", tableName: "platform")
    }
}

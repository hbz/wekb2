databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1786623794803-1") {
        dropColumn(columnName: "plat_central_api_key", tableName: "platform")
    }

    changeSet(author: "galffy (generated)", id: "1786623794803-2") {
        dropColumn(columnName: "plat_intern_label_for_customer_id", tableName: "platform")
    }

    changeSet(author: "galffy (generated)", id: "1786623794803-3") {
        dropColumn(columnName: "plat_intern_label_for_requestor_key", tableName: "platform")
    }
}

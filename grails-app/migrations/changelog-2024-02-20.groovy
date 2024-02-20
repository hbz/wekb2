databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1708438845802-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_sushi_platform", type: "text")
        }
    }
}

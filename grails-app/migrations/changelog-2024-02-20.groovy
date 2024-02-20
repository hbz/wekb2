databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1708443292411-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_r5_sushi_platform", type: "text")
        }
    }
}

databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded))", id: "1701767970643-1") {
        grailsChange {
            change {
                sql.execute('''alter table tipp_price
                            alter column tp_tipp_fk drop not null;''')
                rollback {}
            }
        }
    }

}

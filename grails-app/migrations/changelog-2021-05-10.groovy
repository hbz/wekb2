databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded)", id: "1620650696745-1") {
        grailsChange {
            change {
                sql.execute("update refdata_value set rdv_value = concat('0',rdv_value) where rdv_value::numeric::integer >= 10 and rdv_value::numeric::integer < 100 and rdv_owner = (select rdc_id from refdata_category where rdc_description = 'DDC');")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1620650696745-2") {
        grailsChange {
            change {
                sql.execute("update refdata_value set rdv_value = concat('00',rdv_value) where rdv_value::numeric::integer < 10 and rdv_owner = (select rdc_id from refdata_category where rdc_description = 'DDC');")
            }
            rollback {}
        }
    }
}

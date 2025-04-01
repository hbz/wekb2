databaseChangeLog = {

    changeSet(author: "galffy (modified)", id: "1737550537746-1") {
        addColumn(tableName: "vendor") {
            column(name: "ven_email_orders", type: "boolean")
        }
    }

    changeSet(author: "galffy (hand-coded)", id: "1737550537746-2") {
        grailsChange {
            change {
                sql.execute('ALTER TABLE IF EXISTS vendor RENAME ven_prequalification_vol TO ven_prequalification')
            }
            rollback {}
        }
    }

    changeSet(author: "galffy (hand-coded)", id: "1737550537746-3") {
        grailsChange {
            change {
                sql.execute('ALTER TABLE IF EXISTS vendor RENAME ven_prequalification_vol_info TO ven_prequalification_info')
            }
            rollback {}
        }
    }

    changeSet(author: "galffy (hand-coded)", id: "1737550537746-4") {
        grailsChange {
            change {
                sql.execute('update vendor set ven_email_orders = false')
            }
            rollback {}
        }
    }

    changeSet(author: "galffy (modified)", id: "1737550537746-5") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_email_orders", tableName: "vendor", validate: "true")
    }
}

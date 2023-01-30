databaseChangeLog = {


    changeSet(author: "djebeniani (generated)", id: "1675094079672-1") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table component_price rename to "tipp_price";''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675094079672-2") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column id to tp_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675094079672-3") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column version to tp_version;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675094079672-4") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column cp_owner_component_fk to tp_tipp_fk;''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (generated)", id: "1675094079672-5") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column cp_start_date to tp_start_date;''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (generated)", id: "1675094079672-6") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column cp_currency_fk to tp_currency_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675094079672-7") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column cp_date_created to tp_date_created;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675094079672-8") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column cp_type_fk to tp_type_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675094079672-9") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column cp_price to tp_price;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675094079672-10") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column cp_last_updated to tp_last_updated;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675094079672-11") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "tipp_price"
    rename column cp_end_date to tp_end_date;''')
            }
            rollback {}
        }
    }
}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1738165302788-1") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "platform"
                        rename column plat_sushi_api_authentication_method to plat_counter_api_authentication_method;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1738165302788-2") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "platform"
                        rename column plat_counter_r4_sushi_api_supported_fk_rv to plat_counter_r4_counter_api_supported_fk_rv;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1738165302788-3") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "platform"
                        rename column plat_counter_r4_sushi_server_url to plat_counter_r4_counter_server_url;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1738165302788-4") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "platform"
                        rename column plat_counter_r5_sushi_api_supported_fk_rv to plat_counter_r5_counter_api_supported_fk_rv;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1738165302788-5") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "platform"
                        rename column plat_counter_r5_sushi_platform to plat_counter_r5_counter_platform;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1738165302788-6") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "platform"
                        rename column plat_counter_r5_sushi_server_url to plat_counter_r5_counter_server_url;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1738165302788-6") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "platform"
                        rename column plat_counter_r5_sushi_server_url to plat_counter_r5_counter_server_url;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1738165302788-7") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'Platform.Counter.Api.Authentication.Method' where rdc_description = 'Platform.Sushi.Api.Authentication.Method'; ''')
            }
            rollback {}
        }
    }


}

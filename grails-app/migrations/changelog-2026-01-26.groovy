databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1769416878250-1") {
        grailsChange {
            change {

                sql.executeUpdate('alter table org rename column org_url_prist_lists to "org_url_price_lists";')

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1769416878250-2") {
        grailsChange {
            change {

                sql.executeUpdate('alter table org rename column org_forwarding_usage_statistcs to "org_forwarding_usage_statistics";')

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1769416878250-3") {
        grailsChange {
            change {

                sql.executeUpdate('alter table platform rename column plat_forwarding_usage_statistcs_fk_rv to "plat_forwarding_usage_statistics_fk_rv";')

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1769416878250-4") {
        dropColumn(columnName: "org_alert_exchange_ebook_packages", tableName: "org")
    }


    changeSet(author: "djebeniani (generated)", id: "1769416878250-5") {
        dropColumn(columnName: "org_url_title_lists", tableName: "org")
    }

}

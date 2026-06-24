databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1780502263946-1") {
        grailsChange {
            change {

                String query = "update kbart_source set ks_frequency_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Source.Frequency') and rdv_value = 'Daily') where ks_frequency_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Source.Frequency') and rdv_value = 'Yearly')"

                int count = sql.executeUpdate(query)
                String info = "countYearlyFrquency -> ${count}"
                confirm("${query} :${info}")
                changeSet.setComments(info)
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1780502263946-2") {
        grailsChange {
            change {

                String query = "update kbart_source set ks_frequency_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Source.Frequency') and rdv_value = 'Daily') where ks_frequency_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Source.Frequency') and rdv_value = 'Quarterly')"

                int count = sql.executeUpdate(query)
                String info = "countQuarterlyFrquency -> ${count}"
                confirm("${query} :${info}")
                changeSet.setComments(info)
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1780502263946-3") {
        grailsChange {
            change {

                String query = "update update_package_info set upi_frequency = null where upi_frequency = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Source.Frequency') and rdv_value = 'Yearly')"

                int count = sql.executeUpdate(query)
                String info = "countQuarterlyFrquency -> ${count}"
                confirm("${query} :${info}")
                changeSet.setComments(info)
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1780502263946-4") {
        grailsChange {
            change {

                String query = "update update_package_info set upi_frequency = null where upi_frequency = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Source.Frequency') and rdv_value = 'Quarterly')"

                int count = sql.executeUpdate(query)
                String info = "countQuarterlyFrquency -> ${count}"
                confirm("${query} :${info}")
                changeSet.setComments(info)
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1780502263946-5") {
        grailsChange {
            change {

                String query = "delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Source.Frequency') and rdv_value = 'Yearly'"

                int count = sql.executeUpdate(query)
                String info = "deleteRefdataValueYearlyFrquency -> ${count}"
                confirm("${query} :${info}")
                changeSet.setComments(info)
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1780502263946-6") {
        grailsChange {
            change {

                String query = "delete from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Source.Frequency') and rdv_value = 'Quarterly'"

                int count = sql.executeUpdate(query)
                String info = "deleteRefdataValueQuarterlyFrquency -> ${count}"
                confirm("${query} :${info}")
                changeSet.setComments(info)
            }
            rollback {}
        }
    }
}

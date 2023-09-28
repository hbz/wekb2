databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1693497376546-1") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_ftp_file_name", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1693497376546-2") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_ftp_password", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1693497376546-3") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_ftp_server_url", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1693497376546-4") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_ftp_username", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1693497376546-5") {
        addColumn(tableName: "kbart_source") {
            column(name: "ks_ftp_directory", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1693497376546-6") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_update_from_file_upload", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1693497376546-7") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_update_from_ftp", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1693497376546-8") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_update_from_url", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1693497376546-9") {
        grailsChange {
            change {
                sql.executeUpdate('''UPDATE update_package_info
                                    SET upi_update_from_file_upload  = false, 
                                    upi_update_from_url = false,
                                    upi_update_from_ftp = false;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1693497376546-10") {
        grailsChange {
            change {
                sql.executeUpdate('''UPDATE update_package_info
                                    SET upi_update_from_file_upload  = true
                                    WHERE upi_automatic_update = false;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1693497376546-11") {
        grailsChange {
            change {
                sql.executeUpdate('''UPDATE update_package_info
                                    SET upi_update_from_url  = true
                                    WHERE upi_automatic_update = true;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1693497376546-12") {
        grailsChange {
            change {
                Integer count = sql.executeUpdate("update kbart_source set ks_default_supply_method_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Source.DataSupplyMethod') and rdv_value = 'HTTP Url') WHERE ks_url != null AND ks_url != ''")

                confirm("update kbart_source set ks_default_supply_method_rv_fk = HTTP Url : ${count}")
                changeSet.setComments("update kbart_source set ks_default_supply_method_rv_fk = HTTP Url: ${count}")
            }
        }
        rollback {}
    }

}

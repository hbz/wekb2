databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1708675359058-1") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "vendor"
    rename column web_shop_orders to ven_web_shop_orders;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-2") {
        addNotNullConstraint(columnDataType: "clob", columnName: "pkg_normname", tableName: "package", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-3") {
        addNotNullConstraint(columnDataType: "int", columnName: "upi_count_changed_tipps", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-4") {
        addNotNullConstraint(columnDataType: "int", columnName: "upi_count_invalid_tipps", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-5") {
        addNotNullConstraint(columnDataType: "int", columnName: "upi_count_kbart_rows", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-6") {
        addNotNullConstraint(columnDataType: "int", columnName: "upi_count_new_tipps", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-7") {
        addNotNullConstraint(columnDataType: "int", columnName: "upi_count_now_tipps", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-8") {
        addNotNullConstraint(columnDataType: "int", columnName: "upi_count_previously_tipps", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-9") {
        addNotNullConstraint(columnDataType: "int", columnName: "upi_count_processed_kbart_rows", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-10") {
        addNotNullConstraint(columnDataType: "int", columnName: "upi_count_removed_tipps", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-11") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "upi_only_rows_with_last_changed", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-12") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "upi_update_from_file_upload", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-13") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "upi_update_from_ftp", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-14") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "upi_update_from_url", tableName: "update_package_info", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-15") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_activation_for_new_releases", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-16") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_edi_orders", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-17") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_exchange_of_ind_titles", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-18") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_forw_usage_stati_fr_pub", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-19") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_ind_invoice_design", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-20") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_management_of_credits", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-21") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_paper_invoice", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-22") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_prequalification_vol", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-23") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_pro_of_com_pay", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-24") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_shipping_metadata", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-25") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_technical_support", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-26") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_xml_orders", tableName: "vendor", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1708675359058-27") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "ven_web_shop_orders", tableName: "vendor", validate: "true")
    }
}

databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1737462139895-1") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "FKqiqqq6obmm4mj9eo6nfsbxxhm")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-2") {
        dropColumn(columnName: "plat_counter_r3_supported_fk_rv", tableName: "platform")
    }

    changeSet(author: "galffy (hand-coded)", id: "1737462139895-3") {
        grailsChange {
            change {
                sql.execute('ALTER TABLE IF EXISTS platform RENAME plat_proxy_supported_fk_rv TO plat_other_proxies_fk_rv')
            }
            rollback {}
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-4") {
        addColumn(tableName: "platform") {
            column(name: "plat_dpf_participation_fk_rv", type: "int8")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-5") {
        addColumn(tableName: "platform") {
            column(name: "plat_ex_proxy_fk_rv", type: "int8")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-6") {
        addColumn(tableName: "platform") {
            column(name: "plat_full_text_search_fk_rv", type: "int8")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-7") {
        addColumn(tableName: "platform") {
            column(name: "plat_han_server_fk_rv", type: "int8")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-8") {
        addColumn(tableName: "platform") {
            column(name: "plat_individual_design_logo_fk_rv", type: "int8")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-9") {
        addColumn(tableName: "platform") {
            column(name: "plat_intern_label_for_customer_id", type: "text")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-10") {
        addColumn(tableName: "platform") {
            column(name: "plat_intern_label_for_requestor_key", type: "text")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-11") {
        addColumn(tableName: "platform") {
            column(name: "plat_mail_domain_fk_rv", type: "int8")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-12") {
        addColumn(tableName: "platform") {
            column(name: "plat_platform_blog_url", type: "text")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-13") {
        addColumn(tableName: "platform") {
            column(name: "plat_refeds_support_fk_rv", type: "int8")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-14") {
        addColumn(tableName: "platform") {
            column(name: "plat_referrer_authentification_fk_rv", type: "int8")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-15") {
        addColumn(tableName: "platform") {
            column(name: "plat_rss_url", type: "text")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-16") {
        addColumn(tableName: "platform") {
            column(name: "plat_scc_support_fk_rv", type: "int8")
        }
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-17") {
        addForeignKeyConstraint(baseColumnNames: "plat_full_text_search_fk_rv", baseTableName: "platform", constraintName: "FK36d3qy1ymnr1sfi5lrb289etm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-18") {
        addForeignKeyConstraint(baseColumnNames: "plat_referrer_authentification_fk_rv", baseTableName: "platform", constraintName: "FK67ikny2tudf1c0erecalp1qib", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-19") {
        addForeignKeyConstraint(baseColumnNames: "plat_han_server_fk_rv", baseTableName: "platform", constraintName: "FK8qj4k2t2mcihwj0ulwuhpwht3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-20") {
        addForeignKeyConstraint(baseColumnNames: "plat_ex_proxy_fk_rv", baseTableName: "platform", constraintName: "FKceaovkfhs0dl0lrcgqvho9yra", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-21") {
        addForeignKeyConstraint(baseColumnNames: "plat_refeds_support_fk_rv", baseTableName: "platform", constraintName: "FKfu5imfsxjb254qghivxw2cnpr", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-22") {
        addForeignKeyConstraint(baseColumnNames: "plat_scc_support_fk_rv", baseTableName: "platform", constraintName: "FKh1qx1am1fywbrp93gu6uj37eu", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-23") {
        addForeignKeyConstraint(baseColumnNames: "plat_mail_domain_fk_rv", baseTableName: "platform", constraintName: "FKj42twml23neq3ml53slel0jlw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-24") {
        addForeignKeyConstraint(baseColumnNames: "plat_individual_design_logo_fk_rv", baseTableName: "platform", constraintName: "FKmcktscabsxgqumis9kkgm0mk9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-25") {
        addForeignKeyConstraint(baseColumnNames: "plat_other_proxies_fk_rv", baseTableName: "platform", constraintName: "FKotgx316kt00rnmis9tp8t180r", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1737462139895-26") {
        addForeignKeyConstraint(baseColumnNames: "plat_dpf_participation_fk_rv", baseTableName: "platform", constraintName: "FKs8nc1ngiypa8k82lhoh4eit6t", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }
}

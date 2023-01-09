databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1621438346701-1") {
        addColumn(tableName: "annotation") {
            column(name: "an_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-2") {
        addColumn(tableName: "annotation") {
            column(name: "an_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-3") {
        addColumn(tableName: "additional_property_definition") {
            column(name: "apd_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-4") {
        addColumn(tableName: "additional_property_definition") {
            column(name: "apd_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-5") {
        addColumn(tableName: "curatory_group_watch") {
            column(name: "cgw_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-6") {
        addColumn(tableName: "curatory_group_watch") {
            column(name: "cgw_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-7") {
        addColumn(tableName: "component_like") {
            column(name: "cl_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-8") {
        addColumn(tableName: "component_like") {
            column(name: "cl_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-9") {
        addColumn(tableName: "component_person") {
            column(name: "cp_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-10") {
        addColumn(tableName: "component_person") {
            column(name: "cp_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-11") {
        addColumn(tableName: "component_statistic") {
            column(name: "cs_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-12") {
        addColumn(tableName: "component_statistic") {
            column(name: "cs_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-13") {
        addColumn(tableName: "kbcomponent_variant_name") {
            column(name: "cvn_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-14") {
        addColumn(tableName: "kbcomponent_variant_name") {
            column(name: "cvn_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-15") {
        addColumn(tableName: "component_watch") {
            column(name: "cw_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-16") {
        addColumn(tableName: "component_watch") {
            column(name: "cw_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-17") {
        addColumn(tableName: "web_hook_endpoint") {
            column(name: "ep_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-18") {
        addColumn(tableName: "web_hook_endpoint") {
            column(name: "ep_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-19") {
        addColumn(tableName: "identifier_namespace") {
            column(name: "idns_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-20") {
        addColumn(tableName: "identifier_namespace") {
            column(name: "idns_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-21") {
        addColumn(tableName: "job_result") {
            column(name: "jr_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-22") {
        addColumn(tableName: "job_result") {
            column(name: "jr_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-23") {
        addColumn(tableName: "kbcomponent_additional_property") {
            column(name: "kbcap_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-24") {
        addColumn(tableName: "kbcomponent_additional_property") {
            column(name: "kbcap_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-25") {
        addColumn(tableName: "refdata_category") {
            column(name: "rdc_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-26") {
        addColumn(tableName: "refdata_category") {
            column(name: "rdc_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-27") {
        addColumn(tableName: "refdata_value") {
            column(name: "rdv_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-28") {
        addColumn(tableName: "refdata_value") {
            column(name: "rdv_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-29") {
        addColumn(tableName: "saved_search") {
            column(name: "ss_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-30") {
        addColumn(tableName: "saved_search") {
            column(name: "ss_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-31") {
        addColumn(tableName: "tippcoverage_statement") {
            column(name: "tipp_coverage_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-32") {
        addColumn(tableName: "tippcoverage_statement") {
            column(name: "tipp_coverage_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-33") {
        addColumn(tableName: "user_organisation_membership") {
            column(name: "uom_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-34") {
        addColumn(tableName: "user_organisation_membership") {
            column(name: "uom_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-35") {
        addColumn(tableName: "user_role") {
            column(name: "ur_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-36") {
        addColumn(tableName: "user_role") {
            column(name: "ur_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-37") {
        addColumn(tableName: "update_token") {
            column(name: "ut_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-38") {
        addColumn(tableName: "update_token") {
            column(name: "ut_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-39") {
        addColumn(tableName: "web_hook") {
            column(name: "wh_date_created", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-40") {
        addColumn(tableName: "web_hook") {
            column(name: "wh_last_updated", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-41") {
        dropForeignKeyConstraint(baseTableName: "org_role", constraintName: "fk9wmw2o7u1vkywvmb7hngsgkat")
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-42") {
        dropForeignKeyConstraint(baseTableName: "note_mention", constraintName: "fknnspwa158gka2oxh14t07p9lj")
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-43") {
        dropTable(tableName: "bulk_loader_config")
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-44") {
        dropTable(tableName: "note_mention")
    }

    changeSet(author: "djebeniani (generated)", id: "1621438346701-45") {
        dropTable(tableName: "org_role")
    }
}

databaseChangeLog = {

    //curatory group
    changeSet(author: "djebeniani (modified)", id: "1675696264264-1") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "curatory_group"
    rename column kbc_id to cg_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-2") {
        addColumn(tableName: "curatory_group") {
            column(name: "cg_date_created", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-3") {
        addColumn(tableName: "curatory_group") {
            column(name: "cg_last_updated", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-4") {
        addColumn(tableName: "curatory_group") {
            column(name: "cg_name", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-5") {
        addColumn(tableName: "curatory_group") {
            column(name: "cg_status_rv_fk", type: "int8") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-6") {
        addColumn(tableName: "curatory_group") {
            column(name: "cg_uuid", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-7") {
        addColumn(tableName: "curatory_group") {
            column(name: "cg_version", type: "int8") {

            }
        }
    }


/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-8") {
        addForeignKeyConstraint(baseColumnNames: "cgpl_platform_fk", baseTableName: "curatory_group_platform", constraintName: "FK2bu6nh8uipck8anm8dvbasrkv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "plat_id", referencedTableName: "platform", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-9") {
        addForeignKeyConstraint(baseColumnNames: "cgo_curatory_group_fk", baseTableName: "curatory_group_org", constraintName: "FK3x2ltdi9qax95yhc42dllc75e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "cg_id", referencedTableName: "curatory_group", validate: "true")
    }*/

/*
    changeSet(author: "djebeniani (generated)", id: "1675696264264-10") {
        addForeignKeyConstraint(baseColumnNames: "cgo_org_fk", baseTableName: "curatory_group_org", constraintName: "FK69b076421hlbqrdda72m7shp9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }
*/

    changeSet(author: "djebeniani (generated)", id: "1675696264264-11") {
        addForeignKeyConstraint(baseColumnNames: "cg_status_rv_fk", baseTableName: "curatory_group", constraintName: "FK7fvyfyymdlnqc3onb374xmwtm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-12") {
        addForeignKeyConstraint(baseColumnNames: "cgu_curatory_group_fk", baseTableName: "curatory_group_user", constraintName: "FKcbxls8n9l3i1jnlqjexvw3hf5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "cg_id", referencedTableName: "curatory_group", validate: "true")
    }

/*
    changeSet(author: "djebeniani (generated)", id: "1675696264264-13") {
        addForeignKeyConstraint(baseColumnNames: "cgp_curatory_group_fk", baseTableName: "curatory_group_package", constraintName: "FKhn80wopnug4kipboh22cxvjls", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "cg_id", referencedTableName: "curatory_group", validate: "true")
    }
*/

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-14") {
        addForeignKeyConstraint(baseColumnNames: "cgpl_curatory_group_fk", baseTableName: "curatory_group_platform", constraintName: "FKk0whaf20dv9j5olknre3879eu", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "cg_id", referencedTableName: "curatory_group", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-15") {
        addForeignKeyConstraint(baseColumnNames: "cgp_pkg_fk", baseTableName: "curatory_group_package", constraintName: "FKkil080hpctnbc9l7i2pouqo3n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }*/


    changeSet(author: "djebeniani (generated)", id: "1675696264264-16") {
        addForeignKeyConstraint(baseColumnNames: "cgks_curatory_group_fk", baseTableName: "curatory_group_kbart_source", constraintName: "FKo25tj02pq1eflgow77komyieh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "cg_id", referencedTableName: "curatory_group", validate: "true")
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-17") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update curatory_group set cg_name = (select kbc_name from kbcomponent where kbc_id = cg_id and kbc_name is not null)''')

                confirm("set cg_name from curatory_group: ${countUpdate}")
                changeSet.setComments("set cg_name from curatory_group: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-18") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update curatory_group set cg_date_created = (select kbc_date_created from kbcomponent where kbc_id = cg_id and kbc_date_created is not null)''')

                confirm("set cg_date_created from curatory_group: ${countUpdate}")
                changeSet.setComments("set cg_date_created from curatory_group: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-19") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update curatory_group set cg_last_updated = (select kbc_last_updated from kbcomponent where kbc_id = cg_id and kbc_last_updated is not null)''')

                confirm("set cg_last_updated from curatory_group: ${countUpdate}")
                changeSet.setComments("set cg_last_updated from curatory_group: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-20") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update curatory_group set cg_status_rv_fk = (select kbc_status_rv_fk from kbcomponent where kbc_id = cg_id and kbc_status_rv_fk is not null)''')

                confirm("set cg_status_rv_fk from curatory_group: ${countUpdate}")
                changeSet.setComments("set cg_status_rv_fk from curatory_group: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-21") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update curatory_group set cg_uuid = (select kbc_uuid from kbcomponent where kbc_id = cg_id and kbc_uuid is not null)''')

                confirm("set cg_uuid from curatory_group: ${countUpdate}")
                changeSet.setComments("set cg_uuid from curatory_group: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-22") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update curatory_group set cg_version = (select kbc_version from kbcomponent where kbc_id = cg_id and kbc_version is not null)''')

                confirm("set cg_version from curatory_group: ${countUpdate}")
                changeSet.setComments("set cg_version from curatory_group: ${countUpdate}")
            }
            rollback {}
        }
    }

    //org
    changeSet(author: "djebeniani (modified)", id: "1675696264264-23") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "org"
    rename column kbc_id to org_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-24") {
        addColumn(tableName: "org") {
            column(name: "org_date_created", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-25") {
        addColumn(tableName: "org") {
            column(name: "org_last_updated", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-26") {
        addColumn(tableName: "org") {
            column(name: "org_name", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-27") {
        addColumn(tableName: "org") {
            column(name: "org_status_rv_fk", type: "int8") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-28") {
        addColumn(tableName: "org") {
            column(name: "org_uuid", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-29") {
        addColumn(tableName: "org") {
            column(name: "org_version", type: "int8") {

            }
        }
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-30") {
        addForeignKeyConstraint(baseColumnNames: "id_org_fk", baseTableName: "identifier_new", constraintName: "FK5a0jd1a5jm7luhvfoov6kk1dm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675696264264-31") {
        addForeignKeyConstraint(baseColumnNames: "org_roles_id", baseTableName: "org_refdata_value", constraintName: "FKb74yq0mjgju7rovi2vru0iig9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-32") {
        addForeignKeyConstraint(baseColumnNames: "ct_org_fk", baseTableName: "contact", constraintName: "FKolaxb1rbuquu3r1cx892to7o7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675696264264-33") {
        addForeignKeyConstraint(baseColumnNames: "org_status_rv_fk", baseTableName: "org", constraintName: "FKra3m2apm69oowfslefmfgtb87", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-34") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update org set org_name = (select kbc_name from kbcomponent where kbc_id = org_id and kbc_name is not null)''')

                confirm("set org_name from org: ${countUpdate}")
                changeSet.setComments("set org_name from org: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-35") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update org set org_date_created = (select kbc_date_created from kbcomponent where kbc_id = org_id and kbc_date_created is not null)''')

                confirm("set org_date_created from org: ${countUpdate}")
                changeSet.setComments("set org_date_created from org: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-36") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update org set org_last_updated = (select kbc_last_updated from kbcomponent where kbc_id = org_id and kbc_last_updated is not null)''')

                confirm("set org_last_updated from org: ${countUpdate}")
                changeSet.setComments("set org_last_updated from org: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-37") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update org set org_status_rv_fk = (select kbc_status_rv_fk from kbcomponent where kbc_id = org_id and kbc_status_rv_fk is not null)''')

                confirm("set org_status_rv_fk from org: ${countUpdate}")
                changeSet.setComments("set org_status_rv_fk from org: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-38") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update org set org_uuid = (select kbc_uuid from kbcomponent where kbc_id = org_id and kbc_uuid is not null)''')

                confirm("set org_uuid from org: ${countUpdate}")
                changeSet.setComments("set org_uuid from org: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-39") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update org set org_version = (select kbc_version from kbcomponent where kbc_id = org_id and kbc_version is not null)''')

                confirm("set org_version from org: ${countUpdate}")
                changeSet.setComments("set org_version from org: ${countUpdate}")
            }
            rollback {}
        }
    }

    //pkg
    changeSet(author: "djebeniani (modified)", id: "1675696264264-40") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "package"
    rename column kbc_id to pkg_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-41") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "package"
    rename column pkg_open_access to pkg_open_access_rv_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-42") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "package"
    rename column pkg_file to pkg_file_rv_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-43") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "package"
    rename column content_type_id to pkg_content_type_rv_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-44") {
        addColumn(tableName: "package") {
            column(name: "pkg_normname", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-45") {
        addColumn(tableName: "package") {
            column(name: "pkg_date_created", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-46") {
        addColumn(tableName: "package") {
            column(name: "pkg_description", type: "text") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-47") {
        addColumn(tableName: "package") {
            column(name: "pkg_last_update_comment", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-48") {
        addColumn(tableName: "package") {
            column(name: "pkg_last_updated", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-49") {
        addColumn(tableName: "package") {
            column(name: "pkg_name", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-50") {
        addColumn(tableName: "package") {
            column(name: "pkg_status_rv_fk", type: "int8") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-51") {
        addColumn(tableName: "package") {
            column(name: "pkg_uuid", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-52") {
        addColumn(tableName: "package") {
            column(name: "pkg_version", type: "int8") {

            }
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-53") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_name = (select kbc_name from kbcomponent where kbc_id = pkg_id and kbc_name is not null)''')

                confirm("set pkg_name from package: ${countUpdate}")
                changeSet.setComments("set pkg_name from package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-54") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_date_created = (select kbc_date_created from kbcomponent where kbc_id = pkg_id and kbc_date_created is not null)''')

                confirm("set pkg_date_created from package: ${countUpdate}")
                changeSet.setComments("set pkg_date_created from package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-55") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_last_updated = (select kbc_last_updated from kbcomponent where kbc_id = pkg_id and kbc_last_updated is not null)''')

                confirm("set pkg_last_updated from package: ${countUpdate}")
                changeSet.setComments("set pkg_last_updated from package: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-56") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_status_rv_fk = (select kbc_status_rv_fk from kbcomponent where kbc_id = pkg_id and kbc_status_rv_fk is not null)''')

                confirm("set pkg_status_rv_fk from package: ${countUpdate}")
                changeSet.setComments("set pkg_status_rv_fk from package: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-57") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_uuid = (select kbc_uuid from kbcomponent where kbc_id = pkg_id and kbc_uuid is not null)''')

                confirm("set pkg_uuid from package: ${countUpdate}")
                changeSet.setComments("set pkg_uuid from package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-58") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_version = (select kbc_version from kbcomponent where kbc_id = pkg_id and kbc_version is not null)''')

                confirm("set pkg_version from package: ${countUpdate}")
                changeSet.setComments("set pkg_version from package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-59") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_last_update_comment = (select kbc_last_update_comment from kbcomponent where kbc_id = pkg_id and kbc_last_update_comment is not null)''')

                confirm("set pkg_last_update_comment from package: ${countUpdate}")
                changeSet.setComments("set pkg_last_update_comment from package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-60") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_normname = (select kbc_normname from kbcomponent where kbc_id = pkg_id and kbc_normname is not null)''')

                confirm("set pkg_normname from package: ${countUpdate}")
                changeSet.setComments("set pkg_normname from package: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-61") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update package set pkg_description = (select kbc_description from kbcomponent where kbc_id = pkg_id and kbc_description is not null)''')

                confirm("set pkg_description from package: ${countUpdate}")
                changeSet.setComments("set pkg_description from package: ${countUpdate}")
            }
            rollback {}
        }
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-62") {
        addForeignKeyConstraint(baseColumnNames: "paa_pkg_fk", baseTableName: "package_archiving_agency", constraintName: "FK2mg91xq0t2sdow7x3yya1go8m", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-63") {
        addForeignKeyConstraint(baseColumnNames: "pkg_platform_fk", baseTableName: "package", constraintName: "FK5xmwuoqqyun14gk0qdjiop3g8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "plat_id", referencedTableName: "platform", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675696264264-64") {
        addForeignKeyConstraint(baseColumnNames: "pkg_open_access_rv_fk", baseTableName: "package", constraintName: "FK6dh1njsymmw0j9lxfq4i01l6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-65") {
        addForeignKeyConstraint(baseColumnNames: "package_fk", baseTableName: "package_regional_range", constraintName: "FKc2h783bogobxuld6ax7rj4sa4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675696264264-66") {
        addForeignKeyConstraint(baseColumnNames: "pkg_file_rv_fk", baseTableName: "package", constraintName: "FKio2hhdyo9lnkoer292rcg4eal", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-67") {
        addForeignKeyConstraint(baseColumnNames: "id_pkg_fk", baseTableName: "identifier_new", constraintName: "FKlu1hcsqev8n4715hvi88gwgxl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-68") {
        addForeignKeyConstraint(baseColumnNames: "package_fk", baseTableName: "package_national_range", constraintName: "FKlxoj4vs3pcxvtly1hpj43dt83", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }*/

/*
    changeSet(author: "djebeniani (generated)", id: "1675696264264-69") {
        addForeignKeyConstraint(baseColumnNames: "pkg_provider_fk", baseTableName: "package", constraintName: "FKmxamrrttlt0bq2eu1ioesegg2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }
*/

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-70") {
        addForeignKeyConstraint(baseColumnNames: "upi_pkg_fk", baseTableName: "update_package_info", constraintName: "FKqdhd3y5x2guk6aw8lx8iajpor", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675696264264-71") {
        addForeignKeyConstraint(baseColumnNames: "pkg_status_rv_fk", baseTableName: "package", constraintName: "FKqovkmd948w78qg98cr0pliur", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }


/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-72") {
        addForeignKeyConstraint(baseColumnNames: "package_fk", baseTableName: "package_dewey_decimal_classification", constraintName: "FKs2lrf2p3exmxx7kx88naycciy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }*/


    //component_variant_name
    changeSet(author: "djebeniani (generated)", id: "1675696264264-73") {
        addColumn(tableName: "component_variant_name") {
            column(name: "cvn_org_fk", type: "int8") {
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-74") {
        addColumn(tableName: "component_variant_name") {
            column(name: "cvn_pkg_fk", type: "int8") {
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-75") {
        addForeignKeyConstraint(baseColumnNames: "cvn_org_fk", baseTableName: "component_variant_name", constraintName: "FK893y707mq9uh1y3r4iss8vnkh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-76") {
        addForeignKeyConstraint(baseColumnNames: "cvn_pkg_fk", baseTableName: "component_variant_name", constraintName: "FKb01o9vwhs342w4h8iseq9me7u", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }

    //platform
    changeSet(author: "djebeniani (modified)", id: "1675696264264-77") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "platform"
    rename column kbc_id to plat_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-78") {
        addColumn(tableName: "platform") {
            column(name: "plat_date_created", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-79") {
        addColumn(tableName: "platform") {
            column(name: "plat_last_updated", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-80") {
        addColumn(tableName: "platform") {
            column(name: "plat_name", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-81") {
        addColumn(tableName: "platform") {
            column(name: "plat_status_rv_fk", type: "int8") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-82") {
        addColumn(tableName: "platform") {
            column(name: "plat_uuid", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-83") {
        addColumn(tableName: "platform") {
            column(name: "plat_version", type: "int8") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-84") {
        addForeignKeyConstraint(baseColumnNames: "plat_status_rv_fk", baseTableName: "platform", constraintName: "FKgg8noq7wd3lugstp0jxh82r0l", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-85") {
        addForeignKeyConstraint(baseColumnNames: "id_platform_fk", baseTableName: "identifier_new", constraintName: "FKndieavn0o5cjdb78lpg8jvsgb", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "plat_id", referencedTableName: "platform", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675696264264-86") {
        addForeignKeyConstraint(baseColumnNames: "platform_roles_id", baseTableName: "platform_refdata_value", constraintName: "FKrge4rn0uftw4dbd1444ljyj20", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "plat_id", referencedTableName: "platform", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-87") {
        addForeignKeyConstraint(baseColumnNames: "plat_provider_fk", baseTableName: "platform", constraintName: "FKrgirc6hvu9v50t0wqoylcrltf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org", validate: "true")
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-88") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update platform set plat_name = (select kbc_name from kbcomponent where kbc_id = plat_id and kbc_name is not null)''')

                confirm("set plat_name from platform: ${countUpdate}")
                changeSet.setComments("set plat_name from platform: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-89") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update platform set plat_date_created = (select kbc_date_created from kbcomponent where kbc_id = plat_id and kbc_date_created is not null)''')

                confirm("set plat_date_created from platform: ${countUpdate}")
                changeSet.setComments("set plat_date_created from platform: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-90") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update platform set plat_last_updated = (select kbc_last_updated from kbcomponent where kbc_id = plat_id and kbc_last_updated is not null)''')

                confirm("set plat_last_updated from platform: ${countUpdate}")
                changeSet.setComments("set plat_last_updated from platform: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-91") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update platform set plat_status_rv_fk = (select kbc_status_rv_fk from kbcomponent where kbc_id = plat_id and kbc_status_rv_fk is not null)''')

                confirm("set plat_status_rv_fk from platform: ${countUpdate}")
                changeSet.setComments("set plat_status_rv_fk from platform: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-92") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update platform set plat_uuid = (select kbc_uuid from kbcomponent where kbc_id = plat_id and kbc_uuid is not null)''')

                confirm("set plat_uuid from platform: ${countUpdate}")
                changeSet.setComments("set plat_uuid from platform: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-93") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update platform set plat_version = (select kbc_version from kbcomponent where kbc_id = plat_id and kbc_version is not null)''')

                confirm("set plat_version from platform: ${countUpdate}")
                changeSet.setComments("set plat_version from platform: ${countUpdate}")
            }
            rollback {}
        }
    }

    //tipp

    changeSet(author: "djebeniani (modified)", id: "1675696264264-94") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "title_instance_package_platform"
    rename column kbc_id to tipp_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-95") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_date_created", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-96") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_last_updated", type: "timestamp") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-97") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_name", type: "text") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-98") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_status_rv_fk", type: "int8") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-99") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_uuid", type: "varchar(255)") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-100") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_version", type: "int8") {

            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1675696264264-101") {
        addForeignKeyConstraint(baseColumnNames: "tipp_status_rv_fk", baseTableName: "title_instance_package_platform", constraintName: "FK338weu6wq228ssq2sd1eoe7va", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-102") {
        addForeignKeyConstraint(baseColumnNames: "tipp_fk", baseTableName: "tipp_dewey_decimal_classification", constraintName: "FK6if9ut9kn3xdlbumqp9xne3tg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-103") {
        addForeignKeyConstraint(baseColumnNames: "tipp_pkg_fk", baseTableName: "title_instance_package_platform", constraintName: "FK9rad3hn4ct51x6d2nruxxcakq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package", validate: "true")
    }*/

/*
    changeSet(author: "djebeniani (generated)", id: "1675696264264-104") {
        addForeignKeyConstraint(baseColumnNames: "id_tipp_fk", baseTableName: "identifier_new", constraintName: "FKb5fu4vcuof6tm7ie2jfpi85ju", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }
*/

/*
    changeSet(author: "djebeniani (generated)", id: "1675696264264-105") {
        addForeignKeyConstraint(baseColumnNames: "uti_tipp_fk", baseTableName: "update_tipp_info", constraintName: "FKc22x4csc0cbqx9h8903vky6gg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }
*/

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-106") {
        addForeignKeyConstraint(baseColumnNames: "cl_tipp_fk", baseTableName: "component_language", constraintName: "FKfucdvncfc6aa65cesxvcameiy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1675696264264-107") {
        addForeignKeyConstraint(baseColumnNames: "owner_id", baseTableName: "tippcoverage_statement", constraintName: "FKgtd7sq3sgr1sydtmjuqauy7h6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-108") {
        addForeignKeyConstraint(baseColumnNames: "tp_tipp_fk", baseTableName: "tipp_price", constraintName: "FKh6onpwjyyawuss7fe0cnqnyj9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "tipp_id", referencedTableName: "title_instance_package_platform", validate: "true")
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1675696264264-109") {
        addForeignKeyConstraint(baseColumnNames: "tipp_host_platform_fk", baseTableName: "title_instance_package_platform", constraintName: "FKoiotwfahqljocmuksac3p5kov", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "plat_id", referencedTableName: "platform", validate: "true")
    }*/

    changeSet(author: "djebeniani (modified)", id: "1675696264264-110") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_name = (select kbc_name from kbcomponent where kbc_id = tipp_id and kbc_name is not null)''')

                confirm("set tipp_name from title_instance_package_platform: ${countUpdate}")
                changeSet.setComments("set tipp_name from title_instance_package_platform: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-111") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_date_created = (select kbc_date_created from kbcomponent where kbc_id = tipp_id and kbc_date_created is not null)''')

                confirm("set tipp_date_created from title_instance_package_platform: ${countUpdate}")
                changeSet.setComments("set tipp_date_created from title_instance_package_platform: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-112") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_last_updated = (select kbc_last_updated from kbcomponent where kbc_id = tipp_id and kbc_last_updated is not null)''')

                confirm("set tipp_last_updated from title_instance_package_platform: ${countUpdate}")
                changeSet.setComments("set tipp_last_updated from title_instance_package_platform: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-113") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_status_rv_fk = (select kbc_status_rv_fk from kbcomponent where kbc_id = tipp_id and kbc_status_rv_fk is not null)''')

                confirm("set tipp_status_rv_fk from title_instance_package_platform: ${countUpdate}")
                changeSet.setComments("set tipp_status_rv_fk from title_instance_package_platform: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1675696264264-114") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_uuid = (select kbc_uuid from kbcomponent where kbc_id = tipp_id and kbc_uuid is not null)''')

                confirm("set tipp_uuid from title_instance_package_platform: ${countUpdate}")
                changeSet.setComments("set tipp_uuid from title_instance_package_platform: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-115") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_version = (select kbc_version from kbcomponent where kbc_id = tipp_id and kbc_version is not null)''')

                confirm("set tipp_version from title_instance_package_platform: ${countUpdate}")
                changeSet.setComments("set tipp_version from title_instance_package_platform: ${countUpdate}")
            }
            rollback {}
        }
    }

    //variant name transfer

    changeSet(author: "djebeniani (modified)", id: "1675696264264-116") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update component_variant_name set cvn_org_fk = (select org_id from org where org_id = cvn_kbc_fk and cvn_kbc_fk is not null)''')

                confirm("set cvn_org_fk from component_variant_name: ${countUpdate}")
                changeSet.setComments("set cvn_org_fk from component_variant_name: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1675696264264-117") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update component_variant_name set cvn_pkg_fk = (select pkg_id from package where pkg_id = cvn_kbc_fk and cvn_kbc_fk is not null)''')

                confirm("set cvn_pkg_fk from component_variant_name: ${countUpdate}")
                changeSet.setComments("set cvn_pkg_fk from component_variant_name: ${countUpdate}")
            }
            rollback {}
        }
    }

}

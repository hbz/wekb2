
databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1617261017626-1") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set date_first_online = subquery.bk_datefirstonline from (select bi.bk_datefirstonline, tipp.kbc_id from book_instance as bi
                                join combo on bi.kbc_id = combo_from_fk
                                join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_datefirstonline is not null and tipp.date_first_online is null ) AS subquery
                                WHERE title_instance_package_platform.kbc_id=subquery.kbc_id and title_instance_package_platform.date_first_online is null;''')

                confirm("copy date_first_online from title to tipp: ${countUpdate}")
                changeSet.setComments("copy date_first_online from title to tipp: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-2") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set date_first_in_print = subquery.bk_datefirstinprint from (select bi.bk_datefirstinprint, tipp.kbc_id from book_instance as bi
                                   join combo on bi.kbc_id = combo_from_fk
                                   join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_datefirstinprint is not null and tipp.date_first_in_print is null ) AS subquery
                                WHERE title_instance_package_platform.kbc_id=subquery.kbc_id and title_instance_package_platform.date_first_in_print is null;''')

                confirm("copy date_first_in_print from title to tipp: ${countUpdate}")
                changeSet.setComments("copy date_first_in_print from title to tipp: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-3") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_volume_number = subquery.bk_volume from (select bi.bk_volume, tipp.kbc_id from book_instance as bi
                                   join combo on bi.kbc_id = combo_from_fk
                                   join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_volume is not null and tipp.tipp_volume_number is null ) AS subquery
                                WHERE title_instance_package_platform.kbc_id=subquery.kbc_id and title_instance_package_platform.tipp_volume_number is null;''')

                confirm("copy tipp_volume_number from title to tipp: ${countUpdate}")
                changeSet.setComments("copy tipp_volume_number from title to tipp: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-4") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_first_editor = subquery.bk_firsteditor from (select bi.bk_firsteditor, tipp.kbc_id from book_instance as bi
                                   join combo on bi.kbc_id = combo_from_fk
                                   join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_firsteditor is not null and tipp.tipp_first_editor is null ) AS subquery
                                WHERE title_instance_package_platform.kbc_id=subquery.kbc_id and title_instance_package_platform.tipp_first_editor is null;''')

                confirm("copy tipp_first_editor from title to tipp: ${countUpdate}")
                changeSet.setComments("copy tipp_first_editor from title to tipp: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-5") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_first_author = subquery.bk_firstauthor from (select bi.bk_firstauthor, tipp.kbc_id from book_instance as bi
                                   join combo on bi.kbc_id = combo_from_fk
                                   join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_firstauthor is not null and tipp.tipp_first_author is null ) AS subquery
                                WHERE title_instance_package_platform.kbc_id=subquery.kbc_id and title_instance_package_platform.tipp_first_author is null;''')

                confirm("copy tipp_first_author from title to tipp: ${countUpdate}")
                changeSet.setComments("copy tipp_first_author from title to tipp: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-6") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_edition_statement = subquery.bk_editionstatement from (select bi.bk_editionstatement, tipp.kbc_id from book_instance as bi
                                   join combo on bi.kbc_id = combo_from_fk
                                   join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_editionstatement is not null and tipp.tipp_edition_statement is null ) AS subquery
                                WHERE title_instance_package_platform.kbc_id=subquery.kbc_id and title_instance_package_platform.tipp_edition_statement is null;''')

                confirm("copy tipp_edition_statement from title to tipp: ${countUpdate}")
                changeSet.setComments("copy tipp_edition_statement from title to tipp: ${countUpdate}")
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1617261017626-7") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_medium_rv_fk = subquery.medium_id from (select ti.medium_id, tipp.kbc_id from title_instance as ti
                                   join combo on ti.kbc_id = combo_from_fk
                                   join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where ti.kbc_id = combo_from_fk and ti.medium_id is not null and tipp.tipp_medium_rv_fk is null ) AS subquery
                                WHERE title_instance_package_platform.kbc_id=subquery.kbc_id and title_instance_package_platform.tipp_medium_rv_fk is null;''')

                confirm("copy tipp_medium_rv_fk from title to tipp: ${countUpdate}")
                changeSet.setComments("copy tipp_medium_rv_fk from title to tipp: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-8") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update kbcomponent set kbc_name = subquery.kbc_name, kbc_normname = subquery.kbc_normname from (select kbc.kbc_name, kbc.kbc_normname, tipp.kbc_id from kbcomponent as kbc
                                                                                                            join title_instance as ti on ti.kbc_id = kbc.kbc_id
                                                                                                            join combo on ti.kbc_id = combo_from_fk
                                                                                                            join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where ti.kbc_id = combo_from_fk and kbc.kbc_name is not null) AS subquery
                                                                                                            WHERE kbcomponent.kbc_id=subquery.kbc_id and kbcomponent.kbc_name is null;''')

                confirm("copy kbc_name from title to tipp: ${countUpdate}")
                changeSet.setComments("copy kbc_name from title to tipp: ${countUpdate}")
            }
            rollback {}
        }
    }
    changeSet(author: "djebeniani (modified)", id: "1617261017626-9") {
        grailsChange {
            change {
                sql.execute('''INSERT INTO refdata_category (rdc_id, rdc_version, rdc_description, rdc_label) VALUES ((select nextval ('hibernate_sequence')), 0, 'TitleInstancePackagePlatform.PublicationType', 'TitleInstancePackagePlatform.PublicationType');''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-10") {
        grailsChange {
            change {
                sql.execute('''INSERT INTO refdata_value (rdv_id, rdv_version, rdv_owner, rdv_value) VALUES ((select nextval ('hibernate_sequence')), 0, (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType'), 'Database');''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-11") {
        grailsChange {
            change {
                sql.execute('''INSERT INTO refdata_value (rdv_id, rdv_version, rdv_owner, rdv_value) VALUES ((select nextval ('hibernate_sequence')), 0, (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType'), 'Monograph');''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-12") {
        grailsChange {
            change {
                sql.execute('''INSERT INTO refdata_value (rdv_id, rdv_version, rdv_owner, rdv_value) VALUES ((select nextval ('hibernate_sequence')), 0, (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType'), 'Other');''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1617261017626-13") {
        grailsChange {
            change {
                sql.execute('''INSERT INTO refdata_value (rdv_id, rdv_version, rdv_owner, rdv_value) VALUES ((select nextval ('hibernate_sequence')), 0, (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType'), 'Serial');''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1617261017626-14") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_publication_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType') and rdv_value = 'Database')
                                            where kbc_id in (select tipp.kbc_id from database_instance as ti
                                             join combo on ti.kbc_id = combo_from_fk
                                             join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where ti.kbc_id = combo_from_fk) and tipp_publication_type_rv_fk is null;''')

                confirm("set tipp_publication_type_rv_fk = Database of tipp: ${countUpdate}")
                changeSet.setComments("set tipp_publication_type_rv_fk = Database of tipp: ${countUpdate}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-15") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_publication_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType') and rdv_value = 'Monograph')
                                            where kbc_id in (select tipp.kbc_id from book_instance as ti
                                             join combo on ti.kbc_id = combo_from_fk
                                             join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where ti.kbc_id = combo_from_fk) and tipp_publication_type_rv_fk is null;''')

                confirm("set tipp_publication_type_rv_fk = Monograph of tipp: ${countUpdate}")
                changeSet.setComments("set tipp_publication_type_rv_fk = Monograph of tipp: ${countUpdate}")
            }
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-16") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_publication_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType') and rdv_value = 'Serial')
                                            where kbc_id in (select tipp.kbc_id from journal_instance as ti
                                             join combo on ti.kbc_id = combo_from_fk
                                             join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where ti.kbc_id = combo_from_fk) and tipp_publication_type_rv_fk is null;''')

                confirm("set tipp_publication_type_rv_fk = Serial of tipp: ${countUpdate}")
                changeSet.setComments("set tipp_publication_type_rv_fk = Serial of tipp: ${countUpdate}")
            }
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-17") {
        grailsChange {
            change {

                Integer countUpdate = sql.executeUpdate('''update title_instance_package_platform set tipp_publication_type_rv_fk = (Select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'TitleInstancePackagePlatform.PublicationType') and rdv_value = 'Other')
                                            where kbc_id in (select tipp.kbc_id from other_instance as ti
                                             join combo on ti.kbc_id = combo_from_fk
                                             join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where ti.kbc_id = combo_from_fk) and tipp_publication_type_rv_fk is null;''')

                confirm("set tipp_publication_type_rv_fk = Other of tipp: ${countUpdate}")
                changeSet.setComments("set tipp_publication_type_rv_fk = Other of tipp: ${countUpdate}")
            }
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-18") {
        grailsChange {
            change {

                Integer count = 0
                def identifierTitle = sql.rows("select id.kbc_id, id.id_value, id.id_namespace_fk, combo_from_fk as ti from combo join identifier as id on id.kbc_id = combo_to_fk join title_instance as ti on ti.kbc_id = combo_from_fk where combo_type_rv_fk = (select rdv_id from refdata_value where rdv_value = 'KBComponent.Ids')")

                identifierTitle.each { row ->

                    def titleInstanceTipp = sql.rows("select combo_from_fk as title_instance, combo_to_fk as tipp from combo where combo_from_fk = ${row["ti"]} and combo_type_rv_fk = (select rdv_id from refdata_value where rdv_value = 'TitleInstance.Tipps')")

                    titleInstanceTipp.each { tipps ->

                        def identifierTIPP = sql.rows("""select id.kbc_id, id.id_value, id.id_namespace_fk, combo_from_fk as tipp from combo join identifier as id on id.kbc_id = combo_to_fk 
                                                where combo_from_fk = ${tipps["tipp"]} 
                                                and id.id_value = ${row["id_value"]} 
                                                and id.id_namespace_fk = ${row["id_namespace_fk"]}
                                                and combo_type_rv_fk = (select rdv_id from refdata_value where rdv_value = 'KBComponent.Ids')""")


                        if (!(identifierTIPP)) {
                            String namespace_val = row["id_namespace_fk"]
                            String value = row["id_value"]

                            if (namespace_val && value && namespace_val.toLowerCase() != "originediturl") {
                                count++
                                println(count)

                                sql.execute("INSERT INTO combo (combo_id, combo_version, combo_from_fk, combo_to_fk, combo_status_rv_fk, combo_type_rv_fk) VALUES ((select nextval ('hibernate_sequence')), 0, ${tipps["tipp"]}, ${row["kbc_id"]}, (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Status') and rdv_value = 'Active'), (select rdv_id from refdata_value where rdv_value = 'KBComponent.Ids'));")
                            }
                        }
                    }
                }

                confirm("copy identifiers from title to tipp: ${count}")
                changeSet.setComments("copy identifiers from title to tipp: ${count}")
            }
        }
    }

}

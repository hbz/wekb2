import wekb.helper.RCConstants
import wekb.IdentifierNamespace
import wekb.RefdataCategory
import wekb.RefdataValue

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1648154097625-1") {
        createTable(tableName: "identifier_new") {
            column(autoIncrement: "true", name: "id_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "identifier_newPK")
            }

            column(name: "id_version", type: "BIGINT")

            column(name: "id_org_fk", type: "BIGINT")

            column(name: "id_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "id_uuid", type: "TEXT")

            column(name: "id_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "id_namespace_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "id_platform_fk", type: "BIGINT")

            column(name: "id_pkg_fk", type: "BIGINT")

            column(name: "id_tipp_fk", type: "BIGINT")

            column(name: "id_value", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1648154097625-2") {
        addUniqueConstraint(columnNames: "idns_targettype, idns_value", constraintName: "UKf364b72121a4a3b09831ae13f5c3", tableName: "identifier_namespace")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1648154097625-3") {
        createIndex(indexName: "id_namespace_idx", tableName: "identifier_new") {
            column(name: "id_namespace_fk")
        }
    }*/

    changeSet(author: "djebeniani (generated)", id: "1648154097625-4") {
        createIndex(indexName: "id_uuid_idx", tableName: "identifier_new") {
            column(name: "id_uuid")
        }
    }

/*    changeSet(author: "djebeniani (generated)", id: "1648154097625-5") {
        createIndex(indexName: "id_value_idx", tableName: "identifier_new") {
            column(name: "id_value")
        }
    }*/

    changeSet(author: "djebeniani (generated)", id: "1648154097625-6") {
        addForeignKeyConstraint(baseColumnNames: "id_org_fk", baseTableName: "identifier_new", constraintName: "FK5a0jd1a5jm7luhvfoov6kk1dm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "org")
    }

    changeSet(author: "djebeniani (generated)", id: "1648154097625-7") {
        addForeignKeyConstraint(baseColumnNames: "id_namespace_fk", baseTableName: "identifier_new", constraintName: "FKaktvw457hk2ohluaadufpjnt3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1648154097625-8") {
        addForeignKeyConstraint(baseColumnNames: "id_tipp_fk", baseTableName: "identifier_new", constraintName: "FKb5fu4vcuof6tm7ie2jfpi85ju", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1648154097625-9") {
        addForeignKeyConstraint(baseColumnNames: "id_pkg_fk", baseTableName: "identifier_new", constraintName: "FKlu1hcsqev8n4715hvi88gwgxl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1648154097625-10") {
        addForeignKeyConstraint(baseColumnNames: "id_platform_fk", baseTableName: "identifier_new", constraintName: "FKndieavn0o5cjdb78lpg8jvsgb", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "platform")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1648154097625-11") {
        dropForeignKeyConstraint(baseTableName: "identifier_namespace", constraintName: "fk2caxuubaii38vfb7m1tvmwnoc")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1648154097625-12") {
        dropForeignKeyConstraint(baseTableName: "identifier", constraintName: "fk408vsgum5mrg1kfy18dmyxs6e")
    }

    changeSet(author: "djebeniani (modified)", id: "1648154097625-13") {
        grailsChange {
            change {

                RefdataCategory refdataCategory = RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)
                def ns_objs_forPlaftorms = IdentifierNamespace.findAllByFamilyAndTargetType('ttl_prv', RefdataValue.findByValueAndOwner('Title', refdataCategory))



                RefdataValue targetTypeTipp = RefdataValue.construct([token   : 'TitleInstancePackagePlatform',
                                                                      rdc     : refdataCategory.desc,
                                                                      value_de: 'TitleInstancePackagePlatform',
                                                                      value_en: 'TitleInstancePackagePlatform',
                                                                      hardData: true])

                ns_objs_forPlaftorms.each{IdentifierNamespace identifierNamespace ->

                    identifierNamespace.targetType = targetTypeTipp
                    identifierNamespace.save(flush: true)
                }
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1648154097625-14") {
        grailsChange {
            change {

                RefdataCategory refdataCategory = RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)

                def namespaces = [
                        [value: 'cup', name: 'cup', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'dnb', name: 'dnb', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'doi', name: 'DOI', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'eissn', name: 'e-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'ezb', name: 'EZB-ID', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'gnd-id', name: 'gnd-id', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'isbn', name: 'ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'issn', name: 'p-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'issnl', name: 'ISSN-L', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'pisbn', name: 'Print-ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'oclc', name: 'oclc', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'preselect', name: 'preselect', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'zdb', name: 'ZDB-ID', pattern: "^\\d+-[\\dxX]\$", targetType: 'TitleInstancePackagePlatform'],

                        //Kbart Import
                        [value: 'ill_indicator', name: 'Ill Indicator', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'package_isci', name: 'Package ISCI', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'package_isil', name: 'Package ISIL', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'package_ezb_anchor', name: 'EZB Anchor', targetType: 'TitleInstancePackagePlatform'],


                        [value: 'Anbieter_Produkt_ID', name: 'Anbieter_Produkt_ID', targetType: 'Package'],
                        [value: 'dnb', name: 'dnb', targetType: 'Package'],
                        [value: 'doi', name: 'DOI', targetType: 'Package'],
                        [value: 'ezb', name: 'EZB-ID', targetType: 'Package'],
                        [value: 'gvk_ppn', name: 'gvk_ppn', targetType: 'Package'],
                        [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$", targetType: 'Package'],
                        [value: 'package_isci', name: 'Package ISCI', targetType: 'Package'],
                        [value: 'package_ezb_anchor', name: 'EZB Anchor', targetType: 'Package'],
                        [value: 'zdb', name: 'ZDB-ID', pattern: "^\\d+-[\\dxX]\$", targetType: 'Package'],
                        [value: 'zdb_ppn', name: 'EZB Anchor', targetType: 'Package'],


                        [value: 'gnd-id', name: 'gnd-id', targetType: 'Org'],
                        [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$", targetType: 'Org'],
                        [value: 'zdb_ppn', name: 'EZB Anchor', targetType: 'Org'],
                ]

                namespaces.each { ns ->
                    RefdataValue targetType = RefdataValue.findByValueAndOwner(ns.targetType, refdataCategory)
                    def ns_obj = IdentifierNamespace.findByValueAndTargetType(ns.value, targetType)

                    if (targetType && !ns_obj){
                        sql.execute("""insert into identifier_namespace(id, version, idns_value, idns_name, idns_targettype, idns_last_updated, idns_date_created) values
                                   ((select nextval ('hibernate_sequence')), 0, ${ns.value}, ${ns.name}, ${targetType.id}, (select now()), (select now()))""")
                    }
                }


                RefdataValue targetTypeTipp = RefdataValue.findByValueAndOwner('TitleInstancePackagePlatform', refdataCategory)
                RefdataValue targetTypePkg = RefdataValue.findByValueAndOwner('Package', refdataCategory)
                RefdataValue targetTypeOrg = RefdataValue.findByValueAndOwner('Org', refdataCategory)

                Map counts = [:]

                counts.identifierPkgNewCount = 0
                counts.identifierOrgNewCount = 0
                counts.identifierTippNewCount = 0

                def combosCount = sql.rows("select count(combo_id) from combo where " +
                        "combo_status_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Status') and rdv_value = 'Active')" +
                        " and combo_type_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'KBComponent.Ids')")[0]

                counts.identifierComboCount = combosCount.count
                counts.identifierNewCount = 0

                for(int i = 0; i <= combosCount.count; i=i+100000) {

                    List idCombos = sql.rows("select * from combo where " +
                            "combo_status_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Status') and rdv_value = 'Active')" +
                            " and combo_type_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'KBComponent.Ids')" +
                            " ORDER BY combo_id" +
                            " LIMIT 100000 OFFSET ${i}")

                    //println(idCombos.size())
                    println(i)

                    idCombos.each { Map combo ->
                        List pkg = sql.rows("select * from package where kbc_id = ${combo.combo_from_fk}")
                        if (pkg.size() > 0) {
                            List oldIdentifier = sql.rows("select * from identifier where kbc_id = ${combo.combo_to_fk}")
                            oldIdentifier.each { Map identifier ->
                                Map oldNameSpace = sql.rows("select * from identifier_namespace where id = ${identifier.id_namespace_fk}")[0]
                                Map newNameSpace = sql.rows("select * from identifier_namespace where idns_value = ${oldNameSpace.idns_value} and idns_targettype = ${targetTypePkg.id}")[0]

                                def kbcomponent = sql.rows("select * from kbcomponent where kbc_id = ${identifier.kbc_id}")[0]

                                sql.execute("""insert into identifier_new(id_id, id_version, id_uuid, id_value, id_namespace_fk, id_pkg_fk, id_last_updated, id_date_created) values
                                   ((select nextval ('hibernate_sequence')), 0, (select gen_random_uuid()), ${identifier.id_value}, ${(newNameSpace && newNameSpace.id) ? newNameSpace.id : identifier.id_namespace_fk}, ${combo.combo_from_fk}, ${kbcomponent.kbc_last_updated}, ${kbcomponent.kbc_date_created})""")
                                counts.identifierNewCount =  counts.identifierNewCount+1
                                counts.identifierPkgNewCount =  counts.identifierPkgNewCount+1
                            }
                        }

                        List org = sql.rows("select * from org where kbc_id = ${combo.combo_from_fk}")
                        if (org.size() > 0) {
                            List oldIdentifier = sql.rows("select * from identifier where kbc_id = ${combo.combo_to_fk}")
                            oldIdentifier.each { Map identifier ->
                                Map oldNameSpace = sql.rows("select * from identifier_namespace where id = ${identifier.id_namespace_fk}")[0]
                                Map newNameSpace = sql.rows("select * from identifier_namespace where idns_value = ${oldNameSpace.idns_value} and idns_targettype = ${targetTypeOrg.id}")[0]

                                def kbcomponent = sql.rows("select * from kbcomponent where kbc_id = ${identifier.kbc_id}")[0]

                                sql.execute("""insert into identifier_new(id_id, id_version, id_uuid, id_value, id_namespace_fk, id_org_fk, id_last_updated, id_date_created) values
                                   ((select nextval ('hibernate_sequence')), 0, (select gen_random_uuid()), ${identifier.id_value}, ${(newNameSpace && newNameSpace.id) ? newNameSpace.id : identifier.id_namespace_fk}, ${combo.combo_from_fk}, ${kbcomponent.kbc_last_updated}, ${kbcomponent.kbc_date_created})""")
                                counts.identifierNewCount =  counts.identifierNewCount+1
                                counts.identifierOrgNewCount =  counts.identifierOrgNewCount+1
                            }
                        }

                        List tipp = sql.rows("select * from title_instance_package_platform where kbc_id = ${combo.combo_from_fk}")
                        if (tipp.size() > 0) {
                            List oldIdentifier = sql.rows("select * from identifier where kbc_id = ${combo.combo_to_fk}")
                            oldIdentifier.each { Map identifier ->
                                Map oldNameSpace = sql.rows("select * from identifier_namespace where id = ${identifier.id_namespace_fk}")[0]
                                Map newNameSpace = sql.rows("select * from identifier_namespace where idns_value = ${oldNameSpace.idns_value} and idns_targettype = ${targetTypeTipp.id}")[0]

                                def kbcomponent = sql.rows("select * from kbcomponent where kbc_id = ${identifier.kbc_id}")[0]

                                sql.execute("""insert into identifier_new(id_id, id_version, id_uuid, id_value, id_namespace_fk, id_tipp_fk, id_last_updated, id_date_created) values
                                   ((select nextval ('hibernate_sequence')), 0, (select gen_random_uuid()), ${identifier.id_value}, ${(newNameSpace && newNameSpace.id) ? newNameSpace.id : identifier.id_namespace_fk}, ${combo.combo_from_fk}, ${kbcomponent.kbc_last_updated}, ${kbcomponent.kbc_date_created})""")
                                counts.identifierNewCount =  counts.identifierNewCount+1
                                counts.identifierTippNewCount =  counts.identifierTippNewCount+1
                            }
                        }
                    }
                }

                confirm("get combos and create new identifiers: ${counts}")
                changeSet.setComments("get combos and create new identifiers: ${counts}")
                println(counts)
            }
            rollback {}
        }
    }



    /*changeSet(author: "djebeniani (generated)", id: "1648154097625-15") {
        dropTable(tableName: "identifier")
    }*/

}

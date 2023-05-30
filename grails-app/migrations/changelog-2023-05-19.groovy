
databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded)", id: "1684687476569-1") {
        grailsChange {
            change {
                sql.executeUpdate('''UPDATE identifier_namespace
SET idns_value      = 'eisbn',
    idns_name = 'eISBN'
WHERE idns_targettype = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'TitleInstancePackagePlatform') and idns_value = 'isbn';''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1684687476569-2") {
        grailsChange {
            change {
                sql.executeUpdate('''UPDATE identifier_namespace
SET idns_value      = 'isbn',
    idns_name = 'ISBN'
WHERE idns_targettype = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'TitleInstancePackagePlatform') and idns_value = 'pisbn';''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1684687476569-3") {
        grailsChange {
            change {
                sql.executeInsert('''INSERT INTO identifier_namespace (idns_id, idns_version, idns_family, idns_value, idns_pattern, idns_targettype,
                                         idns_name, idns_date_created, idns_last_updated)
VALUES ((select nextval ('hibernate_sequence')), 0, null, 'title_id', null, (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'TitleInstancePackagePlatform'), 'Title_ID', now(), now());''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1684687476569-4") {
        grailsChange {
            change {

                List<String> idNamespaces = ['acs',
                                             'ava',
                                             'ava.at',
                                             'beck',
                                             'berliner-philharmoniker',
                                             'beuth',
                                             'bloomsbury',
                                             'brepols',
                                             'cambridge',
                                             'carelit',
                                             'cas',
                                             'clarivate',
                                             'contentselect',
                                             'cup',
                                             'degruyter',
                                             'duncker&humblot',
                                             'duz',
                                             'ebookcentral',
                                             'elsevier',
                                             'emerald',
                                             'europalehrmittel',
                                             'felixmeiner',
                                             'filmfriend',
                                             'gbi',
                                             'goodhabitz',
                                             'hanser',
                                             'henle',
                                             'herdt',
                                             'ibisworld',
                                             'igpublishing',
                                             'jstor',
                                             'karger',
                                             'lexisnexis',
                                             'materialatlas',
                                             'meiner',
                                             'meinunterricht',
                                             'mgg online',
                                             'munzinger',
                                             'narr',
                                             'naxos',
                                             'ne_gmbh',
                                             'newyorktimes',
                                             'nkoda',
                                             'nomos',
                                             'oecd',
                                             'oup',
                                             'peterlang',
                                             'pons',
                                             'project_muse',
                                             'prometheus',
                                             'proquest',
                                             'reguvis',
                                             'spie',
                                             'springer',
                                             'taylor&francis',
                                             'thieme',
                                             'utb',
                                             'vde',
                                             'vdi_elibrary',
                                             'wiley',
                                             'wolterskluwer']

                Integer countUpdateSum = 0
                idNamespaces.each { String namespace ->

                    Integer countUpdate = sql.executeUpdate("update identifier set id_namespace_fk = (select idns_id from identifier_namespace WHERE idns_targettype = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'TitleInstancePackagePlatform') and idns_value = 'title_id' ) where id_namespace_fk = (select idns_id from identifier_namespace WHERE idns_targettype = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'TitleInstancePackagePlatform') and idns_value = '" + namespace + "')")

                    countUpdateSum = countUpdateSum + countUpdate

                }
                confirm("update identifier set id_namespace_fk to title_id: ${countUpdateSum}")
                changeSet.setComments("update identifier set id_namespace_fk to title_id: ${countUpdateSum}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1684687476569-5") {
        grailsChange {
            change {

                List<String> idNamespaces = ['acs',
                                             'ava',
                                             'ava.at',
                                             'beck',
                                             'berliner-philharmoniker',
                                             'beuth',
                                             'bloomsbury',
                                             'brepols',
                                             'cambridge',
                                             'carelit',
                                             'cas',
                                             'clarivate',
                                             'contentselect',
                                             'cup',
                                             'degruyter',
                                             'duncker&humblot',
                                             'duz',
                                             'ebookcentral',
                                             'elsevier',
                                             'emerald',
                                             'europalehrmittel',
                                             'felixmeiner',
                                             'filmfriend',
                                             'gbi',
                                             'goodhabitz',
                                             'hanser',
                                             'henle',
                                             'herdt',
                                             'ibisworld',
                                             'igpublishing',
                                             'jstor',
                                             'karger',
                                             'lexisnexis',
                                             'materialatlas',
                                             'meiner',
                                             'meinunterricht',
                                             'mgg online',
                                             'munzinger',
                                             'narr',
                                             'naxos',
                                             'ne_gmbh',
                                             'newyorktimes',
                                             'nkoda',
                                             'nomos',
                                             'oecd',
                                             'oup',
                                             'peterlang',
                                             'pons',
                                             'project_muse',
                                             'prometheus',
                                             'proquest',
                                             'reguvis',
                                             'spie',
                                             'springer',
                                             'taylor&francis',
                                             'thieme',
                                             'utb',
                                             'vde',
                                             'vdi_elibrary',
                                             'wiley',
                                             'wolterskluwer']

                Integer countSum = 0
                idNamespaces.each { String namespace ->

                    Integer count = sql.executeUpdate("update kbart_source set ks_target_namespace_fk = null WHERE ks_target_namespace_fk = (select idns_id from identifier_namespace WHERE idns_targettype = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'TitleInstancePackagePlatform') and idns_value = '" + namespace + "')")

                    countSum = countSum + count

                }
                confirm("update kbart_source set ks_target_namespace_fk = null : ${countSum}")
                changeSet.setComments("update kbart_source set ks_target_namespace_fk = null: ${countSum}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1684687476569-6") {
        grailsChange {
            change {

                List<String> idNamespaces = ['acs',
                                             'ava',
                                             'ava.at',
                                             'beck',
                                             'berliner-philharmoniker',
                                             'beuth',
                                             'bloomsbury',
                                             'brepols',
                                             'cambridge',
                                             'carelit',
                                             'cas',
                                             'clarivate',
                                             'contentselect',
                                             'cup',
                                             'degruyter',
                                             'duncker&humblot',
                                             'duz',
                                             'ebookcentral',
                                             'elsevier',
                                             'emerald',
                                             'europalehrmittel',
                                             'felixmeiner',
                                             'filmfriend',
                                             'gbi',
                                             'goodhabitz',
                                             'hanser',
                                             'henle',
                                             'herdt',
                                             'ibisworld',
                                             'igpublishing',
                                             'jstor',
                                             'karger',
                                             'lexisnexis',
                                             'materialatlas',
                                             'meiner',
                                             'meinunterricht',
                                             'mgg online',
                                             'munzinger',
                                             'narr',
                                             'naxos',
                                             'ne_gmbh',
                                             'newyorktimes',
                                             'nkoda',
                                             'nomos',
                                             'oecd',
                                             'oup',
                                             'peterlang',
                                             'pons',
                                             'project_muse',
                                             'prometheus',
                                             'proquest',
                                             'reguvis',
                                             'spie',
                                             'springer',
                                             'taylor&francis',
                                             'thieme',
                                             'utb',
                                             'vde',
                                             'vdi_elibrary',
                                             'wiley',
                                             'wolterskluwer']

                Integer countSum = 0
                idNamespaces.each { String namespace ->

                    Integer count = sql.executeUpdate("update platform set plat_title_namespace_fk = null WHERE plat_title_namespace_fk = (select idns_id from identifier_namespace WHERE idns_targettype = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'TitleInstancePackagePlatform') and idns_value = '" + namespace + "')")

                    countSum = countSum + count

                }
                confirm("update platform set plat_title_namespace_fk = null : ${countSum}")
                changeSet.setComments("update platform set plat_title_namespace_fk = null: ${countSum}")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1684687476569-7") {
        grailsChange {
            change {

                List<String> idNamespaces = ['acs',
                                             'ava',
                                             'ava.at',
                                             'beck',
                                             'berliner-philharmoniker',
                                             'beuth',
                                             'bloomsbury',
                                             'brepols',
                                             'cambridge',
                                             'carelit',
                                             'cas',
                                             'clarivate',
                                             'contentselect',
                                             'cup',
                                             'degruyter',
                                             'duncker&humblot',
                                             'duz',
                                             'ebookcentral',
                                             'elsevier',
                                             'emerald',
                                             'europalehrmittel',
                                             'felixmeiner',
                                             'filmfriend',
                                             'gbi',
                                             'goodhabitz',
                                             'hanser',
                                             'henle',
                                             'herdt',
                                             'ibisworld',
                                             'igpublishing',
                                             'jstor',
                                             'karger',
                                             'lexisnexis',
                                             'materialatlas',
                                             'meiner',
                                             'meinunterricht',
                                             'mgg online',
                                             'munzinger',
                                             'narr',
                                             'naxos',
                                             'ne_gmbh',
                                             'newyorktimes',
                                             'nkoda',
                                             'nomos',
                                             'oecd',
                                             'oup',
                                             'peterlang',
                                             'pons',
                                             'project_muse',
                                             'prometheus',
                                             'proquest',
                                             'reguvis',
                                             'spie',
                                             'springer',
                                             'taylor&francis',
                                             'thieme',
                                             'utb',
                                             'vde',
                                             'vdi_elibrary',
                                             'wiley',
                                             'wolterskluwer']

                Integer countDeleteSum = 0
                idNamespaces.each { String namespace ->

                    Integer countDelete = sql.executeUpdate("delete from identifier_namespace WHERE idns_targettype = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'IdentifierNamespace.TargetType') and rdv_value = 'TitleInstancePackagePlatform') and idns_value = '" + namespace + "'")

                    countDeleteSum = countDeleteSum + countDelete

                }
                confirm("delete from identifier_namespace : ${countDeleteSum}")
                changeSet.setComments("delete from identifier_namespace : ${countDeleteSum}")
            }
            rollback {}
        }
    }
}
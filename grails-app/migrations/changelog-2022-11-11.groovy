databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1668163897259-1") {
        grailsChange {
            change {
                Integer countUpdate = sql.executeUpdate('''update kbcomponent set kbc_last_updated = CURRENT_DATE where 
                                                           kbc_id in (select tipp.kbc_id from title_instance_package_platform as tipp 
                                                               left join kbcomponent k on tipp.kbc_id = k.kbc_id 
                                                               left join component_price on cp_owner_component_fk = tipp.kbc_id 
                                                                                         where cp_last_updated > k.kbc_last_updated);''')

                confirm("update lastUpdate of title (price.lastUpdate > title.lastUpdate): ${countUpdate}")
                changeSet.setComments("update lastUpdate of title (price.lastUpdate > title.lastUpdate): ${countUpdate}")
            }
            rollback {}
        }
    }


}

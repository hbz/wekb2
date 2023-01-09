

databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1656918437150-1") {
        grailsChange {
            change {
                def titleCount = sql.rows("select count(kbc_id) from title_instance")[0]

                println("Prepare to delete ${titleCount.count} TitleInstance")

                for (int i = 0; i <= titleCount.count; i = i + 100000) {

                    println("Deleting Title_Instances Index: ${i}")

                    List idList = sql.rows("select kbc_id from title_instance ORDER BY kbc_id LIMIT 100000 OFFSET ${i}")

                    idList.each { def map ->
                        println("Deleting Title_Instance with ID: ${map.kbc_id}")
                        sql.execute("delete from combo as c where c.combo_from_fk=:component or c.combo_to_fk=:component;", [component: map.kbc_id])
                        sql.execute("delete from component_watch as cw where cw.cw_component=:component;", [component: map.kbc_id])
                        sql.execute("delete from kbcomponent_variant_name as c where c.cvn_kbc_fk=:component;", [component: map.kbc_id])
                        sql.execute("delete from review_request_allocation_log as c where c.rr_id in ( select r.rr_id from review_request as r where r.component_to_review_id=:component);", [component: map.kbc_id])
                        sql.execute("delete from allocated_review_group as g where g.review_id in ( select r.rr_id from review_request as r where r.component_to_review_id=:component);", [component: map.kbc_id])
                        def events_to_delete = sql.rows("select c.event_id from component_history_event_participant as c where c.participant_id = :component;", [component: map.kbc_id])
                        events_to_delete.each {
                            sql.execute("delete from component_history_event_participant as c where c.event_id = ?;", [it.event_id])
                            sql.execute("delete from component_history_event as c where c.id = ?;", [it.event_id])
                        }
                        sql.execute("delete from review_request as c where c.component_to_review_id=:component;", [component: map.kbc_id])
                        sql.execute("update kbcomponent set kbc_duplicate_of = NULL where kbc_duplicate_of=:component;", [component: map.kbc_id])
                        sql.execute("delete from component_price where cp_owner_component_fk=:component;", [component: map.kbc_id])
                        sql.execute("delete from title_instance where kbc_id = :kbc_id;", [kbc_id: map.kbc_id])
                        sql.execute("delete from kbcomponent where kbc_id = :kbc_id;", [kbc_id: map.kbc_id])
                    }
                }
                confirm("Finish deleting TitleInstance: ${titleCount.count}")
                changeSet.setComments("Finish deleting TitleInstance: ${titleCount.count}")
            }
            rollback {}
        }
    }
}

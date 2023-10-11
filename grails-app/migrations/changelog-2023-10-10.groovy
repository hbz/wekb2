databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded)", id: "1696932180328-1") {
        grailsChange {
            change {
                sql.execute('''alter table title_instance_package_platform
    rename column tipp_preceding_publication_id to "tipp_preceding_publication_title_id";''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1696932180328-2") {
        grailsChange {
            change {
                sql.execute('''alter table title_instance_package_platform
    rename column tipp_parent_publication_id to "tipp_parent_publication_title_id";''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded)", id: "1696932180328-3") {
        grailsChange {
            change {
                sql.execute('''alter table title_instance_package_platform
    rename column tipp_access_type to "tipp_access_type_rv_fk";''')
                rollback {}
            }
        }
    }
}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1639049346115-1") {
        createTable(tableName: "kbcomponent_language") {
            column(autoIncrement: "true", name: "kbc_lang_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "kbcomponent_languagePK")
            }

            column(name: "kbc_lang_version", type: "BIGINT")

            column(name: "kbc_lang_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "kbc_lang_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "kbc_lang_rv_fk", type: "BIGINT")

            column(name: "kbc_lang_kbc_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1639049346115-2") {
        addForeignKeyConstraint(baseColumnNames: "kbc_lang_rv_fk", baseTableName: "kbcomponent_language", constraintName: "FK5se9dtjyq177k80ijecl27ta6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1639049346115-3") {
        addForeignKeyConstraint(baseColumnNames: "kbc_lang_kbc_fk", baseTableName: "kbcomponent_language", constraintName: "FKdt0rixmn9cfw4jdnh57km823f", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1639049346115-4") {
        dropForeignKeyConstraint(baseTableName: "kbc_language", constraintName: "FKf0dr9xej9hx7vctd76ddq9ofc")
    }

    changeSet(author: "djebeniani (generated)", id: "1639049346115-5") {
        dropForeignKeyConstraint(baseTableName: "kbc_language", constraintName: "FKhalvi3dx2ens2t0okp823n36i")
    }


    changeSet(author: "djebeniani (modified)", id: "1639049346115-6") {
        grailsChange {
            change {
                def oldLanguage = sql.rows("select * from kbc_language")

                oldLanguage.each {

                    sql.execute("""insert into kbcomponent_language(kbc_lang_id, kbc_lang_version, kbc_lang_rv_fk, kbc_lang_kbc_fk, kbc_lang_date_created, kbc_lang_last_updated) values
	                ((select nextval ('hibernate_sequence')), 0, ${it.kbc_language_rv_fk}, ${it.kbc_fk}, (select now()), (select now())) 
                    """)
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1639049346115-7") {
        createIndex(indexName: "kbc_lang_kbc_idx", tableName: "kbcomponent_language") {
            column(name: "kbc_lang_kbc_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1639049346115-8") {
        createIndex(indexName: "kbc_lang_language_idx", tableName: "kbcomponent_language") {
            column(name: "kbc_lang_rv_fk")
        }
    }

    /*changeSet(author: "djebeniani (generated)", id: "1639049346115-9") {
        dropTable(tableName: "kbc_language")
    }*/



}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1770144709969-1") {
        createTable(tableName: "contact_language") {
            column(name: "ct_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "language_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1770144709969-2") {
        addForeignKeyConstraint(baseColumnNames: "language_rv_fk", baseTableName: "contact_language", constraintName: "FKdno3nx1nipr736xctdap4pt06", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1770144709969-3") {
        addForeignKeyConstraint(baseColumnNames: "ct_fk", baseTableName: "contact_language", constraintName: "FKj2n47yg7olisyveox59ma4daw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ct_id", referencedTableName: "contact", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1770144709969-4") {
        grailsChange {
            change {

                String query = '''select ct_content, count(*) , array_agg(ct_language_rv_fk) as languages, array_agg(ct_id) as contact_ids, array_agg(ct_type_rv_fk) from contact where ct_language_rv_fk is not null group by ct_content, ct_type_rv_fk'''
                int countTransferredLanguages = 0
                int countDeletedContact = 0
                def languages = sql.rows(query)

                List deletedContact = []
                languages.each {
                    def ct_ids = it.contact_ids
                    def langs = it.languages
                    def first = ct_ids.array?.getAt(0)
                    langs.array.each { def lang ->
                        sql.execute("""insert into contact_language (ct_fk, language_rv_fk) values (${first}, ${lang});""")
                        countTransferredLanguages++
                    }
                    ct_ids.array.each{ def ct_id ->
                        if(first != ct_id){
                            deletedContact << ct_id
                        }
                    }
                }


                deletedContact.each {
                    sql.execute("""delete from contact where ct_id =${it};""")
                    countDeletedContact++
                }

                String info = "countTransferredLanguages -> ${countTransferredLanguages}, countDeletedContact -> ${countDeletedContact}"
                confirm("${query} :${info}")
                changeSet.setComments(info)

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1770144709969-5") {
        dropForeignKeyConstraint(baseTableName: "contact", constraintName: "FKk7cibsotlcmfot0vilqt26926")
    }

    changeSet(author: "djebeniani (generated)", id: "1770144709969-6") {
        dropColumn(columnName: "ct_language_rv_fk", tableName: "contact")
    }


}

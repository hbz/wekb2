import groovy.sql.GroovyRowResult

databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded))", id: "1729855392320-1") {
        grailsChange {
            change {

                String query = "select tp_tipp_fk, tp_currency_fk, tp_type_fk, COUNT( tp_tipp_fk ) " +
                        "from tipp_price \n" +
                        "group by tp_tipp_fk, tp_currency_fk, tp_type_fk \n" +
                        "having COUNT( tp_tipp_fk ) > 1"
                List<GroovyRowResult> tippsWithPriceDuplicates = sql.rows(query)

                confirm("tippsWithPriceDuplicates : ${tippsWithPriceDuplicates.size()}")
                changeSet.setComments("tippsWithPriceDuplicates : ${tippsWithPriceDuplicates.size()}")

                tippsWithPriceDuplicates.each { GroovyRowResult row ->
                    String query2 = "select tp_id from tipp_price where tp_tipp_fk = ${row['tp_tipp_fk']} order by tp_last_updated desc"
                    List<GroovyRowResult> tippWithPriceDuplicates = sql.rows(query2)
                    if(tippWithPriceDuplicates.size() > 1){
                        List ids = []
                        tippWithPriceDuplicates.eachWithIndex { GroovyRowResult entry, int i ->
                            if(i != 0){
                                ids << entry['tp_id']
                            }
                        }
                        ids.each { def tippPriceID ->
                            sql.execute("delete from tipp_price where tp_id = ${tippPriceID}")
                        }
                    }
                    sql.execute("update title_instance_package_platform set tipp_last_updated = now() where tipp_id = ${row['tp_tipp_fk']}")
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1729855392320-2") {
        addUniqueConstraint(columnNames: "tp_currency_fk, tp_type_fk, tp_tipp_fk", constraintName: "UK989d260e26f8e154d574395a205a", tableName: "tipp_price")
    }

    changeSet(author: "djebeniani (generated)", id: "1729855392320-3") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "tp_currency_fk", tableName: "tipp_price", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1729855392320-4") {
        addNotNullConstraint(columnDataType: "float4", columnName: "tp_price", tableName: "tipp_price", validate: "true")
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1729855392320-5") {
        grailsChange {
            change {

                String query = "select count(*) from tipp_price where tp_tipp_fk is null"
                def tippPriceWithoutTipp = sql.rows(query)[0]

                confirm("tippPriceWithoutTipp : ${tippPriceWithoutTipp}")
                changeSet.setComments("tippPriceWithoutTipp : ${tippPriceWithoutTipp}")
                sql.execute("delete from tipp_price where tp_tipp_fk is null")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1729855392320-6") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "tp_tipp_fk", tableName: "tipp_price", validate: "true")
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1729855392320-7") {
        grailsChange {
            change {

                String query = "select count(*) from component_language where cl_tipp_fk is null"
                def componentLanguageWithoutTipp = sql.rows(query)[0]

                confirm("componentLanguageWithoutTipp : ${componentLanguageWithoutTipp}")
                changeSet.setComments("componentLanguageWithoutTipp : ${componentLanguageWithoutTipp}")
                sql.execute("delete from component_language where cl_tipp_fk is null")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1729855392320-8") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "cl_tipp_fk", tableName: "component_language", validate: "true")
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1729855392320-9") {
        grailsChange {
            change {

                String query = "select cl_tipp_fk, cl_rv_fk, COUNT( cl_tipp_fk ) from component_language " +
                        "group by cl_tipp_fk, cl_rv_fk \n" +
                        "having COUNT( cl_tipp_fk ) > 1;"
                List<GroovyRowResult> tippsWithLangDuplicates = sql.rows(query)

                confirm("tippsWithLangDuplicates : ${tippsWithLangDuplicates.size()}")
                changeSet.setComments("tippsWithLangDuplicates : ${tippsWithLangDuplicates.size()}")

                tippsWithLangDuplicates.each { GroovyRowResult row ->
                    String query2 = "select cl_id from component_language where cl_tipp_fk = ${row['cl_tipp_fk']} order by cl_last_updated desc"
                    List<GroovyRowResult> tippWithLangDuplicates = sql.rows(query2)
                    if(tippWithLangDuplicates.size() > 1){
                        List ids = []
                        tippWithLangDuplicates.eachWithIndex { GroovyRowResult entry, int i ->
                            if(i != 0){
                                ids << entry['cl_id']
                            }
                        }
                        ids.each { def tippPriceID ->
                            sql.execute("delete from component_language where cl_id = ${tippPriceID}")
                        }
                    }
                    sql.execute("update title_instance_package_platform set tipp_last_updated = now() where tipp_id = ${row['cl_tipp_fk']}")
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1729855392320-10") {
        addUniqueConstraint(columnNames: "cl_rv_fk, cl_tipp_fk", constraintName: "UKd2fadd6089de1b523deef8b1fde0", tableName: "component_language")
    }

}

import groovy.sql.GroovyRowResult

databaseChangeLog = {

    changeSet(author: "djebeniani (hand-coded))", id: "1730116866096-1") {
        grailsChange {
            change {

                String query = "select tcs_tipp_fk, tcs_start_volume, tcs_end_date, tcs_start_date, tcs_start_issue, tcs_end_issue, tcs_end_volume, COUNT( tcs_tipp_fk ) from tippcoverage_statement\n" +
                        "                        group by tcs_tipp_fk, tcs_start_volume, tcs_end_date, tcs_start_date, tcs_start_issue, tcs_end_issue, tcs_end_volume\n" +
                        "                        having COUNT( tcs_tipp_fk ) > 1;"
                List<GroovyRowResult> tippsWithCoverageDuplicates = sql.rows(query)

                confirm("tippsWithCoverageDuplicates : ${tippsWithCoverageDuplicates.size()}")
                changeSet.setComments("tippsWithCoverageDuplicates : ${tippsWithCoverageDuplicates.size()}")

                tippsWithCoverageDuplicates.each { GroovyRowResult row ->
                    String query2 = "select tcs_id from tippcoverage_statement where tcs_tipp_fk = ${row['tcs_tipp_fk']} order by tcs_last_updated desc"
                    List<GroovyRowResult> tippWithCovDuplicates = sql.rows(query2)
                    if(tippWithCovDuplicates.size() > 1){
                        List ids = []
                        tippWithCovDuplicates.eachWithIndex { GroovyRowResult entry, int i ->
                            if(i != 0){
                                ids << entry['tcs_id']
                            }
                        }
                        ids.each { def tippCovID ->
                            sql.execute("delete from tippcoverage_statement where tcs_id = ${tippCovID}")
                        }
                    }
                    sql.execute("update title_instance_package_platform set tipp_last_updated = now() where tipp_id = ${row['tcs_tipp_fk']}")
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730116866096-2") {
        addUniqueConstraint(columnNames: "tcs_end_issue, tcs_end_volume, tcs_end_date, tcs_start_issue, tcs_start_volume, tcs_start_date, tcs_tipp_fk", constraintName: "UKeadb8f8715b1edee9c6c283d3aa6", tableName: "tippcoverage_statement")
    }



    changeSet(author: "djebeniani (hand-coded))", id: "1730116866096-3") {
        grailsChange {
            change {

                String query = "select count(*) from tippcoverage_statement where tcs_tipp_fk is null"
                def tippcoverageStatementWithoutTipp = sql.rows(query)[0]

                confirm("tippcoverageStatementWithoutTipp : ${tippcoverageStatementWithoutTipp}")
                changeSet.setComments("componentLanguageWithoutTipp : ${tippcoverageStatementWithoutTipp}")
                sql.execute("delete from tippcoverage_statement where tcs_tipp_fk is null")
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730116866096-4") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "tcs_tipp_fk", tableName: "tippcoverage_statement", validate: "true")
    }
}

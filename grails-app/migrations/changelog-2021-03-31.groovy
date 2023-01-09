
databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1617190309675-1") {
        grailsChange {
            change {

                def tippsWithout = sql.rows("select *\n" +
                        "from title_instance_package_platform as tipp where\n" +
                        "not exists(select * from tippcoverage_statement where owner_id = tipp.kbc_id)\n" +
                        "and (tipp_start_date is not null or\n" +
                        "     tipp_start_issue is not null or\n" +
                        "     tipp_start_volume is not null or\n" +
                        "     tipp_end_date is not null or\n" +
                        "     tipp_end_issue is not null or\n" +
                        "     tipp_end_volume is not null or\n" +
                        "     tipp_embargo is not null or\n" +
                        "     tipp_coverage_depth is not null or\n" +
                        "     tipp_coverage_note is not null)")

                tippsWithout.each {

                    sql.execute("""insert into tippcoverage_statement(id, version, tipp_start_date, tipp_start_volume, tipp_start_issue, tipp_end_date, tipp_end_volume, tipp_end_issue, tipp_embargo, tipp_coverage_note, tipp_coverage_depth, owner_id) values
	                ((select nextval ('hibernate_sequence')), 0, ${it.tipp_start_date}, ${it.tipp_start_volume}, ${it.tipp_start_issue}, ${it.tipp_end_date}, ${it.tipp_end_volume}, ${it.tipp_end_issue}, ${it.tipp_embargo}, ${it.tipp_coverage_note}, ${it.tipp_coverage_depth}, ${it.kbc_id}) 
                    """)
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617190309675-2") {
        dropColumn(columnName: "tipp_end_date", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1617190309675-3") {
        dropColumn(columnName: "tipp_end_issue", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1617190309675-4") {
        dropColumn(columnName: "tipp_end_volume", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1617190309675-5") {
        dropColumn(columnName: "tipp_start_date", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1617190309675-6") {
        dropColumn(columnName: "tipp_start_issue", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1617190309675-7") {
        dropColumn(columnName: "tipp_start_volume", tableName: "title_instance_package_platform")
    }

    changeSet(author: "djebeniani (generated)", id: "1617190309675-8") {
        dropColumn(columnName: "tipp_embargo", tableName: "title_instance_package_platform")
    }
}

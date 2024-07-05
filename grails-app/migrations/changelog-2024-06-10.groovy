databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1718015197638-1") {
        grailsChange {
            change {
                sql.execute("update refdata_category set rdc_description = 'TIPPCoverageStatement.CoverageDepth_old' WHERE rdc_description = 'TIPPCoverageStatement.CoverageDepth'")
                sql.execute("update refdata_category set rdc_description = 'TIPPCoverageStatement.CoverageDepth' WHERE rdc_description = 'TitleInstancePackagePlatform.CoverageDepth'")
                sql.execute("update refdata_category set rdc_description = 'TitleInstancePackagePlatform.CoverageDepth' WHERE rdc_description = 'TIPPCoverageStatement.CoverageDepth_old'")
             }
            rollback {}
        }
    }

}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1617722590583-1") {
        addColumn(tableName: "review_request") {
            column(name: "rr_allocated_to_org_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617722590583-2") {
        addColumn(tableName: "review_request") {
            column(name: "rr_closed_by_org_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617722590583-3") {
        addColumn(tableName: "review_request") {
            column(name: "rr_raised_by_org_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617722590583-4") {
        addColumn(tableName: "review_request") {
            column(name: "rr_reviewed_by_org_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617722590583-5") {
        addForeignKeyConstraint(baseColumnNames: "rr_allocated_to_org_fk", baseTableName: "review_request", constraintName: "FK60d5m7ahyhe98r5tbdcnfoifh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "org")
    }

    changeSet(author: "djebeniani (generated)", id: "1617722590583-6") {
        addForeignKeyConstraint(baseColumnNames: "rr_reviewed_by_org_fk", baseTableName: "review_request", constraintName: "FK7wejmemg5u02thg7b6ceyka2g", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "org")
    }

    changeSet(author: "djebeniani (generated)", id: "1617722590583-7") {
        addForeignKeyConstraint(baseColumnNames: "rr_raised_by_org_fk", baseTableName: "review_request", constraintName: "FK9wb0t2le3nqbxx9xmomxnil31", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "org")
    }

    changeSet(author: "djebeniani (generated)", id: "1617722590583-8") {
        addForeignKeyConstraint(baseColumnNames: "rr_closed_by_org_fk", baseTableName: "review_request", constraintName: "FKdm65ft6educn1codyvmtm4n77", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "org")
    }
}

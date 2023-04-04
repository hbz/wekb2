databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1619014613769-1") {
        addColumn(tableName: "review_request") {
            column(name: "type_id", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-2") {
        addForeignKeyConstraint(baseColumnNames: "type_id", baseTableName: "review_request", constraintName: "FK1w7ygkxctnlnkbl2q8vftc6a5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-3") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "FK60d5m7ahyhe98r5tbdcnfoifh")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-4") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "FK7wejmemg5u02thg7b6ceyka2g")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-5") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "FK9wb0t2le3nqbxx9xmomxnil31")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-6") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "FKdm65ft6educn1codyvmtm4n77")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-7") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "fk553ulmawh2o2fv5amxhiwtl3f")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-8") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "fkgp45jlm2b6s71rwrsobr7rbj8")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-9") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "fkq7wptkdmeawnq032tiolco1nj")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-10") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "fkro32fdjk5yvjl5e1rqrng4nkk")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-11") {
        dropColumn(columnName: "allocated_to_id", tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-12") {
        dropColumn(columnName: "closed_by_id", tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-13") {
        dropColumn(columnName: "raised_by_id", tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-14") {
        dropColumn(columnName: "reviewed_by_id", tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-15") {
        dropColumn(columnName: "rr_allocated_to_org_fk", tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-16") {
        dropColumn(columnName: "rr_closed_by_org_fk", tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-17") {
        dropColumn(columnName: "rr_raised_by_org_fk", tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1619014613769-18") {
        dropColumn(columnName: "rr_reviewed_by_org_fk", tableName: "review_request")
    }

}

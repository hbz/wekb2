databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1617809013311-1") {
        createTable(tableName: "package_national_range") {
            column(name: "package_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "national_range_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617809013311-2") {
        createTable(tableName: "package_regional_range") {
            column(name: "package_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "regional_range_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617809013311-3") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_access_type", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617809013311-4") {
        addForeignKeyConstraint(baseColumnNames: "national_range_rv_fk", baseTableName: "package_national_range", constraintName: "FK54wdewlvi2idd1kmm4nhaqisg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1617809013311-5") {
        addForeignKeyConstraint(baseColumnNames: "tipp_access_type", baseTableName: "title_instance_package_platform", constraintName: "FK6io7qhhcyv8s1gilsekthkawb", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1617809013311-6") {
        addForeignKeyConstraint(baseColumnNames: "package_fk", baseTableName: "package_regional_range", constraintName: "FKc2h783bogobxuld6ax7rj4sa4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1617809013311-7") {
        addForeignKeyConstraint(baseColumnNames: "regional_range_rv_fk", baseTableName: "package_regional_range", constraintName: "FKd9e9sa1tjwf4ca24mdy0q5f5e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1617809013311-8") {
        addForeignKeyConstraint(baseColumnNames: "package_fk", baseTableName: "package_national_range", constraintName: "FKlxoj4vs3pcxvtly1hpj43dt83", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "package")
    }


}

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1646130416053-1") {
        dropForeignKeyConstraint(baseTableName: "web_hook_endpoint", constraintName: "fk2t7544u5o2ql4qbc137o6qla8")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-2") {
        dropForeignKeyConstraint(baseTableName: "macro_tags_value", constraintName: "fk4pu4gveqnt1herj35s2h6dd74")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-3") {
        dropForeignKeyConstraint(baseTableName: "review_request", constraintName: "fk6gdr4avi5vjxe6yek4rv1gpsf")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-4") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fk7c5au3ehdu3a3vtaaciccmumq")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-5") {
        dropForeignKeyConstraint(baseTableName: "refine_project", constraintName: "fk8mqjtduwpcrex1oiecc96mwjo")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-6") {
        dropForeignKeyConstraint(baseTableName: "data_file", constraintName: "fk9k4vijew8p6q702ttq9tisv9n")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-7") {
        dropForeignKeyConstraint(baseTableName: "component_person", constraintName: "fkalrgmj8ucf42pdc87ge3hj53y")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-8") {
        dropForeignKeyConstraint(baseTableName: "component_person", constraintName: "fkbb6k9u48l44pfr6lb2t2s6jpo")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-9") {
        dropForeignKeyConstraint(baseTableName: "web_hook", constraintName: "fkbgvs3icnisx7hntjphunp751t")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-10") {
        dropForeignKeyConstraint(baseTableName: "refine_project", constraintName: "fkc4xy0nr41tmx8em0y23d2xl9s")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-11") {
        dropForeignKeyConstraint(baseTableName: "component_ingestion_source", constraintName: "fkf0iskoxcadtepcm2yf500783t")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-12") {
        dropForeignKeyConstraint(baseTableName: "refine_project", constraintName: "fkfy9hht47txw7g4oow7iesud6n")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-13") {
        dropForeignKeyConstraint(baseTableName: "ingestion_profile", constraintName: "fkg06bewydoiog3voyvxnqci76p")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-14") {
        dropForeignKeyConstraint(baseTableName: "component_person", constraintName: "fkisf7qduwss15buqg8bs0caycf")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-15") {
        dropForeignKeyConstraint(baseTableName: "component_ingestion_source", constraintName: "fkjb1eaokarnjbwtrvn0jkswsbw")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-16") {
        dropForeignKeyConstraint(baseTableName: "license", constraintName: "fkjgh122iv0a4yk15vgvlp1ljh6")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-17") {
        dropForeignKeyConstraint(baseTableName: "macro_tags_value", constraintName: "fkkdd191fgv8ni91r9h0crjnux8")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-18") {
        dropForeignKeyConstraint(baseTableName: "rule", constraintName: "fkl53fg4avr2l4xvx6qpl7oxaf8")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-19") {
        dropForeignKeyConstraint(baseTableName: "ingestion_profile", constraintName: "fkm8kmg5o4alvie4t279l4n7bmn")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-20") {
        dropForeignKeyConstraint(baseTableName: "package", constraintName: "fknn7kk7k1cnmess5xmo4aidq0i")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-21") {
        dropForeignKeyConstraint(baseTableName: "refine_project_skipped_titles", constraintName: "fko76nbo79df952y5b32ic15frd")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-22") {
        dropForeignKeyConstraint(baseTableName: "refine_project", constraintName: "fkqle1j5swnf10869ndc7hhik6e")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-23") {
        dropForeignKeyConstraint(baseTableName: "refine_project", constraintName: "fkru7hao9hwirc2faa99lj1wfr7")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-24") {
        dropForeignKeyConstraint(baseTableName: "refine_project", constraintName: "fkt70yc8y9yotrq10rakh8yi0ot")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-25") {
        dropTable(tableName: "batch_control")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-26") {
        dropTable(tableName: "component_ingestion_source")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-27") {
        dropTable(tableName: "component_person")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-28") {
        dropTable(tableName: "data_file")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-29") {
        dropTable(tableName: "document")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-30") {
        dropTable(tableName: "imprint")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-31") {
        dropTable(tableName: "ingestion_profile")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-32") {
        dropTable(tableName: "kbc_language")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-33") {
        dropTable(tableName: "license")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-34") {
        dropTable(tableName: "macro")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-35") {
        dropTable(tableName: "macro_tags_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-36") {
        dropTable(tableName: "person")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-37") {
        dropTable(tableName: "refine_operation")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-38") {
        dropTable(tableName: "refine_project")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-39") {
        dropTable(tableName: "refine_project_skipped_titles")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-40") {
        dropTable(tableName: "rule")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-41") {
        dropTable(tableName: "web_hook")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-42") {
        dropTable(tableName: "web_hook_endpoint")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-43") {
        dropTable(tableName: "work")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-44") {
        dropColumn(columnName: "pkg_refine_project_fk", tableName: "package")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-45") {
        dropColumn(columnName: "refine_project_id", tableName: "review_request")
    }

    changeSet(author: "djebeniani (generated)", id: "1646130416053-46") {
        dropColumn(columnName: "work_id", tableName: "title_instance")
    }
}

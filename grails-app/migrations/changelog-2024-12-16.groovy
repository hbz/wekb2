databaseChangeLog = {

    changeSet(author: "bluoss (generated)", id: "1734351022457-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_access_audio_fk_rv", type: "int8")
        }
    }

    changeSet(author: "bluoss (generated)", id: "1734351022457-2") {
        addColumn(tableName: "platform") {
            column(name: "plat_viewer_for_epub_fk_rv", type: "int8")
        }
    }

    changeSet(author: "bluoss (generated)", id: "1734351022457-3") {
        addColumn(tableName: "platform") {
            column(name: "plat_viewer_for_pdf_fk_rv", type: "int8")
        }
    }


    changeSet(author: "bluoss (generated)", id: "1734351022457-4") {
        addForeignKeyConstraint(baseColumnNames: "plat_viewer_for_epub_fk_rv", baseTableName: "platform", constraintName: "FKbh5x3ifofbguddkbnus3psxdl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "bluoss (generated)", id: "1734351022457-5") {
        addForeignKeyConstraint(baseColumnNames: "plat_access_audio_fk_rv", baseTableName: "platform", constraintName: "FKftcdui8n1knx0uhxydkhlii0t", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "bluoss (generated)", id: "1734351022457-6d") {
        addForeignKeyConstraint(baseColumnNames: "plat_viewer_for_pdf_fk_rv", baseTableName: "platform", constraintName: "FKg16627ay5w6fcev14dthrym1k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

}
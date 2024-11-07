databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1730800150751-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_access_platform_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-2") {
        addColumn(tableName: "platform") {
            column(name: "plat_database_barrier_free_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-3") {
        addColumn(tableName: "platform") {
            column(name: "plat_ebook_epub_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-4") {
        addColumn(tableName: "platform") {
            column(name: "plat_onix_metadata_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-5") {
        addColumn(tableName: "platform") {
            column(name: "plat_pdf_ua_standard_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-6") {
        addColumn(tableName: "platform") {
            column(name: "plat_player_for_audio_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-7") {
        addColumn(tableName: "platform") {
            column(name: "plat_player_for_video_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-8") {
        addColumn(tableName: "platform") {
            column(name: "plat_video_audiodes_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-9") {
        addColumn(tableName: "platform") {
            column(name: "plat_video_subtitles_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-10") {
        addForeignKeyConstraint(baseColumnNames: "plat_video_subtitles_fk_rv", baseTableName: "platform", constraintName: "FK7fci0ekcpirol24xepe28bj44", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-11") {
        addForeignKeyConstraint(baseColumnNames: "plat_player_for_audio_fk_rv", baseTableName: "platform", constraintName: "FKa9p60x26gs19nc8cduc7nl2wg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-12") {
        addForeignKeyConstraint(baseColumnNames: "plat_player_for_video_fk_rv", baseTableName: "platform", constraintName: "FKg0l7jf2lv2qvr6j16ao0393ww", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-13") {
        addForeignKeyConstraint(baseColumnNames: "plat_pdf_ua_standard_fk_rv", baseTableName: "platform", constraintName: "FKgkjeqwunm2gq6497ka3soy56p", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-14") {
        addForeignKeyConstraint(baseColumnNames: "plat_access_platform_fk_rv", baseTableName: "platform", constraintName: "FKh698khtl43xd59tn2kvdk55km", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-15") {
        addForeignKeyConstraint(baseColumnNames: "plat_ebook_epub_fk_rv", baseTableName: "platform", constraintName: "FKkeneex28r2yxsgpu01j9a4oe6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-16") {
        addForeignKeyConstraint(baseColumnNames: "plat_video_audiodes_fk_rv", baseTableName: "platform", constraintName: "FKov2hrscxtp521n75s3i6tc8qh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-17") {
        addForeignKeyConstraint(baseColumnNames: "plat_database_barrier_free_fk_rv", baseTableName: "platform", constraintName: "FKqjx8im910dpwqgoofljg8lh5p", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-18") {
        addForeignKeyConstraint(baseColumnNames: "plat_onix_metadata_fk_rv", baseTableName: "platform", constraintName: "FKsy9r2yvwwdtc0m59p0r3fdnh0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-19") {
        addColumn(tableName: "platform") {
            column(name: "plat_accessibility_statement_available_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-20") {
        addColumn(tableName: "platform") {
            column(name: "plat_roadmap_accessibility_available_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-21") {
        addForeignKeyConstraint(baseColumnNames: "plat_roadmap_accessibility_available_fk_rv", baseTableName: "platform", constraintName: "FKmvtw4yc91lmfi96t21xeerc99", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730800150751-22") {
        addForeignKeyConstraint(baseColumnNames: "plat_accessibility_statement_available_fk_rv", baseTableName: "platform", constraintName: "FKrug4gk33b5e9857dvfjiopaic", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

}

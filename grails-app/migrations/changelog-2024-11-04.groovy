databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1730728805911-1") {
        addColumn(tableName: "platform") {
            column(name: "access_platform_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-2") {
        addColumn(tableName: "platform") {
            column(name: "database_barrier_free_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-3") {
        addColumn(tableName: "platform") {
            column(name: "ebookepub_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-4") {
        addColumn(tableName: "platform") {
            column(name: "onix_metadata_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-5") {
        addColumn(tableName: "platform") {
            column(name: "pdf_ua_standard_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-6") {
        addColumn(tableName: "platform") {
            column(name: "player_for_audio_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-7") {
        addColumn(tableName: "platform") {
            column(name: "player_for_video_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-8") {
        addColumn(tableName: "platform") {
            column(name: "video_audio_des_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-9") {
        addColumn(tableName: "platform") {
            column(name: "video_sub_titles_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-10") {
        addForeignKeyConstraint(baseColumnNames: "player_for_video_id", baseTableName: "platform", constraintName: "FK28s8yov83y4pdhutv1pvc4xo6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-11") {
        addForeignKeyConstraint(baseColumnNames: "onix_metadata_id", baseTableName: "platform", constraintName: "FK29rao7dj2nxv683kiotll8mmk", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-12") {
        addForeignKeyConstraint(baseColumnNames: "pdf_ua_standard_id", baseTableName: "platform", constraintName: "FK58tnkgnljcthdejq6wob471ha", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-13") {
        addForeignKeyConstraint(baseColumnNames: "database_barrier_free_id", baseTableName: "platform", constraintName: "FK7m105mx4tqrdygu4knyq1gq2u", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-14") {
        addForeignKeyConstraint(baseColumnNames: "video_audio_des_id", baseTableName: "platform", constraintName: "FKcfwj2v6a97ai25jim1s4ehmf5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-15") {
        addForeignKeyConstraint(baseColumnNames: "video_sub_titles_id", baseTableName: "platform", constraintName: "FKex6crxk51e7mp3se3jq0ghb5q", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-16") {
        addForeignKeyConstraint(baseColumnNames: "access_platform_id", baseTableName: "platform", constraintName: "FKh07vtwru8extoqpe3ob97ybon", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-17") {
        addForeignKeyConstraint(baseColumnNames: "player_for_audio_id", baseTableName: "platform", constraintName: "FKjxlwihw9omkpvmjdmylbpyv13", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1730728805911-18") {
        addForeignKeyConstraint(baseColumnNames: "ebookepub_id", baseTableName: "platform", constraintName: "FKn1aainbmfifdju0k4txlvssfc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

}

databaseChangeLog = {

    changeSet(author: "galffy (generated)", id: "1733313690657-1") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "FK7fci0ekcpirol24xepe28bj44")
    }

    changeSet(author: "galffy (generated)", id: "1733313690657-2") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "FKgkjeqwunm2gq6497ka3soy56p")
    }

    changeSet(author: "galffy (generated)", id: "1733313690657-3") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "FKkeneex28r2yxsgpu01j9a4oe6")
    }

    changeSet(author: "galffy (generated)", id: "1733313690657-4") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "FKov2hrscxtp521n75s3i6tc8qh")
    }

    changeSet(author: "galffy (generated)", id: "1733313690657-5") {
        dropForeignKeyConstraint(baseTableName: "platform", constraintName: "FKqjx8im910dpwqgoofljg8lh5p")
    }

    changeSet(author: "galffy (hand-coded)", id: "1733313690657-6") {
        grailsChange {
            change {
                sql.execute('alter table platform rename plat_ebook_epub_fk_rv to plat_access_epub_fk_rv')
            }
            rollback {}
        }
    }

    changeSet(author: "galffy (hand-coded)", id: "1733313690657-7") {
        grailsChange {
            change {
                sql.execute('alter table platform rename plat_database_barrier_free_fk_rv to plat_access_database_fk_rv')
            }
            rollback {}
        }
    }

    changeSet(author: "galffy (hand-coded)", id: "1733313690657-8") {
        grailsChange {
            change {
                sql.execute('alter table platform rename plat_pdf_ua_standard_fk_rv to plat_access_pdf_fk_rv')
            }
            rollback {}
        }
    }

    changeSet(author: "galffy (hand-coded)", id: "1733313690657-9") {
        grailsChange {
            change {
                sql.execute('alter table platform rename plat_video_audiodes_fk_rv to plat_access_video_fk_rv')
            }
            rollback {}
        }
    }

    changeSet(author: "galffy (generated)", id: "1733313690657-10") {
        addForeignKeyConstraint(baseColumnNames: "plat_access_epub_fk_rv", baseTableName: "platform", constraintName: "FKb43d8krr7k1kkfudsfbhoypye", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1733313690657-11") {
        addForeignKeyConstraint(baseColumnNames: "plat_access_pdf_fk_rv", baseTableName: "platform", constraintName: "FKc9936me8mtivousfcnch0wq88", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1733313690657-12") {
        addForeignKeyConstraint(baseColumnNames: "plat_access_video_fk_rv", baseTableName: "platform", constraintName: "FKl0e7nenes7yxo5nx6oapwk3m7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1733313690657-13") {
        addForeignKeyConstraint(baseColumnNames: "plat_access_database_fk_rv", baseTableName: "platform", constraintName: "FKnuh4e6u8xaadk91rsd5cvmg9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "galffy (generated)", id: "1733313690657-14") {
        dropColumn(columnName: "plat_video_subtitles_fk_rv", tableName: "platform")
    }
}

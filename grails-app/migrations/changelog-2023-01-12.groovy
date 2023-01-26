databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1673535466327-1") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table party rename to "user";''')
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1673535466327-2") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column id to usr_id;''')
            }
            rollback {}
        }
    }
    changeSet(author: "djebeniani (modified)", id: "1673535466327-3") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column version to usr_version;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-4") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column date_created to usr_date_created;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-5") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column last_updated to usr_last_updated;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-6") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column display_name to usr_display_name;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-7") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column default_page_size to usr_default_page_size;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-8") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column password_expired to usr_password_expired;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-9") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column account_expired to usr_account_expired;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-10") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column username to usr_username;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-11") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column account_locked to usr_account_locked;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-12") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column password to usr_password;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-13") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column enabled to usr_enabled;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-14") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "user"
    rename column email to usr_email;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-15") {
        dropForeignKeyConstraint(baseTableName: "user", constraintName: "fk2owjboy27ws0j58fgdt1hyrv4")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-16") {
        dropForeignKeyConstraint(baseTableName: "user", constraintName: "fk4fr090rvrak58ypun15io5jln")
    }



    changeSet(author: "djebeniani (generated)", id: "1673535466327-17") {
        dropForeignKeyConstraint(baseTableName: "user", constraintName: "fk7nw162thwr6nwwctg23m8rble")
    }


    changeSet(author: "djebeniani (generated)", id: "1673535466327-18") {
        dropForeignKeyConstraint(baseTableName: "user", constraintName: "fkbqcjm1jrj8kohdba8q23n16hf")
    }



    changeSet(author: "djebeniani (generated)", id: "1673535466327-19") {
        dropForeignKeyConstraint(baseTableName: "user", constraintName: "fkcl5t4y4ls2kkaamvvd95xx88v")
    }


    changeSet(author: "djebeniani (generated)", id: "1673535466327-20") {
        dropForeignKeyConstraint(baseTableName: "user", constraintName: "fkqahmggwpiyurmeugut6ctgtnr")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-21") {
        dropColumn(columnName: "alert_check_frequency", tableName: "user")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-22") {
        dropColumn(columnName: "class", tableName: "user")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-23") {
        dropColumn(columnName: "last_alert_check", tableName: "user")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-24") {
        dropColumn(columnName: "mission_id", tableName: "user")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-25") {
        dropColumn(columnName: "org_id", tableName: "user")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-26") {
        dropColumn(columnName: "owner_id", tableName: "user")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-27") {
        dropColumn(columnName: "send_alert_emails_id", tableName: "user")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-28") {
        dropColumn(columnName: "show_info_icon_id", tableName: "user")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-29") {
        dropColumn(columnName: "show_quick_view_id", tableName: "user")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-30") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "usr_account_expired", tableName: "user", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-31") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "usr_account_locked", tableName: "user", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-32") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "usr_date_created", tableName: "user", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-33") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "usr_enabled", tableName: "user", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-34") {
        addNotNullConstraint(columnDataType: "timestamp", columnName: "usr_last_updated", tableName: "user", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-35") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "usr_password", tableName: "user", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-36") {
        addNotNullConstraint(columnDataType: "boolean", columnName: "usr_password_expired", tableName: "user", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-37") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "usr_username", tableName: "user", validate: "true")
    }

    changeSet(author: "djebeniani (generated)", id: "1673535466327-38") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "usr_version", tableName: "user", validate: "true")
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-39") {
        grailsChange {
            change {
                sql.executeUpdate('''update "user" set usr_password = '{bcrypt}' || usr_password;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-40") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table curatory_group drop column owner_id;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-41") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table user_curatory_groups rename to "curatory_group_user";''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-42") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "curatory_group_user"
    rename column user_id to cgu_user_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-43") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table "curatory_group_user"
    rename column curatory_group_id to cgu_curatory_group_fk;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-44") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table curatory_group_user
    add cgu_last_updated timestamp;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-45") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table curatory_group_user
    add cgu_date_created timestamp;''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1673535466327-46") {
        grailsChange {
            change {
                sql.executeUpdate('''delete from role where authority = 'ROLE_CONTRIBUTOR';''')
            }
            rollback {}
        }
    }

}

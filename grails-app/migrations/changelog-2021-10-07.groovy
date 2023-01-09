import wekb.helper.RCConstants
import wekb.RefdataCategory


databaseChangeLog = {
    
    changeSet(author: "djebeniani (modified)", id: "1633607422487-1") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "A & I Database").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "A & I Database").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-2") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Audio").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Audio").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-3") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Database").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Database").id])

            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1633607422487-4") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Film").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Film").id])

            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1633607422487-5") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Image").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Image").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-6") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Journal").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Journal").id])

            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1633607422487-7") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Book").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Book").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-8") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Published Score").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Published Score").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-9") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Article").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Article").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-10") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Software").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Software").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-11") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Statistics").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Statistics").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-12") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Market Data").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Market Data").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-13") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Biography").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Biography").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-14") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Legal Text").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Legal Text").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-15") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Cartography").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Cartography").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-16") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Miscellaneous").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Miscellaneous").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-17") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Other").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Other").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-18") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Standards").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Standards").id])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-19") {
        grailsChange {
            change {

                sql.executeUpdate('update title_instance_package_platform set tipp_medium_rv_fk = :newMedium where tipp_medium_rv_fk = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Dataset").id, oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Dataset").id])

            }
            rollback {}
        }
    }
  
}

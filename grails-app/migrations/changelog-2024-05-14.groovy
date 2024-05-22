import wekb.CuratoryGroup
import wekb.Org

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1715670174551-1") {
        addColumn(tableName: "org") {
            column(name: "org_description", type: "text")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1715670174551-2") {
        addColumn(tableName: "org") {
            column(name: "org_invoicing_yourself", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1715670174551-3") {
        grailsChange {
            change {
                sql.execute('''update org set org_invoicing_yourself = false;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1715670174551-4") {
        grailsChange {
            change {
                sql.execute('''alter table org alter column org_invoicing_yourself set not null;''')
                rollback {}
            }
        }
    }

    changeSet(author: "djebeniani (hand-coded))", id: "1715670174551-5") {
        grailsChange {
            change {
                Org.getAll().each {Org org ->
                    if(org.curatoryGroups.size() == 1){
                        CuratoryGroup curatoryGroup = org.curatoryGroups.curatoryGroup[0]
                        if(curatoryGroup && org.name != curatoryGroup.name){
                            curatoryGroup.name = org.name
                            curatoryGroup.save()
                        }
                    }
                }
            }
        }
    }

}

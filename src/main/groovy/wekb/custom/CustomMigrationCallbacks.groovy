package wekb.custom

import grails.util.Environment
import liquibase.Liquibase
import liquibase.changelog.ChangeSet
import liquibase.changelog.RanChangeSet
import liquibase.database.Database

class CustomMigrationCallbacks {

    def grailsApplication

    void beforeStartMigration(Database Database) {

    }

    void onStartMigration(Database database, Liquibase liquibase, String changelogName) {
        List allIds = liquibase.getDatabaseChangeLog().getChangeSets().collect { ChangeSet it -> it.filePath + '::' + it.id + '::' + it.author }
        List ranIds = database.getRanChangeSetList().collect { RanChangeSet it -> it.changeLog + '::' + it.id + '::' + it.author }
        List diff   = allIds.minus(ranIds)

        if (! diff.empty) {
            println '----------------------------------------Begin onStartMigration----------------------------------------'
            println '-     Database migration'
            println '-        ' + diff.size() + ' relevant changesets detected ..'
            if(!Environment.isDevelopmentMode()) {
                println '-        dumping current database ..'

                def dataSource = grailsApplication.config.dataSource
                URI uri = new URI(dataSource.url.substring(5))

                String backupFile = grailsApplication.config.deployBackupLocation + "/wekb-backup-${new Date().format('yyyy-MM-dd-HH:mm:ss')}.sql"

                Map<String, String> config = [
                        dbname: "${uri.getScheme()}://${dataSource.username}:${dataSource.password}@${uri.getHost()}:${uri.getPort()}${uri.getRawPath()}",
                        schema: "public",
                        file  : "${backupFile}"
                ]
                println '-           pg_dump: ' + grailsApplication.config.pgDumpPath
                println '-            source: ' + database
                println '-            target: ' + backupFile

                try {
                    String cmd = grailsApplication.config.pgDumpPath + ' -x ' + (config.collect { '--' + it.key + '=' + it.value }).join(' ')

                    cmd.execute().waitForProcessOutput(System.out, System.err)

                } catch (Exception e) {
                    println '-           error: ' + e.getMessage()
                    e.printStackTrace()
                }
            }else {
                println '-        not dumping current database because evironment is development mode..'
            }

            println '-        done ..'


            println '----------------------------------------End onStartMigration----------------------------------------'
        }

    }

    void afterMigrations(Database Database) {

    }
}

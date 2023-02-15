
println("------- Using local config from ~/.grails/wekb2-config.groovy ------- ")

grails.mail.host = 'localhost'
grails.mail.port = 25

deployBackupLocation = ''
pgDumpPath = ''

dataSource.username = 'gokb'
dataSource.password = 'gokb'
dataSource.url = 'jdbc:postgresql://localhost:5432/gokb'

serverUrl= 'http://localhost:8080/gokb'
baseUrl= 'http://localhost:8080/gokb'
server.contextPath = '/wekb'

systemId = 'we:kb-Dev'

logSql = true
formatSql = true

wekb.anonymizeUsers = false
wekb.es.cluster = 'gokbes'
wekb.es.host = 'localhost'
wekb.ftupdate_enabled = false
wekb.languagesUrl = 'localhost/languages'
wekb.packageUpdate.enabled = false
wekb.enable_statsrewrite = false
wekb.sendJobInfosJob = false
wekb.kbartImportStorageLocation = '/tmp/kbartImport'



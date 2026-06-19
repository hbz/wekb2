
println("------- Using local config from ~/.grails/wekb2-config.groovy ------- ")

grails.mail.host = 'localhost'
grails.mail.port = 25
grails.mail.disabled = true

deployBackupLocation = ''
pgDumpPath = ''

dataSource.username = 'wekb'
dataSource.password = 'wekb'
dataSource.url = 'jdbc:postgresql://localhost:5432/wekb'

serverUrl= 'http://localhost:8080/wekb'
baseUrl= 'http://localhost:8080/wekb'
server.contextPath = '/wekb'

systemId = 'we:kb-Dev'

logSql = true
formatSql = true

wekb.anonymizeUsers = false
wekb.auditCleanUpJob.enabled = false
wekb.es.cluster = 'wekb_es'
wekb.es.host = 'localhost'
wekb.ftupdate_enabled = false
wekb.packageUpdate.enabled = false
wekb.counterSourceUpdate.enabled = false
wekb.enable_statsrewrite = false
wekb.sendJobInfosJob = false
wekb.kbartImportStorageLocation = '/tmp/kbartImport'
wekb.tenantWithLogin = 'tenantWithLogin'
wekb.tenantWithOutLogin = 'wekb.tenantWithOutLogin'





package wekb

import grails.core.GrailsApplication
import grails.core.GrailsClass
import grails.gorm.transactions.Transactional
import liquibase.repackaged.com.opencsv.CSVParser
import liquibase.repackaged.com.opencsv.CSVReader
import liquibase.repackaged.com.opencsv.CSVReaderBuilder
import liquibase.repackaged.com.opencsv.ICSVParser
import wekb.auth.Role
import wekb.auth.User
import wekb.helper.RCConstants
import wekb.utils.DatabaseUtils

import javax.servlet.http.HttpServletRequest

@Transactional
class BootStrapService {

    RefdataReorderService refdataReorderService

    GrailsApplication grailsApplication
    ComponentStatisticService ComponentStatisticService
    ESWrapperService ESWrapperService

    def init() {
        log.info("Database: ${grailsApplication.config.getProperty('dataSource.url', String)}")
        log.info("Database datasource dbCreate: ${grailsApplication.config.getProperty('dataSource.dbCreate', String)}")
        log.info("Database migration plugin updateOnStart: ${grailsApplication.config.getProperty('grails.plugin.databasemigration.updateOnStart', String)}")

        log.info("\n\n\n **WARNING** \n\n\n - Automatic create of component identifiers index is no longer part of the domain model");

        // Add a custom check to see if this is an ajax request.
        HttpServletRequest.metaClass.isAjax = {
            'XMLHttpRequest' == delegate.getHeader('X-Requested-With')
        }

        // Global System Roles
        log.info("Set global system roles")
        Role.withTransaction() {
            def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER', roleType: 'global').save(failOnError: true)
            def editorRole = Role.findByAuthority('ROLE_EDITOR') ?: new Role(authority: 'ROLE_EDITOR', roleType: 'global').save(failOnError: true)
            def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN', roleType: 'global').save(failOnError: true)
            def apiRole = Role.findByAuthority('ROLE_API') ?: new Role(authority: 'ROLE_API', roleType: 'global').save(failOnError: true)
            def suRole = Role.findByAuthority('ROLE_SUPERUSER') ?: new Role(authority: 'ROLE_SUPERUSER', roleType: 'global').save(failOnError: true)
            def counterRole = Role.findByAuthority('ROLE_COUNTER') ?: new Role(authority: 'ROLE_COUNTER', roleType: 'global').save(failOnError: true)
            def vendorEditorRole = Role.findByAuthority('ROLE_VENDOR_EDITOR') ?: new Role(authority: 'ROLE_VENDOR_EDITOR', roleType: 'global').save(failOnError: true)
        }

        setRefDatas()

        log.info("reorderRefdata ..")
        refdataReorderService.reorderRefdata()

        log.info("Ensure default Identifier namespaces")

        setIdentifierNamespace()

        anonymizeUsers()

        log.info("Setup Trigram indices")
        initTrigramIndices()

        log.info("Ensuring ElasticSearch index")
        ensureEsIndices()

        log.info("Checking for missing component statistics")
        ComponentStatisticService.updateCompStats()
    }

    def destroy = {
    }

    void setIdentifierNamespace() {

        List<Map<String, Object>> namespaces = getParsedCsvData('setup/IdentifierNamespace.csv', 'IdentifierNamespace') as List<Map<String, Object>>

        RefdataCategory targetTypeCategory = RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)

        List<IdentifierNamespace> hardCodedIDNS = IdentifierNamespace.findAllByIsHardData(true)

        hardCodedIDNS.each { IdentifierNamespace current ->
            Map<String, Object> hardCodedNamespaceProps = namespaces.find { Map<String, Object> hardCoded -> hardCoded.value.toLowerCase() == current.value.toLowerCase() && hardCoded.targetType == current.targetType.value }
            boolean updated = false
            if(hardCodedNamespaceProps) {
                hardCodedNamespaceProps.each { String prop, Object value ->
                    if(prop == 'targetType'){
                        RefdataValue targetType = RefdataValue.findByValueAndOwner(value, targetTypeCategory)
                        if(targetType && current[prop] != targetType) {
                            current[prop] = targetType
                            updated = true
                        }
                    }else{
                        if(current[prop] != value) {
                            current[prop] = value
                            updated = true
                        }
                    }
                }
                if(!hardCodedNamespaceProps.containsKey('pattern') && current.pattern) {
                    current.pattern = null
                    updated = true
                }
            }
            else {
                current.isHardData = false
                updated = true
            }
            if(updated)
                current.save()
        }

        namespaces.each { Map<String, Object> namespaceProperties ->
            if(!hardCodedIDNS.find { IdentifierNamespace existing -> existing.value.toLowerCase() == namespaceProperties.value.toLowerCase() && existing.targetType.value == namespaceProperties.targetType }) {
                namespaceProperties.isHardData = true
                RefdataValue targetType = RefdataValue.findByValueAndOwner(namespaceProperties.targetType, targetTypeCategory)

                if(targetType) {
                    IdentifierNamespace idns = IdentifierNamespace.executeQuery('''
                                                                                select ns
                                                                                from IdentifierNamespace ns
                                                                                where lower(trim(ns.value)) = :value
                                                                                  and ns.targetType = :targetType
                    ''', [value     : namespaceProperties.value.toLowerCase(),
                          targetType: targetType]).find()
                    if (!idns) {
                        idns = new IdentifierNamespace([value: namespaceProperties.value, targetType: targetType])
                    }
                    idns.name = namespaceProperties.name
                    idns.description_en = namespaceProperties.description_en
                    idns.isHardData = namespaceProperties.isHardData
                    idns.pattern = namespaceProperties.pattern
                    idns.urlPrefix = namespaceProperties.urlPrefix
                    if (!idns.save())
                        log.error(idns.getErrors().getAllErrors().toListString())
                }
            }
        }
    }


    def setRefDatas() {

        log.info("setRefDatas")
        RefdataValue.executeUpdate('UPDATE RefdataValue rdv SET rdv.isHardData =:reset', [reset: false])
        RefdataCategory.executeUpdate('UPDATE RefdataCategory rdc SET rdc.isHardData =:reset', [reset: false])

        List rdcList = getParsedCsvData('setup/RefdataCategory.csv', 'RefdataCategory')

        rdcList.each { map ->
            map.hardData = true
            RefdataCategory.construct(map)
        }

        List rdvList = getParsedCsvData('setup/RefdataValue.csv', 'RefdataValue')

        rdvList.each { map ->
            map.hardData = true
            RefdataValue.construct(map)
        }

        List ddcList = getParsedCsvData('setup/DDC.csv', 'RefdataValue')

        ddcList.each { map ->
            if(map.get('token').toInteger() < 10)
            {
                map.token = "00"+map.get('token')
            }

            if(map.get('token').toInteger() < 100 && map.get('token').toInteger() >= 10)
            {
                map.token = "0"+map.get('token')
            }
            map.hardData = true
            RefdataValue.construct(map)
        }

        List languages = getParsedCsvData('setup/ISO-639-2.csv', 'RefdataValue')

        languages.each { map ->
            map.hardData = true
            RefdataValue.construct(map)
        }

        log.info("Deleting any null refdata values")
        RefdataValue.executeUpdate('delete from RefdataValue where value is null')

/*        log.info("Cleaup RefdataValue where isHardData = false")
        try {
            RefdataValue.executeUpdate('delete from RefdataValue where isHardData = false')
        }
        catch (Exception e) {
            log.error("Problem by Cleaup RefdataValue where isHardData = false -> Exception: ${e}")
        }

        log.info("Cleaup RefdataCategory where isHardData = false")
        try {
            RefdataCategory.executeUpdate('delete from RefdataCategory where isHardData = false')
        }
        catch (Exception e) {
            log.error("Problem by Cleaup RefdataCategory where isHardData = false -> Exception: ${e}")
        }*/


    }


    def anonymizeUsers() {
        if(grailsApplication.config.getProperty('wekb.anonymizeUsers', Boolean)) {
            log.info("anonymizeUsers")
            User.findAll().each { User user ->

                log.info("anonymizeUsers ${user.displayName} ${user.username}")
                if(user.curatoryGroupUsers && user.curatoryGroupUsers.curatoryGroup.find{CuratoryGroup curatoryGroup -> curatoryGroup.name == "hbz" || curatoryGroup.name == "LAS:eR"}){
                    user.email = 'local@localhost.local'
                }else {
                    user.username = "User ${user.id}"
                    user.displayName = "User ${user.id}"
                    user.email = 'local@localhost.local'
                    user.password = "${user.lastUpdated}+${user.id}"
                    user.enabled = false
                    user.accountLocked = true
                    user.save()
                }

            }
        }
    }

    void initTrigramIndices() {
        DatabaseUtils.initTrigramIndices()
    }

    def ensureEsIndices() {
        def esIndices = grailsApplication.config.getProperty('wekb.es.indices', Map)?.values()
        for (String indexName in esIndices) {
            try {
                ESWrapperService.createIndex(indexName)
            }
            catch (Exception e) {
                log.error("Problem by ensureEsIndices -> Exception: ${e}")
            }
        }
    }


    List getParsedCsvData(String filePath, String objType) {

        List result = []
        File csvFile = grailsApplication.mainContext.getResource(filePath).file

        if (! ['RefdataCategory', 'RefdataValue', 'IdentifierNamespace'].contains(objType)) {
            println "WARNING: invalid object type ${objType}!"
        }
        else if (! csvFile.exists()) {
            println "WARNING: ${filePath} not found!"
        }
        else {
            csvFile.withReader { reader ->
                //CSVReader csvr = new CSVReader(reader, (char) ';', (char) '"', (char) '\\', (int) 1)
                String[] line

                ICSVParser csvp = new CSVParser((char) ';', (char) '"', (char) '\\', false, true, false, CSVParser.DEFAULT_NULL_FIELD_INDICATOR, Locale.getDefault())
                CSVReader csvr = new CSVReaderBuilder( reader ).withCSVParser( csvp ).withSkipLines( 1 ).build()
                while (line = csvr.readNext()) {
                    if (line[0]) {
                        if (objType == 'RefdataCategory') {
                            // CSV: [token; value_de; value_en]
                            Map<String, Object> map = [
                                    token   : line[0].trim(),
                                    desc_de: line[1].trim(),
                                    desc_en: line[2].trim(),
                                    hardData: true
                            ]
                            result.add(map)
                        }
                        if (objType == 'RefdataValue') {
                            // CSV: [rdc; token; value_de; value_en]
                            Map<String, Object> map = [
                                    token   : line[1].trim(),
                                    rdc     : line[0].trim(),
                                    value_de: line[2].trim(),
                                    value_en: line[3].trim(),
                                    hardData: true

                            ]
                            result.add(map)
                        }
                        if (objType == 'IdentifierNamespace') {
                            // CSV: [value; name; description_en; targetType; url_prefix; pattern]
                            Map<String, Object> map = [
                                    value     : line[0].trim(),
                                    name: line[1].trim(),
                                    description_en: line[2].trim() ?: null,
                                    targetType        : line[3].trim() ?: null,
                                    urlPrefix     : line[4].trim() ?: null,
                                    pattern: line[5].trim() ?: null
                            ]
                            result.add(map)
                        }
                    }
                }
            }
        }

        result
    }
}

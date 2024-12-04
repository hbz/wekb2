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
            def sushiRole = Role.findByAuthority('ROLE_SUSHI') ?: new Role(authority: 'ROLE_SUSHI', roleType: 'global').save(failOnError: true)
            def vendorEditorRole = Role.findByAuthority('ROLE_VENDOR_EDITOR') ?: new Role(authority: 'ROLE_VENDOR_EDITOR', roleType: 'global').save(failOnError: true)
        }

        setRefDatas()

        log.info("Ensure default Identifier namespaces")
        def namespaces = [
                [value: 'cup', name: 'cup', targetType: 'TitleInstancePackagePlatform'],
                [value: 'dnb', name: 'dnb', targetType: 'TitleInstancePackagePlatform'],
                [value: 'doi', name: 'DOI', targetType: 'TitleInstancePackagePlatform'],
                [value: 'eissn', name: 'e-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'ezb', name: 'EZB-ID', targetType: 'TitleInstancePackagePlatform'],
                [value: 'gnd-id', name: 'gnd-id', targetType: 'TitleInstancePackagePlatform'],
                [value: 'eisbn', name: 'eISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'issn', name: 'p-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'issnl', name: 'ISSN-L', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$",  targetType: 'TitleInstancePackagePlatform'],
                [value: 'isbn', name: 'ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'oclc', name: 'oclc', targetType: 'TitleInstancePackagePlatform'],
                [value: 'preselect', name: 'preselect', targetType: 'TitleInstancePackagePlatform'],
                [value: 'zdb', name: 'ZDB-ID', pattern: "^\\d+-[\\dxX]\$", targetType: 'TitleInstancePackagePlatform'],

                //Kbart Import
                [value: 'ill_indicator', name: 'Ill Indicator',  targetType: 'TitleInstancePackagePlatform'],
                [value: 'package_isci', name: 'Package ISCI',  targetType: 'TitleInstancePackagePlatform'],
                [value: 'package_isil', name: 'Package ISIL',  targetType: 'TitleInstancePackagePlatform'],
                [value: 'package_ezb_anchor', name: 'EZB Anchor',  targetType: 'TitleInstancePackagePlatform'],


                [value: 'Anbieter_Produkt_ID', name: 'Provider_Product_ID', targetType: 'Package'],
                [value: 'dnb', name: 'dnb', targetType: 'Package'],
                [value: 'doi', name: 'DOI', targetType: 'Package'],
                [value: 'ezb', name: 'EZB-ID', targetType: 'Package'],
                [value: 'gvk_ppn', name: 'gvk_ppn', targetType: 'Package'],
                [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$",  targetType: 'Package'],
                [value: 'package_isci', name: 'Package ISCI',  targetType: 'Package'],
                [value: 'package_ezb_anchor', name: 'EZB Anchor',  targetType: 'Package'],
                [value: 'zdb', name: 'ZDB-ID', pattern: "^\\d+-[\\dxX]\$", targetType: 'Package'],
                [value: 'zdb_ppn', name: 'EZB Anchor',  targetType: 'Package'],


                [value: 'gnd-id', name: 'gnd-id', targetType: 'Org'],
                [value: 'dbpedia', name: 'DBpedia', targetType: 'Org'],
                [value: 'viaf', name: 'VIAF', targetType: 'Org'],
                [value: 'loc id', name: 'LOC ID', targetType: 'Org'],
                [value: 'isni', name: 'ISNI', targetType: 'Org'],
                [value: 'ror id', name: 'ROR ID', targetType: 'Org'],
                [value: 'wikidata id', name: 'Wikidata ID', targetType: 'Org'],
                [value: 'crossref funder id', name: 'Crossref Funder ID', targetType: 'Org'],

        ]
        log.debug("reorderRefdata ..")
        refdataReorderService.reorderRefdata()

        namespaces.each { ns ->
            RefdataValue targetType = RefdataValue.findByValueAndOwner(ns.targetType, RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))
            def ns_obj = IdentifierNamespace.findByValueAndTargetType(ns.value, targetType)

            if (ns_obj) {
                if (ns.pattern && !ns_obj.pattern) {
                    ns_obj.pattern = ns.pattern
                }

                if (ns.name && !ns_obj.name) {
                    ns_obj.name = ns.name
                }

                if (ns.family && !ns_obj.family) {
                    ns_obj.family = ns.family
                }

                if (ns.targetType) {
                    ns_obj.targetType = targetType
                }

                ns_obj.save()
            } else {
                ns.targetType = targetType
                ns_obj = new IdentifierNamespace(ns).save(failOnError: true)
            }

            log.info("Ensured ${ns_obj}!")
        }

        anonymizeUsers()

        log.info("Ensuring ElasticSearch index")
        ensureEsIndices()

        log.info("Checking for missing component statistics")
        ComponentStatisticService.updateCompStats()
    }

    def destroy = {
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

                log.debug("anonymizeUsers ${user.displayName} ${user.username}")
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

        if (! ['RefdataCategory', 'RefdataValue'].contains(objType)) {
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
                            // CSV: [token, value_de, value_en]
                            Map<String, Object> map = [
                                    token   : line[0].trim(),
                                    desc_de: line[1].trim(),
                                    desc_en: line[2].trim(),
                                    hardData: true
                            ]
                            result.add(map)
                        }
                        if (objType == 'RefdataValue') {
                            // CSV: [rdc, token, value_de, value_en]
                            Map<String, Object> map = [
                                    token   : line[1].trim(),
                                    rdc     : line[0].trim(),
                                    value_de: line[2].trim(),
                                    value_en: line[3].trim(),
                                    hardData: true

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

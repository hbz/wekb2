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

    GrailsApplication grailsApplication
    ComponentStatisticService ComponentStatisticService
    ESWrapperService ESWrapperService

    def init() {
        log.info("Database: ${grailsApplication.config.dataSource.url}")
        log.info("Database datasource dbCreate: ${grailsApplication.config.dataSource.dbCreate}")
        log.info("Database migration plugin updateOnStart: ${grailsApplication.config.grails.plugin.databasemigration.updateOnStart}")

        log.info("\n\n\n **WARNING** \n\n\n - Automatic create of component identifiers index is no longer part of the domain model");
        log.info("Create manually with create index norm_id_value_idx on kbcomponent(kbc_normname(64),id_namespace_fk,class)");


        // Add our custom metaclass methods for all KBComponents.
        alterDefaultMetaclass()

        // TODO: 2023 check is important?
        // Add Custom APIs.
        //addCustomApis()

        // Add a custom check to see if this is an ajax request.
        HttpServletRequest.metaClass.isAjax = {
            'XMLHttpRequest' == delegate.getHeader('X-Requested-With')
        }

        // Global System Roles
        log.info("Set global system roles")
        KBComponent.withTransaction() {
            def contributorRole = Role.findByAuthority('ROLE_CONTRIBUTOR') ?: new Role(authority: 'ROLE_CONTRIBUTOR', roleType: 'global').save(failOnError: true)
            def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER', roleType: 'global').save(failOnError: true)
            def editorRole = Role.findByAuthority('ROLE_EDITOR') ?: new Role(authority: 'ROLE_EDITOR', roleType: 'global').save(failOnError: true)
            def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN', roleType: 'global').save(failOnError: true)
            def apiRole = Role.findByAuthority('ROLE_API') ?: new Role(authority: 'ROLE_API', roleType: 'global').save(failOnError: true)
            def suRole = Role.findByAuthority('ROLE_SUPERUSER') ?: new Role(authority: 'ROLE_SUPERUSER', roleType: 'global').save(failOnError: true)
        }

        refdataCats()

        CuratoryGroup.withTransaction() {
            if (grailsApplication.config.wekb.defaultCuratoryGroup != null && grailsApplication.config.wekb.defaultCuratoryGroup != "") {

                log.info("Ensure curatory group: ${grailsApplication.config.wekb.defaultCuratoryGroup}");

                def local_cg = CuratoryGroup.findByName(grailsApplication.config.wekb.defaultCuratoryGroup) ?:
                        new CuratoryGroup(name: grailsApplication.config.wekb.defaultCuratoryGroup).save(flush: true, failOnError: true);
            }
        }

        log.info("Fix missing Combo status");

        def status_active = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_ACTIVE)
        int num_c = Combo.executeUpdate("update Combo set status = :status where status is null", [status: status_active])
        log.debug("${num_c} combos updated");


        log.info("Ensure default Identifier namespaces")
        def namespaces = [
                [value: 'cup', name: 'cup', targetType: 'TitleInstancePackagePlatform'],
                [value: 'dnb', name: 'dnb', targetType: 'TitleInstancePackagePlatform'],
                [value: 'doi', name: 'DOI', targetType: 'TitleInstancePackagePlatform'],
                [value: 'eissn', name: 'e-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'ezb', name: 'EZB-ID', targetType: 'TitleInstancePackagePlatform'],
                [value: 'gnd-id', name: 'gnd-id', targetType: 'TitleInstancePackagePlatform'],
                [value: 'isbn', name: 'ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'issn', name: 'p-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'issnl', name: 'ISSN-L', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$",  targetType: 'TitleInstancePackagePlatform'],
                [value: 'pisbn', name: 'Print-ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
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
                [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$",  targetType: 'Org'],
                [value: 'zdb_ppn', name: 'EZB Anchor',  targetType: 'Org'],
        ]

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

                ns_obj.save(flush: true)
            } else {
                ns.targetType = targetType
                ns_obj = new IdentifierNamespace(ns).save(flush: true, failOnError: true)
            }

            log.info("Ensured ${ns_obj}!")
        }

        anonymizeUsers()

        log.info("Ensuring ElasticSearch index")
        ensureEsIndices()

        log.info("Checking for missing component statistics")
        ComponentStatisticService.updateCompStats()
    }

    /*private void addCustomApis() {

        log.info("addCustomApis()")
        (grailsApplication.getArtefacts("Domain")*.clazz).each { Class<?> c ->

            // SO: Changed this to use the APIs 'applicableFor' method that is used to check whether,
            // to add to the class or not. This defaults to "true". Have overriden on the GrailsDomainHelperApi utils
            // and moved the selective code there. This means that *ALL* domain classes will still receive the methods in the
            // apiClasses.

            // log.debug("Considering ${c}")
            grailsApplication.config.apiClasses.each { String className ->
                // log.debug("Adding methods to ${c.name} from ${className}");
                // Add the api methods.
                A_Api.addMethods(c, Class.forName(className))
            }
        }
    }*/

    def alterDefaultMetaclass = {

        // Inject helpers to Domain classes.
        log.info("alterDefaultMetaclass()")
        grailsApplication.domainClasses.each { GrailsClass domainClass ->

            // Extend the domain class.
            DomainClassExtender.extend(domainClass)

        }
    }

    def destroy = {
    }


    def refdataCats() {

        log.info("refdataCats")
        RefdataValue.executeUpdate('UPDATE RefdataValue rdv SET rdv.isHardData =:reset', [reset: false])
        RefdataCategory.executeUpdate('UPDATE RefdataCategory rdc SET rdc.isHardData =:reset', [reset: false])

        List rdcList = getParsedCsvData('setup/RefdataCategory.csv', 'RefdataCategory')

        rdcList.each { map ->
            RefdataCategory.construct(map)
        }

        List rdvList = getParsedCsvData('setup/RefdataValue.csv', 'RefdataValue')

        rdvList.each { map ->
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

            RefdataValue.construct(map)
        }

        log.debug("Deleting any null refdata values")
        RefdataValue.executeUpdate('delete from RefdataValue where value is null')

        log.debug("Languages Service initialize")
        LanguagesService.initialize()
    }


    def anonymizeUsers() {
        if(grailsApplication.config.wekb.anonymizeUsers) {
            log.info("anonymizeUsers")
            User.findAll().each { User user ->

                log.debug("anonymizeUsers ${user.displayName} ${user.username}")
                if(user.curatoryGroups.find{CuratoryGroup curatoryGroup -> curatoryGroup.name == "hbz" || curatoryGroup.name == "LAS:eR"}){
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
        def esIndices = grailsApplication.config.wekb.es.indices?.values()
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

package wekb

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import wekb.helper.RDStore

import java.text.Normalizer

@Transactional
class Api2Service {

    DateFormatService dateFormatService
    GrailsApplication grailsApplication
    GenericOIDService genericOIDService

    private ApiTemplates = new java.util.HashMap<String, Map>()

    @javax.annotation.PostConstruct
    def init() {
        ApiTemplates.put('orgs', orgs())
        ApiTemplates.put('packages', packages())
        ApiTemplates.put('platforms', platforms())
        ApiTemplates.put('tipps', tipps())
        ApiTemplates.put('deletedKBComponents', deletedKBComponents())

    }

    public Map getApiTemplate(String type) {
        return ApiTemplates.get(type);
    }

    Map orgs() {
        Map result = [
                baseclass   : 'wekb.Org',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                //General Fields
                                [
                                        qparam     : 'name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B']
                                ],
                                [
                                        qparam     : 'identifier',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'ids.value', 'wildcard': 'B']
                                ],
                                [
                                        qparam     : 'identifierNamespace',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.namespace.value']
                                ],
                                [
                                        qparam     : 'curatoryGroup',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup.name']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'changedBefore',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'smaller', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'uuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'uuid']
                                ],
                                //spec Fields
                                [
                                        qparam     : 'variantNames',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'variantNames.variantName']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'roles',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'roles'],
                                ],


                        ],
                        qbeSortFields: [
                                [sort: 'name'],
                                [sort: 'status'],
                                [sort: 'lastUpdated'],
                                [sort: 'dateCreated'],
                                [sort: 'curatoryGroups.curatoryGroup.name'],
                                [sort: 'abbreviatedName']
                        ]
                ]
        ]

        result
    }

    Map packages() {
        Map result = [
                baseclass   : 'wekb.Package',
                defaultSort : 'lastUpdated',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [
                                //General Fields
                                [
                                        qparam     : 'name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike_Combine_Name_And_Anbieter_Produkt_ID_And_ZDB', 'prop': 'name', 'wildcard': 'B']
                                ],
                                [
                                        qparam     : 'identifier',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'ids.value', 'wildcard': 'B']
                                ],
                                [
                                        qparam     : 'identifierNamespace',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.namespace.value']
                                ],
                                [
                                        qparam     : 'curatoryGroup',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup.name']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'changedBefore',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'smaller', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'uuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'uuid']
                                ],
                                //spec Fields
                                [
                                        qparam     : 'variantNames',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'variantNames.variantName']
                                ],
                                [
                                        qparam     : 'providerUuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.uuid']
                                ],
                                [
                                        qparam     : 'platformUuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.uuid']
                                ],
                        ],

                        qbeSortFields: [
                                [sort: 'name'],
                                [sort: 'status'],
                                [sort: 'lastUpdated'],
                                [sort: 'dateCreated'],
                                [sort: 'curatoryGroups.curatoryGroup.name'],
                                [sort: 'provider.name'],
                                [sort: 'nominalPlatform.name'],
                                [sort: 'contentType'],
                                [sort: 'scope'],
                                [sort: 'currentTippCount'],
                                [sort: 'retiredTippCount'],
                                [sort: 'expectedTippCount'],
                                [sort: 'deletedTippCount']
                        ]
                ]
        ]

        result
    }

    Map platforms() {
        Map result = [
                baseclass   : 'wekb.Platform',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                //General Fields
                                [
                                        qparam     : 'name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B']
                                ],
                                [
                                        qparam     : 'identifier',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'ids.value', 'wildcard': 'B']
                                ],
                                [
                                        qparam     : 'identifierNamespace',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.namespace.value']
                                ],
                                [
                                        qparam     : 'curatoryGroup',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup.name']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'changedBefore',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'smaller', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'uuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'uuid']
                                ],
                                //spec Fields
                                [
                                        qparam     : 'providerUuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.uuid']
                                ],
                        ],
                        qbeSortFields: [
                                [sort: 'name'],
                                [sort: 'status'],
                                [sort: 'lastUpdated'],
                                [sort: 'dateCreated'],
                                [sort: 'curatoryGroups.curatoryGroup.name'],
                                [sort: 'provider.name'],
                                [sort: 'primaryUrl']
                        ]
                ]
        ]
        result
    }

    Map tipps() {
        Map result = [
                baseclass: 'wekb.TitleInstancePackagePlatform',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                //General Fields
                                [
                                        qparam     : 'name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B']
                                ],
                                [
                                        qparam     : 'identifier',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'ids.value', 'wildcard': 'B']
                                ],
                                [
                                        qparam     : 'identifierNamespace',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.namespace.value']
                                ],
                                [
                                        qparam     : 'curatoryGroup',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup.name']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'changedBefore',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'smaller', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'uuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'uuid']
                                ],
                                //spec Fields
                                [
                                        qparam     : 'variantNames',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'variantNames.variantName']
                                ],
                                [
                                        qparam     : 'packageUuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.uuid']
                                ],
                                [
                                        qparam     : 'platformUuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform.uuid']
                                ],

                        ],

                        qbeSortFields: [
                                [sort: 'name'],
                                [sort: 'status'],
                                [sort: 'lastUpdated'],
                                [sort: 'dateCreated'],
                                [sort: 'curatoryGroups.curatoryGroup.name'],
                                [sort: 'pkg.name'],
                                [sort: 'hostPlatform.name'],
                                [sort: 'publicationType.value'],
                                [sort: 'medium.value'],
                                [sort: 'firstAuthor'],
                                [sort: 'url']
                        ]
                ]
        ]

        result
    }

    Map deletedKBComponents() {
        Map result = [
                baseclass: 'wekb.DeletedKBComponent',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                //General Fields
                                [
                                        qparam     : 'name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'changedBefore',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'smaller', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                ],
                                [
                                        qparam     : 'uuid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'uuid']
                                ],

                        ],

                        qbeSortFields: [
                                [sort: 'name'],
                                [sort: 'status'],
                                [sort: 'lastUpdated'],
                                [sort: 'dateCreated']
                        ]
                ]
        ]

        result
    }

    private String generateSortName(String input_title) {
        if (!input_title) return null
        String s1 = Normalizer.normalize(input_title, Normalizer.Form.NFKD).trim().toLowerCase()


        return s1.trim()

    }

/*    private LinkedHashMap<Object, Object> mapDomainFieldsToSpecFields2(Map apiSearchTemplate, Object objectList) {
        LinkedHashMap<Object, Object> result = [:]


        apiSearchTemplate.qbeConfig.qbeResults.eachWithIndex { Map fieldMapping, int idx ->

            if(objectList[idx+1] && objectList[idx+1].class == RefdataValue.class){
                result."${fieldMapping.fieldName}" = objectList[idx+1] ? objectList[idx+1].value : ""
            }else {
                result."${fieldMapping.fieldName}" = objectList[idx+1]
            }

        }

        Object object = null

        switch (apiSearchTemplate.baseclass) {
            case Org.class.name:
                object = Org.get(objectList[0])
                break
            case Package.class.name:
                object = Package.get(objectList[0])
                break
            case Platform.class.name:
                object = Platform.get(objectList[0])
                break
            case TitleInstancePackagePlatform.class.name:
                object = TitleInstancePackagePlatform.get(objectList[0])
                break
            case DeletedKBComponent.class.name:
                object = DeletedKBComponent.get(objectList[0])
                break
        }

        result = mapHasManyFieldsToSpecFields(object, result)

        return result

    }*/

    /*@Deprecated
    private LinkedHashMap<Object, Object> mapHasManyFieldsToSpecFields(def object, LinkedHashMap<Object, Object> result) {
        //LinkedHashMap<Object, Object> result = [:]

        //result.test = object

        if(object.class.name == Package.class.name) {

            *//* result.uuid = object.uuid
             result.name = object.name
             result.sortname = generateSortName(object.name)
             result.status = object.status?.value
             result.componentType = object.class.simpleName

             result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)
             result.dateCreatedDisplay = dateFormatService.formatIsoTimestamp(object.dateCreated)

             if(object.provider){
                 result.cpname = object.provider.name
                 result.provider = object.provider.getOID()
                 result.providerName = object.provider.name
                 result.providerUuid = object.provider.uuid
             }else {
                 result.cpname = ""
                 result.provider = ""
                 result.providerName = ""
                 result.providerUuid = ""
             }

             if(object.nominalPlatform) {
                 result.nominalPlatform = object.nominalPlatform.getOID()
                 result.nominalPlatformName = object.nominalPlatform.name
                 result.nominalPlatformUuid = object.nominalPlatform.uuid
             }else {
                 result.nominalPlatform = ""
                 result.nominalPlatformName = ""
                 result.nominalPlatformUuid = ""
             }

             result.description = object.description
             result.descriptionURL = object.descriptionURL
             *//*/*

            result.titleCount = object.getTippCount()
            result.currentTippCount = object.getCurrentTippCount()
            result.retiredTippCount = object.getRetiredTippCount()
            result.expectedTippCount = object.getExpectedTippCount()
            result.deletedTippCount = object.getDeletedTippCount()

              *//*/*


            result.breakable = object.breakable ? object.breakable.value : ""
            result.consistent = object.consistent?.value
            result.contentType = object.contentType?.value
            result.freeTrial = object.freeTrial?.value
            result.scope = object.scope ? object.scope.value : ""
            result.paymentType = object.paymentType ? object.paymentType.value : ""
            result.openAccess = object.openAccess?.value
            result.scope = object.scope ? object.scope.value : ""

            result.freeTrialPhase = object.freeTrialPhase
 *//*

            if (object.kbartSource) {
                result.source = [
                        id              : object.kbartSource.id,
                        uuid              : object.kbartSource.uuid,
                        name            : object.kbartSource.name,
                        automaticUpdates: object.kbartSource.automaticUpdates,
                        url             : object.kbartSource.url,
                        frequency       : object.kbartSource.frequency?.value,
                ]
                if (object.kbartSource.lastRun){
                    result.source.lastRun = dateFormatService.formatIsoTimestamp(object.kbartSource.lastRun)
                }
            }else {
                result.source = []
            }

            result.altname = []
            object.variantNames.each { vn ->
                result.altname.add(vn.variantName)
            }

            result.curatoryGroups = []
            if(object.hasProperty('curatoryGroups')) {
                object.curatoryGroups.each {
                    result.curatoryGroups.add([name: it.curatoryGroup.name,
                                               type: it.curatoryGroup.type?.value,
                                               curatoryGroup: it.curatoryGroup.getOID()])
                }
            }

            result.identifiers = []
            object.ids.each { idc ->
                result.identifiers.add([namespace    : idc.namespace.value,
                                        value        : idc.value,
                                        namespaceName: idc.namespace.name])
            }


            result.nationalRanges = []
            object.nationalRanges.each { nationalRange ->
                result.nationalRanges.add([value     : nationalRange.value,
                                           value_de  : nationalRange.value_de,
                                           value_en  : nationalRange.value_en])
            }

            result.regionalRanges = []
            object.regionalRanges.each { regionalRange ->
                result.regionalRanges.add([value     : regionalRange.value,
                                           value_de  : regionalRange.value_de,
                                           value_en  : regionalRange.value_en])
            }

            result.ddcs = []
            object.ddcs.each { ddc ->
                result.ddcs.add([value     : ddc.value,
                                 value_de  : ddc.value_de,
                                 value_en  : ddc.value_en])
            }

            result.packageArchivingAgencies = []
            object.paas.each { PackageArchivingAgency paa ->
                result.packageArchivingAgencies.add([archivingAgency: paa.archivingAgency?.value,
                                                     openAccess  : paa.openAccess?.value,
                                                     postCancellationAccess  : paa.postCancellationAccess?.value])
            }


            result
        }else if(object.class.name == Org.class.name) {

            *//* result.uuid = object.uuid
             result.name = object.name
             result.sortname = generateSortName(object.name)
             result.status = object.status?.value
             result.componentType = object.class.simpleName

             result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)
             result.dateCreatedDisplay = dateFormatService.formatIsoTimestamp(object.dateCreated)

             result.kbartDownloaderURL = object.kbartDownloaderURL
             result.metadataDownloaderURL = object.metadataDownloaderURL
             result.homepage = object.homepage*//*

            result.roles = []
            object.roles.each { role ->
                result.roles.add(role.value)
            }

            result.altname = []

            object.variantNames.each { vn ->
                result.altname.add(vn.variantName)
            }

            if(object.hasProperty('curatoryGroups')) {
                result.curatoryGroups = []
                object.curatoryGroups?.each {
                    result.curatoryGroups.add([name: it.curatoryGroup.name,
                                               type: it.curatoryGroup.type?.value,
                                               curatoryGroup: it.curatoryGroup.getOID()])
                }
            }

            result.identifiers = []
            object.ids.each { idc ->
                result.identifiers.add([namespace    : idc.namespace.value,
                                        value        : idc.value,
                                        namespaceName: idc.namespace.name])
            }

            result.contacts = []
            object.contacts.each { Contact contact ->
                result.contacts.add([  content: contact.content,
                                       contentType: contact.contentType?.value,
                                       type: contact.type?.value,
                                       language: contact.language?.value])
            }

            result
        }else if(object.class.name == Platform.class.name) {

            result.uuid = object.uuid
            result.name = object.name
            result.sortname = generateSortName(object.name)
            result.status = object.status?.value
            result.componentType = object.class.simpleName

            result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)
            result.dateCreatedDisplay = dateFormatService.formatIsoTimestamp(object.dateCreated)

            result.cpname = object.provider?.name
            result.provider = object.provider ? object.provider.getOID() : ""
            result.providerName = object.provider ? object.provider.name : ""
            result.providerUuid = object.provider ? object.provider.uuid : ""

            result.primaryUrl = object.primaryUrl

            result.titleNamespace = object.titleNamespace?.value

            result.lastAuditDate = object.lastAuditDate ? dateFormatService.formatIsoTimestamp(object.lastAuditDate) : ""

            result.ipAuthentication = object.ipAuthentication?.value

            result.shibbolethAuthentication = object.shibbolethAuthentication?.value

            result.openAthens = object.openAthens?.value

            result.passwordAuthentication = object.passwordAuthentication?.value

            result.statisticsFormat = object.statisticsFormat?.value
            result.counterR3Supported = object.counterR3Supported?.value
            result.counterR4Supported = object.counterR4Supported?.value
            result.counterR5Supported = object.counterR5Supported?.value
            result.counterR4SushiApiSupported = object.counterR4SushiApiSupported?.value
            result.counterR5SushiApiSupported = object.counterR5SushiApiSupported?.value
            result.counterR4SushiServerUrl = object.counterR4SushiServerUrl
            result.counterR5SushiServerUrl = object.counterR5SushiServerUrl
            result.counterRegistryUrl = object.counterRegistryUrl
            result.counterCertified = object.counterCertified?.value
            result.statisticsAdminPortalUrl = object.statisticsAdminPortalUrl
            result.statisticsUpdate = object.statisticsUpdate?.value
            result.proxySupported = object.proxySupported?.value

            result.counterRegistryApiUuid = object.counterRegistryApiUuid

            if(object.hasProperty('curatoryGroups')) {
                result.curatoryGroups = []
                object.curatoryGroups?.each {
                    result.curatoryGroups.add([name: it.curatoryGroup.name,
                                               type: it.curatoryGroup.type?.value,
                                               curatoryGroup: it.curatoryGroup.getOID()])
                }
            }


            result.identifiers = []
            object.ids.each { idc ->
                result.identifiers.add([namespace    : idc.namespace.value,
                                        value        : idc.value,
                                        namespaceName: idc.namespace.name])
            }

            result.federations = []
            object.federations.each { PlatformFederation platformFederation ->
                result.federations.add([federation    : platformFederation.federation])
            }

            result
        }else if(object.class.name == TitleInstancePackagePlatform.class.name) {

            result.uuid = object.uuid
            result.name = object.name
            result.sortname = generateSortName(object.name)
            result.status = object.status?.value
            result.componentType = object.class.simpleName

            result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)
            result.dateCreatedDisplay = dateFormatService.formatIsoTimestamp(object.dateCreated)

            if (object.pkg) {
                result.tippPackage = object.pkg.getOID()
                result.tippPackageName = object.pkg.name
                result.tippPackageUuid = object.pkg.uuid
            }

            if (object.hostPlatform) {
                result.hostPlatform = object.hostPlatform.getOID()
                result.hostPlatformName = object.hostPlatform.name
                result.hostPlatformUuid = object.hostPlatform.uuid
            }

            result.titleType = object.getTitleType() ?: 'Unknown'
            result.url = object.url

            result.dateFirstOnline = object.dateFirstOnline ? dateFormatService.formatIsoTimestamp(object.dateFirstOnline) : ""
            result.dateFirstInPrint = object.dateFirstInPrint ? dateFormatService.formatIsoTimestamp(object.dateFirstInPrint) : ""
            result.accessStartDate = object.accessStartDate ? dateFormatService.formatIsoTimestamp(object.accessStartDate) : ""
            result.accessEndDate = object.accessEndDate ? dateFormatService.formatIsoTimestamp(object.accessEndDate) : ""
            result.lastChangedExternal = object.lastChangedExternal ? dateFormatService.formatIsoTimestamp(object.lastChangedExternal) : ""

            result.medium = object.medium?.value
            result.publicationType = object.publicationType?.value
            result.openAccess = object.openAccess?.value
            result.accessType = object.accessType?.value
            result.publicationType = object.publicationType?.value


            result.publisherName = object.publisherName
            result.subjectArea = object.subjectArea
            result.series = object.series
            result.volumeNumber = object.volumeNumber
            result.editionStatement = object.editionStatement
            result.firstAuthor = object.firstAuthor
            result.firstEditor = object.firstEditor
            result.parentPublicationTitleId = object.parentPublicationTitleId
            result.precedingPublicationTitleId = object.precedingPublicationTitleId
            result.supersedingPublicationTitleId = object.supersedingPublicationTitleId
            result.note = object.note
            result.fromKbartImport = object.fromKbartImport


            if (object.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                result.coverage = []
                ArrayList<TIPPCoverageStatement> coverage_src = object.coverageStatements
                coverage_src.each { TIPPCoverageStatement tcs ->
                    def cst = [:]
                    if (tcs.startDate) cst.startDate = dateFormatService.formatIsoTimestamp(tcs.startDate)
                    cst.startVolume = tcs.startVolume ?: ""
                    cst.startIssue = tcs.startIssue ?: ""
                    if (tcs.endDate) cst.endDate = dateFormatService.formatIsoTimestamp(tcs.endDate)
                    cst.endVolume = tcs.endVolume ?: ""
                    cst.endIssue = tcs.endIssue ?: ""
                    cst.embargo = tcs.embargo ?: ""
                    cst.coverageNote = tcs.coverageNote ?: ""
                    cst.coverageDepth = tcs.coverageDepth ? tcs.coverageDepth.value : ""
                    result.coverage.add(cst)
                }
            }

            result.identifiers = []
            object.ids.each { idc ->
                result.identifiers.add([namespace    : idc.namespace.value,
                                        value        : idc.value,
                                        namespaceName: idc.namespace.name])
            }

            // prices
            result.prices = []
            object.prices?.each { p ->
                def price = [:]
                price.type = p.priceType?.value ?: ""
                price.amount = String.valueOf(p.price) ?: ""
                price.currency = p.currency?.value ?: ""
                if (p.startDate){
                    price.startDate = dateFormatService.formatIsoTimestamp(p.startDate)
                }
                if (p.endDate){
                    price.endDate = dateFormatService.formatIsoTimestamp(p.endDate)
                }
                result.prices.add(price)
            }

            result.ddcs = []
            object.ddcs.each { ddc ->
                result.ddcs.add([value     : ddc.value,
                                 value_de  : ddc.value_de,
                                 value_en  : ddc.value_en])
            }

            result.languages = []
            object.languages.each { ComponentLanguage kbl ->
                result.languages.add([value     : kbl.language.value,
                                      value_de  : kbl.language.value_de,
                                      value_en  : kbl.language.value_en])
            }

            result.curatoryGroups = []
            object.pkg?.curatoryGroups?.each {
                result.curatoryGroups.add([name: it.curatoryGroup.name,
                                           type: it.curatoryGroup.type?.value,
                                           curatoryGroup: it.curatoryGroup.getOID()])
            }

            result
        }else if(object.class.name == DeletedKBComponent.class.name) {

            result.uuid = object.uuid
            result.name = object.name
            result.componentType = object.componentType
            result.status = object.status.value
            result.dateCreated = dateFormatService.formatIsoTimestamp(object.dateCreated)
            result.lastUpdated = dateFormatService.formatIsoTimestamp(object.lastUpdated)
            result.oldDateCreated = dateFormatService.formatIsoTimestamp(object.oldDateCreated)
            result.oldLastUpdated = dateFormatService.formatIsoTimestamp(object.oldLastUpdated)
            result.oldId = object.oldId
            result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)

            result
        }

        return result
    }
*/
    private LinkedHashMap<Object, Object> mapDomainFieldsToSpecFields(Object object) {
        LinkedHashMap<Object, Object> result = [:]

        if(object.class.name == Package.class.name) {

            result.uuid = object.uuid
            result.name = object.name
            result.sortname = generateSortName(object.name)
            result.status = object.status?.value
            result.componentType = object.class.simpleName

            result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)
            result.dateCreatedDisplay = dateFormatService.formatIsoTimestamp(object.dateCreated)

            if(object.provider){
                result.cpname = object.provider.name
                result.provider = object.provider.getOID()
                result.providerName = object.provider.name
                result.providerUuid = object.provider.uuid
            }else {
                result.cpname = ""
                result.provider = ""
                result.providerName = ""
                result.providerUuid = ""
            }

            if(object.nominalPlatform) {
                result.nominalPlatform = object.nominalPlatform.getOID()
                result.nominalPlatformName = object.nominalPlatform.name
                result.nominalPlatformUuid = object.nominalPlatform.uuid
            }else {
                result.nominalPlatform = ""
                result.nominalPlatformName = ""
                result.nominalPlatformUuid = ""
            }

            result.description = object.description
            result.descriptionURL = object.descriptionURL

           result.titleCount = object.getTippCount()
           result.currentTippCount = object.getCurrentTippCount()
           result.retiredTippCount = object.getRetiredTippCount()
           result.expectedTippCount = object.getExpectedTippCount()
           result.deletedTippCount = object.getDeletedTippCount()

           result.breakable = object.breakable ? object.breakable.value : ""
           result.consistent = object.consistent?.value
           result.contentType = object.contentType?.value
           result.freeTrial = object.freeTrial?.value
           result.scope = object.scope ? object.scope.value : ""
           result.paymentType = object.paymentType ? object.paymentType.value : ""
           result.openAccess = object.openAccess?.value
           result.file = object.file?.value

           result.freeTrialPhase = object.freeTrialPhase

           if (object.kbartSource) {
               result.source = [
                       id              : object.kbartSource.id,
                       uuid              : object.kbartSource.uuid,
                       name            : object.kbartSource.name,
                       automaticUpdates: object.kbartSource.automaticUpdates,
                       url             : object.kbartSource.url,
                       frequency       : object.kbartSource.frequency?.value,
               ]
               if (object.kbartSource.lastRun){
                   result.source.lastRun = dateFormatService.formatIsoTimestamp(object.kbartSource.lastRun)
               }
           }else {
               result.source = []
           }

           result.altname = []
           object.variantNames.each { vn ->
               result.altname.add(vn.variantName)
           }

            result.curatoryGroups = []
           if(object.hasProperty('curatoryGroups')) {
               object.curatoryGroups.each {
                   result.curatoryGroups.add([name: it.curatoryGroup.name,
                                              type: it.curatoryGroup.type?.value,
                                              curatoryGroup: it.curatoryGroup.getOID()])
               }
           }

           result.identifiers = []
           object.ids.each { idc ->
               result.identifiers.add([namespace    : idc.namespace.value,
                                       value        : idc.value,
                                       namespaceName: idc.namespace.name])
           }


           result.nationalRanges = []
           object.nationalRanges.each { nationalRange ->
               result.nationalRanges.add([value     : nationalRange.value,
                                          value_de  : nationalRange.value_de,
                                          value_en  : nationalRange.value_en])
           }

           result.regionalRanges = []
           object.regionalRanges.each { regionalRange ->
               result.regionalRanges.add([value     : regionalRange.value,
                                          value_de  : regionalRange.value_de,
                                          value_en  : regionalRange.value_en])
           }

           result.ddcs = []
           object.ddcs.each { ddc ->
               result.ddcs.add([value     : ddc.value,
                                value_de  : ddc.value_de,
                                value_en  : ddc.value_en])
           }

           result.packageArchivingAgencies = []
           object.paas.each { PackageArchivingAgency paa ->
               result.packageArchivingAgencies.add([archivingAgency: paa.archivingAgency?.value,
                                                    openAccess  : paa.openAccess?.value,
                                                    postCancellationAccess  : paa.postCancellationAccess?.value])
           }


            result
        }else if(object.class.name == Org.class.name) {

           result.uuid = object.uuid
            result.name = object.name
            result.sortname = generateSortName(object.name)
            result.abbreviatedName = object.abbreviatedName
            result.status = object.status?.value
            result.componentType = object.class.simpleName

            result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)
            result.dateCreatedDisplay = dateFormatService.formatIsoTimestamp(object.dateCreated)

            result.kbartDownloaderURL = object.kbartDownloaderURL
            result.metadataDownloaderURL = object.metadataDownloaderURL
            result.homepage = object.homepage

            result.roles = []
            object.roles.each { role ->
                result.roles.add(role.value)
            }

            result.altname = []

            object.variantNames.each { vn ->
                result.altname.add(vn.variantName)
            }

            if(object.hasProperty('curatoryGroups')) {
                result.curatoryGroups = []
                object.curatoryGroups?.each {
                    result.curatoryGroups.add([name: it.curatoryGroup.name,
                                               type: it.curatoryGroup.type?.value,
                                               curatoryGroup: it.curatoryGroup.getOID()])
                }
            }

            result.identifiers = []
            object.ids.each { idc ->
                result.identifiers.add([namespace    : idc.namespace.value,
                                        value        : idc.value,
                                        namespaceName: idc.namespace.name])
            }

            result.contacts = []
            object.contacts.each { Contact contact ->
                result.contacts.add([  content: contact.content,
                                       contentType: contact.contentType?.value,
                                       type: contact.type?.value,
                                       language: contact.language?.value])
            }

            result
        }else if(object.class.name == Platform.class.name) {

            result.uuid = object.uuid
            result.name = object.name
            result.sortname = generateSortName(object.name)
            result.status = object.status?.value
            result.componentType = object.class.simpleName

            result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)
            result.dateCreatedDisplay = dateFormatService.formatIsoTimestamp(object.dateCreated)

            result.cpname = object.provider?.name
            result.provider = object.provider ? object.provider.getOID() : ""
            result.providerName = object.provider ? object.provider.name : ""
            result.providerUuid = object.provider ? object.provider.uuid : ""

            result.primaryUrl = object.primaryUrl

            result.titleNamespace = object.titleNamespace?.value

            result.lastAuditDate = object.lastAuditDate ? dateFormatService.formatIsoTimestamp(object.lastAuditDate) : ""

            result.ipAuthentication = object.ipAuthentication?.value

            result.shibbolethAuthentication = object.shibbolethAuthentication?.value

            result.openAthens = object.openAthens?.value

            result.passwordAuthentication = object.passwordAuthentication?.value

            result.statisticsFormat = object.statisticsFormat?.value
            result.counterR3Supported = object.counterR3Supported?.value
            result.counterR4Supported = object.counterR4Supported?.value
            result.counterR5Supported = object.counterR5Supported?.value
            result.counterR4SushiApiSupported = object.counterR4SushiApiSupported?.value
            result.counterR5SushiApiSupported = object.counterR5SushiApiSupported?.value
            result.counterR4SushiServerUrl = object.counterR4SushiServerUrl
            result.counterR5SushiServerUrl = object.counterR5SushiServerUrl
            result.counterRegistryUrl = object.counterRegistryUrl
            result.counterCertified = object.counterCertified?.value
            result.statisticsAdminPortalUrl = object.statisticsAdminPortalUrl
            result.statisticsUpdate = object.statisticsUpdate?.value
            result.proxySupported = object.proxySupported?.value

            result.counterRegistryApiUuid = object.counterRegistryApiUuid

            if(object.hasProperty('curatoryGroups')) {
                result.curatoryGroups = []
                object.curatoryGroups?.each {
                    result.curatoryGroups.add([name: it.curatoryGroup.name,
                                               type: it.curatoryGroup.type?.value,
                                               curatoryGroup: it.curatoryGroup.getOID()])
                }
            }


            result.identifiers = []
            object.ids.each { idc ->
                result.identifiers.add([namespace    : idc.namespace.value,
                                        value        : idc.value,
                                        namespaceName: idc.namespace.name])
            }

            result.federations = []
            object.federations.each { PlatformFederation platformFederation ->
                result.federations.add([federation    : platformFederation.federation?.value])
            }

            result
        }else if(object.class.name == TitleInstancePackagePlatform.class.name) {
            //Long start = System.currentTimeMillis()
            result.uuid = object.uuid
            result.name = object.name
            result.sortname = generateSortName(object.name)
            result.status = object.status?.value
            result.componentType = object.class.simpleName

            result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)
            result.dateCreatedDisplay = dateFormatService.formatIsoTimestamp(object.dateCreated)

            if (object.pkg) {
                result.tippPackage = object.pkg.getOID()
                result.tippPackageName = object.pkg.name
                result.tippPackageUuid = object.pkg.uuid
            }

            if (object.hostPlatform) {
                result.hostPlatform = object.hostPlatform.getOID()
                result.hostPlatformName = object.hostPlatform.name
                result.hostPlatformUuid = object.hostPlatform.uuid
            }

            result.titleType = object.getTitleType() ?: 'Unknown'
            result.url = object.url

            result.dateFirstOnline = object.dateFirstOnline ? dateFormatService.formatIsoTimestamp(object.dateFirstOnline) : ""
            result.dateFirstInPrint = object.dateFirstInPrint ? dateFormatService.formatIsoTimestamp(object.dateFirstInPrint) : ""
            result.accessStartDate = object.accessStartDate ? dateFormatService.formatIsoTimestamp(object.accessStartDate) : ""
            result.accessEndDate = object.accessEndDate ? dateFormatService.formatIsoTimestamp(object.accessEndDate) : ""
            result.lastChangedExternal = object.lastChangedExternal ? dateFormatService.formatIsoTimestamp(object.lastChangedExternal) : ""

            result.medium = object.medium?.value
            result.publicationType = object.publicationType?.value
            result.openAccess = object.openAccess?.value
            result.accessType = object.accessType?.value
            result.publicationType = object.publicationType?.value


            result.publisherName = object.publisherName
            result.subjectArea = object.subjectArea
            result.series = object.series
            result.volumeNumber = object.volumeNumber
            result.editionStatement = object.editionStatement
            result.firstAuthor = object.firstAuthor
            result.firstEditor = object.firstEditor
            result.parentPublicationTitleId = object.parentPublicationTitleId
            result.precedingPublicationTitleId = object.precedingPublicationTitleId
            result.supersedingPublicationTitleId = object.supersedingPublicationTitleId
            result.note = object.note
            result.fromKbartImport = object.fromKbartImport


            if (object.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                result.coverage = []
                ArrayList<TIPPCoverageStatement> coverage_src = object.coverageStatements
                coverage_src.each { TIPPCoverageStatement tcs ->
                    def cst = [:]
                    if (tcs.startDate) cst.startDate = dateFormatService.formatIsoTimestamp(tcs.startDate)
                    cst.startVolume = tcs.startVolume ?: ""
                    cst.startIssue = tcs.startIssue ?: ""
                    if (tcs.endDate) cst.endDate = dateFormatService.formatIsoTimestamp(tcs.endDate)
                    cst.endVolume = tcs.endVolume ?: ""
                    cst.endIssue = tcs.endIssue ?: ""
                    cst.embargo = tcs.embargo ?: ""
                    cst.coverageNote = tcs.coverageNote ?: ""
                    cst.coverageDepth = tcs.coverageDepth ? tcs.coverageDepth.value : ""
                    result.coverage.add(cst)
                }
            }

            result.identifiers = Identifier.executeQuery('select new map(ns.value as namespace, id.value as value, ns.name as namespaceName) from Identifier id join id.namespace ns where id.tipp = :obj', [obj: object])

            // prices
            result.prices = TippPrice.executeQuery("select new map(p.priceType.value as type, p.price as amount, p.currency.value as currency, to_char(p.startDate, 'yyyy-mm-ddThh:ii:ssZ') as startDate, to_char(p.endDate, 'yyyy-mm-ddThh:ii:ss') as endDate) from TippPrice p where p.tipp = :obj", [obj: object])
            /*
            object.prices?.each { p ->
                def price = [:]
                price.type = p.priceType?.value ?: ""
                price.amount = String.valueOf(p.price) ?: ""
                price.currency = p.currency?.value ?: ""
                if (p.startDate){
                    price.startDate = dateFormatService.formatIsoTimestamp(p.startDate)
                }
                if (p.endDate){
                    price.endDate = dateFormatService.formatIsoTimestamp(p.endDate)
                }
                result.prices.add(price)
            }*/

            result.ddcs = RefdataValue.executeQuery("select new map(ddc.value as value, ddc.value_de as value_de, ddc.value_en as value_en) from TitleInstancePackagePlatform tipp join tipp.ddcs ddc where tipp = :obj", [obj: object])
            /*
            result.ddcs = []
            object.ddcs.each { ddc ->
                result.ddcs.add([value     : ddc.value,
                                 value_de  : ddc.value_de,
                                 value_en  : ddc.value_en])
            }
            */

            result.langugages = ComponentLanguage.executeQuery("select new map(lang.value as value, lang.value_de as value_de, lang.value_en as value_en) from ComponentLanguage cl join cl.language lang where cl.tipp = :obj", [obj: object])
            /*
            result.languages = []
            object.languages.each { ComponentLanguage kbl ->
                result.languages.add([value     : kbl.language.value,
                                      value_de  : kbl.language.value_de,
                                      value_en  : kbl.language.value_en])
            }
            */

            result.curatoryGroups = []
            object.pkg?.curatoryGroups?.each {
                result.curatoryGroups.add([name: it.curatoryGroup.name,
                                           type: it.curatoryGroup.type?.value,
                                           curatoryGroup: it.curatoryGroup.getOID()])
            }
            //log.debug("record finished after ${System.currentTimeMillis()-start} msecs")
            result
        }else if(object.class.name == DeletedKBComponent.class.name) {

            result.uuid = object.uuid
            result.name = object.name
            result.componentType = object.componentType
            result.status = object.status.value
            result.dateCreated = dateFormatService.formatIsoTimestamp(object.dateCreated)
            result.lastUpdated = dateFormatService.formatIsoTimestamp(object.lastUpdated)
            result.oldDateCreated = dateFormatService.formatIsoTimestamp(object.oldDateCreated)
            result.oldLastUpdated = dateFormatService.formatIsoTimestamp(object.oldLastUpdated)
            result.oldId = object.oldId
            result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(object.lastUpdated)

            result
        }

        return result
    }

    private String checkAndGlobalSearchComponentType(String typeString) {
        String result = null

        if(typeString) {
            switch ('wekb.' + typeString.toLowerCase()) {
                case Org.class.name.toLowerCase():
                    result = 'orgs'
                    break
                case Package.class.name.toLowerCase():
                    result = 'packages'
                    break
                case Platform.class.name.toLowerCase():
                    result = 'platforms'
                    break
                case TitleInstancePackagePlatform.class.name.toLowerCase():
                    result = 'tipps'
                    break
                case DeletedKBComponent.class.name.toLowerCase():
                    result = 'deletedKBComponents'
                    break
            }
        }

        result
    }


    Map search(Map result, GrailsParameterMap params){
        def start_time = System.currentTimeMillis()
        String globalSearchComponentType = checkAndGlobalSearchComponentType(params.componentType)

        if (globalSearchComponentType) {
            def searchResult = [:]

            Map apiSearchTemplate = getApiTemplate(globalSearchComponentType)
            if (apiSearchTemplate) {

                params.sort = params.sort ?: apiSearchTemplate.defaultSort
                params.order = params.order ?: apiSearchTemplate.defaultOrder

                searchResult.max = params.max ? Integer.parseInt(params.max) : 10
                searchResult.offset = params.offset ? Integer.parseInt(params.offset) : 0

                log.debug("Execute query");
                GrailsParameterMap cleaned_params = processCleanParameterMap(params)

                def target_class = grailsApplication.getArtefact("Domain", apiSearchTemplate.baseclass);
                //HQLBuilder.build(grailsApplication, apiSearchTemplate, cleaned_params, searchResult, target_class, genericOIDService, "rows")
                HQLBuilder.build(grailsApplication, apiSearchTemplate, cleaned_params, searchResult, target_class, genericOIDService)

                log.debug("Query complete");

                //Add information to result
                result.result_count_total = searchResult.reccount
                result.result_count = searchResult.recset.size()
                result.sort = searchResult.sort
                result.order = searchResult.order
                result.offset = searchResult.offset
                result.max = searchResult.max
                result.page_current = (searchResult.offset / searchResult.max) + 1
                result.page_total = (searchResult.reccount / searchResult.max).toInteger() + (searchResult.reccount % searchResult.max > 0 ? 1 : 0)

            } else {
                log.error("no template ${apiSearchTemplate}");
            }

            result.result = []
            log.debug("Create result..")

            for (def r : searchResult.recset) {
                //LinkedHashMap<Object, Object> resultMap = mapDomainFieldsToSpecFields2(apiSearchTemplate, r)
                LinkedHashMap<Object, Object> resultMap = mapDomainFieldsToSpecFields(r)

                if(params.sortFields){
                    resultMap = resultMap.sort {it.key}
                }

                result.result.add(resultMap)
            }

            def searchTime = System.currentTimeMillis() - start_time

            result.searchTime = searchTime + ' ms'
            log.debug("Search completed after ${searchTime}");

        }else if(params.uuid){
            result.result = []

            Object r

            r = Package.findByUuid(params.uuid)
            if (!r) {
                r = Platform.findByUuid(params.uuid)
            }
            if (!r) {
                r = Org.findByUuid(params.uuid)
            }
            if (!r) {
                r = TitleInstancePackagePlatform.findByUuid(params.uuid)
            }

            if (!r) {
                r = DeletedKBComponent.findByUuid(params.uuid)
            }

            if (r) {
                LinkedHashMap<Object, Object> resultMap = mapDomainFieldsToSpecFields(r)

                if (params.sortFields) {
                    resultMap = resultMap.sort { it.key }
                }

                result.result.add(resultMap)
            }

        } else {
            result.message = "This search is not allowed!"
            result.code = "error"
            result.result = []
        }

        result
    }


    private GrailsParameterMap processCleanParameterMap(GrailsParameterMap parameterMap){
        GrailsParameterMap cleaned_params = parameterMap.clone()

        cleaned_params.each {
            if(!(it.value && it.value != "")) {
                cleaned_params.remove(it.key)
            }
        }

        processStatus(cleaned_params, parameterMap)
        processSimpleFields(cleaned_params, parameterMap)
        processRefDataFields(cleaned_params, parameterMap)

        //println(cleaned_params)
        return cleaned_params

    }

    private void processStatus(GrailsParameterMap cleaned_params, GrailsParameterMap parameterMap) {
        if (!parameterMap.status){
            List<String> refdataValueOIDs = [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED, RDStore.KBC_STATUS_EXPECTED].collect {it.getOID()}
            cleaned_params.put('status', refdataValueOIDs)
            return
        }
        setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'status', 'status')
        return
    }

    private void processSimpleFields(GrailsParameterMap cleaned_params, GrailsParameterMap parameterMap) {
        if (parameterMap.uuid){
            cleaned_params.put('uuid', parameterMap.uuid)
        }

        if (parameterMap.name){
            cleaned_params.put('name', parameterMap.name)
        }

        if (parameterMap.curatoryGroup){
            cleaned_params.put('curatoryGroup', parameterMap.curatoryGroup)
        }

        if (parameterMap.curatoryGroupExact){
            cleaned_params.put('curatoryGroup', parameterMap.curatoryGroupExact)
        }

        if (parameterMap.identifier){
            if (parameterMap.identifier.contains(',')) {
                cleaned_params.put('identifierNamespace', parameterMap.identifier.split(',')[0])
                cleaned_params.put('identifier', parameterMap.identifier.split(',')[1])
            }else{
                cleaned_params.put('identifier', parameterMap.identifier)
            }
        }

        if (parameterMap.ids){
            if (parameterMap.ids.contains(',')) {
                cleaned_params.put('identifierNamespace', parameterMap.ids.split(',')[0])
                cleaned_params.put('identifier', parameterMap.ids.split(',')[1])
            }else{
                cleaned_params.put('identifier', parameterMap.ids)
            }
        }

        if (parameterMap.identifiers){
            if (parameterMap.identifiers.contains(',')) {
                cleaned_params.put('identifierNamespace', parameterMap.identifiers.split(',')[0])
                cleaned_params.put('identifier', parameterMap.identifiers.split(',')[1])
            }else{
                cleaned_params.put('identifier', parameterMap.identifiers)
            }
        }

        if (parameterMap.altname){
            cleaned_params.put('variantNames', parameterMap.altname)
        }

        if (parameterMap.providerUuid){
            cleaned_params.put('providerUuid', parameterMap.providerUuid)
        }

        if (parameterMap.nominalPlatformUuid){
            cleaned_params.put('platformUuid', parameterMap.nominalPlatformUuid)
        }

        if (parameterMap.tippPackageUuid){
            cleaned_params.put('packageUuid', parameterMap.tippPackageUuid)
        }

        if (parameterMap.hostPlatformUuid){
            cleaned_params.put('platformUuid', parameterMap.hostPlatformUuid)
        }

        return
    }

    private void setRefdataValueFromGrailsParameterMap(GrailsParameterMap cleaned_params, GrailsParameterMap parameterMap, String cleanedFieldName, String parameterMapFieldName){
        if (parameterMap."${parameterMapFieldName}".getClass().isArray() || parameterMap."${parameterMapFieldName}" instanceof List){
            List<String> refdataValueOIDs = []
            parameterMap."${parameterMapFieldName}".each {
                List refdataValues = RefdataValue.executeQuery("from RefdataValue where LOWER(value) = LOWER(:value)", [value: it])

                refdataValues.each {
                    refdataValueOIDs << it.getOID()
                }
            }

            cleaned_params.put(cleanedFieldName, refdataValueOIDs)
        }
        else if (parameterMap."${parameterMapFieldName}" instanceof String){
            List<RefdataValue> refdataValues = RefdataValue.executeQuery("from RefdataValue where LOWER(value) = LOWER(:value)", [value: parameterMap."${parameterMapFieldName}"])

            if(refdataValues){
                List<String> refdataValueOIDs = []
                refdataValues.each {
                    refdataValueOIDs << it.getOID()
                }
                cleaned_params.put(cleanedFieldName, refdataValueOIDs)
            }
        }
    }

    private void processRefDataFields(GrailsParameterMap cleaned_params, GrailsParameterMap parameterMap) {

        if(parameterMap.ddc) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'ddcs', 'ddc')
        }
        else if(parameterMap.ddcs) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'ddcs', 'ddcs')
        }
        if(parameterMap.language) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'languages', 'language')
        }
        else if(parameterMap.languages) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'languages', 'languages')
        }
        if(parameterMap.curatoryGroupType) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'type', 'curatoryGroupType')
        }

        if(parameterMap.role) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'roles', 'curatoryGroupType')
        }


        if(parameterMap.shibbolethAuthentication) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'shibbolethAuthentication', 'shibbolethAuthentication')
        }

        if(parameterMap.counterCertified) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'counterCertified', 'counterCertified')
        }

        if(parameterMap.ipAuthentication) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'ipAuthentication', 'ipAuthentication')
        }

    }

    Map sushiSources(GrailsParameterMap params, Map result){

        RefdataValue yes = RDStore.YN_YES

        Set counter4Platforms = []
        Set counter5Platforms = []

        result.counter4ApiSources = [:]
        result.counter5ApiSources = [:]


        if(params.uuid){
            counter4Platforms = Platform.executeQuery("from Platform plat where plat.counterR4SushiApiSupported = :r4support and plat.counterR5SushiApiSupported != :r5support and plat.counterR4SushiServerUrl is not null and plat.uuid = :uuid", [r4support: yes, r5support: yes, uuid: params.uuid]).toSet()
            counter5Platforms = Platform.executeQuery("from Platform plat where plat.counterR5SushiApiSupported = :r5support and plat.counterR5SushiServerUrl is not null and plat.uuid = :uuid", [r5support: yes, uuid: params.uuid]).toSet()
        }else {
            counter4Platforms = Platform.executeQuery("from Platform plat where plat.counterR4SushiApiSupported = :r4support and plat.counterR5SushiApiSupported != :r5support and plat.counterR4SushiServerUrl is not null", [r4support: yes, r5support: yes]).toSet()
            counter5Platforms = Platform.executeQuery("from Platform plat where plat.counterR5SushiApiSupported = :r5support and plat.counterR5SushiServerUrl is not null", [r5support: yes]).toSet()
        }

        counter4Platforms.each { Platform platform ->
            result.counter4ApiSources."${platform.uuid}" = mapDomainFieldsToSpecFields(platform)

            result.counter4ApiSources."${platform.uuid}".sushiApiAuthenticationMethod = platform.sushiApiAuthenticationMethod?.value
            result.counter4ApiSources."${platform.uuid}".centralApiKey = platform.centralApiKey

        }

        counter5Platforms.each { Platform platform ->
            result.counter5ApiSources."${platform.uuid}" = mapDomainFieldsToSpecFields(platform)

            result.counter5ApiSources."${platform.uuid}".sushiApiAuthenticationMethod = platform.sushiApiAuthenticationMethod?.value
            result.counter5ApiSources."${platform.uuid}".centralApiKey = platform.centralApiKey

        }

        result
    }
}

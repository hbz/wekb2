package wekb

import wekb.helper.RCConstants
import grails.gorm.transactions.Transactional

@Transactional
class GlobalSearchTemplatesService {

    private globalSearchTemplates = new java.util.HashMap<String, Map>()

    @javax.annotation.PostConstruct
    def init() {
        globalSearchTemplates.put('curatoryGroups', curatoryGroups())
        globalSearchTemplates.put('identifiers', identifiers())
        globalSearchTemplates.put('jobResults', jobResults())
        globalSearchTemplates.put('namespaces', namespaces())
        globalSearchTemplates.put('orgs', orgs())
        globalSearchTemplates.put('packages', packages())
        globalSearchTemplates.put('publicPackages', publicPackages())
        globalSearchTemplates.put('platforms', platforms())
        globalSearchTemplates.put('refdataCategories', refdataCategories())
        globalSearchTemplates.put('refdataValues', refdataValues())
        globalSearchTemplates.put('sources', sources())
        globalSearchTemplates.put('tipps', tipps())
        globalSearchTemplates.put('tippsOfPkg', tippsOfPkg())
        globalSearchTemplates.put('updatePackageInfos', updatePackageInfos())
        globalSearchTemplates.put('updateTippInfos', updateTippInfos())
        globalSearchTemplates.put('users', users())
        globalSearchTemplates.put('userJobs', userJobs())

    }

    public Map getGlobalSearchTemplate(String type) {
        return globalSearchTemplates.get(type);
    }

    public def findAllByBaseClass(String baseClass){
        def result = globalSearchTemplates.findAll {it.value.baseclass==baseClass}
        result
    }

    Map curatoryGroups() {
        Map result = [
                baseclass: 'wekb.CuratoryGroup',
                title    : 'Curatory Groups',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name of Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.CURATORY_GROUP_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Type', property: 'type?.value', sort: 'type']

                        ]
                ]
        ]
        result
    }

    Map identifiers() {
        Map result = [
                baseclass: 'wekb.Identifier',
                title    : 'Identifiers',
                defaultSort : 'value',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Namespace',
                                        qparam     : 'qp_namespace_value',
                                        placeholder: 'Namespace',
                                        filter1    : 'all',
                                        type       : 'lookup',
                                        baseClass  : 'wekb.IdentifierNamespace',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'namespace'],
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'value'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Namespace', property: 'namespace.value', sort: 'namespace.value'],
                                [heading: 'Value', property: 'value', link: true, sort: 'value'],
                                [heading: 'Component', property: 'reference'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                        ]
                ]
        ]
        result
    }

    Map jobResults() {
        Map result = [
                baseclass   : 'wekb.system.JobResult',
                title       : 'Job Results',
                defaultSort : 'id',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.JOB_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type of Job',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true],
                                [heading: 'Component', property: 'linkedItem'],
                                [heading: 'Type', property: 'type?.value', sort: 'type'],
                                [heading: 'Status', property: 'statusText'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                                [heading: 'Curatory Group', property: 'curatoryGroup'],
                        ]
                ]
        ]
        result
    }

    Map namespaces() {
        Map result = [
                baseclass: 'wekb.IdentifierNamespace',
                title    : 'Identifier Namespaces',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Namespace',
                                        qparam     : 'qp_value',
                                        placeholder: 'value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'value', 'wildcard': 'B']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE,
                                        prompt     : 'Target Type',
                                        qparam     : 'qp_targetType',
                                        placeholder: 'Target Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'targetType']
                                ],
                                [
                                        prompt     : 'Category',
                                        qparam     : 'qp_family',
                                        placeholder: 'Category',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'family', 'wildcard': 'B']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name'],
                                [heading: 'Value', property: 'value', link: true, sort: 'value'],
                                [heading: 'Category', property: 'family', sort: 'family'],
                                [heading: 'Target Type', property: 'targetType.value', sort: 'targetType.value'],
                                [heading: 'Count', property: 'identifiersCount', sort: 'identifiersCount']
                        ]
                ]
        ]
        result
    }

    Map orgs() {
        Map result = [
                baseclass   : 'wekb.Org',
                title       : 'Providers',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                //Hidden Fields
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'curatoryGroups.curatoryGroup'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'R']
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.ORG_ROLE,
                                        prompt     : 'Role',
                                        qparam     : 'qp_roles',
                                        placeholder: 'Role',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'roles'],
                                ],

                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true],
                                [heading: 'Homepage', property: 'homepage', sort: 'homepage', outGoingLink: true],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', sort: 'status', property: 'status.value'],
                                [heading: 'Misson', sort: 'misson', property: 'misson.value'],
                                [heading: 'Current Titles', property: 'currentTippCount']

                        ]
                ]
        ]

        result
    }

    Map packages() {
        Map result = [
                baseclass   : 'wekb.Package',
                title       : 'Packages',
                defaultSort : 'lastUpdated',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [

                                //HIDE FIELDS
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Platform ID',
                                        qparam     : 'qp_platform_id',
                                        placeholder: 'Platform ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Source ID',
                                        qparam     : 'qp_source_id',
                                        placeholder: 'Source ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'kbartSource.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Name of Package',
                                        qparam     : 'qp_name',
                                        placeholder: 'Package Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B', normalise: false]
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],

                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.Org',
                                        prompt     : 'Provider',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],

                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup'],

                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.Platform',
                                        prompt     : 'Platform',
                                        qparam     : 'qp_platform',
                                        placeholder: 'Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform'],

                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_SCOPE,
                                        prompt     : 'Scope',
                                        qparam     : 'qp_scope',
                                        placeholder: 'Scope',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'scope'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_CONTENT_TYPE,
                                        prompt     : 'Content Type',
                                        qparam     : 'qp_content',
                                        placeholder: 'Content Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'contentType'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        qparam     : 'qp_oa',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'ddcs'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PAA_ARCHIVING_AGENCY,
                                        prompt     : 'Package Archiving Agency',
                                        qparam     : 'qp_archivingAgency',
                                        placeholder: 'Package Archiving Agency',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paas.archivingAgency']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Source Automatic Updates',
                                        qparam     : 'qp_source_automaticUpdates',
                                        placeholder: 'Source Automatic Updates',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'kbartSource.automaticUpdates'],
                                ],

                                //Package Filter
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_BREAKABLE,
                                        prompt     : 'Breakable Type',
                                        qparam     : 'qp_breakable',
                                        placeholder: 'Breakable Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'breakable'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_CONSISTENT,
                                        prompt     : 'Consistent Type',
                                        qparam     : 'qp_consistent',
                                        placeholder: 'Consistent Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'consistent'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_PAYMENT_TYPE,
                                        prompt     : 'Paid',
                                        qparam     : 'qp_paymentType',
                                        placeholder: 'Paid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paymentType'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_FILE,
                                        prompt     : 'File',
                                        qparam     : 'qp_file',
                                        placeholder: 'File',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'file'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Free Trial',
                                        qparam     : 'qp_freeTrial',
                                        placeholder: 'Free Trial',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'freeTrial'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],


                                //FOR My Components Area
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'curatoryGroups.curatoryGroup'],
                                        hide       : true
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Package', property: 'name', sort: 'name', link: true],
                                [heading: 'Provider', property: 'provider?.name', sort: 'provider?.name', link: true],
                                [heading: 'Nominal Platform', property: 'nominalPlatform?.name', sort: 'nominalPlatform?.name', link: true],
                                [heading: 'Content Type', property: 'contentType?.value', sort: 'contentType'],
                                [heading: 'Scope', property: 'scope', sort: 'scope'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Current Titles', property: 'currentTippCount', sort: 'currentTippCount'],
                                [heading: 'Retired Titles', property: 'retiredTippCount', sort: 'retiredTippCount'],
                                [heading: 'Expected Titles', property: 'expectedTippCount', sort: 'expectedTippCount'],
                                [heading: 'Deleted Titles', property: 'deletedTippCount', sort: 'deletedTippCount'],
                                [heading: 'Product IDs', property: 'anbieterProduktIDs'],
                                [heading: 'Source', property: 'kbartSource?.name', link: true, sort: 'kbartSource.name'],
                                [heading: 'Automatic Updates', property: 'kbartSource?.automaticUpdates', link: true]
                        ],
                        actions   : [
                        ]
                ]
        ]

        result
    }

    Map publicPackages() {
        Map result = [
                baseclass   : 'wekb.Package',
                title       : 'Packages',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Platform ID',
                                        qparam     : 'qp_platform_id',
                                        placeholder: 'Platform ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Source ID',
                                        qparam     : 'qp_source_id',
                                        placeholder: 'Source ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'kbartSource.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Name of Package',
                                        qparam     : 'qp_name',
                                        placeholder: 'Package Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B', normalise: false]
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],

                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup'],

                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.Org',
                                        prompt     : 'Provider',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],

                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Source Automatic Updates',
                                        qparam     : 'qp_source_automaticUpdates',
                                        placeholder: 'Source Automatic Updates',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'kbartSource.automaticUpdates'],
                                ],
                                //Package Filter
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_CONTENT_TYPE,
                                        prompt     : 'Content Type',
                                        qparam     : 'qp_contentType',
                                        placeholder: 'Content Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'contentType'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'ddcs'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_PAYMENT_TYPE,
                                        prompt     : 'Paid',
                                        qparam     : 'qp_paymentType',
                                        placeholder: 'Paid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paymentType'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        qparam     : 'qp_oa',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PAA_ARCHIVING_AGENCY,
                                        prompt     : 'Package Archiving Agency',
                                        qparam     : 'qp_archivingAgency',
                                        placeholder: 'Package Archiving Agency',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paas.archivingAgency'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                //Title Filter
                                [
                                        prompt     : 'Title',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.name'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_tippIdentifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.ids.value'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status_tipp',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.status'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Publication Type',
                                        qparam     : 'qp_publicationType_tipp',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.publicationType'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_MEDIUM,
                                        prompt     : 'Medium',
                                        qparam     : 'qp_medium_tipp',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.medium'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_ACCESS_TYPE,
                                        prompt     : 'Access Type',
                                        qparam     : 'qp_accessType_tipp',
                                        placeholder: 'Access Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.accessType'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'publisher',
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_publisherName',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.publisherName'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Author',
                                        qparam     : 'qp_firstAuthor_tipp',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.firstAuthor'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Editor',
                                        qparam     : 'qp_firstEditor_tipp',
                                        placeholder: 'Editor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.firstEditor'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],

                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'subjectArea',
                                        prompt     : 'Subject Area',
                                        qparam     : 'qp_subjectArea_tipp',
                                        placeholder: 'Subject Area',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.subjectArea'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc_tipp',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'tipps.ddcs'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_LANGUAGE,
                                        prompt     : 'Language',
                                        qparam     : 'qp_language_tipp',
                                        placeholder: 'Language',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.languages.language'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                //Platform Filter
                                [
                                        prompt     : 'Name of Platform',
                                        qparam     : 'qp_name_platform',
                                        placeholder: 'Name of Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'nominalPlatform.name'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Shibboleth Supported',
                                        qparam     : 'qp_shibbolethAuthentication_platform',
                                        placeholder: 'Shibboleth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.shibbolethAuthentication'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Open Athens Supported',
                                        qparam     : 'qp_openAthens_platform',
                                        placeholder: 'Open Athens Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.openAthens'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_IP_AUTH,
                                        prompt     : 'IP Auth Supported',
                                        qparam     : 'qp_ipAuthentication_platform',
                                        placeholder: 'IP Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.ipAuthentication'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_STATISTICS_FORMAT,
                                        prompt     : 'Statistics Format',
                                        qparam     : 'qp_statisticsFormat_platform',
                                        placeholder: 'Statistics Format',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.statisticsFormat'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R3 Supported',
                                        qparam     : 'qp_counterR3Supported_platform',
                                        placeholder: 'Counter R3 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR3Supported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Supported',
                                        qparam     : 'qp_counterR4Supported_platform',
                                        placeholder: 'Counter R4 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR4Supported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Supported',
                                        qparam     : 'qp_counterR5Supported_platform',
                                        placeholder: 'Counter R5 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR5Supported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Sushi Api Supported',
                                        qparam     : 'qp_counterR4SushiApiSupported_platform',
                                        placeholder: 'Counter R4 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR4SushiApiSupported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Sushi Api Supported',
                                        qparam     : 'qp_counterR5SushiApiSupported_platform',
                                        placeholder: 'Counter R5 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR5SushiApiSupported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],

                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true],
                                [heading: 'Provider', property: 'provider?.name', sort: 'provider?.name', link: true],
                                [heading: 'Nominal Platform', property: 'nominalPlatform?.name', sort: 'nominalPlatform?.name', link: true],
                                [heading: 'Curatory Groups', property: 'curatoryGroupsCuratoryGroup', link: true],
                                [heading: 'Content Type', property: 'contentType?.value', sort: 'contentType'],
                                [heading: 'Product IDs', property: 'anbieterProduktIDs'],
                                [heading: 'Current Titles', property: 'currentTippCount', sort: 'currentTippCount'],
                                [heading: 'Retired Titles', property: 'retiredTippCount', sort: 'retiredTippCount'],
                                [heading: 'Expected Titles', property: 'expectedTippCount', sort: 'expectedTippCount'],
                                [heading: 'Deleted Titles', property: 'deletedTippCount', sort: 'deletedTippCount'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Automatic Updates', property: 'kbartSource?.automaticUpdates', link: true]
                        ],
                        actions   : [
                        ]
                ]
        ]

        result
    }

    Map platforms() {
        Map result = [
                baseclass   : 'wekb.Platform',
                title       : 'Platforms',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                //Hidden Fields
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'curatoryGroups.curatoryGroup'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.Org',
                                        prompt     : 'Provider',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        prompt     : 'Platform URL',
                                        qparam     : 'qp_url',
                                        placeholder: 'Platform URL',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'primaryUrl']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Shibboleth Supported',
                                        qparam     : 'qp_shibbolethAuthentication',
                                        placeholder: 'Shibboleth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'shibbolethAuthentication'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Open Athens Supported',
                                        qparam     : 'qp_openAthens',
                                        placeholder: 'Open Athens Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAthens']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_IP_AUTH,
                                        prompt     : 'IP Auth Supported',
                                        qparam     : 'qp_ipAuthentication',
                                        placeholder: 'IP Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ipAuthentication'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_STATISTICS_FORMAT,
                                        prompt     : 'Statistics Format',
                                        qparam     : 'qp_statisticsFormat',
                                        placeholder: 'Statistics Format',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'statisticsFormat'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R3 Supported',
                                        qparam     : 'qp_counterR3Supported',
                                        placeholder: 'Counter R3 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR3Supported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Supported',
                                        qparam     : 'qp_counterR4Supported',
                                        placeholder: 'Counter R4 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR4Supported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Supported',
                                        qparam     : 'qp_counterR5Supported',
                                        placeholder: 'Counter R5 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR5Supported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Sushi Api Supported',
                                        qparam     : 'qp_counterR4SushiApiSupported',
                                        placeholder: 'Counter R4 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR4SushiApiSupported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Sushi Api Supported',
                                        qparam     : 'qp_counterR5SushiApiSupported',
                                        placeholder: 'Counter R5 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR5SushiApiSupported'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true],
                                [heading: 'Primary URL', property: 'primaryUrl', sort: 'primaryUrl', outGoingLink: true],
                                [heading: 'Provider', property: 'provider?.name', sort: 'provider?.name', link: true],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Current Titles', property: 'currentTippCount'],
                                [heading: 'Current Packages', property: 'packagesCount'],
                        ]
                ]
        ]
        result
    }

    Map refdataCategories() {
        Map result = [
                baseclass: 'wekb.RefdataCategory',
                title    : 'Refdata Categories ',
                defaultSort : 'desc',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Description',
                                        qparam     : 'qp_desc',
                                        placeholder: 'Category Description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc']
                                ],

                                [
                                        prompt     : 'Description EN',
                                        qparam     : 'qp_desc_en',
                                        placeholder: 'Category Description En',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc_en']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', sort: 'desc', property: 'desc', link: true],
                                [heading: 'Description EN', sort: 'desc_en', property: 'desc_en'],
                                [heading: 'Description DE', sort: 'desc_de', property: 'desc_de'],
                                [heading: 'Hard Data', sort: 'isHardData', property: 'isHardData'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Refdata Values', sort: 'valuesCount', property: 'valuesCount'],
                        ]
                ]
        ]

        result
    }

    Map refdataValues() {
        Map result = [
                baseclass: 'wekb.RefdataValue',
                title    : 'Refdata Values ',
                defaultSort : 'value',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Description',
                                        qparam     : 'qp_desc',
                                        placeholder: 'Description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc']
                                ],

                                [
                                        prompt     : 'Value',
                                        qparam     : 'qp_value',
                                        placeholder: 'Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'value']
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataCategory',
                                        prompt     : 'Refdata Category',
                                        qparam     : 'qp_owner',
                                        placeholder: 'Refdata Category',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'owner']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Value', sort: 'value', property: 'value', link: true],
                                [heading: 'Value EN', sort: 'value_en', property: 'value_en'],
                                [heading: 'Value DE', sort: 'value_de', property: 'value_de'],
                                [heading: 'Hard Data', sort: 'isHardData', property: 'isHardData'],
                                [heading: 'Description', sort: 'desc', property: 'desc'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Refdata Category', sort: 'owner', property: 'owner.desc'],
                        ]
                ]
        ]

        result
    }

    Map sources() {
        Map result = [
                baseclass: 'wekb.KbartSource',
                title    : 'Source',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                //Hidden Fields
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'curatoryGroups.curatoryGroup'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup'],

                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Automatic Updates',
                                        qparam     : 'qp_automaticUpdates',
                                        placeholder: 'Automatic Updates',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'automaticUpdates'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.SOURCE_FREQUENCY,
                                        prompt     : 'Frequency',
                                        qparam     : 'qp_frequency',
                                        placeholder: 'Frequencys',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'frequency'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true],
                                [heading: 'Package', property: 'pkg', link: true, sort: 'pkg.name'],
                                [heading: 'Url', property: 'url', sort: 'url', outGoingLink: true],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'automatic Updates', property: 'automaticUpdates'],
                                [heading: 'Frequency', property: 'frequency?.value', sort: 'frequency'],
                                [heading: 'Last Run', property: 'lastRun', sort: 'lastRun'],
                                [heading: 'Next Run', property: 'nextUpdateTimestamp']
                        ]
                ]
        ]
        result
    }

    Map tipps() {
        Map result = [
                baseclass: 'wekb.TitleInstancePackagePlatform',
                title    : 'Titles',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                //Hidden Fields
                                [
                                        prompt     : 'Status ID',
                                        qparam     : 'qp_status_id',
                                        placeholder: 'Status ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Title ID',
                                        qparam     : 'qp_title_id',
                                        placeholder: 'Title ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'title.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Package ID',
                                        qparam     : 'qp_pkg_id',
                                        placeholder: 'Package ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Platform ID',
                                        qparam     : 'qp_plat_id',
                                        placeholder: 'Platform ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'pkg.curatoryGroups.curatoryGroup'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Title',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name'],
                                ],

                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value']
                                ],
                                [
                                        prompt     : 'Url',
                                        qparam     : 'qp_url',
                                        placeholder: 'Url',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'url'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.Org',
                                        prompt     : 'Provider',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider'],

                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.Package',
                                        prompt     : 'Package',
                                        qparam     : 'qp_pkg',
                                        placeholder: 'Package',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.Platform',
                                        prompt     : 'Platform',
                                        qparam     : 'qp_plat',
                                        placeholder: 'Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.curatoryGroups.curatoryGroup'],

                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'publisher',
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_publisherName',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publisherName'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Publication Type',
                                        qparam     : 'qp_publicationType',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publicationType'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_MEDIUM,
                                        prompt     : 'Medium',
                                        qparam     : 'qp_medium',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'medium'],
                                ],
                                [
                                        prompt     : 'Author',
                                        qparam     : 'qp_firstAuthor',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstAuthor'],
                                ],
                                [
                                        prompt     : 'Editor',
                                        qparam     : 'qp_firstEditor',
                                        placeholder: 'Editor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstEditor'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_ACCESS_TYPE,
                                        prompt     : 'Access Type',
                                        qparam     : 'qp_accessType',
                                        placeholder: 'Access Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessType'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'subjectArea',
                                        prompt     : 'Subject Area',
                                        qparam     : 'qp_subjectArea_tipp',
                                        placeholder: 'Subject Area',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'subjectArea'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'ddcs'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        qparam     : 'qp_openAccess',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_LANGUAGE,
                                        prompt     : 'Language',
                                        qparam     : 'qp_language',
                                        placeholder: 'Language',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'languages.language'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ]
                        ],
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: true, sort: 'name'],
                                [heading: 'Type', property: 'publicationType?.value', sort: 'publicationType.value'],
                                [heading: 'Medium', property: 'medium?.value', sort: 'medium.value'],
                                [heading: 'First Author', property: 'firstAuthor', sort: 'firstAuthor'],
                                [heading: 'Package', qpEquiv: 'qp_pkg_id', property: 'pkg.name', sort: 'pkg.name', link: true],
                                [heading: 'Platform', qpEquiv: 'qp_plat_id', property: 'hostPlatform.name', sort: 'hostPlatform.name', link: true],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status.value'],
                                [heading: 'URL', property: 'url', sort: 'url', outGoingLink: true]
                        ]
                ]
        ]

        result
    }

    Map tippsOfPkg() {
        Map result = [
                baseclass: 'wekb.TitleInstancePackagePlatform',
                title    : 'Titles',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Status ID',
                                        qparam     : 'qp_status_id',
                                        placeholder: 'Status ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Title ID',
                                        qparam     : 'qp_title_id',
                                        placeholder: 'Title ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'title.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Package ID',
                                        qparam     : 'qp_pkg_id',
                                        placeholder: 'Package ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Platform ID',
                                        qparam     : 'qp_plat_id',
                                        placeholder: 'Platform ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Title',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'publisher',
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_publisherName',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publisherName'],
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],

                                ],
                                [
                                        prompt     : 'Url',
                                        qparam     : 'qp_url',
                                        placeholder: 'Url',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'url'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Publication Type',
                                        qparam     : 'qp_publicationType',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publicationType'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_MEDIUM,
                                        prompt     : 'Medium',
                                        qparam     : 'qp_medium',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'medium'],
                                ],
                                [
                                        prompt     : 'Author',
                                        qparam     : 'qp_firstAuthor',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstAuthor'],
                                ],
                                [
                                        prompt     : 'Editor',
                                        qparam     : 'qp_firstEditor',
                                        placeholder: 'Editor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstEditor'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_ACCESS_TYPE,
                                        prompt     : 'Access Type',
                                        qparam     : 'qp_accessType',
                                        placeholder: 'Access Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessType'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'subjectArea',
                                        prompt     : 'Subject Area',
                                        qparam     : 'qp_subjectArea_tipp',
                                        placeholder: 'Subject Area',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'subjectArea'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'ddcs'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        qparam     : 'qp_openAccess',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_LANGUAGE,
                                        prompt     : 'Language',
                                        qparam     : 'qp_language',
                                        placeholder: 'Language',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'languages.language'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                //FOR My Components Area
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'pkg.curatoryGroups.curatoryGroup'],
                                        hide       : true
                                ],

                        ],
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: true],
                                [heading: 'Type', property: 'publicationType?.value', sort: 'publicationType.value'],
                                [heading: 'Medium', property: 'medium?.value', sort: 'medium.value'],
                                [heading: 'First Author', property: 'firstAuthor', sort: 'firstAuthor'],
                                [heading: 'Platform', qpEquiv: 'qp_plat_id', property: 'hostPlatform.name', sort: 'hostPlatform.name',  link: true],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status.value'],
                                [heading: 'URL', property: 'url', sort: 'url', outGoingLink: true]
                        ]
                ]
        ]

        result
    }

    Map users() {
        Map result = [
                baseclass   : 'wekb.auth.User',
                title       : 'Users',
                defaultSort : 'username',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Username',
                                        qparam     : 'qp_name',
                                        placeholder: 'Username',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'username']
                                ],
                                [
                                        prompt     : 'Email',
                                        qparam     : 'qp_email',
                                        placeholder: 'Email',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'email']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Enabled',
                                        qparam     : 'qp_enabled',
                                        placeholder: 'Enabled',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'enabled'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Account Expired',
                                        qparam     : 'qp_accountExpired',
                                        placeholder: 'Account Expired',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accountExpired'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Account Locked',
                                        qparam     : 'qp_accountLocked',
                                        placeholder: 'Account Locked',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accountLocked'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Password Expired',
                                        qparam     : 'qp_passwordExpired',
                                        placeholder: 'Password Expired',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'passwordExpired'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Username', property: 'username', link: true, sort: 'username'],
                                [heading: 'Enabled', property: 'enabled', sort: 'enabled'],
                                [heading: 'User', property: 'userStatus'],
                                [heading: 'Editor', property: 'editorStatus'],
                                [heading: 'API-User', property: 'apiUserStatus'],
                                [heading: 'Admin', property: 'adminStatus'],
                                [heading: 'Super-User', property: 'superUserStatus'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated']
                        ]
                ]
        ]
        result
    }

    Map userJobs() {
        Map result = [
                baseclass   : 'wekb.system.JobResult',
                title       : 'User Jobs',
                defaultSort : 'id',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.JOB_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type of Job',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true],
                                [heading: 'Component', property: 'linkedItem'],
                                [heading: 'Type', property: 'type?.value', sort: 'type'],
                                [heading: 'Status', property: 'statusText'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                                [heading: 'Curatory Group', property: 'curatoryGroup'],
                        ]
                ]
        ]
        result
    }

    Map updatePackageInfos() {
        Map result = [
                baseclass: 'wekb.UpdatePackageInfo',
                title    : 'Package Update Infos',
                defaultSort : 'startTime',
                defaultOrder: 'desc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Package ID',
                                        qparam     : 'qp_pkg_id',
                                        placeholder: 'Package ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'pkg.curatoryGroups.curatoryGroup'],
                                        hide       : true
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.Package',
                                        prompt     : 'Package',
                                        qparam     : 'qp_pkg',
                                        placeholder: 'Package',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UPDATE_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Automatic Update',
                                        qparam     : 'qp_automaticUpdate',
                                        placeholder: 'Automatic Update',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'automaticUpdate'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true],
                                [heading: 'Package', property: 'pkg.name', link: true],
                                [heading: 'Status', property: 'status', sort: 'status.value'],
                                [heading: 'Automatic Update', property: 'automaticUpdate', sort: 'automaticUpdate'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                                [heading: 'Only Last Changed Update', property: 'onlyRowsWithLastChanged', sort: 'onlyRowsWithLastChanged'],
                                [heading: 'Titles in we:kb before update', property: 'countPreviouslyTippsInWekb', sort: 'countPreviouslyTippsInWekb'],
                                [heading: 'Titles in we:kb after update', property: 'countNowTippsInWekb', sort: 'countNowTippsInWekb'],
                                [heading: 'Rows in KBART-File', property: 'countKbartRows', sort: 'countKbartRows'],
                                [heading: 'Processed KBART Rows', property: 'countProcessedKbartRows', sort: 'countProcessedKbartRows'],
                                [heading: 'Changed Titles ', property: 'countChangedTipps', sort: 'countChangedTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=Changed Title'],
                                [heading: 'Removed Titles ', property: 'countRemovedTipps', sort: 'countRemovedTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=Removed Title'],
                                [heading: 'New Titles', property: 'countNewTipps', sort: 'countNewTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=New Title'],
                                [heading: 'Invalid Titles', property: 'countInValidTipps', sort: 'countInValidTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=Failed Title'],

                        ]
                ]
        ]
        result
    }

    Map updateTippInfos() {
        Map result = [
                baseclass: 'wekb.UpdateTippInfo',
                title    : 'Title Update Infos',
                defaultSort : 'startTime',
                defaultOrder: 'desc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        qparam     : 'qp_aup_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'updatePackageInfo.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_tipp_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipp.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'KBART Field',
                                        qparam     : 'qp_kbartProperty',
                                        placeholder: 'KBART Field',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'kbartProperty']
                                ],
                                [
                                        prompt     : 'Old Value',
                                        qparam     : 'qp_oldValue',
                                        placeholder: 'Old Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'oldValue']
                                ],
                                [
                                        prompt     : 'New Value',
                                        qparam     : 'qp_newValue',
                                        placeholder: 'New Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'newValue']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UPDATE_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UPDATE_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                ],
                                [
                                        qparam     : 'qp_type_value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type.value'],
                                        hide: true
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true],
                                [heading: 'Title', property: 'tipp.name', link: true],
                                [heading: 'Status', property: 'status', sort: 'status.value'],
                                [heading: 'Type', property: 'type', sort: 'type.value'],
                                [heading: 'KBART Field', property: 'kbartProperty', sort: 'kbartProperty'],
                                [heading: 'New Value', property: 'newValue'],
                                [heading: 'Old Value', property: 'oldValue'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                        ]
                ]
        ]
        result
    }
}

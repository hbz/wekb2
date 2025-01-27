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
        globalSearchTemplates.put('refdataCategoriesPublic', refdataCategoriesPublic())
        globalSearchTemplates.put('refdataValues', refdataValues())
        globalSearchTemplates.put('sources', sources())
        globalSearchTemplates.put('tipps', tipps())
        globalSearchTemplates.put('tippsOfPkg', tippsOfPkg())
        globalSearchTemplates.put('updatePackageInfos', updatePackageInfos())
        globalSearchTemplates.put('updateTippInfos', updateTippInfos())
        globalSearchTemplates.put('users', users())
        globalSearchTemplates.put('userJobs', userJobs())
        globalSearchTemplates.put('vendors', vendors())

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
                msgCode    : 'curatorygroup.plural',
                title    : 'Curatory Groups',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name',
                                        msgCode    : 'default.name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name of Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.CURATORY_GROUP_TYPE,
                                        prompt     : 'Type',
                                        msgCode     : 'default.type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Curatory Group', property: 'name', sort: 'name', link: true, linkInfo: 'Link to Curatory Group'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status.value', sort: 'status'],
                                [heading: 'Type', property: 'type.value', sort: 'type']

                        ]
                ]
        ]
        result
    }

    Map identifiers() {
        Map result = [
                baseclass: 'wekb.Identifier',
                msgCode    : 'identifier.plural',
                title    : 'Identifiers',
                defaultSort : 'value',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Namespace',
                                        msgCode     : 'identifier.namespace',
                                        qparam     : 'qp_namespace_value',
                                        placeholder: 'Namespace',
                                        filter1    : 'all',
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.IdentifierNamespace',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'namespace'],
                                ],
                                [
                                        prompt     : 'Identifier',
                                        msgCode     : 'identifier.value',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'value'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Namespace', property: 'namespace.value', sort: 'namespace.value'],
                                [heading: 'Value', property: 'value', link: true, sort: 'value', linkInfo: 'Link to Identifier'],
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
                msgCode    : 'jobresult.plural',
                title       : 'Job Results',
                defaultSort : 'id',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.JOB_TYPE,
                                        prompt     : 'Type',
                                        msgCode     : 'default.type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type of Job',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true, linkInfo: 'Link to Job Result'],
                                [heading: 'Component', property: 'linkedItem'],
                                [heading: 'Type', property: 'type.value', sort: 'type'],
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
                msgCode    : 'identifiernamespace.plural',
                title    : 'Identifier Namespaces',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Namespace',
                                        msgCode     : 'identifier.namespace',
                                        qparam     : 'qp_value',
                                        placeholder: 'value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'value', 'wildcard': 'B']
                                ],
                                [
                                        type       : 'dropDown',
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
                                [heading: 'Value', property: 'value', link: true, sort: 'value', linkInfo: 'Link to Identifier Namespace'],
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
                msgCode    : 'org.plural',
                title       : 'Providers',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                //Hidden Fields
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        qparam     : 'qp_curgroups',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderForCuratoryGroups'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'createdSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'dateCreated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Vendor',
                                        qparam     : 'qp_vendor_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderForVendor'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Name',
                                        msgCode    : 'default.name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike_Combine_Name_And_VariantNames_And_AbbreviatedName_Org', 'prop': 'name', 'wildcard': 'B']
                                ],
                                [
                                        prompt     : 'Identifier',
                                        msgCode     : 'identifier.value',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        msgCode     : 'curatorygroup.label',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.ORG_ROLE,
                                        prompt     : 'Role',
                                        msgCode     : 'org.role',
                                        qparam     : 'qp_roles',
                                        placeholder: 'Role',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'roles'],
                                ],
                                //--------------------------------------------------------------------------------------------
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Inhouse Invoicing',
                                        msgCode     : 'org.invoicingYourself',
                                        qparam     : 'invoicingYourself',
                                        placeholder: 'Inhouse Invoicing',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'invoicingYourself'],
                                        advancedSearch: [title: "Invoicing", category: 'invoice']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.VENDOR_ELECTRONIC_BILLING,
                                        prompt     : 'Electronic Invoice Formats',
                                        msgCode     : 'org.electronicBillings',
                                        qparam     : 'electronicBillings',
                                        placeholder: 'Electronic Invoice Formats',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'electronicBillings'],
                                        advancedSearch: [title: "Invoicing", category: 'invoice']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.VENDOR_INVOICE_DISPATCH,
                                        prompt     : 'Invoice dispatch via',
                                        msgCode     : 'org.invoiceDispatchs',
                                        qparam     : 'invoiceDispatchs',
                                        placeholder: 'Invoice dispatch via',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'invoiceDispatchs'],
                                        advancedSearch: [title: "Invoicing", category: 'invoice']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Paper Invoice',
                                        msgCode     : 'org.paperInvoice',
                                        qparam     : 'paperInvoice',
                                        placeholder: 'Paper Invoice',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paperInvoice'],
                                        advancedSearch: [title: "Invoicing", category: 'invoice']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Management of Credits',
                                        msgCode     : 'org.managementOfCredits',
                                        qparam     : 'managementOfCredits',
                                        placeholder: 'Management of Credits',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'managementOfCredits'],
                                        advancedSearch: [title: "Invoicing", category: 'invoice']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Processing of compensation payments (credits/subsequent debits)',
                                        msgCode     : 'org.processingOfCompensationPayments',
                                        qparam     : 'processingOfCompensationPayments',
                                        placeholder: 'Processing of compensation payments (credits/subsequent debits)',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'processingOfCompensationPayments'],
                                        advancedSearch: [title: "Invoicing", category: 'invoice']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Individual invoice design',
                                        msgCode     : 'org.individualInvoiceDesign',
                                        qparam     : 'individualInvoiceDesign',
                                        placeholder: 'Individual invoice design',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'individualInvoiceDesign'],
                                        advancedSearch: [title: "Invoicing", category: 'invoice']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Vendor',
                                        prompt     : 'Invoicing possible by vendor',
                                        msgCode     : 'org.invoicingVendors',
                                        qparam     : 'invoicingVendors',
                                        placeholder: 'Invoicing possible by vendor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'invoicingVendors'],
                                        advancedSearch: [title: "Invoicing", category: 'invoice']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Collections',
                                        msgCode     : 'org.collections',
                                        qparam     : 'collections',
                                        placeholder: 'Collections',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'collections'],
                                        advancedSearch: [title: "Supported licencing models", category: 'supportedLicencingModels']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Pick and Choose',
                                        msgCode     : 'org.pickAndChoose',
                                        qparam     : 'pickAndChoose',
                                        placeholder: 'Pick and Choose',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pickAndChoose'],
                                        advancedSearch: [title: "Supported licencing models", category: 'supportedLicencingModels']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Prepaid',
                                        msgCode     : 'org.prepaid',
                                        qparam     : 'prepaid',
                                        placeholder: 'Prepaid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'prepaid'],
                                        advancedSearch: [title: "Supported licencing models", category: 'supportedLicencingModels']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Upfront',
                                        msgCode     : 'org.upfront',
                                        qparam     : 'upfront',
                                        placeholder: 'Upfront',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'upfront'],
                                        advancedSearch: [title: "Supported licencing models", category: 'supportedLicencingModels']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Temporary Access',
                                        msgCode     : 'org.temporaryAccess',
                                        qparam     : 'temporaryAccess',
                                        placeholder: 'Temporary Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'temporaryAccess'],
                                        advancedSearch: [title: "Supported licencing models", category: 'supportedLicencingModels']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Perpetual Access',
                                        msgCode     : 'org.perpetualAccess',
                                        qparam     : 'perpetualAccess',
                                        placeholder: 'Perpetual Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'perpetualAccess'],
                                        advancedSearch: [title: "Supported licencing models", category: 'supportedLicencingModels']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.ORG_DRM,
                                        prompt     : 'DRM',
                                        msgCode     : 'org.drm',
                                        qparam     : 'drm',
                                        placeholder: 'DRM',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'drm'],
                                        advancedSearch: [title: "Usage rights", category: 'usageRights']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Remote Access',
                                        msgCode     : 'org.remoteAccess',
                                        qparam     : 'remoteAccess',
                                        placeholder: 'Remote Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'remoteAccess'],
                                        advancedSearch: [title: "Usage rights", category: 'usageRights']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Print/Download Chapter',
                                        msgCode     : 'org.printDownloadChapter',
                                        qparam     : 'printDownloadChapter',
                                        placeholder: 'Print/Download Chapter',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'printDownloadChapter'],
                                        advancedSearch: [title: "Usage rights", category: 'usageRights']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Quotes By Copy/Paste',
                                        msgCode     : 'org.quotesByCopyPaste',
                                        qparam     : 'quotesByCopyPaste',
                                        placeholder: 'Quotes By Copy/Paste',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'quotesByCopyPaste'],
                                        advancedSearch: [title: "Usage rights", category: 'usageRights']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Forwarding Usage Statistcs',
                                        msgCode     : 'org.forwardingUsageStatistcs',
                                        qparam     : 'forwardingUsageStatistcs',
                                        placeholder: 'Forwarding Usage Statistcs',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'forwardingUsageStatistcs'],
                                        advancedSearch: [title: "General Services", category: 'generalServices']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Alerts about new publications within e-book packages',
                                        msgCode     : 'org.org_alert_new_ebook_packages',
                                        qparam     : 'org_alert_new_ebook_packages',
                                        placeholder: 'Alerts about new publications within e-book packages',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'org_alert_new_ebook_packages'],
                                        advancedSearch: [title: "General Services", category: 'generalServices']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Alerts about exchange of individual titles within e-book packages',
                                        msgCode     : 'org.org_alert_exchange_ebook_packages',
                                        qparam     : 'org_alert_exchange_ebook_packages',
                                        placeholder: 'Alerts about exchange of individual titles within e-book packages',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'org_alert_exchange_ebook_packages'],
                                        advancedSearch: [title: "General Services", category: 'generalServices']
                                ],



                        ],
                        qbeResults: [
                                [heading: 'Provider', property: 'name', sort: 'name', link: true, linkInfo: 'Link to Provider'],
                                [heading: 'Abbreviated Name', property: 'abbreviatedName', sort: 'abbreviatedName', link: true, linkInfo: 'Link to Provider'],
                                [heading: 'Homepage', property: 'homepage', sort: 'homepage', outGoingLink: true, linkInfo: 'Link to Homepage'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status.value', sort: 'status'],
                                [heading: 'Current Titles', property: 'currentTippCount', jumpToLink: '/search/componentSearch/wekb.Org:objectID?qbe=g:tipps&hide=qp_provider&hide=qp_provider_id&refOID=wekb.Org:objectID&qp_provider_id=objectID&qp_status_value=Current', linkInfo: 'Link to Current Titles'],
                                [heading: 'Packages', property: 'providedPackagesCount', jumpToLink: '/search/componentSearch/wekb.Org:objectID?qbe=g:packages&hide=qp_provider&hide=qp_provider_id&refOID=wekb.Org:objectID&qp_provider_id=objectID', linkInfo: 'Link to Packages'],
                                [heading: 'Platforms', property: 'providedPlatformsCount', jumpToLink: '/search/componentSearch/wekb.Org:objectID?qbe=g:platforms&hide=qp_provider&hide=qp_provider_id&refOID=wekb.Org:objectID&qp_provider_id=objectID', linkInfo: 'Link to Platforms']

                        ]
                ]
        ]

        result
    }

    Map packages() {
        Map result = [
                baseclass   : 'wekb.Package',
                msgCode    : 'package.plural',
                title       : 'Packages',
                defaultSort : 'lastUpdated',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [

                                //HIDE FIELDS
                                [
                                        qparam     : 'qp_provider_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_platform_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_source_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'kbartSource.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_vendor_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'vendors.vendor.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'createdSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'dateCreated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Name of Package',
                                        msgCode     : 'default.name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Package Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B', normalise: false]
                                ],
                                [
                                        prompt     : 'Identifier',
                                        msgCode     : 'identifier.value',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],

                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Org',
                                        prompt     : 'Provider',
                                        msgCode     : 'org.label',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],

                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        msgCode     : 'curatorygroup.label',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup'],

                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Platform',
                                        prompt     : 'Platform',
                                        msgCode     : 'platform.label',
                                        qparam     : 'qp_platform',
                                        placeholder: 'Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform'],

                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_SCOPE,
                                        prompt     : 'Scope',
                                        msgCode     : 'package.scope',
                                        qparam     : 'qp_scope',
                                        placeholder: 'Scope',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'scope'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_CONTENT_TYPE,
                                        prompt     : 'Content Type',
                                        msgCode     : 'package.contentType',
                                        qparam     : 'qp_content',
                                        placeholder: 'Content Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'contentType'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        msgCode     : 'package.openAccess',
                                        qparam     : 'qp_oa',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        msgCode     : 'package.ddcs',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'ddcs'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PAA_ARCHIVING_AGENCY,
                                        prompt     : 'Package Archiving Agency',
                                        msgCode     : 'package.archivingAgency',
                                        qparam     : 'qp_archivingAgency',
                                        placeholder: 'Package Archiving Agency',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paas.archivingAgency']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Source Automatic Updates',
                                        msgCode     : 'kbartSource.automaticUpdates',
                                        qparam     : 'qp_source_automaticUpdates',
                                        placeholder: 'Source Automatic Updates',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'kbartSource.automaticUpdates'],
                                ],

                                //Package Filter
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_BREAKABLE,
                                        prompt     : 'Breakable Type',
                                        msgCode     : 'package.breakable',
                                        qparam     : 'qp_breakable',
                                        placeholder: 'Breakable Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'breakable'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_CONSISTENT,
                                        prompt     : 'Consistent Type',
                                        msgCode     : 'package.consistent',
                                        qparam     : 'qp_consistent',
                                        placeholder: 'Consistent Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'consistent'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_PAYMENT_TYPE,
                                        prompt     : 'Paid',
                                        msgCode     : 'package.paymentType',
                                        qparam     : 'qp_paymentType',
                                        placeholder: 'Paid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paymentType'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_FILE,
                                        prompt     : 'File',
                                        msgCode     : 'package.file',
                                        qparam     : 'qp_file',
                                        placeholder: 'File',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'file'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Free Trial',
                                        msgCode     : 'package.freeTrial',
                                        qparam     : 'qp_freeTrial',
                                        placeholder: 'Free Trial',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'freeTrial'],
                                        advancedSearch: [title: "More filter options ...", category: 'Package']
                                ],


                                //FOR My Components Area
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        qparam     : 'qp_curgroups',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderForCuratoryGroups'],
                                        hide       : true
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true, linkInfo: 'Link to Package'],
                                [heading: 'Provider', property: 'provider.name', sort: 'provider.name', link: true, linkInfo: 'Link to Provider'],
                                [heading: 'Nominal Platform', property: 'nominalPlatform.name', sort: 'nominalPlatform.name', link: true, linkInfo: 'Link to Nominal Platform'],
                                [heading: 'Content Type', property: 'contentType.value', sort: 'contentType'],
                                [heading: 'Scope', property: 'scope', sort: 'scope'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status.value', sort: 'status'],
                                [heading: 'Current Titles', property: 'currentTippCount', sort: 'currentTippCount'],
                                [heading: 'Retired Titles', property: 'retiredTippCount', sort: 'retiredTippCount'],
                                [heading: 'Expected Titles', property: 'expectedTippCount', sort: 'expectedTippCount'],
                                [heading: 'Deleted Titles', property: 'deletedTippCount', sort: 'deletedTippCount'],
                                [heading: 'Product IDs', property: 'anbieterProduktIDs'],
                                [heading: 'Source', property: 'kbartSource.name', link: true, sort: 'kbartSource.name', linkInfo: 'Link to Source'],
                                [heading: 'Automatic Updates', property: 'kbartSource.automaticUpdates', link: true, linkInfo: 'Link to Source']
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
                msgCode    : 'package.plural',
                title       : 'Packages',
                defaultSort : 'lastUpdated',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        qparam     : 'qp_platform_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_source_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'kbartSource.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_provider_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Name of Package',
                                        qparam     : 'qp_name',
                                        msgCode     : 'package.nameOfPackage',
                                        placeholder: 'Package Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B', normalise: false]
                                ],
                                [
                                        prompt     : 'Identifier',
                                        msgCode     : 'identifier.value',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],

                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        msgCode     : 'curatorygroup.label',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup'],

                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Org',
                                        prompt     : 'Provider',
                                        msgCode     : 'org.label',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],

                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Source Automatic Updates',
                                        msgCode     : 'kbartSource.automaticUpdates',
                                        qparam     : 'qp_source_automaticUpdates',
                                        placeholder: 'Source Automatic Updates',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'kbartSource.automaticUpdates'],
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'createdSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'dateCreated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                //Package Filter
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_CONTENT_TYPE,
                                        prompt     : 'Content Type',
                                        msgCode     : 'package.contentType',
                                        qparam     : 'qp_contentType',
                                        placeholder: 'Content Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'contentType'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        msgCode     : 'package.ddcs',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'ddcs'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_PAYMENT_TYPE,
                                        prompt     : 'Paid',
                                        msgCode     : 'package.paymentType',
                                        qparam     : 'qp_paymentType',
                                        placeholder: 'Paid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paymentType'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        msgCode     : 'package.openAccess',
                                        qparam     : 'qp_oa',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],

                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PAA_ARCHIVING_AGENCY,
                                        prompt     : 'Package Archiving Agency',
                                        msgCode     : 'package.archivingAgency',
                                        qparam     : 'qp_archivingAgency',
                                        placeholder: 'Package Archiving Agency',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paas.archivingAgency'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                //Title Filter
                                [
                                        prompt     : 'Title',
                                        msgCode     : 'package.label',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.name'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Identifier',
                                        msgCode     : 'identifier.value',
                                        qparam     : 'qp_tippIdentifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.ids.value'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status_tipp',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.status'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Publication Type',
                                        msgCode     : 'titleinstancepackageplatform.publicationType',
                                        qparam     : 'qp_publicationType_tipp',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.publicationType'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_MEDIUM,
                                        prompt     : 'Medium',
                                        msgCode     : 'titleinstancepackageplatform.label',
                                        qparam     : 'qp_medium_tipp',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.medium'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_ACCESS_TYPE,
                                        prompt     : 'Access Type',
                                        msgCode     : 'titleinstancepackageplatform.label',
                                        qparam     : 'qp_accessType_tipp',
                                        placeholder: 'Access Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.accessType'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                /*[
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'publisher',
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_publisherName',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.publisherName'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],*/
                                [
                                        prompt     : 'Publisher',
                                        msgCode     : 'titleinstancepackageplatform.label',
                                        qparam     : 'qp_publisherName',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.publisherName'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Author',
                                        msgCode     : 'titleinstancepackageplatform.label',
                                        qparam     : 'qp_firstAuthor_tipp',
                                        placeholder: 'Author',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.firstAuthor'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Editor',
                                        msgCode     : 'titleinstancepackageplatform.label',
                                        qparam     : 'qp_firstEditor_tipp',
                                        placeholder: 'Editor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.firstEditor'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],

                                /* [
                                         type     : 'dropDownGroup',
                                         dropDownType  : 'subjectArea',
                                         prompt     : 'Subject Area',
                                         qparam     : 'qp_subjectArea_tipp',
                                         placeholder: 'Subject Area',
                                         contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.subjectArea'],
                                         advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                 ],
                                 [
                                         type     : 'dropDownGroup',
                                         dropDownType  : 'dateFirstOnlineYear',
                                         prompt     : 'Date First Online Year',
                                         qparam     : 'qp_dateFirstOnlineYear',
                                         placeholder: 'Date First Online Year',
                                         contextTree: ['ctxtp': 'qry', 'comparator': 'eqYear', 'prop': 'tipps.dateFirstOnline'],
                                         advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                 ],
                                 [
                                         type     : 'dropDownGroup',
                                         dropDownType  : 'accessStartDate',
                                         prompt     : 'Access Start Date Year',
                                         qparam     : 'qp_accessStartDate',
                                         placeholder: 'Access Start Date Year',
                                         contextTree: ['ctxtp': 'qry', 'comparator': 'eqYear', 'prop': 'tipps.accessStartDate'],
                                         advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                 ],
                                 [
                                         type     : 'dropDownGroup',
                                         dropDownType  : 'accessEndDate',
                                         prompt     : 'Access End Date Year',
                                         qparam     : 'qp_accessEndDate',
                                         placeholder: 'Access End Date Year',
                                         contextTree: ['ctxtp': 'qry', 'comparator': 'eqYear', 'prop': 'tipps.accessEndDate'],
                                         advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                 ],*/
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        msgCode     : 'package.ddcs',
                                        qparam     : 'qp_ddc_tipp',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderDdcs'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_LANGUAGE,
                                        prompt     : 'Language',
                                        msgCode     : 'package.label',
                                        qparam     : 'qp_language_tipp',
                                        placeholder: 'Language',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.languages.language'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                //Platform Filter
                                [
                                        prompt     : 'Name of Platform',
                                        msgCode     : 'platform.nameOfPlatform',
                                        qparam     : 'qp_name_platform',
                                        placeholder: 'Name of Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'nominalPlatform.name'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Shibboleth Supported',
                                        msgCode     : 'platform.shibbolethAuthentication',
                                        qparam     : 'qp_shibbolethAuthentication_platform',
                                        placeholder: 'Shibboleth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.shibbolethAuthentication'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Open Athens Supported',
                                        msgCode     : 'platform.openAthens',
                                        qparam     : 'qp_openAthens_platform',
                                        placeholder: 'Open Athens Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.openAthens'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_IP_AUTH,
                                        prompt     : 'IP Auth Supported',
                                        msgCode     : 'platform.ipAuthentication',
                                        qparam     : 'qp_ipAuthentication_platform',
                                        placeholder: 'IP Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.ipAuthentication'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Mail Domain Auth Supported',
                                        msgCode    : 'platform.mailDomain',
                                        qparam     : 'qp_mailDomain',
                                        placeholder: 'Mail Domain Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'mailDomain'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Referrer Authentification Auth Supported',
                                        msgCode    : 'platform.referrerAuthentification',
                                        qparam     : 'qp_referrerAuthentification',
                                        placeholder: 'Referrer Authentification Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'referrerAuthentification'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'EZ Proxy Auth Supported',
                                        msgCode    : 'platform.ezProxy',
                                        qparam     : 'qp_ezProxy',
                                        placeholder: 'EZ Proxy Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ezProxy'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'HAN-Server Auth Supported',
                                        msgCode    : 'platform.hanServer',
                                        qparam     : 'qp_hanServer',
                                        placeholder: 'HAN-Server Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hanServer'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Other Proxies Auth Supported',
                                        msgCode    : 'platform.otherProxies',
                                        qparam     : 'qp_hanServer',
                                        placeholder: 'Other Proxies Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'otherProxies'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_STATISTICS_FORMAT,
                                        prompt     : 'Statistics Format',
                                        msgCode     : 'platform.statisticsFormat',
                                        qparam     : 'qp_statisticsFormat_platform',
                                        placeholder: 'Statistics Format',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.statisticsFormat'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Supported',
                                        msgCode     : 'platform.counterR4Supported',
                                        qparam     : 'qp_counterR4Supported_platform',
                                        placeholder: 'Counter R4 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR4Supported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Supported',
                                        msgCode     : 'platform.counterR5Supported',
                                        qparam     : 'qp_counterR5Supported_platform',
                                        placeholder: 'Counter R5 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR5Supported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Sushi Api Supported',
                                        msgCode     : 'platform.counterR4SushiApiSupported',
                                        qparam     : 'qp_counterR4SushiApiSupported_platform',
                                        placeholder: 'Counter R4 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR4SushiApiSupported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Sushi Api Supported',
                                        msgCode     : 'platform.counterR5SushiApiSupported',
                                        qparam     : 'qp_counterR5SushiApiSupported_platform',
                                        placeholder: 'Counter R5 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR5SushiApiSupported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],

                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true, linkInfo: 'Link to Package'],
                                [heading: 'Provider', property: 'provider.name', sort: 'provider.name', link: true, linkInfo: 'Link to Provider'],
                                [heading: 'Nominal Platform', property: 'nominalPlatform.name', sort: 'nominalPlatform.name', link: true, linkInfo: 'Link to Nominal Platform'],
                                [heading: 'Curatory Groups', property: 'curatoryGroupsCuratoryGroup', link: true, linkInfo: 'Link to Curatory Group'],
                                [heading: 'Content Type', property: 'contentType.value', sort: 'contentType'],
                                [heading: 'Product IDs', property: 'anbieterProduktIDs'],
                                [heading: 'Current Titles', property: 'currentTippCount', sort: 'currentTippCount'],
                                [heading: 'Retired Titles', property: 'retiredTippCount', sort: 'retiredTippCount'],
                                [heading: 'Expected Titles', property: 'expectedTippCount', sort: 'expectedTippCount'],
                                [heading: 'Deleted Titles', property: 'deletedTippCount', sort: 'deletedTippCount'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Automatic Updates', property: 'kbartSource.automaticUpdates', link: true, linkInfo: 'Link to Source']
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
                msgCode    : 'platform.plural',
                title       : 'Platforms',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                //Hidden Fields
                                [
                                        qparam     : 'qp_curgroups',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderForCuratoryGroups'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_provider_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'createdSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'dateCreated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Name',
                                        msgCode    : 'default.name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        msgCode     : 'curatorygroup.label',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Org',
                                        prompt     : 'Provider',
                                        msgCode     : 'org.label',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        prompt     : 'Platform URL',
                                        qparam     : 'qp_url',
                                        msgCode     : 'platform.primaryUrl',
                                        placeholder: 'Platform URL',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'primaryUrl']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Shibboleth Supported',
                                        msgCode     : 'platform.shibbolethAuthentication',
                                        qparam     : 'qp_shibbolethAuthentication',
                                        placeholder: 'Shibboleth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'shibbolethAuthentication'],
                                        advancedSearch: [title: "Authentication", category: 'Authentication']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Open Athens Supported',
                                        msgCode     : 'platform.openAthens',
                                        qparam     : 'qp_openAthens',
                                        placeholder: 'Open Athens Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAthens'],
                                        advancedSearch: [title: "Authentication", category: 'Authentication']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_IP_AUTH,
                                        prompt     : 'IP Auth Supported',
                                        msgCode     : 'platform.ipAuthentication',
                                        qparam     : 'qp_ipAuthentication',
                                        placeholder: 'IP Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ipAuthentication'],
                                        advancedSearch: [title: "Authentication", category: 'Authentication']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Mail Domain Auth Supported',
                                        msgCode    : 'platform.mailDomain',
                                        qparam     : 'qp_mailDomain',
                                        placeholder: 'Mail Domain Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'mailDomain'],
                                        advancedSearch: [title: "Authentication", category: 'Authentication']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Referrer Authentification Auth Supported',
                                        msgCode    : 'platform.referrerAuthentification',
                                        qparam     : 'qp_referrerAuthentification',
                                        placeholder: 'Referrer Authentification Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'referrerAuthentification'],
                                        advancedSearch: [title: "Authentication", category: 'Authentication']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'EZ Proxy Auth Supported',
                                        msgCode    : 'platform.ezProxy',
                                        qparam     : 'qp_ezProxy',
                                        placeholder: 'EZ Proxy Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ezProxy'],
                                        advancedSearch: [title: "Authentication", category: 'Authentication']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'HAN-Server Auth Supported',
                                        msgCode    : 'platform.hanServer',
                                        qparam     : 'qp_hanServer',
                                        placeholder: 'HAN-Server Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hanServer'],
                                        advancedSearch: [title: "Authentication", category: 'Authentication']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Other Proxies Auth Supported',
                                        msgCode    : 'platform.otherProxies',
                                        qparam     : 'qp_hanServer',
                                        placeholder: 'Other Proxies Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'otherProxies'],
                                        advancedSearch: [title: "Authentication", category: 'Authentication']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_STATISTICS_FORMAT,
                                        prompt     : 'Statistics Format',
                                        msgCode     : 'platform.statisticsFormat',
                                        qparam     : 'qp_statisticsFormat',
                                        placeholder: 'Statistics Format',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'statisticsFormat'],
                                        advancedSearch: [title: "Statistics", category: 'Statistics']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Supported',
                                        msgCode     : 'platform.counterR4Supported',
                                        qparam     : 'qp_counterR4Supported',
                                        placeholder: 'Counter R4 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR4Supported'],
                                        advancedSearch: [title: "Statistics", category: 'Statistics']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Supported',
                                        msgCode     : 'platform.counterR5Supported',
                                        qparam     : 'qp_counterR5Supported',
                                        placeholder: 'Counter R5 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR5Supported'],
                                        advancedSearch: [title: "Statistics", category: 'Statistics']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Sushi Api Supported',
                                        msgCode     : 'platform.counterR4SushiApiSupported',
                                        qparam     : 'qp_counterR4SushiApiSupported',
                                        placeholder: 'Counter R4 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR4SushiApiSupported'],
                                        advancedSearch: [title: "Statistics", category: 'Statistics']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Sushi Api Supported',
                                        msgCode     : 'platform.counterR5SushiApiSupported',
                                        qparam     : 'qp_counterR5SushiApiSupported',
                                        placeholder: 'Counter R5 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR5SushiApiSupported'],
                                        advancedSearch: [title: "Statistics", category: 'Statistics']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'Platform',
                                        msgCode     : 'platform.accessPlatform',
                                        qparam     : 'qp_accessPlatform',
                                        placeholder: 'Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessPlatform'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'PDF viewer',
                                        msgCode     : 'platform.viewerForPdf',
                                        qparam     : 'qp_viewerForPdf',
                                        placeholder: 'PDF viewer',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'viewerForPdf'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'ePub viewer',
                                        msgCode     : 'platform.viewerForEpub',
                                        qparam     : 'qp_viewerForEpub',
                                        placeholder: 'ePub viewer',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'viewerForEpub'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'Audio player',
                                        msgCode     : 'platform.playerForAudio',
                                        qparam     : 'qp_playerForAudio',
                                        placeholder: 'Audio player',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'playerForAudio'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'Video player',
                                        msgCode     : 'platform.playerForVideo',
                                        qparam     : 'qp_playerForVideo',
                                        placeholder: 'Video player',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'playerForVideo'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Accessibility Statement available',
                                        msgCode     : 'platform.accessibilityStatementAvailable',
                                        qparam     : 'qp_accessibilityStatementAvailable',
                                        placeholder: 'Accessibility Statement available',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessibilityStatementAvailable'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        prompt     : 'Link to the Accessibility Statement',
                                        qparam     : 'qp_accessibilityStatementUrl',
                                        msgCode     : 'platform.accessibilityStatementUrl',
                                        placeholder: 'Link to the Accessibility Statement',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessibilityStatementUrl'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'EPUB e-books content',
                                        msgCode     : 'platform.accessEPub',
                                        qparam     : 'qp_accessEPub',
                                        placeholder: 'EPUB e-books content',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessEPub'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'PDF e-books content',
                                        msgCode     : 'platform.accessPdf',
                                        qparam     : 'qp_accessPdf',
                                        placeholder: 'PDF e-books content',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessPdf'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'Audio content',
                                        msgCode    : 'platform.accessAudio',
                                        qparam     : 'qp_accessAudio',
                                        placeholder: 'Audio content',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessAudio'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'Video content',
                                        msgCode    : 'platform.accessVideo',
                                        qparam     : 'qp_accessVideo',
                                        placeholder: 'Video content',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessVideo'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UYNP,
                                        prompt     : 'Database content',
                                        msgCode    : 'platform.accessDatabase',
                                        qparam     : 'qp_accessDatabase',
                                        placeholder: 'Database content',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessDatabase'],
                                        advancedSearch: [title: "Accessibility", category: 'Accessibility']
                                ],
                                [
                                        prompt     : 'Platform blog URL',
                                        qparam     : 'qp_platformBlogUrl',
                                        msgCode    : 'platform.platformBlogUrl',
                                        placeholder: 'Platform blog URL',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'platformBlogUrl'],
                                        advancedSearch: [title: "Additional services", category: 'Additional services']
                                ],
                                [
                                        prompt     : 'RSS URL',
                                        qparam     : 'qp_rssUrl',
                                        msgCode    : 'platform.rssUrl',
                                        placeholder: 'RSS URL',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'rssUrl'],
                                        advancedSearch: [title: "Additional services", category: 'Additional services']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Individual design / logo',
                                        qparam     : 'qp_individualDesignLogo',
                                        msgCode    : 'platform.individualDesignLogo',
                                        placeholder: 'Individual design / logo',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'individualDesignLogo'],
                                        advancedSearch: [title: "Additional services", category: 'Additional services']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Full text search',
                                        qparam     : 'qp_fullTextSearch',
                                        msgCode    : 'platform.fullTextSearch',
                                        placeholder: 'Full text search',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'fullTextSearch'],
                                        advancedSearch: [title: "Additional services", category: 'Additional services']
                                ]
                        ],
                        qbeResults: [
                                [heading: 'Platform', property: 'name', sort: 'name', link: true, linkInfo: 'Link to Platform'],
                                [heading: 'Primary URL', property: 'primaryUrl', sort: 'primaryUrl', outGoingLink: true, linkInfo: 'Link to Primary URL'],
                                [heading: 'Provider', property: 'provider.name', sort: 'provider.name', link: true, linkInfo: 'Link to Provider'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status.value', sort: 'status'],
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
                msgCode    : 'refdatacategory.plural',
                title    : 'Refdata Categories ',
                defaultSort : 'desc',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Description',
                                        qparam     : 'qp_desc',
                                        placeholder: 'Category Description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc', 'wildcard': 'B']
                                ],

                                [
                                        prompt     : 'Description EN',
                                        qparam     : 'qp_desc_en',
                                        placeholder: 'Category Description En',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc_en']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', sort: 'desc', property: 'desc', link: true, linkInfo: 'Link to Description'],
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

    Map refdataCategoriesPublic() {
        Map result = [
                baseclass: 'wekb.RefdataCategory',
                msgCode    : 'refdatacategory.plural',
                title    : 'Reference Data Categories ',
                defaultSort : 'desc',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name',
                                        qparam     : 'qp_desc',
                                        placeholder: 'Category Name EN',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc_en', 'wildcard': 'B']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Name EN', sort: 'desc_en', property: 'desc_en'],
                                [heading: 'Name DE', sort: 'desc_de', property: 'desc_de'],
                                [heading: 'Refdata Values', sort: 'valuesCount', property: 'valuesCount', link: true]
                        ]
                ]
        ]

        result
    }

    Map refdataValues() {
        Map result = [
                baseclass: 'wekb.RefdataValue',
                msgCode    : 'refdatavalue.plural',
                title    : 'Refdata Values ',
                defaultSort : 'value',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Description',
                                        qparam     : 'qp_desc',
                                        placeholder: 'Description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc', 'wildcard': 'B']
                                ],

                                [
                                        prompt     : 'Value',
                                        qparam     : 'qp_value',
                                        placeholder: 'Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'value']
                                ],

                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataCategory',
                                        prompt     : 'Refdata Category',
                                        qparam     : 'qp_owner',
                                        placeholder: 'Refdata Category',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'owner']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Value', sort: 'value', property: 'value', link: true, linkInfo: 'Link to RefdataValue'],
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
                msgCode    : 'kbartsource.plural',
                title    : 'Source',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                //Hidden Fields
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        qparam     : 'qp_curgroups',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderForCuratoryGroups'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Name',
                                        msgCode    : 'default.name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        msgCode    : 'curatorygroup.label',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup'],

                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Automatic Updates',
                                        msgCode    : 'kbartsource.automaticUpdates',
                                        qparam     : 'qp_automaticUpdates',
                                        placeholder: 'Automatic Updates',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'automaticUpdates'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.SOURCE_FREQUENCY,
                                        prompt     : 'Frequency',
                                        msgCode    : 'kbartsource.frequency',
                                        qparam     : 'qp_frequency',
                                        placeholder: 'Frequencys',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'frequency'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Source', property: 'name', sort: 'name', link: true, linkInfo: 'Link to Source'],
                                [heading: 'Packages', property: 'packages', link: true, sort: 'packages.name', linkInfo: 'Link to Package'],
                                [heading: 'Url', property: 'url', sort: 'url', outGoingLink: true, linkInfo: 'Link to Kbart Url'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status.value', sort: 'status'],
                                [heading: 'automatic Updates', property: 'automaticUpdates'],
                                [heading: 'Frequency', property: 'frequency.value', sort: 'frequency'],
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
                msgCode    : 'titleinstancepackageplatform.plural',
                title    : 'Titles',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                //Hidden Fields
                                [
                                        qparam     : 'qp_status_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_title_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'title.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_provider_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_pkg_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_plat_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_status_value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status.value'],
                                        hide       : true
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        qparam     : 'qp_curgroups',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderForCuratoryGroups'],
                                        hide       : true
                                ],

                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'createdSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'dateCreated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Title',
                                        msgCode    : 'titleinstancepackageplatform.name',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B'],
                                ],

                                [
                                        prompt     : 'Identifier',
                                        msgCode    : 'identifer.value',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value']
                                ],
                                [
                                        prompt     : 'Url',
                                        msgCode    : 'titleinstancepackageplatform.url',
                                        qparam     : 'qp_url',
                                        placeholder: 'Url',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'url'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Org',
                                        prompt     : 'Provider',
                                        msgCode    : 'provider.label',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider'],

                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Package',
                                        prompt     : 'Package',
                                        msgCode    : 'titleinstancepackageplatform.pkg.name',
                                        qparam     : 'qp_pkg',
                                        placeholder: 'Package',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Platform',
                                        prompt     : 'Platform',
                                        msgCode    : 'titleinstancepackageplatform.hostPlatform',
                                        qparam     : 'qp_plat',
                                        placeholder: 'Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        msgCode    : 'curatoryGroup.label',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.curatoryGroups.curatoryGroup'],

                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'publisher',
                                        prompt     : 'Publisher',
                                        msgCode    : 'titleinstancepackageplatform.publisherName',
                                        qparam     : 'qp_publisherName',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publisherName'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Publication Type',
                                        msgCode    : 'titleinstancepackageplatform.publicationType',
                                        qparam     : 'qp_publicationType',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publicationType'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_MEDIUM,
                                        prompt     : 'Medium',
                                        msgCode    : 'titleinstancepackageplatform.medium',
                                        qparam     : 'qp_medium',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'medium'],
                                ],
                                [
                                        prompt     : 'Author',
                                        msgCode    : 'titleinstancepackageplatform.firstAuthor',
                                        qparam     : 'qp_firstAuthor',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstAuthor'],
                                ],
                                [
                                        prompt     : 'Editor',
                                        msgCode    : 'titleinstancepackageplatform.firstEditor',
                                        qparam     : 'qp_firstEditor',
                                        placeholder: 'Editor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstEditor'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_ACCESS_TYPE,
                                        prompt     : 'Access Type',
                                        msgCode    : 'titleinstancepackageplatform.accessType',
                                        qparam     : 'qp_accessType',
                                        placeholder: 'Access Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessType'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'subjectArea',
                                        prompt     : 'Subject Area',
                                        msgCode    : 'titleinstancepackageplatform.subjectArea',
                                        qparam     : 'qp_subjectArea_tipp',
                                        placeholder: 'Subject Area',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'subjectArea'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'dateFirstOnlineYear',
                                        prompt     : 'Date First Online Year',
                                        msgCode    : 'titleinstancepackageplatform.dateFirstOnline',
                                        qparam     : 'qp_dateFirstOnlineYear',
                                        placeholder: 'Date First Online Year',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eqYear', 'prop': 'dateFirstOnline'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'accessStartDate',
                                        prompt     : 'Access Start Date Year',
                                        msgCode    : 'titleinstancepackageplatform.accessStartDate',
                                        qparam     : 'qp_accessStartDate',
                                        placeholder: 'Access Start Date Year',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eqYear', 'prop': 'accessStartDate'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'accessEndDate',
                                        prompt     : 'Access End Date Year',
                                        msgCode    : 'titleinstancepackageplatform.accessEndDate',
                                        qparam     : 'qp_accessEndDate',
                                        placeholder: 'Access End Date Year',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eqYear', 'prop': 'accessEndDate'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        msgCode    : 'titleinstancepackageplatform.ddcs',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'ddcs'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        msgCode    : 'titleinstancepackageplatform.openAccess',
                                        qparam     : 'qp_openAccess',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_LANGUAGE,
                                        prompt     : 'Language',
                                        msgCode    : 'titleinstancepackageplatform.language',
                                        qparam     : 'qp_language',
                                        placeholder: 'Language',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'languages.language'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ]
                        ],
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: true, sort: 'name', linkInfo: 'Link to Title'],
                                [heading: 'Type', property: 'publicationType.value', sort: 'publicationType.value'],
                                [heading: 'Medium', property: 'medium.value', sort: 'medium.value'],
                                [heading: 'First Author', property: 'firstAuthor', sort: 'firstAuthor'],
                                [heading: 'Package', qpEquiv: 'qp_pkg_id', property: 'pkg.name', sort: 'pkg.name', link: true, linkInfo: 'Link to Package'],
                                [heading: 'Platform', qpEquiv: 'qp_plat_id', property: 'hostPlatform.name', sort: 'hostPlatform.name', link: true, linkInfo: 'Link to Platform'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status.value', sort: 'status.value'],
                                [heading: 'URL', property: 'url', sort: 'url', outGoingLink: true, linkInfo: 'Link to Title Url']
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
                                        qparam     : 'qp_status_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_title_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'title.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_provider_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_pkg_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_plat_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Title',
                                        msgCode    : 'titleinstancepackageplatform.name',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'publisher',
                                        prompt     : 'Publisher',
                                        msgCode    : 'titleinstancepackageplatform.publisherName',
                                        qparam     : 'qp_publisherName',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publisherName'],
                                ],
                                [
                                        prompt     : 'Identifier',
                                        msgCode    : 'identifer.value',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],

                                ],
                                [
                                        prompt     : 'Url',
                                        msgCode    : 'titleinstancepackageplatform.url',
                                        qparam     : 'qp_url',
                                        placeholder: 'Url',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'url'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Publication Type',
                                        msgCode    : 'titleinstancepackageplatform.publicationType',
                                        qparam     : 'qp_publicationType',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publicationType'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_MEDIUM,
                                        prompt     : 'Medium',
                                        msgCode    : 'titleinstancepackageplatform.medium',
                                        qparam     : 'qp_medium',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'medium'],
                                ],
                                [
                                        prompt     : 'Author',
                                        msgCode    : 'titleinstancepackageplatform.firstAuthor',
                                        qparam     : 'qp_firstAuthor',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstAuthor'],
                                ],
                                [
                                        prompt     : 'Editor',
                                        msgCode    : 'titleinstancepackageplatform.firstEditor',
                                        qparam     : 'qp_firstEditor',
                                        placeholder: 'Editor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstEditor'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_ACCESS_TYPE,
                                        prompt     : 'Access Type',
                                        msgCode    : 'titleinstancepackageplatform.accessType',
                                        qparam     : 'qp_accessType',
                                        placeholder: 'Access Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessType'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'subjectArea',
                                        prompt     : 'Subject Area',
                                        msgCode    : 'titleinstancepackageplatform.subjectArea',
                                        qparam     : 'qp_subjectArea_tipp',
                                        placeholder: 'Subject Area',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'subjectArea'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'dateFirstOnlineYear',
                                        prompt     : 'Date First Online Year',
                                        msgCode    : 'titleinstancepackageplatform.dateFirstOnline',
                                        qparam     : 'qp_dateFirstOnlineYear',
                                        placeholder: 'Date First Online Year',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eqYear', 'prop': 'dateFirstOnline'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'accessStartDate',
                                        prompt     : 'Access Start Date Year',
                                        msgCode    : 'titleinstancepackageplatform.accessStartDate',
                                        qparam     : 'qp_accessStartDate',
                                        placeholder: 'Access Start Date Year',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eqYear', 'prop': 'accessStartDate'],
                                ],
                                [
                                        type     : 'dropDownGroup',
                                        dropDownType  : 'accessEndDate',
                                        prompt     : 'Access End Date Year',
                                        msgCode    : 'titleinstancepackageplatform.accessEndDate',
                                        qparam     : 'qp_accessEndDate',
                                        placeholder: 'Access End Date Year',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eqYear', 'prop': 'accessEndDate'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        msgCode    : 'titleinstancepackageplatform.ddcs',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'ddcs'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.TIPP_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        msgCode    : 'titleinstancepackageplatform.openAccess',
                                        qparam     : 'qp_openAccess',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_LANGUAGE,
                                        prompt     : 'Language',
                                        msgCode    : 'titleinstancepackageplatform.languages',
                                        qparam     : 'qp_language',
                                        placeholder: 'Language',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'languages.language'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                //FOR My Components Area
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        qparam     : 'qp_curgroups',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderForCuratoryGroups'],
                                        hide       : true
                                ],

                        ],
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: true, linkInfo: 'Link to Title'],
                                [heading: 'Type', property: 'publicationType.value', sort: 'publicationType.value'],
                                [heading: 'Medium', property: 'medium.value', sort: 'medium.value'],
                                [heading: 'First Author', property: 'firstAuthor', sort: 'firstAuthor'],
                                [heading: 'Platform', qpEquiv: 'qp_plat_id', property: 'hostPlatform.name', sort: 'hostPlatform.name',  link: true, linkInfo: 'Link to Platform'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status.value', sort: 'status.value'],
                                [heading: 'URL', property: 'url', sort: 'url', outGoingLink: true, linkInfo: 'Link to Title URL']
                        ]
                ]
        ]

        result
    }

    Map vendors() {
        Map result = [
                baseclass   : 'wekb.Vendor',
                msgCode    : 'vendor.plural',
                title       : 'Vendors',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                //Hidden Fields
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        qparam     : 'qp_curgroups',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderForCuratoryGroups'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'changedSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'lastUpdated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'createdSince',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'greater', 'prop': 'dateCreated', 'type': 'java.util.Date'],
                                        hide       : true
                                ],
                                //General Fields
                                [
                                        prompt     : 'Name',
                                        msgCode    : 'default.name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike_Combine_Name_And_VariantNames_And_AbbreviatedName_Org', 'prop': 'name', 'wildcard': 'B']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        msgCode    : 'curatorygroup.label',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.COMPONENT_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.VENDOR_ROLE,
                                        prompt     : 'Role',
                                        msgCode    : 'vendor.role',
                                        qparam     : 'qp_roles',
                                        placeholder: 'Role',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'roles'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.VENDOR_SUPPORTED_LIB_SYSTEM,
                                        prompt     : 'Supported Library Systems',
                                        msgCode    : 'vendor.supportedLibrarySystems',
                                        qparam     : 'qp_supportedLibrarySystems',
                                        placeholder: 'Role',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'supportedLibrarySystems'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.VENDOR_ELECTRONIC_BILLING,
                                        prompt     : 'Electronic Billings',
                                        msgCode    : 'vendor.electronicBillings',
                                        qparam     : 'qp_electronicBillings',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'electronicBillings'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.VENDOR_INVOICE_DISPATCH,
                                        prompt     : 'Invoice Dispatchs',
                                        msgCode    : 'vendor.invoiceDispatchs',
                                        qparam     : 'qp_invoiceDispatchs',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'invoiceDispatchs'],
                                ],

                        ],
                        qbeResults: [
                                [heading: 'Vendor', property: 'name', sort: 'name', link: true, linkInfo: 'Link to Vendor'],
                                [heading: 'Homepage', property: 'homepage', sort: 'homepage', outGoingLink: true, linkInfo: 'Link to Homepage'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status.value', sort: 'status']
                        ]
                ]
        ]

        result
    }

    Map users() {
        Map result = [
                baseclass   : 'wekb.auth.User',
                msgCode    : 'user.plural',
                title       : 'Users',
                defaultSort : 'username',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Username',
                                        qparam     : 'qp_name',
                                        placeholder: 'Username',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'username', 'wildcard': 'B']
                                ],
                                [
                                        prompt     : 'Email',
                                        qparam     : 'qp_email',
                                        placeholder: 'Email',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'email']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Enabled',
                                        qparam     : 'qp_enabled',
                                        placeholder: 'Enabled',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'enabled'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Account Expired',
                                        qparam     : 'qp_accountExpired',
                                        placeholder: 'Account Expired',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accountExpired'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Account Locked',
                                        qparam     : 'qp_accountLocked',
                                        placeholder: 'Account Locked',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accountLocked'],
                                ],
                                [
                                        type       : 'dropDown',
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
                                [heading: 'Username', property: 'username', link: true, sort: 'username', linkInfo: 'Link to User'],
                                [heading: 'Enabled', property: 'enabled', sort: 'enabled'],
                                [heading: 'Curatory Groups', property: 'curatoryGroupsCuratoryGroup', link: true, linkInfo: 'Link to Curatory Group'],
                                [heading: 'User', property: 'userStatus'],
                                [heading: 'Editor', property: 'editorStatus'],
                                [heading: 'Vendor-Editor', property: 'vendorEditorStatus'],
                                [heading: 'API-User', property: 'apiUserStatus'],
                                [heading: 'Admin', property: 'adminStatus'],
                                [heading: 'Super-User', property: 'superUserStatus'],
                                [heading: 'Account Locked', property: 'accountLocked'],
                                [heading: 'Last Login', property: 'lastLogin'],
                                [heading: 'Invalid Login Attempts', property: 'invalidLoginAttempts'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated']
                        ]
                ]
        ]
        result
    }

    Map userJobs() {
        Map result = [
                baseclass   : 'wekb.system.JobResult',
                msgCode    : 'jobresult.plural',
                title       : 'User Jobs',
                defaultSort : 'id',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.JOB_TYPE,
                                        prompt     : 'Type',
                                        msgCode     : 'default.type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type of Job',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true, linkInfo: 'Link to User Jobs'],
                                [heading: 'Component', property: 'linkedItem'],
                                [heading: 'Type', property: 'type.value', sort: 'type'],
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
                msgCode    : 'updatepackageinfo.plural',
                title    : 'Package Update Infos',
                defaultSort : 'startTime',
                defaultOrder: 'desc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        qparam     : 'qp_pkg_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],

                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.CuratoryGroup',
                                        qparam     : 'qp_curgroups',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'placeHolderForCuratoryGroups'],
                                        hide       : true
                                ],

                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.Package',
                                        prompt     : 'Package',
                                        msgCode    : 'package.label',
                                        qparam     : 'qp_pkg',
                                        placeholder: 'Package',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg']
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UPDATE_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                ],

                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Automatic Update',
                                        msgCode    : 'updatepackageinfo.automaticUpdate',
                                        qparam     : 'qp_automaticUpdate',
                                        placeholder: 'Automatic Update',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'automaticUpdate'],
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true, linkInfo: 'Link to Package Update Info'],
                                [heading: 'Package', property: 'pkg.name', link: true, linkInfo: 'Link to Package'],
                                [heading: 'Status', property: 'status', sort: 'status.value'],
                                [heading: 'Automatic Update', property: 'automaticUpdate', sort: 'automaticUpdate'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                                [heading: 'Only Last Changed Update', property: 'onlyRowsWithLastChanged', sort: 'onlyRowsWithLastChanged'],
                                [heading: 'Titles in we:kb before update', property: 'countPreviouslyTippsInWekb', sort: 'countPreviouslyTippsInWekb'],
                                [heading: 'Titles in we:kb after update', property: 'countNowTippsInWekb', sort: 'countNowTippsInWekb'],
                                [heading: 'Rows in KBART-File', property: 'countKbartRows', sort: 'countKbartRows'],
                                [heading: 'Processed KBART Rows', property: 'countProcessedKbartRows', sort: 'countProcessedKbartRows'],
                                [heading: 'Changed Titles ', property: 'countChangedTipps', sort: 'countChangedTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=Changed%20Title', linkInfo: 'Link to Changed Titles'],
                                [heading: 'Removed Titles ', property: 'countRemovedTipps', sort: 'countRemovedTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=Removed%20Title', linkInfo: 'Link to Removed Titles'],
                                [heading: 'New Titles', property: 'countNewTipps', sort: 'countNewTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=New%20Title', linkInfo: 'Link to New Titles'],
                                [heading: 'Invalid Titles', property: 'countInValidTipps', sort: 'countInValidTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=Failed%20Title', linkInfo: 'Link to Invalid Titles'],

                        ]
                ]
        ]
        result
    }

    Map updateTippInfos() {
        Map result = [
                baseclass: 'wekb.UpdateTippInfo',
                msgCode    : 'updatetippinfo.plural',
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
                                        prompt     : 'Title',
                                        msgCode    : 'tipp.name',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B'],
                                ],
                                [
                                        prompt     : 'KBART Field',
                                        msgCode    : 'updatetippinfo.kbartProperty',
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
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UPDATE_TYPE,
                                        prompt     : 'Type',
                                        msgCode     : 'default.type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type'],
                                ],
                                [
                                        type       : 'dropDown',
                                        baseClass  : 'wekb.RefdataValue',
                                        filter1    : RCConstants.UPDATE_STATUS,
                                        prompt     : 'Status',
                                        msgCode    : 'default.status',
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
                                [heading: 'Description', property: 'description', link: true, linkInfo: 'Link to Title Update Info'],
                                [heading: 'Title', property: 'tipp.name', link: true, linkInfo: 'Link to Title'],
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
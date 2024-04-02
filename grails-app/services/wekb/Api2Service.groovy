package wekb

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.json.JsonSlurper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.grails.orm.hibernate.cfg.GrailsHibernateUtil
import wekb.helper.RDStore
import wekb.utils.DateUtils

import javax.sql.DataSource
import java.text.Normalizer

@Transactional
class Api2Service {

    GrailsApplication grailsApplication
    GenericOIDService genericOIDService

    private ApiTemplates = new java.util.HashMap<String, Map>()

    @javax.annotation.PostConstruct
    def init() {
        ApiTemplates.put('orgs', orgs())
        ApiTemplates.put('packages', packages())
        ApiTemplates.put('platforms', platforms())
        ApiTemplates.put('tipps', tipps())
        ApiTemplates.put('tipps_sql', tipps_sql())
        ApiTemplates.put('vendors', vendors())
        ApiTemplates.put('deletedKBComponents', deletedKBComponents())

    }

    static List complexSortFields = ['titleCount', 'currentTippCount', 'deletedTippCount', 'retiredTippCount', 'expectedTippCount']

    public Map getApiTemplate(String type) {
        return ApiTemplates.get(type);
    }

    Map getApiSqlTemplate(String type) {
        return ApiTemplates.get(type+'_sql')
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
                                [
                                        qparam     : 'uuids',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'in', 'prop': 'uuid']
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
                                [
                                        qparam     : 'provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike_Combine_Name_And_VariantNames_And_AbbreviatedName_Provider', 'prop': 'provider.name', 'wildcard': 'B']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'ddc',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'ddcs'],
                                ],
                                [
                                        qparam     : 'curatoryGroupType',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup.type.value']
                                ],
                                [
                                        qparam     : 'automaticUpdates',
                                        contextTree: ['ctxtp': 'qry', 'type': 'boolean', 'comparator': 'eq', 'prop': 'kbartSource.automaticUpdates']
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
                                [sort: 'titleCount'],
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
                                        qparam     : 'curatoryGroupType',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups.curatoryGroup.type.value']
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
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'qp_shibbolethAuthentication',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'shibbolethAuthentication'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'qp_openAthens',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAthens']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'qp_ipAuthentication',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ipAuthentication'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'qp_statisticsFormat',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'statisticsFormat'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'qp_counterR3Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR3Supported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'qp_counterR4Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR4Supported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'qp_counterR5Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR5Supported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'qp_counterR4SushiApiSupported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR4SushiApiSupported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'wekb.RefdataValue',
                                        qparam     : 'qp_counterR5SushiApiSupported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR5SushiApiSupported'],
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

    Map tipps_sql() {
        Map result = [
                table: 'title_instance_package_platform',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                //General Fields
                                [
                                        qparam     : 'name',
                                        whereClause    : 'tipp_name ilike :name'
                                ],
                                [
                                        qparam     : 'identifier',
                                        whereClause    : 'exists (select id_id from identifier where id_tipp_fk = tipp_id and id_value ilike :identifier)'
                                ],
                                [
                                        qparam     : 'identifierNamespace',
                                        whereClause    : 'exists (select id_id from identifier join identifier_namespace on id_ns_fk = idns_id where id_tipp_fk = tipp_id and idns_ns = :identifierNamespace)'
                                ],
                                [
                                        qparam     : 'curatoryGroup',
                                        whereClause    : 'exists (select cgp_id from curatory_group_package join curatory_group on cgp_curatory_group_fk = cg_id where cgp_pkg_fk = tipp_pkg_fk and cg_name = :name)'
                                ],
                                [
                                        qparam     : 'status',
                                        whereClause    : 'tipp_status_rv_fk = any(:status)'
                                ],
                                [
                                        qparam     : 'changedSince',
                                        whereClause    : 'tipp_last_updated > :changedSince'
                                ],
                                [
                                        qparam     : 'changedBefore',
                                        whereClause    : 'tipp_last_updated < :changedBefore'
                                ],
                                [
                                        qparam     : 'uuid',
                                        whereClause    : 'tipp_uuid = :uuid'
                                ],
                                //spec Fields
                                [
                                        qparam     : 'packageUuid',
                                        whereClause    : 'pkg_uuid = :packageUuid'
                                ],
                                [
                                        qparam     : 'platformUuid',
                                        whereClause    : 'plat_uuid = :platformUuid'
                                ],

                        ],
                        sqlCols: [
                                stubOnly: [
                                        uuid: 'tipp_uuid',
                                        name: 'tipp_name',
                                        status: '(select rdv_value from refdata_value where rdv_id = tipp_status_rv_fk)',
                                        componentType: "'${TitleInstancePackagePlatform.class.simpleName}'",
                                        lastUpdatedDisplay: "to_char(tipp_last_updated,'${DateUtils.DATE_FORMAT_ISO_SQL}')",
                                        dateCreatedDisplay: "to_char(tipp_date_created,'${DateUtils.DATE_FORMAT_ISO_SQL}')",
                                ],
                                principalObject: [
                                        tippPackage: "concat('${Package.class.name}',':',pkg_id)",
                                        tippPackageName: 'pkg_name',
                                        tippPackageUuid: 'pkg_uuid',
                                        hostPlatform: "concat('${Platform.class.name}',':',plat_id)",
                                        hostPlatformName: 'plat_name',
                                        hostPlatformUuid: 'plat_uuid',
                                        titleType: "case when tipp_publication_type_rv_fk = ${RDStore.TIPP_PUBLIC_TYPE_SERIAL.id} then 'Journal' " +
                                                "when tipp_publication_type_rv_fk = ${RDStore.TIPP_PUBLIC_TYPE_MONO.id} then 'Book' " +
                                                "when tipp_publication_type_rv_fk = ${RDStore.TIPP_PUBLIC_TYPE_DB.id} then 'Database' " +
                                                "when tipp_publication_type_rv_fk = ${RDStore.TIPP_PUBLIC_TYPE_OTHER.id} then 'Other' " +
                                                "else 'Title' end",
                                        url: 'tipp_url',
                                        dateFirstOnline: "coalesce(to_char(tipp_date_first_online,'${DateUtils.DATE_FORMAT_ISO_SQL}'),'')",
                                        dateFirstInPrint: "coalesce(to_char(tipp_date_first_in_print,'${DateUtils.DATE_FORMAT_ISO_SQL}'),'')",
                                        accessStartDate: "coalesce(to_char(tipp_access_start_date,'${DateUtils.DATE_FORMAT_ISO_SQL}'),'')",
                                        accessEndDate: "coalesce(to_char(tipp_access_end_date,'${DateUtils.DATE_FORMAT_ISO_SQL}'),'')",
                                        lastChangedExternal: "coalesce(to_char(tipp_last_change_ext,'${DateUtils.DATE_FORMAT_ISO_SQL}'),'')",
                                        medium: '(select rdv_value from refdata_value where rdv_id = tipp_medium_rv_fk)',
                                        publicationType: '(select rdv_value from refdata_value where rdv_id = tipp_publication_type_rv_fk)',
                                        openAccess: '(select rdv_value from refdata_value where rdv_id = tipp_open_access_rv_fk)',
                                        accessType: '(select rdv_value from refdata_value where rdv_id = tipp_access_type_rv_fk)',
                                        publisherName: 'tipp_publisher_name',
                                        subjectArea: 'tipp_subject_area',
                                        series: 'tipp_series',
                                        volumeNumber: 'tipp_volume_number',
                                        editionStatement: 'tipp_edition_statement',
                                        firstAuthor: 'tipp_first_author',
                                        firstEditor: 'tipp_first_editor',
                                        parentPublicationTitleId: 'tipp_parent_publication_title_id',
                                        precedingPublicationTitleId: 'tipp_preceding_publication_title_id',
                                        supersedingPublicationTitleId: 'tipp_superseding_publication_title_id',
                                        note: 'tipp_note',
                                        fromKbartImport: 'tipp_from_kbart_import'
                                ],
                                coverageFields: [
                                        startDate: "to_char(tcs_start_date,'${DateUtils.DATE_FORMAT_ISO_SQL}')",
                                        startVolume: "coalesce(tcs_start_volume,'')",
                                        startIssue: "coalesce(tcs_start_issue,'')",
                                        endDate: "to_char(tcs_end_date,'${DateUtils.DATE_FORMAT_ISO_SQL}')",
                                        endVolume: "coalesce(tcs_end_volume,'')",
                                        endIssue: "coalesce(tcs_end_issue,'')",
                                        embargo: "coalesce(tcs_embargo,'')",
                                        coverageNote: "coalesce(tcs_note,'')",
                                        coverageDepth: "coalesce((select rdv_value from refdata_value where rdv_id = tcs_depth),'')"
                                ],
                                identifierFields: [
                                        namespace: 'idns_value',
                                        value: 'id_value',
                                        namespaceName: 'idns_name'
                                ],
                                priceFields: [
                                        type: '(select rdv_value from refdata_value where rdv_id = tp_type_fk)',
                                        amount: 'tp_price',
                                        currency: '(select rdv_value from refdata_value where rdv_id = tp_currency_fk)',
                                        startDate: "to_char(tp_start_date, '${DateUtils.DATE_FORMAT_ISO_SQL}')",
                                        endDate: "to_char(tp_end_date, '${DateUtils.DATE_FORMAT_ISO_SQL}')"
                                ],
                                ddcFields: [
                                        value: 'rdv_value',
                                        value_de: 'rdv_value_de',
                                        value_en: 'rdv_value_en',
                                ],
                                languageFields: [
                                        value: 'rdv_value',
                                        value_de: 'rdv_value_de',
                                        value_en: 'rdv_value_en',
                                ],
                                curatoryGroupFields: [
                                        name: 'cg_name',
                                        type: '(select rdv_value from refdata_value where rdv_id = cg_type_rv_fk)',
                                        curatoryGroup: "concat('${CuratoryGroup.class.name}',':',cg_id)",
                                ]
                        ],
                        qbeSortFields: [
                                [sort: 'tipp_name'],
                                [sort: 'tipp_last_updated'],
                                [sort: 'tipp_date_created'],
                                [sort: 'tipp_first_author'],
                                [sort: 'tipp_url']
                        ]
                ]
        ]

        result
    }

    Map vendors() {
        Map result = [
                baseclass   : 'wekb.Vendor',
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

             result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
             result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

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
                    result.source.lastRun = DateUtils.getSDF_ISO().format(object.kbartSource.lastRun)
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

             result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
             result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

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

            result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
            result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

            result.cpname = object.provider?.name
            result.provider = object.provider ? object.provider.getOID() : ""
            result.providerName = object.provider ? object.provider.name : ""
            result.providerUuid = object.provider ? object.provider.uuid : ""

            result.primaryUrl = object.primaryUrl

            result.titleNamespace = object.titleNamespace?.value

            result.lastAuditDate = object.lastAuditDate ? DateUtils.getSDF_ISO().format(object.lastAuditDate) : ""

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

            result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
            result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

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

            result.dateFirstOnline = object.dateFirstOnline ? DateUtils.getSDF_ISO().format(object.dateFirstOnline) : ""
            result.dateFirstInPrint = object.dateFirstInPrint ? DateUtils.getSDF_ISO().format(object.dateFirstInPrint) : ""
            result.accessStartDate = object.accessStartDate ? DateUtils.getSDF_ISO().format(object.accessStartDate) : ""
            result.accessEndDate = object.accessEndDate ? DateUtils.getSDF_ISO().format(object.accessEndDate) : ""
            result.lastChangedExternal = object.lastChangedExternal ? DateUtils.getSDF_ISO().format(object.lastChangedExternal) : ""

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
                    if (tcs.startDate) cst.startDate = DateUtils.getSDF_ISO().format(tcs.startDate)
                    cst.startVolume = tcs.startVolume ?: ""
                    cst.startIssue = tcs.startIssue ?: ""
                    if (tcs.endDate) cst.endDate = DateUtils.getSDF_ISO().format(tcs.endDate)
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
                    price.startDate = DateUtils.getSDF_ISO().format(p.startDate)
                }
                if (p.endDate){
                    price.endDate = DateUtils.getSDF_ISO().format(p.endDate)
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
            result.dateCreated = DateUtils.getSDF_ISO().format(object.dateCreated)
            result.lastUpdated = DateUtils.getSDF_ISO().format(object.lastUpdated)
            result.oldDateCreated = DateUtils.getSDF_ISO().format(object.oldDateCreated)
            result.oldLastUpdated = DateUtils.getSDF_ISO().format(object.oldLastUpdated)
            result.oldId = object.oldId
            result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)

            result
        }

        return result
    }
*/
    private LinkedHashMap<Object, Object> mapDomainFieldsToSpecFields(Object object, boolean stubOnly = false) {
        LinkedHashMap<Object, Object> result = [:]

        if(object.class.name == Package.class.name) {

            if(stubOnly){
                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

            }else {

                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

                if (object.provider) {
                    result.cpname = object.provider.name
                    result.provider = object.provider.getOID()
                    result.providerName = object.provider.name
                    result.providerUuid = object.provider.uuid
                } else {
                    result.cpname = ""
                    result.provider = ""
                    result.providerName = ""
                    result.providerUuid = ""
                }

                if (object.nominalPlatform) {
                    result.nominalPlatform = object.nominalPlatform.getOID()
                    result.nominalPlatformName = object.nominalPlatform.name
                    result.nominalPlatformUuid = object.nominalPlatform.uuid
                } else {
                    result.nominalPlatform = ""
                    result.nominalPlatformName = ""
                    result.nominalPlatformUuid = ""
                }

                result.description = object.description
                result.descriptionURL = object.descriptionURL

                /*
                result.titleCount = object.getTippCount()
                result.currentTippCount = object.getCurrentTippCount()
                result.retiredTippCount = object.getRetiredTippCount()
                result.expectedTippCount = object.getExpectedTippCount()
                result.deletedTippCount = object.getDeletedTippCount()
                */
                Map<String, Integer> tippCountMap = object.getTippCountMap()
                result.titleCount = tippCountMap.total
                result.currentTippCount = tippCountMap.get(RDStore.KBC_STATUS_CURRENT.value)
                result.retiredTippCount = tippCountMap.get(RDStore.KBC_STATUS_RETIRED.value)
                result.expectedTippCount = tippCountMap.get(RDStore.KBC_STATUS_EXPECTED.value)
                result.deletedTippCount = tippCountMap.get(RDStore.KBC_STATUS_DELETED.value)

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
                            uuid            : object.kbartSource.uuid,
                            name            : object.kbartSource.name,
                            automaticUpdates: object.kbartSource.automaticUpdates,
                            url             : object.kbartSource.url,
                            frequency       : object.kbartSource.frequency?.value,
                    ]
                    if (object.kbartSource.lastRun) {
                        result.source.lastRun = DateUtils.getSDF_ISO().format(object.kbartSource.lastRun)
                    }
                } else {
                    result.source = []
                }

                result.altname = []
                object.variantNames.each { vn ->
                    result.altname.add(vn.variantName)
                }

                result.curatoryGroups = []
                if (object.hasProperty('curatoryGroups')) {
                    object.curatoryGroups.each {
                        result.curatoryGroups.add([name         : it.curatoryGroup.name,
                                                   type         : it.curatoryGroup.type?.value,
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
                    result.nationalRanges.add([value   : nationalRange.value,
                                               value_de: nationalRange.value_de,
                                               value_en: nationalRange.value_en])
                }

                result.regionalRanges = []
                object.regionalRanges.each { regionalRange ->
                    result.regionalRanges.add([value   : regionalRange.value,
                                               value_de: regionalRange.value_de,
                                               value_en: regionalRange.value_en])
                }

                result.ddcs = []
                object.ddcs.each { ddc ->
                    result.ddcs.add([value   : ddc.value,
                                     value_de: ddc.value_de,
                                     value_en: ddc.value_en])
                }

                result.packageArchivingAgencies = []
                object.paas.each { PackageArchivingAgency paa ->
                    result.packageArchivingAgencies.add([archivingAgency       : paa.archivingAgency?.value,
                                                         openAccess            : paa.openAccess?.value,
                                                         postCancellationAccess: paa.postCancellationAccess?.value])
                }

                result.vendors = []

                object.vendors?.each {
                    result.vendors.add([vendor: it.vendor.name,
                                        vendorUuid: it.vendor.uuid])
                }
            }


            result
        }else if(object.class.name == Org.class.name) {

            if(stubOnly){
                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)
            }else {

                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.abbreviatedName = object.abbreviatedName
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

                result.kbartDownloaderURL = object.kbartDownloaderURL
                result.metadataDownloaderURL = object.metadataDownloaderURL
                result.homepage = object.homepage

                result.paperInvoice = object.paperInvoice ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.managementOfCredits = object.managementOfCredits ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.processingOfCompensationPayments = object.processingOfCompensationPayments ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.individualInvoiceDesign = object.individualInvoiceDesign ? RDStore.YN_YES.value : RDStore.YN_NO.value

                result.roles = []
                object.roles.each { role ->
                    result.roles.add(role.value)
                }

                result.altname = []

                object.variantNames.each { vn ->
                    result.altname.add(vn.variantName)
                }

                if (object.hasProperty('curatoryGroups')) {
                    result.curatoryGroups = []
                    object.curatoryGroups?.each {
                        result.curatoryGroups.add([name         : it.curatoryGroup.name,
                                                   type         : it.curatoryGroup.type?.value,
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
                    result.contacts.add([content    : contact.content,
                                         contentType: contact.contentType?.value,
                                         type       : contact.type?.value,
                                         language   : contact.language?.value])
                }


                result.electronicBillings = []
                object.electronicBillings.each { ProviderElectronicBilling providerElectronicBilling ->
                    result.electronicBillings.add([electronicBilling: providerElectronicBilling.electronicBilling.value])
                }

                result.invoiceDispatchs = []
                object.invoiceDispatchs.each { ProviderInvoiceDispatch providerInvoiceDispatch ->
                    result.invoiceDispatchs.add([invoiceDispatch: providerInvoiceDispatch.invoiceDispatch.value])
                }

                 result.invoicingVendors = []
                object.invoicingVendors.each { ProviderInvoicingVendor providerInvoicingVendor ->
                    result.invoicingVendors.add([name: providerInvoicingVendor.vendor.name])
                }
            }

            result
        }else if(object.class.name == Platform.class.name) {

            if(stubOnly){
                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)
            }else {

                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

                result.cpname = object.provider?.name
                result.provider = object.provider ? object.provider.getOID() : ""
                result.providerName = object.provider ? object.provider.name : ""
                result.providerUuid = object.provider ? object.provider.uuid : ""

                result.primaryUrl = object.primaryUrl

                result.titleNamespace = object.titleNamespace?.value

                result.lastAuditDate = object.lastAuditDate ? DateUtils.getSDF_ISO().format(object.lastAuditDate) : ""

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
                result.counterR5SushiPlatform = object.counterR5SushiPlatform

                if (object.hasProperty('curatoryGroups')) {
                    result.curatoryGroups = []
                    object.curatoryGroups?.each {
                        result.curatoryGroups.add([name         : it.curatoryGroup.name,
                                                   type         : it.curatoryGroup.type?.value,
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
                    result.federations.add([federation: platformFederation.federation.value])
                }
            }

            result
        }else if(object.class.name == TitleInstancePackagePlatform.class.name) {
            if(stubOnly){
                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)
            }else {

                //Long start = System.currentTimeMillis()
                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

                if (object.pkg) {
                    result.tippPackage = object.pkg.getOID()
                    result.tippPackageName = object.pkg.name
                    result.tippPackageUuid = object.pkg.uuid
                }

                if (object.hostPlatform) {
                    // !!!!! observe closely! Danger of session mismatches and performance bottlenecks!!!!!
                    Platform hostPlatform = (Platform) GrailsHibernateUtil.unwrapIfProxy(object.hostPlatform)
                    result.hostPlatform = hostPlatform.getOID()
                    result.hostPlatformName = hostPlatform.name
                    result.hostPlatformUuid = hostPlatform.uuid
                }

                result.titleType = object.getTitleType() ?: 'Unknown'
                result.url = object.url

                result.dateFirstOnline = object.dateFirstOnline ? DateUtils.getSDF_ISO().format(object.dateFirstOnline) : ""
                result.dateFirstInPrint = object.dateFirstInPrint ? DateUtils.getSDF_ISO().format(object.dateFirstInPrint) : ""
                result.accessStartDate = object.accessStartDate ? DateUtils.getSDF_ISO().format(object.accessStartDate) : ""
                result.accessEndDate = object.accessEndDate ? DateUtils.getSDF_ISO().format(object.accessEndDate) : ""
                result.lastChangedExternal = object.lastChangedExternal ? DateUtils.getSDF_ISO().format(object.lastChangedExternal) : ""

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
                        if (tcs.startDate) cst.startDate = DateUtils.getSDF_ISO().format(tcs.startDate)
                        cst.startVolume = tcs.startVolume ?: ""
                        cst.startIssue = tcs.startIssue ?: ""
                        if (tcs.endDate) cst.endDate = DateUtils.getSDF_ISO().format(tcs.endDate)
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
                    price.startDate = DateUtils.getSDF_ISO().format(p.startDate)
                }
                if (p.endDate){
                    price.endDate = DateUtils.getSDF_ISO().format(p.endDate)
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
                    result.curatoryGroups.add([name         : it.curatoryGroup.name,
                                               type         : it.curatoryGroup.type?.value,
                                               curatoryGroup: it.curatoryGroup.getOID()])
                }
                //log.debug("record finished after ${System.currentTimeMillis()-start} msecs")
            }
            result
        }else if(object.class.name == DeletedKBComponent.class.name) {

            result.uuid = object.uuid
            result.name = object.name
            result.componentType = object.componentType
            result.status = object.status.value
            result.dateCreated = DateUtils.getSDF_ISO().format(object.dateCreated)
            result.lastUpdated = DateUtils.getSDF_ISO().format(object.lastUpdated)
            result.oldDateCreated = DateUtils.getSDF_ISO().format(object.oldDateCreated)
            result.oldLastUpdated = DateUtils.getSDF_ISO().format(object.oldLastUpdated)
            result.oldId = object.oldId
            result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)

            result
        }else if(object.class.name == Vendor.class.name) {

            if(stubOnly){
                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)
            }else {

                result.uuid = object.uuid
                result.name = object.name
                result.sortname = generateSortName(object.name)
                result.abbreviatedName = object.abbreviatedName
                result.status = object.status?.value
                result.componentType = object.class.simpleName

                result.lastUpdatedDisplay = DateUtils.getSDF_ISO().format(object.lastUpdated)
                result.dateCreatedDisplay = DateUtils.getSDF_ISO().format(object.dateCreated)

                result.homepage = object.homepage

                result.webShopOrders = object.webShopOrders ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.xmlOrders = object.xmlOrders ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.ediOrders = object.ediOrders ? RDStore.YN_YES.value : RDStore.YN_NO.value

                result.paperInvoice = object.paperInvoice ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.managementOfCredits = object.managementOfCredits ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.processingOfCompensationPayments = object.processingOfCompensationPayments ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.individualInvoiceDesign = object.individualInvoiceDesign ? RDStore.YN_YES.value : RDStore.YN_NO.value

                result.technicalSupport = object.technicalSupport ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.shippingMetadata = object.shippingMetadata ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.forwardingUsageStatisticsFromPublisher = object.forwardingUsageStatisticsFromPublisher ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.activationForNewReleases = object.activationForNewReleases ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.exchangeOfIndividualTitles = object.exchangeOfIndividualTitles ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.researchPlatformForEbooks = object.researchPlatformForEbooks
                result.prequalificationVOL = object.prequalificationVOL ? RDStore.YN_YES.value : RDStore.YN_NO.value
                result.prequalificationVOLInfo = object.prequalificationVOLInfo

                result.roles = []
                object.roles.each { role ->
                    result.roles.add(role.value)
                }


                if (object.hasProperty('curatoryGroups')) {
                    result.curatoryGroups = []
                    object.curatoryGroups?.each {
                        result.curatoryGroups.add([name         : it.curatoryGroup.name,
                                                   type         : it.curatoryGroup.type?.value,
                                                   curatoryGroup: it.curatoryGroup.getOID()])
                    }
                }

                result.contacts = []
                object.contacts.each { Contact contact ->
                    result.contacts.add([content    : contact.content,
                                         contentType: contact.contentType?.value,
                                         type       : contact.type?.value,
                                         language   : contact.language?.value])
                }


                result.packages = []

                object.packages?.each {
                    result.packages.add([package: it.pkg.name,
                                         packageUuid: it.pkg.uuid])
                }

                result.supportedLibrarySystems = []
                object.supportedLibrarySystems.each { VendorLibrarySystem vendorLibrarySystem ->
                    result.supportedLibrarySystems.add([supportedLibrarySystem: vendorLibrarySystem.supportedLibrarySystem.value])
                }

                result.electronicBillings = []
                object.electronicBillings.each { VendorElectronicBilling vendorElectronicBilling ->
                    result.electronicBillings.add([electronicBilling: vendorElectronicBilling.electronicBilling.value])
                }

                result.invoiceDispatchs = []
                object.invoiceDispatchs.each { VendorInvoiceDispatch vendorInvoiceDispatch ->
                    result.invoiceDispatchs.add([invoiceDispatch: vendorInvoiceDispatch.invoiceDispatch.value])
                }

                result.electronicDeliveryDelays = []
                object.electronicDeliveryDelays.each { VendorElectronicDeliveryDelay vendorElectronicDeliveryDelay ->
                    result.electronicDeliveryDelays.add([electronicDeliveryDelay: vendorElectronicDeliveryDelay.electronicDeliveryDelay.value])
                }
            }

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
                case Vendor.class.name.toLowerCase():
                    result = 'vendors'
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

        if(globalSearchComponentType == 'tipps') {
            Sql sql = new Sql(Holders.grailsApplication.mainContext.getBean('dataSource') as DataSource)
            Map apiSearchTemplate = getApiSqlTemplate('tipps'), searchResult = [:]

            switch(params.sort) {
                case 'dateCreated': searchResult.sort = 'tipp_date_created'
                    break
                case 'lastUpdated': searchResult.sort = 'tipp_last_updated'
                    break
                case 'name': searchResult.sort = 'tipp_name'
                    break
                case 'status': searchResult.sort = 'tipp_status_rv_fk'
                    break
                default: searchResult.sort = apiSearchTemplate.defaultSort
                    break
            }
            searchResult.order = params.order ?: apiSearchTemplate.defaultOrder

            searchResult.max = params.max ? Integer.parseInt(params.max) : 10
            searchResult.offset = params.offset ? Integer.parseInt(params.offset) : 0

            Set<String> sqlCols = apiSearchTemplate.qbeConfig.sqlCols.stubOnly.collect { String jsonCol, String sqlCol -> "${sqlCol} as \"${jsonCol}\"" },
            coverageCols = [],
            identifierCols = [],
            priceCols = [],
            ddcCols = [],
            languageCols = [],
            curatoryGroupFields = []

            if(!params.containsKey('stubOnly')) {
                sqlCols.addAll(apiSearchTemplate.qbeConfig.sqlCols.principalObject.collect { String jsonCol, String sqlCol -> "${sqlCol} as \"${jsonCol}\"" })
                coverageCols.addAll(apiSearchTemplate.qbeConfig.sqlCols.coverageFields.collect { String jsonCol, String sqlCol -> "'${jsonCol}', ${sqlCol}" })
                identifierCols.addAll(apiSearchTemplate.qbeConfig.sqlCols.identifierFields.collect { String jsonCol, String sqlCol -> "'${jsonCol}', ${sqlCol}" })
                priceCols.addAll(apiSearchTemplate.qbeConfig.sqlCols.priceFields.collect { String jsonCol, String sqlCol -> "'${jsonCol}', ${sqlCol}" })
                ddcCols.addAll(apiSearchTemplate.qbeConfig.sqlCols.ddcFields.collect { String jsonCol, String sqlCol -> "'${jsonCol}', ${sqlCol}" })
                languageCols.addAll(apiSearchTemplate.qbeConfig.sqlCols.languageFields.collect { String jsonCol, String sqlCol -> "'${jsonCol}', ${sqlCol}" })
                curatoryGroupFields.addAll(apiSearchTemplate.qbeConfig.sqlCols.curatoryGroupFields.collect { String jsonCol, String sqlCol -> "'${jsonCol}', ${sqlCol}" })
            }

            GrailsParameterMap cleaned_params = processCleanParameterMap(params)
            String whereClause = ""
            Set<String> clauseParts = []
            Map<String, Object> sqlParams = [:]
            apiSearchTemplate.qbeConfig.qbeForm.each { Map fieldMap ->
                if(cleaned_params.containsKey(fieldMap.qparam)) {
                    clauseParts << fieldMap.whereClause
                    if(cleaned_params[fieldMap.qparam] instanceof List) {
                        if(cleaned_params[fieldMap.qparam][0].contains('wekb.RefdataValue'))
                            sqlParams[fieldMap.qparam] = sql.getDataSource().getConnection().createArrayOf('bigint', cleaned_params[fieldMap.qparam].collect { String rv -> genericOIDService.resolveOID(rv).id }.toArray() as Object[] )
                        else if(cleaned_params[fieldMap.qparam][0] instanceof String)
                            sqlParams[fieldMap.qparam] = sql.getDataSource().getConnection().createArrayOf('varchar', cleaned_params[fieldMap.qparam].toArray() as Object[])
                        else if(cleaned_params[fieldMap.qparam][0] instanceof Long)
                            sqlParams[fieldMap.qparam] = sql.getDataSource().getConnection().createArrayOf('bigint', cleaned_params[fieldMap.qparam].toArray() as Object[])
                    }
                    else if(fieldMap.qparam in ['changedSince', 'changedBefore']) {
                        sqlParams[fieldMap.qparam] = DateUtils.parseDateGeneric(cleaned_params[fieldMap.qparam]).toTimestamp()
                    }
                    else
                        sqlParams[fieldMap.qparam] = cleaned_params[fieldMap.qparam]
                }
            }
            whereClause += "where ${clauseParts.join(' and ')}"
            String sqlCount = "select count(*) as reccount from title_instance_package_platform join package on tipp_pkg_fk = pkg_id join platform on tipp_host_platform_fk = plat_id ${whereClause}"
            searchResult.reccount = sql.rows(sqlCount, sqlParams)[0]['reccount']
            result.result = []
            String mainClause = "from title_instance_package_platform join package on tipp_pkg_fk = pkg_id join platform on tipp_host_platform_fk = plat_id ${whereClause}"
            String sqlQuery = "select tipp_id, tipp_pkg_fk, ${sqlCols.join(', ')} ${mainClause} order by ${searchResult.sort} ${searchResult.order} limit ${searchResult.max} offset ${searchResult.offset}"
            //log.debug(sqlQuery)
            String coverageQuery = "select tcs_tipp_fk, json_agg(json_build_object(${coverageCols.join(', ')})) as cov from tippcoverage_statement where tcs_tipp_fk = any(:ids) group by tcs_tipp_fk",
            identifierQuery = "select id_tipp_fk, json_agg(json_build_object(${identifierCols.join(', ')})) as ids from identifier join identifier_namespace on id_namespace_fk = idns_id where id_tipp_fk = any(:ids) group by id_tipp_fk",
            priceQuery = "select tp_tipp_fk, json_agg(json_build_object(${priceCols.join(', ')})) as prices from tipp_price where tp_tipp_fk = any(:ids) group by tp_tipp_fk",
            ddcQuery = "select tipp_fk, json_agg(json_build_object(${ddcCols.join(', ')})) as ddcs from refdata_value join tipp_dewey_decimal_classification on rdv_id = ddc_rv_fk where tipp_fk = any(:ids) group by tipp_fk",
            languageQuery = "select cl_tipp_fk, json_agg(json_build_object(${languageCols.join(', ')})) as lang from refdata_value join component_language on rdv_id = cl_rv_fk where cl_tipp_fk = any(:ids) group by cl_tipp_fk",
            curatoryGroupQuery = "select cgp_pkg_fk, json_agg(json_build_object(${curatoryGroupFields.join(', ')})) as cg from curatory_group join curatory_group_package on cgp_curatory_group_fk = cg_id where cgp_pkg_fk = any(:pkgIds) group by cgp_pkg_fk"

            log.debug("Execute queries")
            List<GroovyRowResult> principalRows = sql.rows(sqlQuery, sqlParams)
            List tippIds = principalRows.collect{ GroovyRowResult row -> row['tipp_id'] }, tippPkgIds = principalRows.collect{ GroovyRowResult row -> row['tipp_pkg_fk'] }
            Map<String, Object> tippIdParams = [ids: sql.getDataSource().getConnection().createArrayOf('bigint', tippIds.toArray())],
            tippPkgParams = [pkgIds: sql.getDataSource().getConnection().createArrayOf('bigint', tippPkgIds.toArray())]
            JsonSlurper slurper = new JsonSlurper()
            Map<String, Map> coverageMap = sql.rows(coverageQuery, tippIdParams).collectEntries { GroovyRowResult row -> [row['tcs_tipp_fk'], slurper.parseText(row['cov'].toString())] }
            Map<String, Map> identifierMap = sql.rows(identifierQuery, tippIdParams).collectEntries { GroovyRowResult row -> [row['id_tipp_fk'], slurper.parseText(row['ids'].toString())] }
            Map<String, Map> priceMap = sql.rows(priceQuery, tippIdParams).collectEntries { GroovyRowResult row -> [row['tp_tipp_fk'], slurper.parseText(row['prices'].toString())] }
            Map<String, Map> ddcMap = sql.rows(ddcQuery, tippIdParams).collectEntries { GroovyRowResult row -> [row['tipp_fk'], slurper.parseText(row['ddcs'].toString())] }
            Map<String, Map> langMap = sql.rows(languageQuery, tippIdParams).collectEntries { GroovyRowResult row -> [row['cl_tipp_fk'], slurper.parseText(row['lang'].toString())] }
            Map<String, Map> curatoryGroupMap = sql.rows(curatoryGroupQuery, tippPkgParams).collectEntries { GroovyRowResult row -> [row['cgp_pkg_fk'], slurper.parseText(row['cg'].toString())] }
            principalRows.eachWithIndex { GroovyRowResult row, int i ->
                //long startInner = System.currentTimeMillis()
                row['sortname'] = generateSortName(row['name'])
                if(row.containsKey('publicationType') && row['publicationType'] == RDStore.TIPP_PUBLIC_TYPE_SERIAL.id) {
                    row['coverage'] = coverageMap.get(row['tipp_id'])
                }
                if(!params.containsKey('stubOnly')) {
                    //log.debug("${identifierQuery.replace(':tippId',tippId.tippId.toString())}")
                    row['identifiers'] = identifierMap.get(row['tipp_id'])
                    //log.debug("${priceQuery.replace(':tippId',tippId.tippId.toString())}")
                    row['prices'] = priceMap.get(row['tipp_id'])
                    //log.debug("${ddcQuery.replace(':tippId',tippId.tippId.toString())}")
                    row['ddcs'] = ddcMap.get(row['tipp_id'])
                    //log.debug("${languageQuery.replace(':tippId',tippId.tippId.toString())}")
                    row['languages'] = langMap.get(row['tipp_id'])
                    row['curatoryGroups'] = curatoryGroupMap.get(row['tipp_pkg_fk'])
                }
                result.result << row
                //log.debug("recset ${i} added after ${System.currentTimeMillis()-startInner}")
            }

            log.debug("Query complete")

            //Add information to result
            result.result_count_total = searchResult.reccount
            result.result_count = result.result.size()
            result.sort = searchResult.sort
            result.order = searchResult.order
            result.offset = searchResult.offset
            result.max = searchResult.max
            result.page_current = (searchResult.offset / searchResult.max) + 1
            result.page_total = (searchResult.reccount / searchResult.max).toInteger() + (searchResult.reccount % searchResult.max > 0 ? 1 : 0)

            if(params.stubOnly) {
                result.stubOnly = params.stubOnly
            }
            def searchTime = System.currentTimeMillis() - start_time

            result.searchTime = searchTime + ' ms'
            log.debug("Search completed after ${searchTime}")
        }
        else if (globalSearchComponentType) {
            def searchResult = [:]
            def target_class
            Map apiSearchTemplate = getApiTemplate(globalSearchComponentType)
            if (apiSearchTemplate) {

                params.sort = params.sort ?: apiSearchTemplate.defaultSort
                params.order = params.order ?: apiSearchTemplate.defaultOrder

                searchResult.max = params.max ? Integer.parseInt(params.max) : 10
                searchResult.offset = params.offset ? Integer.parseInt(params.offset) : 0

                log.debug("Execute query")
                GrailsParameterMap cleaned_params = processCleanParameterMap(params)

                target_class = grailsApplication.getArtefact("Domain", apiSearchTemplate.baseclass)
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

                if(params.stubOnly) {
                    result.stubOnly = params.stubOnly
                }

            } else {
                log.error("no template ${apiSearchTemplate}")
            }

            result.result = []
            log.debug("Create result..")

           /* GParsPool.withPool(4) {
                searchResult.recset.eachParallel { r ->
                    KBComponent.withTransaction {
                        //LinkedHashMap<Object, Object> resultMap = mapDomainFieldsToSpecFields2(apiSearchTemplate, r)
                        LinkedHashMap<Object, Object> resultMap = mapDomainFieldsToSpecFields(r, (params.stubOnly ? true : false))

                        if(params.sortFields){
                            resultMap = resultMap.sort { Map subResult -> subResult.key }
                        }

                        result.result.add(resultMap)
                    }
                }
            }*/

            searchResult.recset.each { r ->
                KBComponent.withTransaction {
                    if(params.sort in complexSortFields){
                        r = r[0]
                    }
                    //LinkedHashMap<Object, Object> resultMap = mapDomainFieldsToSpecFields2(apiSearchTemplate, r)
                    LinkedHashMap<Object, Object> resultMap = mapDomainFieldsToSpecFields(r, (params.stubOnly ? true : false))

                    if(params.sortFields){
                        resultMap = resultMap.sort { Map subResult -> subResult.key }
                    }

                    result.result.add(resultMap)
                }
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
                r = Vendor.findByUuid(params.uuid)
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

        if(parameterMap.status.contains(',')){
            List listOfStatus = parameterMap.status.split(',')
            parameterMap.status = listOfStatus
        }

        setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'status', 'status')
        return
    }

    private void processSimpleFields(GrailsParameterMap cleaned_params, GrailsParameterMap parameterMap) {
        if (parameterMap.uuid){
            cleaned_params.put('uuid', parameterMap.uuid)
        }

        if (parameterMap.uuids){
            ArrayList<String> uuids = parameterMap.list('uuids') as ArrayList<String>
            cleaned_params.put('uuids', uuids)
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

        if (parameterMap.automaticUpdates){
            cleaned_params.put('automaticUpdates', parameterMap.boolean('automaticUpdates'))
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
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'ddc', 'ddc')
        }
        else if(parameterMap.ddcs) {
            setRefdataValueFromGrailsParameterMap(cleaned_params, parameterMap, 'ddc', 'ddcs')
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

        if(parameterMap.counterSushiSupport) {
            List counterVersions = parameterMap.list('counterSushiSupport')
            if('counter4' in counterVersions)
                cleaned_params.put('qp_counterR4SushiApiSupported', GenericOIDService.getOID(RDStore.YN_YES))
            if('counter5' in counterVersions)
                cleaned_params.put('qp_counterR5SushiApiSupported', GenericOIDService.getOID(RDStore.YN_YES))
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
            counter5Platforms = Platform.executeQuery("from Platform plat where plat.counterR5SushiApiSupported = :r5support and (plat.counterRegistryApiUuid is not null or plat.counterR5SushiServerUrl is not null) and plat.uuid = :uuid", [r5support: yes, uuid: params.uuid]).toSet()
        }else {
            counter4Platforms = Platform.executeQuery("from Platform plat where plat.counterR4SushiApiSupported = :r4support and plat.counterR5SushiApiSupported != :r5support and plat.counterR4SushiServerUrl is not null", [r4support: yes, r5support: yes]).toSet()
            counter5Platforms = Platform.executeQuery("from Platform plat where plat.counterR5SushiApiSupported = :r5support and (plat.counterRegistryApiUuid is not null or plat.counterR5SushiServerUrl is not null)", [r5support: yes]).toSet()
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
            result.counter5ApiSources."${platform.uuid}".counterR5SushiPlatform = platform.counterR5SushiPlatform

        }

        result
    }
}

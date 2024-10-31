package wekb


import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovy.sql.Sql
import org.grails.web.json.JSONArray
import org.hibernate.Session

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Transactional
class KbartImportService {

    CleanupService cleanupService

    DateFormatService dateFormatService


    /*Platform platformUpsertDTO(platformDTO) {
        // Ideally this should be done on platformUrl, but we fall back to name here

        def result = null
        Boolean skip = false
        RefdataValue status_current = RDStore.KBC_STATUS_CURRENT
        RefdataValue status_deleted = RDStore.KBC_STATUS_DELETED
        ArrayList name_candidates = []
        ArrayList url_candidates = []
        Boolean changed = false
        Boolean viable_url = false

        if(platformDTO.oid) {
            List platform_id_components = platformDTO.oid.split(':')
            if (platform_id_components.size() == 2) {
                result = Platform.get(Long.parseLong(platform_id_components[1].trim()))
            }
        }
        if (platformDTO.uuid) {
            result = Platform.findByUuid(platformDTO.uuid)
        }
        if (result) {
            //changed |= com.k_int.ClassUtils.setStringIfDifferent(result, 'name', platformDTO.name)
        } else {
            if (platformDTO.name.startsWith("http")) {
                try {
                    log.debug("checking if platform name is an URL..")

                    def url_as_name = new URL(platformDTO.name)

                    if (url_as_name.getProtocol()) {
                        if (!platformDTO.primaryUrl || !platformDTO.primaryUrl.trim()) {
                            log.debug("identified URL as platform name")
                            platformDTO.primaryUrl = platformDTO.name
                        }
                        platformDTO.name = url_as_name.getHost()

                        if (platformDTO.name.startsWith("www.")) {
                            platformDTO.name = platformDTO.name.substring(4)
                        }

                        log.debug("New platform name is ${platformDTO.name}.")
                    }
                } catch (MalformedURLException) {
                    log.debug("Platform name is no valid URL")
                }
            }

            name_candidates = Platform.executeQuery("from Platform where name = :name and status != :status ", [name: platformDTO.name, status: status_deleted])

            if (name_candidates.size() == 0) {
                log.debug("No platforms matched by name!")

                def variant_normname = TextUtils.normaliseString(platformDTO.name)

                def varname_candidates = Platform.executeQuery("select distinct pl from Platform as pl join pl.variantNames as v where v.normVariantName = :variantName and pl.status = :status ", [variantName: variant_normname, status: status_current])

                if (varname_candidates.size() == 1) {
                    log.debug("Platform matched by variant name!")
                    result = varname_candidates[0]
                }

            } else if (name_candidates.size() == 1 && name_candidates[0].status == status_current) {
                log.debug("Platform ${platformDTO.name} matched by name!")
                result = name_candidates[0]
            } else {
                log.warn("Could not match a specific current platform for ${platformDTO.name}!")
            }

            if (!result && platformDTO.primaryUrl && platformDTO.primaryUrl.trim().size() > 0) {
                try {
                    def inc_url = new URL(platformDTO.primaryUrl)
                    def other_candidates = []

                    if (inc_url) {
                        viable_url = true
                        String urlHost = inc_url.getHost()

                        if (urlHost.startsWith("www.")) {
                            urlHost = urlHost.substring(4)
                        }

                        def platform_crit = Platform.createCriteria()

                        //TODO: MOE Matching
                        url_candidates = platform_crit.list {
                            or {
                                like("name", "${urlHost}")
                                like("primaryUrl", "%${urlHost}%")
                            }
                        }
                    }
                } catch (MalformedURLException ex) {
                    log.error("URL of ingest Platform ${platformDTO} is broken!")
                }
            }

            if (!result && viable_url) {
                log.debug("Trying to match platform by primary URL..")

                if (url_candidates.size() == 0) {
                    log.debug("Could not match an existing platform!")
                } else if (url_candidates.size() == 1) {
                    log.debug("Matched existing platform by URL!")
                    result = url_candidates[0]
                } else if (url_candidates.size() > 1) {
                    log.warn("Matched multiple platforms by URL!")

                    def current_platforms = url_candidates.findAll { it.status == status_current }

                    if (current_platforms.size() == 1) {
                        result = current_platforms[0]
                    } else if (current_platforms.size() == 0) {
                        log.error("Matched only non-current platforms by URL!")
                        result = url_candidates[0]
                    } else {

                        // Picking randomly from multiple results is bad, but right now a result is always expected. Maybe this should be skipped...
                        // skip = true

                        log.error("Multiple matched current platforms: ${current_platforms}")
                        result = current_platforms[0]
                    }
                }
            }

            if (result && !result.primaryUrl) {
                result.primaryUrl = platformDTO.primaryUrl
                result.save()
            }

            *//*if (!result && !skip) {
                log.debug("Creating new platform for: ${platformDTO}")
                result = new Platform(name: platformDTO.name, normname: KBComponent.generateNormname(platformDTO.name), primaryUrl: (viable_url ? platformDTO.primaryUrl : null), uuid: platformDTO.uuid ?: null).save(flush: true, failOnError: true)
                
            }*//*
        }

        result
    }*/

/*    Package packageUpsertDTO(packageHeaderDTO) {
        log.info("Upsert package with header ${packageHeaderDTO}")
        RefdataValue status_deleted = RDStore.KBC_STATUS_DELETED
        String pkg_normname = Package.generateNormname(packageHeaderDTO.name)

        //log.debug("Checking by normname ${pkg_normname} ..")
        //List name_candidates = Package.executeQuery("from Package as p where p.normname = ? and p.status <> ?", [pkg_normname, status_deleted])
        //ArrayList full_matches = []
        //Boolean created = false
        Package result = packageHeaderDTO.uuid ? Package.findByUuid(packageHeaderDTO.uuid) : null
        boolean changed = false

        *//*if (!result && name_candidates.size() > 0 && packageHeaderDTO.identifiers?.size() > 0) {
            log.debug("Got ${name_candidates.size()} matches by name. Checking against identifiers!")
            name_candidates.each { mp ->
                if (mp.ids.size() > 0) {
                    def id_match = false

                    packageHeaderDTO.identifiers.each { rid ->

                        //TODO: MOE
                        IdentifierNamespace namespace = IdentifierNamespace.findByValue(rid.type)
                        Identifier the_id = namespace ? Identifier.findByValueAndNamespace(rid.value, namespace) : null

                        if (the_id && mp.ids.contains(the_id)) {
                            id_match = true
                        }
                    }

                    if (id_match && !full_matches.contains(mp)) {
                        full_matches.add(mp)
                    }
                }
            }

            if (full_matches.size() == 1) {
                log.debug("Matched package by name + identifier!")
                result = full_matches[0]
            }
            else if (full_matches.size() == 0 && name_candidates.size() == 1) {
                result = name_candidates[0]
                log.debug("Found a single match by name!")
            }
            else {
                log.warn("Found multiple possible matches for package! Aborting..")
                return result
            }
        }
        else if (!result && name_candidates.size() == 1) {
            log.debug("Matched package by name!")
            result = name_candidates[0]
        }
        else if (result && result.name != packageHeaderDTO.name) {
            def current_name = result.name

            changed |= ClassUtils.setStringIfDifferent(result, 'name', packageHeaderDTO.name)


        }*//*

*//*        if (!result) {
            log.debug("Did not find a match via name, trying existing variantNames..")
            def variant_normname = TextUtils.normaliseString(packageHeaderDTO.name)
            def variant_candidates = Package.executeQuery("select distinct p from Package as p join p.variantNames as v where v.normVariantName = ? and p.status <> ? ", [variant_normname, status_deleted])

            if (variant_candidates.size() == 1) {
                result = variant_candidates[0]
                log.debug("Package matched via existing variantName.")
            }
        }*//*

        //variantNames not in ygorJson
        *//*if (!result && packageHeaderDTO.variantNames?.size() > 0) {
            log.debug("Did not find a match via existing variantNames, trying supplied variantNames..")
            packageHeaderDTO.variantNames.each {

                if (it.trim().size() > 0) {
                    result = Package.findByName(it)

                    if (result) {
                        log.debug("Found existing package name for variantName ${it}")
                    }
                    else {
                        def variant_normname = TextUtils.normaliseString(it)
                        def variant_candidates = Package.executeQuery("select distinct p from Package as p join p.variantNames as v where v.normVariantName = ? and p.status <> ? ", [variant_normname, status_deleted])
                        if (variant_candidates.size() == 1) {
                            log.debug("Found existing package variant name for variantName ${it}")
                            result = variant_candidates[0]
                        }
                    }
                }
            }
        }*//*

*//*        if (!result) {
            log.debug("No existing package matched. Creating new package..")
            result = new Package(name: packageHeaderDTO.name, normname: pkg_normname)
            created = true
            if (packageHeaderDTO.uuid && packageHeaderDTO.uuid.trim().size() > 0) {
                result.uuid = packageHeaderDTO.uuid
            }
            result.save(flush: true, failOnError: true)
        }
        else if (user && !user.hasRole('ROLE_SUPERUSER') && result.curatoryGroups && result.curatoryGroups?.size() > 0) {
            def cur = user.curatoryGroups?.id.intersect(result.curatoryGroups?.id)
            if (!cur) {
                log.debug("No curator!")
                return result
            }
        }*//*

        //not in ygorJson
        *//*changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.status, result, 'status')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.scope, result, 'scope')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.breakable, result, 'breakable')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.consistent, result, 'consistent')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.paymentType, result, 'paymentType')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.file, result, 'file')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.openAccess, result, 'openAccess')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.contentType, result, 'contentType')*//*

        // Platform
        Platform platform = null
        if (packageHeaderDTO.nominalPlatform && packageHeaderDTO.nominalPlatform instanceof Map) {
            platform = platformUpsertDTO(packageHeaderDTO.nominalPlatform)
            if (result && result.nominalPlatform == null) {
                result.nominalPlatform = platform
                changed = true
            }
                    else {
                        log.debug("Platform already set")
                    }
        } else {
            log.warn("Could not extract platform information from JSON! ${packageHeaderDTO.nominalPlatform}")
        }

        // Provider
        if (packageHeaderDTO.nominalProvider && packageHeaderDTO.nominalProvider instanceof Map) {

            log.debug("Trying to set package provider.. ${packageHeaderDTO.nominalProvider}")
            Org prov = null
            if (packageHeaderDTO.nominalProvider.uuid) {
                prov = Org.findByUuid(packageHeaderDTO.nominalProvider.uuid)
            }

            if(packageHeaderDTO.nominalProvider.oid) {
                List org_id_components = packageHeaderDTO.nominalProvider.oid.split(':')
                if (org_id_components.size() == 2) {
                    prov = Org.get(Long.parseLong(org_id_components[1].trim()))
                }
            }

            if (!prov && packageHeaderDTO.nominalProvider.name) {
                def norm_prov_name = KBComponent.generateNormname(packageHeaderDTO.nominalProvider.name)
                prov = Org.findByNormname(norm_prov_name)
                if (!prov) {
                    log.debug("None found by Normname ${norm_prov_name}, trying variants")
                    def variant_normname = TextUtils.normaliseString(packageHeaderDTO.nominalProvider.name)
                    def candidate_orgs = Org.executeQuery("select distinct o from Org as o join o.variantNames as v where v.normVariantName = :normName and o.status != :status", [normName: variant_normname, status: status_deleted])
                    if (candidate_orgs.size() == 1) {
                        prov = candidate_orgs[0]
                    }
                    else if (candidate_orgs.size() == 0) {
                        log.debug("No org match for provider ${packageHeaderDTO.nominalProvider}. Creating new org..")
                        prov = new Org(name: packageHeaderDTO.nominalProvider.name, normname: norm_prov_name, uuid: packageHeaderDTO.nominalProvider.uuid ?: null)
                        prov.save()
                    }
                    else {
                        log.warn("Multiple org matches for provider ${packageHeaderDTO.nominalProvider}. Skipping..")
                    }
                }
            }

            if (result && prov) {
                if (result.provider == null) {
                    result.provider = prov
                    log.debug("Provider ${prov.name} set.")
                    changed = true
                }
                else {
                    log.debug("No provider change")
                }
            }
        }
        else {
            log.warn("Could not extract nominalProvider information from JSON! ${packageHeaderDTO.nominalProvider}")
        }

        // CuratoryGroups
*//*        if(packageHeaderDTO.curatoryGroups) {
            packageHeaderDTO.curatoryGroups.each {
                def cg = null
                def cgname = null

                if (it instanceof Integer) {
                    cg = CuratoryGroup.get(it)
                } else if (it instanceof String) {
                    String normname = CuratoryGroup.generateNormname(it)
                    cgname = it
                    cg = CuratoryGroup.findByNormname(normname)
                } else if (it.id) {
                    cg = CuratoryGroup.get(it.id)
                } else if (it.name) {
                    String normname = CuratoryGroup.generateNormname(it.name)
                    cgname = it.name
                    cg = CuratoryGroup.findByNormname(normname)
                }
                if (cg) {
                    if (result.curatoryGroups.find { it.id == cg.id }) {
                    } else {
                        result.curatoryGroups.add(cg)
                        changed = true
                    }
                } else if (cgname) {
                    def new_cg = new CuratoryGroup(name: cgname).save(flush: true, failOnError: true)
                    result.curatoryGroups.add(new_cg)
                    changed = true
                }
            }
        }*//*

        *//*if (packageHeaderDTO.source) {
            def src = null
            if (packageHeaderDTO.source instanceof Integer) {
                src = KbartSource.get(packageHeaderDTO.source)
            }
            else if (packageHeaderDTO.source instanceof Map) {
                def sourceMap = packageHeaderDTO.source
                if (sourceMap.id) {
                    src = KbartSource.get(sourceMap.id)
                }
                else {
                    def namespace = null
                    if (sourceMap.targetNamespace instanceof Integer) {
                        namespace = IdentifierNamespace.get(sourceMap.targetNamespace)
                    }
                    if (!result.source || result.source.name != result.name) {
                        def source_config = [
                                name           : result.name,
                                url            : sourceMap.url,
                                frequency      : sourceMap.frequency,
                                ezbMatch       : (sourceMap.ezbMatch ?: false),
                                zdbMatch       : (sourceMap.zdbMatch ?: false),
                                automaticUpdate: (sourceMap.automaticUpdate ?: false),
                                targetNamespace: namespace
                        ]
                        src = new KbartSource(source_config).save(flush: true)
                        result.curatoryGroups.each { cg ->
                            src.curatoryGroups.add(cg)
                        }
                    }
                    else {
                        src = result.source
                        changed |= ClassUtils.setStringIfDifferent(src, 'frequency', sourceMap.frequency)
                        changed |= ClassUtils.setStringIfDifferent(src, 'url', sourceMap.url)
                        changed |= ClassUtils.setBooleanIfDifferent(src, 'ezbMatch', sourceMap.ezbMatch)
                        changed |= ClassUtils.setBooleanIfDifferent(src, 'zdbMatch', sourceMap.zdbMatch)
                        changed |= ClassUtils.setBooleanIfDifferent(src, 'automaticUpdate', sourceMap.automaticUpdate)
                        if (namespace && namespace != src.targetNamespace) {
                            src.targetNamespace = namespace
                            changed = true
                        }
                        src.save(flush: true)
                    }
                }
            }
            if (src && result.source != src) {
                result.source = src
                changed = true
            }
        }*//*

        *//*if (packageHeaderDTO.ddcs) {
            packageHeaderDTO.ddcs.each{ String ddc ->
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, ddc)

                if(refdataValue && !(refdataValue in result.ddcs)){
                    result.addToDdcs(refdataValue)
                }
            }
        }*//*

        result.save()
        result
    }*/

    /*TitleInstancePackagePlatform tippUpsertDTO(tipp_dto, def user = null, LinkedHashMap tippsWithCoverage, List<Long> tippDuplicates = []) {
        def result = null
        log.info("tippUpsertDTO(${tipp_dto})")
        Package pkg = null
        Platform plt = null


        List identifierNameSpacesExistOnTipp = []

        if (tipp_dto.pkg || tipp_dto.package) {
            def pkg_info = tipp_dto.package ?: tipp_dto.pkg

            if (pkg_info instanceof Map) {
                pkg = Package.get(pkg_info.id ?: pkg_info.internalId)
            } else {
                pkg = Package.get(pkg_info)
            }

            log.debug("Package lookup: ${pkg}")
        }

        if (tipp_dto.hostPlatform || tipp_dto.platform) {
            def plt_info = tipp_dto.hostPlatform ?: tipp_dto.platform

            if (plt_info instanceof Map) {
                plt = Platform.get(plt_info.id ?: plt_info.internalId)
            } else {
                plt = Platform.get(plt_info)
            }

            log.debug("Platform lookup: ${plt}")
        }

        def status_current = RDStore.KBC_STATUS_CURRENT
        def status_retired = RDStore.KBC_STATUS_RETIRED
        def trimmed_url = tipp_dto.url ? tipp_dto.url.trim() : null

        //TODO: Moe
        def curator = pkg?.curatoryGroups?.size() > 0 ? (user.adminStatus || (user.curatoryGroupUsers && user.curatoryGroupUsers.curatoryGroup.id.intersect(pkg.curatoryGroups.curatoryGroup.id))) : false

        if (pkg && plt && curator) {
            log.debug("See if we already have a tipp")
            //and tipp.status != ? ??????
            def tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp  ' +
                    'where tipp.pkg = :pkg ' +
                    'and tipp.hostPlatform = :platform ' +
                    'and tipp.name = :tiDtoName and tipp.status != :removed order by tipp.lastUpdated DESC',
                    [pkg: pkg, platform: plt, tiDtoName: tipp_dto.name, removed: RDStore.KBC_STATUS_REMOVED])
            def uuid_tipp = tipp_dto.uuid ? TitleInstancePackagePlatform.findByUuid(tipp_dto.uuid) : null

            TitleInstancePackagePlatform tipp = null


            if (uuid_tipp && uuid_tipp.pkg == pkg && uuid_tipp.hostPlatform == plt) {
                tipp = uuid_tipp
            }

            if (!tipp) {
                if(tipps.size() == 0){
                    if (trimmed_url && trimmed_url.size() > 0) {
                        log.debug("not found Tipp with title. research in pkg ${pkg} with url")
                        tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp ' +
                                'where tipp.pkg = :pkg ' +
                                'and tipp.hostPlatform = :platform ' +
                                'and tipp.url = :url and tipp.status != :removed order by tipp.lastUpdated DESC',
                                [pkg: pkg, platform: plt, url: trimmed_url, removed: RDStore.KBC_STATUS_REMOVED])
                    }

                    if(tipps.size() == 0) {
                        log.debug("not found Tipp with title. research in pkg ${pkg} with tile_id")
                        tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp ' +
                                'where tipp.pkg = :pkg ' +
                                'and tipp.hostPlatform = :platform and tipp.status != :removed order by tipp.lastUpdated DESC',
                                [pkg: pkg, platform: plt, removed: RDStore.KBC_STATUS_REMOVED])
                    }

                }
                switch (tipps.size()) {
                    case 0:
                        log.debug("not found Tipp: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tipp_dto.name}]")
                        break
                    case 1:
                        if (trimmed_url && trimmed_url.size() > 0) {
                            if (!tipps[0].url || tipps[0].url == trimmed_url) {
                                log.debug("found tipp")
                                tipp = tipps[0]
                            } else {

                                //if url changed find tipp over title id
                                List<TitleInstancePackagePlatform> tippsMatchedByTitleID = tippsMatchingByTitleID(tipp_dto.identifiers, pkg, plt)

                                tippsMatchedByTitleID.each{ TitleInstancePackagePlatform tippByTitleID ->
                                    if (tippByTitleID.id == tipps[0].id) {
                                        log.debug("found tipp")
                                        tipp = tipps[0]
                                    } else {
                                        log.debug("not found Tipp because url changed: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tipp_dto.name}, url: ${trimmed_url}]")
                                    }
                                }
                            }
                        } else {
                            log.debug("found tipp")
                            tipp = tipps[0]
                        }
                        break
                    default:
                        if (trimmed_url && trimmed_url.size() > 0) {
                            tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp  ' +
                                    'where tipp.pkg = :pkg ' +
                                    'and tipp.hostPlatform = :platform ' +
                                    'and tipp.name = :tiDtoName and tipp.status != :removed and (tipp.url = null or tipp.url = :trimmedURL) order by tipp.lastUpdated DESC',
                                    [pkg: pkg, platform: plt, tiDtoName: tipp_dto.name, removed: RDStore.KBC_STATUS_REMOVED, trimmedURL: trimmed_url])
                            log.debug("found ${tipps.size()} " +
                                    "tipps for URL ${trimmed_url}")
                        }

                        if (tipps.size() > 0) {
                            log.warn("found ${tipps.size()} TIPPs with URL ${trimmed_url}!")
                            if (tipps.size() == 1) {
                                tipp = tipps[0]
                            } else {
                                List<TitleInstancePackagePlatform> tippsMatchedByTitleID = tippsMatchingByTitleID(tipp_dto.identifiers, pkg, plt)

                                if(tippsMatchedByTitleID.size() > 0){
                                    List<TitleInstancePackagePlatform> tippsByUrlAndTitleID = tipps.findAll { it.id in tippsMatchedByTitleID.id }.sort { it.lastUpdated }

                                    tippsByUrlAndTitleID.reverse(true)

                                    if (tippsByUrlAndTitleID.size() > 1) {
                                        tippsByUrlAndTitleID.eachWithIndex { TitleInstancePackagePlatform titleInstancePackagePlatform, int index ->
                                            if (index == 0) {
                                                tipp = titleInstancePackagePlatform
                                            } else {
                                                tippDuplicates << titleInstancePackagePlatform.id
                                            }
                                        }

                                    } else if (tippsByUrlAndTitleID.size() == 1) {
                                        tipp = tippsByUrlAndTitleID[0]
                                    }else {
                                        log.debug("not found Tipp after tipps and tippsMatchingByTitleID: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tipp_dto.name}, url: ${trimmed_url}, ids: ${tipp_dto.identifiers}]")
                                    }
                                }
                            }
                        } else {
                            log.debug("None of the matched TIPPs are found!")
                        }
                        break
                }

            }

            if (!tipp) {
                log.debug("Creating new TIPP..")
                def tmap = [
                        'pkg'         : pkg,
                        'hostPlatform': plt,
                        'url'         : trimmed_url,
                        'uuid'        : (tipp_dto.uuid ?: null),
                        'status'      : (tipp_dto.status ?: 'Current'),
                        'name'        : (tipp_dto.name ?: null),
                        'type'        : (tipp_dto.type ?: null),
                        'medium'    : (tipp_dto.medium ?: null),

                ]

                tipp = tippCreate(tmap)
                if (!tipp) {
                    log.error("TIPP creation failed!")
                }
            }

            if (tipp) {

                //Kbart Fields to Ygor and then to wekb (siehe Wiki)

                // KBART -> publication_title -> name -> name
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'name', tipp_dto.name)
                // KBART -> first_author -> firstAuthor -> firstAuthor
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'firstAuthor', tipp_dto.firstAuthor)
                // KBART -> first_editor -> firstEditor -> firstEditor
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'firstEditor', tipp_dto.firstEditor)
                // KBART -> publisher_name -> publisherName -> publisherName
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'publisherName', tipp_dto.publisherName)


                // KBART -> publication_type -> publicationType -> publicationType
                RefdataValue publicationType
                if (tipp_dto.type) {
                    publicationType = determinePublicationType(tipp_dto.type)
                    if (publicationType) {
                        com.k_int.ClassUtils.setRefdataIfDifferent(publicationType.value, tipp, 'publicationType', RCConstants.TIPP_PUBLICATION_TYPE, false)
                    }
                }

                // KBART -> medium -> medium -> medium
                if (tipp_dto.medium) {
                    RefdataValue mediumRef = determineMediumRef(tipp_dto.medium)
                    if (mediumRef) {
                        com.k_int.ClassUtils.setRefdataIfDifferent(mediumRef.value, tipp, 'medium', RCConstants.TIPP_MEDIUM, false)
                    }
                }

                // KBART -> title_url -> url -> url
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'url', trimmed_url)
                // KBART -> subject_area -> subjectArea -> subjectArea
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'subjectArea', tipp_dto.subjectArea)

                // KBART -> ddc -> ddc -> ddcs
                if(tipp_dto.ddc != "") {
                    if (tipp.ddcs) {
                        def ddcsIDs = tipp.ddcs.id.clone()
                        ddcsIDs.each {
                            tipp.removeFromDdcs(RefdataValue.get(it))
                        }
                        tipp.save()
                    }
                }
                // KBART -> ddc -> ddc -> ddcs
                if (tipp_dto.ddc instanceof String) {

                    RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, tipp_dto.ddc)

                    if(refdataValue && !(refdataValue in tipp.ddcs)){
                        tipp.addToDdcs(refdataValue)
                    }
                }

                // KBART -> ddc -> ddc -> ddcs
                if (tipp_dto.ddc instanceof List) {
                    tipp_dto.ddc.each{ String ddc ->
                        RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, ddc)
                        if(refdataValue && !(refdataValue in tipp.ddcs)){
                            tipp.addToDdcs(refdataValue)
                        }
                    }
                }

                // KBART -> language -> language -> languages
                if (tipp_dto.language) {
                    if (tipp.languages) {
                        def langIDs = tipp.languages.id.clone()
                        langIDs.each {
                            ComponentLanguage.executeUpdate('delete from ComponentLanguage lang where lang.id = :langId',[langId: it])
                        }
                        tipp.save()
                        //ComponentLanguage.executeUpdate("delete from ComponentLanguage where tipp = :tipp", [tipp: tipp])
                    }

                    tipp_dto.language.each{ String lan ->
                        RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.COMPONENT_LANGUAGE, lan)
                        if(refdataValue){
                            if(!ComponentLanguage.findByTippAndLanguage(tipp, refdataValue)){
                                ComponentLanguage componentLanguage = new ComponentLanguage(tipp: tipp, language: refdataValue)
                                componentLanguage.save()
                            }
                        }
                    }

                    tipp.save()
                    //tipp.refresh()
                }

                // KBART -> access_type -> accessType -> accessType
                if (tipp_dto.accessType && tipp_dto.accessType.length() > 0) {
                    def access_statement
                    if (tipp_dto.accessType == 'P') {
                        access_statement = 'Paid'
                    } else if (tipp_dto.accessType == 'F') {
                        access_statement = 'Free'
                    } else {
                        access_statement = tipp_dto.accessType
                    }
                    RefdataValue access_ref = RefdataCategory.lookup(RCConstants.TIPP_ACCESS_TYPE, access_statement)
                    if (access_ref) tipp.accessType = access_ref
                }

                // KBART -> access_start_date -> accessStartDate -> accessStartDate
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.accessStartDate, tipp, 'accessStartDate', true)
                // KBART -> access_end_date -> accessEndDate -> accessEndDate
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.accessEndDate, tipp, 'accessEndDate', true)
                // KBART -> last_changed -> lastChangedExternal -> lastChangedExternal
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.lastChanged, tipp, 'lastChangedExternal', true)

                // KBART -> status -> status -> status
                com.k_int.ClassUtils.setRefdataIfDifferent(tipp_dto.status, tipp, 'status', RCConstants.COMPONENT_STATUS, false)

                // KBART -> listprice_eur, listprice_usd, listprice_gbp
                if (tipp_dto.prices) {
                    for (def priceData : tipp_dto.prices) {
                        if (priceData.amount != null && priceData.currency) {
                            tipp.setPrice(priceData.type, priceData.amount, priceData.currency, priceData.startDate ? dateFormatService.parseDate(priceData.startDate) : null, priceData.endDate ? dateFormatService.parseDate(priceData.endDate) : null)
                        }
                    }
                }

                // KBART -> notes -> coverage_notes -> note
                //com.k_int.ClassUtils.setStringIfDifferent(tipp, 'note', tipp_dto.coverage_notes)

                // KBART -> date_monograph_published_print -> dateFirstInPrint -> dateFirstInPrint
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.dateFirstInPrint, tipp, 'dateFirstInPrint', true)
                // KBART -> date_monograph_published_online -> dateFirstOnline -> dateFirstOnline
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.dateFirstOnline, tipp, 'dateFirstOnline', true)

                // KBART -> monograph_volume -> volumeNumber -> volumeNumber
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'volumeNumber', tipp_dto.volumeNumber)
                // KBART -> monograph_edition -> editionStatement -> editionStatement
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'editionStatement', tipp_dto.editionStatement)
                // KBART -> monograph_parent_collection_title -> series -> series
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'series', tipp_dto.series)

                // KBART -> parent_publication_title_id -> parentPublicationTitleId -> parentPublicationTitleId
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'parentPublicationTitleId', tipp_dto.parent_publication_title_id)

                // KBART -> oa_type -> oaType -> openAccess
                com.k_int.ClassUtils.setRefdataIfDifferent(tipp_dto.oaType, tipp, 'openAccess', RCConstants.TIPP_OPEN_ACCESS, true)

                // KBART -> oa_apc_eur -> oa_apc_eur -> prices
                if (tipp_dto.oa_apc_eur) {
                            tipp.setPrice(RDStore.PRICE_TYPE_OA_APC.value, tipp_dto.oa_apc_eur, RDStore.CURRENCY_EUR.value, null, null)
                }

                // KBART -> oa_apc_usd -> oa_apc_usd -> prices
                if (tipp_dto.oa_apc_usd) {
                    tipp.setPrice(RDStore.PRICE_TYPE_OA_APC.value, tipp_dto.oa_apc_usd, RDStore.CURRENCY_USD.value, null, null)
                }

                // KBART -> oa_apc_gbp -> oa_apc_gbp -> prices
                if (tipp_dto.oa_apc_gbp) {
                    tipp.setPrice(RDStore.PRICE_TYPE_OA_APC.value, tipp_dto.oa_apc_gbp, RDStore.CURRENCY_GBP.value, null, null)
                }

                // KBART -> package_isil -> package_isil -> identifiers['package_isil']
                if (tipp_dto.package_isil) {
                    createOrUpdateIdentifierForTipp(tipp, "package_isil", tipp_dto.package_isil)
                    identifierNameSpacesExistOnTipp << "package_isil"
                }

                // KBART -> package_isci -> package_isci -> identifiers['package_isci']
                if (tipp_dto.package_isci) {
                    createOrUpdateIdentifierForTipp(tipp, "package_isci", tipp_dto.package_isci)
                    identifierNameSpacesExistOnTipp << "package_isci"
                }

                // KBART -> ill_indicator -> ill_indicator -> identifiers['ill_indicator']
                if (tipp_dto.ill_indicator) {
                    createOrUpdateIdentifierForTipp(tipp, "ill_indicator", tipp_dto.ill_indicator)
                    identifierNameSpacesExistOnTipp << "ill_indicator"
                }


                // KBART -> preceding_publication_title_id -> preceding_publication_title_id -> precedingPublicationTitleId
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'precedingPublicationTitleId', tipp_dto.preceding_publication_title_id)
                // KBART -> superseding_publication_title_id -> superceding_publication_title_id -> supersedingPublicationTitleId
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'supersedingPublicationTitleId', tipp_dto.superceding_publication_title_id)

                // KBART -> date_first_issue_online, date_last_issue_online,
                // num_first_vol_online, num_first_issue_online,
                // num_last_vol_online, num_last_issue_online
                if (tipp_dto.coverageStatements && !tipp_dto.coverage) {
                    tipp_dto.coverage = tipp_dto.coverageStatements
                }

                if (tipp_dto.coverage && tipp_dto.coverage.size() > 0 && publicationType && publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {

                    if(tippsWithCoverage[tipp.id]){
                        tippsWithCoverage[tipp.id] << tipp_dto.coverage[0]
                    }else {
                        tippsWithCoverage[tipp.id] = [tipp_dto.coverage[0]]
                    }
                }

                if (tipp_dto.coverage && tipp_dto.coverage.size() > 0 && publicationType && publicationType != RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                    com.k_int.ClassUtils.setStringIfDifferent(tipp, 'note', tipp_dto.coverage[0].coverageNote)
                    if(tipp.coverageStatements && tipp.coverageStatements.size() > 0){
                        def cStsIDs = tipp.coverageStatements.id.clone()
                        cStsIDs.each {
                            tipp.removeFromCoverageStatements(TIPPCoverageStatement.get(it))
                        }
                        tipp.save()
                    }
                }

                // KBART -> package_ezb_anchor -> package_ezb_anchor -> identifiers['package_ezb_anchor']
                if (tipp_dto.package_ezb_anchor) {
                    createOrUpdateIdentifierForTipp(tipp, "package_ezb_anchor", tipp_dto.package_ezb_anchor)
                    identifierNameSpacesExistOnTipp << "package_ezb_anchor"
                }

                // KBART -> zdb_id, ezb_id, print_identifier, online_identifier, title_id, doi_identifier  -> identifiers
                tipp_dto.identifiers.each { identifierMap ->
                    def namespace_val = identifierMap.type ?: identifierMap.namespace

                    if (namespace_val && identifierMap.value && namespace_val.toLowerCase() != "originediturl") {
                        createOrUpdateIdentifierForTipp(tipp, namespace_val, identifierMap.value)
                        identifierNameSpacesExistOnTipp << namespace_val
                    }
                }

                //Cleanup Identifiers
                List<Long> deleteIdentifiers = []
                tipp.ids.each { Identifier identifier ->
                    if(!(identifier.namespace.value in identifierNameSpacesExistOnTipp)){
                        deleteIdentifiers << identifier.id
                    }
                }

                deleteIdentifiers.each{
                    Identifier.executeUpdate("delete from Identifier where id_id = :id", [id: it])
                }

                tipp.lastUpdated = new Date()

                tipp.save()
            }

            result = tipp
        } else {
            log.debug("Not able to reference TIPP: ${tipp_dto}")
        }
        result
    }*/

    LinkedHashMap tippImportForUpdate(Map tippMap, LinkedHashMap tippsWithCoverage, HashSet<Long> tippDuplicates = [], UpdatePackageInfo updatePackageInfo, List kbartRowsToCreateTipps) {
        LinkedHashMap result = [newTipp: false, removedTipp: false, tippObject: null, updatePackageInfo: updatePackageInfo,
                                tippsWithCoverage: tippsWithCoverage, kbartRowsToCreateTipps: kbartRowsToCreateTipps, tippDuplicates: tippDuplicates]
        log.info("Begin tippImportForUpdate")
        long start = System.currentTimeMillis()
        Package pkg = tippMap.pkg
        Platform plt = tippMap.nominalPlatform


        def trimmed_url = tippMap.title_url ? tippMap.title_url.trim() : null

        //TODO: Moe
        //def curator = pkg.curatoryGroups?.size() > 0 ? (user.adminStatus || user.curatoryGroups?.id.intersect(pkg.curatoryGroups?.id)) : false

        if (pkg && plt) {
            //and tipp.status != ? ??????
            int countTipps = 0

            List<TitleInstancePackagePlatform> tipps = []

            String title = tippMap.publication_title

            countTipps = TitleInstancePackagePlatform.executeQuery('select count(*) from TitleInstancePackagePlatform as tipp ' +
                    'where tipp.pkg = :pkg and tipp.status != :removed and tipp.name = :tiDtoName ',
                    [pkg: pkg, tiDtoName: title, removed: RDStore.KBC_STATUS_REMOVED])[0]

            if(countTipps > 0) {
                tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp ' +
                        'where tipp.pkg = :pkg and tipp.status != :removed and tipp.name = :tiDtoName ' +
                        ' order by tipp.lastUpdated DESC',
                        [pkg: pkg, tiDtoName: title, removed: RDStore.KBC_STATUS_REMOVED])
            }

            TitleInstancePackagePlatform tipp = null

            if (!tipp) {
                if(countTipps == 0){
                    if (trimmed_url && trimmed_url.size() > 0) {
                        log.debug("not found Tipp with title. research in pkg ${pkg} with url")
                        countTipps = TitleInstancePackagePlatform.executeQuery('select count(*) from TitleInstancePackagePlatform as tipp ' +
                                'where tipp.url = :url and tipp.status != :removed ' +
                                'and tipp.pkg = :pkg ',
                                [pkg: pkg, url: trimmed_url, removed: RDStore.KBC_STATUS_REMOVED])[0]
                        if(countTipps > 0) {
                            tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp ' +
                                    'where tipp.url = :url and tipp.status != :removed ' +
                                    'and tipp.pkg = :pkg order by tipp.lastUpdated DESC',
                                    [pkg: pkg, url: trimmed_url, removed: RDStore.KBC_STATUS_REMOVED])
                        }
                    }

                    if(countTipps == 0) {
                        log.debug("not found Tipp with title. research in pkg ${pkg} with tile_id")
                        tipps = tippsMatchingByTitleIDAutoUpdate(tippMap.title_id, pkg)
                        countTipps = tipps.size()
                    }

                }
                switch (countTipps) {
                    case 0:
                        log.debug("not found Tipp: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tippMap.publication_title}]")
                        break
                    case 1:
                        if (trimmed_url && trimmed_url.size() > 0) {
                            if (!tipps[0].url || tipps[0].url == trimmed_url) {
                                log.debug("found tipp by url")
                                tipp = tipps[0]
                            } else {

                                //if url changed find tipp over title id
                                List<TitleInstancePackagePlatform> tippsMatchedByTitleID = tippsMatchingByTitleIDAutoUpdate(tippMap.title_id, pkg)

                                tippsMatchedByTitleID.each{ TitleInstancePackagePlatform tippByTitleID ->
                                    if (tippByTitleID.id == tipps[0].id) {
                                        log.debug("found tipp by title ID")
                                        tipp = tipps[0]
                                    } else {
                                        log.debug("not found Tipp because url changed: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tippMap.publication_title}, url: ${trimmed_url}]")
                                    }
                                }
                            }
                        } else {
                            log.debug("found tipp by direct match")
                            tipp = tipps[0]
                        }
                        break
                    default:
                        if (trimmed_url && trimmed_url.size() > 0) {
                            tipps = tipps.findAll { !it.url || it.url == trimmed_url }
                            log.debug("found ${tipps.size()} tipps for URL ${trimmed_url}")
                        }

                        if (tipps.size() > 0) {
                            log.warn("found ${tipps.size()} TIPPs with URL ${trimmed_url}!")
                            if (tipps.size() == 1) {
                                tipp = tipps[0]
                            } else {
                                List<TitleInstancePackagePlatform> tippsMatchedByTitleID = tippsMatchingByTitleIDAutoUpdate(tippMap.title_id, pkg)

                                if(tippsMatchedByTitleID.size() > 0){
                                    List<TitleInstancePackagePlatform> tippsByUrlAndTitleID = tipps.findAll { it.id in tippsMatchedByTitleID.id }.sort { it.lastUpdated }

                                    tippsByUrlAndTitleID.reverse(true)

                                    if (tippsByUrlAndTitleID.size() > 1) {
                                        tippsByUrlAndTitleID.eachWithIndex { TitleInstancePackagePlatform titleInstancePackagePlatform, int index ->
                                            if (index == 0) {
                                                tipp = titleInstancePackagePlatform
                                            } else {
                                                result.tippDuplicates.add(titleInstancePackagePlatform.id)
                                            }
                                        }

                                    } else if (tippsByUrlAndTitleID.size() == 1) {
                                        tipp = tippsByUrlAndTitleID[0]
                                    }else {
                                        log.debug("not found Tipp after tipps and tippsMatchingByTitleID: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tippMap.publication_title}, url: ${trimmed_url}, title_id: ${tippMap.title_id}]")
                                    }
                                }
                            }
                        } else {
                            log.debug("None of the matched TIPPs are found!")
                        }
                        break
                }

            }

            if (!tipp) {
                log.debug("push in map to create new TIPP..")
                def tmap = [
                        'pkg'         : pkg,
                        'hostPlatform': plt,
                        'url'         : trimmed_url,
                        'status'      : (tippMap.status ?: 'Current'),
                        'name'        : (tippMap.publication_title ?: null),
                        'type'        : (tippMap.publication_type ?: null),
                        'medium'    : (tippMap.medium ?: null),
                        'kbartRowMap': tippMap
                ]
                result.kbartRowsToCreateTipps << tmap
                result.newTipp = true
            }

            if (tipp) {
                tipp.kbartImportRunning = true
                tipp.fromKbartImport = true
                result = updateTippWithKbart(result, tipp, tippMap, updatePackageInfo, result.tippsWithCoverage)
                tipp = result.tipp
            }

            result.tippObject = tipp
        } else {
            log.debug("Not able to reference TIPP: ${tippMap}")
        }

        log.debug("processed tippImportForUpdate at: ${System.currentTimeMillis()-start} msecs")
        log.info("End tippImportForUpdate: TIPP UUID -> ${result.tippObject?.uuid}")

        result.updatePackageInfo = updatePackageInfo

        result
    }

    @Deprecated
    TitleInstancePackagePlatform tippMatchingByTitleID(JSONArray identifiers, Package aPackage, Platform platform) {
        if(identifiers && aPackage.kbartSource && aPackage.kbartSource.targetNamespace){

            String value = identifiers.find {it.type == aPackage.kbartSource.targetNamespace.value}?.value

            List<TitleInstancePackagePlatform> tippList = Identifier.executeQuery('select i.tipp from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.value = :value and i.tipp is not null', [namespaceValue: aPackage.kbartSource.targetNamespace.value.toLowerCase(), value: value])

            if(tippList.size() == 1){
                    log.debug("tippsMatchingByTitleID provider internal identifier matching by "+tippList.size() + ": "+ tippList.id)
                    return tippList[0]
            }
        }
        else if(identifiers && platform.titleNamespace){
            String value = identifiers.find {it.type == platform.titleNamespace.value}?.value

            List<TitleInstancePackagePlatform> tippList = Identifier.executeQuery('select i.tipp from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.value = :value and i.tipp is not null', [namespaceValue: platform.titleNamespace.value.toLowerCase(), value: value])

            if(tippList.size() == 1){
                log.debug("tippsMatchingByTitleID provider internal identifier matching by "+tippList.size() + ": "+ tippList.id)
                return tippList[0]
            }
        }

    }

    @Deprecated
    List<TitleInstancePackagePlatform> tippsMatchingByTitleID(JSONArray identifiers, Package aPackage, Platform platform) {
        if(identifiers && aPackage.kbartSource && aPackage.kbartSource.targetNamespace){

            String value = identifiers.find {it.type == aPackage.kbartSource.targetNamespace.value}?.value

            List<TitleInstancePackagePlatform> tippList = Identifier.executeQuery('select i.tipp from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.value = :value and i.tipp is not null', [namespaceValue: aPackage.kbartSource.targetNamespace.value.toLowerCase(), value: value])

            tippList = tippList.findAll {it.pkg == aPackage && it.status != RDStore.KBC_STATUS_REMOVED}

            if(tippList.size() > 0){
                log.debug("tippsMatchingByTitleID provider internal identifier matching by "+tippList.size() + ": "+ tippList.id)
                return tippList
            }
        }
        else if(identifiers && platform.titleNamespace){
            String value = identifiers.find {it.type == platform.titleNamespace.value}?.value

            List<TitleInstancePackagePlatform> tippList = Identifier.executeQuery('select i.tipp from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.value = :value and i.tipp is not null', [namespaceValue: platform.titleNamespace.value.toLowerCase(), value: value])

            tippList = tippList.findAll {it.pkg == aPackage && it.status != RDStore.KBC_STATUS_REMOVED}

            if(tippList.size() > 0){
                log.debug("tippsMatchingByTitleID provider internal identifier matching by "+tippList.size() + ": "+ tippList.id)
                return tippList
            }
        }

    }

    List<TitleInstancePackagePlatform> tippsMatchingByTitleIDAutoUpdate(String titleID, Package aPackage) {
        List<TitleInstancePackagePlatform> tippList = []
        log.debug("tippsMatchingByTitleID provider internal identifier matching by ")

        IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

        if(titleID && identifierNamespace) {
                tippList = Identifier.executeQuery('select i.tipp from Identifier i, TitleInstancePackagePlatform tipp where ' +
                        'i.tipp = tipp and ' +
                        'tipp.status != :status and ' +
                        'tipp.pkg = :package and ' +
                        'i.namespace = :namespaceValue and ' +
                        'i.value = :value and ' +
                        'i.tipp is not null', [namespaceValue: identifierNamespace, value: titleID, package: aPackage, status: RDStore.KBC_STATUS_REMOVED])

                if (tippList.size() > 0) {
                    log.debug("tippsMatchingByTitleID provider internal identifier matching by " + tippList.size() + ": " + tippList.id)
                    return tippList
                }
            }
        return tippList
    }

    /**
     * Create a new TIPP
     */
    TitleInstancePackagePlatform tippCreate(tipp_fields = [:]) {

        RefdataValue tipp_status = tipp_fields.status ? RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, tipp_fields.status) : null

        RefdataValue tipp_medium = null
        if (tipp_fields.medium) {
            tipp_medium = determineMediumRef(tipp_fields.medium)
        }
        RefdataValue tipp_publicationType = null
        if (tipp_fields.type) {
            tipp_publicationType = determinePublicationType(tipp_fields.type)
        }
        long start = System.currentTimeMillis()
        TitleInstancePackagePlatform result = new TitleInstancePackagePlatform(
                uuid: tipp_fields.uuid ?: UUID.randomUUID().toString(),
                status: tipp_status,
                name: tipp_fields.name,
                medium: tipp_medium,
                publicationType: tipp_publicationType,
                url: tipp_fields.url,
                pkg: tipp_fields.pkg,
                hostPlatform: tipp_fields.hostPlatform)

        result.save()

        if (!result) {
            log.error("TIPP creation failed!")
        }
        log.debug("processed at: ${System.currentTimeMillis()-start} msecs")

        result
    }

    static RefdataValue determinePublicationType(String type) {
        if (type) {
            switch (type.trim()) {
                case "serials":
                case "serial":
                case "Serial":
                case "Journal":
                case "journal":
                    return RDStore.TIPP_PUBLIC_TYPE_SERIAL
                    break;
                case "monographs":
                case "monograph":
                case "Monograph":
                case "Book":
                case "book":
                    return RDStore.TIPP_PUBLIC_TYPE_MONO
                    break;
                case "Database":
                case "database":
                    return RDStore.TIPP_PUBLIC_TYPE_DB
                    break;
                case "Other":
                case "other":
                    return RDStore.TIPP_PUBLIC_TYPE_OTHER
                    break;
                default:
                    return null
                    break;
            }
        }
        else {
            return null
        }
    }

    static RefdataValue determineMediumRef(String medium) {
        if (medium) {
            switch (medium.toLowerCase().trim()) {
                case "a & i database":
                case "abstract- & indexdatenbank":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "A & I Database")
                case "audio":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Audio")
                case "database":
                case "fulltext database":
                case "Volltextdatenbank":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Database")
                case "dataset":
                case "datenbestand":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Dataset")
                case "film":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Film")
                case "image":
                case "bild":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Image")
                case "journal":
                case "zeitschrift":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Journal")
                case "book":
                case "buch":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Book")
                case "published score":
                case "musiknoten":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Published Score")
                case "article":
                case "artikel":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Article")
                case "software":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Software")
                case "statistics":
                case "statistiken":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Statistics")
                case "market data":
                case "marktdaten":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Market Data")
                case "standards":
                case "normen":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Standards")
                case "biography":
                case "biografie":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Biography")
                case "legal text":
                case "gesetzestext/urteil":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Legal Text")
                case "cartography":
                case "kartenwerk":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Cartography")
                case "miscellaneous":
                case "sonstiges":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Miscellaneous")
                case "other":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Other")
                default:
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, medium)
            }
        }
        else {
            return null
        }
    }

    boolean createOrUpdateIdentifierForTipp(Map result, TitleInstancePackagePlatform tipp, String namespace_val, String identifierValue, String kbartProperty, UpdatePackageInfo updatePackageInfo){
        boolean identifierChanged = false
        String newValue = identifierValue.trim()
        String oldValue = ''
        Identifier identifier
        IdentifierNamespace ns = IdentifierNamespace.findByValueAndTargetType(namespace_val, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

        //tipp = tipp.refresh()
        if(ns) {
            try {
                def pattern = ns.pattern ? ~"${ns.pattern}" : null
                if (pattern && !(newValue ==~ pattern)) {
                    UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                            description: "$kbartProperty has wrong format to import.",
                            tipp: tipp,
                            startTime: new Date(),
                            endTime: new Date(),
                            status: RDStore.UPDATE_STATUS_FAILED,
                            type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                            updatePackageInfo: updatePackageInfo,
                            kbartProperty: kbartProperty,
                            tippProperty: "identifiers[${namespace_val}]",
                            oldValue: oldValue,
                            newValue: newValue
                    ).save()
                } else {
                    LinkedHashSet<Identifier> identifiersWithSameNamespace = tipp.ids.findAll { it.namespace.value == namespace_val }

                    switch (identifiersWithSameNamespace.size()) {
                        case 0:
                            identifier = new Identifier(namespace: ns, value: newValue, tipp: tipp)
                            identifier.save()
                            identifierChanged = true
                            break
                        case 1:
                            if (identifiersWithSameNamespace[0].value != newValue) {
                                identifierChanged = true
                                oldValue = identifiersWithSameNamespace[0].value
                                identifiersWithSameNamespace[0].value = newValue
                                identifiersWithSameNamespace[0].save()
                                identifier = identifiersWithSameNamespace[0]
                            }
                            break
                        default:
                            List toDeletedIdentifier = []
                            identifiersWithSameNamespace.each { Identifier tippIdentifier ->
                                toDeletedIdentifier << tippIdentifier.id
                            }

                            toDeletedIdentifier.each {
                                //Identifier.executeUpdate("delete from Identifier where id_id = :id", [id: it])
                                tipp.removeFromIds(Identifier.get(it))
                            }
                            identifierChanged = true
                            identifier = new Identifier(namespace: ns, value: newValue, tipp: tipp)
                            identifier.save()
                            //println(identifier.errors)
                            break
                    }
                    if (identifier && identifierChanged && !result.newTipp) {
                        //updatePackageInfo = updatePackageInfo.refresh()
                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                description: "Create or update identifier of title '${tipp.name}'",
                                tipp: tipp,
                                startTime: new Date(),
                                endTime: new Date(),
                                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                                updatePackageInfo: updatePackageInfo,
                                kbartProperty: kbartProperty,
                                tippProperty: "identifiers[${namespace_val}]",
                                oldValue: oldValue,
                                newValue: newValue
                        ).save()
                    }
                }
            }
            catch (Exception e) {
                log.error("createOrUpdateIdentifierForTipp: -> ${kbartProperty} ${newValue}:" + e.printStackTrace())
            }
        }


        return result.changedTipp ?: identifierChanged
    }

    @Deprecated
    void createOrUpdateIdentifierForTipp(TitleInstancePackagePlatform tipp, String namespace_val, String identifierValue){
        Identifier identifier
        IdentifierNamespace ns = IdentifierNamespace.findByValueAndTargetType(namespace_val, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

        //tipp = tipp.refresh()

        LinkedHashSet<Identifier> identifiersWithSameNamespace = tipp.ids.findAll{it.namespace.value == namespace_val}

        switch (identifiersWithSameNamespace.size()) {
            case 0:
                identifier = new Identifier(namespace: ns, value: identifierValue, tipp: tipp)
                identifier.save()
                break
            case 1:
                identifiersWithSameNamespace[0].value = identifierValue
                identifiersWithSameNamespace[0].save()
                identifier = identifiersWithSameNamespace[0]
                break
            default:
                List toDeletedIdentifier = []
                identifiersWithSameNamespace.each{Identifier tippIdentifier ->
                    toDeletedIdentifier << tippIdentifier.id
                }

                toDeletedIdentifier.each{
                    //Identifier.executeUpdate("delete from Identifier where id_id = :id", [id: it])
                    tipp.removeFromIds(Identifier.get(it))
                }

                identifier = new Identifier(namespace: ns, value: identifierValue, tipp: tipp)
                identifier.save()
                break
        }

    }

    void createOrUpdateCoverageForTipp(TitleInstancePackagePlatform tipp, List coverage){
        log.debug("createOrUpdateCoverageForTipp Beginn")
        //tipp = tipp.refresh()

        Integer countNewCoverages = coverage.size()
        Integer countTippCoverages = tipp.coverageStatements ? tipp.coverageStatements.size() : 0

        if(countNewCoverages == 1 && countTippCoverages == 1){
            RefdataValue cov_depth = null

            if (coverage[0].coverageDepth instanceof String) {
                cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, coverage[0].coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
            }

            def parsedStart = TextUtils.completeDateString(coverage[0].startDate)
            def parsedEnd = TextUtils.completeDateString(coverage[0].endDate, false)

            ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'startIssue', coverage[0].startIssue)
            ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'endIssue', coverage[0].endIssue)
            ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'startVolume', coverage[0].startVolume)
            ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'endVolume', coverage[0].endVolume)
            ClassUtils.setDateIfPresent(parsedStart, tipp.coverageStatements[0], 'startDate', true)
            ClassUtils.setDateIfPresent(parsedEnd, tipp.coverageStatements[0], 'endDate', true)
            ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'embargo', coverage[0].embargo)
            ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'coverageNote', coverage[0].coverageNote)
            if(cov_depth) {
                ClassUtils.setRefdataIfDifferent(cov_depth.value, tipp.coverageStatements[0], 'coverageDepth', RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, true)
            }
        }
        else if(countNewCoverages == 0 && countTippCoverages > 0){
            def cStsIDs = tipp.coverageStatements.id.clone()
            cStsIDs.each {
                TIPPCoverageStatement.executeUpdate('delete from TIPPCoverageStatement cs where cs.id = :csID',[csID: it])
            }
            if (!tipp.save()) {
                log.error("Tipp save error: ")
                tipp.errors.allErrors.each {
                    println it
                }
            }

        }else if(countNewCoverages > 0 && countTippCoverages == 0){

            List filtered = coverage.unique(false) { a, b ->
                a.startDate <=> b.startDate ?:
                        a.startVolume <=> b.startVolume ?:
                                a.startIssue <=> b.startIssue ?:
                                        a.endDate <=> b.endDate ?:
                                                a.endVolume <=> b.endVolume ?:
                                                        a.endIssue <=> b.endIssue
            }

            filtered.each { c ->
                def parsedStart = TextUtils.completeDateString(c.startDate)
                def parsedEnd = TextUtils.completeDateString(c.endDate, false)
                def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
                def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)

                RefdataValue cov_depth = null

                if (c.coverageDepth instanceof String) {
                    cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, c.coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
                }

                tipp.addToCoverageStatements(
                        'startVolume': c.startVolume,
                        'startIssue': c.startIssue,
                        'endVolume': c.endVolume,
                        'endIssue': c.endIssue,
                        'embargo': c.embargo,
                        'coverageDepth': cov_depth,
                        'coverageNote': c.coverageNote,
                        'startDate': startAsDate,
                        'endDate': endAsDate,
                        'uuid': UUID.randomUUID().toString()
                )
            }
        }else if(countNewCoverages > 1 && countTippCoverages >= 1) {
            def cStsIDs = tipp.coverageStatements.id.clone()
            cStsIDs.each {
                TIPPCoverageStatement.executeUpdate('delete from TIPPCoverageStatement cs where cs.id = :csID',[csID: it])
            }


            List filtered = coverage.unique(false) { a, b ->
                a.startDate <=> b.startDate ?:
                        a.startVolume <=> b.startVolume ?:
                                a.startIssue <=> b.startIssue ?:
                                        a.endDate <=> b.endDate ?:
                                                a.endVolume <=> b.endVolume ?:
                                                        a.endIssue <=> b.endIssue
            }

            filtered.each { c ->
                def parsedStart = TextUtils.completeDateString(c.startDate)
                def parsedEnd = TextUtils.completeDateString(c.endDate, false)
                def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
                def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)

                RefdataValue cov_depth = null

                if (c.coverageDepth instanceof String) {
                    cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, c.coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
                }

                try {

                    TIPPCoverageStatement tippCoverageStatement = new TIPPCoverageStatement(
                                startVolume: c.startVolume,
                                startIssue: c.startIssue,
                                endVolume: c.endVolume,
                                endIssue: c.endIssue,
                                embargo: c.embargo,
                                coverageDepth: cov_depth,
                                coverageNote: c.coverageNote,
                                startDate: startAsDate,
                                endDate: endAsDate,
                                uuid: UUID.randomUUID().toString(),
                                tipp: tipp
                        )
                       if(!tippCoverageStatement.save()) {
                           log.error("Tipp save error: ")
                           tipp.errors.allErrors.each {
                               println it
                           }
                       }


/*                    tipp.addToCoverageStatements(
                            'startVolume': c.startVolume,
                            'startIssue': c.startIssue,
                            'endVolume': c.endVolume,
                            'endIssue': c.endIssue,
                            'embargo': c.embargo,
                            'coverageDepth': cov_depth,
                            'coverageNote': c.coverageNote,
                            'startDate': startAsDate,
                            'endDate': endAsDate,
                            'uuid': UUID.randomUUID().toString()
                    )*/
                }
                catch (Exception e){
                    log.error("Failed to add coverage statement to tipp:" + e.toString())
                }

            }

        }

       /* coverage.each { c ->
            def parsedStart = TextUtils.completeDateString(c.startDate)
            def parsedEnd = TextUtils.completeDateString(c.endDate, false)

            //com.k_int.ClassUtils.setStringIfDifferent(tipp, 'coverageNote', c.coverageNote)
            //com.k_int.ClassUtils.setRefdataIfDifferent(c.coverageDepth, tipp, 'coverageDepth', RCConstants.TIPP_COVERAGE_DEPTH, true)

            def cs_match = false
            def conflict = false
            def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
            def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)
            def conflicting_statements = []



            Map cMap = ["startIssue": c.startIssue,
                          "endIssue": c.endIssue,
                          "startVolume": c.startVolume,
                          "endVolume": c.endVolume]

            println(cMap)

            tipp.coverageStatements?.each { TIPPCoverageStatement tcs ->
                Map tcsMap = ["startIssue": tcs.startIssue,
                              "endIssue": tcs.endIssue,
                              "startVolume": tcs.startVolume,
                              "endVolume": tcs.endVolume]
                println( tcsMap.equals(cMap))
                println( tcsMap.toString() == cMap.toString())
                println(tcsMap)
                *//*if (!cs_match) {
                    if (!tcs.endDate && !endAsDate) {
                        conflict = true
                    } else if (tcs.toString() == c.toString()) {
                        log.debug("Matched CoverageStatement by Map")
                        cs_match = true
                    } else if (tcs.startVolume && tcs.startVolume == c.startVolume) {
                        log.debug("Matched CoverageStatement by startVolume")
                        cs_match = true
                    } else if (tcs.startDate && tcs.startDate == startAsDate) {
                        log.debug("Matched CoverageStatement by startDate")
                        cs_match = true
                    } else if (!tcs.startVolume && !tcs.startDate && !tcs.endVolume && !tcs.endDate) {
                        log.debug("Matched CoverageStatement with unspecified values")
                        cs_match = true
                    } else if (tcs.startDate && tcs.endDate) {
                        if (startAsDate && startAsDate > tcs.startDate && startAsDate < tcs.endDate) {
                            conflict = true
                        } else if (endAsDate && endAsDate > tcs.startDate && endAsDate < tcs.endDate) {
                            conflict = true
                        }
                    }

                    if (conflict) {
                        conflicting_statements.add(tcs)
                    } else if (cs_match) {
                        com.k_int.ClassUtils.setStringIfDifferent(tcs, 'startIssue', c.startIssue)
                        com.k_int.ClassUtils.setStringIfDifferent(tcs, 'endIssue', c.endIssue)
                        com.k_int.ClassUtils.setStringIfDifferent(tcs, 'startVolume', c.startVolume)
                        com.k_int.ClassUtils.setStringIfDifferent(tcs, 'endVolume', c.endVolume)
                        com.k_int.ClassUtils.setDateIfPresent(parsedStart, tcs, 'startDate', true)
                        com.k_int.ClassUtils.setDateIfPresent(parsedEnd, tcs, 'endDate', true)
                        com.k_int.ClassUtils.setStringIfDifferent(tcs, 'embargo', c.embargo)
                        com.k_int.ClassUtils.setStringIfDifferent(tcs, 'coverageNote', c.coverageNote)
                        com.k_int.ClassUtils.setRefdataIfDifferent(tcs.coverageDepth?.value, tcs, 'coverageDepth', RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, true)
                    }
                } else {
                    log.debug("Matched new coverage ${c} on multiple existing coverages!")
                }*//*
            }

            for (def cst : conflicting_statements) {
                tipp.removeFromCoverageStatements(cst)
            }

            if (!cs_match) {

                def cov_depth = null

                if (c.coverageDepth instanceof String) {
                    cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, c.coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
                } else if (c.coverageDepth instanceof Integer) {
                    cov_depth = RefdataValue.get(c.coverageDepth)
                } else if (c.coverageDepth instanceof Map) {
                    if (c.coverageDepth.id) {
                        cov_depth = RefdataValue.get(c.coverageDepth.id)
                    } else {
                        cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, (c.coverageDepth.name ?: c.coverageDepth.value))
                    }
                }

                tipp.addToCoverageStatements(
                        'startVolume': c.startVolume,
                        'startIssue': c.startIssue,
                        'endVolume': c.endVolume,
                        'endIssue': c.endIssue,
                        'embargo': c.embargo,
                        'coverageDepth': cov_depth,
                        'coverageNote': c.coverageNote,
                        'startDate': startAsDate,
                        'endDate': endAsDate
                )
            }
            // refdata setStringIfDifferent(tipp, 'coverageDepth', c.coverageDepth)
        }*/
        if (!tipp.save()) {
            log.error("Tipp save error: ")
            tipp.errors.allErrors.each {
                println it
            }
        }
        log.debug("createOrUpdateCoverageForTipp End")
    }



    boolean checkAndSetByChangedValue(Map result, TitleInstancePackagePlatform tipp, String dataType, UpdatePackageInfo updatePackageInfo, Map tippMap, String kbartProperty, String tippProperty, boolean acceptNullValue = true, String refdataCategory = null) {
        boolean valueChanged = false
        //log.debug("in check and set by changed value")
        if (tippMap.containsKey(kbartProperty)) {
            if (dataType == 'Date') {
                LocalDateTime ldt = null
                if (acceptNullValue && (tippMap[kbartProperty] == null || tippMap[kbartProperty] == "") && (tipp[tippProperty] != null && tipp[tippProperty] != "")) {
                    if (!result.newTipp) {
                        valueChanged = true
                        String oldValue = renderObjectValue(tipp[tippProperty])
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty])
                    }
                    tipp[tippProperty] = null

                }else if (tippMap[kbartProperty] && tippMap[kbartProperty].toString().trim()) {
                    String oldValue = renderObjectValue(tipp[tippProperty])
                    String newValue = tippMap[kbartProperty]
                    try {
                        if ( newValue.trim() ) {
                            ldt = TextUtils.completeDateString(newValue)
                        }
                    }
                    catch (Exception e) {
                        log.error("Parse date fail. Date to parse was -> ${newValue}:" + e.toString())
                    }

                    if (ldt) {
                        Date instant = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
                        if (instant != tipp[tippProperty]) {
                            if (!result.newTipp) {
                                valueChanged = true
                                createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty])
                            }
                            tipp[tippProperty] = instant
                        }
                    }else {
                        createUpdateTippInfoByTippChangeFail(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty], "The Date '${tippMap[kbartProperty]}' could not be parsed.")
                    }
                }
            } else if (dataType == 'RefDataValue') {
                def v = null
                if (acceptNullValue && (tippMap[kbartProperty] == null || tippMap[kbartProperty] == "") && (tipp[tippProperty] != null && tipp[tippProperty] != "")) {
                    if (!result.newTipp) {
                        valueChanged = true
                        String oldValue = renderObjectValue(tipp[tippProperty])
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty])
                    }
                    tipp[tippProperty] = null

                } else if (tippMap[kbartProperty] && tippMap[kbartProperty].trim() && refdataCategory) {
                    String oldValue = renderObjectValue(tipp[tippProperty])
                    if(kbartProperty == 'medium'){
                       v = determineMediumRef(tippMap[kbartProperty])
                    }else if(kbartProperty == 'publication_type'){
                        v = determinePublicationType(tippMap[kbartProperty])
                    }else {
                        v = RefdataCategory.lookup(refdataCategory, tippMap[kbartProperty])
                    }
                    if (v) {
                        String newValue = renderObjectValue(v)
                        if (oldValue.toLowerCase() != newValue.toLowerCase()) {
                            if (!result.newTipp) {
                                valueChanged = true
                                createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                            }
                            tipp[tippProperty] = v
                        }
                    }else {
                        createUpdateTippInfoByTippChangeFail(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, tippMap[kbartProperty], "The value ${tippMap[kbartProperty]} not found in RefdatValues.")
                    }
                }
            } else if (dataType == 'String') {
                String oldValue = renderObjectValue(tipp[tippProperty])
                String newValue = renderObjectValue(tippMap[kbartProperty])
                if (oldValue.toLowerCase() != newValue.toLowerCase()) {
                    if (!result.newTipp) {
                        valueChanged = true
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                    }
                    if(tippMap[kbartProperty] == ""){
                        tipp[tippProperty] = null
                    }else {
                        if (kbartProperty == 'title_url') {
                            tipp[tippProperty] = tippMap[kbartProperty].trim()
                        } else {
                            tipp[tippProperty] = tippMap[kbartProperty]
                        }
                    }
                }
            }
        }

        if(valueChanged){
            if (!tipp.save()) {
                log.error("Tipp save error: ")
                tipp.errors.allErrors.each {
                    println it
                }
            }
        }

        return  result.changedTipp ?: valueChanged
    }

    void createUpdateTippInfoByTippChange(TitleInstancePackagePlatform tipp, UpdatePackageInfo updatePackageInfo, String kbartProperty, String tippProperty, String oldValue, String newValue){
        //updatePackageInfo = updatePackageInfo.refresh()
        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                description: "Changes in Title '${tipp.name}'",
                tipp: tipp,
                startTime: new Date(),
                endTime: new Date(),
                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                updatePackageInfo: updatePackageInfo,
                kbartProperty: kbartProperty,
                tippProperty: tippProperty,
                oldValue: oldValue,
                newValue: newValue
        ).save()
    }

    void createUpdateTippInfoByTippChangeFail(TitleInstancePackagePlatform tipp, UpdatePackageInfo updatePackageInfo, String kbartProperty, String tippProperty, String oldValue, String newValue, String description){
        //updatePackageInfo = updatePackageInfo.refresh()
        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                description: description,
                tipp: tipp,
                startTime: new Date(),
                endTime: new Date(),
                status: RDStore.UPDATE_STATUS_FAILED,
                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                updatePackageInfo: updatePackageInfo,
                kbartProperty: kbartProperty,
                tippProperty: tippProperty,
                oldValue: oldValue,
                newValue: newValue
        ).save()
    }

    String renderObjectValue(value) {
        String result = ''
        if (value != null) {
            switch (value.class) {
                case wekb.RefdataValue.class:
                    result = value.value
                    break
                case Boolean.class:
                    result = (value == true ? 'Yes' : 'No')
                    break
                case Date.class:
                    def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd')
                    result = sdf.format(value).toString()
                    break
                default:
                    result = value.toString();
            }
        }
        result
    }

    boolean createOrUpdatePrice(Map result, TitleInstancePackagePlatform tipp, RefdataValue priceType, RefdataValue currency, String price, String kbartProperty, UpdatePackageInfo updatePackageInfo){
        boolean priceChanged = false
        Float f = null
        String newValue = price
        String oldValue = ''
        TippPrice cp = null
        if (price && priceType && currency) {
            try {
            String uniformedThousandSeparator = price.replaceAll("[,.](\\d{3})", '$1')
            uniformedThousandSeparator = uniformedThousandSeparator.replaceAll(",", ".")
            f = Float.parseFloat(uniformedThousandSeparator)
            if (!f.isNaN()) {
                List<TippPrice> existPrices = TippPrice.findAllByTippAndPriceTypeAndCurrency(tipp, priceType, currency, [sort: 'lastUpdated', order: 'ASC'])
                if (existPrices.size() == 1) {
                    TippPrice existPrice = existPrices[0]
                    if (existPrice.price != f) {
                        oldValue = existPrice.price.toString()
                        existPrice.price = f
                        existPrice.save()
                        if (!tipp.save()) {
                            log.error("Tipp save error: ")
                            tipp.errors.allErrors.each {
                                println it
                            }
                        }
                        priceChanged = true
                    }

                } else if (existPrices.size() > 1) {
                    def pricesIDs = existPrices.id.clone()
                    pricesIDs.each {
                        TippPrice tippPrice = TippPrice.get(it)
                        tipp.removeFromPrices(tippPrice)
                        //TippPrice.executeUpdate("delete from TippPrice id = ${it}")
                    }
                    if (!tipp.save()) {
                        log.error("Tipp save error: ")
                        tipp.errors.allErrors.each {
                            println it
                        }
                    }
                    tipp = tipp.refresh()
                    cp = new TippPrice(
                            tipp: tipp,
                            priceType: priceType,
                            currency: currency,
                            price: f)
                    cp.save()
                    if (!tipp.save()) {
                        log.error("Tipp save error: ")
                        tipp.errors.allErrors.each {
                            println it
                        }
                    }
                    priceChanged = true

                } else {
                    cp = new TippPrice(
                            tipp: tipp,
                            priceType: priceType,
                            currency: currency,
                            price: f)
                    cp.save()
                    if (!tipp.save()) {
                        log.error("Tipp save error: ")
                        tipp.errors.allErrors.each {
                            println it
                        }
                    }
                    priceChanged = true
                }
            } else {
                //updatePackageInfo = updatePackageInfo.refresh()
                UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                        description: "The value '${newValue}' can not parse to a price",
                        tipp: tipp,
                        startTime: new Date(),
                        endTime: new Date(),
                        status: RDStore.UPDATE_STATUS_FAILED,
                        type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                        updatePackageInfo: updatePackageInfo,
                        kbartProperty: kbartProperty,
                        tippProperty: "prices[${priceType.value}]",
                        oldValue: oldValue,
                        newValue: newValue
                ).save()
            }
        }catch (Exception e) {
                log.error("createOrUpdatePrice -> kbartProperty ${newValue}:" + e.printStackTrace())
            }
        }else{
            if(!result.newTipp) {
                List<TippPrice> existPrices = TippPrice.findAllByTippAndPriceTypeAndCurrency(tipp, priceType, currency, [sort: 'lastUpdated', order: 'ASC'])
               if (existPrices.size() > 0) {
                    def pricesIDs = existPrices.id.clone()
                    pricesIDs.each {
                        TippPrice tippPrice = TippPrice.get(it)
                        tipp.removeFromPrices(tippPrice)
                    }
                    if (!tipp.save()) {
                        log.error("Tipp save error: ")
                        tipp.errors.allErrors.each {
                            println it
                        }
                    }else {
                        tipp = tipp.refresh()
                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                description: "Delete price of title '${tipp.name}' because no price in kbart!",
                                tipp: tipp,
                                startTime: new Date(),
                                endTime: new Date(),
                                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                                updatePackageInfo: updatePackageInfo,
                                kbartProperty: kbartProperty,
                                tippProperty: "prices[${priceType.value}]",
                                oldValue: oldValue,
                                newValue: newValue
                        ).save()
                    }
                }

            }
        }

        if(cp && priceChanged && !result.newTipp){
            //updatePackageInfo = updatePackageInfo.refresh()
            UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                    description: "Create or update price of title '${tipp.name}'",
                    tipp: tipp,
                    startTime: new Date(),
                    endTime: new Date(),
                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                    type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                    updatePackageInfo: updatePackageInfo,
                    kbartProperty: kbartProperty,
                    tippProperty: "prices[${priceType.value}]",
                    oldValue: oldValue,
                    newValue: newValue
            ).save()
        }
        return result.changedTipp ?: priceChanged
    }

    boolean checkAndSetByChangedCoverageValue(Map result, TitleInstancePackagePlatform tipp, TIPPCoverageStatement tippCoverageStatement, String dataType, UpdatePackageInfo updatePackageInfo = null, String kbartProperty, String tippProperty, String newValue, boolean acceptNullValue = true, String refdataCategory = null) {
        boolean valueChanged = false
        if (newValue) {
            if (dataType == 'Date') {
                LocalDateTime ldt = null
                if (acceptNullValue && (newValue == null || newValue == "") && (tipp[tippProperty] != null && tipp[tippProperty] != "")) {
                    if (!result.newTipp) {
                        valueChanged = true
                        String oldValue = renderObjectValue(tipp[tippProperty])
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                    }
                    tipp[tippProperty] = null
                }

                if (newValue && newValue.toString().trim()) {
                    String oldValue = renderObjectValue(tipp[tippProperty])
                    try {
                        if ( newValue ) {
                            ldt = TextUtils.completeDateString(newValue)
                        }
                    }
                    catch (Exception e) {
                        log.error("Parse date fail. Date to parse was -> ${newValue}:" + e.toString())
                    }

                    if (ldt) {
                        Date instant = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
                        if (instant != tipp[tippProperty]) {
                            if (!result.newTipp) {
                                valueChanged = true
                                createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                            }
                            tipp[tippProperty] = instant
                        }
                    }else {
                        createUpdateTippInfoByTippChangeFail(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue, "The Date '${newValue}' could not be parsed.")
                    }
                }
            } else if (dataType == 'RefDataValue') {
                def v = null
                if (acceptNullValue && (newValue == null || newValue == "") && (tipp[tippProperty] != null && tipp[tippProperty] != "")) {
                    if (!result.newTipp) {
                        valueChanged = true
                        String oldValue = renderObjectValue(tipp[tippProperty])
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                    }
                    tipp[tippProperty] = null
                } else if (newValue && newValue.trim() && refdataCategory) {
                    String oldValue = renderObjectValue(tipp[tippProperty])
                    v = RefdataCategory.lookup(refdataCategory, newValue)
                    if (v) {
                        String renderNewValue = renderObjectValue(v)
                        if (oldValue.toLowerCase() != newValue.toLowerCase()) {
                            if (!result.newTipp) {
                                valueChanged = true
                                createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, renderNewValue)
                            }
                            tipp[tippProperty] = v
                        }
                    }else {
                        createUpdateTippInfoByTippChangeFail(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue, "The value ${newValue} not found in Refdata values.")
                    }
                }
            } else if (dataType == 'String') {
                String oldValue = renderObjectValue(tipp[tippProperty])
                String renderNewValue = renderObjectValue(newValue)
                if (oldValue.toLowerCase() != newValue.toLowerCase()) {
                    if (!result.newTipp) {
                        valueChanged = true
                        createUpdateTippInfoByTippChange(tipp, updatePackageInfo, kbartProperty, tippProperty, oldValue, newValue)
                    }
                        tipp[tippProperty] = newValue
                }
            }
        }

        if(result.changedTipp){
            tippCoverageStatement.save()
        }

        return  result.changedTipp ?: valueChanged
    }

    LinkedHashMap updateTippWithKbart(LinkedHashMap result, TitleInstancePackagePlatform tipp, Map tippMap, UpdatePackageInfo updatePackageInfo, LinkedHashMap tippsWithCoverage) {
        log.debug("updateTippWithKbart: tipp.id: " + tipp.id)
        List identifierNameSpacesExistOnTipp = []
        //Kbart Fields to Ygor and then to wekb (siehe Wiki)

        // KBART -> status -> status -> status
        if (tipp.status != RDStore.KBC_STATUS_REMOVED && tippMap.status == "Removed") {
            ClassUtils.setRefdataIfDifferent(tippMap.status, tipp, 'status', RCConstants.COMPONENT_STATUS, false)
            result.removedTipp = true

            updatePackageInfo = updatePackageInfo.refresh()
            UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                    description: "Removed Title '${tippMap.publication_title}'",
                    tipp: tipp,
                    startTime: new Date(),
                    endTime: new Date(),
                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                    type: RDStore.UPDATE_TYPE_REMOVED_TITLE,
                    kbartProperty: 'status',
                    oldValue: tipp.status.value,
                    newValue: 'Removed',
                    tippProperty: 'status',
                    updatePackageInfo: updatePackageInfo
            ).save()
        } else {
            result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "status", "status", false, RCConstants.COMPONENT_STATUS)
        }
        log.debug("after save")
        // KBART -> publication_title -> name
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "publication_title", "name")
        //log.debug("1")
        // KBART -> first_author -> firstAuthor
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "first_author", "firstAuthor")
        //log.debug("2")
        // KBART -> first_editor -> firstEditor
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "first_editor", "firstEditor")
        //log.debug("3")
        // KBART -> publisher_name -> publisherName
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "publisher_name", "publisherName")
        //log.debug("4")
        // KBART -> publication_type -> publicationType
        if (tippMap.publication_type) {
            result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "publication_type", "publicationType", false, RCConstants.TIPP_PUBLICATION_TYPE)
        }
        //log.debug("5")
        // KBART -> medium -> medium
        if (tippMap.medium) {
            result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "medium", "medium", false, RCConstants.TIPP_MEDIUM)
        }
        //log.debug("6")
        // KBART -> title_url -> url
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "title_url", "url")
        //log.debug("7")
        // KBART -> subject_area -> subjectArea
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "subject_area", "subjectArea")
        //log.debug("8")
        // KBART -> access_type -> accessType
        if (tippMap.access_type && tippMap.access_type.length() > 0) {
            //log.debug("8a")
            if (tippMap.access_type == 'P') {
                tippMap.access_type = 'Paid'
            } else if (tippMap.access_type == 'F') {
                tippMap.access_type = 'Free'
            }
            result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "access_type", "accessType", true, RCConstants.TIPP_ACCESS_TYPE)
        }
        //log.debug("9")
        // KBART -> access_start_date -> accessStartDate
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "access_start_date", "accessStartDate")
        //log.debug("10")
        // KBART -> access_end_date -> accessEndDate
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "access_end_date", "accessEndDate")
        //log.debug("11")
        // KBART -> last_changed -> lastChangedExternal
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "last_changed", "lastChangedExternal")
        //log.debug("12")
        // KBART -> notes -> note
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "notes", "note")
        //log.debug("13")
        // KBART -> date_monograph_published_print -> dateFirstInPrint
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "date_monograph_published_print", "dateFirstInPrint")
        //log.debug("14")
        // KBART -> date_monograph_published_online -> dateFirstOnline
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'Date', updatePackageInfo, tippMap, "date_monograph_published_online", "dateFirstOnline")
        //log.debug("15")
        // KBART -> monograph_volume -> volumeNumber
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "monograph_volume", "volumeNumber")
        //log.debug("16")
        // KBART -> monograph_edition -> editionStatement
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "monograph_edition", "editionStatement")
        //log.debug("17")
        // KBART -> monograph_parent_collection_title -> series
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "monograph_parent_collection_title", "series")
        //log.debug("18")
        // KBART -> parent_publication_title_id -> parentPublicationTitleId
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "parent_publication_title_id", "parentPublicationTitleId")
        //log.debug("19")
        // KBART -> oa_type -> openAccess
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'RefDataValue', updatePackageInfo, tippMap, "oa_type", "openAccess", true, RCConstants.TIPP_OPEN_ACCESS)
        //log.debug("before package_isil")
        // KBART -> package_isil -> identifiers['package_isil']
        if (tippMap.package_isil) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_isil", tippMap.package_isil, 'package_isil', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "package_isil"
        }

        // KBART -> package_isci -> identifiers['package_isci']
        if (tippMap.package_isci) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_isci", tippMap.package_isci, 'package_isci', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "package_isci"
        }

        // KBART -> ill_indicator -> identifiers['ill_indicator']
        if (tippMap.ill_indicator) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "ill_indicator", tippMap.ill_indicator, 'ill_indicator', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "ill_indicator"
        }


        // KBART -> preceding_publication_title_id -> precedingPublicationTitleId
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "preceding_publication_title_id", "precedingPublicationTitleId")

        // KBART -> superseding_publication_title_id -> supersedingPublicationTitleId
        result.changedTipp = checkAndSetByChangedValue(result, tipp, 'String', updatePackageInfo, tippMap, "superseding_publication_title_id", "supersedingPublicationTitleId")

        //log.debug("before coverages")
        // KBART -> date_first_issue_online, date_last_issue_online,
        // num_first_vol_online, num_first_issue_online,
        // num_last_vol_online, num_last_issue_online
        // embargo_info, coverage_depth
        if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
            if (tippMap.date_first_issue_online || tippMap.date_last_issue_online || tippMap.num_first_vol_online ||
                    tippMap.num_first_issue_online || tippMap.num_last_vol_online || tippMap.num_last_issue_online) {
                Map coverageMap = [startDate    : tippMap.date_first_issue_online,
                                   endDate      : tippMap.date_last_issue_online,
                                   startVolume  : tippMap.num_first_vol_online,
                                   startIssue   : tippMap.num_first_issue_online,
                                   endVolume    : tippMap.num_last_vol_online,
                                   endIssue     : tippMap.num_last_issue_online,
                                   embargo      : tippMap.embargo_info,
                                   coverageDepth: tippMap.coverage_depth]

                if (tippsWithCoverage[tipp.id]) {
                    tippsWithCoverage[tipp.id] << coverageMap
                } else {
                    tippsWithCoverage[tipp.id] = [coverageMap]
                }
            }else{
                //To cleanup title without coverage
                if (tippsWithCoverage[tipp.id]) {
                    tippsWithCoverage[tipp.id] << []
                } else {
                    tippsWithCoverage[tipp.id] = []
                }
            }

        }
        //log.debug("before coverages II")
        if (tipp.publicationType != RDStore.TIPP_PUBLIC_TYPE_SERIAL && TIPPCoverageStatement.findByTipp(tipp)) {
            //log.debug("in coverage statements block")
            ClassUtils.setStringIfDifferent(tipp, 'note', tipp.coverageStatements[0].coverageNote)
            if (tipp.coverageStatements && tipp.coverageStatements.size() > 0) {
                def cStsIDs = tipp.coverageStatements.id.clone()
                cStsIDs.each {
                    tipp.removeFromCoverageStatements(TIPPCoverageStatement.get(it))
                }
                if (!tipp.save()) {
                    log.error("Tipp save error: ")
                    tipp.errors.allErrors.each {
                        println it
                    }
                }
            }
        }
        //log.debug("before creating identifiers")
        // KBART -> package_ezb_anchor -> identifiers['package_ezb_anchor']
        if (tippMap.package_ezb_anchor) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_ezb_anchor", tippMap.package_ezb_anchor, 'package_ezb_anchor', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "package_ezb_anchor"
        }

        // KBART -> zdb_id  -> identifiers['zdb']
        if (tippMap.zdb_id) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ZDB, tippMap.zdb_id, 'zdb_id', updatePackageInfo)
            identifierNameSpacesExistOnTipp << IdentifierNamespace.ZDB
        }

        // KBART -> ezb_id -> identifiers['ezb']
        if (tippMap.ezb_id) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EZB, tippMap.ezb_id, 'ezb_id', updatePackageInfo)
            identifierNameSpacesExistOnTipp << IdentifierNamespace.EZB
        }

        if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
            // KBART -> print_identifier-> identifiers
            if (tippMap.print_identifier) {
                result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISSN, tippMap.print_identifier, 'print_identifier', updatePackageInfo)
                identifierNameSpacesExistOnTipp << IdentifierNamespace.ISSN
            }

            // KBART -> online_identifier  -> identifiers
            if (tippMap.online_identifier) {
                result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISSN, tippMap.online_identifier, 'online_identifier', updatePackageInfo)
                identifierNameSpacesExistOnTipp << IdentifierNamespace.EISSN
            }
        } else if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_MONO) {
            // KBART -> print_identifier-> identifiers
            if (tippMap.print_identifier) {
                result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISBN, tippMap.print_identifier, 'print_identifier', updatePackageInfo)
                identifierNameSpacesExistOnTipp << IdentifierNamespace.ISBN
            }

            // KBART -> online_identifier  -> identifiers
            if (tippMap.online_identifier) {
                result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISBN, tippMap.online_identifier, 'online_identifier', updatePackageInfo)
                identifierNameSpacesExistOnTipp << IdentifierNamespace.EISBN
            }
        }

        // KBART -> title_id  -> identifiers
        if (tippMap.title_id) {
                result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, 'title_id', tippMap.title_id, 'title_id', updatePackageInfo)
                identifierNameSpacesExistOnTipp << 'title_id'
        }

        // KBART -> doi_identifier  -> identifiers
        if (tippMap.doi_identifier) {
            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "doi", tippMap.doi_identifier, 'doi_identifier', updatePackageInfo)
            identifierNameSpacesExistOnTipp << "doi"
        }

        //Cleanup Identifiers
        List<Long> deleteIdentifiers = []
        tipp.ids.each { Identifier identifier ->
            if (!(identifier.namespace.value in identifierNameSpacesExistOnTipp)) {
                deleteIdentifiers << identifier.id
            }
        }

        deleteIdentifiers.each {
            //Identifier.executeUpdate("delete from Identifier where id_id = :id", [id: it])
            tipp.removeFromIds(Identifier.get(it))
        }

        if(deleteIdentifiers.size() > 0){
            if (!tipp.save()) {
                log.error("Tipp save error: ")
                tipp.errors.allErrors.each {
                    println it
                }
            }
        }

        // KBART -> ddc -> ddcs
        if (tippMap.ddc != "") {
            if (tipp.ddcs) {
                def ddcsIDs = tipp.ddcs.id.clone()
                ddcsIDs.each {
                    tipp.removeFromDdcs(RefdataValue.get(it))
                }
                if (!tipp.save()) {
                    log.error("Tipp save error: ")
                    tipp.errors.allErrors.each {
                        println it
                    }
                }
            }
        }
        // KBART -> ddc -> ddcs
        if (tippMap.ddc) {
            List ddcs = tippMap.ddc.split(',')

            ddcs.each { String ddc ->
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, ddc)
                if (refdataValue && !(refdataValue in tipp.ddcs)) {
                    tipp.addToDdcs(refdataValue)
                }
            }
        }

        // KBART -> language -> language -> languages
        if (tippMap.language) {
            if (tipp.languages) {
                def langIDs = tipp.languages.id.clone()
                langIDs.each {
                    tipp.removeFromLanguages(ComponentLanguage.get(it))
                    ComponentLanguage.get(it).delete()
                }
                if (!tipp.save()) {
                    log.error("Tipp save error: ")
                    tipp.errors.allErrors.each {
                        println it
                    }
                }
                //ComponentLanguage.executeUpdate("delete from ComponentLanguage where tipp = :tipp", [tipp: tipp])
            }
            List languages = tippMap.language.split(',')
            languages.each { String lan ->
                RefdataValue refdataValue
                if(lan.size() == 2){
                    refdataValue = RefdataValue.findByOwnerAndValueIlike(RefdataCategory.findByDesc(RCConstants.COMPONENT_LANGUAGE), lan.toLowerCase())
                }else {
                    refdataValue = RefdataCategory.lookup(RCConstants.COMPONENT_LANGUAGE, lan)
                }
                if (refdataValue) {
                    if (!(tipp.languages && refdataValue in tipp.languages.language)) {
                        ComponentLanguage componentLanguage = new ComponentLanguage(tipp: tipp, language: refdataValue)
                        componentLanguage.save()
                    }
                }
            }

            if (!tipp.save()) {
                log.error("Tipp save error: ")
                tipp.errors.allErrors.each {
                    println it
                }
            }
            //tipp.refresh()
        }
        log.debug("before price section")
        // KBART -> listprice_eur -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_EUR, tippMap.listprice_eur, 'listprice_eur', updatePackageInfo)


        // KBART -> listprice_usd -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_USD, tippMap.listprice_usd, 'listprice_usd', updatePackageInfo)

        // KBART -> listprice_gbp -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_GBP, tippMap.listprice_gbp, 'listprice_gbp', updatePackageInfo)


        // KBART -> oa_apc_eur -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_EUR, tippMap.oa_apc_eur, 'oa_apc_eur', updatePackageInfo)


        // KBART -> oa_apc_usd -> prices
        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_USD, tippMap.oa_apc_usd, 'oa_apc_usd', updatePackageInfo)


        // KBART -> oa_apc_gbp -> prices
       result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_GBP, tippMap.oa_apc_gbp, 'oa_apc_gbp', updatePackageInfo)


        log.debug("after price section")

        try {
            tipp = tipp.save(failOnError: true)

        } catch (Exception e) {
            log.error("KbartImportService tipp.save() error: " + e.printStackTrace())
           /* updatePackageInfo = updatePackageInfo.refresh()
            UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                        description: "Changes in title fail. More information can be seen in the system log.",
                        tipp: tipp,
                        startTime: new Date(),
                        endTime: new Date(),
                        status: RDStore.UPDATE_STATUS_FAILED,
                        type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                        updatePackageInfo: updatePackageInfo,
                        kbartProperty: null,
                        tippProperty: null,
                        oldValue: null,
                        newValue: null
                ).save()*/
        }

        result.tippsWithCoverage = tippsWithCoverage
        result.updatePackageInfo = updatePackageInfo
        result.tipp = tipp

        return result
    }

    List createTippBatch(List tippMaps, UpdatePackageInfo updatePackageInfo) {
        List newTippList = []

        IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

        int counterNewTippsToProcess = tippMaps.size()

        //def dataSource = Holders.grailsApplication.mainContext.getBean('dataSource')
        //Sql sql = new Sql(dataSource)

        int max = 500
        int idx = 0
        TitleInstancePackagePlatform.withSession { Session sess ->
            for (int offset = 0; offset < tippMaps.size(); offset += max) {

                List tippMapsToProcess = tippMaps.drop(offset).take(max)
                for (Map tippMap : tippMapsToProcess) {
                    idx++

            long start = System.currentTimeMillis()

            try {

                RefdataValue tipp_status = tippMap.status ? RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, tippMap.status) : null

                RefdataValue tipp_medium = null
                if (tippMap.medium) {
                    tipp_medium = determineMediumRef(tippMap.medium)
                }
                RefdataValue tipp_publicationType = null
                if (tippMap.type) {
                    tipp_publicationType = determinePublicationType(tippMap.type)
                }

                RefdataValue tipp_accessType = null
                if (tippMap.access_type && tippMap.access_type.length() > 0) {
                    if (tippMap.access_type == 'P') {
                        tipp_accessType = RDStore.TIPP_ACCESS_TYPE_PAID
                    } else if (tippMap.access_type == 'F') {
                        tipp_accessType = RDStore.TIPP_ACCESS_TYPE_FREE
                    }
                }

                RefdataValue oaType = tippMap.kbartRowMap.oa_type ? RefdataCategory.lookup(RCConstants.TIPP_OPEN_ACCESS, tippMap.kbartRowMap.oa_type) : null

                Date access_start_date = parseDatebyCreateTipp(tippMap.kbartRowMap.access_start_date)
                Date access_end_date = parseDatebyCreateTipp(tippMap.kbartRowMap.access_end_date)
                Date date_monograph_published_print = parseDatebyCreateTipp(tippMap.kbartRowMap.date_monograph_published_print)
                Date date_monograph_published_online = parseDatebyCreateTipp(tippMap.kbartRowMap.date_monograph_published_online)
                Date last_changed = parseDatebyCreateTipp(tippMap.kbartRowMap.last_changed)

                TitleInstancePackagePlatform tipp = new TitleInstancePackagePlatform(
                        uuid: tippMap.uuid ?: UUID.randomUUID().toString(),
                        status: tipp_status,
                        name: tippMap.name,
                        medium: tipp_medium,
                        publicationType: tipp_publicationType,
                        url: tippMap.url,
                        accessType: tipp_accessType,
                        accessStartDate: access_start_date,
                        accessEndDate: access_end_date,
                        dateFirstInPrint: date_monograph_published_print,
                        dateFirstOnline: date_monograph_published_online,
                        editionStatement: tippMap.kbartRowMap.monograph_edition,
                        firstAuthor: tippMap.kbartRowMap.first_author,
                        firstEditor: tippMap.kbartRowMap.first_editor,
                        lastChangedExternal: last_changed,
                        note: tippMap.kbartRowMap.notes,
                        openAccess: oaType,
                        parentPublicationTitleId: tippMap.kbartRowMap.parent_publication_title_id,
                        precedingPublicationTitleId: tippMap.kbartRowMap.preceding_publication_title_id,
                        publisherName: tippMap.kbartRowMap.publisher_name,
                        subjectArea: tippMap.kbartRowMap.subject_area,
                        series: tippMap.kbartRowMap.monograph_parent_collection_title,
                        supersedingPublicationTitleId: tippMap.kbartRowMap.superseding_publication_title_id,
                        volumeNumber: tippMap.kbartRowMap.monograph_volume,
                        fromKbartImport: true,
                        pkg: tippMap.pkg,
                        hostPlatform: tippMap.hostPlatform
                        )
                if (tipp) {
                    tipp.kbartImportRunning = true
                    if (!tipp.save()) {
                        log.error("Tipp save error: ")
                        tipp.errors.allErrors.each {
                            println it
                        }
                    }
                } else {
                    log.error("TIPP creation failed!")
                }
                if (!tipp) {
                    log.error("TIPP creation failed!")
                } else {

                    Map result = [newTipp: true]

                    // KBART -> package_isil -> identifiers['package_isil']
                    if (tippMap.kbartRowMap.package_isil) {
                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_isil", tippMap.kbartRowMap.package_isil, 'package_isil', updatePackageInfo)
                    }

                    // KBART -> package_isci -> identifiers['package_isci']
                    if (tippMap.kbartRowMap.package_isci) {
                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_isci", tippMap.kbartRowMap.package_isci, 'package_isci', updatePackageInfo)
                    }

                    // KBART -> ill_indicator -> identifiers['ill_indicator']
                    if (tippMap.kbartRowMap.ill_indicator) {
                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "ill_indicator", tippMap.kbartRowMap.ill_indicator, 'ill_indicator', updatePackageInfo)
                    }


                    // KBART -> date_first_issue_online, date_last_issue_online,
                    // num_first_vol_online, num_first_issue_online,
                    // num_last_vol_online, num_last_issue_online
                    // embargo_info, coverage_depth
                    if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                        if (tippMap.kbartRowMap.date_first_issue_online || tippMap.kbartRowMap.date_last_issue_online || tippMap.kbartRowMap.num_first_vol_online ||
                                tippMap.kbartRowMap.num_first_issue_online || tippMap.kbartRowMap.num_last_vol_online || tippMap.kbartRowMap.num_last_issue_online) {
                            Map coverageMap = [startDate    : tippMap.kbartRowMap.date_first_issue_online,
                                               endDate      : tippMap.kbartRowMap.date_last_issue_online,
                                               startVolume  : tippMap.kbartRowMap.num_first_vol_online,
                                               startIssue   : tippMap.kbartRowMap.num_first_issue_online,
                                               endVolume    : tippMap.kbartRowMap.num_last_vol_online,
                                               endIssue     : tippMap.kbartRowMap.num_last_issue_online,
                                               embargo      : tippMap.kbartRowMap.embargo_info,
                                               coverageDepth: tippMap.kbartRowMap.coverage_depth]

                            createOrUpdateCoverageForTipp(tipp, [coverageMap])
                        }

                    }

                    // KBART -> package_ezb_anchor -> identifiers['package_ezb_anchor']
                    if (tippMap.kbartRowMap.package_ezb_anchor) {
                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "package_ezb_anchor", tippMap.kbartRowMap.package_ezb_anchor, 'package_ezb_anchor', updatePackageInfo)
                    }

                    // KBART -> zdb_id  -> identifiers['zdb']
                    if (tippMap.kbartRowMap.zdb_id) {
                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ZDB, tippMap.kbartRowMap.zdb_id, 'zdb_id', updatePackageInfo)
                    }

                    // KBART -> ezb_id -> identifiers['ezb']
                    if (tippMap.kbartRowMap.ezb_id) {
                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EZB, tippMap.kbartRowMap.ezb_id, 'ezb_id', updatePackageInfo)
                    }

                    if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                        // KBART -> print_identifier-> identifiers
                        if (tippMap.kbartRowMap.print_identifier) {
                            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISSN, tippMap.kbartRowMap.print_identifier, 'print_identifier', updatePackageInfo)
                        }

                        // KBART -> online_identifier  -> identifiers
                        if (tippMap.kbartRowMap.online_identifier) {
                            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISSN, tippMap.kbartRowMap.online_identifier, 'online_identifier', updatePackageInfo)
                        }
                    } else if (tipp.publicationType == RDStore.TIPP_PUBLIC_TYPE_MONO) {
                        // KBART -> print_identifier-> identifiers
                        if (tippMap.kbartRowMap.print_identifier) {
                            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.ISBN, tippMap.kbartRowMap.print_identifier, 'print_identifier', updatePackageInfo)
                        }

                        // KBART -> online_identifier  -> identifiers
                        if (tippMap.kbartRowMap.online_identifier) {
                            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, IdentifierNamespace.EISBN, tippMap.kbartRowMap.online_identifier, 'online_identifier', updatePackageInfo)
                        }
                    }

                    // KBART -> title_id  -> identifiers
                    if (tippMap.kbartRowMap.title_id) {
                            result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, 'title_id', tippMap.kbartRowMap.title_id, 'title_id', updatePackageInfo)
                    }

                    // KBART -> doi_identifier  -> identifiers
                    if (tippMap.kbartRowMap.doi_identifier) {
                        result.changedTipp = createOrUpdateIdentifierForTipp(result, tipp, "doi", tippMap.kbartRowMap.doi_identifier, 'doi_identifier', updatePackageInfo)
                    }

                    // KBART -> ddc -> ddcs
                    if (tippMap.kbartRowMap.ddc) {
                        List ddcs = tippMap.kbartRowMap.ddc.split(',')

                        ddcs.each { String ddc ->
                            RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, ddc)
                            if (refdataValue && !(refdataValue in tipp.ddcs)) {
                                tipp.addToDdcs(refdataValue)
                            }
                        }
                    }

                    // KBART -> language -> language -> languages
                    if (tippMap.kbartRowMap.language) {
                        List languages = tippMap.kbartRowMap.language.split(',')
                        languages.each { String lan ->
                            RefdataValue refdataValue
                            if(lan.size() == 2){
                                refdataValue = RefdataValue.findByOwnerAndValueIlike(RefdataCategory.findByDesc(RCConstants.COMPONENT_LANGUAGE), lan)
                            }else {
                                refdataValue = RefdataCategory.lookup(RCConstants.COMPONENT_LANGUAGE, lan)
                            }
                            if (refdataValue) {
                                if (!ComponentLanguage.findByTippAndLanguage(tipp, refdataValue)) {
                                    ComponentLanguage componentLanguage = new ComponentLanguage(tipp: tipp, language: refdataValue)
                                    componentLanguage.save()
                                }
                            }
                        }

                        if (!tipp.save()) {
                            log.error("Tipp save error: ")
                            tipp.errors.allErrors.each {
                                println it
                            }
                        }
                    }
                    // KBART -> listprice_eur -> prices
                    if (tippMap.kbartRowMap.listprice_eur) {
                        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_EUR, tippMap.kbartRowMap.listprice_eur, 'listprice_eur', updatePackageInfo)
                    }

                    // KBART -> listprice_usd -> prices
                    if (tippMap.kbartRowMap.listprice_usd) {
                        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_USD, tippMap.kbartRowMap.listprice_usd, 'listprice_usd', updatePackageInfo)
                    }

                    // KBART -> listprice_gbp -> prices
                    if (tippMap.kbartRowMap.listprice_gbp) {
                        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_LIST, RDStore.CURRENCY_GBP, tippMap.kbartRowMap.listprice_gbp, 'listprice_gbp', updatePackageInfo)
                    }

                    // KBART -> oa_apc_eur -> prices
                    if (tippMap.kbartRowMap.oa_apc_eur) {
                        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_EUR, tippMap.kbartRowMap.oa_apc_eur, 'oa_apc_eur', updatePackageInfo)
                    }

                    // KBART -> oa_apc_usd -> prices
                    if (tippMap.kbartRowMap.oa_apc_usd) {
                        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_USD, tippMap.kbartRowMap.oa_apc_usd, 'oa_apc_usd', updatePackageInfo)
                    }

                    // KBART -> oa_apc_gbp -> prices
                    if (tippMap.kbartRowMap.oa_apc_gbp) {
                        result.changedTipp = createOrUpdatePrice(result, tipp, RDStore.PRICE_TYPE_OA_APC, RDStore.CURRENCY_GBP, tippMap.kbartRowMap.oa_apc_gbp, 'oa_apc_gbp', updatePackageInfo)
                    }


                    // updatePackageInfo = updatePackageInfo.refresh()
                    UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                            description: "New Title '${tippMap.name}'",
                            tipp: tipp,
                            startTime: new Date(),
                            endTime: new Date(),
                            status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                            type: RDStore.UPDATE_TYPE_NEW_TITLE,
                            updatePackageInfo: updatePackageInfo
                    ).save()

                    tipp.lastUpdated = new Date()
                    tipp = tipp.save()

                    newTippList << [tippID: tipp.id, updatePackageInfo: updatePackageInfo]
                }

                log.info("createTippBatch (${idx + 1} of $counterNewTippsToProcess) processed at: ${System.currentTimeMillis() - start} msecs")

            }catch (Exception e) {
                log.error("createTippBatch: -> ${tippMap.kbartRowMap}:" + e.printStackTrace())
            }

                   /* if (idx % 250 == 0) {
                        log.info("Clean up");
                        cleanupService.cleanUpGorm()
                    }*/
                }
                sess.flush()
                sess.clear()
            }
        }

        return newTippList
    }

    Date parseDatebyCreateTipp(String value){
        LocalDateTime ldt = null
        Date instant = null

        if (value && value.trim()) {
            String newValue = value
            try {
                if ( newValue.trim() ) {
                    ldt = TextUtils.completeDateString(newValue)
                }
            }
            catch (Exception e) {
                log.error("Parse date fail. Date to parse was -> ${newValue}:" + e.toString())
            }

            if (ldt) {
                instant = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
            }
        }
        return instant
    }
}

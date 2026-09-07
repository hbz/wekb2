package wekb

import grails.web.mvc.FlashScope

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.nio.charset.StandardCharsets

import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.core.GrailsClass
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import wekb.auth.User
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToOne
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.multipart.MultipartFile
import wekb.utils.DateUtils

import javax.servlet.http.HttpServletRequest
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Transactional
class CreateComponentService {

    def springSecurityService
    def grailsApplication
    def genericOIDService
    def classExaminationService
    def messageSource
    AccessService accessService
    DeletionService deletionService
    
    Map process(Map result, GrailsParameterMap params) {

        FlashScope flash = getCurrentFlashScope()
        // II: Defaulting this to true - don't like it much, but we need to be able to create a title without any
        // props being set... not ideal, but issue closing.
        boolean propertyWasSet = true

        Locale locale = LocaleContextHolder.getLocale()

        User user = springSecurityService.currentUser

        if ( params.cls ) {

            GrailsClass newclass = grailsApplication.getArtefact("Domain",params.cls)
            PersistentEntity pent = grailsApplication.mappingContext.getPersistentEntity(params.cls)
            log.debug("Got entity ${pent} for ${newclass.name}")

            if ( newclass ) {
                try {
                    result.newobj = newclass.getClazz().getDeclaredConstructor().newInstance()
                    log.debug("got newInstance...");

                    params.each { p ->
                        log.debug("Consider ${p.key} -> ${p.value}");
                        if ( pent.getPropertyByName(p.key) ) {
                            // THis deffo didn't work :( if ( newclass.metaClass.hasProperty(p.key) ) {

                            // Ensure that blank values actually null the value instead of trying to use an empty string.
                            if (p.value == "") p.value = null

                            PersistentProperty pprop = pent.getPropertyByName(p.key)

                            if ( pprop instanceof Association ) {
                                if ( pprop instanceof OneToOne) {
                                    log.debug("one-to-one");
                                    def related_item = null

                                    related_item = genericOIDService.resolveOID(p.value);

                                    if (!related_item && pprop.getType().name == 'wekb.RefdataValue') {
                                        def rdc = classExaminationService.deriveCategoryForProperty(params.cls, p.key)
                                        related_item = RefdataCategory.lookup(rdc, p.value)
                                    }

                                    result.newobj[p.key] = related_item
                                    propertyWasSet = propertyWasSet || (related_item != null)
                                }
                                else if ( pprop instanceof ManyToOne ) {
                                    log.debug("many-to-one");
                                    def related_item = genericOIDService.resolveOID(p.value);
                                    result.newobj[p.key] = related_item
                                    propertyWasSet = propertyWasSet || (related_item != null)
                                }
                                else {
                                    log.debug("unhandled association type");
                                }
                            }
                            else {
                                log.debug("Scalar property: Type -> ${pprop.getType().name}");
                                if ( pprop.getType().name == 'java.lang.String' ) {
                                    result.newobj[p.key] = p.value?.trim() ?: null
                                }
                                else if ( pprop.getType().name == 'java.util.Date' && p.value) {
                                    SimpleDateFormat sdf = DateUtils.getSDF_NoTime()

                                    try {
                                        if (p.value && p.value.size() > 0) {
                                            // parse new date
                                            def parsed_date = sdf.parse(p.value)
                                            result.newobj[p.key] = parsed_date
                                        } else {
                                            // delete existing date
                                            result.newobj[p.key] = null
                                        }
                                    }
                                    catch (Exception e) {
                                        log.error(e.toString())
                                    }

                                }else if ( pprop.getType().name == 'java.lang.Boolean' ) {
                                    result.newobj[p.key] = (p.value == '1') ? true : false
                                }
                                propertyWasSet = propertyWasSet || (p.value != null)
                            }
                        }
                        else {
                            log.debug("Persistent class has no property named ${p.key}");
                        }
                    }
                    log.debug("Completed setting properties");

                    /* if ( result.newobj.hasProperty('postCreateClosure') ) {
                       log.debug("Created object has a post create closure.. call it");
                       result.newobj.postCreateClosure.call([user:user])
                     }*/

                    if (result.newobj instanceof TitleInstancePackagePlatform && (params.pkg == null || params.url == null || params.name == null || params.publicationType == null)) {
                        result.errors=["Please fill Title, Package, Publication Type and Host Platform URL to create the component."]
                    }
                    else if (result.newobj instanceof Package && (params.nominalPlatform == null || params.provider == null || params.status == null)) {
                        result.errors=["Please fill Platform, Provider and Status to create the component."]
                    }
                    else if (!propertyWasSet) {
                        // Add an error message here if no property was set via data sent through from the form.
                        log.debug("No properties set");
                        result.errors=["Please fill in at least one piece of information to create the component."]
                    } else {

                        log.debug("Saving..")

                        if(result.newobj instanceof User && (user.isAdmin() || user.getSuperUserStatus())){
                            result.newobj.enabled = true
                        }

                        if(result.newobj instanceof TitleInstancePackagePlatform ){
                            result.newobj.hostPlatform = result.newobj.pkg.nominalPlatform
                            result.newobj.status = RDStore.KBC_STATUS_CURRENT
                        }

                        if(result.newobj.hasProperty('uuid')){
                            result.newobj.uuid = UUID.randomUUID().toString()
                        }
                        if(result.newobj.hasProperty('normname')){
                            result.newobj.normname = result.newobj.generateNormname(result.newobj.name)
                        }

                        if ( !result.newobj.validate() ) {
                            result.errors = []

                            result.newobj.errors.allErrors.each { eo ->

                                String[] messageArgs = eo.getArguments()
                                def errorMessage = [:]

                                log.debug("Found error with args: ${messageArgs}")

                                eo.getCodes().each { ec ->

                                    if (!errorMessage) {
                                        log.debug("testing code -> ${ec}")

                                        def msg = messageSource.resolveCode(ec, locale)?.format(messageArgs)

                                        if(msg && msg != ec) {
                                            errorMessage = msg
                                        }

                                        if(!errorMessage) {
                                            // log.debug("Could not resolve message")
                                        }else{
                                            log.debug("found message: ${msg}")
                                        }
                                    }
                                }

                                if (errorMessage) {
                                    result.errors.add(errorMessage)
                                }else{
                                    log.debug("No message found for ${eo.getCodes()}")
                                }
                            }

                            if(result.errors.size() > 0){
                                result.errors = result.errors
                            }

                            if ( result.errors.size() == 0 ) {
                                result.errors = ["There has been an error creating the component. Please try again."]
                            }
                        } else {
                            result.newobj.save()

                            if (result.newobj.hasProperty('curatoryGroups')) {
                                log.debug("Set CuratoryGroups..");
                                if(user.isAdmin() || user.getSuperUserStatus()) {
                                    flash.message = "Object was not assigned to a curator group because you are admin or superuser!!!!"

                                }else {
                                    if(user.curatoryGroupUsers) {
                                        user.curatoryGroupUsers.curatoryGroup.each { CuratoryGroup cg ->
                                            if(result.newobj instanceof Package){
                                                new CuratoryGroupPackage(pkg:  result.newobj, curatoryGroup: cg).save()
                                            }else if(result.newobj instanceof Platform){
                                                new CuratoryGroupPlatform(platform: result.newobj, curatoryGroup: cg).save()
                                            }else if(result.newobj instanceof Org){
                                                new CuratoryGroupOrg(org: result.newobj, curatoryGroup: cg).save()
                                            }else if(result.newobj instanceof KbartSource){
                                                new CuratoryGroupKbartSource(kbartSource: result.newobj, curatoryGroup: cg).save()
                                            }
                                        }
                                    }
                                }

                                result.newobj.save()
                            }

                            result.objectClassName = result.newobj.class.name
                        }
                    }
                }
                catch ( Exception e ) {
                    log.error("Problem process : ${params}-> ",e);
                    result.errors = ["Could not create component!"]
                }
            }
        }
        return result
    }


    Map packageBatchImport(MultipartFile tsvFile, User user) {

        List<CuratoryGroup> curatoryGroups = []

        if (user.curatoryGroupUsers) {
            user.curatoryGroupUsers.curatoryGroup.each { CuratoryGroup cg ->
                curatoryGroups << cg
            }
        }

        Map colMap = [:]
        Set<String> globalErrors = []
        List<Package> packageList = []

        Reader reader = new InputStreamReader(
                tsvFile.inputStream,
                StandardCharsets.UTF_8
        )

        CSVFormat csvFormat = CSVFormat.TDF.builder()
                .setQuote('"' as char)
                .build()

        CSVParser csvParser = csvFormat.parse(reader)

        List<CSVRecord> rows

        try {
            rows = csvParser.records
        }
        finally {
            csvParser.close()
            reader.close()
        }

        if (!rows) {
            return [
                    packages : [],
                    rowsCount: 0,
                    errors   : ["The uploaded file is empty."]
            ]
        }

        /*
         * ==========================================================
         * Header
         * ==========================================================
         */
        CSVRecord header = rows.remove(0)

        header.eachWithIndex { String s, int c ->

            String headerCol = s?.trim()

            if (headerCol?.startsWith("\uFEFF")) {
                headerCol = headerCol.substring(1)
            }

            switch (headerCol?.toLowerCase()) {

                case "package_name":
                    colMap.name = c
                    break

                case "package_uuid":
                    colMap.package_uuid = c
                    break

                case "provider_uuid":
                    colMap.provider_uuid = c
                    break

                case "nominal_platform_uuid":
                    colMap.nominal_platform_uuid = c
                    break

                case "description":
                    colMap.description = c
                    break

                case "url":
                case "description_url":
                    colMap.description_url = c
                    break

                case "breakable":
                    colMap.breakable = c
                    break

                case "consistent":
                    colMap.consistent = c
                    break

                case "content_type":
                    colMap.content_type = c
                    break

                case "file":
                    colMap.file = c
                    break

                case "open_access":
                    colMap.open_access = c
                    break

                case "payment_type":
                    colMap.payment_type = c
                    break

                case "scope":
                    colMap.scope = c
                    break

                case "editing_status":
                    colMap.editing_status = c
                    break

                case "free_trial":
                    colMap.free_trial = c
                    break

                case "free_trial_phase":
                    colMap.free_trial_phase = c
                    break

                case "national_range":
                    colMap.national_ranges = c
                    break

                case "regional_range":
                    colMap.regional_ranges = c
                    break

                case "anbieter_produkt_id":
                    colMap.anbieter_produkt_id = c
                    break

                case "provider_product_id":
                    colMap.provider_product_id = c
                    break

                case "ddc":
                    colMap.ddcs = c
                    break

                case "source_url":
                    colMap.source_url = c
                    break

                case "frequency":
                    colMap.frequency = c
                    break

                case "automated_updates":
                    colMap.automated_updates = c
                    break

                case "archiving_agency":
                    colMap.archiving_agency = c
                    break

                case "open_access_of_archiving_agency":
                    colMap.open_access_of_archiving_agency = c
                    break

                case "post_cancellation_access_of_archiving_agency":
                    colMap.post_cancellation_access_of_archiving_agency = c
                    break

                case "source_ftp_server_url":
                    colMap.source_ftp_server_url = c
                    break

                case "source_ftp_directory":
                    colMap.source_ftp_directory = c
                    break

                case "source_ftp_file_name":
                    colMap.source_ftp_file_name = c
                    break

                case "source_ftp_username":
                    colMap.source_ftp_username = c
                    break

                case "source_ftp_password":
                    colMap.source_ftp_password = c
                    break

                case "source_default_supply_method":
                    colMap.source_default_supply_method = c
                    break

                case "publication_title":
                    colMap.publication_title = c
                    break

                case "publication_type":
                    colMap.publication_type = c
                    break

                case "title_id":
                    colMap.title_id = c
                    break

                case "title_url":
                    colMap.title_url = c
                    break
            }
        }

        List<RefdataValue> statusList = [
                RDStore.KBC_STATUS_DELETED,
                RDStore.KBC_STATUS_REMOVED
        ]

        List identifiers = []
        List sources = []

        /*
         * ==========================================================
         * Packages importieren
         * ==========================================================
         */
        rows.each { CSVRecord cols ->

            boolean newCreated = false
            Package pkg
            boolean editAllowed = true

            String package_uuid = getValue(cols, colMap.package_uuid)

            if (package_uuid) {

                pkg = Package.findByUuid(package_uuid)

                if (pkg != null &&
                        !accessService.checkEditableObject(pkg, null)) {

                    globalErrors <<
                            "You have no authorization to edit the package with the uuid '${package_uuid}'.!"

                    editAllowed = false
                }
            }

            String name = getValue(cols, colMap.name)

            if (cols.size() > 0 &&
                    (name || pkg != null) &&
                    editAllowed) {

                /*
                 * ==================================================
                 * Neues Package
                 * ==================================================
                 */
                if (pkg == null) {

                    def dupes = []

                    if (curatoryGroups &&
                            colMap.anbieter_produkt_id != null) {

                        String value =
                                getValue(
                                        cols,
                                        colMap.anbieter_produkt_id
                                )

                        if (value) {

                            IdentifierNamespace namespace =
                                    IdentifierNamespace
                                            .findByValueAndTargetType(
                                                    IdentifierNamespace.PKG_ID,
                                                    RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_PACKAGE
                                            )

                            dupes = Identifier.executeQuery(
                                    '''
                                select ident.pkg
                                from Identifier ident
                                where ident.namespace = :ns
                                  and ident.value != :val
                                  and ident.value = :value
                                  and ident.pkg is not null
                                  and ident.pkg.status not in (:stat)
                                  and exists (
                                      select cgp
                                      from CuratoryGroupPackage cgp
                                      where cgp.pkg = ident.pkg
                                        and cgp.curatoryGroup in (:curGroup)
                                  )
                                ''',
                                    [
                                            value   : value,
                                            ns      : namespace,
                                            val     : 'Unknown',
                                            stat    : statusList,
                                            curGroup: curatoryGroups
                                    ]
                            )

                        } else if (name) {

                            dupes = Package.executeQuery(
                                    '''
                                select p
                                from Package p
                                where lower(p.name) like :name
                                  and p.status not in (:stat)
                                  and exists (
                                      select cgp
                                      from CuratoryGroupPackage cgp
                                      where cgp.pkg = p
                                        and cgp.curatoryGroup in (:curGroup)
                                  )
                                ''',
                                    [
                                            name    : name.toLowerCase().trim(),
                                            stat    : statusList,
                                            curGroup: curatoryGroups
                                    ]
                            )
                        }

                    } else if (name) {

                        dupes = Package.executeQuery(
                                '''
                            select p
                            from Package p
                            where lower(p.name) like :name
                              and p.status not in (:stat)
                            ''',
                                [
                                        name: name.toLowerCase().trim(),
                                        stat: statusList
                                ]
                        )

                        if (curatoryGroups) {

                            dupes = Package.executeQuery(
                                    '''
                                select p
                                from Package p
                                where lower(p.name) like :name
                                  and p.status not in (:stat)
                                  and exists (
                                      select cgp
                                      from CuratoryGroupPackage cgp
                                      where cgp.pkg = p
                                        and cgp.curatoryGroup in (:curGroup)
                                  )
                                ''',
                                    [
                                            name    : name.toLowerCase().trim(),
                                            stat    : statusList,
                                            curGroup: curatoryGroups
                                    ]
                            )
                        }
                    }

                    if (dupes && dupes.size() > 0) {

                        globalErrors <<
                                "The we:kb already has a package with the name '${name}'. Therefore a package with the name could not be created!"

                        name = null
                    }

                    String providerUuid =
                            getValue(
                                    cols,
                                    colMap.provider_uuid
                            )

                    String platformUuid =
                            getValue(
                                    cols,
                                    colMap.nominal_platform_uuid
                            )

                    if (!providerUuid || !platformUuid) {

                        globalErrors <<
                                "The package with the name '${name}' could not be created, because provider_uuid or nominal_platform_uuid not set!"

                        name = null
                    } else {

                        Org provider =
                                Org.findByUuid(providerUuid)

                        if (!provider) {

                            globalErrors <<
                                    "The package with the name '${name}' could not be created, because provider_uuid is wrong!"

                            name = null
                        }

                        Platform platform =
                                Platform.findByUuid(platformUuid)

                        if (!platform) {

                            globalErrors <<
                                    "The package with the name '${name}' could not be created, because nominal_platform_uuid is wrong!"

                            name = null
                        }
                    }
                }

                try {

                    /*
                     * Package erzeugen
                     */
                    if (name && pkg == null) {

                        String pkg_normname =
                                Package.generateNormname(name)

                        pkg = new Package(
                                name: name,
                                normname: pkg_normname,
                                uuid: UUID.randomUUID().toString(),
                                status: RDStore.KBC_STATUS_CURRENT
                        )

                        pkg.save(flush: true)

                        newCreated = true
                    }

                    if (pkg != null) {

                        pkg.name = name ?: pkg.name

                        /*
                         * ==================================================
                         * Provider
                         * ==================================================
                         */
                        String providerUuid =
                                getValue(
                                        cols,
                                        colMap.provider_uuid
                                )

                        if (providerUuid) {

                            Org provider =
                                    Org.findByUuid(providerUuid)

                            if (provider &&
                                    pkg.provider != provider) {

                                pkg.provider = provider
                                pkg.save(flush: true)
                            }
                        }

                        /*
                         * ==================================================
                         * Platform
                         * ==================================================
                         */
                        String platformUuid =
                                getValue(
                                        cols,
                                        colMap.nominal_platform_uuid
                                )

                        if (platformUuid) {

                            Platform platform =
                                    Platform.findByUuid(platformUuid)

                            if (platform &&
                                    pkg.nominalPlatform != platform) {

                                pkg.nominalPlatform = platform
                                pkg.save(flush: true)
                            }
                        }

                        /*
                         * ==================================================
                         * Description
                         * ==================================================
                         *
                         * WICHTIG:
                         * KEIN trim().
                         *
                         * Zeilenumbrüche innerhalb des Feldes
                         * bleiben vollständig erhalten.
                         */
                        String description =
                                getRawValue(
                                        cols,
                                        colMap.description
                                )

                        if (description != null &&
                                description != "") {

                            pkg.description = description
                        }

                        /*
                         * Description URL
                         */
                        String descriptionUrl =
                                getValue(
                                        cols,
                                        colMap.description_url
                                )

                        if (descriptionUrl) {
                            pkg.descriptionURL = descriptionUrl
                        }

                        /*
                         * ==================================================
                         * Breakable
                         * ==================================================
                         */
                        String value =
                                getValue(
                                        cols,
                                        colMap.breakable
                                )

                        if (value) {

                            RefdataValue refdataValue =
                                    RefdataCategory.lookup(
                                            RCConstants.PACKAGE_BREAKABLE,
                                            value
                                    )

                            if (refdataValue) {
                                pkg.breakable = refdataValue
                            }
                        }

                        /*
                         * Content Type
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.content_type
                                )

                        if (value) {

                            RefdataValue refdataValue =
                                    RefdataCategory.lookup(
                                            RCConstants.PACKAGE_CONTENT_TYPE,
                                            value
                                    )

                            if (refdataValue) {
                                pkg.contentType = refdataValue
                            }
                        }

                        /*
                         * File
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.file
                                )

                        if (value) {

                            RefdataValue refdataValue =
                                    RefdataCategory.lookup(
                                            RCConstants.PACKAGE_FILE,
                                            value
                                    )

                            if (refdataValue) {
                                pkg.file = refdataValue
                            }
                        }

                        /*
                         * Open Access
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.open_access
                                )

                        if (value) {

                            RefdataValue refdataValue =
                                    RefdataCategory.lookup(
                                            RCConstants.PACKAGE_OPEN_ACCESS,
                                            value
                                    )

                            if (refdataValue) {
                                pkg.openAccess = refdataValue
                            }
                        }

                        /*
                         * Payment Type
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.payment_type
                                )

                        if (value) {

                            RefdataValue refdataValue =
                                    RefdataCategory.lookup(
                                            RCConstants.PACKAGE_PAYMENT_TYPE,
                                            value
                                    )

                            if (refdataValue) {
                                pkg.paymentType = refdataValue
                            }
                        }

                        /*
                         * Scope
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.scope
                                )

                        if (value) {

                            RefdataValue refdataValue =
                                    RefdataCategory.lookup(
                                            RCConstants.PACKAGE_SCOPE,
                                            value
                                    )

                            if (refdataValue) {
                                pkg.scope = refdataValue
                            }
                        }

                        /*
                         * Free Trial
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.free_trial
                                )

                        if (value) {

                            RefdataValue refdataValue =
                                    RefdataCategory.lookup(
                                            RCConstants.YN,
                                            value
                                    )

                            if (refdataValue) {
                                pkg.freeTrial = refdataValue
                            }
                        }

                        /*
                         * Free Trial Phase
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.free_trial_phase
                                )

                        if (value) {
                            pkg.freeTrialPhase = value
                        }

                        /*
                         * ==================================================
                         * National Ranges
                         * ==================================================
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.national_ranges
                                )

                        if (value) {

                            List<String> nationalRanges =
                                    value.split(',')

                            nationalRanges.each { String range ->

                                String normalized =
                                        range?.trim()

                                if (normalized) {

                                    RefdataValue refdataValue =
                                            RefdataCategory.lookup(
                                                    RCConstants.COUNTRY,
                                                    normalized
                                            )

                                    if (refdataValue &&
                                            !(refdataValue in pkg.nationalRanges)) {

                                        pkg.addToNationalRanges(
                                                refdataValue
                                        )
                                    }
                                }
                            }
                        }

                        /*
                         * ==================================================
                         * Regional Ranges
                         * ==================================================
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.regional_ranges
                                )

                        if (value) {

                            List<String> regionalRanges =
                                    value.split(',')

                            regionalRanges.each { String range ->

                                String normalized =
                                        range?.trim()

                                if (normalized) {

                                    RefdataValue refdataValue =
                                            RefdataCategory.lookup(
                                                    RCConstants.PACKAGE_REGIONAL_RANGE,
                                                    normalized
                                            )

                                    if (refdataValue &&
                                            !(refdataValue in pkg.regionalRanges)) {

                                        pkg.addToRegionalRanges(
                                                refdataValue
                                        )
                                    }
                                }
                            }
                        }

                        /*
                         * ==================================================
                         * Anbieter Produkt ID
                         * ==================================================
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.anbieter_produkt_id
                                )

                        if (value) {

                            Map identifierMap = [
                                    pkgID: pkg.id,
                                    ns   : IdentifierNamespace.PKG_ID,
                                    value: value
                            ]

                            identifiers << identifierMap
                        }

                        /*
                         * Provider Product ID
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.provider_product_id
                                )

                        if (value) {

                            Map identifierMap = [
                                    pkgID: pkg.id,
                                    ns   : IdentifierNamespace.PKG_ID,
                                    value: value
                            ]

                            identifiers << identifierMap
                        }

                        /*
                         * ==================================================
                         * DDC
                         * ==================================================
                         */
                        value =
                                getValue(
                                        cols,
                                        colMap.ddcs
                                )

                        if (value) {

                            List<String> ddcs =
                                    value.split(',')

                            ddcs.each { String ddc ->

                                ddc = ddc?.trim()

                                if (ddc) {

                                    if (ddc.toInteger() < 10) {
                                        ddc = "00${ddc}"
                                    } else if (ddc.toInteger() < 100) {
                                        ddc = "0${ddc}"
                                    }

                                    RefdataValue refdataValue =
                                            RefdataCategory.lookup(
                                                    RCConstants.DDC,
                                                    ddc
                                            )

                                    if (refdataValue &&
                                            !(refdataValue in pkg.ddcs)) {

                                        pkg.addToDdcs(
                                                refdataValue
                                        )
                                    }
                                }
                            }
                        }

                        /*
                         * Package speichern
                         */
                        if (pkg.save(flush: true) ||
                                pkg.isAttached()) {

                            /*
                             * ==================================================
                             * Archiving Agency
                             * ==================================================
                             */
                            value =
                                    getValue(
                                            cols,
                                            colMap.archiving_agency
                                    )

                            if (value) {

                                RefdataValue refdataValue =
                                        RefdataCategory.lookup(
                                                RCConstants.PAA_ARCHIVING_AGENCY,
                                                value
                                        )

                                if (refdataValue) {

                                    PackageArchivingAgency packageArchivingAgency =
                                            PackageArchivingAgency
                                                    .findByPkgAndArchivingAgency(
                                                            pkg,
                                                            refdataValue
                                                    )

                                    if (!packageArchivingAgency) {

                                        packageArchivingAgency =
                                                new PackageArchivingAgency(
                                                        archivingAgency:
                                                                refdataValue,
                                                        pkg:
                                                                pkg
                                                )
                                    }

                                    if (packageArchivingAgency.save(
                                            flush: true)) {

                                        /*
                                         * Open Access
                                         */
                                        String paaOp =
                                                getValue(
                                                        cols,
                                                        colMap.open_access_of_archiving_agency
                                                )

                                        if (paaOp) {

                                            RefdataValue refdataValuePaaOp =
                                                    RefdataCategory.lookup(
                                                            RCConstants.PAA_OPEN_ACCESS,
                                                            paaOp
                                                    )

                                            if (refdataValuePaaOp) {
                                                packageArchivingAgency.openAccess =
                                                        refdataValuePaaOp
                                            }
                                        }

                                        /*
                                         * Post Cancellation Access
                                         */
                                        String paaPca =
                                                getValue(
                                                        cols,
                                                        colMap.post_cancellation_access_of_archiving_agency
                                                )

                                        if (paaPca) {

                                            RefdataValue refdataValuePaaPca =
                                                    RefdataCategory.lookup(
                                                            RCConstants.PAA_POST_CANCELLATION_ACCESS,
                                                            paaPca
                                                    )

                                            if (refdataValuePaaPca) {

                                                packageArchivingAgency
                                                        .postCancellationAccess =
                                                        refdataValuePaaPca
                                            }
                                        }

                                        packageArchivingAgency.save(
                                                flush: true
                                        )
                                    }
                                }
                            }

                            /*
                             * ==================================================
                             * Curatory Groups
                             * ==================================================
                             */
                            if (curatoryGroups) {

                                curatoryGroups.each { CuratoryGroup cg ->

                                    if (!(pkg.curatoryGroups &&
                                            cg.id in pkg
                                            .curatoryGroups
                                            .curatoryGroup
                                            .id)) {

                                        new CuratoryGroupPackage(
                                                pkg: pkg,
                                                curatoryGroup: cg
                                        ).save(flush: true)
                                    }
                                }
                            }

                            /*
                             * ==================================================
                             * Source
                             * ==================================================
                             */
                            String sourceUrl =
                                    getValue(
                                            cols,
                                            colMap.source_url
                                    )

                            String sourceFtpServerUrl =
                                    getValue(
                                            cols,
                                            colMap.source_ftp_server_url
                                    )

                            if (sourceUrl ||
                                    sourceFtpServerUrl) {

                                Map sourceMap = [:]

                                if (sourceUrl) {
                                    sourceMap.url = sourceUrl
                                }

                                /*
                                 * Supply Method
                                 */
                                value =
                                        getValue(
                                                cols,
                                                colMap.source_default_supply_method
                                        )

                                if (value) {

                                    RefdataValue refdataValue =
                                            RefdataCategory.lookup(
                                                    RCConstants.SOURCE_DATA_SUPPLY_METHOD,
                                                    value
                                            )

                                    if (refdataValue) {

                                        sourceMap.source_default_supply_method =
                                                refdataValue.id
                                    }
                                }

                                /*
                                 * FTP
                                 */
                                if (sourceFtpServerUrl) {

                                    sourceMap.source_ftp_server_url =
                                            sourceFtpServerUrl
                                }

                                value =
                                        getValue(
                                                cols,
                                                colMap.source_ftp_directory
                                        )

                                if (value) {
                                    sourceMap.source_ftp_directory =
                                            value
                                }

                                value =
                                        getValue(
                                                cols,
                                                colMap.source_ftp_file_name
                                        )

                                if (value) {
                                    sourceMap.source_ftp_file_name =
                                            value
                                }

                                value =
                                        getValue(
                                                cols,
                                                colMap.source_ftp_username
                                        )

                                if (value) {
                                    sourceMap.source_ftp_username =
                                            value
                                }

                                value =
                                        getValue(
                                                cols,
                                                colMap.source_ftp_password
                                        )

                                if (value) {
                                    sourceMap.source_ftp_password =
                                            value
                                }

                                sourceMap.pkgID = pkg.id

                                /*
                                 * Frequency
                                 */
                                value =
                                        getValue(
                                                cols,
                                                colMap.frequency
                                        )

                                if (value) {

                                    RefdataValue refdataValue =
                                            RefdataCategory.lookup(
                                                    RCConstants.SOURCE_FREQUENCY,
                                                    value
                                            )

                                    if (refdataValue) {
                                        sourceMap.frequency =
                                                refdataValue.id
                                    }
                                }

                                /*
                                 * Automated Updates
                                 */
                                value =
                                        getValue(
                                                cols,
                                                colMap.automated_updates
                                        )

                                if (value) {

                                    RefdataValue refdataValue =
                                            RefdataCategory.lookup(
                                                    RCConstants.YN,
                                                    value
                                            )

                                    if (refdataValue) {

                                        sourceMap.automaticUpdates =
                                                refdataValue ==
                                                        RDStore.YN_YES
                                    }
                                }

                                sources << sourceMap
                            }

                            /*
                             * ==================================================
                             * TIPP
                             * ==================================================
                             */
                            if (!package_uuid ||
                                    pkg.getTippCount() == 0) {

                                String publicationTitle =
                                        getValue(
                                                cols,
                                                colMap.publication_title
                                        )

                                String publicationTypeValue =
                                        getValue(
                                                cols,
                                                colMap.publication_type
                                        )

                                String titleUrl =
                                        getValue(
                                                cols,
                                                colMap.title_url
                                        )

                                if (pkg &&
                                        pkg.nominalPlatform &&
                                        publicationTitle &&
                                        publicationTypeValue &&
                                        titleUrl) {

                                    RefdataValue publicationType =
                                            RefdataCategory.lookup(
                                                    RCConstants.TIPP_PUBLICATION_TYPE,
                                                    publicationTypeValue
                                            )

                                    TitleInstancePackagePlatform
                                    titleInstancePackagePlatform =
                                            new TitleInstancePackagePlatform(
                                                    pkg:
                                                            pkg,
                                                    platform:
                                                            pkg.nominalPlatform,
                                                    name:
                                                            publicationTitle,
                                                    url:
                                                            titleUrl,
                                                    publicationType:
                                                            publicationType ?:
                                                                    RDStore.TIPP_PUBLIC_TYPE_NOSET,
                                                    status:
                                                            RDStore.KBC_STATUS_CURRENT,
                                                    uuid:
                                                            UUID.randomUUID()
                                                                    .toString()
                                            )

                                    titleInstancePackagePlatform.save()

                                    /*
                                     * wie bisher zunächst
                                     * provider_product_id
                                     */
                                    String title_id =
                                            getValue(
                                                    cols,
                                                    colMap.provider_product_id
                                            )

                                    /*
                                     * title_id überschreibt es,
                                     * falls vorhanden
                                     */
                                    String importedTitleId =
                                            getValue(
                                                    cols,
                                                    colMap.title_id
                                            )

                                    if (importedTitleId) {
                                        title_id =
                                                importedTitleId
                                    }

                                    if (title_id &&
                                            titleInstancePackagePlatform) {

                                        IdentifierNamespace ns =
                                                IdentifierNamespace
                                                        .findByValueAndTargetType(
                                                                'title_id',
                                                                RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP
                                                        )

                                        Identifier identifier =
                                                new Identifier(
                                                        namespace:
                                                                ns,
                                                        value:
                                                                title_id,
                                                        tipp:
                                                                titleInstancePackagePlatform
                                                )

                                        identifier.save(
                                                flush: true
                                        )
                                    }
                                }
                            }
                        }

                        packageList << pkg
                    }

                }
                catch (Exception e) {

                    if (pkg &&
                            newCreated) {

                        deletionService.expungePkg(
                                pkg.id
                        )
                    }

                    log.error(
                            "Error on package with the name '${name}': ${e.message}",
                            e
                    )

                    globalErrors <<
                            "Error on package with the name '${name}'. Please try again!"
                }
            }
        }

        /*
         * ==========================================================
         * Identifier speichern / aktualisieren
         * ==========================================================
         */
        IdentifierNamespace namespace =
                IdentifierNamespace.findByValueAndTargetType(
                        IdentifierNamespace.PKG_ID,
                        RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_PACKAGE
                )

        identifiers.each { Map map ->

            boolean found = false

            Package aPackage =
                    Package.get(
                            map.pkgID
                    )

            if (aPackage) {

                aPackage.ids.each { Identifier identifier ->

                    if (identifier.namespace.value ==
                            map.ns) {

                        if (map.value &&
                                identifier.value != map.value) {

                            identifier =
                                    identifier.refresh()

                            identifier.value =
                                    map.value

                            identifier.save(
                                    flush: true
                            )
                        }

                        found = true
                    }
                }

                if (!found &&
                        map.value) {

                    Identifier identifier =
                            new Identifier(
                                    namespace:
                                            namespace,
                                    value:
                                            map.value,
                                    pkg:
                                            aPackage
                            )

                    identifier.save(
                            flush: true
                    )
                }
            }
        }

        /*
         * ==========================================================
         * KBART Sources
         * ==========================================================
         */
        sources.each { Map map ->

            KbartSource kbartSource

            Package aPackage =
                    Package.get(
                            map.pkgID
                    )

            if (aPackage) {

                /*
                 * Neue Source
                 */
                if (aPackage.kbartSource == null) {

                    def dupes =
                            KbartSource
                                    .findAllByNameIlikeAndStatusNotInList(
                                            aPackage.name,
                                            statusList
                                    )

                    String sourceName =
                            aPackage.name

                    if (dupes &&
                            dupes.size() > 0) {

                        sourceName =
                                "${sourceName} ${dupes.size() + 1}"
                    }

                    dupes.each { KbartSource source ->

                        if (!Package.findByKbartSource(
                                source)) {

                            source.status =
                                    RDStore.KBC_STATUS_REMOVED

                            source.save(
                                    flush: true
                            )
                        }
                    }

                    kbartSource =
                            new KbartSource(
                                    name:
                                            sourceName,
                                    uuid:
                                            UUID.randomUUID()
                                                    .toString(),
                                    status:
                                            RDStore.KBC_STATUS_CURRENT,
                                    kbartHasWekbFields:
                                            false
                            )
                } else {

                    kbartSource =
                            aPackage.kbartSource

                    def dupes =
                            KbartSource
                                    .findAllByNameIlikeAndStatusNotInList(
                                            aPackage.name,
                                            statusList
                                    )

                    String sourceName =
                            aPackage.name

                    if (dupes &&
                            dupes.size() > 0) {

                        sourceName =
                                "${sourceName} ${dupes.size() + 1}"
                    }

                    if (sourceName !=
                            kbartSource.name) {

                        kbartSource.name =
                                sourceName
                    }
                }

                /*
                 * URL
                 */
                if (map.url) {

                    if (map.url !=
                            kbartSource.url) {

                        kbartSource.lastRun =
                                null

                        kbartSource.lastUpdateUrl =
                                null
                    }

                    kbartSource.url =
                            map.url
                }

                /*
                 * Frequency
                 */
                if (map.frequency) {

                    kbartSource.frequency =
                            RefdataValue.get(
                                    map.frequency
                            )
                }

                /*
                 * Supply method
                 */
                if (map.source_default_supply_method) {

                    kbartSource.defaultSupplyMethod =
                            RefdataValue.get(
                                    map.source_default_supply_method
                            )
                }

                /*
                 * FTP
                 */
                if (map.source_ftp_server_url) {

                    kbartSource.ftpServerUrl =
                            map.source_ftp_server_url
                }

                if (map.source_ftp_directory) {

                    kbartSource.ftpDirectory =
                            map.source_ftp_directory
                }

                if (map.source_ftp_file_name) {

                    kbartSource.ftpFileName =
                            map.source_ftp_file_name
                }

                if (map.source_ftp_username) {

                    kbartSource.ftpUsername =
                            map.source_ftp_username
                }

                if (map.source_ftp_password) {

                    kbartSource.ftpPassword =
                            map.source_ftp_password
                }

                /*
                 * WICHTIG:
                 *
                 * containsKey() statt:
                 *
                 * if(map.automaticUpdates)
                 *
                 * sonst würde false niemals gespeichert.
                 */
                if (map.containsKey(
                        'automaticUpdates')) {

                    kbartSource.automaticUpdates =
                            map.automaticUpdates
                }

                if (kbartSource.save(flush: true) ||
                        kbartSource.isAttached()) {

                    /*
                     * Curatory Groups
                     */
                    if (curatoryGroups) {

                        curatoryGroups.each { CuratoryGroup cg ->

                            if (!(kbartSource.curatoryGroups &&
                                    cg.id in kbartSource
                                    .curatoryGroups
                                    .curatoryGroup
                                    .id)) {

                                new CuratoryGroupKbartSource(
                                        kbartSource:
                                                kbartSource,
                                        curatoryGroup:
                                                cg
                                ).save(
                                        flush: true
                                )
                            }
                        }
                    }

                    aPackage =
                            aPackage.refresh()

                    aPackage.kbartSource =
                            kbartSource

                    aPackage.lastUpdated =
                            new Date()

                    aPackage.save(
                            flush: true
                    )
                }
            }
        }

        /*
         * ==========================================================
         * Ergebnis
         * ==========================================================
         */
        List<Package> packages = []

        packageList.each { Package pkg ->

            if (pkg?.id) {

                packages <<
                        Package.get(
                                pkg.id
                        )
            }
        }

        return [
                packages : packages,
                rowsCount: rows.size(),
                errors   : globalErrors
        ]
    }


    private String getValue(
            CSVRecord cols,
            Integer index) {

        if (cols == null ||
                index == null ||
                index < 0 ||
                index >= cols.size()) {

            return null
        }

        return cols.get(index)?.trim()
    }


    private String getRawValue(
            CSVRecord cols,
            Integer index) {

        if (cols == null ||
                index == null ||
                index < 0 ||
                index >= cols.size()) {

            return null
        }

        return cols.get(index)
    }

    FlashScope getCurrentFlashScope() {
        GrailsWebRequest grailsWebRequest = WebUtils.retrieveGrailsWebRequest()
        grailsWebRequest.getFlashScope()
    }
}

package wekb

import grails.web.mvc.FlashScope
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
                                log.debug("Scalar property");
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

                                }else if ( pprop.getType().name == 'boolean' ) {
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

                    if (result.newobj instanceof TitleInstancePackagePlatform && (params.pkg == null || params.hostPlatform == null || params.url == null || params.name == null)) {
                        result.errors=["Please fill Package, Platform and Host Platform URL to create the component."]
                    }
                    else if (!propertyWasSet) {
                        // Add an error message here if no property was set via data sent through from the form.
                        log.debug("No properties set");
                        result.errors=["Please fill in at least one piece of information to create the component."]
                    } else {

                        log.debug("Saving..")

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
        if(user.curatoryGroupUsers) {
            user.curatoryGroupUsers.curatoryGroup.each { CuratoryGroup cg ->
              curatoryGroups << cg
            }
        }

        Map colMap = [:]
        Set<String> globalErrors = []
        List<Package> packageList = []

        InputStream fileContent = tsvFile.getInputStream()
        List<String> rows = fileContent.text.split('\n')

        rows[0].split('\t').eachWithIndex { String s, int c ->
            String headerCol = s.trim()
            if(headerCol.startsWith("\uFEFF"))
                headerCol = headerCol.substring(1)
            //important: when processing column headers, grab those which are reserved; default case: check if it is a name of a property definition; if there is no result as well, reject.
            switch(headerCol.toLowerCase()) {
                case "package_name": colMap.name = c
                    break
                case "package_uuid": colMap.package_uuid = c
                    break
                case "provider_uuid": colMap.provider_uuid = c
                    break
                case "nominal_platform_uuid": colMap.nominal_platform_uuid = c
                    break
                case "description": colMap.description = c
                    break
                case "url": colMap.description_url = c
                    break
                case "description_url": colMap.description_url = c
                    break
                case "breakable": colMap.breakable = c
                    break
                case "consistent": colMap.consistent = c
                    break
                case "content_type": colMap.content_type = c
                    break
                case "file": colMap.file = c
                    break
                case "open_access": colMap.open_access = c
                    break
                case "payment_type": colMap.payment_type = c
                    break
                case "scope": colMap.scope = c
                    break
                case "editing_status": colMap.editing_status = c
                    break
                case "national_range": colMap.national_ranges = c
                    break
                case "regional_range": colMap.regional_ranges = c
                    break
                case "anbieter_produkt_id": colMap.anbieter_produkt_id = c
                    break
                case "provider_product_id": colMap.provider_product_id = c
                    break
                case "ddc": colMap.ddcs = c
                    break
                case "source_url": colMap.source_url = c
                    break
                case "frequency": colMap.frequency = c
                    break
                case "automated_updates": colMap.automated_updates = c
                    break
                case 'archiving_agency': colMap.archiving_agency = c
                    break
                case 'open_access_of_archiving_agency': colMap.open_access_of_archiving_agency = c
                    break
                case 'post_cancellation_access_of_archiving_agency': colMap.post_cancellation_access_of_archiving_agency = c
                    break
                case 'source_ftp_server_url': colMap.source_ftp_server_url = c
                    break
                case 'source_ftp_directory': colMap.source_ftp_directory = c
                    break
                case 'source_ftp_file_name': colMap.source_ftp_file_name = c
                    break
                case 'source_ftp_username': colMap.source_ftp_username = c
                    break
                case 'source_ftp_password': colMap.source_ftp_password = c
                    break
                case "source_default_supply_method": colMap.source_default_supply_method = c
                    break
            }
        }

        rows.remove(0)

        RefdataValue status_deleted = RDStore.KBC_STATUS_DELETED
        List identifiers = []
        List sources = []

        rows.each { row ->
            List<String> cols = row.split('\t')
            boolean newCreated = false
            Package pkg
            boolean editAllowed = true
            if (colMap.package_uuid != null) {
                String package_uuid = cols[colMap.package_uuid].trim()
                pkg = package_uuid ? Package.findByUuid(package_uuid) : null

                if (pkg != null && !accessService.checkEditableObject(pkg, null)){
                    globalErrors << "You have no authorization to edit the package with the uuid '${package_uuid}'.!"
                    editAllowed = false
                }

            }

            if ((colMap.name != null || pkg != null) && editAllowed) {

                String name = cols[colMap.name].trim()

                if(pkg == null) {
                    def dupes = []
                    if (curatoryGroups && colMap.anbieter_produkt_id != null) {
                        String value = cols[colMap.anbieter_produkt_id].trim()
                        if (value) {
                            IdentifierNamespace namespace = IdentifierNamespace.findByValueAndTargetType(IdentifierNamespace.PKG_ID, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_PACKAGE)
                            dupes = Identifier.executeQuery('select pkg from Identifier where namespace = :ns and value != :val and pkg is not null and pkg.status not in (:stat) and exists ( select cgp from CuratoryGroupPackage cgp where cgp.pkg = pkg and cgp.curatoryGroup in (:curGroup))', [ns: namespace, val: 'Unknown', stat: status_deleted, curGroup: curatoryGroups])
                        }else{
                            dupes = Package.executeQuery("select p from Package as p where lower(name) like :name and status not in (:stat) and exists ( select cgp from CuratoryGroupPackage cgp where cgp.pkg = p and cgp.curatoryGroup in (:curGroup))", [name: name.toLowerCase().trim(), stat: status_deleted, curGroup: curatoryGroups])
                        }
                    }else {
                        dupes = Package.executeQuery("select p from Package as p where lower(name) like :name and status not in (:stat)", [name: name.toLowerCase().trim(), stat: status_deleted])

                        if (curatoryGroups) {
                            dupes = Package.executeQuery("select p from Package as p where lower(name) like :name and status not in (:stat) and exists ( select cgp from CuratoryGroupPackage cgp where cgp.pkg = p and cgp.curatoryGroup in (:curGroup))", [name: name.toLowerCase().trim(), stat: status_deleted, curGroup: curatoryGroups])
                        }
                    }

                    if (dupes && dupes.size() > 0) {
                        globalErrors << "The we:kb already has a package with the name '${name}'. Therefore a package with the name could not be created!"
                        name = null
                    }
                }
                try {

                    if (name && pkg == null) {
                        String pkg_normname = Package.generateNormname(name)
                        pkg = new Package(name: name, normname: pkg_normname, uuid: UUID.randomUUID().toString(), status: RDStore.KBC_STATUS_CURRENT)
                        pkg.save(flush: true)
                        newCreated = true
                    }

                    if(pkg != null) {
                        pkg.name = name ?: pkg.name

                        if (colMap.provider_uuid != null && cols[colMap.provider_uuid]) {
                            Org provider = Org.findByUuid(cols[colMap.provider_uuid].trim())
                            if (provider){
                                if(!(pkg.provider && pkg.provider == provider)){
                                    pkg.provider = provider
                                    pkg.save(flush: true)

                                }
                            }

                        }

                        if (colMap.nominal_platform_uuid != null && cols[colMap.nominal_platform_uuid]) {
                            Platform platform = Platform.findByUuid(cols[colMap.nominal_platform_uuid].trim())
                            if (platform){
                                if(!(pkg.nominalPlatform && pkg.nominalPlatform == platform)){
                                    pkg.nominalPlatform = platform
                                    pkg.save(flush: true)

                                }
                            }

                        }

                        if (colMap.description != null && cols[colMap.description]) {
                            pkg.description = cols[colMap.description].trim()
                        }

                        if (colMap.description_url != null && cols[colMap.description_url]) {
                            pkg.descriptionURL = cols[colMap.description_url].trim()
                        }

                        if (colMap.breakable != null) {
                            String value = cols[colMap.breakable].trim()
                            if (value) {
                                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_BREAKABLE, value)
                                if (refdataValue)
                                    pkg.breakable = refdataValue
                            }
                        }

                        /* if (colMap.consistent != null) {
                            String value = cols[colMap.consistent].trim()
                            if (value) {
                              RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_CONSISTENT, value)
                              if (refdataValue)
                                pkg.consistent = refdataValue
                            }
                          }*/

                        if (colMap.content_type != null) {
                            String value = cols[colMap.content_type].trim()
                            if (value) {
                                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_CONTENT_TYPE, value)
                                if (refdataValue)
                                    pkg.contentType = refdataValue
                            }
                        }

                        if (colMap.file != null) {
                            String value = cols[colMap.file].trim()
                            if (value) {
                                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_FILE, value)
                                if (refdataValue)
                                    pkg.file = refdataValue
                            }
                        }

                        if (colMap.open_access != null) {
                            String value = cols[colMap.open_access].trim()
                            if (value) {
                                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_OPEN_ACCESS, value)
                                if (refdataValue)
                                    pkg.openAccess = refdataValue
                            }
                        }

                        if (colMap.payment_type != null) {
                            String value = cols[colMap.payment_type].trim()
                            if (value) {
                                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_PAYMENT_TYPE, value)
                                if (refdataValue)
                                    pkg.paymentType = refdataValue
                            }
                        }

                        if (colMap.scope != null) {
                            String value = cols[colMap.scope].trim()
                            if (value) {
                                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_SCOPE, value)
                                if (refdataValue)
                                    pkg.scope = refdataValue
                            }
                        }

                        if (colMap.national_ranges && cols[colMap.national_ranges]) {
                            List<String> national_ranges = cols[colMap.national_ranges].split(',')
                            national_ranges.each { String value ->
                                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.COUNTRY, value.trim())
                                if (refdataValue && !(refdataValue in pkg.nationalRanges)) {
                                    pkg.addToNationalRanges(refdataValue)
                                }
                            }
                        }

                        if (colMap.regional_ranges && cols[colMap.regional_ranges]) {
                            List<String> regional_ranges = cols[colMap.regional_ranges].split(',')
                            regional_ranges.each { String value ->
                                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_REGIONAL_RANGE, value.trim())
                                if (refdataValue && !(refdataValue in pkg.regionalRanges)) {
                                    pkg.addToRegionalRanges(refdataValue)
                                }
                            }
                        }

                        if (colMap.anbieter_produkt_id != null) {
                            String value = cols[colMap.anbieter_produkt_id].trim()
                            if (value) {

                                Map idenitiferMap = [:]
                                idenitiferMap.pkgID = pkg.id
                                idenitiferMap.ns = IdentifierNamespace.PKG_ID
                                idenitiferMap.value = value

                                identifiers << idenitiferMap

                            }
                        }

                        if (colMap.provider_product_id != null) {
                            String value = cols[colMap.provider_product_id].trim()
                            if (value) {

                                Map idenitiferMap = [:]
                                idenitiferMap.pkgID = pkg.id
                                idenitiferMap.ns = IdentifierNamespace.PKG_ID
                                idenitiferMap.value = value

                                identifiers << idenitiferMap

                            }
                        }

                        if (colMap.ddcs && cols[colMap.ddcs]) {
                            List<String> ddcs = cols[colMap.ddcs].split(',')
                            ddcs.each { String value ->
                                value = value.trim()
                                if (value != "") {
                                    if (value.toInteger() < 10) {
                                        value = "00" + value
                                    }

                                    if (value.toInteger() >= 10 && value.toInteger() < 100) {
                                        value = "0" + value
                                    }
                                    RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, value)
                                    if (refdataValue && !(refdataValue in pkg.ddcs)) {
                                        pkg.addToDdcs(refdataValue)
                                    }
                                }
                            }
                        }


                        if (pkg.save(flush: true) || pkg.isAttached()) {
                            if (colMap.archiving_agency != null) {
                                String value = cols[colMap.archiving_agency].trim()
                                if (value) {
                                    RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PAA_ARCHIVING_AGENCY, value)
                                    if (refdataValue){
                                        PackageArchivingAgency packageArchivingAgency
                                        packageArchivingAgency = PackageArchivingAgency.findByPkgAndArchivingAgency(pkg, refdataValue)
                                        if(!packageArchivingAgency) {
                                            packageArchivingAgency = new PackageArchivingAgency(archivingAgency: refdataValue, pkg: pkg)
                                        }
                                        if (packageArchivingAgency.save(flush: true)) {
                                            if (colMap.open_access_of_archiving_agency != null) {
                                                String paaOp = cols[colMap.open_access_of_archiving_agency].trim()
                                                if (paaOp) {
                                                    RefdataValue refdataValuePaaOP = RefdataCategory.lookup(RCConstants.PAA_OPEN_ACCESS, paaOp)
                                                    if (refdataValuePaaOP)
                                                        packageArchivingAgency.openAccess = refdataValuePaaOP
                                                }
                                            }

                                            if (colMap.post_cancellation_access_of_archiving_agency != null) {
                                                String paaPCA = cols[colMap.post_cancellation_access_of_archiving_agency].trim()
                                                if (paaPCA) {
                                                    RefdataValue refdataValuePaaPCA = RefdataCategory.lookup(RCConstants.PAA_POST_CANCELLATION_ACCESS, paaPCA)
                                                    if (refdataValuePaaPCA)
                                                        packageArchivingAgency.postCancellationAccess = refdataValuePaaPCA
                                                }
                                            }
                                            packageArchivingAgency.save(flush: true)
                                        }

                                    }
                                }
                            }

                            if(curatoryGroups) {
                                curatoryGroups.each { CuratoryGroup cg ->
                                    if (!(pkg.curatoryGroups && cg.id in pkg.curatoryGroups.curatoryGroup.id)) {
                                        new CuratoryGroupPackage(pkg: pkg, curatoryGroup: cg).save(flush: true)
                                    }
                                }
                            }

                            if (colMap.source_url != null || colMap.source_ftp_server_url != null) {
                                String source_url = cols[colMap.source_url].trim()
                                String source_ftp_server_url = cols[colMap.source_ftp_server_url].trim()
                                if (source_url || source_ftp_server_url) {
                                    Map sourceMap = [:]
                                    if(source_url) {
                                        sourceMap.url = source_url
                                    }

                                    if(colMap.source_default_supply_method != null && cols[colMap.source_default_supply_method]){
                                        String value = cols[colMap.source_default_supply_method].trim()
                                        if (value) {
                                            RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.SOURCE_DATA_SUPPLY_METHOD, value)
                                            if (refdataValue)
                                                sourceMap.source_default_supply_method = refdataValue.id
                                        }
                                    }

                                    if(source_ftp_server_url) {
                                        sourceMap.source_ftp_server_url = source_ftp_server_url
                                    }

                                    if(colMap.source_ftp_directory != null && cols[colMap.source_ftp_directory]){
                                        sourceMap.source_ftp_directory = cols[colMap.source_ftp_directory].trim()
                                    }
                                    if(colMap.source_ftp_file_name != null && cols[colMap.source_ftp_file_name]){
                                        sourceMap.source_ftp_file_name = cols[colMap.source_ftp_file_name].trim()
                                    }
                                    if(colMap.source_ftp_username != null && cols[colMap.source_ftp_username]){
                                        sourceMap.source_ftp_username = cols[colMap.source_ftp_username].trim()
                                    }
                                    if(colMap.source_ftp_password != null && cols[colMap.source_ftp_password]){
                                        sourceMap.source_ftp_password = cols[colMap.source_ftp_password].trim()
                                    }

                                    sourceMap.pkgID = pkg.id

                                    if (colMap.frequency != null) {
                                        String value = cols[colMap.frequency].trim()
                                        if (value) {
                                            RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.SOURCE_FREQUENCY, value)
                                            if (refdataValue)
                                                sourceMap.frequency = refdataValue.id
                                        }
                                    }

                                    if (colMap.automated_updates != null) {
                                        String value = cols[colMap.automated_updates].trim()
                                        if (value) {
                                            RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.YN, value)
                                            if (refdataValue) {
                                                sourceMap.automaticUpdates = (refdataValue.value == "Yes") ? true : false
                                            }

                                            /*if (refdataValue && refdataValue.value == "Yes") {
                                                String charset = (('a'..'z') + ('0'..'9')).join()
                                                String tokenValue = RandomStringUtils.random(255, charset.toCharArray())
                                                if (!UpdateToken.findByPkg(pkg)) {
                                                    UpdateToken newToken = new UpdateToken(pkg: pkg, updateUser: user, value: tokenValue).save(flush: true)
                                                }
                                            }*/

                                        }
                                    }

                                    sources << sourceMap
                                }
                            }
                        }

                        packageList << pkg

                    }

                }catch ( Exception e ) {

                    if(pkg && newCreated){
                        pkg.expunge()
                    }
                    log.error("Error on package with the name '${name}':" + e.printStackTrace())
                    globalErrors << "Error on package with the name '${name}'. Please try agian!"
                }
            }
        }

        IdentifierNamespace namespace = IdentifierNamespace.findByValueAndTargetType(IdentifierNamespace.PKG_ID, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_PACKAGE)
        identifiers.each { Map map ->
            boolean found = false
            Package aPackage = Package.get(map.pkgID)
            if(aPackage) {
                    aPackage.ids.each { Identifier identifier ->
                        if (identifier.namespace.value == map.ns) {
                            if (map.value != "" && identifier.value != map.value) {
                                identifier = identifier.refresh()
                                identifier.value = map.value
                                identifier.save(flush: true)
                            }
                            found = true
                        }
                    }
                    if (!found && map.value != "") {
                        Identifier identifier = new Identifier(namespace: namespace, value: map.value, pkg: aPackage)
                        identifier.save(flush: true)
                    }
                }
        }

        sources.each { Map map ->
            KbartSource kbartSource
            Package aPackage = Package.get(map.pkgID)

            if(aPackage) {
                if (aPackage.kbartSource == null) {
                    def dupes = KbartSource.findAllByNameIlikeAndStatusNotEqual(aPackage.name, status_deleted)
                    String sourceName = aPackage.name
                    if (dupes && dupes.size() > 0) {
                        sourceName = "${sourceName} ${dupes.size() + 1}"
                    }

                    dupes.each {
                        if(!Package.findByKbartSource(it)){
                            it.status = RDStore.KBC_STATUS_REMOVED
                            it.save(flush: true)
                        }
                    }

                    kbartSource = new KbartSource(name: sourceName, uuid: UUID.randomUUID().toString(), status: RDStore.KBC_STATUS_CURRENT, kbartHasWekbFields: false)
                } else {
                    kbartSource = aPackage.kbartSource
                    def dupes = KbartSource.findAllByNameIlikeAndStatusNotEqual(aPackage.name, status_deleted)
                    String sourceName = aPackage.name
                    if (dupes && dupes.size() > 0) {
                        sourceName = "${sourceName} ${dupes.size() + 1}"
                    }

                    if(sourceName != kbartSource.name){
                        kbartSource.name = sourceName
                    }
                }

                if (map.url) {
                    if(map.url != kbartSource.url){
                        kbartSource.lastRun = null
                        kbartSource.lastUpdateUrl = null
                    }

                    kbartSource.url = map.url
                }

                if (map.frequency) {
                    kbartSource.frequency = RefdataValue.get(map.frequency)
                }

                if (map.source_default_supply_method) {
                    kbartSource.defaultSupplyMethod = RefdataValue.get(map.source_default_supply_method)
                }

                if (map.source_ftp_server_url) {
                    kbartSource.ftpServerUrl = map.source_ftp_server_url
                }

                if (map.source_ftp_directory) {
                    kbartSource.ftpDirectory = map.source_ftp_directory
                }

                if (map.source_ftp_file_name) {
                    kbartSource.ftpFileName = map.source_ftp_file_name
                }

                if (map.source_ftp_username) {
                    kbartSource.ftpUsername = map.source_ftp_username
                }

                if (map.source_ftp_password) {
                    kbartSource.ftpPassword = map.source_ftp_password
                }

                if (map.automaticUpdates) {
                    kbartSource.automaticUpdates = map.automaticUpdates
                }

                if (map.targetNamespace) {
                    kbartSource.targetNamespace = IdentifierNamespace.get(map.targetNamespace)
                }
                if (kbartSource.save(flush: true) || kbartSource.isAttached()) {

                    if(curatoryGroups) {
                        curatoryGroups.each { CuratoryGroup cg ->
                            if (!(kbartSource.curatoryGroups && cg.id in kbartSource.curatoryGroups.curatoryGroup.id)) {
                                new CuratoryGroupKbartSource(kbartSource: kbartSource, curatoryGroup: cg).save(flush: true)
                            }
                        }
                    }
                    aPackage = aPackage.refresh()
                    aPackage.kbartSource = kbartSource
                    aPackage.lastUpdated = new Date()
                    aPackage.save(flush: true)
                }
            }
        }

        List<Package> packages = []
        packageList.each {
            packages << Package.get(it.id)
        }


        [packages: packages, rowsCount: rows.size(), errors: globalErrors]
    }

    FlashScope getCurrentFlashScope() {
        GrailsWebRequest grailsWebRequest = WebUtils.retrieveGrailsWebRequest()
        grailsWebRequest.getFlashScope()
    }
}

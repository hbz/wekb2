package wekb

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
        // II: Defaulting this to true - don't like it much, but we need to be able to create a title without any
        // props being set... not ideal, but issue closing.
        boolean propertyWasSet = true

        Locale locale = LocaleContextHolder.getLocale()

        User user = springSecurityService.currentUser

        if ( params.cls ) {

            GrailsClass newclass = grailsApplication.getArtefact("Domain",params.cls)
            PersistentEntity pent = grailsApplication.mappingContext.getPersistentEntity(params.cls)
            // def refdata_properties = classExaminationService.getRefdataPropertyNames(params.cls)
            log.debug("Got entity ${pent} for ${newclass.name}")

            if ( newclass ) {
                try {
                    result.newobj = newclass.newInstance()
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
                                    def sdf = new java.text.SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
                                    def incoming = p.value.substring(0,31) + ":" + p.value.substring(31, 33)
                                    Instant instant = sdf.parse(incoming).toInstant()
                                    LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of("GMT"))

                                    result.newobj[p.key] = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
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

                        log.debug("Saving..");
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

                            log.debug("Setting combos..");

                            if (result.newobj instanceof KBComponent) {
                                // The save completed OK.. if we want to be really cool, we can now loop through the properties
                                // and set any combos on the object
                                boolean changed=false
                                params.each { p ->
                                    def combo_properties = result.newobj.getComboTypeValue(p.key)

                                    if ( combo_properties != null ) {
                                        log.debug("Deal with a combo doodah ${p.key}:${p.value}");
                                        if ( ( p.value != "") && ( p.value != null ) ) {
                                            def related_item = genericOIDService.resolveOID(p.value);
                                            result.newobj[p.key] = related_item
                                            changed = true
                                        }
                                    }
                                    result.newobj.save()
                                }
                            }

                            if (result.newobj.respondsTo("getCuratoryGroups")) {
                                log.debug("Set CuratoryGroups..");
                                if(user.isAdmin() || user.getSuperUserStatus()) {
                                    result.message = "Object was not assigned to a curator group because you are admin or superuser!!!!"

                                }else {
                                    if(user.curatoryGroupUsers) {
                                        user.curatoryGroupUsers.curatoryGroup.each { CuratoryGroup cg ->
                                            result.newobj.curatoryGroups.add(cg)
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
                    log.error("Problem",e);
                    result.errors = ["Could not create component!"]
                }
            }
        }
        return result
    }

    Map packageBatchImport(MultipartFile tsvFile, User user) {
        Map colMap = [:]
        Set<String> globalErrors = []
        List<Package> packageList = []
        RefdataValue combo_type_id = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'KBComponent.Ids')
        RefdataValue combo_type_status = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_ACTIVE)

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
                case "url": colMap.url = c
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
                case "title_id_namespace": colMap.title_id_namespace = c
                    break
                case "automated_updates": colMap.automated_updates = c
                    break
                case 'archiving_agency': colMap.archiving_agency = c
                    break
                case 'open_access_of_archiving_agency': colMap.open_access_of_archiving_agency = c
                    break
                case 'post_cancellation_access_of_archiving_agency': colMap.post_cancellation_access_of_archiving_agency = c
                    break
            }
        }

        rows.remove(0)

        RefdataValue status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
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
                    def dupes = Package.findAllByNameIlikeAndStatusNotEqual(name, status_deleted)

                    if (dupes && dupes.size() > 0) {
                        globalErrors << "The we:kb already has a package with the name '${name}'. Therefore a package with the name could not be created!"
                        name = null
                    }
                }
                try {

                    if (name && pkg == null) {
                        String pkg_normname = Package.generateNormname(name)
                        pkg = new Package(name: name, normname: pkg_normname)
                        pkg.save()
                        newCreated = true
                    }

                    if(pkg != null) {
                        pkg.name = name ?: pkg.name

                        if (colMap.provider_uuid != null && cols[colMap.provider_uuid]) {
                            Org provider = Org.findByUuid(cols[colMap.provider_uuid].trim())
                            if (provider){
                                if(!(pkg.provider && pkg.provider == provider)){
                                    def combo_type = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Provider')
                                    def current_combo = Combo.findByFromComponentAndType(pkg, combo_type)

                                    if (current_combo) {
                                        current_combo.delete()
                                    }

                                    def new_combo = new Combo(fromComponent: pkg, toComponent: provider, type: combo_type).save()

                                }
                            }

                        }

                        if (colMap.nominal_platform_uuid != null && cols[colMap.nominal_platform_uuid]) {
                            Platform platform = Platform.findByUuid(cols[colMap.nominal_platform_uuid].trim())
                            if (platform){
                                if(!(pkg.nominalPlatform && pkg.nominalPlatform == platform)){
                                    def combo_type = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.NominalPlatform')
                                    def current_combo = Combo.findByFromComponentAndType(pkg, combo_type)

                                    if (current_combo) {
                                        current_combo.delete()
                                    }

                                    def new_combo = new Combo(fromComponent: pkg, toComponent: platform, type: combo_type).save()

                                }
                            }

                        }

                        if (colMap.description != null && cols[colMap.description]) {
                            pkg.description = cols[colMap.description].trim()
                        }

                        if (colMap.url != null && cols[colMap.url]) {
                            pkg.descriptionURL = cols[colMap.url].trim()
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

                        /* if (colMap.editing_status != null) {
                            String value = cols[colMap.editing_status].trim()
                            if (value) {
                              RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_EDITING_STATUS, value)
                              if (refdataValue)
                                pkg.editingStatus = refdataValue
                            }
                          }*/


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
                                idenitiferMap.ns = "Anbieter_Produkt_ID"
                                idenitiferMap.value = value

                                identifiers << idenitiferMap

                            }
                        }

                        if (colMap.provider_product_id != null) {
                            String value = cols[colMap.provider_product_id].trim()
                            if (value) {

                                Map idenitiferMap = [:]
                                idenitiferMap.pkgID = pkg.id
                                idenitiferMap.ns = "Anbieter_Produkt_ID"
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


                        if (pkg.save() || pkg.isAttached()) {
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
                                        if (packageArchivingAgency.save()) {
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
                                            packageArchivingAgency.save()
                                        }

                                    }
                                }
                            }

                            if(user.curatoryGroupUsers) {
                                user.curatoryGroupUsers.curatoryGroup.each { CuratoryGroup cg ->
                                    if (!(cg in pkg.curatoryGroups)) {
                                        def combo_type = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.CuratoryGroups')

                                        def new_combo = new Combo(fromComponent: pkg, toComponent: cg, type: combo_type).save()

                                    }
                                }
                            }

                            if (colMap.source_url != null) {
                                String source_url = cols[colMap.source_url].trim()
                                if (source_url) {
                                    Map sourceMap = [:]
                                    sourceMap.url = source_url
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
                                                    UpdateToken newToken = new UpdateToken(pkg: pkg, updateUser: user, value: tokenValue).save()
                                                }
                                            }*/

                                        }
                                    }

                                    if (colMap.title_id_namespace != null) {
                                        String value = cols[colMap.title_id_namespace].trim()
                                        if (value) {
                                            IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValue(value)
                                            if (identifierNamespace)
                                                sourceMap.targetNamespace = identifierNamespace.id
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

        IdentifierNamespace namespace = IdentifierNamespace.findByValueAndTargetType("Anbieter_Produkt_ID", RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_PACKAGE)
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
            Source source
            Package aPackage = Package.get(map.pkgID)

            if(aPackage) {
                if (aPackage.source == null) {
                    def dupes = Source.findAllByNameIlikeAndStatusNotEqual(aPackage.name, status_deleted)
                    String sourceName = aPackage.name
                    if (dupes && dupes.size() > 0) {
                        sourceName = "${sourceName} ${dupes.size() + 1}"
                    }

                    source = new Source(name: sourceName)
                } else {
                    source = aPackage.source
                    def dupes = Source.findAllByNameIlikeAndStatusNotEqual(aPackage.name, status_deleted)
                    String sourceName = aPackage.name
                    if (dupes && dupes.size() > 0) {
                        sourceName = "${sourceName} ${dupes.size() + 1}"
                    }

                    if(sourceName != source.name){
                        source.name = sourceName
                    }
                }

                if (map.url) {
                    source.url = map.url
                }

                if (map.frequency) {
                    source.frequency = RefdataValue.get(map.frequency)
                }

                if (map.automaticUpdates) {
                    source.automaticUpdates = map.automaticUpdates
                }

                if (map.targetNamespace) {
                    source.targetNamespace = IdentifierNamespace.get(map.targetNamespace)
                }

                if (source.save(flush: true) || source.isAttached()) {

                    if(user.curatoryGroupUsers) {
                        user.curatoryGroupUsers.curatoryGroup.each { CuratoryGroup cg ->
                            if (!(cg in source.curatoryGroups)) {
                                def combo_type = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Source.CuratoryGroups')

                                def new_combo = new Combo(fromComponent: source, toComponent: cg, type: combo_type).save()
                            }
                        }
                    }
                    if (source != aPackage.source) {
                        aPackage = aPackage.refresh()
                        aPackage.source = source
                        aPackage.save(flush: true)
                    }
                }
            }
        }

        List<Package> packages = []
        packageList.each {
            packages << Package.get(it.id)
        }


        [packages: packages, rowsCount: rows.size(), errors: globalErrors]
    }
}

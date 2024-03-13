package wekb

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.poi.ooxml.POIXMLProperties
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFDataFormat
import org.apache.poi.xssf.usermodel.XSSFFont
import wekb.helper.BeanStore
import wekb.tools.UrlToolkit
import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import grails.util.Holders
import org.apache.commons.io.FileUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellRangeAddressList
import org.apache.poi.xssf.usermodel.XSSFDataValidation
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.hibernate.Session

import javax.sql.DataSource
import java.nio.file.Files
import java.text.SimpleDateFormat


@Transactional
class ExportService {

    DateFormatService dateFormatService

    public void exportOriginalKBART(def outputStream, Package pkg) {

        if(pkg.kbartSource && (pkg.kbartSource.lastUpdateUrl || pkg.kbartSource.url)){
            if((UrlToolkit.containsDateStamp(pkg.kbartSource.url) || UrlToolkit.containsDateStampPlaceholder(pkg.kbartSource.url)) && pkg.kbartSource.lastUpdateUrl){
                File file = kbartFromUrl(pkg.kbartSource.lastUpdateUrl)
                if(file)
                outputStream << file.bytes
            }else{
                File file = kbartFromUrl(pkg.kbartSource.url)
                if(file)
                outputStream << file.bytes
            }
            outputStream.close()
        }else if(pkg.getLastSuccessfulManualUpdateInfo()){
            def output

            try {
                String fPath = "${Holders.grailsApplication.config.getProperty('wekb.kbartImportStorageLocation', String)}" ?: '/tmp/wekb/kbartImport'

                String packageName = "${pkg.id}"
                File file = new File("${fPath}/${packageName}")
                output = file.getBytes()

            } catch(Exception e) {
                log.error(e)
            }

            outputStream << output
            outputStream.close()
        }
        else {

            outputStream.withWriter { writer ->

                writer.write('publication_title\t' +
                        'print_identifier\t' +
                        'online_identifier\t' +
                        'date_first_issue_online\t' +
                        'num_first_vol_online\t' +
                        'num_first_issue_online\t' +
                        'date_last_issue_online\t' +
                        'num_last_vol_online\t' +
                        'num_last_issue_online\t' +
                        'title_url\t' +
                        'first_author\t' +
                        'title_id\t' +
                        'embargo_info\t' +
                        'coverage_depth\t' +
                        'notes\t' +
                        'publisher_name\t' +
                        'publication_type\t' +
                        'date_monograph_published_print\t' +
                        'date_monograph_published_online\t' +
                        'monograph_volume\t' +
                        'monograph_edition\t' +
                        'first_editor\t' +
                        'parent_publication_title_id\t' +
                        'preceding_publication_title_id\t' +
                        'access_type\t' +
                        '\n'
                )
                writer.flush();
                writer.close();
            }
            outputStream.close()
        }
    }

    private File kbartFromUrl(String urlString, UpdatePackageInfo updatePackageInfo = null) throws Exception{
        URL url = new URL(urlString)
        File folder = new File("/tmp/wekb/kbartExport")
        HttpURLConnection connection
        try {
            connection = (HttpURLConnection) url.openConnection()
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:123.0) Gecko/20100101 Firefox/123.0")
        }
        catch (IOException e) {
            throw new RuntimeException("URL Connection was not established.")
        }
        connection.connect()
        connection = UrlToolkit.resolveRedirects(connection, 5)
        log.debug("Final URL after redirects: ${connection.getURL()}")

        String fileName = folder.absolutePath.concat(File.separator).concat(urlStringToFileString(url.toExternalForm()))
        fileName = fileName.split("\\?")[0]
        File file = new File(fileName)

        log.debug("connection.getResponseCode(): "+connection.getResponseCode())
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            byte[] content = getByteContent(connection.getInputStream())
            //InputStream inputStream = new ByteArrayInputStream(content)
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(content), file)
            // copy content to local file
            //Files.write(file.toPath(), content)
        }else {
            UpdatePackageInfo.withTransaction {
                updatePackageInfo.description = "Server returned HTTP response code: " + connection.getResponseCode()
                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                updatePackageInfo.endTime = new Date()
                updatePackageInfo.updateUrl = connection.getURL()
                updatePackageInfo.save()
            }
        }
        return file
    }

    byte[] getByteContent(InputStream inputStream){
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        byte[] buf = new byte[4096]
        int n = 0
        while ((n = inputStream.read(buf)) >= 0){
            baos.write(buf, 0, n)
        }
        baos.toByteArray()
    }


    static String urlStringToFileString(String url){
        url.replace("://", "_").replace(".", "_").replace("/", "_")
    }

    def exportPackageBatchImportTemplate(def outputStream) {

        List titles = ["package_uuid", "package_name", "provider_uuid", "nominal_platform_uuid", "description", "description_url", "breakable", "content_type",
                              "file", "open_access", "payment_type", "scope", "national_range", "regional_range", "provider_product_id", "ddc", "source_default_supply_method", "source_url", "source_ftp_server_url", "source_ftp_directory", "source_ftp_file_name", "source_ftp_username", "source_ftp_password", "frequency", "automated_updates", "archiving_agency", "open_access_of_archiving_agency", "post_cancellation_access_of_archiving_agency"]


        XSSFWorkbook workbook = new XSSFWorkbook()
        XSSFSheet sheet = workbook.createSheet("Packages")

        Row headerRow = sheet.createRow(0)
        headerRow.setHeightInPoints(16.75f)
        titles.eachWithIndex{ titleName, int i ->
            Cell cell = headerRow.createCell(i)
            cell.setCellValue(titleName)
        }
        sheet.createFreezePane(0,1)

        titles.eachWithIndex{ titleName, int i ->
            String[] datas
            switch(titleName) {
                case 'breakable': datas = RefdataCategory.lookup(RCConstants.PACKAGE_BREAKABLE).sort{it.value}.collect { it -> it.value }
                    break
                case 'consistent': datas = RefdataCategory.lookup(RCConstants.PACKAGE_CONSISTENT).sort{it.value}.collect { it -> it.value }
                    break
                case 'content_type': datas = RefdataCategory.lookup(RCConstants.PACKAGE_CONTENT_TYPE).sort{it.value}.collect { it -> it.value }
                    break
                case 'file': datas = RefdataCategory.lookup(RCConstants.PACKAGE_FILE).sort{it.value}.collect { it -> it.value }
                    break
                case 'open_access': datas = RefdataCategory.lookup(RCConstants.PACKAGE_OPEN_ACCESS).sort{it.value}.collect { it -> it.value }
                    break
                case 'payment_type': datas = RefdataCategory.lookup(RCConstants.PACKAGE_PAYMENT_TYPE).sort{it.value}.collect { it -> it.value }
                    break
                case 'scope': datas = RefdataCategory.lookup(RCConstants.PACKAGE_SCOPE).sort{it.value}.collect { it -> it.value }
                    break
                case 'editing_status': datas = RefdataCategory.lookup(RCConstants.PACKAGE_EDITING_STATUS).sort{it.value}.collect { it -> it.value }
                    break
                case 'national_range': //Because many to many
                    break
                case 'regional_range': //Because many to many
                    break
                case 'ddc': //Because many to many
                    break
                case 'frequency': datas = RefdataCategory.lookup(RCConstants.SOURCE_FREQUENCY).sort{it.value}.collect { it -> it.value }
                    break
                case 'title_id_namespace': //Because more than 255 values // datas = IdentifierNamespace.findAllByFamily('ttl_prv').sort{it.value}.collect{ it -> it.value}
                    break
                case 'automated_updates': datas = RefdataCategory.lookup(RCConstants.YN).sort{it.value}.collect { it -> it.value }
                    break
                case 'archiving_agency': //Because more than 255 values // datas = RefdataCategory.lookup(RCConstants.PAA_ARCHIVING_AGENCY).sort{it.value}.collect { it -> it.value }
                    break
                case 'open_access_of_archiving_agency': datas = RefdataCategory.lookup(RCConstants.PAA_OPEN_ACCESS).sort{it.value}.collect { it -> it.value }
                    break
                case 'post_cancellation_access_of_archiving_agency': datas = RefdataCategory.lookup(RCConstants.PAA_POST_CANCELLATION_ACCESS).sort{it.value}.collect { it -> it.value }
                    break
                case 'source_default_supply_method': datas = [RDStore.KS_DSMETHOD_HTTP_URL.value, RDStore.KS_DSMETHOD_FTP.value]
                    break
            }

            if(datas){
                setInExcelDropDownList(sheet, datas, i)
            }

        }

        try {

            workbook.write(outputStream)
            outputStream.flush()
            outputStream.close()

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private setInExcelDropDownList(XSSFSheet sheet, String[] datas, Integer column){

        //println(datas)
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet)
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(datas)
        CellRangeAddressList addressList = null
        XSSFDataValidation validation = null

        addressList = new CellRangeAddressList(1, 500, column, column)
        validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList)

        // These two lines set the cell can only be a table of contents, otherwise an error
        validation.setSuppressDropDownArrow(true)
        validation.setShowErrorBox(true)

        sheet.addValidationData(validation)

    }

    String generateSeparatorTableString(def titleRow, List columnData, String separator) {
        List output = []
        output.add(titleRow.join(separator))
        columnData.each { row ->
            if(row instanceof GroovyRowResult) {
                output.add(row.values().join(separator).replaceAll('null', ''))
            }
            else {
                if(row.size() > 0)
                    output.add(row.join(separator))
                else output.add(" ")
            }
        }
        output.join("\n")
    }


    Map<String,List> exportPackageTippsAsTSVNew(Package pkg, List status) {

        def export_date = dateFormatService.formatDate(new Date())
        List<String> titleHeaders = getTitleHeadersTSV()
        Map<String,List> export = [titleRow:titleHeaders,rows:[]]

        SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')

        def sanitize = { it ? (it instanceof Date ? sdf.format(it) : "${it}".trim()) : "" }

        List<String> printIdentifier = ["issn", "isbn"]
        List<String> onlineIdentifier = ["eissn", "eisbn"]

        String doiIdentifier = IdentifierNamespace.DOI
        String zdbIdentifier = IdentifierNamespace.ZDB
        String ezbIdentifier = IdentifierNamespace.EZB
        String packageEzbAnchor = IdentifierNamespace.PACKAGE_EZB_ANCHOR
        String packageIsci = IdentifierNamespace.PACKAGE_ISCI

        RefdataValue priceTypeList = RDStore.PRICE_TYPE_LIST
        RefdataValue priceTypeOAAPC = RDStore.PRICE_TYPE_OA_APC

        String hqlQuery = "select tipp.id," +
                " tipp.name, " +
                " tipp.firstAuthor, " +
                " tipp.firstEditor, " +
                " tipp.publisherName, " +
                " (select value from RefdataValue where id = tipp.publicationType), " +
                " (select value from RefdataValue where id = tipp.medium), " +
                " tipp.url, " +
                " 'printIdentifier', " +
                " 'onlineIdentifier', " +
                " 'titleIdNameSpace'," +
                " 'doiIdentifier',"+
                " tipp.subjectArea, " +
                " 'languages', " +
                " 'ddcs', " +
                " (select value from RefdataValue where id = tipp.accessType), " +
                " (select value from RefdataValue where id = cs.coverageDepth), " +
                " 'pkg.name', " +
                " '', " + // package_id
                " tipp.accessStartDate, " +
                " tipp.accessEndDate, " +
                " tipp.lastChangedExternal, " +
                " (select value from RefdataValue where id = tipp.status), " +
                " 'listprice_eur', " +
                " 'listprice_usd', " +
                " 'listprice_gbp', " +
                " tipp.note, " +
                " tipp.dateFirstInPrint, " +
                " tipp.dateFirstOnline, " +
                " tipp.volumeNumber, " +
                " tipp.editionStatement, " +
                " tipp.series, " +
                " tipp.parentPublicationTitleId, " +
                " cs.startDate, " +
                " cs.startVolume, " +
                " cs.startIssue, " +
                " cs.endDate, " +
                " cs.endVolume, " +
                " cs.endIssue, " +
                " 'zdb_id', "+
                " 'ezb_id', "+
                " 'package_ezb_anchor', " +
                " '', " + // oa_gold
                " '', " + // oa_hybrid
                " 'oa_apc_eur', " +
                " 'oa_apc_usd', " +
                " 'oa_apc_gbp', " +
                " '', " + // package_isil
                " tipp.uuid, " +
                " 'pkg.uuid', " +
                " 'package_isci', " +
                " '', " + // ill_indicator
                " tipp.precedingPublicationTitleId, " +
                " tipp.supersedingPublicationTitleId, " +
                " cs.embargo " +
                "from TitleInstancePackagePlatform as tipp left join tipp.coverageStatements as cs where tipp.id in (:tippIDs) order by tipp.name"

        Map queryParams = [:]
        queryParams.p = pkg
        queryParams.removed = RDStore.KBC_STATUS_REMOVED
        String query = "select tipp.id from TitleInstancePackagePlatform as tipp where tipp.pkg = :p and tipp.status != :removed order by tipp.name"
        if(status) {
            query = "select tipp.id from TitleInstancePackagePlatform as tipp where tipp.pkg = :p and tipp.status in (:status) and tipp.status != :removed order by tipp.name"
            queryParams.status = status
        }

        List<Long> tippIDs = TitleInstancePackagePlatform.executeQuery(query, queryParams, [readOnly: true])

        int max = 10000
        TitleInstancePackagePlatform.withSession { Session sess ->
            for (int offset = 0; offset < tippIDs.size(); offset += max) {
                log.debug("Count:"+offset)
                List tippAttributes = TitleInstancePackagePlatform.executeQuery(hqlQuery, [tippIDs: tippIDs.drop(offset).take(max)], [readOnly: true])
                tippAttributes.each { def attribute ->
                    List row = []
                    Long tippID
                    attribute.eachWithIndex { def attributeValue, int index ->
                        if(index == 0){
                            tippID = attribute[index]
                        }
                        else {
                            switch (attribute[index]) {
                                case 'listprice_eur':
                                    List existPrice = TippPrice.executeQuery('select price from TippPrice where price is not null and tipp.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeList, currency: RDStore.CURRENCY_EUR], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(existPrice[0])
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'listprice_usd':
                                    List existPrice = TippPrice.executeQuery('select price from TippPrice where price is not null and tipp.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeList, currency: RDStore.CURRENCY_USD], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(existPrice[0])
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'listprice_gbp':
                                    List existPrice = TippPrice.executeQuery('select price from TippPrice where price is not null and tipp.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeList, currency: RDStore.CURRENCY_GBP], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(existPrice[0])
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'oa_apc_eur':
                                    List existPrice = TippPrice.executeQuery('select price from TippPrice where price is not null and tipp.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeOAAPC, currency: RDStore.CURRENCY_EUR], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(existPrice[0])
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'oa_apc_usd':
                                    List existPrice = TippPrice.executeQuery('select price from TippPrice where price is not null and tipp.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeOAAPC, currency: RDStore.CURRENCY_USD], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(existPrice[0])
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'oa_apc_gbp':
                                    List existPrice = TippPrice.executeQuery('select price from TippPrice where price is not null and tipp.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeOAAPC, currency: RDStore.CURRENCY_GBP], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(existPrice[0])
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'printIdentifier':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in (:namespaceValue) and i.tipp.id = :tippID', [namespaceValue: printIdentifier, tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(identifiers.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'onlineIdentifier':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in (:namespaceValue) and i.tipp.id = :tippID', [namespaceValue: onlineIdentifier, tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(identifiers.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'titleIdNameSpace':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.tipp.id = :tippID', [namespaceValue: 'title_id', tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(identifiers.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'doiIdentifier':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.tipp.id = :tippID', [namespaceValue: doiIdentifier.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(identifiers.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'zdb_id':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.tipp.id = :tippID', [namespaceValue: zdbIdentifier.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(identifiers.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'ezb_id':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.tipp.id = :tippID', [namespaceValue: ezbIdentifier.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(identifiers.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'package_ezb_anchor':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.tipp.id = :tippID', [namespaceValue: packageEzbAnchor.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(identifiers.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'package_isci':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.tipp.id = :tippID', [namespaceValue: packageIsci.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(identifiers.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'languages':
                                    List<ComponentLanguage> languages = ComponentLanguage.executeQuery('select language.value from ComponentLanguage where tipp.id = :tippID', [tippID: tippID], [readOnly: true])

                                    if (languages.size() > 0) {
                                        row.add(languages.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'ddcs':
                                    TitleInstancePackagePlatform titleInstancePackagePlatform = TitleInstancePackagePlatform.get(tippID)

                                    if (titleInstancePackagePlatform.ddcs.size() > 0) {
                                        row.add(titleInstancePackagePlatform.ddcs.join(';'))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'pkg.name':
                                        row.add(pkg.name)
                                    break;
                                case 'pkg.uuid':
                                    row.add(pkg.uuid)
                                    break;
                                default:
                                    row.add(sanitize(attribute[index]))
                            }
                        }
                        if (attribute.size() - 1 == index) {
                            export.rows.add(row)
                        }

                    }
                }
                log.debug("flushing after ${offset} ...")
                sess.flush()
            }
        }

        return export

    }

    Map<String,List> exportPackageTippsAsTSVWithSQL(Package pkg, List status) {
        log.debug("Begin exportPackageTippsAsTSVWithSQL")
        DataSource dataSource = BeanStore.getDataSource()
        Sql sql = new Sql(dataSource)
        Map<String, String> titleHeaders = getTitleHeadersWithSQL()
        Map<String, List> export = [titleRow: titleHeaders.keySet()]
        List rows = []

        try {
            Map queryParams = [:]
            queryParams.p = pkg.id
            queryParams.removed = RDStore.KBC_STATUS_REMOVED.id

            String queryBase = "select ${titleHeaders.values().join(', ')} from title_instance_package_platform left join tippcoverage_statement on tcs_tipp_fk = tipp_id join package on tipp_pkg_fk = pkg_id"
            if(status) {

                queryBase += " where tipp_pkg_fk = :p and tipp_status_rv_fk != :removed and tipp_status_rv_fk = any(:status)"
                List<Long> statusKeys = []
                statusKeys.addAll(status.id)
                queryParams.status = sql.getDataSource().getConnection().createArrayOf('bigint', statusKeys as Object[])
            }else{
                queryBase += " where tipp_pkg_fk = :p and tipp_status_rv_fk != :removed "
            }
            queryBase += " order by tipp_name"

            rows.addAll(sql.rows(queryBase, queryParams))

        }catch (Exception exception) {
            sql.close()
            log.error("exportPackageTippsAsTSVWithSQL: "+ exception.stackTrace)

        }
        finally {
            sql.close()
        }
        export.columnData = rows
        log.debug("End exportPackageTippsAsTSVWithSQL")
        export
    }

    def exportPackages(def outputStream, def packages) {

        def export_date = dateFormatService.formatDate(new Date())
        List<String> titleHeaders = ["package_uuid", "package_name", "provider_name", "provider_uuid", "nominal_platform_name",
                                     "nominal_platform_uuid", "description", "url", "breakable", "content_type",
                                     "file", "open_access", "payment_type", "scope", "national_range", "regional_range", "provider_product_id", "ddc",
                                     "source_default_supply_method", "source_url", "source_ftp_server_url", "source_ftp_directory", "source_ftp_file_name", "source_ftp_username", "source_ftp_password",
                                     "frequency", "automated_updates",
                                     "archiving_agency", "open_access_of_archiving_agency", "post_cancellation_access_of_archiving_agency"]
        Map<String,List> export = [titleRow:titleHeaders,rows:[]]

        SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')

        def sanitize = { it ? (it instanceof Date ? sdf.format(it) : "${it}".trim()) : "" }

        packages.each { Package pkg ->

            List row = []
            row.add(sanitize(pkg.uuid))
            row.add(sanitize(pkg.name))
            row.add(sanitize(pkg.provider?.name))
            row.add(sanitize(pkg.provider?.uuid))
            row.add(sanitize(pkg.nominalPlatform?.name))
            row.add(sanitize(pkg.nominalPlatform?.uuid))
            row.add(sanitize(pkg.description))
            row.add(sanitize(pkg.descriptionURL))
            row.add(sanitize(pkg.breakable?.value))
            row.add(sanitize(pkg.contentType?.value))
            row.add(sanitize(pkg.file?.value))
            row.add(sanitize(pkg.openAccess?.value))
            row.add(sanitize(pkg.paymentType?.value))
            row.add(sanitize(pkg.scope?.value))
            row.add(sanitize(pkg.nationalRanges?.value.join(',')))
            row.add(sanitize(pkg.regionalRanges?.value.join(',')))
            row.add(sanitize(pkg.getAnbieterProduktIDs()))
            row.add(sanitize(pkg.ddcs?.value.join(',')))
            row.add(sanitize(pkg.kbartSource?.defaultSupplyMethod?.value))
            row.add(sanitize(pkg.kbartSource?.url))
            row.add(sanitize(pkg.kbartSource?.ftpServerUrl))
            row.add(sanitize(pkg.kbartSource?.ftpDirectory))
            row.add(sanitize(pkg.kbartSource?.ftpFileName))
            row.add(sanitize(pkg.kbartSource?.ftpUsername))
            row.add(sanitize(pkg.kbartSource?.ftpPassword))
            row.add(sanitize(pkg.kbartSource?.frequency?.value))
            row.add(sanitize(pkg.kbartSource?.automaticUpdates ? 'Yes': 'No'))
            row.add(sanitize(pkg.paas?.archivingAgency?.value))
            row.add(sanitize(pkg.paas?.openAccess?.value))
            row.add(sanitize(pkg.paas?.postCancellationAccess?.value))
            export.rows.add(row)
        }

        outputStream.withWriter { writer ->
           // writer.write("we:kb Export : Packages (${packages.size()}) : ${export_date}\n");
            writer.write(generateSeparatorTableString(export.titleRow, export.rows, '\t'))
        }
        outputStream.flush()
        outputStream.close()
    }

    @Deprecated
    List<String> getTitleHeadersTSV() {
        ['publication_title',
         'first_author',
         'first_editor',
         'publisher_name',
         'publication_type',
         'medium',
         'title_url',
         'print_identifier',
         'online_identifier',
         'title_id',
         'doi_identifier',
         'subject_area',
         'language',
         'ddc',
         'access_type',
         'coverage_depth',
         'package_name',
         'package_id',
         'access_start_date',
         'access_end_date',
         'last_changed',
         'status',
         'listprice_eur',
         'listprice_usd',
         'listprice_gbp',
         'notes',
         'date_monograph_published_print',
         'date_monograph_published_online',
         'monograph_volume',
         'monograph_edition',
         'monograph_parent_collection_title',
         'parent_publication_title_id',
         'date_first_issue_online',
         'num_first_vol_online',
         'num_first_issue_online',
         'date_last_issue_online',
         'num_last_vol_online',
         'num_last_issue_online',
         'zdb_id',
         'ezb_id',
         'package_ezb_anchor',
         'oa_gold',
         'oa_hybrid',
         'oa_apc_eur',
         'oa_apc_usd',
         'oa_apc_gbp',
         'package_isil',
         'title_wekb_uuid',
         'package_wekb_uuid',
         'package_isci',
         'ill_indicator',
         'preceding_publication_title_id',
         'superseding_publication_title_id',
         'embargo_info']
    }

    XSSFWorkbook generateXLSXWorkbook(String sheetTile, Set titleRow, List columnData) {
        XSSFWorkbook wb = new XSSFWorkbook()
        XSSFSheet sheet = wb.createSheet(sheetTile)
        POIXMLProperties xmlProps = wb.getProperties()
        POIXMLProperties.CoreProperties coreProps = xmlProps.getCoreProperties()
        coreProps.setCreator('wekb')

        XSSFCellStyle bold = wb.createCellStyle()
        XSSFFont font = wb.createFont()
        font.setBold(true)
        bold.setFont(font)
        XSSFCellStyle lb = wb.createCellStyle()
        lb.setWrapText(true)

        Row headerRow = sheet.createRow(0)
        headerRow.setHeightInPoints(16.75f)
        titleRow.eachWithIndex{ titleName, int i ->
            Cell cell = headerRow.createCell(i)
            cell.setCellValue(titleName)
        }
        sheet.createFreezePane(0,1)

        try {
            int rownum = 1
            Row row
            Cell cell
            CellStyle numberStyle = wb.createCellStyle();
            XSSFDataFormat df = wb.createDataFormat();
            numberStyle.setDataFormat(df.getFormat("#,##0.00"));
            columnData.each { rowData ->
                int cellnum = 0
                row = sheet.createRow(rownum)
                rowData.each { cellData ->
                    cell = row.createCell(cellnum++)
                    if (cellData.value instanceof String) {
                        cell.setCellValue((String) cellData.value)
                        if (cellData.value.contains('\n'))
                            cell.setCellStyle(lb)
                    } else if (cellData.value instanceof Integer) {
                        cell.setCellValue((Integer) cellData.value)
                    } else if (cellData.value instanceof Double || cellData.value instanceof BigDecimal || cellData.value instanceof Float) {
                        cell.setCellValue((Double) cellData.value)
                        cell.setCellStyle(numberStyle)
                    }
                }
                rownum++
            }
            for (int i = 0; i < titleRow.size(); i++) {
                try {
                    sheet.autoSizeColumn(i)
                }
                catch (Exception e) {
                    log.error("Null pointer exception in column ${i}")
                }
            }
        }
        catch (ClassCastException e) {
            log.error("Data delivered in inappropriate structure!")
        }
        wb
    }

    /**
     * Gets the map of column headers for KBART export with their database query mappings
     * @return a map of column headers and SQL query parts
     */
    Map<String, String> getTitleHeadersWithSQL() {
        Long priceTypeList = RDStore.PRICE_TYPE_LIST.id
        Long priceTypeOAAPC = RDStore.PRICE_TYPE_OA_APC.id

        Map <String, String> mapping = [publication_title: 'tipp_name as publication_title',
                                        first_author: 'tipp_first_author as first_author',
                                        first_editor: 'tipp_first_editor as first_editor',
                                        publisher_name: 'tipp_publisher_name as publisher_name',
                                        publication_type: '(select rdv_value from refdata_value where rdv_id = tipp_publication_type_rv_fk) as publication_type',
                                        medium: '(select rdv_value from refdata_value where rdv_id = tipp_medium_rv_fk) as medium',
                                        title_url: 'tipp_url as title_url',
                                        print_identifier: "(select string_agg(id_value,',') from identifier where id_tipp_fk = tipp_id and (id_namespace_fk = ${IdentifierNamespace.findByValue(IdentifierNamespace.ISBN).id} or id_namespace_fk = ${IdentifierNamespace.findByValue(IdentifierNamespace.ISSN).id})) as print_identifier",
                                        online_identifier: "(select string_agg(id_value,',') from identifier where id_tipp_fk = tipp_id and (id_namespace_fk = ${IdentifierNamespace.findByValue(IdentifierNamespace.EISBN).id} or id_namespace_fk = ${IdentifierNamespace.findByValue(IdentifierNamespace.EISSN).id})) as online_identifier",
                                        title_id: "(select string_agg(id_value,',') from identifier where id_tipp_fk = tipp_id and id_namespace_fk = ${IdentifierNamespace.findByValue('title_id').id}) as title_id",
                                        doi_identifier: "(select string_agg(id_value,',') from identifier where id_tipp_fk = tipp_id and id_namespace_fk = '${IdentifierNamespace.findByValue(IdentifierNamespace.DOI).id}') as doi_identifier",
                                        subject_area: 'tipp_subject_area as subject_area',
                                        language: "(select string_agg((select rdv_value from refdata_value where rdv_id = cl_rv_fk),',') from component_language where cl_tipp_fk = tipp_id) as language",
                                        ddc: "(select string_agg((select rdv_value from refdata_value where rdv_id = ddc_rv_Fk),',') from tipp_dewey_decimal_classification where tipp_fk = tipp_id) as ddc",
                                        access_type: '(select rdv_value from refdata_value where rdv_id = tipp_access_type_rv_fk) as access_type',
                                        coverage_depth: '(select rdv_value from refdata_value where rdv_id = tcs_coverage_depth) as coverage_depth',
                                        package_name: 'pkg_name as package_name',
                                        package_id: "(select string_agg(id_value,',') from identifier where id_pkg_fk = pkg_id and id_namespace_fk = '${IdentifierNamespace.findByValue(IdentifierNamespace.PKG_ID).id}') as package_id",
                                        access_start_date: "to_char(tipp_access_start_date, 'yyyy-MM-dd') as access_start_date",
                                        access_end_date: "to_char(tipp_access_end_date, 'yyyy-MM-dd') as access_end_date",
                                        last_changed: "to_char(tipp_last_change_ext, 'yyyy-MM-dd') as last_changed",
                                        status: '(select rdv_value from refdata_value where rdv_id = tipp_status_rv_fk) as status',
                                        listprice_eur: "(select trim(to_char(tp_price, '999999999D99')) from tipp_price where tp_tipp_fk = tipp_id and tp_currency_fk = ${RDStore.CURRENCY_EUR.id} and tp_type_fk = ${priceTypeList} order by tp_last_updated desc limit 1) as listprice_eur",
                                        listprice_gbp: "(select trim(to_char(tp_price, '999999999D99')) from tipp_price where tp_tipp_fk = tipp_id and tp_currency_fk = ${RDStore.CURRENCY_GBP.id} and tp_type_fk = ${priceTypeList} order by tp_last_updated desc limit 1) as listprice_gbp",
                                        listprice_usd: "(select trim(to_char(tp_price, '999999999D99')) from tipp_price where tp_tipp_fk = tipp_id and tp_currency_fk = ${RDStore.CURRENCY_USD.id} and tp_type_fk = ${priceTypeList} order by tp_last_updated desc limit 1) as listprice_usd",
                                        notes: 'tcs_note as notes',
                                        date_monograph_published_print: "to_char(tipp_date_first_in_print, 'yyyy-MM-dd') as date_monograph_published_print",
                                        date_monograph_published_online: "to_char(tipp_date_first_online, 'yyyy-MM-dd') as date_monograph_published_online",
                                        monograph_volume: 'tipp_volume_number as monograph_volume',
                                        monograph_edition: 'tipp_edition_statement as monograph_edition',
                                        monograph_parent_collection_title: "tipp_series as monograph_parent_collection_title",
                                        parent_publication_title_id: "tipp_parent_publication_title_id as parent_publication_title_id",
                                        date_first_issue_online: "to_char(tcs_start_date, 'yyyy-MM-dd') as date_first_issue_online",
                                        num_first_vol_online: 'tcs_start_volume as num_first_vol_online',
                                        num_first_issue_online: 'tcs_start_issue as num_first_issue_online',
                                        date_last_issue_online: "to_char(tcs_end_date, 'yyyy-MM-dd') as date_last_issue_online",
                                        num_last_vol_online: 'tcs_end_volume as num_last_vol_online',
                                        num_last_issue_online: 'tcs_end_issue as num_last_issue_online',
                                        coverage_depth: 'tcs_depth as coverage_depth',
                                        zdb_id: "(select string_agg(id_value,',') from identifier where id_tipp_fk = tipp_id and id_namespace_fk = ${IdentifierNamespace.findByValue(IdentifierNamespace.ZDB).id}) as zdb_id",
                                        ezb_id: "(select string_agg(id_value,',') from identifier where id_tipp_fk = tipp_id and id_namespace_fk = '${IdentifierNamespace.findByValueAndTargetType(IdentifierNamespace.EZB, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP).id}') as ezb_id",
                                        package_ezb_anchor: "(select string_agg(id_value,',') from identifier where id_pkg_fk = tipp_id and id_namespace_fk = '${IdentifierNamespace.findByValueAndTargetType(IdentifierNamespace.PACKAGE_EZB_ANCHOR, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP).id}') as package_ezb_anchor",
                                        oa_type: '(select rdv_value from refdata_value where rdv_id = tipp_open_access_rv_fk) as oa_type',
                                        oa_apc_eur: "(select trim(to_char(tp_price, '999999999D99')) from tipp_price where tp_tipp_fk = tipp_id and tp_currency_fk = ${RDStore.CURRENCY_EUR.id} and tp_type_fk = ${priceTypeOAAPC} order by tp_last_updated desc limit 1) as oa_apc_eur",
                                        oa_apc_usd: "(select trim(to_char(tp_price, '999999999D99')) from tipp_price where tp_tipp_fk = tipp_id and tp_currency_fk = ${RDStore.CURRENCY_GBP.id} and tp_type_fk = ${priceTypeOAAPC} order by tp_last_updated desc limit 1) as oa_apc_usd",
                                        oa_apc_gbp: "(select trim(to_char(tp_price, '999999999D99')) from tipp_price where tp_tipp_fk = tipp_id and tp_currency_fk = ${RDStore.CURRENCY_USD.id} and tp_type_fk = ${priceTypeOAAPC} order by tp_last_updated desc limit 1) as oa_apc_gbp",
                                        package_isil: "(select string_agg(id_value,',') from identifier where id_pkg_fk = tipp_id and id_namespace_fk = '${IdentifierNamespace.findByValueAndTargetType(IdentifierNamespace.PACKAGE_ISIL, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP).id}') as package_isil",
                                        title_wekb_uuid: 'tipp_uuid as title_wekb_uuid',
                                        package_wekb_uuid: 'pkg_uuid as package_wekb_uuid',
                                        //ill_indicator: "null as ill_indicator",
                                        preceding_publication_title_id: "tipp_preceding_publication_title_id as preceding_publication_title_id",
                                        superceding_publication_title_id: "tipp_superseding_publication_title_id as superceding_publication_title_id",
                                        embargo_info: 'tcs_embargo as embargo_info']


        mapping
    }
}

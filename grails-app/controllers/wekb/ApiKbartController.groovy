package wekb

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.web.servlet.mvc.GrailsParameterMap
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import wekb.annotations.AltchaAnnotation
import wekb.auth.User
import wekb.helper.RDStore
import wekb.utils.DateUtils

import javax.servlet.ServletOutputStream
import java.security.SecureRandom
import java.text.SimpleDateFormat

@AltchaAnnotation(comment = AltchaAnnotation.ACCESS_ALLOWED)
class ApiKbartController {
    SecureRandom rand = new SecureRandom()

    Api2Service api2Service
    AuthenticationManager authenticationManager
    DateFormatService dateFormatService
    ExportService exportService
    GenericOIDService genericOIDService
    SpringSecurityService springSecurityService

    /**
     * Check if the api is up. Just return true.
     */
    def isUp() {
        apiReturn(["isUp": true])
    }

    // Internal API return object that ensures consistent formatting of API return objects
    private def apiReturn = { result, String message = "", String status = (result instanceof Throwable) ? "error" : "success" ->

        // If the status is error then we should log an entry.
        if (status == 'error') {

            // Generate 6bytes of random data to be base64 encoded which can be returned to the user to help with tracking issues in the logs.
            byte[] randomBytes = new byte[6]
            rand.nextBytes(randomBytes)
            def ticket = Base64.encodeBase64String(randomBytes);

            // Let's see if we have a throwable.
            if (result && result instanceof Throwable) {

                // Log the error with the stack...
                log.error("[[${ticket}]] - ${message == "" ? result.getLocalizedMessage() : message}", result)
            } else {
                log.error("[[${ticket}]] - ${message == "" ? 'An error occured, but no message or exception was supplied. Check preceding log entries.' : message}")
            }

            // Ensure we have something to send back to the user.
            if (message == "") {
                message = "An unknow error occurred."
            } else {

                // We should now send the message along with the ticket.
                message = "${message}".replaceFirst("\\.\\s*\$", ". The error has been logged with the reference '${ticket}'")
            }
        }

        def data = [
                code   : (status),
                result : (result),
                message: (message),
        ]

        def json = data as JSON
        //log.debug("json:"+json.toString())
        render json
        //    render (text: "${params.callback}(${json})", contentType: "application/javascript", encoding: "UTF-8")
    }

    private Map checkPermisson(String username, String password, String checkRole = null) {

        Map result = [code: 'success', message: '']

        if (!username || !password) {
            return [
                    code: 'error',
                    message: 'Please provide username and password!'
            ]
        }

        try {
            def authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username.toString(),
                            password.toString()
                    )
            )

            if (!authentication.authenticated) {
                return [
                        code: 'error',
                        message: 'Your login is not correct!'
                ]
            }

            if (checkRole && !authentication.authorities.any {
                it.authority == checkRole
            }) {
                return [
                        code: 'error',
                        message: 'This user does not have permission to access the KBART API!'
                ]
            }

        } catch (AuthenticationException e) {
            log.warn("KBART API authentication failed for user: ${params.username}")

            return [
                    code: 'error',
                    message: 'Your login is not correct!'
            ]
        }

        return result
    }

    def index() {
    }

    def kbart() {
        String username = params.username
        String password = params.password

        params.remove('username')
        params.remove('password')

        log.info("kbart::${params}")

        Map<String, Object> result = checkPermisson(username, password, 'ROLE_KBART_API')

        if(result.code == 'success') {

            wekb.Package pkg = genericOIDService.resolveOID(params.id)

            if (!pkg) {
                pkg = wekb.Package.findByUuid(params.id)
            }

            if (!pkg) {
                response.sendError(404)
                return
            }

            String export_date = dateFormatService.formatDate(new Date());

            String filename = "kbart_${pkg.name}_${export_date}.txt"


            try {
                response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

                def out = response.outputStream

                exportService.exportOriginalKBART(out, pkg)
                return
            }
            catch (Exception e) {
                log.error("Problem with export", e);
                response.sendError(500)
                return
            }
        }else {
            response.sendError(401)
            return
        }
    }

    def packageTSVExport() {
        String username = params.username
        String password = params.password

        params.remove('username')
        params.remove('password')

        log.info("packageTSVExport::${params}")

        Map<String, Object> result = checkPermisson(username, password, 'ROLE_KBART_API')

        if(result.code == 'success') {

            wekb.Package pkg = genericOIDService.resolveOID(params.id)

            if (!pkg) {
                pkg = wekb.Package.findByUuid(params.id)
            }

           if (!pkg /*|| (pkg && pkg.getTippCount() > 200000)*/) {
               response.sendError(404)
               return
            }

            String export_date = dateFormatService.formatDate(new Date());

            String filename = "wekb_package_${pkg.name.toLowerCase()}_${export_date}"

            try {
                List status = []

                if ("Current" in params.list('status') || "Current" == params.status) {
                    status << RDStore.KBC_STATUS_CURRENT
                }
                if ("Retired" in params.list('status') || "Retired" == params.status) {
                    status << RDStore.KBC_STATUS_RETIRED
                }
                if ("Expected" in params.list('status') || "Expected" == params.status) {
                    status << RDStore.KBC_STATUS_EXPECTED
                }
                if ("Deleted" in params.list('status') || "Deleted" == params.status) {
                    status << RDStore.KBC_STATUS_DELETED
                }

                if (status.size() == 0) {
                    status = [RDStore.KBC_STATUS_CURRENT, RDStore.KBC_STATUS_RETIRED, RDStore.KBC_STATUS_EXPECTED, RDStore.KBC_STATUS_DELETED]
                }

                Map<String, List> export = exportService.exportPackageTippsAsTSVWithSQL(pkg, status)

                if (params.exportFormat == 'xcel') {
                    response.setHeader("Content-disposition", "attachment; filename=${filename}.xlsx")
                    response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    Map sheetData = [:]

                    XSSFWorkbook workbook = exportService.generateXLSXWorkbook('Package_Export', export.titleRow, export.columnData)
                    workbook.write(response.outputStream)
                    response.outputStream.flush()
                    response.outputStream.close()
                    return
                } else {
                    response.setContentType('text/tab-separated-values');
                    response.setHeader("Content-disposition", "attachment; filename=${filename}.tsv")
                    String exportString = exportService.generateSeparatorTableString(export.titleRow, export.columnData, '\t')

                    ServletOutputStream out = response.outputStream
                    out.withWriter { writer ->
                        writer.write("we:kb Export : Provider (${pkg.provider?.name}) : Package (${pkg.name}) : ${export_date}\n")
                        writer.write(exportString)
                    }
                    out.flush()
                    out.close()
                }
                return

            }
            catch (Exception e) {
                log.error("Problem with export", e);
                response.sendError(500)
                return
            }
        }else{
            response.sendError(401)
            return
        }
    }

    def packageLastRun() {
        String username = params.username
        String password = params.password

        params.remove('username')
        params.remove('password')

        log.info("packageLastRun::${params}")

        Map<String, Object> result = checkPermisson(username, password, 'ROLE_KBART_API')

        if(result.code == 'success') {

            wekb.Package pkg = genericOIDService.resolveOID(params.id)

            if (!pkg) {
                pkg = wekb.Package.findByUuid(params.id)
            }

            if (!pkg /*|| (pkg && pkg.getTippCount() > 200000)*/) {
                response.sendError(404)
                return
            }

            String dateString

            if(pkg.kbartSource && pkg.kbartSource.lastRun){
                SimpleDateFormat sdf = DateUtils.getSDF_NoTime()
                dateString = sdf.format(pkg.kbartSource.lastRun)
            }

            result.lastRun = dateString
            result.message = dateString
            render result as JSON
        }else{
            response.sendError(401)
            return
        }
    }


}

package wekb

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.web.servlet.mvc.GrailsParameterMap
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import wekb.annotations.AltchaAnnotation
import wekb.auth.User
import wekb.helper.RDStore

import javax.servlet.ServletOutputStream
import java.security.SecureRandom

@AltchaAnnotation(comment = AltchaAnnotation.ACCESS_ALLOWED)
class ApiKbartController {
    SecureRandom rand = new SecureRandom()

    Api2Service api2Service
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

    private Map checkPermisson(GrailsParameterMap params, String checkRole = null){
        Map result = [code: 'success', message: '']
        User user

        log.info 'APIKBART request from ' + request.getRemoteAddr() + ' for ' + request.requestURI + ' ---> ' + request.getHeaderNames().findAll{
            it in ['host', 'referer', 'cookie', 'user-agent']
        }.collect{it + ': ' + request.getHeaders( it )}

        if(!springSecurityService.loggedIn){

            if (params.username && params.password) {
                springSecurityService.reauthenticate(params.username, params.password)

                if(!springSecurityService.loggedIn){
                    result.code = 'error'
                    result.message = 'Your login is not correct!'
                    return result
                }
            }else {
                result.code = 'error'
                result.message = 'Please set your authentication to login!'
                log.warn('checkPermisson: Please set your authentication to login!')
                return result
            }
        }

        user = springSecurityService.getCurrentUser()

        if(checkRole){
            if (!user.hasRole('ROLE_KBART_API')) {
                result.code = 'error'
                result.message = 'This user does not have permission to access the api!'
                log.warn('checkPermisson: This user does not have permission to access the api!')
                return result
            }
        }

        return result
    }

    def index() {
    }

    def kbart() {
        log.info("kbart::${params}")

        Map<String, Object> result = checkPermisson(params, 'ROLE_KBART_API')

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
        log.info("packageTSVExport::${params}")

        Map<String, Object> result = checkPermisson(params, 'ROLE_KBART_API')

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


}

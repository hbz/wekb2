package wekb

import wekb.auth.User
import wekb.helper.RDStore
import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.mozilla.universalchardet.UniversalDetector
import org.springframework.security.access.annotation.Secured
import org.springframework.web.multipart.MultipartFile

import java.util.concurrent.ExecutorService

@Secured(['IS_AUTHENTICATED_FULLY'])
class PackageController {

    SpringSecurityService springSecurityService
    GenericOIDService genericOIDService
    AccessService accessService
    GrailsApplication grailsApplication
    SearchService searchService
    KbartProcessService kbartProcessService
    ExecutorService executorService

    def index() { }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def recentActivity() {
        User user = springSecurityService.currentUser

        log.debug("PackageController::recentActivity ${params}");
        def result = ['params':params]
        def oid = params.id
        Package pkg = null
        def read_perm = false

       if (params.int('id')) {
           pkg = Package.get(params.int('id'))
            oid = (pkg ? (pkg.class.name + ":" + params.id) : null)
       }

        if ( oid ) {
            pkg = Package.findByUuid(oid)

            if (!pkg) {
                pkg = genericOIDService.resolveOID(oid)
            }

            if ( pkg ) {

                read_perm = accessService.checkReadable(pkg.class.name)

                if (read_perm) {

                    result.editable = accessService.checkEditableObject(pkg, params)

                }
                else {
                    response.setStatus(403)
                    result.code = 403
                    result.result = "ERROR"
                    result.message = "You have no permission to view this resource."
                }
            }
            else {
                log.debug("unable to resolve object")
                response.setStatus(404)
                result.status = 404
                result.result = "ERROR"
                result.message = "Unable to find the requested resource."
            }
        }

        if (pkg && read_perm) {
            result.pkg = pkg

            result.recentActivitys = pkg.getRecentActivity()
        }

        result
    }

    def packageChangeHistory() {
        log.debug("packageChangeHistory:: ${params}")
        def searchResult = [:]
        params.qp_pkg_id = params.id
        Package pkg = Package.get(params.id)

        if(params.qp_pkg_id && pkg) {
            params.qbe = 'g:updatePackageInfos'
            params.hide = ['qp_pkg_id']
            searchResult = searchService.search(searchResult.user, searchResult, params)
            searchResult.result.pkg = pkg
        }else {
            flash.error = "Unable to find the requested resource."
        }
        searchResult.result
    }

    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def kbartImport() {
        log.debug("PackageController::kbartImport ${params}");
        def result = ['params':params]
        def oid = params.id
        Package pkg = null
        def read_perm = false

        if (params.int('id')) {
            pkg = Package.get(params.int('id'))
            oid = (pkg ? (pkg.class.name + ":" + params.id) : null)
        }
        if ( oid ) {
            pkg = Package.findByUuid(oid)

            if (!pkg) {
                pkg = genericOIDService.resolveOID(oid)
            }

            if ( pkg ) {
                read_perm = accessService.checkReadable(pkg.class.name)
                if (read_perm) {
                    result.editable = accessService.checkEditableObject(pkg, params)
                }
                else {
                    response.setStatus(403)
                    flash.error = "You have no permission to view this resource."
                }
            }
            else {
                log.debug("unable to resolve object")
                response.setStatus(404)
                flash.error = "Unable to find the requested resource."
            }
        }

        if (pkg && read_perm) {
            result.pkg = pkg
        }

        result
    }

    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def processKbartImport() {
        log.debug("PackageController::processKbartImport ${params}");
        def result = ['params': params]

        Package pkg = Package.get(params.int('id'))
        Boolean onlyRowsWithLastChanged = params.onlyRowsWithLastChanged ? true : false
        if (pkg) {
            params.curationOverride = SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN") ? 'true' : null
            result.editable = accessService.checkEditableObject(pkg, params)
            if (result.editable) {
                MultipartFile tsvFile = request.getFile("tsvFile")
                if(tsvFile && tsvFile.size > 0) {
                    String encoding = UniversalDetector.detectCharset(tsvFile.getInputStream())
                    if(encoding in ["UTF-8"]) {
                        if (pkg.status in [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED]) {
                            String errorText = "Package status is ${pkg.status.value}. Update for this package is not starting."
                            flash.error = errorText
                        } else {


                            String fPath = '/tmp/wekb/kbartImportTmp'

                            File folder = new File("${fPath}")
                            if (!folder.exists()) {
                                folder.mkdirs()
                            }

                            String packageName = "${pkg.id}"
                            String fileName = folder.absolutePath.concat(File.separator).concat(packageName)
                            File file = new File(fileName)

                            tsvFile.transferTo(file)

                            Set<Thread> threadSet = Thread.getAllStackTraces().keySet()
                            Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()])
                            boolean processRunning = false
                            threadArray.each { Thread thread ->
                                if (thread.name == 'kbartImport' + pkg.id) {
                                    processRunning = true
                                }
                            }

                            if(processRunning){
                                flash.error = 'A package update is already in progress. Please wait this has finished.'
                            }else {
                                executorService.execute({
                                    Package aPackage = Package.get(pkg.id)
                                    Thread.currentThread().setName('kbartImport' + pkg.id)
                                    kbartProcessService.kbartImportManual(aPackage, file, onlyRowsWithLastChanged)
                                })

                                flash.success = "The package update for Package '${pkg.name}' was started. This runs in the background. When the update has gone through, you will see this on the Update Info of the package tab."
                            }
                        }
                    }
                    else {
                        String errorText = "The file you have uploaded has a wrong character encoding! Please ensure that your file is encoded in UTF-8. Guessed encoding has been: ${encoding}"
                        flash.error = errorText
                    }
                }
                else {
                    String errorText = "You have not uploaded a valid file!"
                    flash.error = errorText
                }
            } else {
                response.setStatus(403)
                flash.error = "You have no permission to do this."
            }
        } else {
            log.debug("unable to resolve object")
            response.setStatus(404)
            flash.error = "Unable to find the requested resource."
        }
        redirect(url: request.getHeader('referer'))
    }

}

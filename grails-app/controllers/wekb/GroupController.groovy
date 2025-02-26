package wekb

import grails.plugin.springsecurity.SpringSecurityService
import org.apache.xmlbeans.impl.store.Cur
import wekb.helper.RCConstants
import wekb.helper.RDStore
import org.springframework.security.access.annotation.Secured

@Secured(['IS_AUTHENTICATED_FULLY'])
class GroupController {

    SpringSecurityService springSecurityService
    SearchService searchService
    DateFormatService dateFormatService
    ExportService exportService
    AccessService accessService
    ManagementService managementService
    WorkflowService workflowService
    CheckMyInfosService checkMyInfosService

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        def result = [:]
        result = getResultGenerics()
        return result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def checkMyInfos() {
        def result = [:]
        result = getResultGenerics()

        if(params.curGroupID && result.user.admin){
            result.groups = [CuratoryGroup.findById(Long.valueOf(params.curGroupID))]
        }

        result.checkContacts = checkMyInfosService.checkContacts(result.groups)
        result.checkSourcesWithoutTitles = checkMyInfosService.checkSourcesWithoutTitles(result.groups)
        result.noChangesPackageLast30DaysAutoUpdate = checkMyInfosService.noChangesPackageLast30DaysAutoUpdate(result.groups)
        result.packagesWithoutTitles = checkMyInfosService.checkPackagesWithoutTitles(result.groups)
        result.checkPackageWithoutSource = checkMyInfosService.checkPackageWithoutSource(result.groups)
        result.checkPackagesWithoutProductID = checkMyInfosService.checkPackagesWithoutProductID(result.groups)
        result.checkPackagesWithoutContentType = checkMyInfosService.checkPackagesWithoutContentType(result.groups)

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myProviders() {
        def searchResult = [:]
        searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }

        params.qbe = 'g:orgs'
        params.qp_curgroups = searchResult.groups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params)

        searchResult.result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myPackages() {
        def searchResult = [:]

        searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }

        params.qbe = 'g:packages'
        params.qp_curgroups = searchResult.groups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params)

        searchResult.result

    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myPlatforms() {
        def searchResult = [:]
        searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }

        params.qbe = 'g:platforms'
        params.qp_curgroups = searchResult.groups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params)

        searchResult.result

    }

/*    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myReviewRequests() {
        def searchResult = [:]
            searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }

            params.qbe = 'g:allocatedReviewGroups'
            params.qp_curgroups = searchResult.groups.id
            params.hide = ['qp_curgroup', 'qp_curgroups']

            searchResult = searchService.search(searchResult.user, searchResult, params)

            searchResult.result
    }*/


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def mySources() {
        def searchResult = [:]

        searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }

        params.qbe = 'g:sources'
        params.qp_curgroups = searchResult.groups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params)

        searchResult.result
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myTitles() {
        def searchResult = [:]
        searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }

        params.qbe = 'g:tipps'
        params.qp_curgroups = searchResult.groups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params)

        searchResult.result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myAutoUpdateInfos() {
        def searchResult = [:]
        searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }

        params.qbe = 'g:updatePackageInfos'
        params.qp_automaticUpdate = "${RDStore.YN_YES.class.name}:${RDStore.YN_YES.id}"
        params.qp_curgroups = searchResult.groups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params)

        searchResult.result
    }

    private Map getResultGenerics() {
        Map result = [:]
        result.user = springSecurityService.currentUser

        boolean cur = result.user.curatoryGroupUsers?.size() > 0
        if (!cur) {
            log.debug("No curator!")
            response.sendError(403)
            return
        }

        //result.s_action = actionName
        //result.s_controller = controllerName

        result.groups = result.user.curatoryGroupUsers.curatoryGroup

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def exportMyPackages() {
        def searchResult = [:]
        searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }

        params.qbe = 'g:packages'
        params.qp_curgroups = searchResult.groups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']
        params.max = '10000'

        searchResult = searchService.search(searchResult.user, searchResult, params)

        searchResult.result

        String export_date = dateFormatService.formatDate(new Date());

        String filename = "wekb_my_packages_${export_date}.tsv"

        try {
            response.setContentType('text/tab-separated-values');
            response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

            def out = response.outputStream

            exportService.exportPackages(out, searchResult.result.recset)

        }
        catch (Exception e) {
            log.error("Problem with export", e);
        }
    }

    def myPackagesNeedsAutoUpdates() {
        log.debug("myPackagesNeedsAutoUpdates::${params}")
        def result =  getResultGenerics()

        List pkgs = []

        Package.executeQuery(
                "select p from Package p " +
                        " join p.curatoryGroups as curatoryGroups_curatoryGroupPackage " +
                        " join curatoryGroups_curatoryGroupPackage.curatoryGroup as curatoryGroups " +
                        " WHERE  exists (select qp_curgroups from CuratoryGroup as qp_curgroups where qp_curgroups = curatoryGroups and qp_curgroups in (:curgroups) ) " +
                        " AND p.kbartSource is not null AND " +
                        " p.kbartSource.automaticUpdates = true " +
                        " AND (p.kbartSource.lastRun is null or p.kbartSource.lastRun < current_date) order by p.name",[curgroups: result.groups]).each { Package p ->
            if (p.kbartSource.needsUpdate()) {
                pkgs << p
            }
        }

        result.pkgs = pkgs

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myPackageManagement() {
        def searchResult = [:]

        searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }


        if(params.processOption){
            managementService.processPackageManagement(params)
        }

        params.qbe = 'g:packages'
        params.qp_curgroups = searchResult.groups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        params.max = params.max ?: '1000'
        params.sort = params.sort ?: 'name'
        params.order = params.order ?: 'asc'

        searchResult = searchService.search(searchResult.user, searchResult, params)

        searchResult.result.editable = accessService.checkReadable(searchResult.result.qbetemplate.baseclass)

        searchResult.result.packageGeneralInfosBatchForm = managementService.packageGeneralInfosBatchForm

        searchResult.result.packageSourceInfosBatchForm = managementService.packageSourceInfosBatchForm

        params.activeTab = params.activeTab ?: 'generalInfos'

        searchResult.result
    }


    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def processPackageUpdate() {
        def searchResult = [:]

        searchResult = getResultGenerics()

        if(!searchResult.groups){
            flash.error = "You are not assigned to any curatory group to view this area!"
            redirect(controller: 'public', action: 'index')
            return
        }

        params.qbe = 'g:packages'
        params.qp_curgroups = searchResult.groups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']
        params.max = '5000'

        searchResult = searchService.search(searchResult.user, searchResult, params)

        searchResult.result

        List<Package> packageList = []
        searchResult.result.new_recset.each {
            if(it.obj.kbartSource && it.obj.kbartSource.automaticUpdates) {
                packageList << it.obj
            }
        }

        boolean allTitles = params.allTitles == 'true' ? true : false

        Map result = [:]
        if(packageList.size() > 0){
            result = workflowService.updateListOfPackageWithKbart(packageList, allTitles, searchResult.groups)
        }

        if(result.error){
            flash.error = result.error
        }

        if(result.message){
            flash.message = result.message
        }

        redirect(url: request.getHeader('referer'))

    }

}

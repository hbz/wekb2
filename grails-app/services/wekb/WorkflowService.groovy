package wekb

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.web.mapping.LinkGenerator
import groovyx.gpars.GParsPool
import wekb.auth.User
import wekb.auth.UserRole
import wekb.helper.RCConstants
import wekb.system.SavedSearch
import wekb.utils.ServerUtils

import java.util.concurrent.ExecutorService

@Transactional
class WorkflowService {

    AccessService accessService
    AutoUpdatePackagesService autoUpdatePackagesService
    ExecutorService executorService
    LinkGenerator grailsLinkGenerator
    SpringSecurityService springSecurityService

    List availableActions(String domainClassName) {
        switch (domainClassName) {
            case CuratoryGroup.class.name:
                if (springSecurityService.currentUser.isAdmin()) {
                    [
                            [code: 'workFlowSetStatus::Deleted', label: 'Mark the Curatory Group as deleted', message: '', onlyAdmin: true],
                            [code: 'workFlowSetStatus::Removed', label: 'Remove Curatory Group', message: '', onlyAdmin: true]
                    ]
                }else{
                    []
                }
                break
            case IdentifierNamespace.class.name:
                if (springSecurityService.currentUser.isAdmin()) {
                    [
                            [code: 'workFlowMethod::deleteIdentifierNamespace', label: 'Delete Namespace', message: '', onlyAdmin: true],
                    ]
                }else{
                    []
                }
                break
            case Identifier.class.name:
                if (springSecurityService.currentUser.isAdmin()) {
                    [
                            [code: 'workFlowMethod::deleteIdentifier', label: 'Delete Identifier', message: '', onlyAdmin: true],
                    ]
                }else{
                    []
                }
                break
            case KbartSource.class.name:
                [
                        [code: 'objectMethod::deleteSoft', label: 'Mark the Source as deleted', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowSetStatus::Removed', label: 'Remove Source', message: '', onlyAdmin: false, group: 1]
                ]
                break
            case Org.class.name:
                [
                        [code: 'workFlowSetStatus::Deleted', label: 'Mark the Provider as deleted', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowSetStatus::Retired', label: 'Mark the Provider as retired', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowSetStatus::Current', label: 'Mark the Provider as current', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowSetStatus::Removed', label: 'Remove Provider', message: '', onlyAdmin: true, group: 2]
                ]
                break
            case Package.class.name:
                [
                        [code: 'objectMethod::currentWithTipps', label: 'Mark the package as current (with all Titles)', message: '', onlyAdmin: false, group: 2],
                        [code: 'objectMethod::deleteSoft', label: 'Mark the package as deleted (with all Titles)', message: '', onlyAdmin: false, group: 2],
                        [code: 'objectMethod::retireWithTipps', label: 'Mark the package as retired (with all Titles)', message: '', onlyAdmin: false, group: 2],
                        [code: 'objectMethod::removeOnlyTipps', modalID: 'removeOnlyTipps', label: 'Remove all Titles', message: '', onlyAdmin: false, group: 5],
                        [code: 'objectMethod::removeOnlyDeletedTipps', modalID: 'removeOnlyDeletedTipps', label: 'Remove only deleted Titles', message: '', onlyAdmin: false, group: 4],
                        [code: 'objectMethod::removeWithTipps', modalID: 'removeWithTipps', label: 'Remove the package (with all Titles)', message: '', onlyAdmin: false, group: 6],

                        [code: 'workFlowMethod::manualKbartImport', label: 'Manual KBART Import', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowMethod::updatePackageFromKbartSource', label: 'Trigger KBART Update (Changed Titles)', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowMethod::updatePackageAllTitlesFromKbartSource', label: 'Trigger KBART Update (all Titles)', message: '', onlyAdmin: false, group: 1]
                ]
                break
            case Platform.class.name:
                if (springSecurityService.currentUser.isAdmin()) {
                    [
                            [code: 'workFlowSetStatus::Deleted', label: 'Mark the Platform as deleted', message: '', onlyAdmin: true, group: 1],
                            [code: 'workFlowSetStatus::Removed', label: 'Remove Platform', message: '', onlyAdmin: true, group: 1]
                    ]
                }else{
                    []
                }
                break
            case TitleInstancePackagePlatform.class.name:
                [
                        [code: 'workFlowSetStatus::Retired', label: 'Mark the title as retired', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowSetStatus::Deleted', label: 'Mark the title as deleted', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowSetStatus::Removed', label: 'Remove the title', message: '', onlyAdmin: false, group: 2],
                        [code: 'workFlowSetStatus::Expected', label: 'Mark the title as expected', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowSetStatus::Current', label: 'Mark the title as current', message: '', onlyAdmin: false, group: 1]
                ]
                break
            case User.class.name:
                if (springSecurityService.currentUser.isAdmin()) {
                    [
                            [code: 'workFlowMethod::deleteUser', label: 'Delete', message: '', onlyAdmin: true, group: 1],
                    ]
                }else{
                    []
                }
                break

            case Vendor.class.name:
                if (springSecurityService.currentUser.isAdmin()) {
                    [
                            [code: 'workFlowSetStatus::Deleted', label: 'Mark the Library Supplier as deleted', message: '', onlyAdmin: true, group: 1],
                            [code: 'workFlowSetStatus::Current', label: 'Mark the Library Supplier as current', message: '', onlyAdmin: true, group: 1],
                            [code: 'workFlowSetStatus::Removed', label: 'Remove Library Supplier', message: '', onlyAdmin: true, group: 2]
                    ]
                } else {
                    []
                }
                break
            default:
                []
                break
        }
    }

    List availableActionsWithModal(String domainClassName) {
        switch (domainClassName) {
            case Package.class.name:
                [
                        [modalID: 'removeOnlyDeletedTipps', code: 'objectMethod::removeOnlyDeletedTipps', label: 'Remove only deleted Titles', info: 'You are about to permanently remove all deleted titles from your package. Are you sure you want to continue?'],
                        [modalID: 'removeOnlyTipps', code: 'objectMethod::removeOnlyTipps', label: 'Remove all Titles', info: 'You are about to permanently remove all titles from your package. Are you sure you want to continue?'],
                        [modalID: 'removeWithTipps', code: 'objectMethod::removeWithTipps', label: 'Remove the package (with all Titles)', info: 'You are about to permanently remove all titles from your package. Are you sure you want to continue?'],
                ]
                break
            default:
                []
                break
        }
    }

    def processAction(result, params) {
        log.debug("processAction -> result: ${result}, params:" + params)
        result.objects_to_action.each {
            boolean editable = accessService.checkEditableObject(it, params)
            List availableActions = availableActions(it.class.name)
            Map selectedActionMap = [:]
            availableActions.each {
                if(result.selectedAction == it.code){
                    selectedActionMap = it
                }
            }

            if(selectedActionMap){
                if (selectedActionMap && selectedActionMap.onlyAdmin) {
                    if (springSecurityService.currentUser.isAdmin()) {
                        result = processSelectedAction(result, result.selectedAction, it)
                    }
                }else {
                    if (editable) {
                        println(result)
                        result = processSelectedAction(result, result.selectedAction, it)
                    }
                }
            }

        }
        result
    }

    def processSelectedAction(def result, String action, def component) {
        List method_config = action.split(/\:\:/) as List

        switch (method_config[0]) {
            case "objectMethod":
                log.debug("objectMethod: ${method_config[1]}")
                log.debug("Component: ${component} (${component.class.name})")
                try {
                    // Just try and fire the method.
                    component.invokeMethod("${method_config[1]}", null)
                    // Save the object.
                    component.save(failOnError: true)
                }
                catch (Exception e) {
                    log.error("objectMethod: ${method_config[1]} -> " + e.message)
                    e.printStackTrace()
                }
                break
            case "workFlowSetStatus":
                log.debug("workFlowSetStatus: ${method_config[1]} for " + component )
                RefdataValue status_to_set = RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, method_config[1])
                if (status_to_set) {
                    component.status = status_to_set
                    component.save()
                    log.debug("Updated status of components: ${component}")
                }
                break
            case "workFlowMethod":
                log.debug("workFlowMethod: ${method_config[1]}")
                result = this.invokeMethod("${method_config[1]}", component)
                break
        }
        result
    }

    private Map manualKbartImport(Package pkg) {
        Map result = [:]
        result.ref = grailsLinkGenerator.link(controller: 'package', action: 'kbartImport', id: pkg.id, absolute: true)
        result
    }

    private Map updatePackageAllTitlesFromKbartSource(Package pkg) {
        Map result = [:]
        result = updatePackageFromKbartSource(pkg, true)
        result
    }

    private Map updatePackageFromKbartSource(Package pkg, boolean allTitles = false) {
        log.debug("updatePackageFromKbartSource for Package ${pkg}..")
        Map result = [:]

        if (pkg && pkg.nominalPlatform && pkg.kbartSource && (pkg.kbartSource.url || pkg.kbartSource.ftpServerUrl)) {
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet()
            Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()])
            boolean processRunning = false
            threadArray.each { Thread thread ->
                if (thread.name == 'uPFKS' + pkg.id) {
                    processRunning = true
                }
            }

            if (processRunning) {
                result.error = 'A package update is already in progress. Please wait this has finished.'
            } else {
                executorService.execute({
                    Package aPackage = Package.get(pkg.id)
                    Thread.currentThread().setName('uPFKS' + pkg.id)
                    autoUpdatePackagesService.startAutoPackageUpdate(aPackage, !allTitles)
                })

                result.success = "The package update for Package '${pkg.name}' was started. This runs in the background. When the update has gone through, you will see this on the Auto Update Info of the package tab."
            }
        } else if (!pkg) {
            result.error = "Unable to reference provided Package!"
        } else if (!pkg.nominalPlatform) {
            result.error = "Please check the nominal Platform by the package! No nominal Platform set!"
        }else {
            result.error = "Please check the source for validity! Check URL or FTP SERVER URL"
        }

        result.ref = grailsLinkGenerator.link(controller: 'resource', action: 'show', id: pkg.getOID(), absolute: true)

        result
    }

    Map updateListOfPackageWithKbart(List<Package> packageList, boolean allTitles = false, List<CuratoryGroup> curatoryGroups){
        Map result = [:]

        Set<Thread> threadSetMain = Thread.getAllStackTraces().keySet()
        Thread[] threadArrayMain = threadSetMain.toArray(new Thread[threadSetMain.size()])
        boolean processRunningMain = false
        threadArrayMain.each { Thread thread ->
            if (thread.name == 'uOPWK' + curatoryGroups.id.join('_').substring(0,7)) {
                processRunningMain = true
            }
        }

        if (processRunningMain) {
            result.error = "The package update for ${packageList.size()} Package is already running. Please wait this has finished."
        } else {
            executorService.execute({
                Thread.currentThread().setName('uOPWK' + curatoryGroups.id.join('_').substring(0,7))

                    packageList.each { aPackage ->
                        Package aPackage1 = Package.get(aPackage.id)

                        try {
                            autoUpdatePackagesService.startAutoPackageUpdate(aPackage1, !allTitles)
                        }catch (Exception exception) {
                            log.error("Error by updateListOfPackageWithKbart (${aPackage1.id} -> ${aPackage1.name}): ${exception.message}")
                            //exception.printStackTrace()
                        }

                }
            })

            result.message = "The package update for ${packageList.size()} Package was started. This runs in the background."
        }

        result

    }

    private Map deleteIdentifierNamespace(IdentifierNamespace identifierNamespace) {
        Map result = [:]
        log.info("deleteIdentifierNamespace: ${identifierNamespace}..")
        if (!Identifier.findByNamespace(identifierNamespace)) {
            identifierNamespace.delete()
        } else {
            result.error = "Identifier Namespace is linked with identifier or org or source or platform! Please unlink first!"
        }

        result.ref = grailsLinkGenerator.link(controller: 'resource', action: 'show', id: identifierNamespace.class.name+':'+identifierNamespace.id, absolute: true)
        result
    }

    private Map deleteIdentifier(Identifier identifier) {
        Map result = [:]
        log.info("deleteIdentifier: ${identifier}..")
        identifier.delete()

        result
    }

    private Map deleteUser(User user){
        log.info("Deleting user ${user.id} ..")
        Map result = [:]

        Long userID = user.id

        SavedSearch.executeUpdate("delete from SavedSearch where owner = :utd", [utd: user])
        UserRole.removeAll(user)
        CuratoryGroupUser.findAllByUser(user).each {
            it.delete()
        }

        log.info("Deleting user object ..")
        user.delete()

        log.info("Done")

        if(User.get(userID)){
            result.error = "User can not deleted!"
        }else {
            result.ref = grailsLinkGenerator.link(controller: "search", action: "componentSearch", params: [qbe: 'g:users'], absolute: true)
        }

        result

    }

}

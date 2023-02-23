package wekb

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.web.mapping.LinkGenerator
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
                [
                        [code: 'workFlowSetStatus::Deleted', label: 'Mark the Curatory Group as deleted', message: '', onlyAdmin: true],
                        [code: 'workFlowSetStatus::Removed', label: 'Remove Curatory Group', message: '', onlyAdmin: true]
                ]
                break
            case IdentifierNamespace.class.name:
                [
                        [code: 'workFlowMethod::deleteIdentifierNamespace', label: 'Delete Namespace', message: '', onlyAdmin: true],
                ]
                break
            case Identifier.class.name:

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
                        [code: 'objectMethod::removeOnlyTipps', label: 'Remove only all Titles', message: '', onlyAdmin: false, group: 5],
                        [code: 'objectMethod::removeWithTipps', label: 'Remove the package (with all Titles)', message: '', onlyAdmin: false, group: 6],

                        [code: 'workFlowMethod::manualKbartImport', label: 'Manual KBART Import', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowMethod::updatePackageFromKbartSource', label: 'Trigger KBART Update (Changed Titles)', message: '', onlyAdmin: false, group: 1],
                        [code: 'workFlowMethod::updatePackageAllTitlesFromKbartSource', label: 'Trigger KBART Update (all Titles)', message: '', onlyAdmin: false, group: 1]
                ]
                break
            case Platform.class.name:
                [
                        [code: 'workFlowSetStatus::Deleted', label: 'Mark the Platform as deleted', message: '', onlyAdmin: true, group: 1],
                        [code: 'workFlowSetStatus::Removed', label: 'Remove Platform', message: '', onlyAdmin: true, group: 1]
                ]
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
                [
                        [code: 'workFlowMethod::deleteUser', label: 'Delete', message: '', onlyAdmin: true, group: 1],
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
                    e.printStackTrace()
                    log.error("objectMethod: ${method_config[1]} -> " + e.printStackTrace())
                }
                break
            case "workFlowSetStatus":
                log.debug("workFlowSetStatus: ${method_config[1]}")
                RefdataValue status_to_set = RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, method_config[1])
                if (status_to_set) {
                    def res = Package.executeUpdate("update ${component.class.name} as component set component.status = :st, component.lastUpdated = :currentDate where component = :component", [st: status_to_set, component: component, currentDate: new Date()])
                    log.debug("Updated status of ${res} components")
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
        if (pkg && pkg.kbartSource?.url) {
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet()
            Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()])
            boolean processRunning = false
            threadArray.each { Thread thread ->
                if (thread.name == 'updatePackageFromKbartSource' + pkg.id) {
                    processRunning = true
                }
            }

            if (processRunning) {
                result.error = 'A package update is already in progress. Please wait this has finished.'
            } else {
                executorService.execute({
                    Package aPackage = Package.get(pkg.id)
                    Thread.currentThread().setName('updatePackageFromKbartSource' + pkg.id)
                    autoUpdatePackagesService.startAutoPackageUpdate(aPackage, !allTitles)
                })

                result.success = "The package update for Package '${pkg.name}' was started. This runs in the background. When the update has gone through, you will see this on the Auto Update Info of the package tab."
            }
        } else if (!pkg) {
            result.error = "Unable to reference provided Package!"
        } else {
            result.error = "Please check the Package KbartSource for validity!"
        }

        result.ref = grailsLinkGenerator.link(controller: 'resource', action: 'show', id: pkg.getOID(), absolute: true)

        result
    }

    private Map deleteIdentifierNamespace(IdentifierNamespace identifierNamespace) {
        Map result = [:]
        log.info("deleteIdentifierNamespace: ${identifierNamespace}..")
        if (!Platform.findByTitleNamespace(identifierNamespace) && !KbartSource.findByTargetNamespace(identifierNamespace) && !Identifier.findByNamespace(identifierNamespace)) {
            identifierNamespace.delete()
        } else {
            result.error = "Identifier Namespace is linked with identifier or org or source or platform! Please unlink first!"
        }

        result.ref = grailsLinkGenerator.link(controller: 'resource', action: 'show', id: identifierNamespace.class.name+':'+identifierNamespace.id, absolute: true)
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

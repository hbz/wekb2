package wekb

import grails.gorm.transactions.Transactional
import wekb.utils.ServerUtils

@Transactional
class WorkflowService {

    List availableActions(String domainClassName){
        switch (domainClassName) {
            case CuratoryGroup.class.name:
                [
                        [code: 'workFlowSetStatus::Deleted', label: 'Delete Curatory Group', message: '', onlyAdmin: true],
                        [code: 'workFlowSetStatus::Removed', label: 'Remove Curatory Group', message: '', onlyAdmin: true]
                ]
                break
            case IdentifierNamespace.class.name:
                [
                        [code: 'workFlow_deleteIdentifierNamespace', label: 'Delete Namespace', message: '', onlyAdmin: true],
                ]
                break
            case Identifier.class.name:

                break
            case KbartSource.class.name:
                [
                        [code: 'method::deleteSoft', label: 'Delete Source', message: '', onlyAdmin: false],
                        [code: 'workFlowSetStatus::Removed', label: 'Remove Source', message: '', onlyAdmin: false]
                ]
                break
            case Org.class.name:
                [
                        [code: 'workFlowSetStatus::Deleted', label: 'Delete Provider', message: '', onlyAdmin: false],
                        [code: 'workFlowSetStatus::Retired', label: 'Retire Provider', message: '', onlyAdmin: false],
                        [code: 'workFlowSetStatus::Current', label: 'Set Provider Current', message: '', onlyAdmin: false],
                        [code: 'workFlowSetStatus::Removed', label: 'Remove Provider', message: '', onlyAdmin: true]
                ]
                break
            case Package.class.name:
                [
                        [code: 'method::currentWithTipps', label: 'Mark the package as current (with all Titles)', message: '', onlyAdmin: false],
                        [code: 'method::deleteSoft', label: 'Mark the package as deleted (with all Titles)', perm: 'delete', message: '', onlyAdmin: false],
                        [code: 'method::retireWithTipps', label: 'Mark the package as retired (with all Titles)', message: '', onlyAdmin: false],
                        [code: 'method::removeWithTipps', label: 'Remove the package (with all Titles)', perm: 'delete', message: '', onlyAdmin: false],

                        [code: 'workFlowManualKbartImport', label: 'Manual KBART Import', message: '', onlyAdmin: false],
                        [code: 'workFlowPackageUrlUpdate', label: 'Trigger Update (Changed Titles)', message: '', onlyAdmin: false],
                        [code: 'workFlowPackageUrlUpdateAllTitles', label: 'Trigger Update (all Titles)', message: '', onlyAdmin: false],
                ]
                break
            case Platform.class.name:
                [
                        [code: 'workFlowSetStatus::Deleted', label: 'Delete Platform', message: '', onlyAdmin: false]
                ]
                break
            case TitleInstancePackagePlatform.class.name:
                [
                        [code: 'workFlowSetStatus::Retired', label: 'Mark the title as retired', message: '', onlyAdmin: false],
                        [code: 'workFlowSetStatus::Deleted', label: 'Mark the title as deleted', message: '', onlyAdmin: false],
                        [code: 'workFlowSetStatus::Removed', label: 'Remove the title', message: '', onlyAdmin: false],
                        [code: 'workFlowSetStatus::Expected', label: 'Mark the title as expected', message: '', onlyAdmin: false],
                        [code: 'workFlowSetStatus::Current', label: 'Mark the title as current', message: '', onlyAdmin: false]
                ]
                break
        }
    }

}

<!DOCTYPE html>
<%@ page import="wekb.TitleInstancePackagePlatform; wekb.helper.RDStore;" %>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb :
    <g:if test="${displayobj}">
        ${displayobj.getDomainName()}: ${displayobj.getShowName()}
    </g:if><g:else>
        Component
    </g:else>
    </title>
</head>

<body>

<semui:flashMessage data="${flash}"/>

<g:if test="${displayobj != null}">
    <div class="ui grid">
        <div class="thirteen wide column">

            <g:if test="${displayobj.hasProperty('status') && displayobj.status == RDStore.KBC_STATUS_REMOVED}">
                <div class="ui negative icon huge message">
                    <i class="info icon"></i>

                    <div class="content">
                        <div class="header">
                            Removed component
                        </div>

                        <p>This component has been set to removed and will soon be permanently removed from this system</p>
                    </div>
                </div>
            </g:if>

            <g:if test="${editable}">

                <g:set var="workflowService" bean="workflowService"/>

                <g:set var="object" value="${displayobj.getOID()}"/>

                <g:set var="availableActions" value="${workflowService.availableActions(displayobj.class.name)}"/>
                <g:set var="availableActionModals" value="${workflowService.availableActionsWithModal(displayobj.class.name)}"/>
                <g:each var="availableActionModal" in="${availableActionModals}" status="i">
                <semui:modal id="worklowModal_${availableActionModal.modalID}" title="${availableActionModal.label}" msgSave="Perform action">
                        <g:form controller="workflow"
                                action="action"
                                params="[component: object, selectedAction: availableActionModal.code, curationOverride: params.curationOverride]"
                                class="ui form">

                            <h3 class="ui header">Info: </h3>
                            <p>${availableActionModal.info}</p>

                        </g:form>
                    </semui:modal>
                </g:each>

                <g:if test="${availableActions}">
                    <div class="ui right floated buttons">
                        <semui:actionsDropdown text="Available actions">
                            <g:set var="availableActionsGroupBy"
                                   value="${availableActions.sort { it.group }.groupBy { it.group }}"/>

                            <g:each var="availableActionsGroupByGroup" in="${availableActionsGroupBy}" status="i">
                                <g:each var="action" in="${availableActionsGroupByGroup.value.sort { it.label }}">
                                    <g:if test="${action.onlyAdmin}">
                                        <sec:ifAnyGranted roles="ROLE_ADMIN">
                                            <semui:actionsDropdownItem description="(Admin)" controller="workflow"
                                                                       action="action"
                                                                       params="[component: object, selectedAction: action.code, curationOverride: params.curationOverride]"
                                                                       text="${action.label}"/>
                                        </sec:ifAnyGranted>
                                    </g:if>
                                    <g:else>
                                        <g:if test="${action.code in ["workFlowMethod::updatePackageFromKbartSource", "workFlowMethod::updatePackageAllTitlesFromKbartSource"]}">
                                            <g:if test="${displayobj.kbartSource}">
                                                <semui:actionsDropdownItem controller="workflow" action="action"
                                                                           params="[component: object, selectedAction: action.code, curationOverride: params.curationOverride]"
                                                                           text="${action.label}"/>
                                            </g:if>
                                        </g:if>
                                        <g:else>
                                            <g:if test="${action.modalID}">
                                                <a class="ui item" href="#"
                                                   onclick="$('#worklowModal_${action.modalID}').modal('show');">${action.label}</a>
                                            </g:if>
                                            <g:else>
                                            <semui:actionsDropdownItem controller="workflow" action="action"
                                                                       params="[component: object, selectedAction: action.code, curationOverride: params.curationOverride]"
                                                                       text="${action.label}"/>
                                            </g:else>
                                        </g:else>
                                    </g:else>
                                </g:each>

                                <g:if test="${availableActionsGroupBy.size() > 1 && i < availableActionsGroupBy.size()-1}">
                                    <div class="divider"></div>
                                </g:if>

                            </g:each>
                        </semui:actionsDropdown>
                    </div>
                </g:if>
            </g:if>

            <h1 class="ui header">${displayobj.getDomainName()}: ${displayobj.getShowName()}</h1>


            <div class="ui segment">
                <div class="content wekb-inline-lists">
                    <g:if test="${displaytemplate != null}">
                        <!-- Using display template ${displaytemplate.rendername} -->
                        <g:render template="/templates/domains/${displaytemplate.rendername}"
                                  model="${[d: displayobj, dtype: displayobjclassname_short]}"/>
                    </g:if>
                </div>
            </div>

        </div>

        <div class="three wide column">
            <g:render template="rightBox" model="${[d: displayobj]}"/>
        </div>

        <div class="sixteen wide column">
            <g:if test="${displaytemplate != null}">
                <g:if test="${displaytemplate.rendername in ["curatory_group", "org", "package", "platform", "kbart_source", "tipp"]}">
                    <g:render template="/templates/tabTemplates/domainTabs/${displaytemplate.rendername}Tabs"
                              model="${[d: displayobj]}"/>
                </g:if>
            </g:if>
        </div>
    </div>
</g:if>
<g:else>
    <g:if test="${!noPermission && params.id}">
        <div class="ui piled segment">
            <h2 class="ui header">Not found!</h2>

            <p><g:message code="component.notFound.label" args="[params.id]"/></p>
        </div>
    </g:if>
</g:else>

</body>
</html>

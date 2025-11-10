<!DOCTYPE html>
<%@ page import="wekb.Package; wekb.TitleInstancePackagePlatform; wekb.helper.RDStore;" %>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb :
    <g:if test="${displayobj}">
        <g:message code="${displayobj.class.simpleName.toLowerCase()}.label" default="${displayobj.getDomainName()}"/> : ${displayobj.getShowName()}
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
                           <g:message code="resource.show.removed"/>
                        </div>

                        <p><g:message code="resource.show.removed.info"/></p>
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

            <h1 class="ui header"><g:message code="${displayobj.class.simpleName.toLowerCase()}.label" default="${displayobj.getDomainName()}"/>: ${displayobj.getShowName()}</h1>

            <sec:ifAnyGranted roles="ROLE_ADMIN">
                <g:if test="${displayobj instanceof wekb.Package}">
                    <g:if test="${displayobj.isPackageLinkedInLaser()}">
                        <g:render template="/templates/laserInfosForPkg" model="${[pkg: displayobj]}"/>
                    </g:if>
                    <g:set var="tippDuplicatesByTitleIDCount" value="${displayobj.getTippDuplicatesByTitleIDCount()}"/>
                    <g:set var="tippDuplicatesByURLCount" value="${displayobj.getTippDuplicatesByURLCount()}"/>
                    <g:set var="tippDuplicatesByNameCount" value="${displayobj.getTippDuplicatesByNameCount()}"/>

                    <g:if test="${tippDuplicatesByTitleIDCount > 0}">
                        <div class="ui warning icon message">

                            <div class="content wekb-inline-lists">
                                <div class="header">
                                    Duplicates of Tipps with Title_ID
                                </div>

                                <div class="ui bulleted list">

                                    <div class="item">
                                        <g:link controller="admin" action="findTippDuplicatesByPkg" id="${displayobj.uuid}" target="_blank"
                                                params="[tippsDuplicatesBy: 'titleID', max: 100, offset: 0]">
                                            ${tippDuplicatesByTitleIDCount}
                                        </g:link>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </g:if>

                    <g:if test="${tippDuplicatesByURLCount > 0}">
                        <div class="ui warning icon message">

                            <div class="content wekb-inline-lists">
                                <div class="header">
                                    Duplicates of Tipps with Url
                                </div>

                                <div class="ui bulleted list">

                                    <div class="item">
                                        <g:link controller="admin" action="findTippDuplicatesByPkg" id="${displayobj.uuid}" target="_blank"
                                                params="[tippsDuplicatesBy: 'url', max: 100, offset: 0]">
                                            ${tippDuplicatesByURLCount}
                                        </g:link>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </g:if>

                    <g:if test="${tippDuplicatesByNameCount > 0}">
                        <div class="ui warning icon message">

                            <div class="content wekb-inline-lists">
                                <div class="header">
                                    Duplicates of Tipps with Name
                                </div>

                                <div class="ui bulleted list">

                                    <div class="item">
                                        <g:link controller="admin" action="findTippDuplicatesByPkg" id="${displayobj.uuid}" target="_blank"
                                                params="[tippsDuplicatesBy: 'name', max: 100, offset: 0]">
                                            ${tippDuplicatesByNameCount}
                                        </g:link>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </g:if>
                </g:if>

                <g:if test="${displayobj instanceof wekb.TitleInstancePackagePlatform}">

                    <g:render template="/templates/laserInfosForTipp" model="${[pkg: displayobj.pkg, tipp: displayobj]}"/>

                    <g:if test="${displayobj.getTippDuplicatesByTitleIDCount() > 0}">
                        <div class="ui warning icon message">

                            <div class="content wekb-inline-lists">
                                <div class="header">
                                    Tipp Duplicates with Title_ID
                                </div>

                                <div class="ui bulleted list">
                                    <g:each in="${displayobj.findTippDuplicatesByTitleID()}" var="tipp">
                                        <g:if test="${tipp.id != displayobj.id}">
                                            <div class="item">
                                                <g:link controller="resource" action="show"
                                                        id="${'wekb.TitleInstancePackagePlatform:' + tipp.id}">${tipp.name} [${tipp.status.getI10n('value')}] (${tipp.id})</g:link>
                                            </div>
                                        </g:if>
                                    </g:each>
                                </div>
                            </div>
                        </div>

                    </g:if>

                    <g:if test="${displayobj.getTippDuplicatesByNameCount() > 0}">
                        <div class="ui warning icon message">

                            <div class="content wekb-inline-lists">
                                <div class="header">
                                    Tipp Duplicates with Name
                                </div>

                                <div class="ui bulleted list">
                                    <g:each in="${displayobj.findTippDuplicatesByName()}" var="tipp">
                                        <g:if test="${tipp.id != displayobj.id}">
                                            <div class="item">
                                                <g:link controller="resource" action="show"
                                                        id="${'wekb.TitleInstancePackagePlatform:' + tipp.id}">${tipp.name} [${tipp.status.getI10n('value')}] (${tipp.id})</g:link>
                                            </div>
                                        </g:if>
                                    </g:each>
                                </div>
                            </div>
                        </div>

                    </g:if>

                    <g:if test="${displayobj.getTippDuplicatesByURLCount() > 0}">
                        <div class="ui warning icon message">

                            <div class="content wekb-inline-lists">
                                <div class="header">
                                    Tipp Duplicates with URL
                                </div>

                                <div class="ui bulleted list">
                                    <g:each in="${displayobj.findTippDuplicatesByURL()}" var="tipp">
                                        <g:if test="${tipp.id != displayobj.id}">
                                            <div class="item">
                                                <g:link controller="resource" action="show"
                                                        id="${'wekb.TitleInstancePackagePlatform:' + tipp.id}">${tipp.name} [${tipp.status.getI10n('value')}] (${tipp.id})</g:link>
                                            </div>
                                        </g:if>
                                    </g:each>
                                </div>
                            </div>
                        </div>

                    </g:if>
                </g:if>

            </sec:ifAnyGranted>

            <g:if test="${displaytemplate != null}">
                <!-- Using display template ${displaytemplate.rendername} -->
                <g:render template="/templates/domains/${displaytemplate.rendername}"
                          model="${[d: displayobj, dtype: displayobjclassname_short]}"/>
            </g:if>


        </div>

        <div class="three wide column">
            <g:render template="rightBox" model="${[d: displayobj]}"/>
        </div>

        <div class="sixteen wide column">
            <g:if test="${displaytemplate != null}">
                <g:if test="${displaytemplate.rendername in ["curatory_group", "org", "package", "platform", "kbart_source", "tipp", "vendor"]}">
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

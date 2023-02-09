<!DOCTYPE html>
<%@ page import="wekb.TitleInstancePackagePlatform; wekb.helper.RDStore;" %>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Show
    <g:if test="${displayobj}">
        ${displayobj} (${displayobj.getShowName()})
    </g:if><g:else>
        Component
    </g:else>
    </title>
</head>

<body>

<semui:flashMessage data="${flash}"/>

<g:if test="${displayobj != null}">

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

    <g:if test="${displayobj.respondsTo('availableActions') && editable}">

        <g:set var="object" value="${displayobj.class.name}:${displayobj.id}"/>

        <div class="ui right floated buttons">
        <semui:actionsDropdown text="Available actions">
                <g:each var="action" in="${displayobj.userAvailableActions().sort{it.label}}">
                    <g:if test="${action.code in ["packageUrlUpdate", "packageUrlUpdateAllTitles"]}">
                        <g:if test="${displayobj.kbartSource}">
                            <semui:actionsDropdownItem controller="workflow" action="action"
                                                       params="[component: object, selectedBulkAction: action.code, curationOverride: params.curationOverride]" text="${action.label}"/>
                        </g:if>
                    </g:if>
                    <g:else>
                        <semui:actionsDropdownItem controller="workflow" action="action"
                                                   params="[component: object, selectedBulkAction: action.code, curationOverride: params.curationOverride]" text="${action.label}"/>
                    </g:else>

                </g:each>
        </semui:actionsDropdown>
        </div>
    </g:if>

    <h1 class="ui header">${displayobj.getDomainName()}: ${displayobj.getShowName()}</h1>

    <div class="ui segment">
        <g:render template="rightBox" model="${[d: displayobj]}"/>

        <div class="content wekb-inline-lists">
            <g:if test="${displaytemplate != null}">
                <!-- Using display template ${displaytemplate.rendername} -->
                    <g:render template="/templates/domains/${displaytemplate.rendername}"
                              model="${[d: displayobj, dtype: displayobjclassname_short]}"/>
            </g:if>
        </div>
    </div>

    <br>
    <br>
    <g:if test="${displaytemplate != null}">
        <g:if test="${displaytemplate.rendername in ["org", "package", "platform", "kbartSource", "tipp"]}">
            <g:render template="/templates/tabTemplates/domainTabs/${displaytemplate.rendername}Tabs"
                      model="${[d: displayobj]}"/>
        </g:if>
    </g:if>
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

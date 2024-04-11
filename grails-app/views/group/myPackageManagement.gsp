<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Package Management</title>
</head>

<body>

<g:set var="cleaned_params" value="${params.findAll { it.value && it.value != "" }}"/>

<h1 class="ui header">My Package Management</h1>

<g:if test="${(qbetemplate.message != null)}">
    <semui:message message="${qbetemplate.message}"/>
</g:if>

<g:render template="/search/qbeform"
          model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>

<g:if test="${recset && !init}">
    <div class="ui header">
        <h1>Showing results ${offset.toInteger() + 1} to ${lasthit.toInteger() as int} of
            ${reccount.toInteger() as int}</h1>
    </div>


    <semui:flashMessage data="${flash}"/>

    <g:render template="/search/pagination" model="${cleaned_params}"/>

    <semui:tabs>
        <semui:tabsItemWithLink controller="$controllerName" action="$actionName" activeTab="generalInfos" text="General Information" params="${cleaned_params+[activeTab: "generalInfos"]}"/>
        <semui:tabsItemWithLink controller="$controllerName" action="$actionName" activeTab="vendors" text="Vendors" params="${cleaned_params+[activeTab: "vendors"]}"/>

       %{-- <semui:tabsItemWithLink controller="$controllerName" action="$actionName" activeTab="rangeInfos" text="Range Information" params="${cleaned_params+[activeTab: "rangeInfos"]}"/
           --}%
        <semui:tabsItemWithLink controller="$controllerName" action="$actionName" activeTab="archivingAgencyInfos" text="Archiving Agency" params="${cleaned_params+[activeTab: "archivingAgencyInfos"]}"/>
        <semui:tabsItemWithLink controller="$controllerName" action="$actionName" activeTab="identifiers" text="Identifiers" params="${cleaned_params+[activeTab: "identifiers"]}"/>
        <semui:tabsItemWithLink controller="$controllerName" action="$actionName" activeTab="ddcs" text="Dewey Decimal Classifications" params="${cleaned_params+[activeTab: "ddcs"]}"/>
        <semui:tabsItemWithLink controller="$controllerName" action="$actionName" activeTab="sources" text="Source" params="${cleaned_params+[activeTab: "sources"]}"/>

    </semui:tabs>

    <div class="ui bottom active attached tab segment">
        <g:if test="${params.activeTab == 'generalInfos'}">
            <g:render template="management/package/generalInfos"/>
        </g:if>
        <g:elseif test="${params.activeTab == 'vendors'}">
            <g:render template="management/package/vendors"/>
        </g:elseif>
        <g:elseif test="${params.activeTab == 'rangeInfos'}">
            <g:render template="management/package/rangeInfos"/>
        </g:elseif>
        <g:elseif test="${params.activeTab == 'archivingAgencyInfos'}">
            <g:render template="management/package/archivingAgencyInfos"/>
        </g:elseif>
        <g:elseif test="${params.activeTab == 'identifiers'}">
            <g:render template="management/package/identifiers"/>
        </g:elseif>
        <g:elseif test="${params.activeTab == 'ddcs'}">
            <g:render template="management/package/ddcs"/>
        </g:elseif>
        <g:elseif test="${params.activeTab == 'sources'}">
            <g:render template="management/package/sourceInfos"/>
        </g:elseif>
    </div>

    <g:render template="/search/pagination" model="${cleaned_params}"/>
</g:if>
<g:elseif test="${!init && !params.inline}">
    <g:render template="/search/qbeempty"/>
</g:elseif>
<g:else>
    <semui:message>
        <p>No results.</p>
    </semui:message>
</g:else>

</body>
</html>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>Package Management</title>
</head>

<body>

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

    <g:render template="/search/pagination" model="${params}"/>

    <semui:tabs>
        <semui:tabsItemWithoutLink tab="generalInfos" defaultTab="generalInfos" activeTab="${params.activeTab}">
            General Information
        </semui:tabsItemWithoutLink>
       %{-- <semui:tabsItemWithoutLink tab="rangeInfos" activeTab="${params.activeTab}">
            Range Information
        </semui:tabsItemWithoutLink>--}%
        <semui:tabsItemWithoutLink tab="archivingAgencyInfos" activeTab="${params.activeTab}">
            Archiving Agency
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="identifiers" activeTab="${params.activeTab}">
            Identifiers
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="ddcs" activeTab="${params.activeTab}">
            Dewey Decimal Classifications
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="sources" activeTab="${params.activeTab}">
            Source
        </semui:tabsItemWithoutLink>
    </semui:tabs>



    <semui:tabsItemContent tab="generalInfos" activeTab="${params.activeTab}">
        <g:render template="management/package/generalInfos"/>
    </semui:tabsItemContent>
%{--
    <semui:tabsItemContent tab="rangeInfos" activeTab="${params.activeTab}">
        <g:render template="management/package/rangeInfos"/>
    </semui:tabsItemContent>--}%

    <semui:tabsItemContent tab="archivingAgencyInfos" activeTab="${params.activeTab}">
        <g:render template="management/package/archivingAgencyInfos"/>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="identifiers" activeTab="${params.activeTab}">
        <g:render template="management/package/identifiers"/>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="ddcs" activeTab="${params.activeTab}">
        <g:render template="management/package/ddcs"/>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="sources" activeTab="${params.activeTab}">
        <g:render template="management/package/sourceInfos"/>
    </semui:tabsItemContent>

    <g:render template="/search/pagination" model="${params}"/>
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
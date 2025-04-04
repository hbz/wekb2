<%@ page import="wekb.helper.RDStore;" %>
<g:if test="${d}">
    <semui:tabs>

        <g:if test="${d.publicationType?.value == 'Serial'}">
            <semui:tabsItemWithoutLink tab="tippcoverage"
                                       class="active"  counts="${d.coverageStatements.size()}">
                Coverage
            </semui:tabsItemWithoutLink>
        </g:if>
        <semui:tabsItemWithoutLink tab="identifiers" class="${(d.publicationType?.value != 'Serial') ? 'active' : ''}" counts="${d.ids.size()}">
            Identifiers
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="subjectArea">
            Subject Area
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="ddcs" counts="${d.ddcs.size()}">
            DDCs
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="series">
            Series
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="openAccess">
            Open Access
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="prices" counts="${d.prices.size()}">
            Prices
        </semui:tabsItemWithoutLink>

        <g:if test="${d.pkg && d.pkg.kbartSource}">
            <semui:tabsItemWithoutLink tab="autoUpdatePackageInfos" activeTab="${params.activeTab}" counts="${d.getCountAutoUpdateTippInfos()}">
                Auto Update Infos
            </semui:tabsItemWithoutLink>
        </g:if>

        <g:set var="countManualUpdateInfos" value="${d.getCountManualUpdateTippInfos()}"/>
        <g:if test="${countManualUpdateInfos > 0}">
            <semui:tabsItemWithoutLink tab="manualUpdatePackageInfos" activeTab="${params.activeTab}" counts="${countManualUpdateInfos}">
                Manual Update Infos
            </semui:tabsItemWithoutLink>
        </g:if>

    </semui:tabs>


    <g:if test="${d.publicationType?.value == 'Serial'}">
        <g:render template="/templates/tabTemplates/coverageTab" model="${[d: d]}"/>
    </g:if>

    <g:render template="/templates/tabTemplates/identifiersTab"
              model="${[d: d, activeTab: (d.publicationType?.value != 'Serial')]}"/>

    <g:render template="/templates/tabTemplates/subjectAreaTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/ddcsTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/seriesTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/openAccessTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/pricesTab" model="${[d: d]}"/>

    <g:if test="${d.pkg && d.pkg.kbartSource}">
        <semui:tabsItemContent tab="autoUpdatePackageInfos" activeTab="${params.activeTab}">

            <div class="content">

                <g:link class="display-inline" controller="search" action="inlineSearch"
                        params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'autoUpdatePackageInfos' ? params.offset : '', sort: params.activeTab == 'autoUpdatePackageInfos' ? params.sort : '', order: params.activeTab == 'autoUpdatePackageInfos' ? params.order : '', qbe: 'g:updateTippInfos', qp_tipp_id: d.id, inline: true, refOID: d.getOID(), qp_automaticUpdate: RDStore.YN_YES.class.name+':'+RDStore.YN_YES.id, activeTab: 'autoUpdatePackageInfos', jumpOffset: params.activeTab == 'autoUpdatePackageInfos' ? params.jumpOffset : '']"
                        id="">Package Update Infos on this Source</g:link>

            </div>

        </semui:tabsItemContent>
    </g:if>

    <g:if test="${countManualUpdateInfos > 0}">
        <semui:tabsItemContent tab="manualUpdatePackageInfos" activeTab="${params.activeTab}">

            <div class="content">

                <g:link class="display-inline" controller="search" action="inlineSearch"
                        params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'manualUpdatePackageInfos' ? params.offset : '', sort: params.activeTab == 'manualUpdatePackageInfos' ? params.sort : '', order: params.activeTab == 'manualUpdatePackageInfos' ? params.order : '', qbe: 'g:updateTippInfos', qp_tipp_id: d.id, inline: true, refOID: d.getOID(), qp_automaticUpdate: RDStore.YN_NO.class.name+':'+RDStore.YN_NO.id, activeTab: 'manualUpdatePackageInfos', jumpOffset: params.activeTab == 'manualUpdatePackageInfos' ? params.jumpOffset : '']"
                        id="">Package Update Infos on this Source</g:link>

            </div>

        </semui:tabsItemContent>
    </g:if>

</g:if>

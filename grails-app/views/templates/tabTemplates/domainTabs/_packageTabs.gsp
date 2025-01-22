<%@ page import="wekb.RefdataValue; wekb.helper.RDStore;" %>
<g:if test="${d}">

    <semui:tabs>
        <semui:tabsItemWithoutLink tab="currentTipps" counts="${d.getCurrentTippCount()}"
                                   defaultTab="currentTipps" activeTab="${params.activeTab}">
            Current Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="expectedTipps" counts="${d.getExpectedTippCount()}"
                                   activeTab="${params.activeTab}">
            Expected Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="retiredTipps" counts="${d.getRetiredTippCount()}"
                                   activeTab="${params.activeTab}">
            Retired Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="deletedTipps" counts="${d.getDeletedTippCount()}"
                                   activeTab="${params.activeTab}">
            Deleted Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="identifiers" activeTab="${params.activeTab}" counts="${d.ids.findAll{it.value != 'Unknown'}.size()}">
            Identifiers
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="variantNames" activeTab="${params.activeTab}" counts="${d.variantNames.size()}">
            Alternate Names
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="ddcs" activeTab="${params.activeTab}" counts="${d.ddcs.size()}">
            DDCs
        </semui:tabsItemWithoutLink>

        <g:if test="${d.kbartSource}">
            <semui:tabsItemWithoutLink tab="autoUpdatePackageInfos" activeTab="${params.activeTab}" counts="${d.getCountAutoUpdateInfos()}">
                Auto Update Infos
            </semui:tabsItemWithoutLink>
        </g:if>

        <g:set var="countManualUpdateInfos" value="${d.getCountManualUpdateInfos()}"/>
        <g:if test="${countManualUpdateInfos > 0}">
            <semui:tabsItemWithoutLink tab="manualUpdatePackageInfos" activeTab="${params.activeTab}" counts="${countManualUpdateInfos}">
                Manual Update Infos
            </semui:tabsItemWithoutLink>
        </g:if>
    </semui:tabs>


    <semui:tabsItemContent tab="currentTipps" defaultTab="currentTipps" activeTab="${params.activeTab}">

        <g:if test="${editable}">
            <div class="ui right floated buttons">
                <g:link class="ui button primary" controller="create" action="index"
                        params="[tmpl: 'wekb.TitleInstancePackagePlatform', linkwithPkg: d.id, linkwithPlatform: d.nominalPlatform?.id]">Create new title for this package</g:link>
            </div>
            <br>
            <br>
        </g:if>

        <div class="content">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'currentTipps' ? params.offset : '', sort: params.activeTab == 'currentTipps' ? params.sort : '', order: params.activeTab == 'currentTipps' ? params.order : '', qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_CURRENT.id, activeTab: 'currentTipps', jumpOffset: params.activeTab == 'currentTipps' ? params.jumpOffset : '']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="retiredTipps" activeTab="${params.activeTab}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'retiredTipps' ? params.offset : '', sort: params.activeTab == 'retiredTipps' ? params.sort : '', order: params.activeTab == 'retiredTipps' ? params.order : '', qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_RETIRED.id, activeTab: 'retiredTipps', jumpOffset: params.activeTab == 'retiredTipps' ? params.jumpOffset : '']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="expectedTipps" activeTab="${params.activeTab}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'expectedTipps' ? params.offset : '', sort: params.activeTab == 'expectedTipps' ? params.sort : '', order: params.activeTab == 'expectedTipps' ? params.order : '', qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_EXPECTED.id, activeTab: 'expectedTipps', jumpOffset: params.activeTab == 'expectedTipps' ? params.jumpOffset : '']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="deletedTipps" activeTab="${params.activeTab}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'deletedTipps' ? params.offset : '', sort: params.activeTab == 'deletedTipps' ? params.sort : '', order: params.activeTab == 'deletedTipps' ? params.order : '', qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_DELETED.id, activeTab: 'deletedTipps', jumpOffset: params.activeTab == 'deletedTipps' ? params.jumpOffset : '']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>

    <g:render template="/templates/tabTemplates/variantNamesTab" model="${[d: d, showActions: true]}"/>

    <g:render template="/templates/tabTemplates/ddcsTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/identifiersTab" model="${[d: d]}"/>

    <g:if test="${d.kbartSource}">
        <semui:tabsItemContent tab="autoUpdatePackageInfos" activeTab="${params.activeTab}">

            <div class="content">

                <g:link class="display-inline" controller="search" action="inlineSearch"
                        params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'autoUpdatePackageInfos' ? params.offset : '', sort: params.activeTab == 'retiredTipps' ? params.sort : '', order: params.activeTab == 'retiredTipps' ? params.order : '', qbe: 'g:updatePackageInfos', qp_pkg_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_pkg_id', 'qp_pkg'], activeTab: 'autoUpdatePackageInfos', qp_automaticUpdate: RDStore.YN_YES.class.name+':'+RDStore.YN_YES.id, jumpOffset: params.activeTab == 'autoUpdatePackageInfos' ? params.jumpOffset : '']"
                        id="">Update Package Info on this Source</g:link>

            </div>

        </semui:tabsItemContent>
    </g:if>

    <g:if test="${countManualUpdateInfos > 0}">
        <semui:tabsItemContent tab="manualUpdatePackageInfos" activeTab="${params.activeTab}">

            <div class="content">

                <g:link class="display-inline" controller="search" action="inlineSearch"
                        params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'autoUpdatePackageInfos' ? params.offset : '', sort: params.activeTab == 'retiredTipps' ? params.sort : '', order: params.activeTab == 'retiredTipps' ? params.order : '', qbe: 'g:updatePackageInfos', qp_pkg_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_pkg_id', 'qp_pkg'], activeTab: 'manualUpdatePackageInfos', qp_automaticUpdate: RDStore.YN_NO.class.name+':'+RDStore.YN_NO.id]"
                        id="">Update Package Info on this Source</g:link>

            </div>

        </semui:tabsItemContent>
    </g:if>

</g:if>
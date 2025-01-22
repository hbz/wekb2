<%@ page import="wekb.helper.RDStore; wekb.RefdataValue; wekb.helper.RCConstants" %>
<g:if test="${d.id}">
    <semui:tabs>
        <semui:tabsItemWithoutLink tab="packages" defaultTab="packages" activeTab="${params.activeTab}"
                                   counts="${d.getProvidedPackages().size()}">
            Packages
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="currentTitles" activeTab="${params.activeTab}"
                                   counts="${d.getCurrentTippCount()}">
            Current Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="retiredTipps" counts="${d.getRetiredTippCount()}"
                                   activeTab="${params.activeTab}">
            Retired Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="expectedTipps" counts="${d.getExpectedTippCount()}"
                                   activeTab="${params.activeTab}">
            Expected Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="deletedTipps" counts="${d.getDeletedTippCount()}"
                                   activeTab="${params.activeTab}">
            Deleted Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="providers" activeTab="${params.activeTab}"
                                   counts="${d.getProvidedPlatforms().size()}">
            Providers
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="platforms" activeTab="${params.activeTab}"
                                   counts="${d.getProvidedPlatforms().size()}">
            Platforms
        </semui:tabsItemWithoutLink>
    </semui:tabs>


    <semui:tabsItemContent tab="platforms" activeTab="${params.activeTab}">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'platforms' ? params.offset : '', sort: params.activeTab == 'platforms' ? params.sort : '', order: params.activeTab == 'platforms' ? params.order : '', qbe: 'g:platforms', refOID: d.getOID(), inline: true, qp_curgroup: d.getOID(), hide: ['qp_curgroup'], activeTab: 'platforms', jumpOffset: params.activeTab == 'platforms' ? params.jumpOffset : '']"
                id="">Platforms published</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="currentTitles" activeTab="${params.activeTab}">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'currentTitles' ? params.offset : '', sort: params.activeTab == 'currentTitles' ? params.sort : '', order: params.activeTab == 'currentTitles' ? params.order : '', qbe: 'g:tipps', refOID: d.getOID(), inline: true, qp_curgroup: d.getOID(), hide: ['qp_curgroup'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_CURRENT.id, activeTab: 'currentTitles', jumpOffset: params.activeTab == 'currentTitles' ? params.jumpOffset : '']"
                id="">Titles published</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="retiredTipps" activeTab="${params.activeTab}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'retiredTipps' ? params.offset : '', sort: params.activeTab == 'retiredTipps' ? params.sort : '', order: params.activeTab == 'retiredTipps' ? params.order : '', qbe: 'g:tipps', refOID: d.getOID(), inline: true, qp_curgroup: d.getOID(), hide: ['qp_curgroup'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_RETIRED.id, activeTab: 'retiredTipps', jumpOffset: params.activeTab == 'retiredTipps' ? params.jumpOffset : '']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="expectedTipps" activeTab="${params.activeTab}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'expectedTipps' ? params.offset : '', sort: params.activeTab == 'expectedTipps' ? params.sort : '', order: params.activeTab == 'expectedTipps' ? params.order : '', qbe: 'g:tipps', refOID: d.getOID(), inline: true, qp_curgroup: d.getOID(), hide: ['qp_curgroup'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_EXPECTED.id, activeTab: 'expectedTipps']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="deletedTipps" activeTab="${params.activeTab}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'deletedTipps' ? params.offset : '', sort: params.activeTab == 'deletedTipps' ? params.sort : '', order: params.activeTab == 'deletedTipps' ? params.order : '', qbe: 'g:tipps', refOID: d.getOID(), inline: true, qp_curgroup: d.getOID(), hide: ['qp_curgroup'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_DELETED.id, activeTab: 'deletedTipps', jumpOffset: params.activeTab == 'deletedTipps' ? params.jumpOffset : '']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>

    <semui:tabsItemContent tab="packages" defaultTab="packages" activeTab="${params.activeTab}">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'packages' ? params.offset : '', sort: params.activeTab == 'packages' ? params.sort : '', order: params.activeTab == 'packages' ? params.order : '', qbe: 'g:packages', refOID: d.getOID(), inline: true, qp_curgroup: d.getOID(), hide: ['qp_curgroup'], activeTab: 'packages', jumpOffset: params.activeTab == 'packages' ? params.jumpOffset : '']"
                id="">Packages on this Platform</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="providers" activeTab="${params.activeTab}">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'providers' ? params.offset : '', sort: params.activeTab == 'providers' ? params.sort : '', order: params.activeTab == 'providers' ? params.order : '', qbe: 'g:orgs', refOID: d.getOID(), inline: true, qp_curgroup: d.getOID(), hide: ['qp_curgroup'], activeTab: 'providers', jumpOffset: params.activeTab == 'providers' ? params.jumpOffset : '']"
                id="">Providers published</g:link>
    </semui:tabsItemContent>

</g:if>
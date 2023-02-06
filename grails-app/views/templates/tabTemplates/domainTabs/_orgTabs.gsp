<%@ page import="wekb.helper.RCConstants" %>
<g:if test="${d.id}">
    <semui:tabs>
        <semui:tabsItemWithoutLink tab="identifiers" defaultTab="identifiers" activeTab="${params.activeTab}" counts="${d.ids.size()}">
            Identifiers
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="variantNames" activeTab="${params.activeTab}" counts="${d.variantNames.size()}">
            Alternate Names
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="packages" activeTab="${params.activeTab}" counts="${d.providedPackages.size()}">
            Packages
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="titles" activeTab="${params.activeTab}" counts="${d.getCurrentTippCount()}">
            Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="platforms" activeTab="${params.activeTab}" counts="${d.getCombosByPropertyNameAndStatus('providedPlatforms', 'Active').size()}">
            Platforms
        </semui:tabsItemWithoutLink>
    </semui:tabs>


    <g:render template="/templates/tabTemplates/identifiersTab" model="${[d: d, defaultTab: 'identifiers']}"/>

    <g:render template="/templates/tabTemplates/variantNamesTab" model="${[d: d, showActions: true]}"/>

    <semui:tabsItemContent tab="platforms" activeTab="${params.activeTab}">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:platforms', refOID: d.getOID(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id'], activeTab: 'platforms']"
                    id="">Titles published</g:link>
    </semui:tabsItemContent>
    <semui:tabsItemContent tab="titles" activeTab="${params.activeTab}">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:tipps', refOID: d.getOID(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id'], activeTab: 'titles']"
                    id="">Titles published</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="packages" activeTab="${params.activeTab}">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:packages', refOID: d.getOID(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id'], activeTab: 'packages']"
                    id="">Packages on this Platform</g:link>
    </semui:tabsItemContent>

</g:if>
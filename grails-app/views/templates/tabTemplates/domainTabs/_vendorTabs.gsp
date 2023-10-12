<%@ page import="wekb.helper.RCConstants" %>
<g:if test="${d.id}">
%{--
    <semui:tabs>
        <semui:tabsItemWithoutLink tab="packages" activeTab="${params.activeTab}" counts="${d.providedPackages.size()}">
            Packages
        </semui:tabsItemWithoutLink>
    </semui:tabs>

    <semui:tabsItemContent tab="packages" activeTab="${params.activeTab}">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:packages', refOID: d.getOID(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id'], activeTab: 'packages']"
                    id="">Packages on this Platform</g:link>
    </semui:tabsItemContent>
--}%

</g:if>
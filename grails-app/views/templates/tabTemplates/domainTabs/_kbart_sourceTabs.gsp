<g:if test="${d.id}">
    <semui:tabs>

        <semui:tabsItemWithoutLink tab="packages" defaultTab="packages" activeTab="${params.activeTab}">
            Packages
        </semui:tabsItemWithoutLink>
      %{--  <semui:tabsItemWithoutLink tab="notes">
            Notes
        </semui:tabsItemWithoutLink>--}%

    </semui:tabs>

    <semui:tabsItemContent tab="packages" defaultTab="packages" activeTab="${params.activeTab}">
        <div class="content">

                    <g:link class="display-inline" controller="search" action="inlineSearch"
                            params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:packages', qp_source_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_source', 'qp_source_id']]"
                            id="">Packages on this Source</g:link>

        </div>
    </semui:tabsItemContent>

   %{-- <semui:tabsItemContent tab="notes">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    Notes
                </dt>
                <dd>
                    <g:link class="display-inline" controller="search" action="inlineSearch"
                            params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.activeTab == 'retiredTipps' ? params.sort : '', order: params.activeTab == 'retiredTipps' ? params.order : '', qbe: 'g:notes', qp_ownerClassID: d.id, inline: true, qp_ownerClass: d.getClass().name]"
                            id="">Notes on this KbartSource</g:link>
                </dd>
            </dl>
        </div>
    </semui:tabsItemContent>--}%

</g:if>

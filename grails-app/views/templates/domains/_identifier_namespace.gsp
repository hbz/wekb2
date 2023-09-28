<%@ page import="wekb.helper.RCConstants" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">Value</dt>
            <dd><semui:xEditable owner="${d}" field="value"/></dd>

        </dl>
        <dl>
            <dt class="control-label">Name</dt>
            <dd><semui:xEditable owner="${d}" field="name"/></dd>

        </dl>
        <dl>
            <dt class="control-label">Category</dt>
            <dd><semui:xEditable owner="${d}" field="family"/></dd>

        </dl>
        <dl>
            <dt class="control-label">Pattern</dt>
            <dd><semui:xEditable owner="${d}" field="pattern"/></dd>

        </dl>
        <dl>
            <dt class="control-label">Target Type</dt>
            <dd><semui:xEditableRefData owner="${d}" field="targetType"
                                        config="${RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE}"/></dd>
        </dl>

        <g:if test="${d.id}">
            <semui:tabs>
                <semui:tabsItemWithoutLink tab="identifiers" defaultTab="identifiers" activeTab="${params.activeTab}"
                                           counts="${d.getIdentifiersCount()}">
                    Identifiers
                </semui:tabsItemWithoutLink>
            </semui:tabs>


            <semui:tabsItemContent tab="identifiers" activeTab="${params.activeTab}">
                <g:link class="display-inline" controller="search" action="inlineSearch"
                        params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:identifiers', refOID: d.getOID(), inline: true, qp_namespace_value: d.getOID(), hide: ['qp_namespace_value'], activeTab: 'identifiers']"
                        id="">Identifiers</g:link>
            </semui:tabsItemContent>
        </g:if>
    </div>
</div>
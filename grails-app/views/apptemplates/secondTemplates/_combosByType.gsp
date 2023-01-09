<%@ page import="wekb.helper.RCConstants" %>
<g:set var="ctxoid" value="${wekb.KBComponent.deproxy(d).class.name}:${d.id}"/>
<g:set var="pstring" value="${property + '_status'}"/>
<g:set var="pstatus" value="${params[pstring] ?: (combo_status ?: 'Active')}"/>

<g:if test="${controllerName != 'public'}">

    <div style="margin:5px 0px;">
        <g:form method="POST" controller="${controllerName}" action="${actionName}" fragment="${fragment}"
                params="${params.findAll { k, v -> k != pstring }}">

            <span>Hide Deleted:</span> <g:select name="${pstring}" optionKey="key" optionValue="value"
                                                 from="${['Active': 'On']}" value="${pstatus}"
                                                 noSelection="${[null: 'Off']}"/>
        </g:form>

    </div>
</g:if>

<table class="ui selectable striped sortable celled table">
    <thead>
    <tr>
        <g:each in="${cols}" var="ch">
            <th>${ch.colhead}</th>
        </g:each>
        <g:if test="${editable && !noaction}">
            <th>Actions</th>
        </g:if>
    </tr>
    </thead>
    <tbody>
    <g:each in="${d.getCombosByPropertyNameAndStatus(property, pstatus)}" var="row">
        <g:set var="combooid" value="${wekb.KBComponent.deproxy(row).class.name}:${row.id}"/>
        <g:if test="${d.isComboReverse(property)}">
            <g:set var="linkedoid"
                   value="${wekb.KBComponent.deproxy(row.fromComponent).class.name}:${row.fromComponent.id}"/>
        </g:if>
        <g:else>
            <g:set var="linkedoid"
                   value="${wekb.KBComponent.deproxy(row.toComponent).class.name}:${row.toComponent.id}"/>
        </g:else>
        <tr>
            <g:each in="${cols}" var="c">
                <td>
                    <g:if test="${c.action == 'link'}">
                        <g:link controller="resource" action="show"
                                id="${linkedoid}">${groovy.util.Eval.x(row, 'x.' + c.expr)}</g:link>
                    </g:if>
                    <g:elseif test="${c.action == 'editRefData'}">
                        <semui:xEditableRefData owner="${row}" field="${c.expr}" config="${RCConstants.COMBO_STATUS}"/>
                    </g:elseif>
                    <g:else>
                        <span class="${row.status?.value == 'Deleted' ? 'text-deleted' : ''}"
                              title="${row.status?.value == 'Deleted' ? 'This link has been marked as Deleted.' : ''}">
                            ${groovy.util.Eval.x(row, 'x.' + c.expr)}
                        </span>
                    </g:else>
                </td>
            </g:each>

            <g:if test="${editable && !noaction}">
                <td>
                    <g:if test="${!onlyUnlink}">
                        <span>
                            <g:if test="${row.status?.value == 'Deleted'}">
                                <g:link
                                        controller='ajaxSupport'
                                        action='genericSetRel'
                                        params="${['pk': 'wekb.Combo:' + row.id, 'name': 'status', 'fragment': fragment, value: 'wekb.RefdataValue:' + wekb.RefdataCategory.lookup(RCConstants.COMBO_STATUS, 'Active').id]}"
                                        class="confirm-click btn-delete"
                                        title="Reactivate deleted link"
                                        data-confirm-message="Are you sure you wish to remove this ${row.toComponent.niceName}?">Reactivate</g:link>
                            </g:if>
                            <g:else>
                                <g:link
                                        controller='ajaxSupport'
                                        action='deleteCombo'
                                        params="${['id': row.id, 'fragment': fragment, 'keepLink': true, 'propagate': "true"]}"
                                        class="confirm-click btn-delete"
                                        title="Mark this link as 'Deleted'. This will prevent future automatic linkage of these components."
                                        data-confirm-message="Are you sure you wish to remove this ${row.toComponent.niceName}?">Delete</g:link>
                            </g:else>
                        </span>
                        &nbsp;–&nbsp;
                    </g:if>

                    <g:link
                            controller='ajaxSupport'
                            action='deleteCombo'
                            params="${['id': row.id, 'fragment': fragment, 'propagate': "true"]}"
                            class="confirm-click btn-delete"
                            title="Delete this link"
                            data-confirm-message="Are you sure you wish to delete this ${row.toComponent.niceName}?">Delete</g:link>
                </td>
            </g:if>
        </tr>
    </g:each>
    </tbody>
</table>

<asset:script>
    $("select[name='${pstring}']").change(function(event) {
    var form =$(event.target).closest("form")
    form.submit();
  });
</asset:script>

<%@ page import="wekb.ClassUtils; wekb.helper.RDStore; wekb.TitleInstancePackagePlatform; wekb.Combo;" %>
<semui:tabsItemContent tab="identifiers" class="${activeTab ? 'active' : ''}" defaultTab="${defaultTab}" activeTab="${params.activeTab}" counts="${d.ids.size()}">

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>Identifier Namespace Name</th>
            <th>Identifier Namespace Value</th>
            <th>Identifier</th>
            <g:if test="${editable}">
                <th>Actions</th>
            </g:if>
        </tr>
        </thead>
        <tbody>
            <g:each in="${d.ids?.sort { it.namespace?.value }}" var="identifier">
                <tr>
                    <td>
                        ${identifier.namespace.name}
                    </td>
                    <td>
                        ${identifier.namespace.value}
                    </td>
                    <td>
                            <semui:xEditable owner="${identifier}" field="value" />
                            &nbsp;
                            <g:link controller="resource" action="show" id="${identifier.class.name}:${identifier.id}" title="Jump to resource"><i class="fas fa-eye"></i></g:link>
                    </td>
                    <g:if test="${editable}">
                        <td>
                            <g:link controller='ajaxHtml'
                                    action='delete'
                                    params="${["__context": "${identifier.class.name}:${identifier.id}", 'tab': tab, curationOverride: params.curationOverride]}"
                                    class="confirm-click btn-delete"
                                    title="Delete this link"
                                    data-confirm-message="Are you sure you wish to delete this Identifier (${identifier.namespace.value}: ${identifier.value})?">Delete</g:link>
                        </td>
                    </g:if>
                </tr>
            </g:each>
        </tbody>
    </table>

            <g:if test="${editable}">
                <g:set var="ctxoid" value="${wekb.ClassUtils.deproxy(d).class.name}:${d.id}"/>
                <a class="ui right floated black button" href="#" onclick="$('#identifiersModal').modal('show');">Add Identifier</a>
                <br>
                <br>
                <semui:modal id="identifiersModal" title="Add Identifier">

                    <g:form controller="ajaxHtml" action="addIdentifier" class="ui form">
                        <input type="hidden" name="hash" value="${hash}"/>
                        <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                        <input type="hidden" name="__context" value="${ctxoid}" />

                        <div class="field">
                            <label>Identifier Namespace</label>
                            <semui:simpleReferenceDropdown name="identifierNamespace"
                                                           baseClass="wekb.IdentifierNamespace"
                                                           filter1="${d.class.simpleName}"/>
                        </div>

                        <div class="field">
                            <label>Identifier Value</label>

                            <input type="text" name="identifierValue" required/>
                        </div>

                    </g:form>
                </semui:modal>
            </g:if>
</semui:tabsItemContent>
<%@ page import="wekb.Package; wekb.helper.RCConstants; wekb.RefdataCategory;" %>
<g:set var="counter" value="${offset}"/>


<div style="overflow-x: auto">
    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th rowspan="2">#</th>
            <semui:sortableColumn property="name" title="Name" rowspan="2"
                                  params="${params}"/>
            <th colspan="5">
            </th>
            <th rowspan="2"></th>
        </tr>
        <tr>
            <th>#</th>
            <th>Identifier Namespace Name</th>
            <th>Identifier Namespace Value</th>
            <th>Identifier</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${new_recset}" var="r">
            <g:if test="${r != null}">
                <g:set var="row_obj" value="${r.obj}"/>
                <tr>
                    <td>${++counter}</td>
                    <td>
                        <semui:xEditable owner="${row_obj}" field="name" required="true" overwriteEditable="${false}"/>
                    </td>
                    <td colspan="5">
                        <g:if test="${row_obj.ids}">
                            <table class="ui selectable striped sortable celled table">
                                <tbody>
                                <g:each in="${row_obj.ids.sort { it.namespace?.value }}" var="identifier" status="i">
                                    <tr>
                                        <td>${i + 1}</td>
                                        <td>
                                            ${identifier.namespace.name}
                                        </td>
                                        <td>
                                            ${identifier.namespace.value}
                                        </td>
                                        <td>
                                            <semui:xEditable owner="${identifier}" field="value"/>
                                            &nbsp;
                                            <g:link controller="resource" action="show"
                                                    id="${identifier.getOID()}"
                                                    title="Jump to resource"><i class="fas fa-eye"></i></g:link>
                                        </td>
                                        <g:if test="${editable}">
                                            <td>
                                                <g:link controller='ajaxHtml'
                                                        action='delete'
                                                        params="${["__context": "${identifier.getOID()}", 'tab': tab, curationOverride: params.curationOverride]}"
                                                        class="confirm-click btn-delete"
                                                        title="Delete this link"
                                                        data-confirm-message="Are you sure you wish to delete this Identifier (${identifier.namespace.value}: ${identifier.value})?">Delete</g:link>
                                            </td>
                                        </g:if>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </g:if>

                        <a class="ui right floated black button" href="#"
                           onclick="$('#identifiersModal_${row_obj.uuid}').modal('show');">Add Identifier</a>
                        <br>
                        <br>
                        <semui:modal id="identifiersModal_${row_obj.uuid}" title="Add Identifier">

                            <g:form controller="ajaxHtml" action="addIdentifier" class="ui form">
                                <input type="hidden" name="activeTab" value="identifiers"/>

                                <input type="hidden" name="__context" value="${row_obj.getOID()}"/>

                                <div class="field">
                                    <label>Identifier Namespace</label>
                                    <semui:simpleReferenceDropdown name="identifierNamespace"
                                                                   baseClass="wekb.IdentifierNamespace"
                                                                   filter1="${row_obj.class.simpleName}"/>
                                </div>

                                <div class="field">
                                    <label>Identifier Value</label>

                                    <input type="text" name="identifierValue" required/>
                                </div>

                            </g:form>
                        </semui:modal>
                    </td>
                    <td>
                        <g:link class="ui icon button" controller="resource" action="show" id="${row_obj.getOID()}">
                            <i class="edit icon"></i>
                        </g:link>
                    </td>
                </tr>
            </g:if>
            <g:else>
                <tr>
                    <td>Error - Row not found</td>
                </tr>
            </g:else>
        </g:each>
        </tbody>
    </table>
</div>
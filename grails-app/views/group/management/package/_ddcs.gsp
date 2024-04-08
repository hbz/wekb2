<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory;" %>
<g:set var="counter" value="${offset}"/>

<g:form method="post" class="ui form" controller="${controllerName}" action="${actionName}"
        params="[activeTab: 'ddcs']">

    <div class="ui segment">
        <h1 class="ui header">Bulk Process</h1>

        <div class="field">
            <label>Dewey Decimal Classification:</label>

            <g:select from="${RefdataCategory.lookup(RCConstants.DDC).sort { it.value }}"
                      class="dropdown fluid"
                      id="ddcSelection"
                      optionKey="${{ it.class.name + ':' + it.id }}"
                      optionValue="${{ it.value + ': ' + it.getI10n('value') }}"
                      name="ddc"
                      value=""/>
        </div>

        <button class="ui button primary" type="submit" value="changeDdcs"
                name="processOption">Do bulk process to the selected items</button>

        <br>
        <br>
    </div>

    <div style="overflow-x: auto">
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th rowspan="2"></th>
                <th rowspan="2">#</th>
                <semui:sortableColumn property="name" title="Name" rowspan="2"
                                      params="${params}"/>
                <th colspan="3">
                </th>
                <th rowspan="2"></th>
            </tr>
            <tr>
                <th><g:if test="${editable}"><input id="select-all" type="checkbox" name="chkall" /> <div id="numberOfChecked">Select (0)</div></g:if></th>
                <th>#</th>
                <th>Dewey Decimal Classification</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${new_recset}" var="r">
                <g:if test="${r != null}">
                    <g:set var="row_obj" value="${r.obj}"/>
                    <tr>
                        <td>
                            <g:if test="${editable}">
                                <g:checkBox class="bulkcheck" id="selectedPackages_${row_obj.uuid}" name="selectedPackages"
                                            value="${row_obj.uuid}"
                                            checked="false"/>
                            </g:if>
                        </td>
                        <td>${++counter}</td>
                        <td>
                            <semui:xEditable owner="${row_obj}" field="name" required="true"
                                             overwriteEditable="${false}"/>
                        </td>
                        <td colspan="3">
                            <g:if test="${row_obj.ddcs}">
                                <table class="ui selectable striped sortable celled table">
                                    <tbody>
                                    <g:each in="${row_obj.ddcs.sort { it.value }}" var="ddc" status="i">
                                        <tr>
                                            <td>${i + 1}</td>
                                            <td>
                                                ${ddc.value}: ${ddc.getI10n('value')}
                                            </td>
                                            <g:if test="${editable}">
                                                <td><g:link class='ui mini button negative' controller='ajaxHtml'
                                                            action='unlinkManyToMany'
                                                            params="${["__context": "${row_obj.getOID()}", "__property": "ddcs", "__itemToRemove": "${ddc.getOID()}", activeTab: 'ddcs']}">Delete</g:link>
                                                </td>
                                            </g:if>
                                        </tr>
                                    </g:each>
                                    </tbody>
                                </table>
                            </g:if>
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

    <g:javascript>
        $("#select-all").click(function() {
            $('#select-all').is( ":checked")? $('.bulkcheck').prop('checked', true) : $('.bulkcheck').prop('checked', false);
            var numberOfChecked = $('.bulkcheck:checked').length;
            $("#numberOfChecked").html("Select ("+numberOfChecked+")");
        });

        $(".bulkcheck").click(function() {
            var numberOfChecked = $('.bulkcheck:checked').length;
            $("#numberOfChecked").html("Select ("+numberOfChecked+")");
        });
    </g:javascript>
</g:form>
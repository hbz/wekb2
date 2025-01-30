<%@ page import="wekb.helper.RDStore; wekb.helper.RCConstants; wekb.RefdataCategory; wekb.Vendor;" %>
<g:set var="counter" value="${offset}"/>

<g:form method="post" class="ui form" controller="${controllerName}" action="${actionName}"
        params="[activeTab: 'vendors']">

    <div class="ui segment">
        <h1 class="ui header">Bulk Process</h1>

        <div class="field">
            <label>Library Suppliers:</label>

            <g:select from="${Vendor.findAllByStatus(RDStore.KBC_STATUS_CURRENT).sort { it.name }}"
                      class="dropdown fluid"
                      id="vendorSelection"
                      optionKey="${{ it.class.name + ':' + it.id }}"
                      optionValue="${{ it.name }}"
                      name="vendor"
                      value=""/>
        </div>

        <div class="grouped fields">
            <label>What should be done with Library Supplier?</label>
            <div class="field">
                <div class="ui radio checkbox">
                    <input type="radio" name="processLinkVendor" value="linkVendor" checked="checked">
                    <label>Link</label>
                </div>
            </div>
            <div class="field">
                <div class="ui radio checkbox">
                    <input type="radio" name="processLinkVendor" value="unlinkVendor">
                    <label>Unlink</label>
                </div>
            </div>
        </div>

        <button class="ui button primary" type="submit" value="addVendor"
                name="processOption">Start Bulk Process</button>

        <br>
        <br>
    </div>

    <div style="overflow-x: auto">
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th rowspan="2"><g:if test="${editable}"><input id="select-all" type="checkbox" name="chkall" /> <div id="numberOfChecked">Select (0)</div></g:if></th>
                <th rowspan="2">#</th>
                <semui:sortableColumn property="name" title="Name" rowspan="2"
                                      params="${params}"/>
                <th colspan="3">
                </th>
                <th rowspan="2"></th>
            </tr>
            <tr>
                <th>#</th>
                <th>Library Suppliers</th>
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
                            <g:if test="${row_obj.vendors}">
                                <table class="ui selectable striped sortable celled table">
                                    <tbody>
                                    <g:each in="${row_obj.vendors.sort { it.vendor.name }}" var="packageVendor" status="i">
                                        <tr>
                                            <td>${i + 1}</td>
                                            <td>
                                                ${packageVendor.vendor.name}
                                            </td>
                                            <g:if test="${editable}">
                                                <td>
                                                    <g:link class='ui negative mini button' controller='ajaxHtml'
                                                            action='removeVendorFromPackage'
                                                            params="${["__context": "${row_obj.getOID()}", "__relatedObject":"${packageVendor.getOID()}", activeTab: 'vendors']}">Unlink</g:link>
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
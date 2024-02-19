<%@ page import="wekb.helper.RDStore; wekb.helper.RCConstants; wekb.RefdataCategory; wekb.Vendor;" %>
<g:set var="counter" value="${offset}"/>

<g:form method="post" class="ui form" controller="${controllerName}" action="${actionName}"
        params="[activeTab: 'vendors']">

    <div class="ui segment">
        <h1 class="ui header">Bulk Process</h1>

        <div class="field">
            <label>Vendors:</label>

            <g:select from="${Vendor.findAllByStatus(RDStore.KBC_STATUS_CURRENT).sort { it.name }}"
                      class="dropdown fluid"
                      id="vendorSelection"
                      optionKey="${{ it.class.name + ':' + it.id }}"
                      optionValue="${{ it.name }}"
                      name="vendor"
                      value=""/>
        </div>

        <button class="ui button primary" type="submit" value="addVendor"
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
                <th>#</th>
                <th>Vendors</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${new_recset}" var="r">
                <g:if test="${r != null}">
                    <g:set var="row_obj" value="${r.obj}"/>
                    <tr>
                        <td>
                            <g:if test="${editable}">
                                <g:checkBox id="selectedPackages_${row_obj.uuid}" name="selectedPackages"
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
</g:form>
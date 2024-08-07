<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory;" %>
<g:set var="counter" value="${offset}"/>

<g:form method="post" class="ui form" controller="${controllerName}" action="${actionName}"
        params="[activeTab: 'generalInfos']">

    <g:render template="/group/management/managementForm" model="[batchForm: packageGeneralInfosBatchForm]"/>


    <div style="overflow-x: auto">
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th><g:if test="${editable}"><input id="select-all" type="checkbox" name="chkall" /> <div id="numberOfChecked">Select (0)</div></g:if></th>
                <th>#</th>
                <semui:sortableColumn property="name" title="Name"
                                      params="${params}"/>
                <semui:sortableColumn property="status" title="Status"
                                      params="${params}"/>
                <semui:sortableColumn property="breakable" title="Breakable"
                                      params="${params}"/>
                <semui:sortableColumn property="contentType" title="Content Type"
                                      params="${params}"/>
                <semui:sortableColumn property="file" title="File"
                                      params="${params}"/>
                <semui:sortableColumn property="openAccess" title="Open Access"
                                      params="${params}"/>
                <semui:sortableColumn property="paymentType" title="Payment Type"
                                      params="${params}"/>
                <semui:sortableColumn property="scope" title="Scope"
                                      params="${params}"/>
                <semui:sortableColumn property="freeTrial" title="Free Trial"
                                      params="${params}"/>
                <semui:sortableColumn property="freeTrialPhase" title="Free Trial Phase"
                                      params="${params}"/>

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
                            <semui:xEditable owner="${row_obj}" field="name" required="true"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${row_obj}" field="status"
                                                    config="${RCConstants.COMPONENT_STATUS}"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${row_obj}" field="breakable"
                                                    config="${RCConstants.PACKAGE_BREAKABLE}"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${row_obj}" field="contentType"
                                                    config="${RCConstants.PACKAGE_CONTENT_TYPE}"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${row_obj}" field="file"
                                                    config="${RCConstants.PACKAGE_FILE}"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${row_obj}" field="openAccess"
                                                    config="${RCConstants.PACKAGE_OPEN_ACCESS}"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${row_obj}" field="paymentType"
                                                    config="${RCConstants.PACKAGE_PAYMENT_TYPE}"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${row_obj}" field="scope"
                                                    config="${RCConstants.PACKAGE_SCOPE}"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${row_obj}" field="freeTrial"
                                                    config="${RCConstants.YN}"/>
                        </td>
                        <td>
                            <semui:xEditable owner="${row_obj}" field="freeTrialPhase"/>
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
<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory;" %>
<g:set var="counter" value="${offset}"/>

<g:form method="post" class="ui form" controller="${controllerName}" action="${actionName}"
        params="[activeTab: 'sources']">

    <g:render template="/group/management/managementForm" model="[batchForm: packageSourceInfosBatchForm]"/>

    <div style="overflow-x: auto">
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th><g:if test="${editable}"><input id="select-all" type="checkbox" name="chkall" /> <div id="numberOfChecked">Select (0)</div></g:if></th>
                <th>#</th>
                <semui:sortableColumn property="name" title="Package Name"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.name" title="Source Name"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.status" title="Source Status"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.url" title="URL"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.ftpServerUrl" title="FTP Server Url"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.ftpDirectory" title="FTP Directory"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.ftpFileName" title="FTP File Name"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.ftpUsername" title="FTP Username"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.ftpPassword" title="FTP Password"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.frequency" title="Frequency"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.defaultSupplyMethod" title="Default Supply Method"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.defaultDataFormat" title="Default Data Format"
                                      params="${params}"/>
                <semui:sortableColumn property="kbartSource.automaticUpdates" title="Automatic Updates"
                                      params="${params}"/>
                <th>Source</th>
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
                            ${row_obj.name}
                        </td>
                        <g:if test="${row_obj.kbartSource}">
                            <td>
                                <semui:xEditable owner="${row_obj.kbartSource}" field="name" required="true"/>
                            </td>
                            <td>
                                <semui:xEditableRefData owner="${row_obj.kbartSource}" field="status"
                                                        config="${RCConstants.COMPONENT_STATUS}"
                                                        overwriteEditable="false"/>
                            </td>
                            <td>
                                <semui:xEditable owner="${row_obj.kbartSource}" field="url" validation="url"
                                                 outGoingLink="true"/>
                            </td>
                            <td>
                                <semui:xEditable owner="${row_obj.kbartSource}" field="ftpServerUrl"/>
                            </td>
                            <td>
                                <semui:xEditable owner="${row_obj.kbartSource}" field="ftpDirectory"/>
                            </td>
                            <td>
                                <semui:xEditable owner="${row_obj.kbartSource}" field="ftpFileName"/>
                            </td>
                            <td>
                                <semui:xEditable owner="${row_obj.kbartSource}" field="ftpUsername"/>
                            </td>
                            <td>
                                <semui:xEditable owner="${row_obj.kbartSource}" field="ftpPassword"/>
                            </td>
                            <td>
                                <semui:xEditableRefData owner="${row_obj.kbartSource}" field="frequency"
                                                        config="${RCConstants.SOURCE_FREQUENCY}"/>
                            </td>
                            <td>
                                <semui:xEditableRefData owner="${row_obj.kbartSource}" field="defaultSupplyMethod"
                                                        config="${RCConstants.SOURCE_DATA_SUPPLY_METHOD}"/>
                            </td>
                            <td>
                                <semui:xEditableRefData owner="${row_obj.kbartSource}" field="defaultDataFormat"
                                                        config="${RCConstants.SOURCE_DATA_FORMAT}"/>
                            </td>
                            <td>
                                <semui:xEditableBoolean owner="${row_obj.kbartSource}" field="automaticUpdates"/>
                            </td>
                            <td>
                                <g:link class="ui icon button" controller="resource" action="show"
                                        id="${row_obj.kbartSource.getOID()}">
                                    <i class="edit icon"></i>
                                </g:link>
                            </td>
                        </g:if>
                        <g:else>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </g:else>
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
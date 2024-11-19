<%@ page import="wekb.helper.RDStore; wekb.RefdataValue;" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Change History for Package: ${pkg.name}</title>
</head>

<body>
<h1 class="ui header">Change History for Package: ${pkg.name}</h1>

<g:if test="${(qbetemplate.message != null)}">
    <semui:message message="${qbetemplate.message}"/>
</g:if>

<g:if test="${recset && !init}">
    <div class="ui header">
        <h1>Showing results ${offset.toInteger() + 1} to ${lasthit.toInteger() as int} of
            ${reccount.toInteger() as int}</h1>
    </div>

    <g:set var="counter" value="${offset}"/>

    <g:render template="/search/pagination" model="${params}"/>
    <div style="overflow-x: auto">
    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <semui:sortableColumn property="status.value" title="Status"
                                  params="${params}"/>
            <semui:sortableColumn property="automaticUpdate" title="Automatic Update"
                                  params="${params}"/>
            <semui:sortableColumn property="startTime" title="Start Time"
                                  params="${params}"/>
            <semui:sortableColumn property="enTime" title="End Time"
                                  params="${params}"/>
            <semui:sortableColumn property="onlyRowsWithLastChanged" title="Only Last Changed Update"
                                  params="${params}"/>
            <semui:sortableColumn property="countPreviouslyTippsInWekb" title="Titles in we:kb before update"
                                  params="${params}"/>
            <semui:sortableColumn property="countNowTippsInWekb" title="Titles in we:kb after update"
                                  params="${params}"/>
            <semui:sortableColumn property="countKbartRows" title="Rows in KBART-File"
                                  params="${params}"/>
            <semui:sortableColumn property="countProcessedKbartRows" title="Processed KBART Rows"
                                  params="${params}"/>
            <semui:sortableColumn property="countChangedTipps" title="Changed Titles"
                                  params="${params}"/>
            <semui:sortableColumn property="countRemovedTipps" title="Removed Titles"
                                  params="${params}"/>
            <semui:sortableColumn property="countNewTipps" title="New Titles"
                                  params="${params}"/>
            <semui:sortableColumn property="countInValidTipps" title="Invalid Titles"
                                  params="${params}"/>
            <semui:sortableColumn property="kbartHasWekbFields" title="KBART Wekb Fields"
                                  params="${params}"/>
            <th>Last Changed in KBART</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${new_recset}" var="r">
            <g:if test="${r != null}">
                <g:set var="row_obj" value="${r.obj}"/>
                <tr>
                    <td>${++counter}</td>
                    <td>
                        <g:link controller="resource" action="show" id="${row_obj.getOID()}">
                            ${row_obj.status.value}
                        </g:link>
                    </td>
                    <td>
                        <g:if test="${row_obj.automaticUpdate}">
                            <i class="check green circle icon"
                               title="${message(code: 'default.boolean.true')}"></i>
                        </g:if>
                        <g:else>
                            <i class="times red circle icon"
                               title="${message(code: 'default.boolean.false')}"></i>
                        </g:else>
                    </td>
                    <td><g:if test="${row_obj.startTime}">
                        <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                      date="${row_obj.startTime}"/>
                    </g:if></td>
                    <td><g:if test="${row_obj.endTime}">
                        <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                      date="${row_obj.endTime}"/>
                    </g:if></td>
                    <td>
                        <g:if test="${row_obj.onlyRowsWithLastChanged}">
                            <i class="check green circle icon"
                               title="${message(code: 'default.boolean.true')}"></i>
                        </g:if>
                        <g:else>
                            <i class="times red circle icon"
                               title="${message(code: 'default.boolean.false')}"></i>
                        </g:else>
                    </td>
                    <td>
                        <g:formatNumber number="${row_obj.countPreviouslyTippsInWekb}" type="number"/>
                    </td>
                    <td>
                        <g:formatNumber number="${row_obj.countNowTippsInWekb}" type="number"/>
                    </td>
                    <td>
                        <g:formatNumber number="${row_obj.countKbartRows}" type="number"/>
                    </td>
                    <td>
                        <g:formatNumber number="${row_obj.countProcessedKbartRows}" type="number"/>
                    </td>
                    <td>
                        <g:link controller="search" action="componentSearch" id=""
                                params="[qbe: 'g:updateTippInfos', qp_aup_id: row_obj.id, qp_type: RefdataValue.class.name+':'+RDStore.UPDATE_TYPE_CHANGED_TITLE.id]">
                            <g:formatNumber number="${row_obj.countChangedTipps}" type="number"/>
                        </g:link>
                    </td>
                    <td>
                        <g:link controller="search" action="componentSearch" id=""
                                params="[qbe: 'g:updateTippInfos', qp_aup_id: row_obj.id, qp_type: RefdataValue.class.name+':'+RDStore.UPDATE_TYPE_REMOVED_TITLE.id]">
                            <g:formatNumber number="${row_obj.countRemovedTipps}" type="number"/>
                        </g:link>
                    </td>
                    <td>
                        <g:link controller="search" action="componentSearch" id=""
                                params="[qbe: 'g:updateTippInfos', qp_aup_id: row_obj.id, qp_type: RefdataValue.class.name+':'+RDStore.UPDATE_TYPE_NEW_TITLE.id]">
                            <g:formatNumber number="${row_obj.countNewTipps}" type="number"/>
                        </g:link>
                    </td>
                    <td>!!!!!!!!!!!!!!!!!!!!!!
                        <g:link controller="search" action="componentSearch" id=""
                                params="[qbe: 'g:updateTippInfos', qp_aup_id: row_obj.id, qp_type: RefdataValue.class.name+':'+RDStore.UPDATE_TYPE_FAILED_TITLE.id]">
                            <g:formatNumber number="${row_obj.countInValidTipps}" type="number"/>
                        </g:link>
                    </td>
                    <td>
                        <g:if test="${row_obj.kbartHasWekbFields}">
                            <i class="check green circle icon"
                               title="${message(code: 'default.boolean.true')}"></i>
                        </g:if>
                        <g:else>
                            <i class="times red circle icon"
                               title="${message(code: 'default.boolean.false')}"></i>
                        </g:else>
                    </td>
                    <td><g:if test="${row_obj.lastChangedInKbart}">
                        <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                      date="${row_obj.lastChangedInKbart}"/>
                    </g:if></td>
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
    <g:render template="/search/pagination" model="${params}"/>
</g:if>
<g:elseif test="${!init && !params.inline}">
    <g:render template="/search/qbeempty"/>
</g:elseif>
<g:else>
    <semui:message>
        <p>No results.</p>
    </semui:message>
</g:else>

</body>
</html>
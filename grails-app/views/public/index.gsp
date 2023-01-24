<%@ page import="wekb.Platform; wekb.Org" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>we:kb : Packages</title>
</head>

<body>

<wekb:serviceInjection/>

<semui:flashMessage data="${flash}"/>

<g:render template="number-chart-hero"/>

%{--<div class="ui segment">
    <h1 class="ui header">Filter</h1>
    <g:form controller="public" class="ui form" action="index" method="post" params="${params}">

        <div class="fields">

            <div class="four wide field">
                <label for="q">Search for packages...</label>
                <input type="text" placeholder="Find package like..." value="${params.q}"
                       name="q">
            </div>

            <g:if test="${facets}">
                <g:each in="${facets.sort { it.key }}" var="facet">
                    <div class="six wide field">
                        <g:if test="${facet.key != 'type'}">
                            <label for="${facet.key}"><g:message code="facet.so.${facet.key}"
                                                                 default="${facet.key}"/></label>
                            <select name="${facet.key}" class="ui search selection fluid multiple dropdown"
                                    multiple="multiple" placeholder="">
                                <g:each in="${facet.value?.sort { it.display.toLowerCase() }}" var="v">
                                    <g:if test="${params.list(facet.key).contains(v.term)}">
                                        <option value="${v.term}"
                                                selected="selected">${v.display} (${v.count})</option>
                                    </g:if>
                                    <g:else>
                                        <option value="${v.term}">${v.display} (${v.count})</option>
                                    </g:else>
                                </g:each>
                            </select>
                        </g:if>
                    </div>
                </g:each>
            </g:if>

        </div>

        <div class="ui right floated buttons">
            <g:link class="ui button" controller="public">Reset</g:link>
            <button class="ui button black" type="submit" value="yes" name="search">Search</button>
        </div>

        <br>
        <br>
    </g:form>
</div>--}%

<g:render template="/search/qbeform"
          model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>

<div class="container">

    %{--<div class="ui header">
        <h1>Results ${resultsTotal}</h1>
    </div>

    <div class="ui form">
        <g:form controller="public" action="${actionName}"
                method="get"
                params="${params}">
            <div class="ui right floated header inline field">
                <label>Results on Page</label>
                <g:select name="newMax" from="[10, 25, 50, 100]" value="${params.max}"
                          onChange="this.form.submit()"/>
            </div>
        </g:form>
    </div>

    <br>
    <br>

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <semui:sortableColumn property="sortname" title="Package Name"/>
            <semui:sortableColumn property="cpname" title="Provider"/>
            <semui:sortableColumn property="nominalPlatformName" title="Platform"/>
            <th>Curatory Groups</th>
            <semui:sortableColumn property="contentType" title="Content Type"/>
            <semui:sortableColumn property="titleCount" title="Title Count"/>
            <semui:sortableColumn property="lastUpdatedDisplay" title="Last Updated"/>
        </tr>
        </thead>
        <tbody>
        <g:each in="${hits}" var="hit" status="i">
            <tr>
                <td>
                    ${(params.int('offset') ?: 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show"
                            id="${hit.id}">${hit.getSourceAsMap().name}</g:link>

                </td>
                <td>
                    <g:if test="${hit.getSourceAsMap().providerUuid}">
                        <g:link controller="resource" action="show"
                                id="${hit.getSourceAsMap().providerUuid}">${Org.findByUuid(hit.getSourceAsMap().providerUuid).name}</g:link>
                    </g:if>

                </td>
                <td>
                    <g:if test="${hit.getSourceAsMap().nominalPlatformUuid}">
                        <g:link controller="resource" action="show"
                                id="${hit.getSourceAsMap().nominalPlatformUuid}">${Platform.findByUuid(hit.getSourceAsMap().nominalPlatformUuid).name}</g:link>
                    </g:if>

                </td>
                <td>
                    <g:if test="${hit.getSourceAsMap().curatoryGroups?.size() > 0}">
                        <g:each in="${hit.getSourceAsMap().curatoryGroups}" var="cg" status="c">
                            <g:if test="${c > 0}"><br></g:if>
                            ${cg.name}
                            <g:if test="${cg.type}">
                                (${cg.type})
                            </g:if>
                        </g:each>
                    </g:if>
                    <g:else>
                        <div>No Curators</div>
                    </g:else>
                </td>
                <td>${hit.getSourceAsMap().contentType}</td>
                <td>${hit.getSourceAsMap().titleCount}</td>
                <td>
                    <g:if test="${hit.getSourceAsMap().lastUpdatedDisplay}">
                        <g:formatDate format="${message(code: 'default.date.format')}"
                                      date="${dateFormatService.parseDate(hit.getSourceAsMap().lastUpdatedDisplay)}"/>
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>


    <g:if test="${resultsTotal ?: 0 > 0}">
        <semui:paginate controller="public" action="index" params="${params}"
                        max="${max}" total="${resultsTotal}"/>
    </g:if>--}%

    <g:if test="${(qbetemplate.message != null)}">
        <semui:message message="${qbetemplate.message}"/>
    </g:if>

    <g:if test="${recset && !init}">
        <g:render template="/search/qberesult"
                  model="${[qbeConfig: qbetemplate.qbeConfig, rows: new_recset, offset: offset, jumpToPage: 'jumpToPage', det: det, page: page_current, page_max: page_total, baseClass: qbetemplate.baseclass]}"/>
    </g:if>
    <g:elseif test="${!init && !params.inline}">
        <g:render template="/search/qbeempty"/>
    </g:elseif>
    <g:else>
        <semui:message>
            <p>No results.</p>
        </semui:message>
    </g:else>

</div>
</body>
</html>

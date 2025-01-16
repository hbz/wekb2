<%@ page import="wekb.Platform; wekb.Org" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb</title>
</head>

<body>

<wekb:serviceInjection/>

<semui:flashMessage data="${flash}"/>

<g:render template="number-chart-hero"/>

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
--}%

    <g:if test="${(qbetemplate.message != null)}">
        <semui:message message="${qbetemplate.message}"/>
    </g:if>

    <g:if test="${recset && !init}">
        <g:render template="/search/qberesult"
                  model="${[qbeConfig: qbetemplate.qbeConfig, rows: new_recset, offset: offset, det: det, page: page_current, page_max: page_total, baseClass: qbetemplate.baseclass]}"/>
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

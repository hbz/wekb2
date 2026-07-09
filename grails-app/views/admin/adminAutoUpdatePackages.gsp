<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb | wekb - Auto Update Packages</title>
</head>

<body>

<g:set var="cleaned_params" value="${params.findAll { it.value && it.value != "" }}"/>

<h1 class="ui header">Auto Update Packages</h1>

<g:if test="${(qbetemplate.message != null)}">
    <semui:message message="${qbetemplate.message}"/>
</g:if>

<semui:flashMessage data="${flash}"/>

<g:link controller="admin" action="processPackageUpdate" params="${params}"
        class="ui left floated primary button">Trigger KBART Update (all Titles) for ${reccount.toInteger()} Packages</g:link>

<br>
<br>

<g:render template="/search/qbeform"
          model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>

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

</body>
</html>
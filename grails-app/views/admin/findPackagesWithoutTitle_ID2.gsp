<%@ page import="wekb.IdentifierNamespace" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb | wekb - Packages without Identifier Namespace by Titles</title>
</head>

<body>

<g:set var="cleaned_params" value="${params.findAll { it.value && it.value != "" }}"/>

<h1 class="ui header">Packages without Identifier Namespace by Titles ${params.qp_namespace_value ? wekb.IdentifierNamespace.findByValueAndTargetType(params.qp_namespace_value, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP).name : ''}</h1>

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
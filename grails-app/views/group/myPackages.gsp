<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : My Packages of Curatory Groups (${groups.name.join(',')})</title>
</head>

<body>
<h1 class="ui header">My Packages of Curatory Groups (${groups.name.join(',')})</h1>

    <g:link controller="group" action="myPackagesNeedsAutoUpdates"
        class="ui right floated black button">My Packages due to automatic update</g:link>
    <br>
    <br>

    <g:link controller="group" action="exportMyPackages" id="${params.id}"
            class="ui right floated black button">Export my packages</g:link>

    <br>
    <br>

    <g:if test="${(qbetemplate.message != null)}">
        <semui:message message="${qbetemplate.message}"/>
    </g:if>

    <g:render template="/search/qbeform"
              model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>

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

    <br>

    <div class="ui right floated buttons">
        <g:link controller="create" action="packageBatch" class="ui black button right aligned">Upload Packages</g:link>
    </div>
    <br>
    <br>



</body>
</html>

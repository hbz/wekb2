<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Tipp Duplicates</title>
</head>

<body>

<wekb:serviceInjection/>


<semui:flashMessage data="${flash}"/>


<h1 class="ui header">Tipp without Title_ID for

<g:link controller="resource" action="show" id="${pkg.getOID()}">
    ${pkg.name}
</g:link>
</h1>

<div class="container">

        <h3>Tipps without Title ID (${totalCountByWithoutTitleID})</h3>
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th>#</th>
                <th>Title</th>
                <th>Identifiers</th>
                <th>Status</th>
                <th>Platform</th>
                <th>Publication Type</th>
                <th>Medium</th>
                <th>Url</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${tippsByWithoutTitleID}" var="t" status="i">
                <tr>
                    <td>
                        ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                    </td>
                    <td>
                        <g:link controller="resource" action="show" id="${t.getOID()}">
                            ${t.name} <strong>(${t.status.value})</strong>
                        </g:link>
                    </td>
                    <td>
                        <ul>
                            <g:each in="${t.ids.sort { it.namespace.value }}" var="id">
                                <li><strong>${id.namespace.value}</strong>:<g:link controller="resource"
                                                                                   action="show"
                                                                                   id="${id.getOID()}">${id.value}</g:link>
                                </li>
                            </g:each>
                        </ul>
                    </td>
                    <td>
                        ${t.status.value}
                    </td>
                    <td>
                        <g:link controller="resource" action="show"
                                id="${t.hostPlatform.getOID()}">
                            ${t.hostPlatform?.name}
                        </g:link>
                    </td>
                    <td>${t.publicationType?.value}</td>
                    <td>${t.medium?.value}</td>
                    <td>
                        ${t.url}
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

        <semui:paginate controller="${controllerName}" action="${actionName}"
                        params="[id: params.id, papaginateByTitleID: true]"
                        max="${maxByWithoutTitleID}" offset="${offsetByWithoutTitleID}" total="${totalCountByWithoutTitleID}"/>

</div>

</body>
</html>

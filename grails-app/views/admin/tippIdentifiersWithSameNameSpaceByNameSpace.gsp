<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>we:kb : Title Identifiers with same Identifier Namespace </title>
</head>

<body>

<wekb:serviceInjection/>

<h1 class="ui header">Title Identifiers with same Identifier Namespace: ${namespace} (${count})</h1>

<table class="ui selectable striped sortable celled table">
    <thead>
    <tr>
        <th>#</th>
        <th>Title</th>
        <th>Identifiers</th>
        <th>Package</th>
        <th>Platform</th>
        <th>Publication Type</th>
        <th>Medium</th>
        <th>Url</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${tipps}" var="t" status="i">
        <tr>
            <td>
                ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
            </td>
            <td>
                <g:link controller="resource" action="show" id="${t.uuid}">
                    ${t.name} <b>(${t.status.value})</b>
                </g:link>
            </td>
            <td>
                <ul>
                    <g:each in="${t.ids.sort { it.namespace.value }}" var="id">
                        <li><strong>${id.namespace.value}</strong>:<g:link controller="resource" action="show"
                                                                           id="${id.class.name}:${id.id}">${id.value}</g:link>
                        </li>
                    </g:each>
                </ul>
            </td>
            <td>
                <g:link controller="resource" action="show"
                        id="${t.pkg?.uuid}">
                    ${t.pkg?.name}
                </g:link>
            </td>
            <td>
                <g:link controller="resource" action="show"
                        id="${t.hostPlatform.uuid}">
                    ${t.hostPlatform.name}
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

</body>
</html>

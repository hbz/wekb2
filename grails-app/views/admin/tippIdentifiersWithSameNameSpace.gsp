<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>we:kb : Title Identifiers with same Identifier Namespace</title>
</head>

<body>

<wekb:serviceInjection/>

<h1 class="ui header">Title Identifiers with same Identifier Namespace (${total})</h1>

<table class="ui selectable striped sortable celled table">
    <thead>
    <tr>
        <th>#</th>
        <th>Name</th>
        <th>Family</th>
        <th>Count</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${namespaces}" var="namespaceMap" status="i">
        <tr>
            <td>
                ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
            </td>
            <td>
                ${namespaceMap.name}
            </td>
            <td>
                ${namespaceMap.family}
            </td>
            <td>
                <g:link controller="admin" action="tippIdentifiersWithSameNameSpaceByNameSpace"
                        id="${namespaceMap.namespaceID}">
                    ${namespaceMap.count}
                </g:link>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

</body>
</html>

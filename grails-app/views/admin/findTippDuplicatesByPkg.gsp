<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>we:kb : Tipp Duplicates</title>
</head>

<body>

<wekb:serviceInjection/>


<semui:flashMessage data="${flash}"/>


<h1 class="ui header">Tipp Duplicates</h1>

<div class="container">

    <ul id="tabs" class="nav nav-tabs">
        <li role="presentation" class="${!params.papaginateByUrl && !params.papaginateByTitleID ? 'active' : ''}"><a
                href="#byName" data-toggle="tab">Tipps Duplicates By Name <span
                    class="badge badge-warning">${totalCountByName}</span></a></li>
        <li role="presentation"
            class="${params.papaginateByUrl && !params.papaginateByTitleID && !params.papaginateByName ? 'active' : ''}"><a
                href="#byUrl" data-toggle="tab">Tipps Duplicates By Url <span
                    class="badge badge-warning">${totalCountByUrl}</span></a></li>
        <li role="presentation"
            class="${!params.papaginateByUrl && params.papaginateByTitleID && !params.papaginateByName ? 'active' : ''}"><a
                href="#byTitleID" data-toggle="tab">Tipps Duplicates By Title ID <span
                    class="badge badge-warning">${totalCountByTitleID}</span></a></li>
    </ul>

    <div class="tab-content">

        <div class="tab-pane ${!params.papaginateByUrl && !params.papaginateByTitleID ? 'active' : ''}" id="byName">

            <h3>Tipps Duplicates By Name (${totalCountByName})</h3>

            <table class="ui selectable striped sortable celled table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Title</th>
                    <th>Identifiers</th>
                    <th>Platform</th>
                    <th>Publication Type</th>
                    <th>Medium</th>
                    <th>Url</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${tippsDuplicatesByName}" var="t" status="i">
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
                                    <li><strong>${id.namespace.value}</strong>:<g:link controller="resource"
                                                                                       action="show"
                                                                                       id="${id.class.name}:${id.id}">${id.value}</g:link>
                                    </li>
                                </g:each>
                            </ul>
                        </td>
                        <td>
                            <g:link controller="resource" action="show"
                                    id="${t.hostPlatform?.uuid}">
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
                            params="[id: params.id, papaginateByName: true]"
                            max="${maxByName}" offset="${offsetByName}" total="${totalCountByName}"/>

        </div>

        <div class="tab-pane ${params.papaginateByUrl && !params.papaginateByTitleID && !params.papaginateByName ? 'active' : ''}"
             id="byUrl">
            <h3>Tipps Duplicates By Url (${totalCountByUrl})</h3>

            <div class="ui right floated buttons">
                <g:link controller="admin" action="removeTippDuplicatesByUrl" id="${params.id}"
                        class="ui button black">Remove Tipps Duplicates By Url</g:link>
            </div>

            <table class="ui selectable striped sortable celled table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Title</th>
                    <th>Identifiers</th>
                    <th>Platform</th>
                    <th>Publication Type</th>
                    <th>Medium</th>
                    <th>Url</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${tippsDuplicatesByUrl}" var="t" status="i">
                    <tr>
                        <td>
                            ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                        </td>
                        <td>
                            <g:link controller="resource" action="show" id="${t.uuid}">
                                ${t.name}
                            </g:link> <b>(${t.status.value})</b>
                        </td>
                        <td>
                            <ul>
                                <g:each in="${t.ids.sort { it.namespace.value }}" var="id">
                                    <li><strong>${id.namespace.value}</strong>:<g:link controller="resource"
                                                                                       action="show"
                                                                                       id="${id.class.name}:${id.id}">${id.value}</g:link>
                                    </li>
                                </g:each>
                            </ul>
                        </td>
                        <td>
                            <g:link controller="resource" action="show"
                                    id="${t.hostPlatform?.uuid}">
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
                            params="[id: params.id, papaginateByUrl: true]"
                            max="${maxByUrl}" offset="${offsetByUrl}" total="${totalCountByUrl}"/>
        </div>
    </div>

    <div class="tab-pane ${!params.papaginateByUrl && params.papaginateByTitleID && !params.papaginateByName ? 'active' : ''}"
         id="byTitleID">

        <h3>Tipps Duplicates By Title ID (${totalCountByTitleID})</h3>
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th>#</th>
                <th>Title</th>
                <th>Identifiers</th>
                <th>Platform</th>
                <th>Publication Type</th>
                <th>Medium</th>
                <th>Url</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${tippsDuplicatesByTitleID}" var="t" status="i">
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
                                <li><strong>${id.namespace.value}</strong>:<g:link controller="resource"
                                                                                   action="show"
                                                                                   id="${id.class.name}:${id.id}">${id.value}</g:link>
                                </li>
                            </g:each>
                        </ul>
                    </td>
                    <td>
                        <g:link controller="resource" action="show"
                                id="${t.hostPlatform?.uuid}">
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
                        max="${maxByTitleID}" offset="${offsetByTitleID}" total="${totalCountByTitleID}"/>

    </div>
</div>

</body>
</html>

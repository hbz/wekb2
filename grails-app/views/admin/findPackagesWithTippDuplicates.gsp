<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Packages with Tipp Duplicates</title>
</head>

<body>

<wekb:serviceInjection/>

<h1 class="ui header">Packages with Tipp Duplicates (${totalCount})</h1>


<div class="container">

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Provider</th>
            <th>Platform</th>
            <th>Curatory Groups</th>
            <th>Auto Update</th>
            <th>Titles</th>
            <semui:sortableColumn property="tippDuplicatesByNameCount" title="Tipp Duplicates By Name"/>
            <semui:sortableColumn property="tippDuplicatesByUrlCount" title="Tipp Duplicates By Url"/>
            <semui:sortableColumn property="tippDuplicatesByTitleIDCount" title="Tipp Duplicates By Title ID"/>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgs}" var="pkgMap" status="i">
            <g:set var="pkg" value="${pkgMap.pkg}"/>
            <tr class="${pkgMap.tippDuplicatesByTitleIDCount > 0 ? 'info' : (pkgMap.tippDuplicatesByUrlCount > 0 ? 'success' : '')}">
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.getOID()}">
                        ${pkg.name}
                    </g:link>
                </td>
                <td>
                    ${pkg.provider}
                </td>
                <td>
                    ${pkg.nominalPlatform}
                </td>
                <td>
                    <g:each in="${pkg.curatoryGroups}" var="curatoryGroupPackage">
                        ${curatoryGroupPackage.curatoryGroup.name}
                    </g:each>
                </td>
                <td>
                    <g:if test="${pkg.kbartSource?.automaticUpdates}">
                        <i class="check green circle icon"
                           title="${message(code: 'default.boolean.true')}"></i>
                    </g:if>
                    <g:else>
                        <<i class="times red circle icon"
                           title="${message(code: 'default.boolean.false')}"></i>
                    </g:else>
                </td>
                <td>
                    <g:set var="allTipps1" value="${pkg.getTippCountWithoutRemoved()}"/>
                    <g:set var="allTipps2" value="${pkg.getTippCount()}"/>

                    <g:if test="${allTipps1 != allTipps2}">
                    ${allTipps1} / ${allTipps2}
                    </g:if>
                    <g:else>
                        ${allTipps2}
                    </g:else>
                </td>
                <td>
                    <g:link controller="admin" action="findTippDuplicatesByPkg" id="${pkg.uuid}" target="_blank"
                            params="[papaginateByName: true, max: 100, offset: 0]">
                        ${pkgMap.tippDuplicatesByNameCount}
                    </g:link>
                </td>
                <td>
                    <g:link controller="admin" action="findTippDuplicatesByPkg" id="${pkg.uuid}" target="_blank"
                            params="[papaginateByUrl: true, max: 100, offset: 0]">
                        ${pkgMap.tippDuplicatesByUrlCount}
                    </g:link>
                </td>
                <td>
                    <g:link controller="admin" action="findTippDuplicatesByPkg" id="${pkg.uuid}" target="_blank"
                            params="[papaginateByTitleID: true, max: 100, offset: 0]">
                        ${pkgMap.tippDuplicatesByTitleIDCount}
                    </g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

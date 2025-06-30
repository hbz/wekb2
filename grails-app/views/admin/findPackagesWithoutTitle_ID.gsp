<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Packages with Tipp without Title_ID</title>
</head>

<body>

<wekb:serviceInjection/>

<h1 class="ui header">Packages with Tipp without Title_ID (${totalCount})</h1>

<g:set var="allCount" value="${0}"/>

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
            <semui:sortableColumn property="tippsWithoutTitleIDCount" title="Tipp without Title ID"/>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgs}" var="pkgMap" status="i">
            <g:set var="pkg" value="${pkgMap.pkg}"/>
            <tr>
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
                        <i class="times red circle icon"
                           title="${message(code: 'default.boolean.false')}"></i>
                    </g:else>
                </td>
                <td>
                    <g:set var="allTipps1" value="${pkg.getTippCountWithoutRemoved()}"/>
                    <g:set var="allTipps2" value="${pkg.getTippCount()}"/>

                    <g:if test="${allTipps1 != allTipps2}">
                    ${allTipps1} / ${allTipps2}
                    </g:if>
                </td>
                <td>
                    <g:set var="tippCount" value="${pkgMap.tippsWithoutTitleIDCount}"/>
                    <g:link controller="admin" action="findTippWithoutTitleIDByPkg" id="${pkg.uuid}" target="_blank"
                            params="[max: 100, offset: 0]">
                        ${tippCount}
                    </g:link>

                    <g:set var="allCount" value="${allCount+tippCount}"/>
                </td>
            </tr>
        </g:each>
        </tbody>
        <tfoot>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td>${allCount}</td>
        </tr>
        </tfoot>
    </table>

</div>

</body>
</html>

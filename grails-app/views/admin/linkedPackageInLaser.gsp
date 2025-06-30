<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Packages linked in Laser</title>
</head>

<body>

<wekb:serviceInjection/>

<h1 class="ui header">Packages linked in Laser (${totalCount})</h1>


<div class="container">

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Status</th>
            <th>Provider</th>
            <th>Platform</th>
            <th>Curatory Groups</th>
            <th>Auto Update</th>
            <th>Titles</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgs}" var="pkg" status="i">
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
                    ${pkg.status.value}
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
                    <g:else>
                        ${allTipps2}
                    </g:else>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

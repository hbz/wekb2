<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb | wekb -  Packages linked in Laser</title>
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
            <semui:sortableColumn property="curatoryGroups" title="Curatory Groups"/>
            <th>Auto Update</th>
            <th>Package linked in Laser</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgs}" var="pkg" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.id}">
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
                    <g:link controller="admin" action="linkedSubsInLaser" id="${pkg.id}">${pkg.linkedPackageCount}</g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

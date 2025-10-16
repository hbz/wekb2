<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Permanent Titles in Laser [Status: ${status}]</title>
</head>

<body>

<wekb:serviceInjection/>

<g:set var="laserService" bean="${wekb.LaserService}"/>

<h1 class="ui header">Packages linked with Permanent Titles in Laser (${totalCount}) [Status: ${status}]</h1>


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
            <th>Linked Count</th>
            <th>Wekb Titles</th>
            <th>Laser Titles</th>
            <th>Permanent Titles</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgs}" var="pkg" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkgWekb.getOID()}">
                        ${pkgWekb.name}
                    </g:link>
                </td>
                <td>
                    ${pkgWekb.status.value}
                </td>
                <td>
                    ${pkgWekb.provider}
                </td>
                <td>
                    ${pkgWekb.nominalPlatform}
                </td>
                <td>
                    <g:each in="${pkgWekb.curatoryGroups}" var="curatoryGroupPackage">
                        ${curatoryGroupPackage.curatoryGroup.name}
                    </g:each>
                </td>
                <td>
                    <g:if test="${pkgWekb.kbartSource?.automaticUpdates}">
                        <i class="check green circle icon"
                           title="${message(code: 'default.boolean.true')}"></i>
                    </g:if>
                    <g:else>
                        <i class="times red circle icon"
                           title="${message(code: 'default.boolean.false')}"></i>
                    </g:else>
                </td>
                <td>
                    <g:link action="linkedSubsInLaser" controller="admin" id="${pkgWekb.id}">${pkgLaser.packageLinkedInLaserCount}</g:link>
                </td>
                <td>
                    ${pkgWekb.getTippCountWithStatus(status)}

                </td>
                <td>
                    ${pkgLaser.tippCount}
                </td>
                <td>
                    ${pkgLaser.ptCount}
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Permanent Titles by Providers in Laser</title>
</head>

<body>

<wekb:serviceInjection/>

<g:set var="laserService" bean="${wekb.LaserService}"/>

<h1 class="ui header">Permanent Titles by Providers in Laser (${totalCount}) </h1>


<div class="container">


    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <th>Provider</th>
            <th>Tipp as PT Current</th>
            <th>PT Current</th>
            <th>Tipp as PT Retired</th>
            <th>PT Retired</th>
            <th>Tipp as PT Expected</th>
            <th>PT Expected</th>
            <th>Tipp as PT Deleted</th>
            <th>PT Deleted</th>
            <th>Tipp as PT Removed</th>
            <th>PT Removed</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${permanentTitlesInLaserByProviders}" var="pt" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pt.wekbUUID}">${pt.name}</g:link>
                </td>
                <td>
                    ${pt.permanent_Tipp_Current}
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Current', providerUuid: pt.wekbUUID]">${pt.permanent_Current}</g:link>
                </td>
                <td>
                    ${pt.permanent_Tipp_Retired}
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Retired', providerUuid: pt.wekbUUID]">${pt.permanent_Retired}</g:link>
                </td>
                <td>
                    ${pt.permanent_Tipp_Expected}
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Expected', providerUuid: pt.wekbUUID]">${pt.permanent_Expected}</g:link>
                </td>
                <td>
                    ${pt.permanent_Tipp_Deleted}
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Deleted', providerUuid: pt.wekbUUID]"> ${pt.permanent_Deleted}</g:link>
                </td>
                <td>
                    ${pt.permanent_Tipp_Removed}
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Removed', providerUuid: pt.wekbUUID]">${pt.permanent_Removed}</g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

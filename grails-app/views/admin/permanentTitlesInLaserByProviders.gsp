<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb | wekb -  Permanent Titles by Providers in Laser</title>
</head>

<body>

<wekb:serviceInjection/>

<g:set var="laserService" bean="${wekb.LaserService}"/>

<h1 class="ui header">Permanent Titles by Providers in Laser (${totalCount}) </h1>


<div class="container">

    <g:set var="sumPtCurrentCount" value="${0}"/>
    <g:set var="sumPtRetiredCount" value="${0}"/>
    <g:set var="sumPtExpectedCount" value="${0}"/>
    <g:set var="sumPtDeletedCount" value="${0}"/>
    <g:set var="sumPtRemovedCount" value="${0}"/>
    <g:set var="sumCurrentCount" value="${0}"/>
    <g:set var="sumRetiredCount" value="${0}"/>
    <g:set var="sumExpectedCount" value="${0}"/>
    <g:set var="sumDeletedCount" value="${0}"/>
    <g:set var="sumRemovedCount" value="${0}"/>
    <g:set var="sumPackagesCount" value="${0}"/>
    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <th>Provider</th>
            <th>Packages with PT</th>
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
                    <g:link controller="admin" action="linkedPackageWithPermanentTitlesInLaser" params="${[providerUuid: pt.wekbUUID]}">${pt.linkedPackages}</g:link>
                    <g:set var="sumPackagesCount" value="${sumPackagesCount+pt.linkedPackages}"/>
                </td>
                <td>
                    ${pt.permanent_Tipp_Current}
                    <g:set var="sumCurrentCount" value="${sumCurrentCount+pt.permanent_Tipp_Current}"/>
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Current', providerUuid: pt.wekbUUID]">${pt.permanent_Current}</g:link>
                    <g:set var="sumPtCurrentCount" value="${sumPtCurrentCount+pt.permanent_Current}"/>
                </td>
                <td>
                    ${pt.permanent_Tipp_Retired}
                    <g:set var="sumRetiredCount" value="${sumRetiredCount+pt.permanent_Tipp_Retired}"/>
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Retired', providerUuid: pt.wekbUUID]">${pt.permanent_Retired}</g:link>
                    <g:set var="sumPtRetiredCount" value="${sumPtRetiredCount+pt.permanent_Retired}"/>
                </td>
                <td>
                    ${pt.permanent_Tipp_Expected}
                    <g:set var="sumExpectedCount" value="${sumExpectedCount+pt.permanent_Tipp_Expected}"/>
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Expected', providerUuid: pt.wekbUUID]">${pt.permanent_Expected}</g:link>
                    <g:set var="sumPtExpectedCount" value="${sumPtExpectedCount+pt.permanent_Expected}"/>
                </td>
                <td>
                    ${pt.permanent_Tipp_Deleted}
                    <g:set var="sumDeletedCount" value="${sumDeletedCount+pt.permanent_Tipp_Deleted}"/>
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Deleted', providerUuid: pt.wekbUUID]"> ${pt.permanent_Deleted}</g:link>
                    <g:set var="sumPtDeletedCount" value="${sumPtDeletedCount+pt.permanent_Deleted}"/>
                </td>
                <td>
                    ${pt.permanent_Tipp_Removed}
                    <g:set var="sumRemovedCount" value="${sumRemovedCount+pt.permanent_Tipp_Removed}"/>
                </td>
                <td>
                    <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Removed', providerUuid: pt.wekbUUID]">${pt.permanent_Removed}</g:link>
                    <g:set var="sumPtRemovedCount" value="${sumPtRemovedCount+pt.permanent_Removed}"/>
                </td>
            </tr>
        </g:each>
        </tbody>
    <tfoot>
    <tr>
        <td></td>
        <td></td>
        <td><g:formatNumber number="${sumPackagesCount}" type="number"/></td>
        <td><g:formatNumber number="${sumCurrentCount}" type="number"/></td>
        <td><g:formatNumber number="${sumPtCurrentCount}" type="number"/></td>
        <td><g:formatNumber number="${sumRetiredCount}" type="number"/></td>
        <td><g:formatNumber number="${sumPtRetiredCount}" type="number"/></td>
        <td><g:formatNumber number="${sumExpectedCount}" type="number"/></td>
        <td><g:formatNumber number="${sumPtExpectedCount}" type="number"/></td>
        <td><g:formatNumber number="${sumDeletedCount}" type="number"/></td>
        <td><g:formatNumber number="${sumPtDeletedCount}" type="number"/></td>
        <td><g:formatNumber number="${sumRemovedCount}" type="number"/></td>
        <td><g:formatNumber number="${sumPtRemovedCount}" type="number"/></td>
    </tr>
    </tfoot>
    </table>

</div>

</body>
</html>

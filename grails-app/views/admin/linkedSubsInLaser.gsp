<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Linked Subs in Laser</title>
</head>

<body>

<wekb:serviceInjection/>

<g:set var="laserService" bean="${wekb.LaserService}"/>

<h1 class="ui header">Linked Subs in Laser for Package ${pkg.name} (${totalCount})</h1>

<g:render template="/templates/laserInfosForPkg" model="${[pkg: pkg]}"/>

<div class="container">

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <th>Org</th>
            <th>Subscription</th>
            <th>Status</th>
            <th>Startdate</th>
            <th>Endate</th>
            <th>Perpetual Access</th>
            <th>Holding Selection</th>
            <th>Typ</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${linkedSubs}" var="subInfo" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <a href="${laserService.getLaserOrgURL()+'/'+subInfo.org_id}" target="_blank">${subInfo.org_name}</a>
                </td>
                <td>
                    <a href="${laserService.getLaserSubURL()+'/'+subInfo.sub_id}" target="_blank">${subInfo.sub_name}</a>
                </td>
                <td>
                    ${subInfo.status}
                </td>
                <td>
                    ${subInfo.sub_start_date}
                </td>
                <td>
                    ${subInfo.sub_end_date}
                </td>
                <td>
                    ${subInfo.sub_has_perpetual_access}
                </td>
                <td>
                    ${subInfo.holding_selection}
                </td>
                <td>
                    ${subInfo.sub_typ}
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

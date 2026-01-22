<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb | wekb -  Plaforms Diff</title>
</head>

<body>

<wekb:serviceInjection/>

<g:set var="laserService" bean="${wekb.LaserService}"/>
<g:set var="platformUrl" value="${laserService.getLaserPlatformURL()}"/>
<g:set var="providerUrl" value="${laserService.getLaserProviderURL()}"/>

<h1 class="ui header">Platforms in Laser but not in WEKB (${totalCount})</h1>


<div class="container">

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Status</th>
            <th>Provider</th>
            <th>Provider Status</th>
            <th>Titles</th>
            <th>Packages</th>
            <th>Packages linked with Sub</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${platformDiff}" var="platform" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <a href="${platformUrl+'/'+platform.plat_id}" target="_blank">${platform.plat_name}</a>
                </td>
                <td>
                    ${platform.platform_status}
                </td>
                <td>
                    <a href="${providerUrl+'/'+platform.prov_id}" target="_blank">${platform.prov_name}</a>
                </td>
                <td>
                    ${platform.prov_status}
                </td>
                <td>
                    ${platform.titles}
                </td>
                <td>
                    ${platform.packages}
                </td>
                <td>
                    ${platform.linkedPackages}
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

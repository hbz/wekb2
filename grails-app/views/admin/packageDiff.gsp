<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Packages Diff</title>
</head>

<body>

<wekb:serviceInjection/>

<g:set var="laserService" bean="${wekb.LaserService}"/>
<g:set var="packageUrl" value="${laserService.getLaserPackageURL()}"/>
<g:set var="providerUrl" value="${laserService.getLaserProviderURL()}"/>

<h1 class="ui header">Packages in Laser but not in WEKB (${totalCount})</h1>


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
            <th>Packages linked with Sub</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${packageDiff}" var="pkg" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <a href="${packageUrl+'/'+pkg.pkg_id}" target="_blank">${pkg.pkg_name}
                </td>
                <td>
                    ${pkg.pkg_status}
                </td>
                <td>
                    <a href="${providerUrl+'/'+pkg.prov_id}" target="_blank">${pkg.prov_name}</a>
                </td>
                <td>
                    ${pkg.prov_status}
                </td>
                <td>
                    ${pkg.titles}
                </td>
                <td>
                    ${pkg.linkedPackages}
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

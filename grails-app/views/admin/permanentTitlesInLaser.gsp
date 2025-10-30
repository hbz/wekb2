<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Permanent Titles in Laser [Status: ${status}]</title>
</head>

<body>

<wekb:serviceInjection/>

<g:set var="laserService" bean="${wekb.LaserService}"/>
<g:set var="subPackageUrl" value="${laserService.getLaserSubPackageURL()}"/>
<g:set var="ieUrl" value="${laserService.getLaserIeURL()}"/>
<g:set var="tippUrl" value="${laserService.getLaserTippURL()}"/>

<h1 class="ui header">Permanent Titles in Laser (${totalCount}) [Status: ${status}]</h1>


<div class="container">

    <g:form action="${actionName}" controller="${controllerName}" params="${params}">
        <div class="ui toggle checkbox">
            <input type="checkbox" name="withWekbTipp" ${params.withWekbTipp ? 'checked' : ''} onchange="this.form.submit()">
            <label>Show WEKB Tipp (Takes longer)</label>
        </div>
    </g:form>

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <g:if test="${params.withWekbTipp}">
                <th>Title wekb</th>
                <th>Tipp Status wekb</th>
            </g:if>
            <th>Package</th>
            <th>Title in Laser</th>
            <th>Tipp Status</th>
            <th>IE Status</th>
            <th>Subscription</th>
            <th>Sub Status</th>
            <th>Sub Startdate</th>
            <th>Sub Endate</th>
            <th>Sub Perpetual Access</th>
            <th>Sub Holding Selection</th>
            <th>Sub Typ</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${linkedPTs}" var="ptInfo" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <g:if test="${params.withWekbTipp}">
                    <td>
                        <g:link controller="resource" action="show" id="${'wekb.TitleInstancePackagePlatform:' + ptInfo.id}">${ptInfo.name}</g:link>
                    </td>
                    <td>
                        ${ptInfo.status}
                    </td>
                </g:if>
                <td>
                    ${ptInfo.pkg_name}
                </td>
                <td>
                    <a href="${tippUrl + '/' + ptInfo.laser_tipp_id}" target="_blank">${ptInfo.laser_tipp_name}</a>
                </td>
                <td>
                    ${ptInfo.laser_tipp_status}
                </td>
                <td>
                    <a href="${ieUrl + '/' + ptInfo.laser_pt_ie_fk}" target="_blank">${ptInfo.laser_ie_status}</a>
                </td>
                <td>
                    <a href="${subPackageUrl+'/'+ptInfo.sub_id}" target="_blank">${ptInfo.sub_name}</a>
                </td>
                <td>
                    ${ptInfo.status}
                </td>
                <td>
                    ${ptInfo.sub_start_date}
                </td>
                <td>
                    ${ptInfo.sub_end_date}
                </td>
                <td>
                    ${ptInfo.sub_has_perpetual_access}
                </td>
                <td>
                    ${ptInfo.holding_selection}
                </td>
                <td>
                    ${ptInfo.sub_typ}
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>


    <semui:paginateNew controller="${controllerName}" action="${actionName}" id="${params.id}" params="${params}"
                       max="${params.max}" total="${totalCount}"/>
</div>

</body>
</html>

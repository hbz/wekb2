<%@ page import="wekb.TitleInstancePackagePlatform" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Linked Subs in Laser</title>
</head>

<body>

<wekb:serviceInjection/>

<g:set var="laserService" bean="${wekb.LaserService}"/>
<g:set var="orgUrl" value="${laserService.getLaserOrgURL()}"/>
<g:set var="subPackageUrl" value="${laserService.getLaserSubPackageURL()}"/>
<g:set var="ieUrl" value="${laserService.getLaserIeURL()}"/>
<g:set var="tippUrl" value="${laserService.getLaserTippURL()}"/>

<h1 class="ui header">Linked Subs in Laser for Package <g:link controller="resource" action="show"
                                                               id="${pkg.class.name + ':' + pkg.id}">${pkg.name}</g:link> (${totalCount}) <g:if
        test="${status}">[PT in LASER (${status})]</g:if></h1>

<g:render template="/templates/laserInfosForPkg" model="${[pkg: pkg]}"/>

<table class="ui selectable striped sortable celled table">
    <thead>
    <tr>
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
    <tr>
        <td>
            <a href="${orgUrl + '/' + subInfo.org_id}" target="_blank">${subInfo.org_name}</a>
        </td>
        <td>
            <a href="${subPackageUrl + '/' + subInfo.sub_id}" target="_blank">${subInfo.sub_name}</a>
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
    </tbody>
</table>

<div class="container">
    <g:form action="${actionName}" controller="${controllerName}" params="[status: params.status, subId: params.subId]" id="${params.id}">
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
            <th>Title in Laser</th>
            <th>Tipp Status</th>
            <th>IE Status</th>
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
                    <a href="${tippUrl + '/' + ptInfo.laser_tipp_id}" target="_blank">${ptInfo.laser_tipp_name}</a>
                </td>
                <td>
                    ${ptInfo.laser_tipp_status}
                </td>
                <td>
                    <a href="${ieUrl + '/' + ptInfo.laser_pt_ie_fk}" target="_blank">${ptInfo.laser_ie_status}</a>
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

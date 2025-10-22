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

<h1 class="ui header">Linked Subs in Laser for Package <g:link controller="resource" action="show" id="${pkg.class.name+':'+pkg.id}">${pkg.name}</g:link> (${totalCount}) <g:if test="${status}">[PT in LASER (${status})]</g:if> </h1>

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
                <a href="${laserService.getLaserOrgURL()+'/'+subInfo.org_id}" target="_blank">${subInfo.org_name}</a>
            </td>
            <td>
                <a href="${laserService.getLaserSubPackageURL()+'/'+subInfo.sub_id}" target="_blank">${subInfo.sub_name}</a>
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
<%
    Set<String> wekbUuids = []
%>
<div class="container">

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <th>Title wekb</th>
            <th>Tipp Status wekb</th>
            <th>Title in Laser</th>
            <th>Tipp Status</th>
            <th>IE Status</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${linkedPTs}" var="ptInfo" status="i">
            <g:set var="tippWekb" value="${wekb.TitleInstancePackagePlatform.findByUuid(ptInfo.tipp_gokb_id)}"/>
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${tippWekb.class.name+':'+tippWekb.id}">${tippWekb.name}</g:link>
                </td>
                <td>
                    ${tippWekb.status.value_en}
                </td>
                <td>
                    <a href="${laserService.getLaserTippURL()+'/'+ptInfo.tipp_id}" target="_blank">${ptInfo.tipp_name}</a>
                </td>
                <td>
                    ${ptInfo.tipp_status}
                </td>
                <td>
                    <a href="${laserService.getLaserIeURL()+'/'+ptInfo.pt_ie_fk}" target="_blank">${ptInfo.ie_status}</a>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

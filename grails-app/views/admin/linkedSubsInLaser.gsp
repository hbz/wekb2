<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory" %>
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

<h1 class="ui header">Linked Subs in Laser for Package <g:link controller="resource" action="show"
                                                               id="${pkg.class.name + ':' + pkg.id}">${pkg.name}</g:link> (${totalCount}) <g:if
        test="${status}">[PT in LASER (${status})]</g:if></h1>

<g:render template="/templates/laserInfosForPkg" model="${[pkg: pkg]}"/>

<g:set var="refDataValueStatus" value="${RefdataCategory.lookup(RCConstants.COMPONENT_STATUS, status)}"/>

<g:if test="${refDataValueStatus}">
    <g:link controller="admin" action="findTippDuplicatesByPkg" id="${pkg.uuid}" params="[status: refDataValueStatus.id]"
            class="ui button primary">Tipps Duplicates by Status ${status} (${pkg.getTippDuplicatesWithStatusByTitleIDCount(refDataValueStatus)})</g:link>
</g:if>


<g:link controller="admin" action="permanentTitlesInLaser" params="[status: status]" id="${pkg.id}"
        class="ui button">Show ${linkedSubs?.sum { it.ptCount }} Permanent Titles (${status})</g:link>
<br>
<br>

<div class="container">

    <g:set var="sumPtCount" value="${0}"/>

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
            <th>[PT Tipp in LASER (${status})]</th>
            <th>[PT in LASER (${status})]</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${linkedSubs}" var="subInfo" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
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
                <td>
                    ${subInfo.tippCount}
                </td>
                <td>
                    <g:link action="linkedPTOverSubInLaser" controller="admin"
                            id="${pkg.id}" params="[subId: subInfo.sub_id, status: params.status]">
                        ${subInfo.ptCount}
                    </g:link>
                    <g:set var="sumPtCount" value="${sumPtCount + subInfo.ptCount}"/>
                </td>
            </tr>
        </g:each>
        </tbody>
        <tfoot>
        <tr>
            <td colspan="10"></td>
            <td>Total: ${sumPtCount}</td>
        </tr>
        </tfoot>
    </table>

</div>

</body>
</html>

<%@ page import="wekb.helper.RDStore" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Tipp Duplicates</title>
</head>

<body>

<wekb:serviceInjection/>
<g:set var="laserService" bean="${wekb.LaserService}"/>


<semui:flashMessage data="${flash}"/>


<h1 class="ui header">Tipp Duplicates for

<g:link controller="resource" action="show" id="${pkg.getOID()}">
    ${pkg.name}
</g:link>
</h1>

<g:if test="${params.tippsDuplicatesBy == "name"}">
    <h3>Tipps Duplicates By Name (${totalCount})</h3>
</g:if>

<g:if test="${params.tippsDuplicatesBy == "url"}">
    <h3>Tipps Duplicates By Url (${totalCount})</h3>

    <div class="ui right floated buttons">
        <g:link controller="admin" action="removeTippDuplicatesByUrlOfPkg" id="${params.id}"
                class="ui button primary">Remove Tipps Duplicates By Url</g:link>
    </div>
    <br>
</g:if>

<g:if test="${params.tippsDuplicatesBy == "titleID"}">
    <h3>Tipps Duplicates By Title ID (${totalCount}) <g:if test="${status}">[${status}]</g:if> </h3>


    <g:link controller="admin" action="removeTippDuplicatesByTitleIDOfPkg" id="${params.id}"
            class="ui button right floated  primary">Remove Tipps Duplicates By Title_ID</g:link>
    <br>
    <br>
    <g:link controller="workflow" action="action"
            params="${[component: 'wekb.Package:' + pkg.id, selectedAction: 'workFlowMethod::updatePackageAllTitlesFromKbartSource', curationOverride: 'true']}"
            class="ui button right floated  primary">Trigger KBART Update (all Titles)</g:link>
    <br>


    <g:link controller="admin" action="findTippDuplicatesByPkg" id="${params.id}" params="[status: RDStore.KBC_STATUS_CURRENT.id]"
            class="ui button primary">Tipps Duplicates by Status Current (${pkg.getTippDuplicatesWithStatusByTitleIDCount(wekb.helper.RDStore.KBC_STATUS_CURRENT)})</g:link>
    <br>
    <br>
    <g:link controller="admin" action="findTippDuplicatesByPkg" id="${params.id}" params="[status: RDStore.KBC_STATUS_RETIRED.id]"
            class="ui button primary">Tipps Duplicates by Status Retired (${pkg.getTippDuplicatesWithStatusByTitleIDCount(wekb.helper.RDStore.KBC_STATUS_RETIRED)})</g:link>

    <br>
    <br>
    <g:link controller="admin" action="findTippDuplicatesByPkg" id="${params.id}" params="[status: RDStore.KBC_STATUS_EXPECTED.id]"
            class="ui button primary">Tipps Duplicates by Status Expected (${pkg.getTippDuplicatesWithStatusByTitleIDCount(wekb.helper.RDStore.KBC_STATUS_EXPECTED)})</g:link>

    <br>
    <br>
    <g:link controller="admin" action="findTippDuplicatesByPkg" id="${params.id}" params="[status: RDStore.KBC_STATUS_DELETED.id]"
            class="ui button primary">Tipps Duplicates by Status Deleted (${pkg.getTippDuplicatesWithStatusByTitleIDCount(wekb.helper.RDStore.KBC_STATUS_DELETED)})</g:link>

    <br>
    <br>
    <g:link controller="admin" action="findTippDuplicatesByPkg" id="${params.id}" params="[status: RDStore.KBC_STATUS_REMOVED.id]"
            class="ui button primary">Tipps Duplicates by Status Removed (${pkg.getTippDuplicatesWithStatusByTitleIDCount(wekb.helper.RDStore.KBC_STATUS_REMOVED)})</g:link>
</g:if>

<table class="ui selectable striped sortable celled table">
    <thead>
    <tr>
        <th>#</th>
        <th>Title</th>
        <th>Identifiers</th>
        <th>Platform</th>
        <th>Publication Type</th>
        <th>Medium</th>
        <th>Url</th>
        <th>PT in LASER</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${tippsDuplicates}" var="t" status="i">
        <tr>
            <td>
                ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
            </td>
            <td>
                <g:link controller="resource" action="show" id="${t.getOID()}">
                    ${t.name} <strong>(${t.status.value})</strong>
                </g:link>
            </td>
            <td>
                <ul>
                    <g:each in="${t.ids.sort { it.namespace.value }}" var="id">
                        <li><strong>${id.namespace.value}</strong>:<g:link controller="resource"
                                                                           action="show"
                                                                           id="${id.getOID()}">${id.value}</g:link>
                        </li>
                    </g:each>
                </ul>
            </td>
            <td>
                <g:link controller="resource" action="show"
                        id="${t.pkg.nominalPlatform.getOID()}">
                    ${t.pkg.nominalPlatform?.name}
                </g:link>
            </td>
            <td>${t.publicationType?.value}</td>
            <td>${t.medium?.value}</td>
            <td>
                ${t.url}
            </td>
            <td>
                <g:link controller="admin" action="permanentTitlesInLaser" params="[tippId: t.id]" id="${t.pkg.id}">
                    ${laserService.permanentTitleInLaserCount(t.uuid)}
                </g:link>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<semui:paginate controller="${controllerName}" action="${actionName}"
                params="[id: params.id, papaginateByName: true]"
                max="${max}" offset="${offset}" total="${totalCount}"/>

</body>
</html>

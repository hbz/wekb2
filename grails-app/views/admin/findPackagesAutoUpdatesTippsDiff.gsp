<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Automatic update packages with title difference</title>
</head>

<body>

<wekb:serviceInjection/>


<semui:flashMessage data="${flash}"/>

<h1 class="ui header">Automatic update packages with title difference (${pkgs.size()})</h1>


<div class="container">

    <div class="ui segment">
        <g:form controller="admin" action="findPackagesAutoUpdatesTippsDiff" class="ui form">
            <div class="field">
                <label>Filter by Curatory Group</label>
                <semui:simpleReferenceDropdown name="curatoryGroup"
                                               baseClass="wekb.CuratoryGroup"
                                               filter1="Current" value="${params.curatoryGroup}"/>
            </div>

            <div class="ui right floated buttons">
                <button type="submit" class="ui black button">Filter</button>
                <g:link class="ui button" action="findPackagesAutoUpdatesTippsDiff">Reset</g:link>
            </div>

            <br>
            <br>
        </g:form>
    </div>

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <semui:sortableColumn property="p.name" title="Name"/>
            <semui:sortableColumn property="p.curatoryGroups" title="Curatory Groups"/>
            <th>Source / KBART URL</th>
            <semui:sortableColumn property="p.kbartSource.lastRun" title="Last Run"/>
            <th>Titles in Package</th>
            <th>Titles in KBART</th>
            <th>Invalid Titles in KBART</th>
            <th>Current Titles</th>
            <th>Deleted Titles</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgs}" var="pkg" status="i">
            <g:set var="tippCount" value="${pkg.getTippCount()}"/>
            <g:set var="autoUpdateInfo" value="${pkg.getLastSuccessfulAutoUpdateInfo()}"/>
            <g:set var="currentTippCount" value="${pkg.getCurrentTippCount()}"/>
            <g:set var="deletedTippCount" value="${pkg.getDeletedTippCount()}"/>
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.getOID()}">
                        ${pkg.name} (${pkg.provider})
                    </g:link>
                </td>
                <td>
                    <g:each in="${pkg.curatoryGroups}" var="curatoryGroupPackage">
                        ${curatoryGroupPackage.curatoryGroup.name}
                    </g:each>
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.kbartSource.getOID()}">
                        ${pkg.kbartSource.name}
                    </g:link>
                    <semui:showOutGoingLink text="KBART Url" outGoingLink="${pkg.kbartSource.url}"/>
                </td>
                <td>
                    <g:if test="${pkg.kbartSource.lastRun}">
                        <g:formatDate date="${pkg.kbartSource.lastRun}"
                                      format="${message(code: 'default.date.format')}"/>
                    </g:if>
                </td>
                <td class="${(autoUpdateInfo && (autoUpdateInfo.countKbartRows > tippCount)) ? 'negative': ''}">
                    ${tippCount}
                </td>
                <td class="${(autoUpdateInfo && (autoUpdateInfo.countKbartRows > tippCount)) ? 'negative': ''}">
                    <g:if test="${autoUpdateInfo}">
                        ${autoUpdateInfo.countKbartRows}
                    </g:if>
                </td>
                <td>
                    <g:if test="${autoUpdateInfo}">
                        ${autoUpdateInfo.countInValidTipps}
                    </g:if>
                </td>
                <td class="${deletedTippCount > currentTippCount ? 'negative': ''}">
                    ${currentTippCount}
                </td>
                <td class="${deletedTippCount > currentTippCount ? 'negative': ''}">
                    ${deletedTippCount}
                </td>
                <td>
                    <g:set var="object" value="${pkg.getOID()}"/>
                    <g:link class="ui button" controller="workflow" action="action"
                            params="[component: object, selectedAction: 'workFlowMethod::updatePackageFromKbartSource', curationOverride: true]">Trigger Update (Changed Titles) </g:link>

                    <br>
                    <br>
                    <g:link class="ui button black" controller="workflow" action="action"
                            params="[component: object, selectedAction: 'workFlowMethod::updatePackageAllTitlesFromKbartSource', curationOverride: true]">Trigger Update (all Titles)</g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

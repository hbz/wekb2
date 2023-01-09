<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>Automatic update packages with title difference</title>
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
                                               baseClass="org.gokb.cred.CuratoryGroup"
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
            <semui:sortableColumn property="p.source.lastRun" title="Last Run"/>
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
                    <g:link controller="resource" action="show" id="${pkg.uuid}">
                        ${pkg.name} (${pkg.provider})
                    </g:link>
                </td>
                <td>
                    <g:each in="${pkg.curatoryGroups}" var="curatoryGroup">
                        ${curatoryGroup.name}
                    </g:each>
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.source.uuid}">
                        ${pkg.source.name}
                    </g:link>
                    <semui:showOutGoingLink text="KBART Url" outGoingLink="${pkg.source.url}"/>
                </td>
                <td>
                    <g:if test="${pkg.source.lastRun}">
                        <g:formatDate date="${pkg.source.lastRun}"
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
                    <g:set var="object" value="${pkg.class.name}:${pkg.id}"/>
                    <g:link class="ui button" controller="workflow" action="action"
                            params="[component: object, selectedBulkAction: 'packageUrlUpdate', curationOverride: true]">Trigger Update (Changed Titles) </g:link>

                    <br>
                    <br>
                    <g:link class="ui button black" controller="workflow" action="action"
                            params="[component: object, selectedBulkAction: 'packageUrlUpdateAllTitles', curationOverride: true]">Trigger Update (all Titles)</g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

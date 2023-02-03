<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>we:kb : Packages due to automatic update</title>
</head>

<body>

<wekb:serviceInjection/>


<semui:flashMessage data="${flash}"/>

<h1 class="ui header">Packages due to automatic update (${pkgs.size()})</h1>


<div class="container">

    <div class="ui segment">
        <g:form controller="admin" action="findPackagesNeedsAutoUpdates" class="ui form">
            <div class="field">
                <label>Filter by Curatory Group</label>
                <semui:simpleReferenceDropdown name="curatoryGroup"
                                               baseClass="wekb.CuratoryGroup"
                                               filter1="Current" value="${params.curatoryGroup}"/>
            </div>

            <div class="ui right floated buttons">
                <button type="submit" class="ui black button">Filter</button>
                <g:link class="ui button" action="findPackagesNeedsAutoUpdates">Reset</g:link>
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
            <th>Provider</th>
            <semui:sortableColumn property="p.curatoryGroups" title="Curatory Groups"/>
            <th>Source / KBART URL</th>
            <semui:sortableColumn property="p.kbartSource.lastUpdateUrl" title="Last Update URL"/>
            <semui:sortableColumn property="p.kbartSource.lastRun" title="Last Run"/>
            <th>Next Run</th>
            <th>Titles</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgs}" var="pkg" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.uuid}">
                        ${pkg.name}
                    </g:link>
                </td>
                <td>
                    ${pkg.provider}
                </td>
                <td>
                    <g:each in="${pkg.curatoryGroups}" var="curatoryGroup">
                        ${curatoryGroup.name}
                    </g:each>
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.kbartSource.uuid}">
                        ${pkg.kbartSource.name}
                    </g:link>
                    <semui:showOutGoingLink text="KBART Url" outGoingLink="${pkg.kbartSource.url}"/>
                </td>
                <td>
                    <semui:showOutGoingLink text="Last Update Url" outGoingLink="${pkg.kbartSource.lastUpdateUrl}"/>
                </td>
                <td>
                    <g:if test="${pkg.kbartSource.lastRun}">
                        <g:formatDate date="${pkg.kbartSource.lastRun}"
                                      format="${message(code: 'default.date.format')}"/>
                    </g:if>
                </td>
                <td>
                    <g:set var="nextRun" value="${pkg.kbartSource.getNextUpdateTimestamp()}"/>
                    <g:if test="${nextRun}">
                        <g:formatDate date="${nextRun}"
                                      format="${message(code: 'default.date.format')}"/>
                    </g:if>
                </td>
                <td>
                    ${pkg.getTippCount()}
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

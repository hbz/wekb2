<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Packages due to automatic update of Curatory Groups (${groups.sort{it.name}.name.join(',')})</title>
</head>

<body>

<wekb:serviceInjection/>


<semui:flashMessage data="${flash}"/>

<h1 class="ui header">${pkgs.size()} packages needing auto updates of Curatory Groups (${groups.name.join(',')})</h1>


<div class="container">

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Provider</th>
            <th>Source / KBART URL</th>
            <th>Last Update URL</th>
            <th>Frequency</th>
            <th>Last Run</th>
            <th>Next Run</th>
            <th>Titles</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgs}" var="pkg" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.getOID()}">
                        ${pkg.name}
                    </g:link>
                </td>
                <td>
                    ${pkg.provider}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.kbartSource.getOID()}">
                        ${pkg.kbartSource.name}
                    </g:link>
                    <br>
                    <br>
                    ${pkg.kbartSource.url}
                    <semui:showOutGoingLink text="KBART Url" outGoingLink="${pkg.kbartSource.url}"/>
                </td>
                <td>
                    ${pkg.kbartSource.lastUpdateUrl} <semui:showOutGoingLink text="Last Update Url" outGoingLink="${pkg.kbartSource.lastUpdateUrl}"/>
                </td>
                <td>
                    ${pkg.kbartSource.frequency.getI10n('value')}
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
                    <g:set var="object" value="${pkg.getOID()}"/>
                    <g:link class="ui button" controller="workflow" action="action"
                            params="[component: object, selectedAction: 'workFlowMethod::updatePackageFromKbartSource', curationOverride: true]">Trigger Update (Changed Titles) </g:link>

                    <br>
                    <br>
                    <g:link class="ui button primary" controller="workflow" action="action"
                            params="[component: object, selectedAction: 'workFlowMethod::updatePackageAllTitlesFromKbartSource', curationOverride: true]">Trigger Update (all Titles)</g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>

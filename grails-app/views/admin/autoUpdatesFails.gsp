<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Automatic Update Fails</title>
</head>

<body>

<wekb:serviceInjection/>


<semui:flashMessage data="${flash}"/>

<h1 class="ui header">Automatic Update Fails (${autoUpdates.size()})</h1>

<h3>Only fails automatic update from <g:formatDate date="${new java.util.Date()-1}" format="${message(code: 'default.date.format.noZ')}"/>
until <g:formatDate date="${new java.util.Date()}" format="${message(code: 'default.date.format.noZ')}"/></h3>


<div class="container">
    <g:if test="${autoUpdates.size() > 0}">
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th></th>
                <th>Description</th>
                <th>Package</th>
                <th>Provider</th>
                <th>Curatory Group</th>
                <th>Start Time</th>
                <th>End Time</th>

            </tr>
            </thead>
            <g:each in="${autoUpdates}" var="autoUpdate" status="i">
                <tr>
                    <td>
                        ${i + 1}
                    </td>
                    <td>
                        <g:link controller="resource" action="show" id="${autoUpdate.class.name}:${autoUpdate.uuid}">
                        ${autoUpdate.description}
                        </g:link>
                    </td>
                    <td>
                        <g:link controller="resource" action="show" id="${autoUpdate.pkg.uuid}">
                        ${autoUpdate.pkg.name}
                        </g:link>
                    </td>
                    <td>
                        ${autoUpdate.pkg.provider?.name}
                    </td>
                    <td>
                        <g:each in="${autoUpdate.pkg.curatoryGroups}" var="cgp">
                            ${cgp.curatoryGroup.name}
                        </g:each>
                    </td>
                    <td>
                        <g:formatDate date="${autoUpdate.startTime}"
                                      format="${message(code: 'default.date.format.noZ')}"/>
                    </td>
                    <td>
                        <g:formatDate date="${autoUpdate.endTime}"
                                      format="${message(code: 'default.date.format.noZ')}"/>
                    </td>
                </tr>
            </g:each>
        </table>
    </g:if>
    <g:else>
        <b>No Auto Update with Fail found. Everything is right.</b>
    </g:else>
</div>

</body>
</html>

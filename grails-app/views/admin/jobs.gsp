<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>we:kb : Jobs</title>
</head>

<body>

<h1 class="ui header">Jobs</h1>


<div class="ui segment"><h2>${cms.executorService.executor.activeCount} out of ${cms.executorService.executor.poolSize} threads In use Jobs</h2>
</div>

<semui:flashMessage data="${flash}"/>

<div class="ui segment">
    <h2 class="ui header">Finished Upload Jobs</h2>

    <g:link class="display-inline" controller="search" action="inlineSearch"
            params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:jobResults', inline: true]"
            id="">Finished Upload Jobs</g:link>
</div>

<div class="ui segment">
    <h2 class="ui header">Current Jobs</h2>

    <button class="ui black button" value="Refresh Page"
            onClick="window.location.reload()">Reload</button>
    <g:link controller="admin" action="cleanJobList" class="ui button">Clean Job List</g:link>

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Group-ID</th>
            <th>Type</th>
            <th>Description</th>
            <th>Has Started</th>
            <th>Start Time</th>
            <th>Status</th>
            <th>End Time</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${jobs}" var="k,v">
            <tr class="${k == params.highlightJob ? 'highlightRow' : ''}">
                <td rowspan="2">${k}</td>
                <td>${v.groupId}</td>
                <td>${v.type}</td>
                <td>${v.description}</td>
                <td>${v.begun}</td>
                <td>${v.startTime}</td>
                <td>
                    <g:if test="${v.isCancelled()}">
                        Cancelled
                    </g:if>
                    <g:elseif test="${v.isDone() && v.endTime}">
                        Finished
                    </g:elseif>
                    <g:elseif test="${v.isDone()}">
                        Done
                    </g:elseif>
                    <g:else>
                        Not Done <g:if test="${v.progress}">(${v.progress}%)</g:if>
                    </g:else>
                </td>
                <td>${v.endTime}</td>
                <td><g:if test="${!v.isCancelled() && !v.isDone()}"><g:link controller="admin"
                                                                            action="cancelJob"
                                                                            onclick="return confirm('Are you sure?')"
                                                                            id="${v.uuid}">Cancel</g:link></g:if></td>
            </tr>
            <tr>
                <td colspan="6">
                    messages:
                    <ul>
                        <g:each in="${v.messages}" var="m">
                            <g:if test="${m instanceof String}">
                                <li>${m}</li>
                            </g:if>
                            <g:else>
                                <li>${m?.message}</li>
                            </g:else>
                        </g:each>
                    </ul>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
</body>
</html>

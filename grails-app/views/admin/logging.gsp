<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Logging</title>
</head>

<body>
<h1 class="ui header">Logging (${totalCount})</h1>

<table class="ui selectable striped sortable celled table">
    <thead>
    <tr>
        <th>Logger</th>
        <th>Effective</th>
        <th>Configured</th>
        <th>Source</th>
        <th>Expires At</th>
        <th>Changed By</th>
        <th>Previous Level</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
<g:each in="${listLoggers}" var="log">
    <tr>
        <td>${log.logger}</td>
        <td>${log.effective}</td>
        <td>${log.configured}</td>
        <td>${log.source}</td>
        <td><g:formatDate date="${log.expiresAt}" format="yyyy-MM-dd HH:mm:ss"/></td>
        <td>${log.changedBy}</td>
        <td>${log.previousLevel}</td>
        <td>
            <g:if test="${log.previousLevel}">
                <g:link class="ui button" action="logging" params="[setLogging: log.previousLevel, logger: log.logger]">SET ${log.previousLevel}</g:link>
            </g:if>
            <g:else>
                <g:link class="ui button" action="logging" params="[setLogging: 'DEBUG', logger: log.logger]">SET DEBUG</g:link>
            </g:else>

            %{--<g:link  class="ui button" action="logging" params="[resetLogging: 'RESET', logger: log.logger]">RESET</g:link>--}%
        </td>
    </tr>
</g:each>
    </tbody>
</table>

</body>
</html>
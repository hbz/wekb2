<!doctype html>
<html>
<head>
    <meta name="layout" content="public_semui">
    <title>Manage FTControl</title>
</head>

<body>

<h1 class="ui header">Manage FTControl</h1>

<div class="ui segment">
    <h2 class="ui header">Full Text Control</h2>
    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>domainClassName</th>
            <th>lastTimestamp</th>
            <th>lastTimestamp in Date</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${ftControls.sort { it.domainClassName }}" var="ftControl">
            <tr>
                <td>${ftControl.domainClassName}</td>
                <td>
                    <semui:xEditable owner="${ftControl}" field="lastTimestamp"/>
                </td>
                <td>
                    <g:formatDate date="${new Date(ftControl.lastTimestamp)}"
                                  format="${message(code: 'default.datetime.format')}"/>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

<div class="ui segment">
    <h2 class="ui header">Indices</h2>

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>index</th>
            <th>type</th>
            <th>count index</th>
            <th>count DB</th>
            <th>count status deleted in DB</th>
            <th>count status removed in DB</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${indices.sort { it.type }}" var="indexInfo">
            <tr>
                <td>${indexInfo.name}</td>
                <td>${indexInfo.type}</td>
                <td>${indexInfo.countIndex}</td>
                <td>${indexInfo.countDB}</td>
                <td>${indexInfo.countDeletedInDB}</td>
                <td>${indexInfo.countRemovedInDB}</td>
                <td><g:link action="deleteIndex" params="[name: indexInfo.name]">Delete and refill Index</g:link></td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

</body>
</html>

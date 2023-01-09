<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: About</title>
</head>

<body>
<h1 class="ui header">About <g:message code="gokb.appname" default="we:kb"/></h1>

<div class="segment">
        <h3 class="ui header">
            Application Info
        </h3>
    <table class="ui selectable striped sortable celled table">
        <tr><th>Git Branch</th><td><g:meta name="build.git.branch"/></td></tr>
        <tr><th>Git Commit</th><td><g:meta name="build.git.revision"/></td></tr>
        <tr><th>App version</th><td><g:meta name="info.app.version"/></td></tr>
        <tr><th>App name</th><td><g:meta name="info.app.name"/></td></tr>
        <tr><th>Grails version</th><td><g:meta name="info.app.grailsVersion"/></td></tr>
        <tr><th>Groovy version</th><td>${GroovySystem.getVersion()}</td></tr>
        <tr><th>Environment</th><td><g:meta name="grails.env"/></td></tr>
        <tr><th>JVM version</th><td>${System.getProperty('java.version')}</td></tr>
        <tr><th>Reloading active</th><td>${grails.util.Environment.reloadingAgentEnabled}</td></tr>
        <tr><th>Build Date</th><td><g:meta name="build.time"/></td></tr>
        <tr><th>ES Cluster</th><td>${grailsApplication.config.wekb.es?.cluster}</td></tr>
        <tr><th>ES Index</th><td>${grailsApplication.config.wekb.es?.indices?.values().join(", ")}</td></tr>
    </table>

</div>

<div class="segment">
    <h3 class="ui header">
            Database
        </h3>
        <table class="ui selectable striped sortable celled table">
        <tbody>
        <tr><td>DBM version</td><td>${dbmVersion[0]} : ${dbmVersion[1]}</td></tr>
        <tr><td>DBM updateOnStart</td><td>${grailsApplication.config.grails.plugin.databasemigration.updateOnStart}</td>
        </tr>
        <tr><td>DataSource.dbCreate</td><td>${grailsApplication.config.dataSource.dbCreate}</td></tr>
        <tbody>
    </table>
</div>
</body>
</html>

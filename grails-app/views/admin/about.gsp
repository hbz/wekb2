<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : About</title>
</head>

<body>
<h1 class="ui header">About we:kb</h1>

<div class="segment">
        <h3 class="ui header">
            Application Info
        </h3>
    <table class="ui selectable striped sortable celled table">
        <tr><td>App profile </td><td>${grailsApplication.config.getProperty('grails.profile')}</td></tr>
        <tr><td>Git Branch</td><td><g:meta name="build.git.branch"/></td></tr>
        <tr><td>Git Commit</td><td><g:meta name="build.git.revision"/></td></tr>
        <tr><td>App version</td><td><g:meta name="info.app.version"/></td></tr>
        <tr><td>App name</td><td><g:meta name="info.app.name"/></td></tr>
        <tr><td>Grails version</td><td><g:meta name="info.app.grailsVersion"/></td></tr>
        <tr><td>Groovy version</td><td>${GroovySystem.getVersion()}</td></tr>
        <tr><td>Environment</td><td><g:meta name="grails.env"/></td></tr>
        <tr><td>JVM version</td><td>${System.getProperty('java.version')}</td></tr>
        <tr><td>Reloading active</td><td>${grails.util.Environment.reloadingAgentEnabled}</td></tr>
        <tr><td>Build Date</td><td><g:meta name="build.time"/></td></tr>
        <tr><td>ES Cluster</td><td>${grailsApplication.config.wekb.es?.cluster}</td></tr>
        <tr><td>ES Index</td><td>${grailsApplication.config.wekb.es?.indices?.values().join(", ")}</td></tr>
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

<div class="segment">
    <h3 class="ui header">
        Artefacts
    </h3>
    <table class="ui selectable striped sortable celled table">
        <tbody>
        <tr><td>Controllers:</td><td>${grailsApplication.controllerClasses.size()}</td></tr>
        <tr><td>Domains:</td><td>${grailsApplication.domainClasses.size()}</td></tr>
        <tr><td>Services:</td><td>${grailsApplication.serviceClasses.size()}</td></tr>
        <tr><td>Tag Libraries:</td><td>${grailsApplication.tagLibClasses.size()}</td></tr>
        <tbody>
    </table>
</div>

<div class="segment">
    <h3 class="ui header">
        Installed Plugins
    </h3>
    <table class="ui selectable striped sortable celled table">
        <tbody>
        <g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
            <tr><td>${plugin.name} </td><td>${plugin.version}</td></tr>
        </g:each>
        <tbody>
    </table>
</div>
</body>
</html>

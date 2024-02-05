<%@ page import="wekb.utils.DateUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Admin</title>
</head>

<body>
<h1 class="ui header"><i class="tools icon"></i> Admin Dashboard</h1>

<div class="ui equal width grid">
    <div class="row">
        <div class="column">
            <h2 class="ui header">Admin Search</h2>
            <div class="ui divided large relaxed list">
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:updatePackageInfos']">Update Package Infos</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:updateTippInfos']">Update Title Infos</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:identifiers']">Identifiers</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:namespaces']">Identifier Namespaces</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:jobResults']">Job Infos</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:refdataCategories']">Refdata Categories</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:refdataValues']">Refdata Values</g:link>
            </div>
        </div>
        <div class="column">
            <h2 class="ui header">Admin Create</h2>
            <div class="ui divided large relaxed list">
                <g:link class="item" controller="create" action="index"
                        params="[tmpl: 'wekb.IdentifierNamespace']">Identifier Namespace</g:link>
                <g:link class="item" controller="create" action="index"
                        params="[tmpl: 'wekb.RefdataCategory']">Refdata Category</g:link>
                <g:link class="item" controller="create" action="index"
                        params="[tmpl: 'wekb.RefdataValue']">Refdata Value</g:link>
                <g:link class="item" controller="create" action="index"
                        params="[tmpl: 'wekb.CuratoryGroup']">Curatory Group</g:link>
                <g:link class="item" controller="create" action="index"
                        params="[tmpl: 'wekb.Org']">Provider</g:link>
                <g:link class="item" controller="create" action="index"
                        params="[tmpl: 'wekb.Vendor']">Vendor</g:link>
                <g:link class="item" controller="create" action="index"
                        params="[tmpl: 'wekb.auth.User']">User</g:link>
            </div>
        </div>
        <div class="column">
            <h2 class="ui header">Admin Jobs</h2>
            <div class="ui divided large relaxed list">
                <g:link class="item" controller="admin" action="updateTextIndexes"
                        onclick="return confirm('Are you sure?')">Update Free Text Indexes</g:link>
                %{--              <g:link class="item" controller="admin" action="resetTextIndexes" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Reset Free Text Indexes</g:link>--}%
                <g:link class="item" controller="admin" action="recalculateStats"
                        onclick="return confirm('Are you sure?')">Recalculate Statistics</g:link>
                <g:link class="item" controller="admin" action="expungeRemovedComponents"
                        onclick="return confirm('Are you sure?')">Expunge Removed Component</g:link>
                <g:link class="item" controller="admin" action="autoUpdatePackages"
                        onclick="return confirm('Are you sure?')">Auto Update All Packages (Last Changed)</g:link>
                <g:link class="item" controller="admin" action="autoUpdatePackagesAllTitles"
                        onclick="return confirm('Are you sure?')">Auto Update All Packages</g:link>
                %{-- <g:link class="item" controller="admin" action="cleanupTippIdentifersWithSameNamespace"
                         onclick="return confirm('Are you sure?')">Cleanup Tipp Identifers with same Namespace</g:link>--}%
                <g:link class="item" controller="admin" action="setTippsWithoutUrlToDeleted"
                        onclick="return confirm('Are you sure?')">Set Tipps without Url to deleted</g:link>
            </div>
        </div>
    </div>
</div>

<br />

<div class="ui equal width grid">
    <div class="row">
        <div class="column">
            <h2 class="ui header">Admin Infos</h2>
            <div class="ui divided large relaxed list">
                <g:link class="item" controller="admin" action="systemThreads">Show Threads</g:link>
                <g:link class="item" controller="admin" action="autoUpdatesFails">Automatic update fails</g:link>
                <g:link class="item" controller="admin" action="findPackagesWithTippDuplicates">Packages with Tipp Duplicates</g:link>
                <g:link class="item" controller="admin" action="findPackagesAutoUpdatesTippsDiff">Auto Update Packages with Tipp Diff</g:link>
                <g:link class="item" controller="admin" action="tippIdentifiersWithSameNameSpace">Title Identifiers with same Identifier Namespace</g:link>
            </div>
        </div>
    </div>
</div>

<br>
<div class="ui segment">
    <h2 class="ui header">Components Infos</h2>

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>Name</th>
            <th>count in DB</th>
            <th>count status deleted in DB</th>
            <th>count status removed in DB</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${componentsInfos.sort { it.type }}" var="componentsInfo">
            <tr>
                <td>${componentsInfo.name}</td>
                <td>${componentsInfo.countDB}</td>
                <td>${componentsInfo.countDeletedInDB}</td>
                <td>${componentsInfo.countRemovedInDB}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

<br>

<div class="ui segment">
        <h2 class="ui header">
            Application Info
        </h2>
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
        <tr><td>ES Cluster</td><td>${grailsApplication.config.getProperty('wekb.es.cluster', String)}</td></tr>
        <tr><td>ES Index</td><td>${grailsApplication.config.getProperty('wekb.es.indices', Map).values().join(", ")}</td></tr>
    </table>
</div>

<div class="ui segment">
    <h2 class="ui header">
            Database
        </h2>
        <table class="ui selectable striped sortable celled table">
        <tbody>
        <tr><td>DBM version</td><td>${dbmVersion[0]} : ${dbmVersion[1]} -------> ${wekb.utils.DateUtils.getSDF_NoZ().format(dbmVersion[2])}</td></tr>
        <tr><td>DBM updateOnStart</td><td>${grailsApplication.config.getProperty('grails.plugin.databasemigration.updateOnStart', Boolean)}</td>
        </tr>
        <tr><td>DataSource.dbCreate</td><td>${grailsApplication.config.getProperty('dataSource.dbCreate', Boolean)}</td></tr>
        <tbody>
    </table>
</div>

<div class="ui segment">
    <h2 class="ui header">
        Artefacts
    </h2>
    <table class="ui selectable striped sortable celled table">
        <tbody>
        <tr><td>Controllers:</td><td>${grailsApplication.controllerClasses.size()}</td></tr>
        <tr><td>Domains:</td><td>${grailsApplication.domainClasses.size()}</td></tr>
        <tr><td>Services:</td><td>${grailsApplication.serviceClasses.size()}</td></tr>
        <tr><td>Tag Libraries:</td><td>${grailsApplication.tagLibClasses.size()}</td></tr>
        <tbody>
    </table>
</div>

<div class="ui segment">
    <h2 class="ui header">
        Installed Plugins
    </h2>
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

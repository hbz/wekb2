<%@ page import="wekb.utils.ServerUtils; wekb.utils.ServerUtils; wekb.helper.RDStore;" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title><g:layoutTitle default="we:kb"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}">

    <g:javascript> var spotlightSearchUrl="${g.createLink(controller: 'search', action: 'spotlightSearch')}";</g:javascript>
    <g:javascript> var ajaxLookUp="${g.createLink(controller: 'ajaxJson', action: 'lookup')}";</g:javascript>

    <asset:javascript src="wekb.js"/>
    <asset:stylesheet src="wekb.css"/>

</head>

<wekb:serviceInjection/>
<g:set var="currentServer" scope="page" value="${wekb.utils.ServerUtils.getCurrentServer()}"/>

<body class="pushable">
    <wekb:serverlabel server="${currentServer}"/>
    <div class="ui left vertical inverted visible menu sidebar ${serverLabel}" id="toc">
        <g:link controller="public" action="index" class="header item">
            <img alt="Logo wekb" src="${resource(dir: 'images', file: 'logo.svg')}"/>
        </g:link>

        <div class="item">
            <div class="header">Search</div>

            <div class="menu">
                <g:link class="item" controller="search" action="index">All Components</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:packages']">Packages</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:platforms']">Platforms</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:orgs']">Providers</g:link>
                <g:link class="item" controller="search" action="componentSearch" params="[qbe: 'g:tipps']">Titles</g:link>
            </div>

            <div class="menu">
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:curatoryGroups']">Curatory Groups</g:link>
                <g:link class="item" controller="search" action="componentSearch"
                        params="[qbe: 'g:sources']">Sources</g:link>

            </div>
        </div>
        <sec:ifLoggedIn>

            <div class="item">
                <div class="header">My Components</div>

                <div class="menu">
                    <g:link class="item" controller="group" action="myPackages">My Packages</g:link>
                    <g:link class="item" controller="group" action="myPlatforms">My Platforms</g:link>
                    <g:link class="item" controller="group" action="myProviders">My Providers</g:link>
                    <g:link class="item" controller="group" action="mySources">My Sources</g:link>
                    <g:link class="item" controller="group" action="myTitles">My Titles</g:link>
                </div>

                <div class="menu">
                    <g:link class="item" controller="group" action="myPackageManagement">My Package Management</g:link>
                    <g:link class="item" controller="group" action="myPackagesNeedsAutoUpdates">My Packages due to automatic update</g:link>
                    <g:link class="item" controller="group" action="myAutoUpdateInfos">My Auto Update Infos</g:link>
                </div>
            </div>

            <div class="item">
                <div class="header">Statistics</div>

                <div class="menu">
                    <g:link class="item" controller="home">Statistics</g:link>
                </div>
            </div>
            <sec:ifAnyGranted roles='ROLE_ADMIN,ROLE_EDITOR,ROLE_CONTRIBUTOR'>
                <div class="item">
                    <div class="header">Create</div>

                    <div class="menu">
                        <g:link class="item" controller="create" action="index"
                                params="[tmpl: 'wekb.Package']">Packages</g:link>
                        <g:link class="item" controller="create" action="index"
                                params="[tmpl: 'wekb.Platform']">Platforms</g:link>
                        <g:link class="item" controller="create" action="index"
                                params="[tmpl: 'wekb.Source']">Sources</g:link>
                        <g:link class="item" controller="create" action="index"
                                params="[tmpl: 'wekb.TitleInstancePackagePlatform']">Titles</g:link>
                    </div>
                </div>
            </sec:ifAnyGranted>
            <sec:ifAnyGranted roles="ROLE_ADMIN">
                        <div class="item">
                            <div class="header">Admin Views</div>
                            <div class="menu">
                                <g:link class="item" controller="search" action="componentSearch"
                                        params="[qbe: 'g:users']">User Management</g:link>
                                <g:link class="item" controller="admin" action="jobs">Manage Jobs</g:link>
                                <g:link class="item" controller="admin"
                                        action="manageFTControl">Manage FT Control</g:link>
                                <g:link class="item" controller="admin"
                                        action="packagesChanges">Packages Changes</g:link>
                                <g:link class="item" controller="admin"
                                        action="findPackagesNeedsAutoUpdates">Packages due to automatic update</g:link>
                                <g:link class="item" controller="admin"
                                        action="autoUpdatesFails">Automatic update fails</g:link>
                                <g:link class="item" controller="admin"
                                        action="findPackagesWithTippDuplicates">Packages with Tipp Duplicates</g:link>
                                <g:link class="item" controller="admin"
                                        action="findPackagesAutoUpdatesTippsDiff">Auto Update Packages with Tipp Diff</g:link>
                                <g:link class="item" controller="admin"
                                        action="tippIdentifiersWithSameNameSpace">Title Identifiers with same Identifier Namespace</g:link>
                            </div>
                        </div>
                        <div class="item">
                            <div class="header"> Admin Search</div>
                            <div class="menu">
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
                        <div class="item">
                            <div class="header"> Admin Create
                            </div>
                            <div class="menu">
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
                            </div>
                        </div>
                        <div class="item">
                            <div class="header"> Admin Jobs</div>
                            <div class="menu">
                                <g:link class="item" controller="admin" action="updateTextIndexes"
                                        onclick="return confirm('Are you sure?')">Update Free Text Indexes</g:link>
                                %{--              <g:link class="item" controller="admin" action="resetTextIndexes" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Reset Free Text Indexes</g:link>--}%
                                <g:link class="item" controller="admin" action="recalculateStats"
                                        onclick="return confirm('Are you sure?')">Recalculate Statistics</g:link>
                                <g:link class="item" controller="admin" action="expungeRemovedComponents"
                                        onclick="return confirm('Are you sure?')">Expunge Removed Component</g:link>
                                <g:link class="item" controller="admin" action="cleanupPlatforms"
                                        onclick="return confirm('Are you sure?')">Deprecate Platforms Without URLs</g:link>
                                <g:link class="item" controller="admin" action="ensureUuids"
                                        onclick="return confirm('Are you sure?')">Ensure UUIDs</g:link>
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

                <div class="item">
                    <div class="header">Infos</div>

                    <div class="menu">
                        <g:link class="item" controller="frontend" action="index">Frontend</g:link>
                        <g:link class="item" controller="admin" action="about">Operating environment</g:link>
                    </div>
                </div>

            </sec:ifAnyGranted>
        </sec:ifLoggedIn>

    </div>
    <div class="ui top fixed inverted shrink menu">
        <div class="ui fluid container">
            <a class="launch icon item" id="sidebar-menu-button">
                <i class="content icon"></i>
            </a>
            <div class="ui category search item inverted" id="spotlightSearch" style="flex-grow:1;">
                <div class="ui inverted icon input">
                    <input class="prompt" type="text" placeholder="Search for Packages, Titles, Providers, Platforms...">
                    <i class="search link icon"></i>
                </div>

                <div class="results"></div>
            </div>

            <sec:ifLoggedIn>
                <div class="ui dropdown icon item">
                    <i class="ui icon user"></i>&nbsp; ${springSecurityService.currentUser.displayName ?: springSecurityService.currentUser.username}
                    <i class="dropdown icon"></i>
                    <div class="menu">
                        <g:link controller="home" action="userdash" class="item">My User Dashboard</g:link>
                        <g:link controller="home" action="profile" class="item">My Profile & Preferences</g:link>
                        <g:link controller="home" action="dsgvo" class="item">Privacy Statement</g:link>
                    </div>
                </div>

                <div class="item">
                    <g:link class="ui inverted button" controller="logout"><i class="sign out alternate icon"></i>Logout</g:link>
                </div>
            </sec:ifLoggedIn>

            <div class="right menu">
                <sec:ifNotLoggedIn>
                    <div class="item">
                        <g:link class="ui inverted button" controller="home" action="index"><i
                                class="sign in alternate icon icon"></i>Login</g:link>
                    </div>
                </sec:ifNotLoggedIn>
                <sec:ifAnyGranted roles="ROLE_ADMIN">
                            <div class="item">
                            ${adminService.getNumberOfActiveUsers()} User online
                            </div>
                </sec:ifAnyGranted>

            </div>
        </div>
    </div>

    <div class="pusher shrink" id="main">
        <div class="wekb-content" >
            <main class="ui main fluid container">
                <g:layoutBody/>
            </main>
            <br>
            <br>

            <g:render template="/layouts/footer_semui"/>
        </div>
    </div>
</body>

</html>

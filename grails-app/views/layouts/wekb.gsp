<%@ page import="wekb.utils.ServerUtils; wekb.utils.ServerUtils; wekb.helper.RDStore;" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title><g:layoutTitle default="we:kb"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    <g:javascript> var spotlightSearchUrl="${g.createLink(controller: 'search', action: 'spotlightSearch')}";</g:javascript>
    <g:javascript> var ajaxLookUp="${g.createLink(controller: 'ajaxJson', action: 'lookup')}";</g:javascript>

    <asset:javascript src="wekb.js"/>
    <asset:stylesheet src="wekb.css"/>

</head>

<wekb:serviceInjection/>
<g:set var="currentServer" scope="page" value="${wekb.utils.ServerUtils.getCurrentServer()}"/>
<g:set var="isUserLoggedIn" scope="page" value="${springSecurityService.isLoggedIn()}"/>

<g:if test="${isUserLoggedIn}">
    <g:set var="user" scope="page" value="${springSecurityService.currentUser}"/>
</g:if>

<body class="pushable">
<wekb:serverlabel server="${currentServer}"/>
<nav class="ui left vertical inverted visible menu sidebar ${serverLabel}" id="toc" aria-label="main navigation">
    <g:link controller="public" action="index" class="header item">
        <img alt="Logo wekb" src="${resource(dir: 'images', file: 'logo.svg')}"/>
    </g:link>

    <div class="item">
        <div class="header">News</div>

        <div class="menu">
            <g:link class="item" controller="public" action="wekbNews">we:kb News</g:link>
        </div>
        <div class="header"><g:message code="default.button.search"/></div>

        <div class="menu">
            <g:link class="item" controller="search" action="index"><g:message code="public.allComponents"/></g:link>
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:publicPackages']"><g:message code="public.packages"/></g:link>
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:platforms']"><g:message code="public.platforms"/></g:link>
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:orgs']"><g:message code="public.providers"/></g:link>
            <g:link class="item" controller="search" action="componentSearch" params="[qbe: 'g:tipps']"><g:message code="public.titles"/></g:link>
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:vendors']"><g:message code="public.vendors"/></g:link>
        </div>

        <div class="menu">
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:curatoryGroups', qp_type: RDStore.CURATORY_GROUP_TYPE_PROVIDER.getOID()]"><g:message code="public.curatoryGroups"/></g:link>
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:sources']"><g:message code="public.sources"/></g:link>

        </div>
    </div>
    <g:if test="${isUserLoggedIn}">
        <g:if test="${user.curatoryGroupUsers.size() > 0 && user.showMyComponents()}">
            <div class="item">
                <div class="header"><g:message code="public.myComponents"/></div>

                <div class="menu">
                    <g:link class="item" controller="group" action="myPackages"><g:message code="public.myPackages"/></g:link>
                    <g:link class="item" controller="group" action="myPlatforms"><g:message code="public.myPlatforms"/></g:link>
                    <g:link class="item" controller="group" action="myProviders"><g:message code="public.myProviders"/></g:link>
                    <g:link class="item" controller="group" action="mySources"><g:message code="public.mySources"/></g:link>
                    <g:link class="item" controller="group" action="myTitles"><g:message code="public.myTitles"/></g:link>
                </div>

                <div class="menu">
                    <g:link class="item" controller="group" action="myPackageManagement"><g:message code="public.myPackageManagement"/></g:link>
                    <g:link class="item" controller="group"
                            action="myPackagesNeedsAutoUpdates"><g:message code="public.myPackagesNeedsAutoUpdates"/></g:link>
                    <g:link class="item" controller="group" action="myAutoUpdateInfos"><g:message code="public.myAutoUpdateInfos"/></g:link>
                </div>
            </div>
        </g:if>

        <div class="item">
            <div class="header"><g:message code="public.statistic"/></div>

            <div class="menu">
                <g:link class="item" controller="home" action="statistic"><g:message code="public.statistic"/></g:link>
            </div>
        </div>
        <sec:ifAnyGranted roles='ROLE_ADMIN,ROLE_EDITOR'>
            <div class="item">
                <div class="header"><g:message code="default.button.create.label"/></div>

                <div class="menu">
                    <g:link class="item" controller="create" action="index"
                            params="[tmpl: 'wekb.Package']"><g:message code="public.packages"/></g:link>
                    <g:link class="item" controller="create" action="index"
                            params="[tmpl: 'wekb.Platform']"><g:message code="public.platforms"/></g:link>
                    <g:link class="item" controller="create" action="index"
                            params="[tmpl: 'wekb.KbartSource']"><g:message code="public.sources"/></g:link>
                    <g:link class="item" controller="create" action="index"
                            params="[tmpl: 'wekb.TitleInstancePackagePlatform']"><g:message code="public.titles"/></g:link>
                </div>
            </div>
        </sec:ifAnyGranted>
        <sec:ifAnyGranted roles="ROLE_ADMIN">
            <div class="item">
                <div class="header">Admin Views</div>

                <div class="menu">
                    <g:link class="item" controller="admin" action="index">Admin Dashboard</g:link>
                    <g:link class="item" controller="search" action="componentSearch"
                            params="[qbe: 'g:users']">User Management</g:link>
                    <g:link class="item" controller="admin" action="jobs">Manage Jobs</g:link>
                    <g:link class="item" controller="admin"
                            action="manageFTControl">Manage FT Control</g:link>
                    <g:link class="item" controller="admin"
                            action="packagesChanges">Packages Changes</g:link>
                            <g:link class="item" controller="admin"
                            action="findPackagesWithoutTitles">Packages without Titles</g:link>
                </div>
            </div>


            <div class="item">
                <div class="header">Frontend</div>

                <div class="menu">
                    <g:link class="item" controller="frontend" action="index">Frontend</g:link>
                </div>
            </div>

        </sec:ifAnyGranted>
    </g:if>

</nav>

<div class="ui top fixed inverted shrink menu" role="search">
    <div class="ui fluid container">
        <a class="launch icon item" id="sidebar-menu-button">
            <i class="content icon"></i>
        </a>

        <div class="ui category search item inverted" id="spotlightSearch" style="flex-grow:1;">
            <div class="ui inverted icon input">
                <input class="prompt" type="text" placeholder="${g.message(code: 'public.globalSearch.placeHolder')}">
                <i class="search link icon"></i>
            </div>

            <div class="results"></div>
        </div>

%{--        <div class="ui simple dropdown item">
            <i class="globe alternate icon icon"></i>
            <i class="dropdown icon"></i>
            <div class="menu">
                <g:link class="item" controller="${controllerName}" action="${actionName}" params="${params+[lang: 'en']}"><g:message code="language.en"/></g:link>
                <g:link class="item" controller="${controllerName}" action="${actionName}" params="${params+[lang: 'de']}"><g:message code="language.de"/></g:link>
            </div>
        </div>--}%

        <g:if test="${isUserLoggedIn}">
            <div class="ui dropdown icon item">
                <i class="ui icon user"></i>&nbsp; ${user.displayName ?: user.username}
                <i class="dropdown icon"></i>

                <div class="menu">
                    <g:link controller="home" action="userdash" class="item"><g:message code="public.userdash"/></g:link>
                    <g:link controller="home" action="profile" class="item"><g:message code="public.profile"/></g:link>
                    <g:link controller="home" action="dsgvo" class="item"><g:message code="public.dsgvo"/></g:link>
                </div>
            </div>

            <div class="item">
                <g:link class="ui inverted button" controller="logout"><i
                        class="sign out alternate icon"></i>Logout</g:link>
            </div>
        </g:if>

        <div class="right menu">
            <g:if test="${!isUserLoggedIn}">
                <div class="item">
                    <g:link class="ui inverted button" controller="home" action="index"><i
                            class="sign in alternate icon icon"></i>Login</g:link>
                </div>

            </g:if>
            <g:else>
                <sec:ifAnyGranted roles="ROLE_ADMIN">
                    <div class="item">
                        ${adminService.getNumberOfActiveUsers()} User online
                    </div>
                </sec:ifAnyGranted>
            </g:else>

        </div>
    </div>
</div>

<div class="pusher shrink" id="main">
    <div class="wekb-content">
        <main class="ui main fluid container">
            <g:layoutBody/>
        </main>
        <br>
        <br>

        <g:render template="/layouts/footer"/>
    </div>
</div>
</body>

</html>

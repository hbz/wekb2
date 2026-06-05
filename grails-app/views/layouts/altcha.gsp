<%@ page import="wekb.utils.ServerUtils; wekb.utils.ServerUtils; wekb.helper.RDStore; wekb.RefdataValue;" %>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title><g:layoutTitle default="we:kb"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="In the we:kb (pronounced wekb), the providers manage their current electronic sales units and meta-information themselves.">
    <g:if test="${ServerUtils.getCurrentServer() == ServerUtils.SERVER_PROD}">
        <meta name="google-site-verification" content="-kK1UKmjJAt_9QnZg6YL-96yI65sls58pHyheOOrS0M">
    </g:if>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    <script src="/assets/jquery-3.6.0.min.js"></script>
    <script src="/assets/altcha/altcha.min.js" async defer type="module"></script>

    <asset:stylesheet src="wekb.css"/>
</head>

<wekb:serviceInjection/>
<g:set var="currentServer" scope="page" value="${wekb.utils.ServerUtils.getCurrentServer()}"/>

<body class="pushable">
<wekb:serverlabel server="${currentServer}"/>
<!-- skip to main content / for screenreader -->


<nav class="ui left vertical inverted visible menu sidebar ${serverLabel}" id="toc" aria-label="main navigation" >

    <g:link controller="public" action="index" class="header item">
        <img alt="Logo wekb" src="${resource(dir: 'images', file: 'logo.svg')}"/>
    </g:link>
    <nav class="la-skipLink" role="navigation" aria-label="${g.message(code: 'accessibility.jumpLink')}">
        <p>
            <a href="#jumper" class="la-screenReaderText">${g.message(code: 'accessibility.jumpToMain')}</a>
        </p>
    </nav>
    <div class="item">
        <div class="header">News</div>

        <div class="menu">
            <g:link class="item we-link" controller="public" action="wekbNews">we:kb News</g:link>
        </div>
    </div>
    <div class="item">
        <div class="header"><g:message code="default.button.search"/></div>
        <div class="menu">
            <g:link class="item we-link" controller="search" action="index"><g:message code="public.allComponents"/></g:link>
            <g:link class="item we-link" controller="search" action="componentSearch"
                    params="[qbe: 'g:publicPackages']"><g:message code="public.packages"/></g:link>
            <g:link class="item we-link" controller="search" action="componentSearch"
                    params="[qbe: 'g:platforms']"><g:message code="public.platforms"/></g:link>
            <g:link class="item we-link" controller="search" action="componentSearch"
                    params="[qbe: 'g:orgs']"><g:message code="public.providers"/></g:link>
            <g:link class="item we-link" controller="search" action="componentSearch" params="[qbe: 'g:tipps', qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_CURRENT.id]"><g:message code="public.titles"/></g:link>
            <g:link class="item we-link" controller="search" action="componentSearch"
                    params="[qbe: 'g:vendors']"><g:message code="public.vendors"/></g:link>
        </div>
        <div class="menu">
            <g:link class="item we-link" controller="search" action="componentSearch"
                    params="[qbe: 'g:curatoryGroups', qp_type: RDStore.CURATORY_GROUP_TYPE_PROVIDER.getOID()]"><g:message code="public.curatoryGroups"/></g:link>
            <g:link class="item we-link" controller="search" action="componentSearch"
                    params="[qbe: 'g:sources']"><g:message code="public.sources"/></g:link>
        </div>
    </div>

</nav>

<div class="ui top fixed inverted shrink menu" role="search">
    <div class="ui fluid container">
        <a class="launch icon item" id="sidebar-menu-button">
            <i class="content icon"></i>
        </a>

        <div class="ui category search item inverted disabled" id="spotlightSearch" style="flex-grow:1;">
            <div class="ui inverted icon input">
                <input class="prompt" type="text" aria-label="${g.message(code: 'public.globalSearch.placeHolder')}" placeholder="${g.message(code: 'public.globalSearch.placeHolder')}">
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

        <div class="right menu">
            <g:set var="backUrl" value="${request.forwardURI ?: request.requestURI}${request.queryString ? '?' + request.queryString : ''}" />

            <div class="item">
                <g:link class="ui inverted button" controller="login" action="auth"
                        params="[( 'spring-security-redirect' ): URLDecoder.decode(backUrl.replace('/altcha/prompt?origin=', ''), 'UTF-8')]">
                <i class="sign in alternate icon icon"></i>Login</g:link>
            </div>
        </div>
    </div>
</div>

<div style="position: absolute; right: 0"  id="jumper"></div>

<div class="pusher shrink" id="main">
    <div class="wekb-content">
        <main class="ui main fluid container">
            <g:layoutBody/>
        </main>
        <g:render template="/layouts/footer"/>
    </div>
</div>

%{-- global loading indicator --}%
<div class="ui page dimmer" id="globalLoadingIndicator">
    <div class="ui large red text loader">Loading</div>
</div>
<script>
    $('.we-link').click(function(e) {
        $('#globalLoadingIndicator').addClass('active');
    });
</script>
</body>

</html>

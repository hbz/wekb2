<%@ page import="wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>My User Dashboard</title>
</head>

<body>
<h1 class="ui header">My User Dashboard (${request.user?.displayName ?: request.user?.username})</h1>

<div class="ui segment">
    <h3 class="ui header">Saved Searchs</h3>

    <div class="content">
        <g:if test="${saved_items.size() > 8}">
            <g:link controller="savedItems" action="index">All Saved Searchs (${saved_items.size()})</g:link>
            <g:set var="saved_items" value="${saved_items.take(8)}"/>
            <br><br>
        </g:if>

        <g:each in="${saved_items}" var="itm">
            <g:set var="savedParams" value="${itm.toParam()}"/>
            <div class="col-md-3 center-block center-text">
                <i class="fa fa-search fa-fw"></i>
                <g:link controller="${savedParams.controller ?: 'search'}" action="${savedParams.action ?: 'index'}"
                        params="${savedParams}">${itm.name}</g:link>
            </div>
        </g:each>
    </div>
</div>

<div class="ui segment">
    <h3 class="ui header">Most recently updated Watched Components</h3>

    <div class="content">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:userWatchedComponents', inline: true]"
                id="">User Watched Components</g:link>
    </div>
</div>

%{--<div class="ui segment">
    <h3 class="ui header">Finished Upload Jobs</h3>

    <div class="content">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:userJobs', inline: true]"
                id="">Finished Upload Jobs</g:link>
    </div>
</div>--}%

</body>
</html>

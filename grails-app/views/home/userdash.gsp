<%@ page import="wekb.helper.RCConstants" %>
<wekb:serviceInjection/>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>we:kb : My User Dashboard</title>
</head>

<body>
<h1 class="ui header">My User Dashboard (${springSecurityService.currentUser?.displayName ?: springSecurityService.currentUser?.username})</h1>

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

</body>
</html>

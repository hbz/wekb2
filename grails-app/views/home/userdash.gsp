<%@ page import="wekb.helper.RCConstants" %>
<wekb:serviceInjection/>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : My User Dashboard</title>
</head>

<body>
<h1 class="ui header">My User Dashboard (${springSecurityService.currentUser?.displayName ?: springSecurityService.currentUser?.username})</h1>

<div class="ui segment">
    <h3 class="ui header">Saved Searchs (${saved_items.size()})</h3>

    <div class="content">
        <g:if test="${saved_items.size() > 10 && !removeMax}">
            <g:link controller="home" action="userdash" params="[removeMax: true]">Show all Saved Searchs (${saved_items.size()})</g:link>
            <g:set var="saved_items" value="${saved_items.take(10)}"/>
            <br><br>
        </g:if>

        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th>#</th>
                <semui:sortableColumn property="name" title="Name of Search"/>
                <semui:sortableColumn property="dateCreated" title="Date Created"/>
                <th>Jump to search</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
        <g:each in="${saved_items}" var="itm" status="i">
            <g:set var="savedParams" value="${itm.toParam()}"/>
            <tr>
                <td>${i+1}</td>
                <td>
                    <semui:xEditable owner="${itm}" field="name" required="true"/>
                </td>
                <td><g:formatDate format="${message(code: 'default.date.format.noZ')}"
                                  date="${itm.dateCreated}"/>
                </td>
                <td>
                    <g:link controller="${savedParams.controller ?: 'search'}" action="${savedParams.action ?: 'index'}"
                            params="${savedParams}" class="ui primary button" target="_blank">
                        <i class="icon search"></i>
                        ${itm.name}</g:link>
                </td>
                <td><g:link controller="home" action="userdash" params="[removeSearch: true, search_id: itm.id]" class="ui negative button icon"><i class="icon trash"></i>Remove Search</g:link></td>
            </tr>
        </g:each>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>

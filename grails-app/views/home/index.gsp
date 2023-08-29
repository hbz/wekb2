<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Welcome</title>
</head>

<body>
<h1 class="ui header">Welcome to we:kb</h1>

<div class="ui segment">

    <h2 class="ui header">we:kb News <div class="ui black label">
        Changes in the last <b>14</b> days.
    </div></h2>


    <div class="ui two column grid">

        <div class="column">
            <div class="ui fluid card" style="margin-bottom:0">
                <div class="content">
                    <div class="header">
                        Packages
                    </div>
                </div>

                <div class="content">
                    <div class="description">

                        <div class="ui styled fluid accordion">
                            <div class="title">
                                <i class="dropdown icon"></i>
                                New: ${news.package.countNewInDB}
                            </div>
                            <div class="content">
                                <table class="ui selectable striped sortable celled table">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Name</th>
                                        <th>Curatory Group</th>
                                        <th>Date Created</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <g:each in="${news.package.newInDB}" var="obj" status="i">
                                        <tr>
                                            <td>${i + 1}</td>
                                            <td><g:link controller="resource"
                                                        action="show"
                                                        id="${obj.getOID()}">
                                                ${obj.name}
                                            </g:link></td>
                                            <td>
                                                <div class="ui bulleted list">
                                                    <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                        <div class="item">
                                                            ${curatoryGroup.name}
                                                        </div>
                                                    </g:each>
                                                </div>
                                            </td>
                                            <td>
                                                <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                              date="${obj.dateCreated}"/>
                                            </td>
                                        </tr>
                                    </g:each>
                                    </tbody>
                                </table>
                            </div>
                            <div class="title">
                                <i class="dropdown icon"></i>
                                Changes: ${news.package.countLastUpdatedInDB}
                            </div>
                            <div class="content">
                                <table class="ui selectable striped sortable celled table">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Name</th>
                                        <th>Curatory Group</th>
                                        <th>Last Updated</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <g:each in="${news.package.lastUpdatedInDB}" var="obj" status="i">
                                        <tr>
                                            <td>${i + 1}</td>
                                            <td><g:link controller="resource"
                                                        action="show"
                                                        id="${obj.getOID()}">
                                                ${obj.name}
                                            </g:link></td>
                                            <td>
                                                <div class="ui bulleted list">
                                                    <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                        <div class="item">
                                                            ${curatoryGroup.name}
                                                        </div>
                                                    </g:each>
                                                </div>
                                            </td>
                                            <td>
                                                <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                              date="${obj.lastUpdated}"/>
                                            </td>
                                        </tr>
                                    </g:each>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                </div>
            </div>
        </div>
    </div>

    <div class="column">
        <div class="ui fluid card" style="margin-bottom:0">
            <div class="content">
                <div class="header">
                    Platforms
                </div>
            </div>

            <div class="content">
                <div class="description">
                    <div class="ui styled fluid accordion">
                        <div class="title">
                            <i class="dropdown icon"></i>
                            New: ${news.platform.countNewInDB}
                        </div>
                        <div class="content">
                            <table class="ui selectable striped sortable celled table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Curatory Group</th>
                                    <th>Date Created</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${news.platform.newInDB}" var="obj" status="i">
                                    <tr>
                                        <td>${i + 1}</td>
                                        <td><g:link controller="resource"
                                                    action="show"
                                                    id="${obj.getOID()}">
                                            ${obj.name}
                                        </g:link></td>
                                        <td>
                                            <div class="ui bulleted list">
                                                <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                    <div class="item">
                                                        ${curatoryGroup.name}
                                                    </div>
                                                </g:each>
                                            </div>
                                        </td>
                                        <td>
                                            <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                          date="${obj.dateCreated}"/>
                                        </td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>
                        <div class="title">
                            <i class="dropdown icon"></i>
                            Changes: ${news.platform.countLastUpdatedInDB}
                        </div>
                        <div class="content">
                            <table class="ui selectable striped sortable celled table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Curatory Group</th>
                                    <th>Last Updated</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${news.platform.lastUpdatedInDB}" var="obj" status="i">
                                    <tr>
                                        <td>${i + 1}</td>
                                        <td><g:link controller="resource"
                                                    action="show"
                                                    id="${obj.getOID()}">
                                            ${obj.name}
                                        </g:link></td>
                                        <td>
                                            <div class="ui bulleted list">
                                                <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                    <div class="item">
                                                        ${curatoryGroup.name}
                                                    </div>
                                                </g:each>
                                            </div>
                                        </td>
                                        <td>
                                            <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                          date="${obj.lastUpdated}"/>
                                        </td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="ui two column grid">

    <div class="column">
        <div class="ui fluid card" style="margin-bottom:0">
            <div class="content">
                <div class="header">
                    Providers
                </div>
            </div>

            <div class="content">
                <div class="description">

                    <div class="ui styled fluid accordion">
                        <div class="title">
                            <i class="dropdown icon"></i>
                            New: ${news.org.countNewInDB}
                        </div>
                        <div class="content">
                            <table class="ui selectable striped sortable celled table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Curatory Group</th>
                                    <th>Date Created</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${news.org.newInDB}" var="obj" status="i">
                                    <tr>
                                        <td>${i + 1}</td>
                                        <td><g:link controller="resource"
                                                    action="show"
                                                    id="${obj.getOID()}">
                                            ${obj.name}
                                        </g:link></td>
                                        <td>
                                            <div class="ui bulleted list">
                                                <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                    <div class="item">
                                                        ${curatoryGroup.name}
                                                    </div>
                                                </g:each>
                                            </div>
                                        </td>
                                        <td>
                                            <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                          date="${obj.dateCreated}"/>
                                        </td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>
                        <div class="title">
                            <i class="dropdown icon"></i>
                            Changes: ${news.org.countLastUpdatedInDB}
                        </div>
                        <div class="content">
                            <table class="ui selectable striped sortable celled table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Curatory Group</th>
                                    <th>Last Updated</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${news.org.lastUpdatedInDB}" var="obj" status="i">
                                    <tr>
                                        <td>${i + 1}</td>
                                        <td><g:link controller="resource"
                                                    action="show"
                                                    id="${obj.getOID()}">
                                            ${obj.name}
                                        </g:link></td>
                                        <td>
                                            <div class="ui bulleted list">
                                                <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                    <div class="item">
                                                        ${curatoryGroup.name}
                                                    </div>
                                                </g:each>
                                            </div>
                                        </td>
                                        <td>
                                            <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                          date="${obj.lastUpdated}"/>
                                        </td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

    <div class="column">
        <div class="ui fluid card" style="margin-bottom:0">
            <div class="content">
                <div class="header">
                    Titles
                </div>
            </div>

            <div class="content">
                <div class="description">
                    <div class="ui styled fluid accordion">
                        <div class="title">
                            <i class="dropdown icon"></i>
                            New: ${news.titleinstancepackageplatform.countNewInDB}
                        </div>
                        <div class="content">
                            <table class="ui selectable striped sortable celled table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Curatory Group</th>
                                    <th>Date Created</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${news.titleinstancepackageplatform.newInDB}" var="obj" status="i">
                                    <tr>
                                        <td>${i + 1}</td>
                                        <td><g:link controller="resource"
                                                    action="show"
                                                    id="${obj.getOID()}">
                                            ${obj.name}
                                        </g:link></td>
                                        <td>
                                            <div class="ui bulleted list">
                                                <g:each in="${obj.pkg.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                    <div class="item">
                                                        ${curatoryGroup.name}
                                                    </div>
                                                </g:each>
                                            </div>
                                        </td>
                                        <td>
                                            <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                          date="${obj.dateCreated}"/>
                                        </td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>
                        <div class="title">
                            <i class="dropdown icon"></i>
                            Changes: ${news.titleinstancepackageplatform.countLastUpdatedInDB}
                        </div>
                        <div class="content">
                            <table class="ui selectable striped sortable celled table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Curatory Group</th>
                                    <th>Last Updated</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${news.titleinstancepackageplatform.lastUpdatedInDB}" var="obj" status="i">
                                    <tr>
                                        <td>${i + 1}</td>
                                        <td><g:link controller="resource"
                                                    action="show"
                                                    id="${obj.getOID()}">
                                            ${obj.name}
                                        </g:link></td>
                                        <td>
                                            <div class="ui bulleted list">
                                                <g:each in="${obj.pkg.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                    <div class="item">
                                                        ${curatoryGroup.name}
                                                    </div>
                                                </g:each>
                                            </div>
                                        </td>
                                        <td>
                                            <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                          date="${obj.lastUpdated}"/>
                                        </td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>

<g:if test="${rssFeed}">
    <div class="ui segment">

        <h2 class="ui header">we:kb Wiki News</h2>

        <div class="ui items">
            <g:each in="${rssFeed.entry}" var="feedEntry" status="i">
                <div class="item">
                    <div class="content">
                        <div class="header">
                            ${i + 1}. ${feedEntry.title.text()}
                        </div>

                        <div class="meta">
                            News object added by <b>${feedEntry.author.name.text()}</b> on <span
                                class="stay"><g:formatDate
                                    format="${message(code: 'default.date.format.notime')}"
                                    date="${new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").parse(feedEntry.updated.text())}"/></span>
                        </div>

                        <g:if test="${feedEntry.summary}">
                            <div class="description">
                                <div class="ui segment">
                                    ${raw(feedEntry.summary.text())}
                                </div>
                            </div>
                        </g:if>
                    </div>
                </div>
                <br>
                <br>
            </g:each>
        </div>

    </div>
</g:if>
</body>
</html>

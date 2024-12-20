<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Welcome</title>
</head>

<body>
<h1 class="ui header">Welcome to we:kb</h1>

<g:if test="${rssFeed}">

    <h2 class="ui header">we:kb Wiki News</h2>

    <div class="ui items">
        <g:each in="${rssFeed.entry}" var="feedEntry" status="i">
            <div class="item">
                <div class="content">
                    <div class="header">
                        ${i + 1}. ${feedEntry.title.text()}
                    </div>

                    <div class="meta">
                        News object added by <strong>${feedEntry.author.name.text()}</strong> on <span
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
</g:if>


<g:if test="${news}">
<g:render template="/templates/wekbNews"/>
</g:if>



</body>
</html>

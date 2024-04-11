<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb News</title>
</head>

<body>
<h1 class="ui header">Welcome to we:kb</h1>

<g:if test="${news}">
    <g:render template="/templates/wekbNews"/>
</g:if>

</body>
</html>

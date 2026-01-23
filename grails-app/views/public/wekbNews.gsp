<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <meta name="description" content="In the we:kb (pronounced wekb), the providers manage their current electronic sales units and meta-information themselves.">
    <title>we:kb | wekb News</title>
</head>

<body>
<h1 class="ui header">Welcome to we:kb</h1>

<g:if test="${news}">
    <g:render template="/templates/wekbNews"/>
</g:if>

</body>
</html>

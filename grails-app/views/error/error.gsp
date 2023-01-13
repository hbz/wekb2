<%@ page import="wekb.utils.ServerUtils; wekb.utils.ServerUtils; wekb.utils.ServerUtils;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname" default="we:kb"/>:Error</title>
</head>

<body>
<div class="ui tall stacked segment">
        <div>
            <span class="ui black label huge">${code}</span>
        </div>
    <div class="ui icon header">
        <i class="ambulance icon"></i>
        <h2 class="ui header">
            ${message(code: "default.error.exception")}
        </h2>
    </div>

    <p><strong>${request.forwardURI}</strong></p>
    <br />
    <br />
    <g:if test="${exception}">
        <p>${exception.message}</p>
        <br/>
        <p>
            <a href="mailto:laser@hbz-nrw.de?${mailString}">
                Send Mail to support
            </a>
        </p>
    </g:if>
    <br />
    <br />

    <p>
        <button class="ui black button" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
    </p>

</div>

<g:if test="${wekb.utils.ServerUtils.getCurrentServer() == wekb.utils.ServerUtils.SERVER_DEV}">
    <g:renderException exception="${exception}"/>
</g:if>
<g:elseif env="development">
    <g:renderException exception="${exception}"/>
</g:elseif>

</body>
</html>

<%@ page import="wekb.utils.ServerUtils; wekb.utils.ServerUtils; wekb.utils.ServerUtils;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Error</title>
</head>

<body>
<div class="ui tall stacked segment">
        <div>
            <span class="ui primary  label huge">${code}</span>
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
        <sec:ifLoggedIn>
            <p>${exception.message}</p>
            <br/>
        </sec:ifLoggedIn>

        <p>
            <a href="mailto:wekb@hbz-nrw.de?${mailString}">
                Send Mail to support
            </a>
        </p>

    </g:if>
    <br />
    <br />

    <p>
        <button class="ui primary button" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
    </p>

</div>

<g:if test="${wekb.utils.ServerUtils.getCurrentServer() == wekb.utils.ServerUtils.SERVER_DEV}">
    <sec:ifLoggedIn>
    <g:renderException exception="${exception}"/>
    </sec:ifLoggedIn>
</g:if>
<g:elseif env="development">
    <g:renderException exception="${exception}"/>
</g:elseif>

</body>
</html>

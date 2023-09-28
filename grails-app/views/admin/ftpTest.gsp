<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : FTP Test</title>
</head>

<body>
<h1 class="ui header"><i class="tools icon"></i> FTP Test</h1>

<g:form action="ftpTest" method="post" class="ui form">
    <div class="field">
        <label>Server</label>
        <input type="text" name="serverAddress" value="${params.serverAddress}">
    </div>
    <div class="field">
        <label>Username</label>
        <input type="text" name="username" value="${params.username}">
    </div>
    <div class="field">
        <label>Password</label>
        <input type="text" name="password" value="${params.password}">
    </div>

    <button class="ui button" type="submit">Check connection</button>
</g:form>

</body>
</html>
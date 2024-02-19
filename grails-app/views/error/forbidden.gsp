<!doctype html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Forbidden</title>
</head>

<body>

<div class="ui tall stacked segment">
    <div>
        <span class="ui black label huge">${code}</span>
    </div>
    <div class="ui icon header">
        <i class="exclamation triangle icon"></i>
        <h2 class="ui header">
            Forbidden
        </h2>
    </div>

    <p>You do not have permission to view the requested page.</p>
    <br />
    <br />
    <p><strong>${request.forwardURI}</strong></p>
    <br />
    <br />
    <p>
        <button class="ui primary button" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
    </p>
</div>
</body>
</html>

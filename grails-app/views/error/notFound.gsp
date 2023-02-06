<!doctype html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Page Not Found</title>
</head>

<body>

<div class="ui tall stacked segment">
    <div>
        <span class="ui black label huge">${code}</span>
    </div>
    <div class="ui icon header">
        <i class="eye slash icon"></i>
        <h2 class="ui header">
            Page not found!
        </h2>
    </div>

    <p><strong>${request.forwardURI}</strong></p>

    <br />
    <br />

    <p>
        <button class="ui black button" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
    </p>
</div>
</body>
</html>

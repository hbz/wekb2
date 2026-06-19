<%@ page import="wekb.system.AltchaClient" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="layout" content="altcha"/>
    <title>we:kb | wekb</title>
</head>
<body>
    <div class="ui card">
        <g:render template="/templates/altchaForm" model="[altchaForm: [origin: params.origin, startpage: false]]" />
    </div>
</body>
</html>
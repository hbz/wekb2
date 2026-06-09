<%@ page import="wekb.system.AltchaClient" %>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="layout" content="altcha"/>
    <title>we:kb | wekb</title>
</head>
<body>
    <div class="ui segment">
        <g:render template="/templates/altchaForm" model="[altchaFormOrigin: params.origin]" />
    </div>
</body>
</html>
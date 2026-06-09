<%@ page import="wekb.system.AltchaClient" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="altcha"/>
    <title>we:kb | wekb</title>
</head>
<body>
    <g:render template="number-chart-hero"/>

    <div class="container">
        <h1>WEKB</h1>
        <h2>Startseite</h2>
        <p>todo ..</p>

        <div class="ui segment">
            <g:render template="/templates/altchaForm" model="[altchaFormOrigin: origin]"/>
        </div>
    </div>
</body>
</html>

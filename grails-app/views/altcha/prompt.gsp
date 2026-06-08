<%@ page import="wekb.system.AltchaClient" %>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="layout" content="altcha"/>
    <title>we:kb | wekb</title>
</head>
<body>
    <h1 class="ui header">Hello again ..</h1>

    <div class="ui segment">
        <form id="altcha-form" class="ui form"
              action="${createLink(controller: 'altcha', action: 'submit')}" method="post">
            <input type="hidden" name="origin" value="${params.origin}" />
            <div class="field">
                Aufgrund des massiven Anstiegs der Anfragen von Webcrawlern und KI-Bots haben wir beschlossen, .. bla bla.
                <br />
                Diese Maßnahme soll weiterhin die Verfügbarkeit und Leistung des Systems gewährleisten.
            </div>
            <div class="field">
                <altcha-widget
                        challenge="${createLink(controller: 'altcha', action: 'challenge', absolute:true)}"
                        name="${AltchaClient.getWidgetToken(request)}"
                        auto="onsubmit"
                        display="invisible"
                ></altcha-widget>
            </div>
            <div class="field">
                <button class="altcha-button we-link"> Ich bin ein Mensch! </button>
            </div>
        </form>
    </div>
</body>
</html>
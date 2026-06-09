<%@ page import="wekb.system.AltchaClient" %>

<form action="${createLink(controller: 'altcha', action: 'submit')}" method="post" id="altcha-form" class="ui form">
    <input type="hidden" name="origin" value="${altchaFormOrigin}" />
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
        <button class="altcha-button we-link"> Finde ich gut; ich bin ein Mensch! </button>
    </div>
</form>
<%@ page import="wekb.system.AltchaClient" %>

<form action="${createLink(controller: 'altcha', action: 'submit')}" method="post"
      id="altcha-form" class="ui form${altchaForm.startpage ? '' : ' content'}"
>
    <input type="hidden" name="origin" value="${altchaForm.origin}" />
    <g:if test="${! altchaForm.startpage}">
        <div class="field">
            <i class="robot large grey icon"></i>
            <br />
            <br />
            Due to the massive increase in requests from web crawlers and AI bots, we have decided to... blah blah.
            <br />
            This measure is intended to ensure the continued availability and performance of the system.
        </div>
    </g:if>
    <div class="field">
        <altcha-widget
                challenge="${createLink(controller: 'altcha', action: 'challenge', absolute:true)}"
                name="${AltchaClient.getWidgetToken(request)}"
                auto="onsubmit"
                display="invisible"
        ></altcha-widget>
    </div>
    <div class="field">
        <button class="ui fluid ${altchaForm.startpage ? 'huge ' : ''}button we-link"> Great - let's search! </button>
    </div>
</form>
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
            This protection against web crawlers and AI bots is intended to ensure the continuous availability and performance of the system.
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
        <g:if test="${altchaForm.startpage}">
            <button class="ui fluid huge button we-link"> ${message(code: 'public.searchPackages')} </button>
        </g:if>
        <g:else>
            <button class="ui fluid button we-link"> Yes, i'm human! </button>
        </g:else>
    </div>
</form>
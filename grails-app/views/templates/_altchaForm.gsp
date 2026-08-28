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
    <g:if test="${altchaForm.startpage}">
        <div class="field" style="margin-left: 10rem">
            <a href="/search/componentSearch?qbe=g:publicPackages" class="ui big blue button">
            <i class="search icon"></i>
            Search Now</a>
        </div>
    </g:if>
    <g:else>
        <div class="field">
            <button class="ui fluid button we-link"> Yes, i'm human! </button>
        </div>
    </g:else>
</form>
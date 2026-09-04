<%@ page import="wekb.system.AltchaClient" %>

<form action="${createLink(controller: 'altcha', action: 'submit')}" method="post"
      id="altcha-form" class="ui form${altchaForm.startpage ? '' : ' content'}"
>
    <g:if test="${! altchaForm.startpage}">
        <input type="hidden" name="origin" value="${altchaForm.origin}" />
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
            <button
                    class="ui  blue big icon button we-link"
                    style="margin: 2rem 2rem 1rem 3rem;"
                    type="submit"
                    name="origin"
                    value="/search/componentSearch?qbe=g%3ApublicPackages">
                <i class="search icon"></i>
                Search we:kb
            </button>
            <g:render template="/templates/statistic"/>
        </g:if>
        <g:else>
            <button class="ui fluid button we-link"> Yes, i'm human! </button>
        </g:else>
    </div>
</form>
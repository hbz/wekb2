<%@ page import="wekb.utils.ServerUtils; wekb.system.AltchaClient; wekb.ui.Icon;" %>
<wekb:serviceInjection/>

<!DOCTYPE html>
<html lang="en">
<head>
    <g:if test="${showAltcha}">
        <meta name="layout" content="altcha"/>
    </g:if>
    <g:else>
        <meta name="layout" content="wekb"/>
    </g:else>

    <title>we:kb | wekb</title>

</head>

<body>
<style>

.wekb-content {
    flex: 1 1 auto;
    display: flex;
    overflow: visible;

    background-color: #fff;

    background-image:
            radial-gradient(
                    ellipse at 25% 45%,
                    rgba(242, 113, 28, 0.09) 0%,
                    transparent 48%
            ),
            radial-gradient(
                    ellipse at 88% 42%,
                    rgba(33, 133, 208, 0.08) 0%,
                    transparent 50%
            );
}

.wekb-content > .ui.main.container {
    flex: 1 1 auto;
    display: flex;
    overflow: visible;
}

.full-height-grid {
    flex: 1 1 auto;
    min-height: auto;
    margin-top: 0 !important;
    margin-bottom: 0 !important;
}
.wekb-columns {
    align-items: stretch;
}
.news-column {
    display: flex !important;
    flex-direction: column;
    min-height: 0;
    overflow: hidden;
    align-items: flex-start;
    padding-left: 2rem!important;
    padding-right: 2rem!important;

}
.news-column-inner {
    position: absolute;
    inset: 1rem;
    overflow: hidden;
    margin: 3rem;
    padding: 1rem;

    background: rgba(255, 255, 255, 1);

    border: 1px solid rgba(34, 36, 38, 0.07);
    border-radius: 1rem;

    box-shadow:
            0 0.4rem 1.5rem rgba(34, 36, 38, 0.045);
}

.news-feed {
    flex: 1 1 auto;
    min-height: 0;
    overflow: hidden;
}

.ui.feed > .event > .content .extra {
    margin: 0.1em 0 0;
}

.ui.button.news-more {
    width: auto;
    margin: 0 0 30px 50px;
}

.ui.connected.feed > .event.last-visible::before {
    border-left: none;
}

.ui.feed > .event > .label .icon {
    font-size: 1.3em;
}
.hero-chart-column {
    flex: 1 1 auto;
}
</style>
<g:set var="objectConfig" value="${[
        'package' : [
                icon   : Icon.PACKAGE,
                color  : 'package',
                tooltip: 'Package'
        ],
        'platform': [
                icon   : Icon.PLATFORM,
                color  : 'platform',
                tooltip: 'Platform'
        ],
        'vendor'  : [
                icon   : Icon.VENDOR,
                color  : 'vendor',
                tooltip: 'Library Suppliers'
        ],
        'org'     : [
                icon   : Icon.PROVIDER,
                color  : 'provider',
                tooltip: 'Provider'
        ]
]}"/>

<div class="ui stackable grid full-height-grid">
    <aside class="four wide column news-column">
        <div class="news-column-inner">
            <h3 class="ui header">we:kb News</h3>
            <g:if test="${allNews}">
                <div class="ui connected feed news-feed">
                    <g:each in="${allNews}" var="item">
                        <article class="event">
                            <div class="label" data-tooltip="${objectConfig[item.object]?.tooltip}" data-position="top left">
                                <i class="inverted circular wekb-${objectConfig[item.object]?.color} ${objectConfig[item.object]?.icon}"
                                   aria-hidden="true"></i>
                            </div>
                            <div class="content">
                                <div class="date">
                                    <g:formatDate
                                            format="${message(code: 'default.date.format')}"
                                            date="${item.date}"/>
                                </div>
                                <div class="summary">
                                    <g:link controller="resource"
                                            action="show"
                                            id="${item.event.getOID()}">
                                        ${item.event}
                                    </g:link>
                                    <label class="ui tiny black label">
                                        <g:if test="${item.changeType == 'new'}">
                                            NEW
                                        </g:if>
                                        <g:else>
                                            CHANGED
                                        </g:else>
                                    </label>
                                </div>
                                <div class="extra text">
                                    <g:if test="${item.provider}">
                                        <g:link controller="resource"
                                                action="show"
                                                id="${item.provider.getOID()}">
                                            ${item.provider.name}
                                        </g:link>
                                    </g:if>
                                </div>
                            </div>
                        </article>
                    </g:each>
                </div>
                <g:link class="ui black basic button news-more" controller="public"
                        action="wekbNews">More News</g:link>
            </g:if>

            <g:else>
                <div class="ui info message">
                    There are currently no new or updated entries.
                </div>
            </g:else>
        </div>
    </aside>
    <main class="twelve wide column hero-chart-column">
        <section class="hero-claim">
            <h1>Provider Tool <span>we:kb&nbsp;</span>
            </h1>
            <div style="display: flex;" >
                <p>Provider-Curated Knowledge Base – Freely available under CC0</p>

            </div>
        </section>
        <g:if test="${showAltcha}">
            <g:render template="/templates/altchaForm" model="[altchaForm: [origin: origin, startpage: true]]"/>
        </g:if>
        <g:else>
                <a href="/search/componentSearch?qbe=g:publicPackages"
                   style="margin: 2rem 2rem 1rem 3rem;"
                   class="ui big blue icon button">
                    <i class="search icon"></i>
                    Search we:kb</a>
                <g:if test="${ServerUtils.getCurrentServer() in [ServerUtils.SERVER_LOCAL, ServerUtils.SERVER_DEV] && AltchaClient.isValid(request)}">%{-- DEBUG/TESTING --}%
                    <a href="/altcha/revoke" class="ui big orange button we-link">REVOKE ALTCHA TOKEN</a>
                </g:if>
                <g:render template="/templates/statistic"/>

        </g:else>
    </main>
</div>
</body>
</html>

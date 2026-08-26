<%@ page import="wekb.utils.ServerUtils; wekb.system.AltchaClient; wekb.ui.Icon;" %>
<wekb:serviceInjection/>

<!DOCTYPE html>
<html lang="en">
<head>
    <g:if test="${showAltcha}">
        <meta name="layout" content="altcha" />
    </g:if>
    <g:else>
        <meta name="layout" content="wekb" />
    </g:else>

    <title>we:kb | wekb</title>

</head>
<body>
<style>

.wekb-content > .ui.main.container {
    flex: 1;
    display: flex;
}
.wekb-columns {
    flex: 1;
    display: flex;
}

.wekb-columns > aside {
    flex: 0 0 25%;
    background: #eee;
}

.wekb-columns > main {
    flex: 1;
}

.wekb-content > footer {
    flex: 0 0 auto;
}
</style>
    <g:set var="objectIcons" value="${[
            package : Icon.PACKAGE,
            platform: Icon.PLATFORM,
            provider: Icon.PROVIDER,
            vendor  : Icon.VENDOR,
            org     : Icon.ORG
    ]}" />
    <g:set var="objectColors" value="${[
            package : 'package',
            platform: 'platform',
            provider: 'provider',
            vendor  : 'vendor',
            org     : 'org'
    ]}" />

%{--    <div class="container">
        <div class="ui main fluid container">--}%
            <div class="ui stackable grid full-height-grid">
                <aside class="four wide column" style="background-color:#f9fafb;border-right: 1px solid rgba(34, 36, 38, .15);">
                    <h3 class="ui header">we:kb News</h3>
                    <g:if test="${allNews}">
                        <div class="ui connected feed news-feed">

                            <g:each in="${allNews}" var="item">
                                <article class="event">
                                    <div class="label">
                                        <i class="inverted circular wekb-${objectColors[item.object] ?: 'red'} ${objectIcons[item.object] ?: 'info circle'} icon"
                                           aria-hidden="true"></i>
                                    </div>

                                    <div class="content">
                                        ${item.event}
                                    </div>
                                </article>
                            </g:each>

                        </div>
                    </g:if>
                    <g:else>
                        <div class="ui info message">
                            Derzeit liegen keine neuen oder geänderten Inhalte vor.
                        </div>
                    </g:else>
%{--                    <div class="ui connected feed">
                        <div class="event">
                            <div class="label" data-tooltip="Package" data-position="top left">
                                <i class="orange circular inverted gift icon"></i>
                            </div>
                            <div class="content">
                                <div class="date">
                                    23.07.2026
                                </div>
                                <div class="summary">
                                    Inlibra Paket C.H.Beck Jura Wirtschaftsrecht 2026 I <label class="ui tiny black label">NEW</label>
                                </div>
                                <div class="extra text">
                                    Nomos Verlagsgesellschaft mbH & Co. KG
                                </div>
                            </div>
                        </div>
                        <div class="event">
                            <div class="label" data-tooltip="Package" data-position="top left">
                                <i class="orange circular inverted gift icon"></i>
                            </div>
                            <div class="content">
                                <div class="date">
                                    23.07.2026
                                </div>
                                <div class="summary">
                                    scholars_Wirtschaft 2026_2 <label class="ui tiny black label">NEW</label>
                                </div>
                                <div class="extra text">
                                    utb GmbH
                                </div>
                            </div>
                        </div>
                        <div class="event">
                            <div class="label" data-tooltip="Package" data-position="top left">
                                <i class="orange circular inverted gift icon"></i>
                            </div>
                            <div class="content">
                                <div class="date">
                                    23.07.2026
                                </div>
                                <div class="summary">
                                    scholars_Sprachwissenschaft/Spracherwerb 2026_2 <label class="ui tiny black label">NEW</label>
                                </div>
                                <div class="extra text">
                                    utb GmbH
                                </div>
                            </div>
                        </div>
                        <div class="event">
                            <div class="label" data-tooltip="Package" data-position="top left">
                                <i class="orange circular inverted gift icon"></i>
                            </div>
                            <div class="content">
                                <div class="date">
                                    23.07.2026
                                </div>
                                <div class="summary">
                                    scholars_Sprachwissenschaft allg./Anglistik 2026_2 <label class="ui tiny black label">NEW</label>
                                </div>
                                <div class="extra text">
                                    utb GmbH
                                </div>
                            </div>
                        </div>
                        <div class="event">
                            <div class="label" data-tooltip="Package" data-position="top left">
                                <i class="circle outline mini icon"></i>
                            </div>
                            <div class="content">
                                <div class="summary">
                                    <a href="#">More news about packages</a>
                                </div>
                                <div class="extra text"> </div>
                                <div class="extra text"> </div>
                            </div>
                        </div>
                        <div class="event">
                            <div class="label" data-tooltip="Platform" data-position="top left">
                                <i class="yellow circular inverted cloud icon"></i>
                            </div>
                            <div class="content">
                                <div class="date">
                                    23.07.2026
                                </div>
                                <div class="summary">
                                    LinkedIn Learning <label class="ui tiny black label">NEW</label>
                                </div>
                                <div class="extra text">
                                    LinkedIn
                                </div>
                            </div>
                        </div>
                        <div class="event">
                            <div class="label" data-tooltip="Provider" data-position="top left">
                                <i class="yellow circular inverted broadcast tower icon" style="background-color: #b54800;
                                border-color: #b54800;"></i>
                            </div>
                            <div class="content">
                                <div class="date">
                                    23.07.2026
                                </div>
                                <div class="summary">
                                    LinkedIn Learning <label class="ui tiny black label">NEW</label>
                                </div>
                                <div class="extra text">
                                    LinkedIn
                                </div>
                            </div>
                        </div>
                        <div class="event">
                            <div class="label">
                                <i class="yellow circular inverted broadcast tower icon" style="    background-color: #b54800;
                                border-color: #b54800;"></i>
                            </div>
                            <div class="content">
                                <div class="date">
                                    23.07.2026
                                </div>
                                <div class="summary">
                                    IBISWorld GmbH <label class="ui tiny black label">CHANGE</label>
                                </div>
                                <div class="extra text">
                                    IBISWorld GmbH
                                </div>
                            </div>
                        </div>
                        <div class="event">
                            <div class="label" data-tooltip="Library Suppliers" data-position="top left">
                                <i class="yellow circular inverted handshake icon" style="background-color: #5d7c62;
                                border-color: #b54800;"></i>
                            </div>
                            <div class="content">
                                <div class="date">
                                    14.07.2026
                                </div>
                                <div class="summary">
                                    EBSCO Information Services GmbH <label class="ui tiny black label">CHANGE</label>
                                </div>
                                <div class="extra text">
                                    EBSCO Information Services GmbH
                                </div>
                            </div>
                        </div>
                    </div>--}%

                </aside>
                <main class="twelve wide column hero-chart-column">

                    <section class="hero-claim">
                        <h1>
                            <span>Provider Tool we:kb</span>
                        </h1>
                        <p>Provider-Curated Knowledge Base – Freely available under CC0
                        </p>
                        <div>
                            <button class="ui big tertiary  button">
                                <i class="download icon"></i>
                                Data Export
                            </button>
                            <button class="ui big tertiary  button">
                                <i class="assistive listening systems icon"></i>
                                Sync to LAS:eR
                            </button>
                            <button class="ui big blue button">
                                <i class="search icon"></i>
                                Search Now</button>
                        </div>
                    </section>
                    <section class="statistics-chart" aria-label="Statistiken">
                        <div class="statistics-track">
                            <div class="statistics-line" aria-hidden="true">
                                <div class="statistics-line-progress"></div>
                            </div>
                            <article class="statistic" data-delay="0">
                                <div class="statistic-marker" aria-hidden="true">
                                    <span class="statistic-dot">
                                        <i class="broadcast tower icon"></i>
                                    </span>
                                </div>
                                <div class="statistic-number" data-value="118" data-decimals="0">
                                    0
                                </div>

                                <h3 class="statistic-title">
                                    Providers
                                </h3>

                                <p class="statistic-description">
                                    Content providers and library suppliers share authoritative service information directly with the library community.
                                </p>

                            </article>

                            <article class="statistic" data-delay="350">

                                <div class="statistic-marker" aria-hidden="true">
                                    <span class="statistic-dot">
                                        <i class="cloud icon"></i>
                                    </span>
                                </div>
                                <div class="statistic-number" data-value="189" data-decimals="0">
                                    0
                                </div>
                                <h3 class="statistic-title">
                                    Platforms
                                </h3>
                                <p class="statistic-description">
                                    Information on platform services answers central questions
                                    concerning authentication methods, usage statistics and matters
                                    of accessibility on providers' platforms.
                                </p>
                            </article>
                            <article class="statistic" data-delay="700">

                                <div class="statistic-marker" aria-hidden="true">
                                    <span class="statistic-dot">
                                        <i class="gift icon"></i>
                                    </span>
                                </div>

                                <div class="statistic-number" data-value="7778" data-decimals="0">
                                    0
                                </div>

                                <h3 class="statistic-title">
                                    Packages
                                </h3>

                                <p class="statistic-description">
                                    Content providers create and maintain packages that reflect their
                                    current sales units, heightening the visibility of these products
                                    and giving detailed descriptions with regard to their content.
                                </p>

                            </article>

                            <article class="statistic" data-delay="1050">

                                <div class="statistic-marker" aria-hidden="true">
                                    <span class="statistic-dot">
                                        <i class="book icon"></i>
                                    </span>
                                </div>

                                <div class="statistic-number" data-value="7.34" data-decimals="2" data-suffix=" Mio.">
                                    0
                                </div>

                                <h3 class="statistic-title">
                                    Titles
                                </h3>

                                <p class="statistic-description">
                                    Automated updates provide reliable, up-to-date package and title information.
                                </p>
                            </article>
                        </div>
                    </section>

                </main>
          </div>
%{--  </div>--}%


        <g:if test="${showAltcha}">
            <g:render template="/templates/altchaForm" model="[altchaForm: [origin: origin, startpage: true]]"/>
        </g:if>
        <g:else>
            <br/>
            <a href="/search/componentSearch?qbe=g:publicPackages" class="ui fluid huge button we-link"> ${message(code: 'public.searchPackages')} </a>

            <g:if test="${ServerUtils.getCurrentServer() in [ServerUtils.SERVER_LOCAL, ServerUtils.SERVER_DEV] && AltchaClient.isValid(request)}">%{-- DEBUG/TESTING --}%
                <br/>
                <a href="/altcha/revoke" class="ui orange button we-link"> REVOKE ALTCHA TOKEN </a>
            </g:if>
        </g:else>
%{--    </div>--}%
</body>
</html>

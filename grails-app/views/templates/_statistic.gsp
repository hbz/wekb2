<%@ page import="wekb.helper.RDStore; wekb.RefdataValue" %>
<section class="statistics-chart" aria-label="statistics">
    <div class="statistics-track">
        <article class="statistic" data-delay="0" >
            <div class="statistic-marker" aria-hidden="true">
                <span class="statistic-dot">
                    <i class="broadcast tower icon"></i>
                </span>
            </div>
            <div class="statistic-number" data-value="${countComponent['Provider']}" data-decimals="0">
                0
            </div>
            <h3 class="statistic-title">
                Providers
            </h3>
            <p class="statistic-description">
                Content providers and library suppliers share authoritative service information directly with the library community.
            </p>
            <g:if test="${showAltcha}">
                <button
                        type="submit"
                        name="origin"
                        value="/search/componentSearch?qbe=g%3Aorgs"
                        class="ui black basic button">
                    ${message(code: 'public.searchProviders')}
                </button>
            </g:if>
            <g:else>
                <g:link
                        controller="search" action="componentSearch"
                        params="[qbe: 'g:orgs']"
                        class="ui black basic button">
                    ${message(code: 'public.searchProviders')}
                </g:link>
            </g:else>
        </article>
        <article class="statistic" data-delay="350" >
            <div class="statistic-marker" aria-hidden="true">
                <span class="statistic-dot">
                    <i class="cloud icon"></i>
                </span>
            </div>

            <div class="statistic-number" data-value="${countComponent['Platform']}" data-decimals="0">
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
            <g:if test="${showAltcha}">
                <button
                        type="submit"
                        name="origin"
                        value="/search/componentSearch?qbe=g%3Aplatforms"
                        class="ui black basic button">
                    ${message(code: 'public.searchPlatforms')}
                </button>
            </g:if>
            <g:else>
                <g:link
                        class="ui black basic button"
                        controller="search" action="componentSearch"
                        params="[qbe: 'g:platforms']">
                    ${message(code: 'public.searchPlatforms')}
                </g:link>
            </g:else>
        </article>
        <article class="statistic" data-delay="700">
            <div class="statistic-marker" aria-hidden="true">
                <span class="statistic-dot">
                    <i class="box icon"></i>
                </span>
            </div>
            <div class="statistic-number" data-value="${countComponent['Package']}" data-decimals="0">
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
            <g:if test="${showAltcha}">
                <button
                        type="submit"
                        name="origin"
                        value="/search/componentSearch?qbe=g%3ApublicPackages"
                        class="ui black basic button">
                    ${message(code: 'public.searchPackages')}
                </button>
            </g:if>
            <g:else>
                <g:link class="ui black basic button"
                        controller="search" action="componentSearch"
                        params="[qbe: 'g:publicPackages']">${message(code: 'public.searchPackages')}
                </g:link>
            </g:else>
        </article>
        <article class="statistic" data-delay="1050">
            <div class="statistic-marker" aria-hidden="true">
                <span class="statistic-dot">
                    <i class="book icon"></i>
                </span>
            </div>
            <div class="statistic-number" data-value="${countComponent['TIPP']}"
                 data-decimals="1"
                 data-suffix="M"
                 data-millions="true">
                0
            </div>
            <h3 class="statistic-title">
                Titles
            </h3>
            <p class="statistic-description">
                Automated updates provide reliable, up-to-date package and title information.
            </p>
            <g:if test="${showAltcha}">
                <button
                        type="submit"
                        name="origin"
                        value="/search/componentSearch?qbe=g%3Atipps&qp_status=${wekb.RefdataValue.class?.name + ':' + RDStore?.KBC_STATUS_CURRENT?.id}"
                        class="ui black basic button">
                    ${message(code: 'public.searchTitles')}
                </button>
            </g:if>
            <g:else>
                <g:link class="ui black basic button"
                        controller="search" action="componentSearch"
                        params="[qbe: 'g:tipps', qp_status: wekb.RefdataValue.class?.name + ':' + wekb.helper.RDStore?.KBC_STATUS_CURRENT?.id]">
                    ${message(code: 'public.searchTitles')}
                </g:link>
            </g:else>
        </article>
    </div>
</section>
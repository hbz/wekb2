<%@ page import="wekb.system.AltchaClient" %>
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
    <div class="container">
        <h1>we knowledge base</h1>

        <h2 class="ui header">Provider and Platform Data</h2>
        <h3 class="ui header">
            <g:formatNumber number="${countComponent['Provider']}" type="number" format="###.###"/> Providers and
            <g:formatNumber number="${countComponent['Platform']}" type="number" format="###.###"/> Platforms
        </h3>
        <p>
            In we:kb, content providers and library suppliers may contribute valuable information about their services,
            platforms, and products and share this data with the library community.
            This ensures that libraries have access to up-to-date and authoritative information directly from the source.
        </p>

        <h2 class="ui header">Package information and title data</h2>
        <h3 class="ui header">
            <g:formatNumber number="${countComponent['Package']}" type="number" format="###.###"/> Packages and
            <g:formatNumber number="${countComponent['TIPP']}" type="number" format="###.###"/> Titles
        </h3>
        <p>
            Content providers can create and maintain packages that reflect their current products and sales units.
            Each package can include detailed metadata and title information.
            Automated import and update routines ensure to keep the package content current and provide libraries with a reliable overview of available titles.
        </p>

        <h2 class="ui header">Synchronization with LAS:eR</h2>
        <p>
            All data maintained in we:kb is seamlessly integrated in the Electronic Resource Management System LAS:eR (<a href="https://laser.hbz-nrw.de">https://laser.hbz-nrw.de</a>) where its visibility and usability is increased.
            In LAS:eR, a large community of consortia and academic institutions across the DACH region is able to work with the data provided in we:kb
            and manage electronic resources in the context of their specific license agreements.
        </p>

        <br/>

        <g:if test="${showAltcha}">
            <g:render template="/templates/altchaForm" model="[altchaForm: [origin: origin, startpage: true]]"/>
        </g:if>
%{--        <g:else>--}%
%{--            <br/>--}%
%{--            <a href="/search/generalSearch" class="ui fluid huge button we-link"> ${message(code: 'public.genSearch')} </a>--}%
%{--        </g:else>--}%
    </div>
</body>
</html>

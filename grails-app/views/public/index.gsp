<%@ page import="wekb.system.AltchaClient" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="layout" content="altcha"/>
    <title>we:kb | wekb</title>
</head>
<body>
    <g:render template="number-chart-hero"/>

    <div class="container">
        <h1>we:kb</h1>
        <h2>we knowledge base</h2>
        <h3 class="ui header">Provider and Platform Data</h3>
        <p>
            Content providers and library suppliers alike are able to share valuable data with the library community regarding their own services and platforms.
        </p>

        <h3 class="ui header">Package information and title data</h3>
        <p>
            Content providers can create packages that mirror their current sales units as well as provide valuable information concerning their products.
            Automated import and update routines may be set up for each package to provide an overview over the current titles.
        </p>

        <h3 class="ui header">Synchronization with LAS:eR</h3>
        <p>
            All we:kb data is made visible in the Electronic Resource Management System LAS:eR (<a href="https://laser.hbz-nrw.de">https://laser.hbz-nrw.de</a>) where a community of consortia
            and more than 600 institutions from the DACH region is able to work with the data provided in we:kb and put it in context with specific license agreements.
        </p>

        <br/>
        <br/>
        <br/>

        <g:render template="/templates/altchaForm" model="[altchaForm: [origin: origin, startpage: true]]"/>
    </div>
</body>
</html>

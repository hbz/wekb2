<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Group</title>
</head>

<body>
<h1 class="ui header">Check my Infos (${groups.name.join(',')})</h1>


<div class="ui accordion">
    <div class="title">
        <i class="dropdown icon"></i>
        Missing contact information in the provider profile: <span class="ui black circular label">${checkContacts.size()}</span>
    </div>

    <div class="content">
        <div class="ui list">
            <g:each in="${checkContacts}" var="org" status="i">
                <g:link class="item" controller="resource" action="show" id="${org.getOID()}">${i+1}: ${org.name}</g:link>
            </g:each>
        </div>
    </div>

    <div class="title">
        <i class="dropdown icon"></i>
        Missing product identifier for packages: <span class="ui black circular label">${checkPackagesWithoutProductID.size()}</span>
    </div>

    <div class="content">
        <div class="ui list">
            <g:each in="${checkPackagesWithoutProductID}" var="pkg" status="i">
                <g:link class="item" controller="resource" action="show" id="${pkg.getOID()}">${i+1}: ${pkg.name}</g:link>
            </g:each>
        </div>
    </div>

    <div class="title">
        <i class="dropdown icon"></i>
        Missing content type for packages: <span class="ui black circular label">${checkPackagesWithoutContentType.size()}</span>
    </div>

    <div class="content">
        <div class="ui list">
            <g:each in="${checkPackagesWithoutContentType}" var="pkg" status="i">
                <g:link class="item" controller="resource" action="show" id="${pkg.getOID()}">${i+1}: ${pkg.name}</g:link>
            </g:each>
        </div>
    </div>


    <div class="title">
        <i class="dropdown icon"></i>
        Missing titles by packages with source: <span class="ui black circular label">${checkSourcesWithoutTitles.size()}</span>
    </div>

    <div class="content">
        <div class="ui list">
            <g:each in="${checkSourcesWithoutTitles}" var="pkg" status="i">
                <g:link class="item" controller="resource" action="show" id="${pkg.getOID()}">${i+1}: ${pkg.name}</g:link>
            </g:each>
        </div>
    </div>

    <div class="title">
        <i class="dropdown icon"></i>
        Packages without source: <span class="ui black circular label">${checkPackageWithoutSource.size()}</span>
    </div>

    <div class="content">
        <div class="ui list">
            <g:each in="${checkPackageWithoutSource}" var="pkg" status="i">
                <g:link class="item" controller="resource" action="show" id="${pkg.getOID()}">${i+1}: ${pkg.name}</g:link>
            </g:each>
        </div>
    </div>

    <div class="title">
        <i class="dropdown icon"></i>
        No title changes for more than 30 days: <span class="ui black circular label">${noChangesPackageLast30Days.size()}</span>
    </div>

    <div class="content">
        <div class="ui list">
            <g:each in="${noChangesPackageLast30Days}" var="pkg" status="i">
                <g:link class="item" controller="resource" action="show" id="${pkg.getOID()}">${i+1}: ${pkg.name}</g:link>
            </g:each>
        </div>
    </div>


    <div class="title">
        <i class="dropdown icon"></i>
        Packages without titles: <span class="ui black circular label">${packagesWithoutTitles.size()}</span>
    </div>

    <div class="content">
        <div class="ui list">
            <g:each in="${packagesWithoutTitles}" var="pkg" status="i">
                <g:link class="item" controller="resource" action="show" id="${pkg.getOID()}">${i+1}: ${pkg.name}</g:link>
            </g:each>
        </div>
    </div>
</div>

</body>
</html>

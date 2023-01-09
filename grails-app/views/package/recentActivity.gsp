<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>Package Recent Activity</title>
</head>

<body>
<h1 class="ui header">${displayobj.getDomainName()}: ${displayobj.name}</h1>

<div class="ui segment">
    <h3 class="ui header">Package Recent Activity (${recentActivitys.size()})</h3>

    <div class="content">

        <g:render template="/apptemplates/secondTemplates/recentActivity" model="[recentActivitys: recentActivitys]"/>

    </div>
</div>
</body>
</html>

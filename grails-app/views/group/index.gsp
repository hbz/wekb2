<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="public_semui" />
<title>Group</title>
</head>
<body>
<h1 class="ui header">My Compoents (${groups.name.join(',')})</h1>

    <div class="ui bulleted link list">
      <g:link class="item" controller="group" action="myPackages">My Packages</g:link>
      <g:link class="item" controller="group" action="myPlatforms">My Platforms</g:link>
      <g:link class="item" controller="group" action="myProviders">My Providers</g:link>
      <g:link class="item" controller="group" action="mySources">My Sources</g:link>
      <g:link class="item" controller="group" action="myTitles">My Titles</g:link>
    </div>

</body>
</html>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Components</title>
</head>

<body>

<h1 class="ui header">we:kb : Components</h1>

<div class="ui segments">


    <div class="ui segment"><g:link controller="search" action="componentSearch" params="[qbe: 'g:packages']"
                                 title="Search Packages">Packages</g:link></div>

    <div class="ui segment"><g:link controller="search" action="componentSearch" params="[qbe: 'g:platforms']"
                                 title="Search Platforms">Platforms</g:link></div>

  <div class="ui segment"><g:link controller="search" action="componentSearch" params="[qbe: 'g:orgs']"
                                  title="Search Orgs">Providers</g:link></div>

  <div class="ui segment"><g:link controller="search" action="componentSearch" params="[qbe: 'g:tipps']"
                                  title="Search Titles">Titles</g:link></div>

    <div class="ui segment"><g:link controller="search" action="componentSearch" params="[qbe: 'g:curatoryGroups']"
                                 title="Search Curatory Groups">Curatory Groups</g:link></div>

    <div class="ui segment"><g:link controller="search" action="componentSearch" params="[qbe: 'g:sources']"
                                 title="Search Sources">Sources</g:link></div>
</div>
</body>
</html>

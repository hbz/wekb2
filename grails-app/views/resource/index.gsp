<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname" default="we:kb"/> Components</title>
  </head>
  <body>
    <g:if test="${params.status == '404'}">
    <h1 class="ui header">
      <g:message code="component.notFound.label" args="[params.id]"/>
    </h1>
    </g:if>
    <g:else>
      <h1 class="ui header"><g:message code="gokb.appname" default="we:kb"/> Components</h1>

      <li><g:link controller="search" action="componentSearch" params="[qbe:'g:tipps']" title="Search Titles" > Titles</g:link></li>
      <li><g:link controller="search" action="componentSearch" params="[qbe:'g:packages']" title="Search Packages" > Packages</g:link></li>
      <li><g:link controller="search" action="componentSearch" params="[qbe:'g:platforms']" title="Search Platforms" > Platforms</g:link></li>

      <li><g:link controller="search" action="componentSearch" params="[qbe:'g:curatoryGroups']" title="Search Curatory Groups" > Curatory Groups</g:link></li>
      <li><g:link controller="search" action="componentSearch" params="[qbe:'g:orgs']" title="Search Orgs" > Organizations</g:link></li>
      <li><g:link controller="search" action="componentSearch" params="[qbe:'g:sources']" title="Search Sources" > Sources</g:link></li>
    </g:else>
  </body>
</html>

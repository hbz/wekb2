<%@ page import="wekb.RefdataCategory; wekb.helper.ServerUtils;" %>
<!doctype html>
<html class="no-js" lang="">
<head>
	<meta charset="utf-8">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="x-ua-compatible" content="ie=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
  <s2ui:stylesheet src='spring-security-ui'/>
  <asset:stylesheet src="security-styles.css"/>
<g:layoutHead/>
</head>
<body>
  <div id="wrapper">
    <!-- Page Content -->
    <div id="page-wrapper" class="${ params.controller ?: 'default' }-display" >
      <div class="row" >
        <div id="page-content" class="col-lg-12">
          <div>
            <h1 class="page-header" style="font-size:39px;">wekb User Management Console</h1>

            <g:link controller="public" action="index" class="ui-button ui-widget ui-state-default">Back to normal wekb View</g:link>

            <br>
            <br>
            <div id="mainarea" class="panel panel-default">
              <ul class="jd_menu jd_menu_slate">
                <sec:ifAllGranted roles='ROLE_SUPERUSER'>
                <s2ui:menu controller='user'/>
                <s2ui:menu controller='role'/>
                <g:if test='${securityConfig.securityConfigType?.toString() == 'Requestmap'}'><s2ui:menu controller='requestmap'/></g:if>
                <g:if test='${securityConfig.rememberMe.persistent}'><s2ui:menu controller='persistentLogin' searchOnly='true'/></g:if>
                <s2ui:menu controller='registrationCode' searchOnly='true'/>
                <g:if test='${applicationContext.pluginManager.hasGrailsPlugin('springSecurityAcl')}'>
                <li><a class="accessible"><g:message code='spring.security.ui.menu.acl'/></a>
                  <ul>
                    <s2ui:menu controller='aclClass' submenu='true'/>
                    <s2ui:menu controller='aclSid' submenu='true'/>
                    <s2ui:menu controller='aclObjectIdentity' submenu='true'/>
                    <s2ui:menu controller='aclEntry' submenu='true'/>
                  </ul>
                </li>
                </g:if>
                <li><a class="accessible"><g:message code='spring.security.ui.menu.securityInfo'/></a>
                  <ul>
                    <s2ui:menu controller='securityInfo' itemAction='config'/>
                    <s2ui:menu controller='securityInfo' itemAction='mappings'/>
                    <s2ui:menu controller='securityInfo' itemAction='currentAuth'/>
                    <s2ui:menu controller='securityInfo' itemAction='usercache'/>
                    <s2ui:menu controller='securityInfo' itemAction='filterChains'/>
                    <s2ui:menu controller='securityInfo' itemAction='logoutHandlers'/>
                    <s2ui:menu controller='securityInfo' itemAction='voters'/>
                    <s2ui:menu controller='securityInfo' itemAction='providers'/>
                    <s2ui:menu controller='securityInfo' itemAction='secureChannel'/>
                  </ul>
                </li>
                </sec:ifAllGranted>
              </ul>
            </div>
            <div id="s2ui_main">
              <g:set var='securityConfig' value='${applicationContext.springSecurityService.securityConfig}'/>
              <asset:script>
                var loginButtonCaption = "<g:message code='spring.security.ui.login.login'/>";
                var cancelButtonCaption = "<g:message code='spring.security.ui.login.cancel'/>";
                var loggingYouIn = "<g:message code='spring.security.ui.login.loggingYouIn'/>";
                var loggedInAsWithPlaceholder = "<g:message code='spring.security.ui.login.loggedInAs' args='["{0}"]'/>";
              </asset:script>
              <div id="s2ui_content">
                <p/>
                <g:layoutBody/>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <asset:javascript src='spring-security-ui.js'/>
  <s2ui:showFlash/>
  <s2ui:deferredScripts/>
</body>
</html>

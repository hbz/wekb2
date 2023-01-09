<g:if test="${recset != null}">
<g:set var="s_action" value="${s_action?:actionName}"/>
<g:set var="s_controller" value="${s_controller?:controllerName}"/>
<g:set var="jumpToPage" value="${jumpToPage?:'jumpToPage'}"/>
<g:set var="custom_offset" value="${offset_param?:'offset'}"/>

  <semui:paginate controller="${s_controller}" action="${s_action}" id="${params.id}" params="${params}"
                  max="${max}" total="${reccount}"/>

%{--  <nav class="navbar navbar-inverse">
    <div class="container-fluid">
      <ul class="nav navbar-nav navbar-right">
        <g:if test="${ !request.isAjax() }">

          <!-- see grails-app/assets/javascripts/gokb/action-forms.js for code relating to bulk actions -->
          <g:if test="${!hideActions}">
            <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">Actions <b class="caret"></b></a>
              <ul class="dropdown-menu actions"></ul>
            </li>
            <li class="divider-vertical"></li>
          </g:if>

          <li><span class="navbar-text search-page-index"><g:form controller="${s_controller}" action="${s_action}" params="${withoutJump}" method="post">Page <input type="text" class="search-page-index-input" name="${jumpToPage}" size="5" value="${page}" style="color:#000000;" /> of ${page_max}</g:form></span></li>
        </g:if>

      </ul>
    </div>
  </nav>--}%
</g:if>

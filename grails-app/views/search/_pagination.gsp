<g:if test="${recset != null}">
<g:set var="s_action" value="${s_action?:actionName}"/>
<g:set var="s_controller" value="${s_controller?:controllerName}"/>
<g:set var="jumpToPage" value="${jumpToPage?:'jumpToPage'}"/>
<g:set var="custom_offset" value="${offset_param?:'offset'}"/>

  %{--<semui:paginate controller="${s_controller}" action="${s_action}" id="${params.id}" params="${params}"
                  max="${max}" total="${reccount}"/>--}%

  <semui:paginateNew controller="${s_controller}" action="${s_action}" id="${params.id}" params="${params}"
                  max="${max}" total="${reccount}"/>
</g:if>
<g:javascript>
  $(document).ready(function() {
    r2d2.initDynamicSemuiStuff('.inline-content');
  });
</g:javascript>
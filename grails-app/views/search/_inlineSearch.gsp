<g:if test="${qbetemplate}">
    <g:if test="${(qbetemplate.message != null)}">
        <semui:message message="${qbetemplate.message}"/>
    </g:if>
    <g:render template="qbeform"
              model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>

    <g:if test="${recset && !init}">
        <g:render template="qberesult"
                  model="${[qbeConfig: qbetemplate.qbeConfig, rows: new_recset, offset: offset, det: det, page: page_current, page_max: page_total, baseClass: qbetemplate.baseclass]}"/>
    </g:if>
    <g:elseif test="${!init && !params.inline}">
        <g:render template="qbeempty"/>
    </g:elseif>
    <g:else>
        <semui:message>
            <p>No results.</p>
        </semui:message>
    </g:else>
</g:if>
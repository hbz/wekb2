<g:if test="${c.value instanceof Boolean}">
    <g:if test="${c.value}">
        <i class="check green circle icon"
           title="${message(code: 'default.boolean.true')}"></i>
    </g:if>
    <g:else>
        <i class="times red circle icon"
           title="${message(code: 'default.boolean.false')}"></i>
    </g:else>
</g:if>
<g:elseif test="${c.value instanceof java.lang.Integer}">
    <g:if test="${c.value}">
        <g:formatNumber number="${c.value}" type="number"/>
    </g:if>
    <g:else>
        0
    </g:else>
</g:elseif>
<g:elseif test="${c.value instanceof java.util.Date}">
    <g:if test="${c.value}">
        <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                      date="${c.value}"/>
    </g:if>
</g:elseif>
<g:elseif test="${c.value instanceof java.time.LocalDateTime}">
    <g:if test="${c.value}">
        <g:formatDate format="${message(code: 'default.date.format.noZWihoutSS')}"
                      date="${c.value}"/>
    </g:if>
</g:elseif>
<g:else>
    ${c.value}
</g:else>

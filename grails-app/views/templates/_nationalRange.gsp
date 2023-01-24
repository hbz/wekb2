<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory" %>
<g:if test="${d.id != null}">
    <div class="ui bulleted list">
        <g:each in="${d.nationalRanges.sort { it.getI10n('value') }}" var="nationalRange">
            <div class="item">${nationalRange.value}: ${nationalRange.getI10n('value')}
            <g:if test="${editable}">
                <g:link controller='ajaxHtml'
                        action='unlinkManyToMany'
                        params="${["__context": "${d.class.name}:${d.id}", "__property": "nationalRanges", "__itemToRemove": "${nationalRange.getClassName()}:${nationalRange.id}"]}">Delete</g:link>
            </g:if>
            </div>
        </g:each>
    </div>

    <g:if test="${editable}">
        <a class="ui right floated black button" href="#" onclick="$('#nationalRangesModal').modal('show');">Add National Range</a>

        <br>
        <br>

        <semui:modal id="nationalRangesModal" title="Add National Range">
            <g:form controller="ajaxHtml" action="addToStdCollection" class="ui form">
                <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
                <input type="hidden" name="__property" value="nationalRanges"/>
                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                <div class="field">
                    <label>National Range:</label>

                    <semui:simpleReferenceDropdown name="__relatedObject"
                                                   baseClass="wekb.RefdataValue"
                                                   filter1="${RCConstants.COUNTRY}"/>
                </div>

            </g:form>
        </semui:modal>
    </g:if>
</g:if>
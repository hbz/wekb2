<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory" %>
<g:if test="${d.id != null}">
    <div class="ui bulleted list">
        <g:each in="${d.regionalRanges.sort { it.getI10n('value') }}" var="regionalRange">
            <div class="item">${regionalRange.getI10n('value')}
            <g:if test="${editable}">
                <g:link class='ui mini button negative' controller='ajaxHtml'
                        action='unlinkManyToMany'
                        params="${["__context": "${d.getOID()}", "__property": "regionalRanges", "__itemToRemove": "${regionalRange.getOID()}"]}">Delete</g:link>
            </g:if>
            </div>
        </g:each>
    </div>

    <g:if test="${editable}">
        <a class="ui right floated primary button" href="#" onclick="$('#regionalRangesModal').modal('show');">Add Regional Range</a>

        <br>
        <br>

        <semui:modal id="regionalRangesModal" title="Add Regional Range">
            <g:form controller="ajaxHtml" action="addToStdCollection" class="ui form">
                <input type="hidden" name="__context" value="${d.getOID()}"/>
                <input type="hidden" name="__property" value="regionalRanges"/>
                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                <div class="field">
                    <label>Regional Range:</label>

                    <semui:simpleReferenceDropdown name="__relatedObject"
                                                   baseClass="wekb.RefdataValue"
                                                   filter1="${RCConstants.PACKAGE_REGIONAL_RANGE}"/>
                </div>
            </g:form>
        </semui:modal>
    </g:if>
</g:if>
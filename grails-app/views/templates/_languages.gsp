<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory" %>
<g:if test="${d.id != null}">
    <div class="ui bulleted list">
        <g:each in="${d.languages.sort { it.language.getI10n('value') }}" var="tippLanguage">
            <div class="item">${tippLanguage.language.getI10n('value')}
            <g:if test="${editable}">
                <g:link controller="ajaxHtml"
                        action="deleteLanguage" id="${tippLanguage.id}"
                        params="[activeTab: 'languages', curationOverride: params.curationOverride]">Delete</g:link>
            </g:if>
            </div>
        </g:each>
    </div>

    <g:if test="${editable}">
        <a class="ui right floated primary button" href="#" onclick="$('#languageModal').modal('show');">Add Language</a>

        <br>
        <br>

        <semui:modal id="languageModal" title="Add Language">
            <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
                <input type="hidden" name="__context"
                       value="${d.getOID()}"/>
                <input type="hidden" name="__newObjectClass"
                       value="wekb.TippLanguage"/>
                <input type="hidden" name="__recip" value="tipp"/>
                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                <div class="field">
                    <label>Language:</label>

                    <semui:simpleReferenceDropdown name="language"
                                                   baseClass="wekb.RefdataValue"
                                                   filter1="${RCConstants.COMPONENT_LANGUAGE}"/>
                </div>
            </g:form>
        </semui:modal>
    </g:if>
</g:if>
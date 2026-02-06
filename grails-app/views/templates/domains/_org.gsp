<%@ page import="wekb.helper.RCConstants; wekb.helper.RDStore;" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                <g:message code="org.name"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="name" required="true"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="org.abbreviatedName"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="abbreviatedName" required="true"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="default.status"/>
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.COMPONENT_STATUS}"/>
                %{--  <sec:ifAnyGranted roles="ROLE_SUPERUSER">
                      <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.COMPONENT_STATUS}"/>
                  </sec:ifAnyGranted>
                  <sec:ifNotGranted roles="ROLE_SUPERUSER">
                      ${d.status?.value ?: 'Not Set'}
                  </sec:ifNotGranted>--}%
            </dd>
        </dl>
        <dl>
            <dt class="control-label"><g:message code="package.description"/></dt>
            <dd><g:if test="${!createObject}">
                <semui:xEditable owner="${d}" type="textarea" field="description"/>
            </g:if>
            </dd>

        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="org.homepage"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="homepage" outGoingLink="true"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="org.urlToTrainingMaterials"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="urlToTrainingMaterials" validation="url" outGoingLink="true"/>
            </dd>
        </dl>
        <g:if test="${d.id != null}">
            <dl>
                <dt class="control-label">
                    <g:message code="org.role"/>
                </dt>
                <dd>
                    <div class="ui bulleted list">
                        <g:each in="${d.roles?.sort { it.getI10n('value') }}" var="t">
                            <div class="item">
                                ${t.value}
                                <g:if test="${editable}">
                                    <g:link class='ui mini button negative' controller='ajaxHtml'
                                            action='unlinkManyToMany'
                                            params="${["__context": "${d.getOID()}", "__property": "roles", "__itemToRemove": "${t.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>
                                </g:if>
                            </div>
                        </g:each>
                    </div>

                    <g:if test="${editable}">
                        <a class="ui right floated primary button" href="#"
                           onclick="$('#rolesModal').modal('show');">Add Role</a>

                        <br>
                        <br>
                    </g:if>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Contacts
                </dt>
                <dd>

                    <table class="ui small selectable striped celled table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Contact Type</th>
                            <th>Languages</th>
                            <th>Content Type</th>
                            <th>Value</th>
                            <g:if test="${editable}">
                                <th>Action</th>
                            </g:if>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${d.contacts?.sort { it.type }}" var="contact" status="i">
                            <tr>
                                <td>${i + 1}</td>
                                <td><semui:xEditableRefData owner="${contact}" field="type"
                                                            config="${RCConstants.CONTACT_TYPE}" required="true"/>
                                </td>
                                <td>

                                    <div class="ui bulleted list">
                                        <g:each in="${contact.languages?.sort { it.getI10n('value') }}" var="t">
                                            <div class="item">
                                                ${t.getI10n('value')}
                                                <g:if test="${editable}">
                                                    <g:link class='' controller='ajaxHtml'
                                                            action='unlinkManyToMany'
                                                            params="${["__context": "${contact.getOID()}", "__property": "languages", "__itemToRemove": "${t.getOID()}", curationOverride: params.curationOverride]}">(Delete)</g:link>
                                                </g:if>
                                            </div>
                                        </g:each>
                                    </div>
                                </td>
                                <td>
                                    <semui:xEditableRefData owner="${contact}" field="contentType"
                                                            config="${RCConstants.CONTACT_CONTENT_TYPE}" required="true"/>
                                <td>
                                <g:if test="${editable}">
                                    <semui:xEditable owner="${contact}" field="content"
                                                     validation="${contact.contentType == RDStore.CONTACT_CONTENT_TYPE_EMAIL ? 'email' : ''}" required="true"/>
                                </g:if>
                                <g:else>
                                    <span class="js-copyTriggerParent">
                                        <span class="ui small basic image label js-copyTrigger la-popup-tooltip" data-position="top center"
                                              data-tooltip="${message(code: 'tooltip.clickToCopy', args: [contact.contentType?.value_en])}">
                                            <i class="copy black icon la-js-copyTriggerIcon"></i>
                                            <span class="detail js-copyTopic">${contact.content}</span>
                                        </span>
                                    </span>
                                </g:else>

                                </td>
                                <g:if test="${editable}">
                                    <td>
                                        <a class="ui mini primary button" href="#"
                                           onclick="$('#languagesModal'+${contact.id}).modal('show');">Add Language</a>

                                        <br>
                                        <br>
                                        <g:link controller='ajaxHtml' class="ui mini button negative"
                                                action='delete'
                                                params="${["__context": "${contact.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>

                                        <semui:modal id="languagesModal${contact.id}" title="Add Language for ${contact.content}">

                                            <g:form controller="ajaxHtml" action="addToStdCollection" class="ui form">
                                                <input type="hidden" name="__context" value="${contact.getOID()}"/>
                                                <input type="hidden" name="__property" value="languages"/>
                                                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                                                <div class="field">
                                                    <label>Language:</label> <semui:simpleReferenceDropdown name="__relatedObject"
                                                                                                            baseClass="wekb.RefdataValue"
                                                                                                            filter1="${RCConstants.COMPONENT_LANGUAGE}"/>
                                                </div>
                                            </g:form>
                                        </semui:modal>
                                    </td>
                                </g:if>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>

                    <g:if test="${editable}">
                        <a class="ui right floated primary button" href="#"
                           onclick="$('#contactModal').modal('show');">Add Contact</a>

                        <br>
                        <br>
                    </g:if>

                </dd>
            </dl>
        </g:if>
        <g:if test="${editable}">
            <semui:modal id="contactModal" title="Add Contact">

                <g:form controller="ajaxHtml" action="addContact" class="ui form contactModal">
                    <input type="hidden" name="__context" value="${d.getOID()}"/>
                    <input type="hidden" name="__newObjectClass" value="wekb.Contact"/>
                    <input type="hidden" name="__recip" value="org"/>
                    <input type="hidden" name="activeTab" value="contact"/>
                    <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                    <div class="required field">
                        <label>Contact Type</label>
                        <semui:simpleReferenceDropdown name="type"
                                                       baseClass="wekb.RefdataValue"
                                                       filter1="${RCConstants.CONTACT_TYPE}"/>
                    </div>

                    <div class="required field">
                        <label>Language</label>
                        <semui:simpleReferenceDropdown name="language"
                                                       baseClass="wekb.RefdataValue"
                                                       filter1="${RCConstants.COMPONENT_LANGUAGE}" />
                    </div>

                    <div class="required field">
                        <label>Content Type</label>
                        <semui:simpleReferenceDropdown name="contentType"
                                                       baseClass="wekb.RefdataValue"
                                                       filter1="${RCConstants.CONTACT_CONTENT_TYPE}"/>
                    </div>

                     <div class="required field">
                        <label>Value</label>

                        <input type="text" name="content"/>
                    </div>

                  
                    <div class="ui error message"></div>
                </g:form>
            </semui:modal>

            <semui:modal id="rolesModal" title="Add Role">

                <g:form controller="ajaxHtml" action="addToStdCollection" class="ui form">
                    <input type="hidden" name="__context" value="${d.getOID()}"/>
                    <input type="hidden" name="__property" value="roles"/>
                    <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                    <div class="field">
                        <label>Role:</label> <semui:simpleReferenceDropdown name="__relatedObject"
                                                                            baseClass="wekb.RefdataValue"
                                                                            filter1="${RCConstants.ORG_ROLE}"/>
                    </div>
                </g:form>
            </semui:modal>

            <g:javascript>
                $('.contactModal').form({
                    on: 'blur',
                    fields: {
                        language: {
                            identifier: 'language',
                            rules: [
                                {
                                    type: 'empty',
                                    prompt: 'Please select a language'
                                }
                            ]
                        },
                        content: {
                            identifier: 'content',
                            rules: [
                                {
                                    type: 'empty',
                                    prompt: 'Please enter a value'
                                }
                            ]
                        },
                        contentType: {
                            identifier: 'contentType',
                            rules: [
                                {
                                    type: 'empty',
                                    prompt: 'Please select a content type'
                                }
                            ]
                        },
                        type: {
                            identifier: 'type',
                            rules: [
                                {
                                    type: 'empty',
                                    prompt: 'Please select a contact type'
                                }
                            ]
                        }
                    }
                })
                ;

            </g:javascript>
        </g:if>

    </div>
</div>

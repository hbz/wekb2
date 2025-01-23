<%@ page import="wekb.helper.RCConstants; wekb.helper.RDStore;" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                <g:message code="vendor.name"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="name" required="true"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="vendor.abbreviatedName"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="abbreviatedName" required="true"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="vendor.homepage"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="homepage" outGoingLink="true"/>
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

        <g:if test="${d.id != null}">
            <dl>
                <dt class="control-label">
                    <g:message code="vendor.role"/>
                </dt>
                <dd>
                    <div class="ui bulleted list">
                        <g:each in="${d.roles?.sort { it.getI10n('value') }}" var="t">
                            <div class="item">
                                ${t.value}
                                <g:if test="${editable}">
                                    <g:link class='ui mini button negative' controller='ajaxHtml'
                                            action='unlinkManyToMany'
                                            params="${["__context": "${d.getOID()}", "__property": "roles", "__itemToRemove": "${t.getOID()}"]}">Delete</g:link>
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
                            <th>Main Language</th>
                            <th>Content Type</th>
                            <th>Value</th>
                            <g:if test="${editable}">
                                <th>Action</th>
                            </g:if>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${d.contacts?.sort { it.content }}" var="contact" status="i">
                            <tr>
                                <td>${i + 1}</td>
                                <td><semui:xEditableRefData owner="${contact}" field="type"
                                                            config="${RCConstants.CONTACT_TYPE}"/>
                                </td>
                                <td>
                                    <semui:xEditableRefData owner="${contact}" field="language"
                                                            config="${RCConstants.COMPONENT_LANGUAGE}"/>
                                </td>
                                <td>
                                    <semui:xEditableRefData owner="${contact}" field="contentType"
                                                            config="${RCConstants.CONTACT_CONTENT_TYPE}"/>
                                <td>
                                    <semui:xEditable owner="${contact}" field="content"
                                                     validation="${contact.contentType == RDStore.CONTACT_CONTENT_TYPE_EMAIL ? 'email' : ''}"/>
                                </td>
                                <g:if test="${editable}">
                                    <td>

                                        <g:link controller='ajaxHtml'
                                                action='delete'
                                                params="${["__context": "${contact.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>

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

                <g:form controller="ajaxHtml" action="addContact" class="ui form">
                    <input type="hidden" name="__context" value="${d.getOID()}"/>
                    <input type="hidden" name="__newObjectClass" value="wekb.Contact"/>
                    <input type="hidden" name="__recip" value="vendor"/>
                    <input type="hidden" name="activeTab" value="contact"/>
                    <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                    <div class="field">
                        <label>Value</label>

                        <input type="text" name="content"/>
                    </div>

                    <div class="field">
                        <label>Language</label>
                        <semui:simpleReferenceDropdown name="language"
                                                       baseClass="wekb.RefdataValue"
                                                       filter1="${RCConstants.COMPONENT_LANGUAGE}"/>
                    </div>

                    <div class="field">
                        <label>Content Type</label>
                        <semui:simpleReferenceDropdown name="contentType"
                                                       baseClass="wekb.RefdataValue" class="required"
                                                       filter1="${RCConstants.CONTACT_CONTENT_TYPE}"/>
                    </div>

                    <div class="field">
                        <label>Contact Type</label>
                        <semui:simpleReferenceDropdown name="type"
                                                       baseClass="wekb.RefdataValue" class="required"
                                                       filter1="${RCConstants.CONTACT_TYPE}"/>
                    </div>
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
        </g:if>
    </div>
</div>
<%@ page import="wekb.helper.RCConstants" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                <g:message code="platform.name"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="name" required="true"/>
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
            <dt class="control-label">
                <g:message code="platform.provider"/>
            </dt>
            <dd>
                <semui:xEditableManyToOne owner="${d}" field="provider"
                                          baseClass="wekb.Org" onylMyComponents="true"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="platform.primaryUrl"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="primaryUrl" validation="url" outGoingLink="true"/>
            </dd>
        </dl>
        <dl>
        <dt class="control-label"><g:message code="platform.ipAuthentication"/></dt>
            <dd><semui:xEditableRefData owner="${d}" field="ipAuthentication"
                                        config="${RCConstants.PLATFORM_IP_AUTH}"/></dd>
        </dl>
        <dl>
            <dt class="control-label"><g:message code="platform.openAthens"/></dt>
            <dd><semui:xEditableRefData owner="${d}" field="openAthens"
                                        config="${RCConstants.YN}"/></dd>
        </dl>
        <dl>
            <dt class="control-label"><g:message code="platform.shibbolethAuthentication"/></dt>
            <dd><semui:xEditableRefData owner="${d}" field="shibbolethAuthentication"
                                        config="${RCConstants.YN}"/></dd>
        </dl>

        <g:if test="${controllerName != 'create' && d.shibbolethAuthentication && d.shibbolethAuthentication == wekb.helper.RDStore.YN_YES}">
            <dl>
                <dt class="control-label">
                    Federations
                </dt>
                <dd>
                    <table class="ui small selectable striped celled table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Federation</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${d.federations?.sort { it.federation?.value }}" var="federation" status="i">
                            <tr>
                                <td>${i + 1}</td>
                                <td><semui:xEditableRefData owner="${federation}" field="federation"
                                                            config="${RCConstants.PLATFORM_FEDERATION}"/>
                                <td>
                                    <g:if test="${editable}">
                                        <g:link controller='ajaxHtml'
                                                action='delete'
                                                params="${["__context": "${federation.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>
                                    </g:if>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>

                    <g:if test="${editable}">
                        <a class="ui right floated primary button" href="#"
                           onclick="$('#pfModal').modal('show');">Add Federations</a>

                        <br>
                        <br>
                    </g:if>
                </dd>
            </dl>
        </g:if>


        <dl>
            <dt class="control-label"><g:message code="platform.passwordAuthentication"/></dt>
            <dd><semui:xEditableRefData owner="${d}" field="passwordAuthentication"
                                        config="${RCConstants.YN}"/></dd>
        </dl>
        <dl>
            <dt class="control-label"><g:message code="platform.proxySupported"/></dt>
            <dd><semui:xEditableRefData owner="${d}" field="proxySupported"
                                        config="${RCConstants.YN}"/></dd>
        </dl>

        <dl>
            <dt class="control-label"><g:message code="platform.counterRegistryApiUuid"/></dt>
            <dd><semui:xEditable owner="${d}" field="counterRegistryApiUuid"/></dd>
        </dl>


        <g:if test="${editable && controllerName != 'create'}">
            <semui:modal id="pfModal" title="Add Federations">

                <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
                    <input type="hidden" name="__context" value="${d.getOID()}"/>
                    <input type="hidden" name="__newObjectClass" value="wekb.PlatformFederation"/>
                    <input type="hidden" name="__recip" value="platform"/>
                    <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                    <div class="field">
                        <label>Federations</label>
                        <semui:simpleReferenceDropdown name="federation"
                                                       baseClass="wekb.RefdataValue"
                                                       filter1="${RCConstants.PLATFORM_FEDERATION}"/>
                    </div>
                </g:form>
            </semui:modal>
        </g:if>

    </div>
</div>
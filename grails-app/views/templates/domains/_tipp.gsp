<%@ page import="wekb.helper.RCConstants" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                Title
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="name" required="true"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Package
            </dt>
            <dd>
                <g:if test="${controllerName == 'create'}">
                    <semui:xEditableManyToOne value="${d.pkg ? d.pkg.getOID() : null}" owner="${d}" field="pkg"
                                              baseClass="wekb.Package" required="true"/>
                </g:if>
                <g:else>
                    <g:if test="${d.pkg}">
                        <g:link controller="resource" action="show"
                                id="${d.pkg.getOID()}">
                            ${(d.pkg.name) ?: 'Empty'}
                        </g:link>
                    </g:if>
                    <g:else>Empty</g:else>
                </g:else>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Platform
            </dt>
            <dd>
                <g:if test="${controllerName == 'create'}">
                    <semui:xEditableManyToOne value="${d.hostPlatform ? d.hostPlatform.getOID() : null}" owner="${d}"
                                              field="hostPlatform" baseClass="wekb.Platform"
                                              required="true"/>
                </g:if>
                <g:else>
                    <g:if test="${d.hostPlatform}">
                        <g:link controller="resource" action="show"
                                id="${d.hostPlatform.getOID()}">${d.hostPlatform.name}</g:link>
                    </g:if>
                    <g:else>Empty</g:else>
                </g:else>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Host Platform URL
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="url" validation="url" outGoingLink="true" required="true"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Status
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
                Publication Type
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="publicationType"
                                        config="${RCConstants.TIPP_PUBLICATION_TYPE}"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Medium
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="medium" config="${RCConstants.TIPP_MEDIUM}"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Language
            </dt>
            <dd>
                <g:render template="/templates/languages"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                First Author
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="firstAuthor"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                First Editor
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="firstEditor"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Publisher Name
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="publisherName"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Date first in print
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="dateFirstInPrint"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Date first online
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="dateFirstOnline"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Access Start Date
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="accessStartDate"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Access End Date
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="accessEndDate"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Volume Number
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="volumeNumber"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Edition
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="editionStatement"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Access Type
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="accessType"
                                        config="${RCConstants.TIPP_ACCESS_TYPE}"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Notes
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="note"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Last Changed
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="lastChangedExternal" type="date"/>
            </dd>

        </dl>

        <sec:ifAnyGranted roles="ROLE_ADMIN">
            <dl>
                <dt class="control-label">
                    This information comes from KBART import
                </dt>
                <dd>
                    <semui:xEditableBoolean owner="${d}" field="fromKbartImport"/>
                </dd>
            </dl>
        </sec:ifAnyGranted>
    </div>
</div>
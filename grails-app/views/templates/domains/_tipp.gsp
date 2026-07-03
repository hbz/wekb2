<%@ page import="wekb.helper.RDStore; wekb.helper.RCConstants" %>

<g:if test="${d.pkg.kbartSource}">
    <div class="ui info icon message">

        <div class="content wekb-inline-lists">
            <div class="header">No editing permissions for packages with a kbart source</div>
            <div class="content">
                You cannot edit this title because the package has a KBART source, and all changes are made via the KBART file.
            </div>
        </div>

    </div>
</g:if>

<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.name"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="name" required="true" />
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
                Provider
            </dt>
            <dd>
                <g:if test="${controllerName != 'create'}">
                    <g:if test="${d.pkg.provider}">
                        <g:link controller="resource" action="show"
                                id="${d.pkg.provider.getOID()}">
                            ${(d.pkg.provider.name) ?: 'Empty'}
                        </g:link>
                    </g:if>
                    <g:else>Empty</g:else>
                </g:if>
                <g:else>
                    Automatic from Package
                </g:else>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Platform
            </dt>
            <dd>
                <g:if test="${false}">
                    <semui:xEditableManyToOne value="${d.hostPlatform ? d.hostPlatform.getOID() : null}" owner="${d}"
                                              field="hostPlatform" baseClass="wekb.Platform"
                                              required="true"/>
                </g:if>
                <g:else>
                    <g:if test="${d.pkg && d.pkg.nominalPlatform}">
                        <g:link controller="resource" action="show"
                                id="${d.pkg.nominalPlatform.getOID()}">${d.pkg.nominalPlatform.name}</g:link>
                    </g:if>
                    <g:else>Automatic from Nominal Platform of Package</g:else>
                </g:else>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.url"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="url" validation="url" outGoingLink="true" required="true" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="default.status"/>
            </dt>
            <dd>
                <g:if test="${!d.status}">
                    ${RDStore.KBC_STATUS_CURRENT.value_en}
                </g:if><g:else>
                    ${d.status.value_en}
                </g:else>
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
                <g:message code="titleinstancepackageplatform.publicationType"/>
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="publicationType"
                                        config="${RCConstants.TIPP_PUBLICATION_TYPE}" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.medium"/>
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="medium" config="${RCConstants.TIPP_MEDIUM}" />
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
                <g:message code="titleinstancepackageplatform.firstAuthor"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="firstAuthor" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.firstEditor"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="firstEditor" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.publisherName"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="publisherName" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.dateFirstInPrint"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="dateFirstInPrint" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.dateFirstOnline"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="dateFirstOnline" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.accessStartDate"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="accessStartDate" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.accessEndDate"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="accessEndDate" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.volumeNumber"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="volumeNumber" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.editionStatement"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="editionStatement" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.accessType"/>
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="accessType"
                                        config="${RCConstants.TIPP_ACCESS_TYPE}" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.note"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="note" />
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="titleinstancepackageplatform.lastChangedExternal"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="lastChangedExternal" type="date" />
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
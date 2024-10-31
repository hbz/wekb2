<%@ page import="wekb.helper.RCConstants; wekb.IdentifierNamespace; wekb.helper.RDStore;" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                <g:message code="kbartsource.name"/>
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
                <g:message code="kbartsource.frequency"/>
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="frequency" config="${RCConstants.SOURCE_FREQUENCY}"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="kbartsource.defaultSupplyMethod"/>
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="defaultSupplyMethod"
                                        config="${RCConstants.SOURCE_DATA_SUPPLY_METHOD}"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="kbartsource.defaultDataFormat"/>
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="defaultDataFormat"
                                        config="${RCConstants.SOURCE_DATA_FORMAT}"/>
            </dd>
        </dl>
        %{--<dl>
            <dt class="control-label">
                Responsible Party
            </dt>
            <dd>
                <semui:xEditableManyToOne owner="${d}" field="responsibleParty"
                                          baseClass="wekb.Org">
                    ${d.responsibleParty?.name ?: ''}
                </semui:xEditableManyToOne>
            </dd>
        </dl>--}%
        <dl>
            <dt class="control-label">
                <g:message code="kbartsource.automaticUpdates"/>
            </dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="automaticUpdates"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Title ID Namespace
            </dt>
            <dd>
                ${IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP).value}
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="kbartsource.kbartHasWekbFields"/>
            </dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditableBoolean owner="${d}" field="kbartHasWekbFields" overwriteEditable="false"/>
                </g:if>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="kbartsource.lastChangedInKbart"/>
            </dt>
            <dd>
                <sec:ifAnyGranted roles="ROLE_SUPERUSER">
                    <g:if test="${!createObject}">
                        <semui:xEditable owner="${d}" type="date" field="lastChangedInKbart">${d.lastChangedInKbart}</semui:xEditable>
                    </g:if>
                </sec:ifAnyGranted>
                <sec:ifNotGranted roles="ROLE_SUPERUSER">
                    <g:formatDate format="${message(code: 'default.date.format.notime')}"
                                  date="${d.lastChangedInKbart}"/>
                </sec:ifNotGranted>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="kbartsource.lastRun"/>
            </dt>
            <dd>
                <sec:ifAnyGranted roles="ROLE_SUPERUSER">
                    <g:if test="${!createObject}">
                        <semui:xEditable owner="${d}" type="date" field="lastRun">${d.lastRun}</semui:xEditable>
                    </g:if>
                </sec:ifAnyGranted>
                <sec:ifNotGranted roles="ROLE_SUPERUSER">
                    <g:if test="${!createObject}">
                        <g:formatDate format="${message(code: 'default.date.format.noZ')}"
                                      date="${d.lastRun}"/>
                    </g:if>
                </sec:ifNotGranted>

            </dd>
        </dl>

        <g:if test="${controllerName != 'create' && d.automaticUpdates}">
            <dl>
                <dt class="control-label">
                    <g:message code="kbartsource.nextUpdateTimestamp"/>
                </dt>
                <dd>
                    <g:formatDate format="${message(code: 'default.date.format.noZ')}"
                                  date="${d.getNextUpdateTimestamp()}"/>
                </dd>
            </dl>

            <dl>
                <dt class="control-label">
                    Anticipated Run Times
                </dt>
                <dd>
                    <div class="ui bulleted list">
                        <g:each in="${d.getAllNextUpdateTimestamp()}" var="updateDate">
                            <div class="item">
                                <g:formatDate format="${message(code: 'default.date.format.noZ')}"
                                              date="${updateDate}"/>
                            </div>
                        </g:each>
                    </div>
                </dd>
            </dl>
        </g:if>
    </div>
</div>

<div class="sixteen wide column">
        <g:if test="${d.defaultSupplyMethod == wekb.helper.RDStore.KS_DSMETHOD_HTTP_URL || editable || d.defaultSupplyMethod == null}">
            <div class="ui segment">
                <div class="content wekb-inline-lists">
                    <div class="header">
                        <g:if test="${d.defaultSupplyMethod == wekb.helper.RDStore.KS_DSMETHOD_HTTP_URL}">
                            <div class="ui green ribbon label">Active</div>
                        </g:if>
                        <g:else>
                            <div class="ui red ribbon label">Not active</div>
                        </g:else>
                        HTTP URL Configuration
                    </div>

                    <div class="description">
                        <dl>
                            <dt class="control-label">
                                URL
                            </dt>
                            <dd>
                                <semui:xEditable owner="${d}" field="url" validation="url" outGoingLink="true"/>
                            </dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                Last Update Url
                            </dt>
                            <dd>
                                <g:if test="${!createObject}">
                                    <semui:xEditable owner="${d}" field="lastUpdateUrl" overwriteEditable="${false}"
                                                     outGoingLink="true"/>
                                </g:if>
                            </dd>
                        </dl>
                    </div>
                </div>
            </div>
        </g:if>
        <g:if test="${d.defaultSupplyMethod == wekb.helper.RDStore.KS_DSMETHOD_FTP || editable || d.defaultSupplyMethod == null}">
            <div class="ui segment">
                <div class="content wekb-inline-lists">
                    <div class="header">
                        <g:if test="${d.defaultSupplyMethod == wekb.helper.RDStore.KS_DSMETHOD_FTP}">
                            <div class="ui green ribbon label">Activ</div>
                        </g:if><g:else>
                        <div class="ui red ribbon label">Not activ</div>
                    </g:else>
                        FTP Configuration
                    </div>

                    <div class="description">
                        <dl>
                            <dt class="control-label">
                                FTP Server Url
                            </dt>
                            <dd>
                                <g:if test="${editable || curator}">
                                    <semui:xEditable owner="${d}" field="ftpServerUrl"/>
                                </g:if>
                                <g:else>
                                    Only the curator of this component can see this.
                                </g:else>
                            </dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                FTP Username
                            </dt>
                            <dd>
                                <g:if test="${editable || curator}">
                                    <semui:xEditable owner="${d}" field="ftpUsername"/>
                                </g:if>
                                <g:else>
                                    Only the curator of this component can see this.
                                </g:else>
                            </dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                FTP Password
                            </dt>
                            <dd>
                                <g:if test="${editable || curator}">
                                    <semui:xEditable owner="${d}" field="ftpPassword"/>
                                </g:if>
                                <g:else>
                                    Only the curator of this component can see this.
                                </g:else>
                            </dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                FTP Directory
                            </dt>
                            <dd>
                                <g:if test="${editable || curator}">
                                    <semui:xEditable owner="${d}" field="ftpDirectory"/>
                                </g:if>
                                <g:else>
                                    Only the curator of this component can see this.
                                </g:else>
                            </dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                FTP File Name
                            </dt>
                            <dd>
                                <g:if test="${editable || curator}">
                                    <semui:xEditable owner="${d}" field="ftpFileName"/>
                                </g:if>
                                <g:else>
                                    Only the curator of this component can see this.
                                </g:else>
                            </dd>
                        </dl>
                    </div>
                </div>
            </div>
        </g:if>

</div>

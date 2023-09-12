<%@ page import="wekb.helper.RCConstants; wekb.IdentifierNamespace; wekb.helper.RDStore;" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                Source Name
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="name" required="true"/>
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
                Frequency
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="frequency" config="${RCConstants.SOURCE_FREQUENCY}"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Default Supply Method
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="defaultSupplyMethod"
                                        config="${RCConstants.SOURCE_DATA_SUPPLY_METHOD}"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Default Data Format
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
                Automated Updates
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
                KBART has additional fields for we:kb
            </dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="kbartHasWekbFields" overwriteEditable="false"
                                        disabled="${createObject}"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Last Changed in KBART
            </dt>
            <dd>
                <sec:ifAnyGranted roles="ROLE_SUPERUSER">
                    <semui:xEditable owner="${d}" type="date" field="lastChangedInKbart"
                                     disabled="${createObject}">${d.lastChangedInKbart}</semui:xEditable>
                </sec:ifAnyGranted>
                <sec:ifNotGranted roles="ROLE_SUPERUSER">
                    <g:formatDate format="${message(code: 'default.date.format.notime')}"
                                  date="${d.lastChangedInKbart}"/>
                </sec:ifNotGranted>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Last Run
            </dt>
            <dd>
                <sec:ifAnyGranted roles="ROLE_SUPERUSER">
                    <semui:xEditable owner="${d}" type="date" field="lastRun"
                                     disabled="${createObject}">${d.lastRun}</semui:xEditable>
                </sec:ifAnyGranted>
                <sec:ifNotGranted roles="ROLE_SUPERUSER">
                    <semui:xEditable owner="${d}" type="date" field="lastRun" overwriteEditable="false"
                                     disabled="${createObject}">${d.lastRun}</semui:xEditable>
                </sec:ifNotGranted>

            </dd>
        </dl>

        <g:if test="${controllerName != 'create' && d.automaticUpdates}">
            <dl>
                <dt class="control-label">
                    Next Run
                </dt>
                <dd>
                    ${d.getNextUpdateTimestamp()}
                </dd>
            </dl>

            <dl>
                <dt class="control-label">
                    Anticipated Run Times
                </dt>
                <dd>
                    <div class="ui bulleted list">
                        <g:each in="${d.getAllNextUpdateTimestamp()}" var="updateDate">
                            <div class="item">${updateDate}</div>
                        </g:each>
                    </div>
                </dd>
            </dl>
        </g:if>
    </div>
</div>

<div class="sixteen wide column">
    <div class="ui two doubling stackable cards">
        <div class="ui card">
            <div class="content wekb-inline-lists">
                <div class="header">
                    <g:if test="${d.defaultSupplyMethod == wekb.helper.RDStore.KS_DSMETHOD_HTTP_URL}">
                        <div class="ui green ribbon label">Activ</div>
                    </g:if>
                    <g:else>
                        <div class="ui red ribbon label">Not activ</div>
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
                            <semui:xEditable owner="${d}" field="lastUpdateUrl" overwriteEditable="${false}"
                                             outGoingLink="true"
                                             disabled="${createObject}"/>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>

        <div class="ui card">
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
    </div>
</div>

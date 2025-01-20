<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory;" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                <g:message code="package.name"/>
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="name" required="true"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label"><g:message code="package.provider.name"/></dt>
            <dd><semui:xEditableManyToOne owner="${d}" field="provider" baseClass="wekb.Org" onylMyComponents="true"/></dd>
        </dl>

        <g:if test="${controllerName != 'create'}">
            <dl>
                <dt class="control-label">
                    <g:message code="public.vendors"/>
                </dt>
                <dd>
                    <g:render template="/templates/packageVendors"/>
                </dd>
            </dl>
        </g:if>

        <dl>
            <dt class="control-label"><g:message code="package.source"/></dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditableManyToOne owner="${d}" field="kbartSource" baseClass="wekb.KbartSource" onylMyComponents="true"/>
                </g:if>
                <g:if test="${d.kbartSource}">
                    <p>(<b>Frequency:</b> <semui:xEditableRefData owner="${d.kbartSource}" field="frequency" config="${RCConstants.SOURCE_FREQUENCY}" overwriteEditable="false"/>, <b>AutomaticUpdates:</b> <semui:xEditableBoolean owner="${d.kbartSource}" field="automaticUpdates" overwriteEditable="false"/>, <b>LastRun:</b> <g:formatDate format="${message(code: 'default.date.format.noZ')}" date="${d.kbartSource.lastRun}"/>)</p>
                </g:if>
            </dd>
        </dl>

        <dl>
            <dt class="control-label"><g:message code="package.nominalPlatform.name"/></dt>
            <dd><semui:xEditableManyToOne owner="${d}" field="nominalPlatform"
                                          baseClass="wekb.Platform" onylMyComponents="true"/></dd>
        </dl>
        <dl>
            <dt class="control-label"><g:message code="default.status"/></dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.COMPONENT_STATUS}" overwriteEditable="${controllerName == 'create' ? 'true' : 'false' }"/>
            </dd>
        </dl>
        <g:if test="${controllerName != 'create' && (d.getCountManualUpdateInfos() > 0 || d.getCountAutoUpdateInfos() > 0)}">
            <dl>
                <dt class="control-label"><g:message code="package.lastUpdateComment"/></dt>
                <dd><semui:xEditable owner="${d}" field="lastUpdateComment" overwriteEditable="false"/>
                    <br>
                    <br>
                    <g:link class="ui mini primary button" id="${d.id}" controller="package"
                            action="packageChangeHistory">Change History</g:link>
                </dd>
            </dl>
        </g:if>
        <dl>
            <dt class="control-label"><g:message code="package.description"/></dt>
            <dd><g:if test="${!createObject}">
                <semui:xEditable owner="${d}" type="textarea" field="description"/>
            </g:if>
            </dd>

        </dl>
        <dl>
            <dt class="control-label"><g:message code="package.descriptionURL"/></dt>
            <dd><g:if test="${!createObject}">
                <semui:xEditable owner="${d}" field="descriptionURL" validation="url" outGoingLink="true"/>
            </g:if>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                <g:message code="package.breakable"/>
            </dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditableRefData owner="${d}" field="breakable" config="${RCConstants.PACKAGE_BREAKABLE}"/>
                </g:if>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="package.contentType"/>
            </dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditableRefData owner="${d}" field="contentType"
                                            config="${RCConstants.PACKAGE_CONTENT_TYPE}"/>
                </g:if>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="package.file"/>
            </dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditableRefData owner="${d}" field="file" config="${RCConstants.PACKAGE_FILE}"/>
                </g:if>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="package.openAccess"/>
            </dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditableRefData owner="${d}" field="openAccess" config="${RCConstants.PACKAGE_OPEN_ACCESS}"/>
                </g:if>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="package.paymentType"/>
            </dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditableRefData owner="${d}" field="paymentType"
                                            config="${RCConstants.PACKAGE_PAYMENT_TYPE}"/>
                </g:if>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="package.scope"/>
            </dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditableRefData owner="${d}" field="scope" config="${RCConstants.PACKAGE_SCOPE}"/>
                </g:if>
            </dd>
        </dl>


        <g:if test="${controllerName != 'create'}">
            <dl>
                <dt class="control-label">
                    <g:message code="package.nationalRanges"/>
                </dt>
                <dd>
                    <g:if test="${d.scope?.value == 'National'}">
                        <g:render template="/templates/nationalRange"/>
                    </g:if>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    <g:message code="package.regionalRanges"/>
                </dt>
                <dd>
                    <g:if test="${RefdataCategory.lookup(RCConstants.COUNTRY, 'DE') in d.nationalRanges && d.scope?.value == 'National'}">
                        <g:render template="/templates/regionalRange"/>
                    </g:if>
                </dd>
            </dl>
        </g:if>

        <dl>
            <dt class="control-label">
                <g:message code="package.freeTrial"/>
            </dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditableRefData owner="${d}" field="freeTrial" config="${RCConstants.YN}"/>
                </g:if>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                <g:message code="package.freeTrialPhase"/>
            </dt>
            <dd>
                <g:if test="${!createObject}">
                    <semui:xEditable owner="${d}" field="freeTrialPhase"/>
                </g:if>
            </dd>
        </dl>

        <g:if test="${controllerName != 'create'}">
            <dl>
                <dt class="control-label">
                    Archiving Agency
                </dt>
                <dd>
                    <table class="ui small selectable striped celled table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Archiving Agency</th>
                            <th>Open Access</th>
                            <th>Post-Cancellation Access (PCA)</th>
                            <g:if test="${editable}"><th>Actions</th></g:if>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${d.paas?.sort { it.archivingAgency?.value }}" var="paa" status="i">
                            <tr>
                                <td>${i + 1}</td>
                                <td><semui:xEditableRefData owner="${paa}" field="archivingAgency"
                                                            config="${RCConstants.PAA_ARCHIVING_AGENCY}"/>
                                </td>
                                <td><semui:xEditableRefData owner="${paa}" field="openAccess"
                                                            config="${RCConstants.PAA_OPEN_ACCESS}"/>
                                </td>
                                <td>
                                    <semui:xEditableRefData owner="${paa}" field="postCancellationAccess"
                                                            config="${RCConstants.PAA_POST_CANCELLATION_ACCESS}"/>
                                </td>
                                <g:if test="${editable}">
                                    <td>
                                        <g:link controller='ajaxHtml'
                                                action='delete'
                                                params="${["__context": "${paa.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>
                                    </td>
                                </g:if>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>

                    <g:if test="${editable}">
                        <a class="ui right floated primary button" href="#"
                           onclick="$('#paaModal').modal('show');"><g:message code="default.add.label" args="['Archiving Agency']"/>y</a>

                        <br>
                        <br>
                    </g:if>
                </dd>
            </dl>
        </g:if>


        <g:if test="${editable && controllerName != 'create'}">
            <semui:modal id="paaModal" title="${g.message(code: "default.add.label", args: ['Archiving Agency'])}">

                <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
                    <input type="hidden" name="__context" value="${d.getOID()}"/>
                    <input type="hidden" name="__newObjectClass" value="wekb.PackageArchivingAgency"/>
                    <input type="hidden" name="__recip" value="pkg"/>
                    <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                    <div class="field">
                        <label>Archiving Agency</label>
                        <semui:simpleReferenceDropdown name="archivingAgency"
                                                       baseClass="wekb.RefdataValue"
                                                       filter1="${RCConstants.PAA_ARCHIVING_AGENCY}"/>
                    </div>

                    <div class="field">
                        <label>Open Access</label>
                        <semui:simpleReferenceDropdown name="openAccess"
                                                       baseClass="wekb.RefdataValue"
                                                       filter1="${RCConstants.PAA_OPEN_ACCESS}"/>
                    </div>

                    <div class="field">
                        <label>Post-Cancellation Access (PCA)</label>
                        <semui:simpleReferenceDropdown name="postCancellationAccess"
                                                       baseClass="wekb.RefdataValue"
                                                       filter1="${RCConstants.PAA_POST_CANCELLATION_ACCESS}"/>
                    </div>
                </g:form>
            </semui:modal>
        </g:if>

    </div>
</div>
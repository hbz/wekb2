<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory;" %>
<dl>
    <dt class="control-label">
        Name
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="name" required="true"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">Provider</dt>
    <dd><semui:xEditableManyToOne owner="${d}" field="provider" baseClass="wekb.Org"/></dd>
</dl>

<dl>
    <dt class="control-label">Source</dt>
    <dd><semui:xEditableManyToOne owner="${d}" field="kbartSource" baseClass="wekb.KbartSource"
                                  disabled="${createObject}"/></dd>
</dl>

<dl>
    <dt class="control-label">Nominal Platform</dt>
    <dd><semui:xEditableManyToOne owner="${d}" field="nominalPlatform"
                                  baseClass="wekb.Platform"/></dd>
</dl>
<dl>
    <dt class="control-label">Status</dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}"/>
    </dd>
</dl>
<g:if test="${controllerName != 'create' && (d.getCountManualUpdateInfos() > 0 || d.getCountAutoUpdateInfos() > 0)}">
    <dl>
        <dt class="control-label">Last Update Comment</dt>
        <dd><semui:xEditable owner="${d}" field="lastUpdateComment" overwriteEditable="false"/>
            <br>
            <br>
            <g:link class="ui mini black button" id="${d.id}" controller="package"
                    action="packageChangeHistory">Change History</g:link>
        </dd>
    </dl>
</g:if>
<dl>
    <dt class="control-label">Description</dt>
    <dd><semui:xEditable owner="${d}" type="textarea" field="description" disabled="${createObject}"/></dd>

</dl>
<dl>
    <dt class="control-label">Description URL</dt>
    <dd><semui:xEditable owner="${d}" field="descriptionURL" validation="url" outGoingLink="true"
                         disabled="${createObject}"/>
    </dd>
</dl>

<dl>
    <dt class="control-label">
        Breakable
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="breakable" config="${RCConstants.PACKAGE_BREAKABLE}"
                                disabled="${createObject}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Content Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="contentType"
                                config="${RCConstants.PACKAGE_CONTENT_TYPE}" disabled="${createObject}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        File
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="file" config="${RCConstants.PACKAGE_FILE}"
                                disabled="${createObject}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Open Access
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="openAccess" config="${RCConstants.PACKAGE_OPEN_ACCESS}"
                                disabled="${createObject}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Payment Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="paymentType"
                                config="${RCConstants.PACKAGE_PAYMENT_TYPE}" disabled="${createObject}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Scope
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="scope" config="${RCConstants.PACKAGE_SCOPE}"
                                disabled="${createObject}"/>
    </dd>
</dl>


<g:if test="${controllerName != 'create'}">
    <dl>
        <dt class="control-label">
            National Range
        </dt>
        <dd>
            <g:if test="${d.scope?.value == 'National'}">
                <g:render template="/templates/nationalRange"/>
            </g:if>
        </dd>
    </dl>
    <dl>
        <dt class="control-label">
            Regional Range
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
        Free Trial
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="freeTrial" config="${RCConstants.YN}" disabled="${createObject}"/>
    </dd>
</dl>

<dl>
    <dt class="control-label">
        Free Trial Phase
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="freeTrialPhase" disabled="${createObject}"/>
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
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${d.paas?.sort { it.archivingAgency?.value }}" var="paa" status="i">
                    <tr>
                        <td>${i + 1}</td>
                        <td><semui:xEditableRefData owner="${paa}" field="archivingAgency"
                                                    config="${RCConstants.PAA_ARCHIVING_AGENCY}"/>
                        <td><semui:xEditableRefData owner="${paa}" field="openAccess"
                                                    config="${RCConstants.PAA_OPEN_ACCESS}"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${paa}" field="postCancellationAccess"
                                                    config="${RCConstants.PAA_POST_CANCELLATION_ACCESS}"/>
                        </td>
                        <td>
                            <g:if test="${editable}">
                                <g:link controller='ajaxHtml'
                                        action='delete'
                                        params="${["__context": "${paa.class.name}:${paa.id}", curationOverride: params.curationOverride]}">Delete</g:link>
                            </g:if>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <g:if test="${editable}">
                <a class="ui right floated black button" href="#"
                   onclick="$('#paaModal').modal('show');">Add Archiving Agency</a>

                <br>
                <br>
            </g:if>
        </dd>
    </dl>
</g:if>


<g:if test="${editable && controllerName != 'create'}">
    <semui:modal id="paaModal" title="Add Archiving Agency">

        <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
            <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
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


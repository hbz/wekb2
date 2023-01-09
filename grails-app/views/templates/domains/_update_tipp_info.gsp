<%@ page import="wekb.helper.RCConstants" %>
<dl>
    <dt class="control-label">
        Package Update Info
    </dt>
    <dd>
        <g:if test="${d.updatePackageInfo}">
            <g:link controller="resource" action="show"
                    id="${d.updatePackageInfo.class.name}:${d.updatePackageInfo.id}">
                ${(d.updatePackageInfo.pkg.name) ?: 'Empty'}
            </g:link>
        </g:if>
        <g:else>Empty</g:else>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Title
    </dt>
    <dd>
        <g:if test="${d.tipp}">
            <g:link controller="resource" action="show"
                    id="${d.tipp.uuid}">
                ${(d.tipp.name) ?: 'Empty'}
            </g:link>
        </g:if>
        <g:else>Empty</g:else>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Description
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="description" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Status
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.UPDATE_STATUS}" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="type" config="${RCConstants.UPDATE_TYPE}" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Start Time
    </dt>
    <dd>
        <semui:xEditable owner="${d}" type="date"
                         field="startTime" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        End Time
    </dt>
    <dd>
        <semui:xEditable owner="${d}" type="date"
                         field="endTime" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        KBART Field
    </dt>
    <dd>
        <semui:xEditable owner="${d}"
                         field="kbartProperty" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Old Value
    </dt>
    <dd>
        <semui:xEditable owner="${d}"
                         field="oldValue" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        New value
    </dt>
    <dd>
        <semui:xEditable owner="${d}"
                         field="newValue" overwriteEditable="false"/>
    </dd>
</dl>




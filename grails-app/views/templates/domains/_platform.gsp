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
        %{--<dl>
            <dt class="control-label">
                Title Namespace
            </dt>
            <dd>
                <semui:xEditableManyToOne owner="${d}" field="titleNamespace" baseClass="wekb.IdentifierNamespace"
                                          filter1="TitleInstancePackagePlatform" >${(d.titleNamespace?.name) ?: d.titleNamespace?.value}</semui:xEditableManyToOne>
            </dd>
        </dl>--}%

    </div>
</div>
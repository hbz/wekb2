<%@ page import="de.wekb.helper.RCConstants" %>
<dl>
    <dt class="control-label">
        Name
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
        <sec:ifAnyGranted roles="ROLE_SUPERUSER">
            <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}"/>
        </sec:ifAnyGranted>
        <sec:ifNotGranted roles="ROLE_SUPERUSER">
            ${d.status?.value ?: 'Not Set'}
        </sec:ifNotGranted>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Provider
    </dt>
    <dd>
        <semui:xEditableManyToOne owner="${d}" field="provider"
                                  baseClass="org.gokb.cred.Org"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Primary URL
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="primaryUrl" validation="url" outGoingLink="true"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Title Namespace
    </dt>
    <dd>
        <semui:xEditableManyToOne owner="${d}" field="titleNamespace" baseClass="org.gokb.cred.IdentifierNamespace"
                                  filter1="TitleInstancePackagePlatform">${(d.titleNamespace?.name) ?: d.titleNamespace?.value}</semui:xEditableManyToOne>
    </dd>
</dl>
<dl>
    <dt class="control-label">IP Auth Supported
    <dd><semui:xEditableRefData owner="${d}" field="ipAuthentication"
                                config="${RCConstants.PLATFORM_IP_AUTH}"/></dd>
</dl>
<dl>
    <dt class="control-label">Open Athens Supported</dt>
    <dd><semui:xEditableRefData owner="${d}" field="openAthens"
                                config="${RCConstants.YN}"/></dd>
</dl>
<dl>
    <dt class="control-label">Shibboleth Supported</dt>
    <dd><semui:xEditableRefData owner="${d}" field="shibbolethAuthentication"
                                config="${RCConstants.YN}"/></dd>
</dl>
<dl>
    <dt class="control-label">User/Pass Supported</dt>
    <dd><semui:xEditableRefData owner="${d}" field="passwordAuthentication"
                                config="${RCConstants.YN}"/></dd>
</dl>
<dl>
    <dt class="control-label">Proxy Supported</dt>
    <dd><semui:xEditableRefData owner="${d}" field="proxySupported"
                                config="${RCConstants.YN}"/></dd>
</dl>


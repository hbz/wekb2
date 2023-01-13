
<g:if test="${d.id != null}">
    <dl>
        <dt class="control-label">User Name</dt>
        <dd>${d.username}</dd>
    </dl>
    <dl>
        <dt class="control-label">Display Name</dt>
        <dd><semui:xEditable owner="${d}" field="displayName"/></dd>
    </dl>
    <g:if test="${d == request.user || request.user.isAdmin()}">
        <dl>
            <dt class="control-label">Email</dt>
            <dd>
                <semui:xEditable owner="${d}" field="email" validation="email"/>
            </dd>
        </dl>
    </g:if>

    <g:if test="${d == request.user || request.user.isAdmin()}">
        <dl>
            <dt class="control-label">Curatory Groups</dt>
            <dd>
                <g:render template="/apptemplates/secondTemplates/curatory_groups" model="${[d: d, linkToCur: true]}"/>
            </dd>
        </dl>
    </g:if>
    <g:else>
        <dl>
            <dt class="control-label">Curatory Groups</dt>
            <dd>
                <ul>
                    <g:each in="${d.curatoryGroups}" var="curatoryGroup">
                        <li><g:link controller="resource" action="show"
                                    id="${curatoryGroup.class.name}:${curatoryGroup.id}">${curatoryGroup.displayName}</g:link></li>
                    </g:each>
                </ul>
            </dd>
        </dl>
    </g:else>

%{--    <dl>
        <dt class="control-label">Organisations</dt>
        <dd>
            <ul>
                <g:each in="${d.getGroupMemberships()}" var="oum">
                    <li><g:link controller="resource" action="show"
                                id="${oum.memberOf.class.name}:${oum.memberOf.id}">${oum.memberOf.displayName} – (${oum.role?.value ?: 'Not Set'})</g:link></li>
                </g:each>
            </ul>
        </dd>
    </dl>--}%


%{--<div id="content">
    <ul id="tabs" class="nav nav-tabs">
        <li class="active"><a href="#roles" data-toggle="tab">Roles</a></li>
    </ul>

    <div id="my-tab-content" class="tab-content">
        <div class="tab-pane active" id="roles">
            <g:link class="display-inline" controller="security" action="roles"
                    params="${['id': (d.class.name + ':' + d.id)]}"></g:link>
        </div>
    </div>
</div>--}%
</g:if>

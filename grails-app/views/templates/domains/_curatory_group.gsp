<%@ page import="wekb.helper.RCConstants" %>
<dl>

    <dt class="control-label">Curatory Group Name</dt>
    <dd><semui:xEditable owner="${d}" field="name" required="true"/></dd>
</dl>
<dl>
    <dt class="control-label">Type</dt>
    <dd><semui:xEditableRefData owner="${d}" field="type" config="${RCConstants.CURATORY_GROUP_TYPE}" required="true"/></dd>
</dl>

<g:if test="${d.id != null}">
    <dl>
        <dt class="control-label">Status</dt>
        <dd><semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}"/></dd>
    </dl>

    <sec:ifAnyGranted roles="ROLE_ADMIN">
        <dl>
            <dt class="control-label">
                Owner
            </dt>
            <dd>
                <semui:xEditableManyToOne owner="${d}" field="owner"
                                          baseClass="wekb.auth.User">${d.owner?.username}</semui:xEditableManyToOne>
            </dd>
        </dl>
    </sec:ifAnyGranted>
    <g:if test="${sec.ifLoggedIn() && (user.isAdmin())}">
        <dl>
            <dt class="control-label">Members</dt>
            <dd>
                <g:if test="${d.curatoryGroupUsers}">
                    <ul>
                        <g:each var="u" in="${d.curatoryGroupUsers}">
                            <sec:ifAnyGranted roles="ROLE_ADMIN">
                                <li><a href="mailto:${u.email}"><i class="fa fa-envelope"></i>&nbsp;</a><g:link
                                        controller="resource" action="show"
                                        id="${u.getLogEntityId()}">${u.displayName ?: u.username}</g:link></li>
                            </sec:ifAnyGranted>
                            <sec:ifNotGranted roles="ROLE_ADMIN">
                                <li>${u.displayName ?: u.username}</li>
                            </sec:ifNotGranted>
                        </g:each>
                    </ul>
                </g:if>
                <g:else>
                    <p>There are no members of this curatory group.</p>
                </g:else>
            </dd>
        </dl>
    </g:if>
</g:if>

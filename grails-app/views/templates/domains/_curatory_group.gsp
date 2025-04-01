<%@ page import="wekb.helper.RCConstants" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label"><g:message code="curatorygroup.name"/></dt>
            <dd><semui:xEditable owner="${d}" field="name" required="true"/></dd>
        </dl>
        <dl>
            <dt class="control-label"><g:message code="curatorygroup.type"/></dt>
            <dd><semui:xEditableRefData owner="${d}" field="type" config="${RCConstants.CURATORY_GROUP_TYPE}"
                                        required="true"/></dd>
        </dl>

        <dl>
            <dt class="control-label"><g:message code="default.status"/></dt>
            <dd><semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.COMPONENT_STATUS}"/></dd>
        </dl>

        <g:if test="${d.id != null}">
            <sec:ifAnyGranted roles='ROLE_ADMIN'>
                <dl>
                    <dt class="control-label">Members</dt>
                    <dd>
                        <g:if test="${d.curatoryGroupUsers}">
                            <ul>
                                <g:each var="curatoryGroupUser" in="${d.curatoryGroupUsers}">
                                    <li><g:link
                                            controller="resource" action="show"
                                            id="${curatoryGroupUser.user.id}">${curatoryGroupUser.user.username}
                                    </g:link>&nbsp;&nbsp;
                                        <a class="ui icon" href="mailto:${curatoryGroupUser.user.email}"><i
                                                class="envelope icon"></i></a>
                                    </li>
                                </g:each>
                            </ul>
                        </g:if>
                        <g:else>
                            <p>There are no members of this curatory group.</p>
                        </g:else>
                    </dd>
                </dl>
            </sec:ifAnyGranted>
        </g:if>
    </div>
</div>

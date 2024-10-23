<dl>
    <dt class="control-label"><g:message code="package.dateCreated"/></dt>
    <dd>
        <g:formatDate format="${message(code: 'default.date.format.noZ')}"
                      date="${d.dateCreated}"/>
    </dd>
    <dt class="control-label"><g:message code="package.lastUpdated"/></dt>
    <dd>
        <g:formatDate format="${message(code: 'default.date.format.noZ')}"
                      date="${d.lastUpdated}"/>
    </dd>
    <g:if test="${d.hasProperty('uuid')}">
        <dt class="control-label">UUID</dt>
        <dd>
            ${d.uuid}
        </dd>
    </g:if>
    <sec:ifAnyGranted roles="ROLE_ADMIN">
        <dt class="control-label">DB-ID</dt>
        <dd>
            ${d.id}
        </dd>

        <g:if test="${curatoryGroups || d.hasProperty('curatoryGroups')}">

            <g:if test="${curatoryGroups}">
                <dt class="control-label">User-Emails:</dt>
                <dd>
                    <div class="ui bulleted list">
                        <g:each in="${curatoryGroups.sort { it.name }}" var="curatoryGroup">
                            <g:each in="${curatoryGroup.curatoryGroupUsers.sort { it.user?.email }}" var="curatoryGroupUser">
                                <g:if test="${curatoryGroupUser.user.email}">
                                    <div class="item">
                                        ${curatoryGroupUser.user.email}
                                        <a href="mailto:${curatoryGroupUser.user.email}" class="ui icon">
                                            <i class="mail icon"></i>
                                        </a>
                                    </div>
                                </g:if>
                            </g:each>
                        </g:each>
                    </div>
                </dd>
            </g:if>

        </g:if>

    </sec:ifAnyGranted>
</dl>

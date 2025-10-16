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
                    <div class="ui middle aligned divided list">
                        <g:each in="${curatoryGroups.sort { it.name }}" var="curatoryGroup">
                            <g:each in="${curatoryGroup.curatoryGroupUsers.sort { it.user?.email }}" var="curatoryGroupUser">
                                <g:if test="${curatoryGroupUser.user.email}">
                                    <div class="item">
                                        <div class="right floated content">
                                            <a href="mailto:${curatoryGroupUser.user.email}" class="ui icon button">
                                                <i class="mail icon"></i>
                                            </a>
                                        </div>
                                        <div class="content">
                                            <div class="header">

                                                <span class="js-copyTriggerParent">
                                                    <span class="ui small basic image label js-copyTrigger la-popup-tooltip" data-position="top center" data-tooltip="${message(code: 'tooltip.clickToCopy', args: ['E-Mail'])}">
                                                        <i class="copy black icon la-js-copyTriggerIcon"></i>
                                                        <span class="detail js-copyTopic">${curatoryGroupUser.user.email}</span>
                                                    </span>
                                                </span>
                                            </div>
                                        </div>
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

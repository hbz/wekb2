<wekb:serviceInjection/>

<g:set var="userIsAdmin" value="${springSecurityService.currentUser.isAdmin()}"/>

<dl>
    <dt class="control-label">Display Name</dt>
    <dd><semui:xEditable owner="${d}" field="displayName"/></dd>
</dl>
<g:if test="${d == springSecurityService.currentUser || userIsAdmin}">
    <dl>
        <dt class="control-label">Email</dt>
        <dd>
            <semui:xEditable owner="${d}" field="email" validation="email"/>
        </dd>
    </dl>
    <dl>
        <dt class="control-label">Username</dt>
        <dd>
            <semui:xEditable owner="${d}" field="username"/>
        </dd>
    </dl>

    <g:if test="${userIsAdmin}">
        <g:if test="${controllerName == 'create'}">
            <dl>
                <dt class="control-label">Password</dt>
                <dd>
                    <semui:xEditable owner="${d}" field="password" type="password"/>
                </dd>
            </dl>
        </g:if>
        <dl>
            <dt class="control-label">Enabled</dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="enabled"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">Account Expired</dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="accountExpired"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">Account Locked</dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="accountLocked"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">Password Expired</dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="passwordExpired"/>
            </dd>
        </dl>
    </g:if>

    <g:if test="${d.id != null}">
        <semui:tabs>
            <semui:tabsItemWithoutLink tab="curatoryGroupUsers" defaultTab="curatoryGroupUsers"
                                       activeTab="${params.activeTab}" counts="${d.curatoryGroupUsers.size()}">
                Curatory Groups
            </semui:tabsItemWithoutLink>
            <g:if test="${userIsAdmin}">
                <semui:tabsItemWithoutLink tab="roles" activeTab="${params.activeTab}" counts="${d.roles.size()}">
                    Roles
                </semui:tabsItemWithoutLink>
            </g:if>
        </semui:tabs>

        <semui:tabsItemContent tab="curatoryGroupUsers" activeTab="${params.activeTab}">
            <table class="ui selectable striped sortable celled table">
                <thead>
                <tr>
                    <th>Curatory Group</th>
                    <g:if test="${userIsAdmin}">
                        <th>Actions</th>
                    </g:if>
                </tr>
                </thead>
                <tbody>
                <g:if test="${d.curatoryGroupUsers.size() > 0}">
                    <g:each in="${d.curatoryGroupUsers.sort { it.curatoryGroup.name }}" var="curatoryGroupUser">
                        <tr>
                            <td><g:link controller="resource" action="show"
                                        id="${curatoryGroupUser.curatoryGroup.getClass().name}:${curatoryGroupUser.curatoryGroup.id}">${curatoryGroupUser.curatoryGroup.name}</g:link></td>
                            <g:if test="${userIsAdmin}">
                                <td>
                                    <g:link controller="ajaxHtml" action="removeCuratoryGroupFromUser"
                                            class="confirm-click"
                                            data-confirm-message="Are you sure you wish to unlink ${curatoryGroupUser.curatoryGroup.name}?"
                                            params="${["us_Id": curatoryGroupUser.user.id, "cur_Id": curatoryGroupUser.curatoryGroup.id, activeTab: "curatoryGroupUsers"]}">Delete</g:link>
                                </td>
                            </g:if>
                        </tr>
                    </g:each>
                </g:if>
                <g:else>
                    <tr>
                        <td colspan="2">There are currently no linked Curatory Groups</td>
                    </tr>
                </g:else>
                </tbody>
            </table>

            <g:if test="${userIsAdmin}">

                <a class="ui right floated black button" href="#"
                   onclick="$('#addCuratoryGroup').modal('show');">Add a Curatory Group</a>

                <br>
                <br>

                <semui:modal id="addCuratoryGroup" title="Add a Curatory Group">
                    <g:form controller="ajaxHtml" action="addUserToCuratoryGroup" class="ui form">
                        <input type="hidden" name="__user" value="${d.getClass().name}:${d.id}"/>
                        <input type="hidden" name="activeTab" value="curatoryGroupUsers"/>

                        <div class="field">
                            <semui:simpleReferenceDropdown name="__curatoryGroup"
                                                           baseClass="wekb.CuratoryGroup"
                                                           filter1="Current"/>
                        </div>

                    </g:form>
                </semui:modal>
            </g:if>
        </semui:tabsItemContent>
    </g:if>
</g:if>
<g:if test="${d.id != null}">
    <g:if test="${userIsAdmin}">
        <semui:tabsItemContent tab="roles" activeTab="${params.activeTab}">
            <table class="ui selectable striped sortable celled table">
                <thead>
                <tr>
                    <th>Role</th>
                    <g:if test="${editable}">
                        <th>Actions</th>
                    </g:if>
                </tr>
                </thead>
                <tbody>
                <g:each in="${d.roles.sort { it.role.authority }}" var="userRole">
                    <tr>
                        <td>
                            ${userRole.role.authority}
                        </td>
                        <g:if test="${editable}">
                            <td><g:link controller='ajaxHtml'
                                        action='removeRoleFromUser'
                                        params="${["us_Id": "${userRole.user.id}", "rol_Id": "${userRole.role.id}", activeTab: 'roles']}">Delete</g:link>
                            </td>
                        </g:if>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <g:if test="${userIsAdmin}">

                <a class="ui right floated black button" href="#"
                   onclick="$('#addRoleToUser').modal('show');">Add Role</a>

                <br>
                <br>

                <semui:modal id="addRoleToUser" title="Add Role">
                    <g:form controller="ajaxHtml" action="addRoleToUser" class="ui form">
                        <input type="hidden" name="__user" value="${d.getClass().name}:${d.id}"/>
                        <input type="hidden" name="activeTab" value="roles"/>

                        <div class="field">
                            <semui:simpleReferenceDropdown name="__role"
                                                           baseClass="wekb.auth.Role"
                                                           filter1="Current"/>
                        </div>

                    </g:form>
                </semui:modal>
            </g:if>
        </semui:tabsItemContent>
    </g:if>

</g:if>

<%@page import="org.gokb.cred.CuratoryGroup; org.gokb.cred.TitleInstancePackagePlatform;"%>
<g:set var="curatoryGroups" value="${(d instanceof TitleInstancePackagePlatform && d.pkg) ? d.pkg.curatoryGroups : d.curatoryGroups }" />

<g:set var="cur_editable" value="${ (editable && linkToCur) || ((editable && d.class.name != 'org.gokb.cred.User' ) && ((curatoryGroups?.size() == 0) || (request.curator?.size() > 0) || (params.curationOverride == "true" && request.user.isAdmin()))) }" />
<g:set var="editable" value="${ d == user || (editable && ((curatoryGroups ? (request.curator != null && request.curator.size() > 0) : true) || (params.curationOverride == 'true' && request.user.isAdmin())) ) }" />

<table class="ui selectable striped sortable celled table">
  <thead>
    <tr>
      <th>Curatory Group</th>
      <g:if test="${cur_editable && editable}">
        <th>Actions</th>
      </g:if>
    </tr>
  </thead>
  <tbody>
    <g:if test="${ curatoryGroups.size() > 0 }" >
      <g:each in="${curatoryGroups}" var="t">
        <tr>
          <td><g:link controller="resource" action="show" id="${t.getClassName()}:${t.id}"> ${t.name}</g:link></td>
          <g:if test="${cur_editable && editable && !(d instanceof TitleInstancePackagePlatform)}">
            <td>
                <g:link controller="ajaxSupport" action="unlinkManyToMany" class="confirm-click" data-confirm-message="Are you sure you wish to unlink ${ t.name }?" params="${ ["__property":"curatoryGroups", "__context":d.getClassName() + ":" + d.id, "__itemToRemove" : t.getClassName() + ":" + t.id, "propagate": "true"] }" >Delete</g:link>
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
    <g:if test="${cur_editable && editable && !(d instanceof TitleInstancePackagePlatform)}">
      <tr>
          <th colspan="2">Link a Curatory Group</th>
      </tr>
      <tr>
        <g:form controller="ajaxSupport" action="addToStdCollection">
          <td colspan="2">
            <input type="hidden" name="__context" value="${d.getClassName()}:${d.id}"/>
            <input type="hidden" name="__property" value="curatoryGroups"/>
              <div class="input-group" style="width:100%;">
                <semui:simpleReferenceDropdown  name="__relatedObject" baseClass="org.gokb.cred.CuratoryGroup" filter1="Current"/>
                <span class="input-group-btn" style="padding: 0px 10px;vertical-align:top;">
                  <button type="submit" class="ui black button">Link</button>
                </span>
              </div>
               %{-- <sec:ifAnyGranted roles="ROLE_SUPERUSER">
                  <p>
                  <g:link controller="create" params="${["tmpl": "org.gokb.cred.CuratoryGroup"]}">New Curatory Group</g:link>
                  </p>
                </sec:ifAnyGranted>--}%
          </td>
        </g:form>
      </tr>
    </g:if>
  </tbody>
</table>

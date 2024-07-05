
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
	</sec:ifAnyGranted>
</dl>

<g:each var="entry" in="${rd.sort{it.value.title}}">
	<g:if test="${ (dtype ? entry.key.startsWith(dtype + '.' ) : true) && !(entry.key in notShowProps)}">
		<dt>
				${ entry.value.title }
		</dt>
		<dd>
			<semui:xEditableRefData owner="${d}" field="${entry.value.name}"
				config="${entry.key}" />
		</dd>
	</g:if>
</g:each>

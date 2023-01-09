<dl class="dl-horizontal">
	<dt>
		Shortcode
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="shortcode" />
	</dd>

	<g:if test="${ d.ids.size() > 0 }">
		<dt>
			Identifiers
		</dt>
		<dd>
			<ul>
				<g:each in="${d.ids}" var="id">
					<li>
						${id.namespace.value}:${id.value}
					</li>
				</g:each>
			</ul>
		</dd>
	</g:if>
	<g:if test="${!d.id || (d.id && d.name)}">
		<dt>
				${ d.getNiceName() } Name
		</dt>
		<dd>
			<semui:xEditable  owner="${d}" field="name" />
		</dd>
	</g:if>

	<g:if test="${d.id != null}">
		<g:render template="/apptemplates/secondTemplates/refdataprops"
			model="${[d:(d), rd:(rd), dtype:(dtype)]}" />
	</g:if>
</dl>

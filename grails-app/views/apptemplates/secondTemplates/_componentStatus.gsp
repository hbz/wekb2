<g:if test="${d.id}">
    <dl class="dl-horizontal">
        <dt>Date Created</dt>
        <dd>
            ${d.dateCreated ?: ''}
        </dd>
        <dt>Last Updated</dt>
        <dd>
            ${d.lastUpdated ?: ''}
        </dd>
        <dt>UUID</dt>
        <dd>
            ${d.uuid ?: ''}
        </dd>
    </dl>
</g:if>

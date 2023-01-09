<div class="row">
    <div class="col-sm">
        <h2>Titles (${tippsCount})</h2>
    </div>
    <div class="col-sm">
        <g:form controller="public" class="form-group row justify-content-end"   action="${actionName}" method="get" params="${params}">
            <label class="col-sm-6 col-form-label text-right" for="newMax">Results on Page</label>
            <div class="col-sm-6">
                <g:select   name="newMax" from="[10, 25, 50, 100]" value="${params.max}" onChange="this.form.submit()"/>
            </div>
        </g:form>
    </div>
</div>
<table class="ui selectable striped sortable celled table">
    <thead>
    <tr>
        <th>#</th>
        <semui:sortableColumn property="tipp.name" title="Title"/>
        <th>Identifiers</th>
        <th>Platform</th>
        <semui:sortableColumn property="tipp.publicationType" title="Publication Type"/>
        <semui:sortableColumn property="tipp.medium" title="Medium"/>
        <th>Note</th>
        <semui:sortableColumn property="tipp.lastUpdated" title="Last Updated"/>
    </tr>
    </thead>
    <tbody>
    <g:each in="${tipps}" var="t" status="i">
        <tr>
            <td>
                ${ (params.offset ? params.offset.toInteger(): 0)  + i + 1 }
            </td>
            <td>
                <g:link controller="resource" action="show" id="${t.uuid}">
                    ${t.name}
                </g:link>
            </td>
            <td>
                <div class="ui bulleted list">
                    <g:each in="${t.ids.sort{it.namespace.value}}" var="id">
                        <div class="item"><strong>${id.namespace.value}</strong>:<g:link controller="resource" action="show" id="${id.uuid}">  ${id.value}</g:link></div>
                    </g:each>
                </div>
            </td>
            <td>
                <g:link controller="resource" action="show"
                        id="${t.hostPlatform?.uuid}">
                    ${t.hostPlatform?.name}
                </g:link>
            </td>
            <td>${t.publicationType?.value}</td>
            <td>${t.medium?.value}</td>
            <td>
                ${t.note}
            </td>
            <td>
                <g:if test="${t.lastUpdated}">
                    <g:formatDate format="${message(code: 'default.date.format.noZ')}"
                                  date="${t.lastUpdated}"/>
                </g:if>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<g:if test="${tippsCount ?: 0 > 0}">
        <semui:paginate controller="public" action="packageContent" params="${params+[tab: tab]}" max="${max}" total="${tippsCount}"/>
</g:if>
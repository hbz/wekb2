%{--<g:set var="json" value="${d.resultJson}" />--}%
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                Internal ID
            </dt>
            <dd>
                ${d.uuid}
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Description
            </dt>
            <dd>
                ${d.description}
            </dd>
        </dl>
        <dl>
            <dt class="control-label">Status Text
            </dt>
            <dd>
                ${d.statusText}
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Type
            </dt>
            <dd>
                ${d.type?.value}
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Curatory Group
            </dt>
            <dd>
                <g:set var="curatoryGroup" value="${d.getCuratoryGroup()}"/>
                <g:if test="${curatoryGroup}">
                    <g:link controller="resource" action="show"
                            id="${curatoryGroup.uuid}">${curatoryGroup.name}</g:link>
                </g:if>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Linked Component
            </dt>
            <dd>
                <g:set var="item" value="${d.getLinkedItem()}"/>
                <g:if test="${item}">
                    <g:link controller="resource" action="show" id="${item.uuid}">${item.name}</g:link>
                </g:if>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Start Time
            </dt>
            <dd>
                ${d.startTime}
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                End Time
            </dt>
            <dd>
                ${d.endTime}
            </dd>
        </dl>
        %{--
        <dl>
            <dt class="control-label">
                Result Message
            </dt>
            <dd>
                ${json?.message}
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Messages
            </dt>
            <dd>
                <g:if test="${json?.messages}">
                    messages:
                    <ul>
                        <g:each in="${json.messages}" var="m">
                            <g:if test="${m instanceof String}">
                                <li>${m}</li>
                            </g:if>
                            <g:else>
                                <li>${m.message}</li>
                            </g:else>
                        </g:each>
                    </ul>
                </g:if>
                <g:if test="${!json?.messages}">
                    No Messages
                </g:if>
            </dd>

        </dl>
        <dl>
            <dt class="control-label">
                Errors
            </dt>
            <dd>
                <g:if test="${json?.errors?.global}">
                    <div>Global</div>
                    <ul>
                        <g:each in="${json?.errors?.global}" var="ge">
                            <li>${ge}</li>
                        </g:each>
                    </ul>
                </g:if>
                <g:if test="${json?.errors?.tipps}">
                    <div>Titles</div>
                    <ul>
                        <g:each in="${json?.errors?.tipps}" var="te">
                            <li>${te}</li>
                        </g:each>
                    </ul>
                </g:if>
                <g:if test="${!json?.errors}">
                    No Errors
                </g:if>
            </dd>
        </dl>--}%
    </div>
</div>

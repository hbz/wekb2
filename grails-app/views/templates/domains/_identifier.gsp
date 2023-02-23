<%@ page import="wekb.TitleInstancePackagePlatform; wekb.Identifier; wekb.helper.RDStore; wekb.Package; wekb.Org;" %>
<dl>
    <dt class="control-label">Identifier Namespace</dt>
    <dd>${d.namespace?.value}</dd>
</dl>
<dl>
    <dt class="control-label">Identifier</dt>
    <dd>${d.value}</dd>

</dl>

<br>
<br>
<h4 class="ui header">Identified Components with same Identifier:</h4>
<%
    List tippIDs = []
    List pkgIDs = []
    List orgIDs = []

    Identifier.findAllByValue(d.value).each {
        if (it.tipp) {
            tippIDs << it.tipp
        }
        if (it.pkg) {
            pkgIDs << it.pkg
        }
        if (it.org) {
            orgIDs << it.org
        }
    }
%>


<g:if test="${tippIDs}">
    <table class="ui selectable striped celled table">
        <thead>
        <tr>
            <th>Title</th>
            <th>Package</th>
            <g:if test="${editable}">
                <th>Actions</th>
            </g:if>
        </tr>
        </thead>
        <tbody>
        <g:each in="${tippIDs.sort { it.name }}" var="component">
            <tr>
                <td>
                    <g:link controller="resource" action="show" id="${component.uuid}">
                        ${component.name}
                    </g:link> <b>(${component.status?.value})</b>
                </td>
                <td>
                    <g:if test="${component.pkg}">
                        <g:link controller="resource" action="show" id="${component.pkg.uuid}">
                            ${component.pkg.name}
                        </g:link>
                    </g:if>
                </td>
                <g:set var="identifierOfComponent" value="${Identifier.findByValueAndTipp(d.value, component)}"/>
                <g:if test="${editable && identifierOfComponent}">
                    <td>
                        <g:link controller='ajaxHtml'
                                action='delete'
                                params="${["__context": "${identifierOfComponent.class.name}:${identifierOfComponent.id}", 'activeTab': 'identifiers', curationOverride: params.curationOverride]}"
                                class="confirm-click btn-delete"
                                title="Delete this link"
                                data-confirm-message="Are you sure you wish to delete this Identifier from the title ${component}?">Delete</g:link>
                    </td>
                </g:if>
            </tr>
        </g:each>
        </tbody>
    </table>
</g:if>

<g:if test="${pkgIDs}">
    <table class="ui selectable striped celled table">
        <thead>
        <tr>
            <th>Package</th>
            <g:if test="${editable}">
                <th>Actions</th>
            </g:if>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgIDs.sort { it.name }}" var="component">
            <tr>
                <td>
                    <g:link controller="resource" action="show" id="${component.uuid}">
                        ${component.name}
                    </g:link> <b>(${component.status?.value})</b>
                </td>
                <g:set var="identifierOfComponent" value="${Identifier.findByValueAndPkg(d.value, component)}"/>
                <g:if test="${editable && identifierOfComponent}">
                    <td>
                        <g:link controller='ajaxHtml'
                                action='delete'
                                params="${["__context": "${identifierOfComponent.class.name}:${identifierOfComponent.id}", 'activeTab': 'identifiers', curationOverride: params.curationOverride]}"
                                class="confirm-click btn-delete"
                                title="Delete this link"
                                data-confirm-message="Are you sure you wish to delete this Identifier from the title ${component}?">Delete</g:link>
                    </td>
                </g:if>
            </tr>
        </g:each>
        </tbody>
    </table>
</g:if>

<g:if test="${orgIDs}">
    <table class="ui selectable striped celled table">
        <thead>
        <tr>
            <th>Provider</th>
            <g:if test="${editable}">
                <th>Actions</th>
            </g:if>
        </tr>
        </thead>
        <tbody>
        <g:each in="${orgIDs.sort { it.name }}" var="component">
            <tr>
                <td>
                    <g:if test="${controllerName == 'public'}">
                        <g:link controller="public" action="orgContent" id="${component.uuid}">
                            ${component.name}
                        </g:link> <b>(${component.status?.value})</b>
                    </g:if>
                    <g:else>
                        <g:link controller="resource" action="show" id="${component.uuid}">
                            ${component.name}
                        </g:link> <b>(${component.status?.value})</b>
                    </g:else>
                </td>
                <g:set var="identifierOfComponent" value="${Identifier.findByValueAndOrg(d.value, component)}"/>
                <g:if test="${editable && identifierOfComponent}">
                    <td>
                        <g:link controller='ajaxHtml'
                                action='delete'
                                params="${["__context": "${identifierOfComponent.class.name}:${identifierOfComponent.id}", 'activeTab': 'identifiers', curationOverride: params.curationOverride]}"
                                class="confirm-click btn-delete"
                                title="Delete this link"
                                data-confirm-message="Are you sure you wish to delete this Identifier from the title ${component}?">Delete</g:link>
                    </td>
                </g:if>
            </tr>
        </g:each>
        </tbody>
    </table>
</g:if>


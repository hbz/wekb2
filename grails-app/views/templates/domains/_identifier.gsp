<%@ page import="wekb.TitleInstancePackagePlatform; wekb.Identifier; wekb.helper.RDStore; wekb.Package; wekb.Org;" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
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
                    tippIDs << it
                }
                if (it.pkg) {
                    pkgIDs << it
                }
                if (it.org) {
                    orgIDs << it
                }
            }
        %>


        <g:if test="${tippIDs}">
            <table class="ui selectable striped celled table">
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Package</th>
                    <th>Namespace</th>
                    <g:if test="${editable}">
                        <th>Actions</th>
                    </g:if>
                </tr>
                </thead>
                <tbody>
                <g:each in="${tippIDs.sort { it.tipp.name }}" var="identifier">
                    <tr>
                        <td>
                            <g:link controller="resource" action="show" id="${identifier.tipp.getOID()}">
                                ${identifier.tipp.name}
                            </g:link> <strong>(${identifier.tipp.status?.value})</strong>
                        </td>
                        <td>
                            <g:if test="${identifier.tipp.pkg}">
                                <g:link controller="resource" action="show" id="${identifier.tipp.pkg.getOID()}">
                                    ${identifier.tipp.pkg.name}
                                </g:link>
                            </g:if>
                        </td>
                        <td>${identifier.namespace?.value}</td>
                        <g:if test="${editable}">
                            <td>
                                <g:link controller='ajaxHtml'
                                        action='delete'
                                        params="${["__context": "${identifier.getOID()}", 'activeTab': 'identifiers', curationOverride: params.curationOverride]}"
                                        class="confirm-click btn-delete"
                                        title="Delete this link"
                                        data-confirm-message="Are you sure you wish to delete this Identifier from the title ${identifier.tipp}?">Delete</g:link>
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
                    <th>Namespace</th>
                    <g:if test="${editable}">
                        <th>Actions</th>
                    </g:if>
                </tr>
                </thead>
                <tbody>
                <g:each in="${pkgIDs.sort { it.pkg.name }}" var="identifier">
                    <tr>
                        <td>
                            <g:link controller="resource" action="show" id="${identifier.pkg.getOID()}">
                                ${identifier.pkg.name}
                            </g:link> <strong>(${identifier.pkg.status?.value})</strong>
                        </td>
                        <td>${identifier.namespace?.value}</td>
                        <g:if test="${editable}">
                            <td>
                                <g:link controller='ajaxHtml'
                                        action='delete'
                                        params="${["__context": "${identifier.getOID()}", 'activeTab': 'identifiers', curationOverride: params.curationOverride]}"
                                        class="confirm-click btn-delete"
                                        title="Delete this link"
                                        data-confirm-message="Are you sure you wish to delete this Identifier from the title ${identifier.pkg}?">Delete</g:link>
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
                    <th>Namespace</th>
                    <g:if test="${editable}">
                        <th>Actions</th>
                    </g:if>
                </tr>
                </thead>
                <tbody>
                <g:each in="${orgIDs.sort { it.org.name }}" var="identifier">
                    <tr>
                        <td>
                            <g:link controller="resource" action="show" id="${identifier.org.getOID()}">
                                ${identifier.org.name}
                            </g:link> <strong>(${identifier.org.status?.value})</strong>
                        </td>
                        <td>${identifier.namespace?.value}</td>
                        <g:if test="${editable}">
                            <td>
                                <g:link controller='ajaxHtml'
                                        action='delete'
                                        params="${["__context": "${identifier.getOID()}", 'activeTab': 'identifiers', curationOverride: params.curationOverride]}"
                                        class="confirm-click btn-delete"
                                        title="Delete this link"
                                        data-confirm-message="Are you sure you wish to delete this Identifier from the title ${identifier.org}?">Delete</g:link>
                            </td>
                        </g:if>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </g:if>
    </div>
</div>


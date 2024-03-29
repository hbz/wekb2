<%@ page import="wekb.helper.RDStore; wekb.IdentifierNamespace; wekb.helper.RCConstants; wekb.RefdataCategory; wekb.IdentifierNamespace;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Upload Packages</title>
</head>

<body>
<h1 class="ui header">
    Upload Packages completed

    <g:link controller="create" action="packageBatch" class="ui primary button">Back to Upload Packages</g:link>
</h1>

<div class="ui segment">
    <h3 class="ui header">${packages.size()} of ${rowsCount} packages were created/changed</h3>

    <g:set var="counter" value="${1}" />
    <div class="content">
    <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Provider</th>
                <th>Platform</th>
                <th>Source</th>
                <th>Frequency</th>
                <th>Title ID Namespace</th>
                <th>Automated Updates</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${packages}" var="pkg">
                <tr>
                    <td>${counter++}</td>
                    <td>
                        <g:link controller="resource" action="show" id="${pkg.getOID()}">${pkg.name}</g:link>
                    </td>

                    <td>
                        <g:if test="${pkg.provider}">
                            <g:link controller="resource" action="show" id="${pkg.provider.getOID()}">${pkg.provider.name}</g:link>
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.nominalPlatform}">
                            <g:link controller="resource" action="show" id="${pkg.nominalPlatform.getOID()}">${pkg.nominalPlatform.name}</g:link>
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.kbartSource}">
                            <g:link controller="resource" action="show" id="${pkg.kbartSource.getOID()}">${pkg.kbartSource.name}</g:link>
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.kbartSource && pkg.kbartSource.frequency}">
                            ${pkg.kbartSource.frequency.getI10n('value')}
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.kbartSource}">
                            ${IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP).value}
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.kbartSource}">
                            ${pkg.kbartSource.automaticUpdates ? 'Yes' : 'No'}
                        </g:if>
                    </td>

                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>

<div class="ui segment">
    <h3 class="ui header">Package not created/changed due to errors</h3>

    <g:set var="counter" value="${1}" />
    <div class="content">
    <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th>#</th>
                <th>Error</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${errors}" var="error">
                <tr>
                    <td>${counter++}</td>
                    <td>
                        ${error}
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>

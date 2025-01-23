<%@ page import="wekb.IdentifierNamespace; wekb.helper.RDStore;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : KBART Import</title>
</head>

<body>

<semui:flashMessage data="${flash}"/>

<g:if test="${pkg}">

    <h1 class="ui header">KBART Import for Package: ${pkg.name}</h1>

    <div class="ui segment">
        <h3 class="ui header">Information</h3>

        <div class="content">
            The KBART import allows you to update this package with titles.
            <br><br>
            <strong> Please keep in mind that:</strong>

            <div class="ui ordered list">
                <div class="item">The KBART file must be <strong><u>tab-delimited</u></strong> and encoded in <strong><u>UTF-8</u></strong> as recommended by NISO.</div>

                <div class="item">The KBART file must contain <strong><u>all titles</u></strong> for the package, not only those you wish to update.</div>
            </div>
        </div>
    </div>


    <div class="ui segment">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    Package
                </dt>
                <dd>
                    <g:link controller="resource" action="show" id="${pkg.getOID()}">
                        ${pkg.name}
                    </g:link>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">Provider</dt>
                <dd><semui:xEditableManyToOne owner="${pkg}" field="provider" baseClass="wekb.Org"
                                              overwriteEditable="false"/></dd>
            </dl>

            <dl>
                <dt class="control-label">Source</dt>
                <dd><semui:xEditableManyToOne owner="${pkg}" field="kbartSource" baseClass="wekb.KbartSource"
                                              overwriteEditable="false"/></dd>
            </dl>

            <dl>
                <dt class="control-label">Nominal Platform</dt>
                <dd><semui:xEditableManyToOne owner="${pkg}" field="nominalPlatform"
                                              baseClass="wekb.Platform" overwriteEditable="false"/></dd>
            </dl>

            <dl>
                <dt class="control-label">Identifier Namespace for title_id field in KBART</dt>
                <dd><g:set var="idNamespace" value="${IdentifierNamespace.findByValueAndTargetType('title_id', RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)}"/>
                    <g:if test="${idNamespace}">
                        Identifier Namespace Name: ${idNamespace.name}<br>
                        Identifier Namespace Value: ${idNamespace.value}
                    </g:if><g:else>
                    Empty
                </g:else>
                </dd>
            </dl>
        </div>
    </div>

    <g:set var="lastSuccessfulUpdateInfo" value="${pkg.getLastSuccessfulUpdateInfo()}"/>

    <div class="ui segment">
        <h3 class="ui header">KBART Import File</h3>
        <div class="content">
            <g:uploadForm class="ui form" action="processKbartImport" method="post" id="${pkg.id}">
                    <div class="field">
                        <div class="ui checkbox">
                            <input type="checkbox" name="onlyRowsWithLastChanged">
                            <label>Only update titles with the latest last_changed time stamp in your we:kb/KBART file. Last registered date is:
                            <g:if test="${lastSuccessfulUpdateInfo && lastSuccessfulUpdateInfo.lastChangedInKbart}">
                                <strong><g:formatDate format="${message(code: 'default.date.format.notime')}"
                                                    date="${lastSuccessfulUpdateInfo.lastChangedInKbart}"/></strong>
                            </g:if><g:else>
                                <strong>Empty</strong>
                            </g:else>
                            </label>
                        </div>
                    </div>

                <div class="ui fluid action input labeled">
                    <div class="ui label">
                        KBART-File:
                    </div>
                    <input type="text" name="upload_file_placeholder" readonly="readonly"
                           placeholder="Selected KBART-File">
                    <input type="file" name="tsvFile" accept=".tsv, .txt" style="display: none;" id="uploadFile">
                    <label for="uploadFile" class="ui primary button" style="padding-left:30px; padding-right:30px">
                        <i class="upload icon"></i>
                        Click here to upload KBART-File
                    </label>
                </div>
                <br>
                <br>

                <button class="ui primary button" type="submit">Process Kbart Import</button>
            </g:uploadForm>
        </div>
    </div>

</g:if>

<g:javascript>
    $('input:file', '.ui.action.input').on('change', function(e) {
        var name = e.target.files[0].name;
        $('input:text', $(e.target).parent()).val(name);
    });
</g:javascript>

</body>

</html>

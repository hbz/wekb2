<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils; wekb.CuratoryGroup; wekb.TitleInstancePackagePlatform;" %>
<g:set var="curatoryGroups"
       value="${(d instanceof TitleInstancePackagePlatform && d.pkg) ? d.pkg.getCuratoryGroupObjects() : (d.respondsTo('getCuratoryGroupObjects') ? d.getCuratoryGroupObjects() : [])}"/>
<wekb:serviceInjection/>

<g:set var="isUserLoggedIn" scope="page" value="${springSecurityService.isLoggedIn()}"/>

<div class="ui card">
    <g:if test="${curatoryGroups || d.hasProperty('curatoryGroups')}">
        <div class="content">
            <div class="header"><g:message code="rightBox.curatedBy"/></div>
        </div>

        <div class="content">
            <div class="ui bulleted list">
                <g:if test="${curatoryGroups}">
                    <g:each in="${curatoryGroups.sort { it.name }}" var="curatoryGroup">
                        <div class="item">
                            <g:link controller="resource" action="show"
                                    id="${curatoryGroup.getOID()}">${curatoryGroup.name}</g:link>

                            <g:if test="${params.curationOverride == 'true' && d.hasProperty('curatoryGroups') && isUserLoggedIn && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")}">
                                <g:link controller="ajaxHtml" action="removeCuratoryGroupFromObject"
                                        class="ui right floated negative mini button"
                                        params="['curationOverride': params.curationOverride, '__contextObjectClass': d.getClass().name, 'contextObject': d.id, '__curatoryGroup': curatoryGroup.class.name + ':' + curatoryGroup.id]">Unlink</g:link>
                            </g:if>
                        </div>
                    </g:each>
                </g:if>


                <g:if test="${!curatoryGroups}">
                    <div class="item"><g:message code="rightBox.notCuratedBy.info"/></div>
                </g:if>
            </div>

            <g:if test="${(params.curationOverride == 'true') && d.hasProperty('curatoryGroups') && !(d instanceof CuratoryGroup) && isUserLoggedIn && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")}">
                <div class="ui segment">
                    <g:form controller="ajaxHtml" action="addCuratoryGroupToObject" class="ui form">
                        <input type="hidden" name="__contextObjectClass" value="${d.getClass().name}"/>
                        <input type="hidden" name="contextObject" value="${d.id}"/>
                        <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                        <div class="field">
                            <label><g:message code="rightBox.curatedBy.info"/></label>
                            <semui:simpleReferenceDropdown name="__curatoryGroup"
                                                           baseClass="wekb.CuratoryGroup"
                                                           filter1="Current"/>
                        </div>

                        <button type="submit" class="ui black button">Link</button>
                    </g:form>
                </div>
            </g:if>
        </div>

    </g:if>
    <sec:ifNotLoggedIn>
        <div class="content center aligned">
            <g:link controller="resource" action="showLogin" class="ui icon black button"
                    id="${d.getOID()}"><i
                    class="edit icon"></i><g:message code="rightBox.edit.info"/></g:link>
        </div>
    </sec:ifNotLoggedIn>
    <sec:ifLoggedIn>
        <g:if test="${(d.hasProperty('curatoryGroups')) && !((request.curator != null ? request.curator.size() > 0 : true))}">
            <div class="content">
                <h4 class="ui header">Info</h4>
                <g:message code="rightBox.curatedBy.info2"/>
            </div>
        </g:if>
        <sec:ifAnyGranted roles="ROLE_ADMIN">
            <g:if test="${d.hasProperty('curatoryGroups') || d instanceof TitleInstancePackagePlatform}">
                <div class="content">
                    <h4 class="ui header">Warning</h4>

                    <p>As an admin you can still edit, but please contact a curator before making major changes.</p>

                    <g:if test="${params.curationOverride == 'true'}">
                        <g:link class="ui button green"
                                controller="${params.controller}"
                                action="${params.action}"
                                id="${displayobj.getOID()}"
                                params="${(request.param ?: [:])}">
                            Disable admin override
                        </g:link>
                    </g:if>
                    <g:else>
                        <g:link class="ui button red"
                                controller="${params.controller}"
                                action="${params.action}"
                                id="${displayobj.getOID()}"
                                params="${(request.param ?: [:]) + ["curationOverride": true]}">
                            Enable admin override
                        </g:link>
                    </g:else>
                </div>
            </g:if>
        </sec:ifAnyGranted>
    </sec:ifLoggedIn>

    <div class="content">

        <g:render template="/templates/componentStatus" model="${[d: d]}"/>

    </div>


    <g:if test="${d instanceof wekb.Package}">
        <div class="extra content center aligned">
            <div class="ui buttons">

                <g:if test="${(d.kbartSource && (d.kbartSource.lastUpdateUrl || d.kbartSource.url)) || d.getLastSuccessfulManualUpdateInfo()}">
                    <g:link controller="public" action="kbart" class="ui black button"
                            id="${params.id}">KBART File</g:link> &nbsp;
                    <div class="or"></div>
                </g:if>

                <a class="ui black button" href="#" onclick="$('#packageTSVExport').modal('show');">we:kb File</a>

            </div>

            <semui:modal id="packageTSVExport" title="Export we:kb File" msgSave="Export">

                <g:form controller="public" action="packageTSVExport" id="${params.id}" class="ui form">
                    <div class="grouped fields">
                        <label><g:message code="rightBox.export.info"/>:</label>

                        <div class="field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="status" value="Current">
                                <label><g:message code="rightBox.export.currentTitles"/></label>
                            </div>
                        </div>

                        <div class="field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="status" value="Expected">
                                <label><g:message code="rightBox.export.expectedTitles"/></label>
                            </div>
                        </div>

                        <div class="field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="status" value="Retired">
                                <label><g:message code="rightBox.export.retiredTitles"/></label>
                            </div>
                        </div>

                        <div class="field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="status" value="Deleted">
                                <label><g:message code="rightBox.export.deletedTitles"/></label>
                            </div>
                        </div>
                    </div>

                    <div class="inline fields">
                        <label><g:message code="rightBox.export.exportas"/>: </label>

                        <div class="field">
                            <div class="ui radio checkbox">
                                <input type="radio" name="exportFormat" checked="checked"  value="tsv">
                                <label><g:message code="rightBox.export.tsvfile"/></label>
                            </div>
                        </div>

                        <div class="field">
                            <div class="ui radio checkbox">
                                <input type="radio" name="exportFormat" value="xcel">
                                <label><g:message code="rightBox.export.excelfile"/></label>
                            </div>
                        </div>
                    </div>
                </g:form>
            </semui:modal>
        </div>
    </g:if>

</div>



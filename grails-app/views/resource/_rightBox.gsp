<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils; wekb.CuratoryGroup; wekb.TitleInstancePackagePlatform;" %>
<g:set var="curatoryGroups"
       value="${(d instanceof TitleInstancePackagePlatform && d.pkg) ? d.pkg.curatoryGroups : (d.hasProperty('curatoryGroups') ? d.curatoryGroups : [])}"/>
<wekb:serviceInjection/>


<div class="ui inverted blue right floated segment">
    <g:if test="${d instanceof wekb.KBComponent}">
    <h2 class="ui header">Curated By</h2>

    <div class="ui bulleted list">
        <g:each in="${curatoryGroups}" var="cg">
            <div class="item">${cg.name}
                <g:if test="${params.curationOverride == 'true' && springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")}">
                    <g:link controller="ajaxHtml" action="unlinkManyToMany" class="ui right floated negative mini button"
                            params="['curationOverride': params.curationOverride, '__property':'curatoryGroups', '__context':d.getClass().name + ':' + d.id, '__itemToRemove' : cg.getClass().name + ':' + cg.id]">Unlink Curatory Group</g:link>
                </g:if>
            </div>
        </g:each>


        <g:if test="${!curatoryGroups}">
            <div class="item">There are currently no linked Curatory Groups</div>
        </g:if>
    </div>

        <g:if test="${(params.curationOverride == 'true' || !curatoryGroups) && !(d instanceof CuratoryGroup) && !(d instanceof TitleInstancePackagePlatform) && springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")}">
            <div class="ui segment">
                <g:form controller="ajaxHtml" action="addToStdCollection" class="ui form">
                    <input type="hidden" name="__context" value="${d.getClass().name}:${d.id}"/>
                    <input type="hidden" name="__property" value="curatoryGroups"/>
                    <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                    <div class="field">
                        <label>Select a Curatory Group to link with this component</label>
                        <semui:simpleReferenceDropdown name="__relatedObject"
                                                       baseClass="wekb.CuratoryGroup"
                                                       filter1="Current"/>
                    </div>

                    <button type="submit" class="ui black button">Link</button>
                </g:form>
            </div>

        </g:if>

    </g:if>
    <sec:ifNotLoggedIn>
        <div style="margin-top:10px;">
            <g:link controller="resource" action="showLogin" class="ui icon inverted button"
                    id="${d instanceof wekb.KBComponent ? d.uuid : d.class.name + ':' + d.id}"><i class="edit icon"></i> Edit (Login required)</g:link>
        </div>
    </sec:ifNotLoggedIn>
    <sec:ifLoggedIn>
        <g:if test="${(d.respondsTo("getCuratoryGroups") || d instanceof wekb.KBComponent) && !((request.curator != null ? request.curator.size() > 0 : true))}">
            <div class="ui segment">
                <h4 class="ui header">Info</h4>

                <p>You are not a curator of this component. If you notice any errors, please contact a curator or request a review.</p>
            </div>
        </g:if>
        <sec:ifAnyGranted roles="ROLE_ADMIN">
            <div class="ui segment">
                <h4 class="ui header">Warning</h4>

                <p>As an admin you can still edit, but please contact a curator before making major changes.</p>

                <g:if test="${params.curationOverride == 'true'}">
                    <g:link class="ui button green"
                            controller="${params.controller}"
                            action="${params.action}"
                            id="${displayobj.class.name}:${displayobj.id}"
                            params="${(request.param ?: [:])}">
                        Disable admin override
                    </g:link>
                </g:if>
                <g:else>
                    <g:link class="ui button red"
                            controller="${params.controller}"
                            action="${params.action}"
                            id="${displayobj.class.name}:${displayobj.id}"
                            params="${(request.param ?: [:]) + ["curationOverride": true]}">
                        Enable admin override
                    </g:link>
                </g:else>
            </div>
        </sec:ifAnyGranted>
    </sec:ifLoggedIn>

    <g:render template="/templates/componentStatus" model="${[d: d]}"/>

    <g:if test="${d instanceof wekb.Package}">
        <br>
        &nbsp;
        <br>

        <div class="ui buttons">

            <g:if test="${(d.source && (d.source.lastUpdateUrl || d.source.url)) || d.getLastSuccessfulManualUpdateInfo()}">
                <g:link controller="public" action="kbart" class="ui inverted button"
                        id="${params.id}">KBART File</g:link> &nbsp;
                <div class="or"></div>
            </g:if>
            <a class="ui inverted button" href="#" onclick="$('#packageTSVExport').modal('show');"><g:message
                    code="gokb.appname" default="we:kb"/> File</a>

            <semui:modal id="packageTSVExport" title="Export we:kb File" msgSave="Export">

                <g:form controller="public" action="packageTSVExport"  id="${params.id}" class="ui form">
                    <div class="grouped fields">
                        <label>Which titles should be exported:</label>
                        <div class="field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="status" value="Current">
                                <label>Current Titles</label>
                            </div>
                        </div>
                        <div class="field">
                            <div class="ui checkbox">
                               <input type="checkbox" name="status" value="Retired">
                                <label>Retired Titles</label>
                            </div>
                        </div>
                        <div class="field">
                            <div class="ui checkbox">
                               <input type="checkbox" name="status" value="Expected">
                                <label>Expected Titlesx</label>
                            </div>
                        </div>
                        <div class="field">
                            <div class="ui checkbox">
                               <input type="checkbox" name="status" value="Deleted">
                                <label>Deleted Titles</label>
                            </div>
                        </div>
                    </div>
                </g:form>
            </semui:modal>
          %{--  <g:link controller="public" action="packageTSVExport" class="ui inverted button"
                    id="${params.id}"><g:message
                    code="gokb.appname" default="we:kb"/> File</g:link>--}%
        </div>
    </g:if>
</div>



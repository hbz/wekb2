<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils; wekb.helper.RCConstants; wekb.helper.RDStore;" %>
<wekb:serviceInjection/>
<g:set var="user" scope="page" value="${springSecurityService.currentUser}"/>

<g:if test="${d}">
    <semui:tabs>
        <semui:tabsItemWithoutLink tab="authentication" defaultTab="authentication" activeTab="${params.activeTab}">
            Authentication
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="statistic" activeTab="${params.activeTab}">
            Statistics
        </semui:tabsItemWithoutLink>
        <g:if test="${user && (SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN, ROLE_SUSHI") || user.curatoryGroupUsers.curatoryGroup.id.intersect(d.curatoryGroups.curatoryGroup.id))}">
            <semui:tabsItemWithoutLink tab="sushiApiInfo" activeTab="${params.activeTab}">
                Sushi Api Key Information
            </semui:tabsItemWithoutLink>
        </g:if>
        <semui:tabsItemWithoutLink tab="accessibility" activeTab="${params.activeTab}">
            Accessibility
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="additionalServices" activeTab="${params.activeTab}">
            Additional Services
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="titledetails" activeTab="${params.activeTab}" counts="${d.currentTippCount}">
            Hosted Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="packages" activeTab="${params.activeTab}" counts="${d.hostedPackages.size()}">
            Packages
        </semui:tabsItemWithoutLink>
    </semui:tabs>

    <semui:tabsItemContent tab="titledetails" activeTab="${params.activeTab}">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'titledetails' ? params.offset : '', sort: params.activeTab == 'titledetails' ? params.sort : '', order: params.activeTab == 'titledetails' ? params.order : '', qbe: 'g:tipps', qp_platform_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_plat', 'qp_platform_id'], qp_status: wekb.RefdataValue.class.name + ':' + RDStore.KBC_STATUS_CURRENT.id, activeTab: 'titledetails', jumpOffset: params.activeTab == 'titledetails' ? params.jumpOffset : '']"
                id="">Titles on this Platform</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="packages" activeTab="${params.activeTab}">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'packages' ? params.offset : '', sort: params.activeTab == 'packages' ? params.sort : '', order: params.activeTab == 'packages' ? params.order : '', qbe: 'g:packages', qp_platform_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_platform', 'qp_platform_id'], activeTab: 'packages', jumpOffset: params.activeTab == 'packages' ? params.jumpOffset : '']"
                id="">Packages on this Platform</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="authentication" defaultTab="authentication" activeTab="${params.activeTab}">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label"><g:message code="platform.ipAuthentication"/></dt>
                <dd><semui:xEditableRefData owner="${d}" field="ipAuthentication"
                                            config="${RCConstants.PLATFORM_IP_AUTH}"/></dd>
            </dl>
            <dl>
                <dt class="control-label"><g:message code="platform.openAthens"/></dt>
                <dd><semui:xEditableRefData owner="${d}" field="openAthens"
                                            config="${RCConstants.YN}"/></dd>
            </dl>
            <dl>
                <dt class="control-label"><g:message code="platform.shibbolethAuthentication"/></dt>
                <dd><semui:xEditableRefData owner="${d}" field="shibbolethAuthentication"
                                            config="${RCConstants.YN}"/>
                    <g:if test="${editable}">
                        <g:form controller="ajaxHtml" action="setShibbolethAuthentication" id="${d.id}" params="[curationOverride: params.curationOverride]">
                            <g:select from="${RefdataCategory.lookup(RCConstants.YN).sort { it.value }}"
                                      class="ui dropdown fluid"
                                      id="shibbolethAuthentication"
                                      optionKey="value"
                                      optionValue="${{ it.getI10n('value') }}"
                                      name="shibbolethAuthentication"
                                      value="${d.shibbolethAuthentication ? RDStore.YN_YES.value : RDStore.YN_NO.value}" onChange="this.form.submit()"/>
                        </g:form>
                    </g:if>
                    <g:else>
                        ${d.shibbolethAuthentication ? RDStore.YN_YES.getI10n('value') : RDStore.YN_NO.getI10n('value') }
                    </g:else>
                </dd>
            </dl>
            <g:if test="${d.shibbolethAuthentication && d.shibbolethAuthentication == wekb.helper.RDStore.YN_YES}">
                <dl>
                    <dt class="control-label">
                        Federations
                    </dt>
                    <dd>
                        <table class="ui small selectable striped celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Federation</th>
                                <g:if test="${editable}">
                                    <th>Action</th>
                                </g:if>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${d.federations?.sort { it.federation?.value }}" var="federation" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><semui:xEditableRefData owner="${federation}" field="federation"
                                                                config="${RCConstants.PLATFORM_FEDERATION}"/>
                                    <g:if test="${editable}">
                                        <td>

                                            <g:link controller='ajaxHtml'
                                                    action='delete'
                                                    params="${["__context": "${federation.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>

                                        </td>
                                    </g:if>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${editable}">
                            <a class="ui right floated primary button" href="#"
                               onclick="$('#pfModal').modal('show');">Add Federations</a>

                            <br>
                            <br>
                        </g:if>
                    </dd>
                </dl>
                <dl>
                    <dt class="control-label"><g:message code="platform.refedsSupport"/></dt>
                    <dd><semui:xEditableRefData owner="${d}" field="refedsSupport" config="${RCConstants.YN}"/></dd>
                </dl>
                <dl>
                    <dt class="control-label"><g:message code="platform.dpfParticipation"/></dt>
                    <dd><semui:xEditableRefData owner="${d}" field="dpfParticipation" config="${RCConstants.YN}"/></dd>
                </dl>
                <dl>
                    <dt class="control-label"><g:message code="platform.sccSupport"/></dt>
                    <dd><semui:xEditableRefData owner="${d}" field="sccSupport" config="${RCConstants.YN}"/></dd>
                </dl>
            </g:if>
            <dl>
                <dt class="control-label"><g:message code="platform.passwordAuthentication"/></dt>
                <dd><semui:xEditableRefData owner="${d}" field="passwordAuthentication" config="${RCConstants.YN}"/></dd>
            </dl>
            <dl>
                <dt class="control-label"><g:message code="platform.mailDomain"/></dt>
                <dd><semui:xEditableRefData owner="${d}" field="mailDomain" config="${RCConstants.YN}"/></dd>
            </dl>
            <dl>
                <dt class="control-label"><g:message code="platform.referrerAuthentification"/></dt>
                <dd><semui:xEditableRefData owner="${d}" field="referrerAuthentification" config="${RCConstants.YN}"/></dd>
            </dl>
            <dl>
                <dt class="control-label"><g:message code="platform.ezProxy"/></dt>
                <dd><semui:xEditableRefData owner="${d}" field="ezProxy" config="${RCConstants.YN}"/></dd>
            </dl>
            <dl>
                <dt class="control-label"><g:message code="platform.hanServer"/></dt>
                <dd><semui:xEditableRefData owner="${d}" field="hanServer" config="${RCConstants.YN}"/></dd>
            </dl>
            <dl>
                <dt class="control-label"><g:message code="platform.otherProxies"/></dt>
                <dd><semui:xEditableRefData owner="${d}" field="otherProxies" config="${RCConstants.YN}"/></dd>
            </dl>
        </div>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="statistic" activeTab="${params.activeTab}">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    Statistics Format
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="statisticsFormat"
                                            config="${RCConstants.PLATFORM_STATISTICS_FORMAT}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Statistics Update
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="statisticsUpdate"
                                            config="${RCConstants.PLATFORM_STATISTICS_UPDATE}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Statistics Admin Portal Url
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="statisticsAdminPortalUrl" validation="url" outGoingLink="true"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter Certified
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterCertified" config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Last Audit Date
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="lastAuditDate" type="date"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter Registry Url
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="counterRegistryUrl" validation="url" outGoingLink="true"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R4 Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR4Supported" config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R5 Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR5Supported" config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R4 Sushi Api Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR4SushiApiSupported"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R5 Sushi Api Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR5SushiApiSupported"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R4 Sushi Server Url
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="counterR4SushiServerUrl" validation="url"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R5 Sushi Server Url
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="counterR5SushiServerUrl" validation="url"/>
                </dd>

            </dl>
            <dl>
                <dt class="control-label">
                    Counter R5 Sushi Platform Name
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="counterR5SushiPlatform"/>
                </dd>

            </dl>
            <dl>
                <dt class="control-label"><g:message code="platform.counterRegistryApiUuid"/></dt>
                <dd><semui:xEditable owner="${d}" field="counterRegistryApiUuid"/></dd>
            </dl>
        </div>
    </semui:tabsItemContent>

    <g:if test="${user && (SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN, ROLE_SUSHI") || user.curatoryGroupUsers.curatoryGroup.id.intersect(d.curatoryGroups.curatoryGroup.id))}">

        <semui:tabsItemContent tab="sushiApiInfo" activeTab="${params.activeTab}">
            <div class="content wekb-inline-lists">
                <dl>
                    <dt class="control-label">
                        Sushi Api Authentication Method
                    </dt>
                    <dd>
                        <semui:xEditableRefData owner="${d}" field="sushiApiAuthenticationMethod"
                                                config="${RCConstants.PLATFORM_SUSHI_API_AUTH_METHOD}"/>
                    </dd>
                </dl>
                <dl>
                    <dt class="control-label">
                        Central Api Key
                    </dt>
                    <dd>
                        <semui:xEditable owner="${d}" field="centralApiKey"/>
                    </dd>
                </dl>
                <dl>
                    <dt class="control-label">
                        Label for Customer ID
                    </dt>
                    <dd>
                        <semui:xEditable owner="${d}" field="internLabelForCustomerID"/>
                    </dd>
                </dl>
                <dl>
                    <dt class="control-label">
                        Label for Requestor-ID / API-Key
                    </dt>
                    <dd>
                        <semui:xEditable owner="${d}" field="internLabelForRequestorKey"/>
                    </dd>
                </dl>
            </div>
        </semui:tabsItemContent>
    </g:if>

    <semui:tabsItemContent tab="accessibility" activeTab="${params.activeTab}">
        <div class="sixteen wide column">
            <div class="ui segment">
                <div class="content wekb-inline-lists">
                    <h2 class="ui header">Platform</h2>
                    <div class="description">
                        <dl>
                            <dt class="ui header wekb-header">Accessibility requirements according to
                                <g:link url="https://accessible-eu-centre.ec.europa.eu/content-corner/digital-library/en-3015492021-accessibility-requirements-ict-products-and-services_en"
                                        target="_blank">
                                    EN 301549
                                </g:link>
                            of the ...
                            </dt>
                            <dd></dd>
                        </dl>

                        <dl>
                            <dt class="control-label">
                                ... Platform
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="accessPlatform"
                                                        config="${RCConstants.UYNP}"/>
                            </dd>
                            <dd></dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                ... PDF viewer
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="viewerForPdf"
                                                        config="${RCConstants.UYNP}"/>
                            </dd>
                            <dd></dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                ... ePub viewer
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="viewerForEpub"
                                                        config="${RCConstants.UYNP}"/>
                            </dd>
                            <dd></dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                ... Audio player
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="playerForAudio"
                                                        config="${RCConstants.UYNP}"/>
                            </dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                ... Video player
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="playerForVideo"
                                                        config="${RCConstants.UYNP}"/>
                            </dd>
                        </dl>
                        <dl>
                            <dt class="ui header wekb-header">Accessibility Statement</dt>
                            <dd></dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                Accessibility Statement available
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="accessibilityStatementAvailable" config="${RCConstants.YN}"/>
                            </dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                Link to the Accessibility Statement
                            </dt>
                            <dd>
                                <semui:xEditable owner="${d}" field="accessibilityStatementUrl" validation="url" outGoingLink="true"/>
                            </dd>
                        </dl>
                    </div>
                </div>
            </div>

            <div class="ui segment">
                <div class="content wekb-inline-lists">
                    <h2 class="ui header">Content on the platform</h2>
                    <div class="description">
                        <dl>
                            <dt class="ui header wekb-header">Accessibility requirements according to
                                <g:link url="https://accessible-eu-centre.ec.europa.eu/content-corner/digital-library/en-3015492021-accessibility-requirements-ict-products-and-services_en"
                                        target="_blank">
                                    EN 301549
                                </g:link>
                                of the ...
                            </dt>
                            <dd></dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                ... EPUB e-books
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="accessEPub" config="${RCConstants.UYNP}"/>
                            </dd>
                        </dl>
                        <dl>
                            <dt class="control-label">
                                ... PDF e-books
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="accessPdf" config="${RCConstants.UYNP}"/>
                            </dd>
                        </dl>

                        <dl>
                            <dt class="control-label">
                                ... Audios
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="accessAudio" config="${RCConstants.UYNP}"/>
                            </dd>
                        </dl>

                        <dl>
                            <dt class="control-label">
                                ... Videos
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="accessVideo" config="${RCConstants.UYNP}"/>
                            </dd>
                        </dl>

                        <dl>
                            <dt class="control-label">
                                ... Databases
                            </dt>
                            <dd>
                                <semui:xEditableRefData owner="${d}" field="accessDatabase" config="${RCConstants.UYNP}"/>
                            </dd>
                        </dl>

                    </div>
                </div>
            </div>
        </div>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="additionalServices" activeTab="${params.activeTab}">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">Platform blog URL</dt>
                <dd><semui:xEditable owner="${d}" field="platformBlogUrl" validation="url" outGoingLink="true"/></dd>
            </dl>
            <dl>
                <dt class="control-label">RSS URL</dt>
                <dd><semui:xEditable owner="${d}" field="rssUrl" validation="url" outGoingLink="true"/></dd>
            </dl>
            <dl>
                <dt class="control-label">Individual design / logo</dt>
                <dd><semui:xEditableRefData owner="${d}" field="individualDesignLogo" config="${RCConstants.YN}"/></dd>
            </dl>
            <dl>
                <dt class="control-label">Full text search</dt>
                <dd><semui:xEditableRefData owner="${d}" field="fullTextSearch" config="${RCConstants.YN}"/></dd>
            </dl>
        </div>
    </semui:tabsItemContent>

    <g:if test="${editable}">
        <semui:modal id="pfModal" title="Add Federations">

            <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
                <input type="hidden" name="__context" value="${d.getOID()}"/>
                <input type="hidden" name="__newObjectClass" value="wekb.PlatformFederation"/>
                <input type="hidden" name="__recip" value="platform"/>
                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                <div class="field">
                    <label>Federations</label>
                    <semui:simpleReferenceDropdown name="federation"
                                                   baseClass="wekb.RefdataValue"
                                                   filter1="${RCConstants.PLATFORM_FEDERATION}"/>
                </div>
            </g:form>
        </semui:modal>
    </g:if>

</g:if>
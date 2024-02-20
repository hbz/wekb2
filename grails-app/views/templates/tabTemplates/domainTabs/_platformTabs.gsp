<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils; wekb.helper.RCConstants; wekb.helper.RDStore;" %>
<wekb:serviceInjection/>
<g:set var="user" scope="page" value="${springSecurityService.currentUser}"/>

<g:if test="${d}">
    <semui:tabs>

        <semui:tabsItemWithoutLink tab="statistic" defaultTab="statistic" activeTab="${params.activeTab}">
            Statistic
        </semui:tabsItemWithoutLink>
        <g:if test="${user && (SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN, ROLE_SUSHI") || user.curatoryGroupUsers.curatoryGroup.id.intersect(d.curatoryGroups.curatoryGroup.id))}">
            <semui:tabsItemWithoutLink tab="sushiApiInfo" activeTab="${params.activeTab}">
                Sushi Api Key Information
            </semui:tabsItemWithoutLink>
        </g:if>
        <semui:tabsItemWithoutLink tab="titledetails" activeTab="${params.activeTab}" counts="${d.currentTippCount}">
            Hosted Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="packages" activeTab="${params.activeTab}" counts="${d.hostedPackages.size()}">
            Packages
        </semui:tabsItemWithoutLink>
    </semui:tabs>

    <semui:tabsItemContent tab="titledetails" activeTab="${params.activeTab}">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:tipps', qp_plat_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_plat', 'qp_plat_id'], qp_status: wekb.RefdataValue.class.name + ':' + RDStore.KBC_STATUS_CURRENT.id, activeTab: 'titledetails']"
                id="">Titles on this Platform</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="packages" activeTab="${params.activeTab}">
        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:packages', qp_platform_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_platform', 'qp_platform_id'], activeTab: 'packages']"
                id="">Packages on this Platform</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="statistic" defaultTab="statistic" activeTab="${params.activeTab}">
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
                    Counter R3 Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR3Supported" config="${RCConstants.YN}"/>
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
            </div>
        </semui:tabsItemContent>
    </g:if>

</g:if>

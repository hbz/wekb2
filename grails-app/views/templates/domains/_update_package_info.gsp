<%@ page import="wekb.RefdataValue; wekb.helper.RDStore; wekb.helper.RCConstants" %>
<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">
                Package
            </dt>
            <dd>
                <g:if test="${d.pkg}">
                    <g:link controller="resource" action="show"
                            id="${d.pkg.getOID()}">
                        ${(d.pkg.name) ?: 'Empty'}
                    </g:link>
                </g:if>
                <g:else>Empty</g:else>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Description
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="description" overwriteEditable="false"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                <g:message code="default.status"/>
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.UPDATE_STATUS}"
                                        overwriteEditable="false"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Update was automatic
            </dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="automaticUpdate" overwriteEditable="false"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Start Time
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="startTime" overwriteEditable="false"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                End Time
            </dt>
            <dd>
                <semui:xEditable owner="${d}" type="date"
                                 field="endTime" overwriteEditable="false"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Titles in we:kb before update
            </dt>
            <dd>
                <g:formatNumber number="${d.countPreviouslyTippsInWekb}" type="number"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Titles in we:kb after update
            </dt>
            <dd>
                <g:formatNumber number="${d.countNowTippsInWekb}" type="number"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Rows in KBART-File by Update
            </dt>
            <dd>
                <g:formatNumber number="${d.countKbartRows}" type="number"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Processed KBART Rows by Update
            </dt>
            <dd>
                <g:formatNumber number="${d.countProcessedKbartRows}" type="number"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Changed Titles by Update / Changes in Titles
            </dt>
            <dd>
                <g:link controller="search" action="componentSearch" id=""
                        params="[qbe: 'g:updateTippInfos', qp_aup_id: d.id, qp_type: RefdataValue.class.name + ':' + RDStore.UPDATE_TYPE_CHANGED_TITLE.id]">
                    ${d.getCountInfosAboutChangedTitles()}
                </g:link>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Removed Titles by Update
            </dt>
            <dd>
                <g:link controller="search" action="componentSearch" id=""
                        params="[qbe: 'g:updateTippInfos', qp_aup_id: d.id, qp_type: RefdataValue.class.name + ':' + RDStore.UPDATE_TYPE_REMOVED_TITLE.id]">
                    <g:formatNumber number="${d.countRemovedTipps}" type="number"/>
                </g:link>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                New Titles by Update
            </dt>
            <dd>
                <g:link controller="search" action="componentSearch" id=""
                        params="[qbe: 'g:updateTippInfos', qp_aup_id: d.id, qp_type: RefdataValue.class.name + ':' + RDStore.UPDATE_TYPE_NEW_TITLE.id]">
                    <g:formatNumber number="${d.countNewTipps}" type="number"/>
                </g:link>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                In valid Titles by Update
            </dt>
            <dd>
                <g:link controller="search" action="componentSearch" id=""
                        params="[qbe: 'g:updateTippInfos', qp_aup_id: d.id, qp_type: RefdataValue.class.name + ':' + RDStore.UPDATE_TYPE_FAILED_TITLE.id]">
                    <g:formatNumber number="${d.countInValidTipps}" type="number"/>
                </g:link>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Only KBART rows which last changed after last update run
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="onlyRowsWithLastChanged" overwriteEditable="false"/>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                KBART has additional fields for we:kb
            </dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="kbartHasWekbFields" overwriteEditable="false"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">
                Last Changed in KBART
            </dt>
            <dd>
                <g:formatDate format="${message(code: 'default.date.format.notime')}"
                              date="${d.lastChangedInKbart}"/>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                Update Url
            </dt>
            <dd>
                <g:if test="${d.updateFromFTP}">
                    The update ran over FTP!
                </g:if>
                <g:elseif test="${d.updateFromFileUpload}">
                    The update ran over manuel upload!
                </g:elseif>
                <g:else>
                    <semui:xEditable owner="${d}" field="updateUrl" overwriteEditable="false" outGoingLink="true"/>
                </g:else>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                Frequency
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="frequency" overwriteEditable="false"/>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
               Last Run
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="lastRun" overwriteEditable="false"/>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                Last UpdateUrl
            </dt>
            <dd>
                <g:if test="${d.updateFromFTP}">
                    The update ran over FTP!
                </g:if>
                <g:elseif test="${d.updateFromFileUpload}">
                    The update ran over manuel upload!
                </g:elseif>
                <g:else>
                                <semui:xEditable owner="${d}" field="lastUpdateUrl" overwriteEditable="false" outGoingLink="true"/>
                </g:else>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                Update from URL
            </dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="updateFromURL" overwriteEditable="false"/>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                Update from FTP
            </dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="updateFromFTP" overwriteEditable="false"/>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                Update from File Upload
            </dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="updateFromFileUpload" overwriteEditable="false"/>
            </dd>
        </dl>


        <semui:tabs>
            <semui:tabsItemWithoutLink tab="updateTippInfos" counts="${d.getCountUpdateTippInfos()}" class="active">
                Title Update Infos
            </semui:tabsItemWithoutLink>
        </semui:tabs>


        <semui:tabsItemContent tab="updateTippInfos">

            <div class="content">

                <g:link class="display-inline" controller="search" action="inlineSearch"
                        params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:updateTippInfos', qp_aup_id: d.id, inline: true]"
                        id="">Title Update Infos on this Source</g:link>

            </div>

        </semui:tabsItemContent>

    </div>
</div>
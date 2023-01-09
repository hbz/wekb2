<%@ page import="wekb.helper.RCConstants;" %>
<semui:tabsItemContent tab="subjectArea" activeTab="${params.activeTab}">
    <g:if test="${d.id != null}">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    Subject Area
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="subjectArea"/>
                </dd>
            </dl>
        </div>
    </g:if>
</semui:tabsItemContent>

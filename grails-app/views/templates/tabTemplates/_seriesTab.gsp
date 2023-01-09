<%@ page import="de.wekb.helper.RCConstants;" %>
<semui:tabsItemContent tab="series" activeTab="${params.activeTab}">
    <g:if test="${d.id != null}">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    Series
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="series"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Parent publication title ID
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="parentPublicationTitleId"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Superseding publication title ID
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="supersedingPublicationTitleId"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Preceding publication title ID
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="precedingPublicationTitleId"/>
                </dd>
            </dl>
        </div>
    </g:if>
</semui:tabsItemContent>

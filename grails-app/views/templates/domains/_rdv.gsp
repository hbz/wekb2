<div class="ui segment">
    <div class="content wekb-inline-lists">
        <dl>
            <dt class="control-label">Value</dt>
            <dd>
                <semui:xEditable owner="${d}" field="value"/>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                Value EN
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="value_en"/>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                Value DE
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="value_de"/>
            </dd>
        </dl>

        <dl>
            <dt class="control-label">
                Description
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="description"/>
            </dd>
        </dl>


        <dl>
            <dt class="control-label">
                Hard Data
            </dt>
            <dd>
                <semui:xEditableBoolean owner="${d}" field="isHardData" overwriteEditable="false"/>
            </dd>
        </dl>
        <dl>
            <dt class="control-label">Refdata Category</dt>
            <dd>
                <g:if test="${controllerName == 'create'}">
                    <semui:xEditableManyToOne owner="${d}" field="owner" baseClass="wekb.RefdataCategory"/>
                </g:if>
                <g:else>
                    <g:link controller="resource" action="show"
                            id="wekb.RefdataCategory:${d.owner.id}">${d?.owner?.desc}</g:link>
                </g:else>
            </dd>
        </dl>
    </div>
</div>
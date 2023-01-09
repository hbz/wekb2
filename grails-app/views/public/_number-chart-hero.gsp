<div class="ui segment" style="background-color: #4c7f9029">
    <div class="ui five small statistics">
        <g:each in="${componentsOfStatistic.sort { it }}" var="component">
            <div class="statistic" >
                <div class="value">
                    <g:formatNumber number="${countComponent."${component.toLowerCase()}"}" type="number"
                                    format="###.###"/></div>
                <div class="label">
                    <g:message code="public.index.component.${component.toLowerCase()}"/>
                </div>
            </div>
        </g:each>
    </div>
</div>


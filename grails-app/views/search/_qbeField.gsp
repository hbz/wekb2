<div class="field">
    <g:if test="${field.type == 'lookup'}">
        <div class="ui field">
            <label>
                <g:if test="${field.msgCode}">
                    <g:message code="${field.msgCode}" default="${field.prompt}"/>
                </g:if>
                <g:else>
                    ${field.prompt}
                </g:else>
            </label>
            <semui:simpleReferenceDropdown
                    multiple="${field.multiple}"
                    id="refdata_${params.inline ? 'inline_' : ''}${field.qparam}"
                    name="${field.qparam}"
                    baseClass="${field.baseClass}"
                    filter1="${field.filter1 ?: ''}"
                    value="${params[field.qparam]}"/>
        </div>
    </g:if>
    <g:elseif test="${field.type == 'dropDown'}">
        <div class="ui field">
            <label>
                <g:if test="${field.msgCode}">
                    <g:message code="${field.msgCode}" default="${field.prompt}"/>
                </g:if>
                <g:else>
                    ${field.prompt}
                </g:else>
            </label>
            <semui:componentsDropDown multiple="${field.multiple}"
                                      value="${params[field.qparam]}"
                                      name="${field.qparam}"
                                      baseClass="${field.baseClass}"
                                      filter1="${field.filter1}"
                                      placeholder="${message(code: 'search.select')}${field.selectPlaceHolder ?: field.prompt}"/>
        </div>
    </g:elseif>
    <g:elseif test="${field.type == 'dropDownGroup'}">
        <div class="ui field">
            <label>
                <g:if test="${field.msgCode}">
                    <g:message code="${field.msgCode}" default="${field.prompt}"/>
                </g:if>
                <g:else>
                    ${field.prompt}
                </g:else>
            </label>
            <g:if test="${refObject}">
                <semui:selectedDropDown multiple="${field.multiple}"
                                        value="${params[field.qparam]}"
                                        name="${field.qparam}"
                                        dropDownType="${field.dropDownType}"
                                        refObject="${refObject.class.name}:${refObject.id}"
                                        qpStatusId="${params.qp_status_id}"
                                        placeholder="${message(code: 'search.select')}${field.selectPlaceHolder ?: field.prompt}"/>
            </g:if>
            <g:else>
                <semui:selectedDropDown multiple="${field.multiple}"
                                        value="${params[field.qparam]}"
                                        name="${field.qparam}"
                                        dropDownType="${field.dropDownType}"
                                        qpStatusId="${params.qp_status_id}"
                                        placeholder="${message(code: 'search.select')}${field.selectPlaceHolder ?: field.prompt}"/>
            </g:else>
        </div>
    </g:elseif>
    <g:elseif test="${field.type == 'date'}">
        <semui:datepicker label="${field.msgCode ?: field.prompt}" id="${field.qparam}" name="${field.qparam}" placeholder="${field.placeholder}"
                          value="${params[field.qparam]}"/>
    </g:elseif>
    <g:else>
        <label for="${field.qparam}">
            <g:if test="${field.msgCode}">
                <g:message code="${field.msgCode}" default="${field.prompt}"/>
            </g:if>
            <g:else>
                ${field.prompt}
            </g:else>
        </label>

        <div class="${field.contextTree.wildcard != null ? 'ui labeled input' : ''}">
            <g:if test="${field.contextTree.wildcard == 'B' || field.contextTree.wildcard == 'L'}"><div
                    class="ui label">*</div></g:if>
            <input type="${field.contextTree.type == 'java.lang.Long' ? 'number' : 'text'}"
                   name="${field.qparam}" id="${field.qparam}" placeholder="${field.placeholder}"
                   value="${params[field.qparam]}"/>
            <g:if test="${field.contextTree.wildcard == 'B' || field.contextTree.wildcard == 'R'}"><div
                    class="ui label">*</div></g:if>
        </div>
    </g:else>
</div>
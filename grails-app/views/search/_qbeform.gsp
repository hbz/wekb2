%{--<g:set var="s_action" value="${s_action ?: 'componentSearch'}"/>
<g:set var="s_controller" value="${s_controller ?: 'search'}"/>--}%

<g:set var="dropdownService" bean="dropdownService"/>

<g:if test="${hide.contains('SEARCH_FORM')}">
</g:if>
<g:elseif test="${params.inline}">
    <g:form method="get" class="ui form wekb-filter" controller="search" action="componentSearch" id="${params.id}">
        <input type="hidden" name="qbe" value="${params.qbe}"/>
        <input type="hidden" name="hideResetButton" value="${true}"/>

        <g:each in="${hide}" var="hidden_var">
            <input type="hidden" name="hide" value="${hidden_var}"/>
        </g:each>

        <g:if test="${refOID}">
            <input type="hidden" name="refOID" value="${refOID}"/>
        </g:if>

        <g:each in="${formdefn}" var="fld">
            <g:if test="${((hide?.contains(fld.qparam)) || (fld.hide == true))}">
                <input type="hidden" name="${fld.qparam}" id="${fld.qparam}" value="${params[fld.qparam]}"/>
            </g:if>
        </g:each>

        <div class="ui right floated buttons">
            <button class="ui button primary" type="submit" value="search"
                    name="searchAction">Search View</button>
        </div>
        <br><br>
    </g:form>
</g:elseif>
<g:else>

    <% Map advancedSearchMap = [:] %>
    <div class="ui segment">
    <g:form method="get" class="ui form wekb-topFilter ${minInput ? 'wekb-filter-mininput' : 'wekb-filter'}" controller="${controllerName}" action="${actionName}" id="${params.id}">

        <input type="hidden" name="qbe" value="${params.qbe}"/>

        <g:if test="${refOID}">
            <input type="hidden" name="refOID" value="${refOID}"/>
        </g:if>

        <g:each in="${hide}" var="hidden_var">
            <input type="hidden" name="hide" value="${hidden_var}"/>
        </g:each>


        <div class="two fields">
        <g:each in="${formdefn}" var="fld" status="frmidx">
            <g:if test="${((hide?.contains(fld.qparam)) || (fld.hide == true))}">
                <input type="hidden" name="${fld.qparam}" id="${fld.qparam}" value="${params[fld.qparam]}"/>
            </g:if>
            <g:elseif test="${fld.advancedSearch}">
                <%
                    if (!advancedSearchMap."${fld.advancedSearch.category}") {
                        advancedSearchMap."${fld.advancedSearch.category}" = [title: fld.advancedSearch.title, formFields: []]
                    }
                    advancedSearchMap."${fld.advancedSearch.category}".formFields << fld
                %>
            </g:elseif>
            <g:else>
                <g:render template="/search/qbeField" model="[field: fld]"/>

                <g:if test="${((frmidx) % 2) == 0}">
                    </div>
                    <div class="two fields">
                </g:if>

            </g:else>
        </g:each>
        </div>

        <g:each in="${advancedSearchMap}" var="advancedSearch" status="fieldIndex">
            <g:set var="filterSetInAccordion" value="${0}"/>

            <g:each in="${params}" var="parameter">
                <g:if test="${parameter.key in advancedSearch.value.formFields.qparam && (parameter.value != null && parameter.value != "")}">
                    <g:set var="filterSetInAccordion" value="${filterSetInAccordion+1}"/>
                </g:if>
            </g:each>

            <div class="ui accordion field">
                <div class="title">
                    <i class="icon dropdown"></i>
                ${advancedSearch.value.title}
                <g:if test="${filterSetInAccordion > 0}">
                    <strong><g:message code="search.filter.set"/>:</strong> <div class="ui primary circular label">${filterSetInAccordion}</div>
                </g:if>
            </div>

            <div class="content">
            <div class="two fields">
                <g:each in="${advancedSearch.value.formFields}" var="field" status="formFieldsIndex">
                    <g:render template="/search/qbeField" model="[field: field]"/>

                    <g:if test="${(formFieldsIndex + 1) % 2 == 0}">
                        </div>
                        <div class="two fields">
                    </g:if>
                </g:each>
            </div>
            </div>
            </div>
        </g:each>



        <g:if test="${hide.contains('SEARCH_BUTTONS')}">
        </g:if>
        <g:else>
            <div class="ui right floated buttons">
                <g:if test="${!hideResetButton}">
                    <%
                        Map<String, Object> resetParams = [id: params.id, qbe: params.qbe]
                        if(qbetemplate.containsKey('defaultStatus'))
                            resetParams.qp_status = qbetemplate.defaultStatus
                    %>
                    <g:link class="ui button" controller="${controllerName}" action="${actionName}"
                            params="${resetParams}"><g:message code="search.reset"/></g:link>
                </g:if>
                <button class="ui button primary" type="submit" value="Search"
                        name="searchAction"><g:message code="search.filter"/></button>

                <sec:ifLoggedIn>
                    <div class="ui icon dropdown button">
                        <div class="text"><g:message code="default.button.save"/></div>
                        <i class="dropdown icon"></i>

                        <div class="menu">
                            <div class="header" id="test">
                                <g:message code="search.saveSearch"/>:
                            </div>

                            <div class="ui left input">
                                <input aria-labelledby="test" type="text" name="searchName"
                                       placeholder="<g:message code="search.saveSearch.name"/>">
                            </div>

                            <div class="item">
                                <input class="ui button primary" type="submit" name="searchAction"
                                       value="Save"/>
                            </div>
                        </div>
                    </div>
                </sec:ifLoggedIn>

            </div>
            <br>
            <br>
        </g:else>
    </g:form>
    </div>
</g:else>



<g:javascript>
    <g:if test="${minInput}">
        $.fn.form.settings.rules.minInput = function() {
            let fields = new Array();
            <g:each in="${formdefn}" var="fld">
                <g:if test="${!fld.hide}">
                    fields.push('[name="${fld.qparam}"');
                </g:if>
            </g:each>
            let inputFilledCount = 0;
            let minInput = ${minInput};
            $.each(fields, function(i, field) {
                if($(field).val().length > 0) {
                    inputFilledCount++;
                }
            });
            return inputFilledCount >= minInput;
        }

        $('.wekb-filter-mininput').form({
            on: 'submit',
            inline: true,
            fields: {
            <g:each in="${formdefn}" var="fld">
                <g:if test="${!fld.hide}">
                    ${fld.qparam}: {
                        identifier: '${fld.qparam}',
                        rules: [
                            {
                                type: 'minInput',
                                prompt: 'At least two fields need to be filled!'
                            }
                        ]
                    },
                </g:if>
            </g:each>
            },
            onSuccess: function() {
                let fields = $(this).find('input[type=text], input[type=hidden], select');
                let emptyFields = fields.filter(function(){ return !this.value; });
                // remove unused filter params
                emptyFields.attr('disabled', 'disabled');
            }
        });
    </g:if>
</g:javascript>

<%@ page import="grails.converters.JSON" %>

<wekb:serviceInjection/>

<g:set var="counter" value="${offset}"/>
<g:set var="s_action" value="${s_action?:actionName}"/>
<g:set var="s_controller" value="${s_controller?:controllerName}"/>

<g:if test="${request.isAjax()}">

    <div class="ui header">
        <h1>Showing results ${offset.toInteger() + 1} to ${lasthit.toInteger() as int} of
            ${reccount.toInteger() as int}</h1>
    </div>

    <g:render template="/search/pagination" model="${params}"/>

    <div style="overflow-x: auto">
    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <g:each in="${qbeConfig.qbeResults}" var="c">
                <g:if test="${!params.hide || !(c.qpEquiv && params.hide && (params.hide.contains(c.qpEquiv)))}">
                    <g:set var="colcode" value="${baseClass + '.' + c.heading}"/>
                    <g:set var="colmsg" value="${message(code: colcode, default: c.heading)}"/>
                    <g:if test="${c.sort}">
                        <semui:sortableColumn controller="${s_controller}" action="${s_action}" id="${params.id}" property="${c.sort}" title="${colmsg == colcode ? c.heading : colmsg}"
                                              params="${params}"/>
                    </g:if>
                    <g:else>
                        <th>${colmsg == colcode ? c.heading : colmsg}</th>
                    </g:else>
                </g:if>
            </g:each>
        </tr>
        </thead>
        <tbody>
        <g:each in="${rows}" var="r">
            <g:set var="r" value="${r}"/>
            <tr class="${++counter == det ? 'positive' : ''}">
                <!-- Row ${counter} -->
                <td>${counter}</td>
                <g:each in="${r.cols}" var="c">
                    <td>
                        <g:if test="${c.value instanceof java.util.List}">
                            <div class="ui bulleted list">
                                <g:each in="${c.value}" var="element">
                                    <div class="item">
                                        <g:if test="${c.link}">
                                            <g:link controller="resource"
                                                    action="show"
                                                    id="${element.getOID()}">
                                                ${element.name}
                                            </g:link>
                                        </g:if><g:else>
                                            ${element.name}
                                        </g:else>
                                    </div>
                                </g:each>
                            </div>
                        </g:if>
                        <g:elseif test="${c.link != null && c.value && c.value != '-Empty-'}">
                            <g:link controller="resource"
                                    action="show"
                                    id="${c.link}">
                                <g:render template="/search/qbevalue" model="[c: c]"/>
                            </g:link>
                        </g:elseif>
                        <g:elseif test="${c.outGoingLink != null}">
                            <g:render template="/search/qbevalue" model="[c: c]"/>
                            <g:if test="${c.value && c.value != '-Empty-'}">
                                &nbsp;<a aria-label="${c.value}"
                                         href="${c.value.startsWith('http') ? c.value : 'http://' + c.value}"
                                         target="_blank"><i class="share square icon"></i></a>
                            </g:if>
                        </g:elseif>
                        <g:elseif test="${c.jumpToLink != null}">
                            <g:link uri="${c.jumpToLink}">
                                <g:render template="/search/qbevalue" model="[c: c]"/>
                            </g:link>
                        </g:elseif>
                        <g:else>
                            <g:render template="/search/qbevalue" model="[c: c]"/>
                        </g:else>
                    </td>
                </g:each>
            </tr>
        </g:each>
        </tbody>
    </table>
    </div>
    <g:render template="/search/pagination" model="${params}"/>
</g:if>
<g:else>

    <g:set var="nowDate" value="${new java.util.Date()}"/>
    <div class="ui header">
        <h1>Showing results ${offset.toInteger() + 1} to ${lasthit.toInteger() as int} of
            ${reccount.toInteger() as int}</h1>
    </div>

    <div class="batch-all-info" style="display:none;"></div>

    <g:render template="/search/pagination" model="${params}"/>
    <g:form controller="workflow" action="action" method="post" params="${params}" class='action-form'>
       <div style="overflow-x: auto">
        <table class="ui selectable striped sortable celled table">
            <thead>
%{--            <sec:ifLoggedIn>
              <tr>
                <th></th>
                <th colspan="${qbeConfig.qbeResults.size() + 1}"></th>
                --}%%{--<!-- see grails-app/assets/javascripts/gokb/action-forms.js for code relating to bulk actions -->
                <g:if test="${!hideActions}">
                  <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">Actions <b class="caret"></b></a>
                    <ul class="dropdown-menu actions"></ul>
                  </li>
                  <li class="divider-vertical"></li>
                </g:if>--}%%{--
              </tr>
            </sec:ifLoggedIn>--}%
            <tr>
                <sec:ifLoggedIn>
                    <g:if test="${controllerName == 'group'}">
                    <th></th>
                    </g:if>
                </sec:ifLoggedIn>
                <th>#</th>
                <g:each in="${qbeConfig.qbeResults}" var="c">
                    <g:set var="colcode" value="${baseClass + '.' + c.heading}"/>
                    <g:set var="colmsg" value="${message(code: colcode, default: c.heading)}"/>
                    <g:if test="${!params.hide || !(c.qpEquiv && params.hide && (params.hide.contains(c.qpEquiv)))}">
                        <g:if test="${c.sort}">
                            <semui:sortableColumn property="${c.sort}" title="${colmsg == colcode ? c.heading : colmsg}"
                                                  params="${params}"/>
                        </g:if>
                        <g:else>
                            <th>${colmsg == colcode ? c.heading : colmsg}</th>
                        </g:else>
                    </g:if>
                </g:each>
            </tr>
            </thead>
            <tbody>
            <g:each in="${rows}" var="r">
                <g:if test="${r != null}">
                    <g:set var="row_obj" value="${r.obj}"/>
                    <tr class="${++counter == det ? 'positive' : ''}">
                    <!-- Row ${counter} -->
                        <sec:ifLoggedIn>
                            <g:if test="${controllerName == 'group'}">
                            <td>
                                <g:set var="objEditable" value="${accessService.checkEditableObject(row_obj, params)}"/>
                                <g:if test="${objEditable && row_obj.respondsTo('availableActions')}">
                                    <g:set var="al"
                                           value="${new JSON(row_obj?.userAvailableActions()).toString().encodeAsHTML()}"/>
                                    <input type="checkbox" name="bulk:${r.oid}" data-actns="${al}"
                                           class="obj-action-ck-box"/>
                                </g:if>
                                <g:else>
                                    <input type="checkbox"
                                           title="${!objEditable ? 'Component is read only' : 'No actions available'}"
                                           disabled="disabled" readonly="readonly"/>
                                </g:else>
                            </td>
                            </g:if>
                        </sec:ifLoggedIn>
                        <td>${counter}</td>
                        <g:each in="${r.cols}" var="c">
                            <td>
                                <g:if test="${c.value instanceof java.util.List}">
                                    <div class="ui bulleted list">
                                        <g:each in="${c.value}" var="element">
                                            <div class="item">
                                                <g:if test="${c.link}">
                                                    <g:link controller="resource"
                                                            action="show"
                                                            id="${element.getOID()}">
                                                        ${element.name}
                                                    </g:link>
                                                </g:if><g:else>
                                                    ${element.name}
                                                </g:else>
                                            </div>
                                        </g:each>
                                    </div>
                                </g:if>
                                <g:elseif test="${c.link != null && c.value && c.value != '-Empty-'}">
                                    <g:link controller="resource"
                                            action="show"
                                            id="${c.link}">
                                        <g:render template="/search/qbevalue" model="[c: c]"/>
                                    </g:link>

                                    <g:set var="duration" value="${null}"/>
                                    <g:if test="${c.globalSearchTemplateProperty == 'name' &&
                                            (row_obj instanceof wekb.Package || row_obj instanceof wekb.Org
                                            || row_obj instanceof wekb.Platform || row_obj instanceof wekb.TitleInstancePackagePlatform
                                                    || row_obj instanceof wekb.Vendor)}">

                                        <% use(groovy.time.TimeCategory) {
                                            duration = nowDate - row_obj.dateCreated
                                            }
                                        %>
                                        <g:if test="${duration && duration.days <= 14}">
                                            <div class="ui black label" data-tooltip="Newly added in the last 14 days!">
                                                <i class="star icon"></i>
                                                New
                                            </div>
                                        </g:if>

                                    </g:if>
                                </g:elseif>
                                <g:elseif test="${c.outGoingLink != null}">
                                    <g:render template="/search/qbevalue" model="[c: c]"/>
                                    <g:if test="${c.value && c.value != '-Empty-'}">
                                        &nbsp;<a aria-label="${c.value}"
                                                 href="${c.value.startsWith('http') ? c.value : 'http://' + c.value}"
                                                 target="_blank"><i class="share square icon"></i></a>
                                    </g:if>
                                </g:elseif>
                                <g:elseif test="${c.jumpToLink != null}">
                                    <g:link uri="${c.jumpToLink}">
                                        <g:render template="/search/qbevalue" model="[c: c]"/>
                                    </g:link>
                                </g:elseif>
                                <g:else>
                                    <g:render template="/search/qbevalue" model="[c: c]"/>
                                </g:else>
                            </td>
                        </g:each>
                    </tr>
                </g:if>
                <g:else>
                    <tr>
                        <td>Error - Row not found</td>
                    </tr>
                </g:else>
            </g:each>
            </tbody>
        </table>
       </div>
    </g:form>
    <g:render template="/search/pagination" model="${params}"/>
</g:else>

<br>


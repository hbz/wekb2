<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : Curatory Groups Check Infos</title>
</head>

<body>
<h1 class="ui header">Curatory Groups Check Infos</h1>


<g:set var="checkMyInfosService" bean="checkMyInfosService"/>

<g:set var="counter" value="${offset}"/>
<g:set var="s_action" value="${s_action?:actionName}"/>
<g:set var="s_controller" value="${s_controller?:controllerName}"/>

    <g:if test="${(qbetemplate.message != null)}">
        <semui:message message="${qbetemplate.message}"/>
    </g:if>

    <g:render template="/search/qbeform"
              model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>

    <g:if test="${recset && !init}">

        <g:set var="nowDate" value="${new java.util.Date()}"/>
        <div class="ui header">
            <h1><g:message code="search.show.results" args="[offset.toInteger() + 1, lasthit.toInteger() as int, reccount.toInteger() as int]"/></h1>
        </div>

        <div class="batch-all-info" style="display:none;"></div>

        <g:render template="/search/pagination" model="${params}"/>

            <div style="overflow-x: auto">
                <table class="ui selectable striped sortable celled table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <g:each in="${qbetemplate.qbeConfig.qbeResults}" var="c">
                            <g:set var="colcode" value="${classSimpleName.toLowerCase() + '.' + c.property}"/>
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
                        <th>No Contacts</th>
                        <th>No product idenifier</th>
                        <th>No content type</th>
                        <th>Sources without Titles</th>
                        <th>Packages without Source</th>
                        <th>No changes in packages last 30 Days</th>
                        <th>Packages without Titles</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${new_recset}" var="r">
                        <g:if test="${r != null}">
                            <g:set var="row_obj" value="${r.obj}"/>
                            <tr class="${++counter == det ? 'positive' : ''}">
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
                                                    <div class="ui primary  label" data-tooltip="<g:message code="search.result.new.info"/>">
                                                        <i class="star icon"></i>
                                                        <g:message code="search.result.new"/>
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
                                <td><g:link controller="group" action="checkMyInfos" params="[curGroupID: row_obj.id]"> ${checkMyInfosService.checkContacts([row_obj]).size()} </g:link></td>
                                <td><g:link controller="group" action="checkMyInfos" params="[curGroupID: row_obj.id]"> ${checkMyInfosService.checkPackagesWithoutProductID([row_obj]).size()} </g:link></td>
                                <td><g:link controller="group" action="checkMyInfos" params="[curGroupID: row_obj.id]"> ${checkMyInfosService.checkPackagesWithoutContentType([row_obj]).size()} </g:link></td>
                                <td><g:link controller="group" action="checkMyInfos" params="[curGroupID: row_obj.id]"> ${checkMyInfosService.checkSourcesWithoutTitles([row_obj]).size()} </g:link></td>
                                <td><g:link controller="group" action="checkMyInfos" params="[curGroupID: row_obj.id]"> ${checkMyInfosService.checkPackageWithoutSource([row_obj]).size()} </g:link></td>
                                <td><g:link controller="group" action="checkMyInfos" params="[curGroupID: row_obj.id]"> ${checkMyInfosService.noChangesPackageLast30DaysAutoUpdate([row_obj]).size()} </g:link></td>
                                <td><g:link controller="group" action="checkMyInfos" params="[curGroupID: row_obj.id]"> ${checkMyInfosService.checkPackagesWithoutTitles([row_obj]).size()} </g:link></td>
                            </tr>
                        </g:if>
                        <g:else>
                            <tr>
                                <td><g:message code="search.result.error"/></td>
                            </tr>
                        </g:else>
                    </g:each>
                    </tbody>
                </table>
            </div>
        <g:render template="/search/pagination" model="${params}"/>


    </g:if>
    <g:elseif test="${!init && !params.inline}">
        <g:render template="/search/qbeempty"/>
    </g:elseif>
    <g:else>
        <semui:message>
            <p>No results.</p>
        </semui:message>
    </g:else>



</body>
</html>

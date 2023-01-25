<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory;" %>
<g:set var="counter" value="${offset}"/>

<div style="overflow-x: auto">
    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <semui:sortableColumn property="name" title="Name"
                                  params="${params}"/>
            <th>National Ranges</th>
            <th>Regional Ranges</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${new_recset}" var="r">
            <g:if test="${r != null}">
                <g:set var="row_obj" value="${r.obj}"/>
                <tr>
                    <td>${++counter}</td>
                    <td>
                        <semui:xEditable owner="${row_obj}" field="name" required="true" overwriteEditable="${false}"/>
                    </td>
                    <td>
                        <g:if test="${row_obj.scope?.value == 'National'}">
                            <div class="ui bulleted list">
                                <g:each in="${row_obj.nationalRanges.sort { it.getI10n('value') }}" var="nationalRange">
                                    <div class="item">${nationalRange.value}: ${nationalRange.getI10n('value')}
                                    <g:if test="${editable}">
                                        <g:link controller='ajaxHtml'
                                                action='unlinkManyToMany'
                                                params="${["__context": "${row_obj.class.name}:${row_obj.id}", "__property": "nationalRanges", "__itemToRemove": "${nationalRange.getClass().name}:${nationalRange.id}"]}">Delete</g:link>
                                    </g:if>
                                    </div>
                                </g:each>
                            </div>
                        </g:if>
                        <g:else>
                            The package scope is not set to national. This must be set to National to select national range.
                        </g:else>
                    </td>
                    <td>
                        <g:if test="${RefdataCategory.lookup(RCConstants.COUNTRY, 'DE') in row_obj.nationalRanges && row_obj.scope?.value == 'National'}">
                            <div class="ui bulleted list">
                                <g:each in="${row_obj.regionalRanges.sort { it.getI10n('value') }}" var="regionalRange">
                                    <div class="item">${regionalRange.getI10n('value')}
                                    <g:if test="${editable}">
                                        <g:link controller='ajaxHtml'
                                                action='unlinkManyToMany'
                                                params="${["__context": "${row_obj.class.name}:${row_obj.id}", "__property": "regionalRanges", "__itemToRemove": "${regionalRange.getClass().name}:${regionalRange.id}"]}">Delete</g:link>
                                    </g:if>
                                    </div>
                                </g:each>
                            </div>
                        </g:if>
                        <g:else>
                            The package scope is not set to national. This must be set to National to select regional range.
                        </g:else>
                    </td>
                    <td>
                        <g:link class="ui icon button" controller="resource" action="show" id="${row_obj.uuid}">
                            <i class="edit icon"></i>
                        </g:link>
                    </td>
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
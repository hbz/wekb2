<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory" %>
<semui:tabsItemContent tab="ddcs" activeTab="${params.activeTab}">
    <g:if test="${d.id != null}">
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th>Dewey Decimal Classification</th>
                <g:if test="${editable}">
                    <th>Actions</th>
                </g:if>
            </tr>
            </thead>
            <tbody>
            <g:each in="${d.ddcs.sort { it.value }}" var="ddc">
                <tr>
                    <td>
                        ${ddc.value}: ${ddc.getI10n('value')}
                    </td>
                    <g:if test="${editable}">
                        <td><g:link controller='ajaxHtml'
                                action='unlinkManyToMany'
                                params="${["__context": "${d.getOID()}", "__property": "ddcs", "__itemToRemove": "${ddc.getOID()}", activeTab: 'ddcs']}">Delete</g:link>
                        </td>
                    </g:if>
                </tr>
            </g:each>
            </tbody>
        </table>


        <g:if test="${editable}">
                        <a class="ui right floated black button" href="#"
                           onclick="$('#ddcModal').modal('show');">Add Dewey Decimal Classification</a>

                        <br>
                        <br>

                        <semui:modal id="ddcModal" title="Add Dewey Decimal Classification">

                            <g:form class="ui form" controller="ajaxHtml" action="addToStdCollection" params="[activeTab: 'ddcs']">
                                <input type="hidden" name="__context" value="${d.getOID()}"/>
                                <input type="hidden" name="__property" value="ddcs"/>
                                <input type="hidden" name="object" value="${d.getOID()}"/>
                                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                                <div class="field">
                                    <label>Dewey Decimal Classification:</label>

                                    <g:select from="${RefdataCategory.lookup(RCConstants.DDC).sort { it.value }}"
                                              class="dropdown fluid"
                                              id="ddcSelection"
                                              optionKey="${{ it.class.name + ':' + it.id }}"
                                              optionValue="${{ it.value + ': ' + it.getI10n('value') }}"
                                              name="__relatedObject"
                                              value=""/>
                                </div>
                            </g:form>
                        </semui:modal>

                    </g:if>
    </g:if>
    <g:else>
        DDCs can be added after the creation process is finished.
    </g:else>
</semui:tabsItemContent>

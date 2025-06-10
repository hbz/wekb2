<%@ page import="wekb.ClassUtils; wekb.helper.RDStore; wekb.TitleInstancePackagePlatform;" %>
<semui:tabsItemContent tab="identifiers" class="${activeTab ? 'active' : ''}" defaultTab="${defaultTab}" activeTab="${params.activeTab}" counts="${d.ids.size()}">

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>Identifier Namespace Name</th>
            <th>Identifier Namespace Value</th>
            <th>Identifier</th>
            <g:if test="${editable}">
                <th>Actions</th>
            </g:if>
        </tr>
        </thead>
        <tbody>
            <g:each in="${d.ids?.sort { it.namespace?.value }}" var="identifier">
                <tr>
                    <td>
                        ${identifier.namespace.name}
                    </td>
                    <td>
                        ${identifier.namespace.value}
                    </td>
                    <td>
                        <semui:xEditable owner="${identifier}" field="value" />

                        <span class="js-copyTriggerParent">
                            <span class="ui small basic image label js-copyTrigger la-popup-tooltip" data-position="top center" data-tooltip="${message(code: 'tooltip.clickToCopy', args: [identifier.namespace.name])}">
                                <i class="copy black icon la-js-copyTriggerIcon"></i>
                                <span class="detail js-copyTopic">Copy</span>
                            </span>
                        </span>

                        <g:if test="${identifier.namespace.value == 'doi'}">
                            <g:set value="${identifier.value ? (identifier.value.startsWith('http') ? identifier.value : 'https://www.doi.org/'+identifier.value) : ""}"
                                   var="url"/>
                        </g:if>
                        <g:elseif test="${identifier.namespace.value in ['issn', 'eissn', 'issnl']}">
                            <g:set value="${identifier.value ? (identifier.value.startsWith('http') ? identifier.value : 'https://portal.issn.org/resource/ISSN/'+identifier.value) : ""}"
                                   var="url"/>
                        </g:elseif>
                        <g:else>
                            <g:set value="${identifier.value ? (identifier.value.startsWith('http') ? identifier.value : "") : ""}"
                                   var="url"/>
                        </g:else>



                        <g:if test="${url}">
                            &nbsp;<a aria-label="${identifier.value}" href="${url}" target="_blank"><i
                                class="external alternate icon"></i></a>
                        </g:if>

                            <g:link controller="resource" action="show" id="${identifier.getOID()}" title="Jump to resource"><i class="fas fa-eye"></i></g:link>
                    </td>
                    <g:if test="${editable}">
                        <td>
                            <g:link controller='ajaxHtml'
                                    action='delete'
                                    params="${["__context": "${identifier.getOID()}", 'activeTab': activeTab, curationOverride: params.curationOverride]}"
                                    class="confirm-click btn-delete"
                                    title="Delete this link"
                                    data-confirm-message="Are you sure you wish to delete this Identifier (${identifier.namespace.value}: ${identifier.value})?">Delete</g:link>
                        </td>
                    </g:if>
                </tr>
            </g:each>
        </tbody>
    </table>

            <g:if test="${editable}">
                <g:set var="ctxoid" value="${wekb.ClassUtils.deproxy(d).class.name}:${d.id}"/>
                <a class="ui right floated primary button" href="#" onclick="$('#identifiersModal').modal('show');">Add Identifier</a>
                <br>
                <br>
                <semui:modal id="identifiersModal" title="Add Identifier">

                    <g:form controller="ajaxHtml" action="addIdentifier" class="ui form">
                        <input type="hidden" name="activeTab" value="identifiers"/>
                        <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                        <input type="hidden" name="__context" value="${ctxoid}" />

                        <div class="field">
                            <label>Identifier Namespace</label>
                            <semui:simpleReferenceDropdown name="identifierNamespace"
                                                           baseClass="wekb.IdentifierNamespace"
                                                           filter1="${d.class.simpleName}"/>
                        </div>

                        <div class="field">
                            <label for="identifierValue">Identifier Value</label>

                            <input type="text" id="identifierValue" name="identifierValue" required/>
                        </div>

                    </g:form>
                </semui:modal>
            </g:if>
</semui:tabsItemContent>
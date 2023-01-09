<g:set var="current_key" value=""/>
<table class="table table-bordered"><table class="ui selectable striped sortable celled table">    <thead>
    <tr>
        <th>Property</th>
        <th>Value</th>
        <g:if test="${editable}">
            <th>Actions</th>
        </g:if>
    </tr>
    </thead>
    <tbody>
    <g:each in="${d.additionalProperties.sort { it.propertyDefn?.propertyName }}" var="cp">
        <g:if test="${current_key != cp.propertyDefn?.propertyName}">
            <tr>
                <g:set var="current_key" value="${cp.propertyDefn?.propertyName}"/>
                <td>${cp.propertyDefn?.propertyName}</td>
                <td>${cp.apValue}</td>
                <g:if test="${editable}">
                    <td>
                        <g:link controller="ajaxSupport" action="unlinkManyToMany" class="confirm-click"
                                data-confirm-message="Are you sure you wish to unlink this property?"
                                params="${["__property": "additionalProperties", "__context": d.getClassName() + ":" + d.id, "__itemToRemove": cp.getClassName() + ":" + cp.id]}">Delete</g:link>
                    </td>
                </g:if>
            </tr>
        </g:if>
        <g:else>
            <tr class="grouped">
                <td></td>
                <td>${cp.apValue}</td>
                <g:if test="${editable}">
                    <td></td>
                </g:if>
            </tr>
        </g:else>
    </g:each>
    </tbody>
</table>

<g:if test="${editable}">

    <a class="ui right floated black button" href="#" onclick="$('#additionalPropertiesModal').modal('show');">Add Additional Property</a>

    <br>
    <br>

        <semui:modal id="additionalPropertiesModal" title="Add Additional Property">

            <g:form controller="ajaxSupport" action="addToCollection">
                <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
                <input type="hidden" name="__newObjectClass" value="org.gokb.cred.KBComponentAdditionalProperty"/>
                <input type="hidden" name="__addToColl" value="additionalProperties"/>
                <div class="field">
                    <label>Additional Property Definition</label>
                <semui:simpleReferenceDropdown  name="propertyDefn"
                                                  baseClass="org.gokb.cred.AdditionalPropertyDefinition"
                                                  editable="${editable}"/>
                </div>
                <div class="field">
                    <label>Value</label>
                    <input type="text"  name="apValue"/>
                </div>
            </g:form>
        </semui:modal>

</g:if>




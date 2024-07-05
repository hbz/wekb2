<%@ page import="wekb.helper.RCConstants; wekb.RefdataCategory" %>
<g:if test="${d.id != null}">
    <div class="ui bulleted list">
        <g:each in="${d.vendors.sort { it.vendor.name }}" var="packageVendor">
            <div class="item">
                <g:link controller="resource"
                        action="show"
                        id="${packageVendor.vendor.getOID()}">
                    ${packageVendor.vendor.name}
                </g:link>
            <g:if test="${editable}">
                <g:link class='ui right floated negative mini button' controller='ajaxHtml'
                        action='removeVendorFromPackage'
                        params="${["__context": "${d.getOID()}", "__relatedObject":"${packageVendor.getOID()}", curationOverride: params.curationOverride]}">Unlink</g:link>
            </g:if>
            </div>
        </g:each>
    </div>

    <g:if test="${editable}">
        <a class="ui right floated primary button" href="#" onclick="$('#vendorsModal').modal('show');">Add Vendor</a>

        <br>
        <br>

        <semui:modal id="vendorsModal" title="Add Vendor">
            <g:form controller="ajaxHtml" action="addVendorToPackage" class="ui form">
                <input type="hidden" name="__context" value="${d.getOID()}"/>
                <input type="hidden" name="__property" value="vendors"/>
                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                <div class="field">
                    <label>Vendor:</label>

                    <semui:simpleReferenceDropdown name="__relatedObject"
                                                   baseClass="wekb.Vendor"
                                                   filter1="Current"/>
                </div>

            </g:form>
        </semui:modal>
    </g:if>
</g:if>
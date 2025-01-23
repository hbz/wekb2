<%@ page import="wekb.helper.RCConstants" %>
<g:if test="${d.id}">
    <semui:tabs>
        <semui:tabsItemWithoutLink tab="anOrder" defaultTab="anOrder" activeTab="${params.activeTab}">
           Ordering
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="invoice" activeTab="${params.activeTab}">
            Invoicing
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="generalServices" activeTab="${params.activeTab}">
            General services
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="supplierInformation" activeTab="${params.activeTab}">
            Supplier information
        </semui:tabsItemWithoutLink>

        <semui:tabsItemWithoutLink tab="packages" activeTab="${params.activeTab}" counts="${d.packages.size()}">
            Packages
        </semui:tabsItemWithoutLink>

        <semui:tabsItemWithoutLink tab="providers" activeTab="${params.activeTab}" counts="${d.getProvidersCount()}">
            Providers
        </semui:tabsItemWithoutLink>
    </semui:tabs>


    <semui:tabsItemContent tab="anOrder" defaultTab="anOrder" activeTab="${params.activeTab}">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    via Webshop
                </dt>
                <dd>
                    <semui:xEditableBoolean owner="${d}" field="webShopOrders"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    via XML
                </dt>
                <dd>
                    <semui:xEditableBoolean owner="${d}" field="xmlOrders"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    via EDI
                </dt>
                <dd>
                    <semui:xEditableBoolean owner="${d}" field="ediOrders"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Supported Library Systems
                </dt>
                <dd>
                    <table class="ui small selectable striped celled table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Supported Library System</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${d.supportedLibrarySystems.sort { it.supportedLibrarySystem.value }}"
                                var="vendorSupportedLibrarySystem" status="i">
                            <tr>
                                <td>${i + 1}</td>
                                <td><semui:xEditableRefData owner="${vendorSupportedLibrarySystem}"
                                                            field="supportedLibrarySystem"
                                                            config="${RCConstants.VENDOR_SUPPORTED_LIB_SYSTEM}"/>
                                <td>
                                    <g:if test="${editable}">
                                        <g:link controller='ajaxHtml'
                                                action='delete'
                                                params="${["__context": "${vendorSupportedLibrarySystem.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>
                                    </g:if>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>

                    <g:if test="${editable}">
                        <a class="ui right floated primary button" href="#"
                           onclick="$('#supportedLibrarySystemsModal').modal('show');">Add supported Library System</a>

                        <br>
                        <br>

                        <semui:modal id="supportedLibrarySystemsModal" title="Add supported Library System">

                            <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
                                <input type="hidden" name="__context" value="${d.getOID()}"/>
                                <input type="hidden" name="__newObjectClass" value="wekb.VendorLibrarySystem"/>
                                <input type="hidden" name="__recip" value="vendor"/>
                                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>
                                <input type="hidden" name="activeTab" value="anOrder"/>

                                <div class="field">
                                    <label>Supported Library System:</label> <semui:simpleReferenceDropdown
                                        name="supportedLibrarySystem"
                                        baseClass="wekb.RefdataValue"
                                        filter1="${RCConstants.VENDOR_SUPPORTED_LIB_SYSTEM}"/>
                                </div>
                            </g:form>
                        </semui:modal>
                    </g:if>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Electronic delivery delay notifications via
                </dt>
                <dd>
                    <table class="ui small selectable striped celled table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Electronic delivery delay notifications via</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${d.electronicDeliveryDelays.sort { it.electronicDeliveryDelay.value }}"
                                var="vendorElectronicDeliveryDelay" status="i">
                            <tr>
                                <td>${i + 1}</td>
                                <td><semui:xEditableRefData owner="${vendorElectronicDeliveryDelay}"
                                                            field="electronicDeliveryDelay"
                                                            config="${RCConstants.VENDOR_ELECTRONIC_DELIVERY_DELAY}"/>
                                <td>
                                    <g:if test="${editable}">
                                        <g:link controller='ajaxHtml'
                                                action='delete'
                                                params="${["__context": "${vendorElectronicDeliveryDelay.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>
                                    </g:if>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>

                    <g:if test="${editable}">
                        <a class="ui right floated primary button" href="#"
                           onclick="$('#electronicDeliveryDelaysModal').modal('show');">Add electronic delivery delay notifications</a>

                        <br>
                        <br>

                        <semui:modal id="electronicDeliveryDelaysModal" title="Add electronic delivery delay notifications">

                            <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
                                <input type="hidden" name="__context" value="${d.getOID()}"/>
                                <input type="hidden" name="__newObjectClass" value="wekb.VendorElectronicDeliveryDelay"/>
                                <input type="hidden" name="__recip" value="vendor"/>
                                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>
                                <input type="hidden" name="activeTab" value="anOrder"/>

                                <div class="field">
                                    <label>Electronic delivery delay notifications via:</label> <semui:simpleReferenceDropdown
                                        name="electronicDeliveryDelay"
                                        baseClass="wekb.RefdataValue"
                                        filter1="${RCConstants.VENDOR_ELECTRONIC_DELIVERY_DELAY}"/>
                                </div>
                            </g:form>
                        </semui:modal>
                    </g:if>
                </dd>
            </dl>
        </div>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="invoice" activeTab="${params.activeTab}">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    Electronic Invoice Formats
                </dt>
                <dd>
                    <table class="ui small selectable striped celled table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Format</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${d.electronicBillings.sort { it.electronicBilling.value }}"
                                var="vendorElectronicBilling" status="i">
                            <tr>
                                <td>${i + 1}</td>
                                <td><semui:xEditableRefData owner="${vendorElectronicBilling}" field="electronicBilling"
                                                            config="${RCConstants.VENDOR_ELECTRONIC_BILLING}"/>
                                <td>
                                    <g:if test="${editable}">
                                        <g:link controller='ajaxHtml'
                                                action='delete'
                                                params="${["__context": "${vendorElectronicBilling.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>
                                    </g:if>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>

                    <g:if test="${editable}">
                        <a class="ui right floated primary button" href="#"
                           onclick="$('#electronicBillingsModal').modal('show');">Add Electronic Invoice Format</a>

                        <br>
                        <br>

                        <semui:modal id="electronicBillingsModal" title="Add supported Library System">

                            <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
                                <input type="hidden" name="__context" value="${d.getOID()}"/>
                                <input type="hidden" name="__newObjectClass" value="wekb.VendorElectronicBilling"/>
                                <input type="hidden" name="__recip" value="vendor"/>
                                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>
                                <input type="hidden" name="activeTab" value="invoice"/>


                                <div class="field">
                                    <label>Electronic Invoice Format:</label> <semui:simpleReferenceDropdown
                                        name="electronicBilling"
                                        baseClass="wekb.RefdataValue"
                                        filter1="${RCConstants.VENDOR_ELECTRONIC_BILLING}"/>
                                </div>
                            </g:form>
                        </semui:modal>
                    </g:if>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Invoice dispatch via
                </dt>
                <dd>

                    <table class="ui small selectable striped celled table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Method</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${d.invoiceDispatchs.sort { it.invoiceDispatch.value }}" var="vendorInvoiceDispatch"
                                status="i">
                            <tr>
                                <td>${i + 1}</td>
                                <td><semui:xEditableRefData owner="${vendorInvoiceDispatch}" field="invoiceDispatch"
                                                            config="${RCConstants.VENDOR_INVOICE_DISPATCH}"/>
                                <td>
                                    <g:if test="${editable}">
                                        <g:link controller='ajaxHtml'
                                                action='delete'
                                                params="${["__context": "${vendorInvoiceDispatch.getOID()}", curationOverride: params.curationOverride]}">Delete</g:link>
                                    </g:if>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>

                    <g:if test="${editable}">
                        <a class="ui right floated primary button" href="#"
                           onclick="$('#invoiceDispatchsModal').modal('show');">Add Invoice dispatch</a>

                        <br>
                        <br>

                        <semui:modal id="invoiceDispatchsModal" title="Add supported Library System">

                            <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
                                <input type="hidden" name="__context" value="${d.getOID()}"/>
                                <input type="hidden" name="__newObjectClass" value="wekb.VendorInvoiceDispatch"/>
                                <input type="hidden" name="__recip" value="vendor"/>
                                <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>
                                <input type="hidden" name="activeTab" value="invoice"/>


                                <div class="field">
                                    <label>Method:</label> <semui:simpleReferenceDropdown
                                        name="invoiceDispatch"
                                        baseClass="wekb.RefdataValue"
                                        filter1="${RCConstants.VENDOR_INVOICE_DISPATCH}"/>
                                </div>
                            </g:form>
                        </semui:modal>
                    </g:if>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Paper Invoice
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="paperInvoice"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Management of Credits
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="managementOfCredits"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Processing of compensation payments (credits/subsequent debits)
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="processingOfCompensationPayments"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Individual invoice design
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="individualInvoiceDesign"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
        </div>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="generalServices" activeTab="${params.activeTab}">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    Technical Support
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="technicalSupport"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Metadata (MARC records)
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="shippingMetadata"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Forwarding usage statistics from the publisher
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="forwardingUsageStatisticsFromPublisher"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Alerts about new publications within e-book packages
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="activationForNewReleases"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Exchange of individual titles within e-book packages
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="exchangeOfIndividualTitles"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Research platform for e-books
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" validation="url" outGoingLink="true" field="researchPlatformForEbooks"/>
                </dd>
            </dl>
        </div>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="supplierInformation" activeTab="${params.activeTab}">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    Prequalification
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="prequalification"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Info to Prequalification
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="prequalificationInfo"/>
                </dd>
            </dl>
        </div>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="packages" activeTab="${params.activeTab}">
        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'packages' ? params.offset : '', sort: params.activeTab == 'packages' ? params.sort : '', order: params.activeTab == 'packages' ? params.order : '', qbe: 'g:packages', qp_vendor_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_vendor', 'qp_vendor_id']]"
                    id="">Packages</g:link>

        </div>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="providers" activeTab="${params.activeTab}">
        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.activeTab == 'providers' ? params.offset : '', sort: params.activeTab == 'providers' ? params.sort : '', order: params.activeTab == 'providers' ? params.order : '', qbe: 'g:orgs', qp_vendor_id: d.id, inline: true, refOID: d.getOID(), hide: ['qp_vendor', 'qp_vendor_id']]"
                    id="">Providers</g:link>

        </div>
    </semui:tabsItemContent>

</g:if>
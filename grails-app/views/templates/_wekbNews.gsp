<h2 class="ui header">we:kb News <span class="ui primary  label">
    Changes in the last <strong>14</strong> days.
</span></h2>


<div class="ui two column stackable wekb-widescreenMonitorBreakpoint grid">

    <div class="column">
        <div class="ui fluid card" style="margin-bottom:0">
            <div class="content">
                <div class="header">
                    Packages
                </div>
            </div>

            <div class="content">
                <div class="description">
                    <div class="ui top attached tabular menu">
                        <a class="item" data-tab="packageNew">New <div
                                class="ui floating primary label">${news.package.countNewInDB}</div></a>
                        <a class="item"
                           data-tab="packageLastUpdated">Changes <div
                                class="ui floating primary label">${news.package.countLastUpdatedInDB}</div></a>
                    </div>

                    <div class="ui bottom attached tab segment" data-tab="packageNew">

                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Provider</th>
                                <th>Curatory Group</th>
                                <th>Date Created</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.package.newInDB}" var="obj" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td>
                                        <g:if test="${obj.provider}">
                                            <g:link controller="resource"
                                                    action="show"
                                                    id="${obj.provider.getOID()}">
                                                ${obj.provider.name}
                                            </g:link>
                                        </g:if>
                                    </td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.dateCreated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.package.countNewInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:packages', createdSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>
                    </div>


                    <div class="ui bottom attached tab segment" data-tab="packageLastUpdated">
                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Provider</th>
                                <th>Curatory Group</th>
                                <th>Last Updated</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.package.lastUpdatedInDB}" var="obj" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td>
                                        <g:if test="${obj.provider}">
                                            <g:link controller="resource"
                                                    action="show"
                                                    id="${obj.provider.getOID()}">
                                                ${obj.provider.name}
                                            </g:link>
                                        </g:if>
                                    </td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.lastUpdated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.package.countLastUpdatedInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:packages', changedSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="column">
        <div class="ui fluid card" style="margin-bottom:0">
            <div class="content">
                <div class="header">
                    Platforms
                </div>
            </div>

            <div class="content">
                <div class="description">
                    <div class="ui top attached tabular menu">
                        <a class="item" data-tab="platformNew">New <div
                                class="ui floating primary label">${news.platform.countNewInDB}</div></a>
                        <a class="item"
                           data-tab="platformLastUpdated">Changes <div
                                class="ui floating primary label">${news.platform.countLastUpdatedInDB}</div></a>
                    </div>

                    <div class="ui bottom attached tab segment" data-tab="platformNew">
                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Provider</th>
                                <th>Curatory Group</th>
                                <th>Date Created</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.platform.newInDB}" var="obj" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td>
                                        <g:if test="${obj.provider}">
                                            <g:link controller="resource"
                                                    action="show"
                                                    id="${obj.provider.getOID()}">
                                                ${obj.provider.name}
                                            </g:link>
                                        </g:if>
                                    </td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.dateCreated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.platform.countNewInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:platforms', createdSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>
                    </div>

                    <div class="ui bottom attached tab segment" data-tab="platformLastUpdated">
                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Provider</th>
                                <th>Curatory Group</th>
                                <th>Last Updated</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.platform.lastUpdatedInDB}" var="obj" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td>
                                        <g:if test="${obj.provider}">
                                            <g:link controller="resource"
                                                    action="show"
                                                    id="${obj.provider.getOID()}">
                                                ${obj.provider.name}
                                            </g:link>
                                        </g:if>
                                    </td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.lastUpdated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.platform.countLastUpdatedInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:platforms', changedSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="ui two column stackable wekb-widescreenMonitorBreakpoint grid">

    <div class="column">
        <div class="ui fluid card" style="margin-bottom:0">
            <div class="content">
                <div class="header">
                    Providers
                </div>
            </div>

            <div class="content">
                <div class="description">

                    <div class="ui top attached tabular menu">
                        <a class="item" data-tab="orgNew">New <div
                                class="ui floating primary label">${news.org.countNewInDB}</div></a>
                        <a class="item" data-tab="orgLastUpdated">Changes <div
                                class="ui floating primary label">${news.org.countLastUpdatedInDB}</div></a>
                    </div>

                    <div class="ui bottom attached tab segment" data-tab="orgNew">
                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Curatory Group</th>
                                <th>Date Created</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.org.newInDB}" var="obj" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.dateCreated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.org.countNewInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:orgs', createdSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>
                    </div>

                    <div class="ui bottom attached tab segment" data-tab="orgLastUpdated">
                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Curatory Group</th>
                                <th>Last Updated</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.org.lastUpdatedInDB}" var="obj" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.lastUpdated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.org.countLastUpdatedInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:orgs', changedSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>
                    </div>

                </div>
            </div>
        </div>
    </div>

    <div class="column">
        <div class="ui fluid card" style="margin-bottom:0">
            <div class="content">
                <div class="header">
                    Library Suppliers
                </div>
            </div>

            <div class="content">
                <div class="description">

                    <div class="ui top attached tabular menu">
                        <a class="item" data-tab="vendorNew">New <div
                                class="ui floating primary label">${news.vendor.countNewInDB}</div></a>
                        <a class="item" data-tab="vendorLastUpdated">Changes <div
                                class="ui floating primary label">${news.vendor.countLastUpdatedInDB}</div></a>
                    </div>

                    <div class="ui bottom attached tab segment" data-tab="vendorNew">
                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Curatory Group</th>
                                <th>Date Created</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.vendor.newInDB}" var="obj" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.dateCreated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.vendor.countNewInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:vendors', createdSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>
                    </div>

                    <div class="ui bottom attached tab segment" data-tab="vendorLastUpdated">
                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Curatory Group</th>
                                <th>Last Updated</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.vendor.lastUpdatedInDB}" var="obj" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.getCuratoryGroupObjects()}" var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.lastUpdated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.vendor.countLastUpdatedInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:vendors', changedSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<div class="ui two column stackable wekb-widescreenMonitorBreakpoint grid">
    <div class="column">
        <div class="ui fluid card" style="margin-bottom:0">
            <div class="content">
                <div class="header">
                    Titles
                </div>
            </div>

            <div class="content">
                <div class="description">
                    <div class="ui top attached tabular menu">
                        <a class="item"
                           data-tab="tippNew">New <div
                                class="ui floating primary label">${news.titleinstancepackageplatform.countNewInDB}</div>
                        </a>
                        <a class="item"
                           data-tab="tippLastUpdated">Changes <div
                                class="ui floating primary label">${news.titleinstancepackageplatform.countLastUpdatedInDB}</div>
                        </a>
                    </div>

                    <div class="ui bottom attached tab segment" data-tab="tippNew">
                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Package</th>
                                <th>Curatory Group</th>
                                <th>Date Created</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.titleinstancepackageplatform.newInDB}" var="obj" status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.pkg.getOID()}">
                                        ${obj.pkg.name}
                                    </g:link></td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.pkg.getCuratoryGroupObjects()}"
                                                    var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.dateCreated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.titleinstancepackageplatform.countNewInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:tipps', createdSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>

                    </div>

                    <div class="ui bottom attached tab segment" data-tab="tippLastUpdated">
                        <table class="ui selectable striped sortable celled table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Package</th>
                                <th>Curatory Group</th>
                                <th>Last Updated</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${news.titleinstancepackageplatform.lastUpdatedInDB}" var="obj"
                                    status="i">
                                <tr>
                                    <td>${i + 1}</td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.getOID()}">
                                        ${obj.name}
                                    </g:link></td>
                                    <td><g:link controller="resource"
                                                action="show"
                                                id="${obj.pkg.getOID()}">
                                        ${obj.pkg.name}
                                    </g:link></td>
                                    <td>
                                        <div class="ui bulleted list">
                                            <g:each in="${obj.pkg.getCuratoryGroupObjects()}"
                                                    var="curatoryGroup">
                                                <div class="item">
                                                    ${curatoryGroup.name}
                                                </div>
                                            </g:each>
                                        </div>
                                    </td>
                                    <td>
                                        <g:formatDate
                                                format="${message(code: 'default.date.format.noZWihoutSS')}"
                                                date="${obj.lastUpdated}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <g:if test="${news.titleinstancepackageplatform.countLastUpdatedInDB > 50}">
                            <g:link class="ui primary button" controller="search" action="componentSearch"
                                    params="[qbe: 'g:tipps', changedSince: dateFor14Days, sort: 'lastUpdated', order: 'desc']">Show more</g:link>
                        </g:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
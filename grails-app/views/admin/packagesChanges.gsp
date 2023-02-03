<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>we:kb : Packages Changes</title>
</head>

<body>

<wekb:serviceInjection/>

    <h1 class="ui header">Packages Changes (${packagesCount})</h1>




            <table class="ui selectable striped sortable celled table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Package Name</th>
                    <th>Provider</th>
                    <th>Curatory Groups</th>
                    <th>Title Count</th>
                    <th>Last Updated</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${packages}" var="pkg" status="i">
                    <tr>
                        <td>
                            ${ (params.int('offset') ?: 0)  + i + 1 }
                        </td>
                        <td>
                            <g:link controller="resource" action="show"
                                    id="${pkg.id}">${pkg.name}</g:link>

                        </td>
                        <td>${pkg.provider?.name}</td>
                        <td>
                            <g:if test="${pkg.curatoryGroups.size() > 0}">
                                <g:each in="${pkg.curatoryGroups}" var="curatoryGroupPackage" status="c">
                                    <g:if test="${c > 0}"><br></g:if>
                                    ${curatoryGroupPackage.curatoryGroup.name}
                                    <g:if test="${curatoryGroupPackage.curatoryGroup.type}">
                                        (${curatoryGroupPackage.curatoryGroup.type.value})
                                    </g:if>
                                </g:each>
                            </g:if>
                            <g:else>
                                <div>No Curators</div>
                            </g:else>
                        </td>
                        <td>${pkg.currentTippCount}</td>
                        <td>
                            <g:if test="${pkg.lastUpdated}">
                                <g:formatDate format="${message(code: 'default.date.format')}"
                                              date="${pkg.lastUpdated}"/>
                            </g:if>
                        </td>
                        <td>
                           %{-- TODO:Moe
                           <a data-toggle="modal" data-cache="false"
                               title="Show History (with Titles)"
                               data-remote='<g:createLink controller="fwk" action="history"
                                                          id="${pkg.uuid}"
                                                          params="[withCombos: true]"/>'
                               data-target="#infoModal">Show History with Title</a>

                            <br><br>
                            <a data-toggle="modal" data-cache="false"
                               title="Show History"
                               data-remote='<g:createLink controller="fwk" action="history" id="${pkg.class.name}:${pkg.id}"/>'
                               data-target="#infoModal">Show History</a>--}%

                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>


                    <semui:paginate controller="${controllerName}" action="${actionName}" params="${params}"
                                max="${max}" total="${packagesCount}"/>



%{--<div id="infoModal" class="qmodal modal fade modal-wide" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>

                <h3 class="modal-title">Loading Content..</h3>
            </div>

            <div class="modal-body"></div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<asset:script type="text/javascript">

    $(document).on('show.bs.modal','#infoModal', function(){
      $(".modal-content").empty();
      $(".modal-content").append('<div class="modal-loading"><h4>Loading <asset:image src="img/loading.gif"/></h4></div>');
        });

</asset:script>--}%

</body>
</html>

<!DOCTYPE html>
<%@ page import="org.gokb.cred.TitleInstancePackagePlatform; de.wekb.helper.RDStore;" %>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>${displayobj?.getNiceName() ?: 'Component'}
    <g:if test="${displayobj}">
        &lt;${editable ? 'Editable' : (response.status == 403 ? 'Not Accessible' : 'Read Only')}&gt;
    </g:if>
    <g:else>
        &lt;Not found&gt;
    </g:else>
    </title>
</head>

<body>

<semui:flashMessage data="${flash}"/>

%{--<nav class="navbar navbar-inverse ${(displayobj.respondsTo('status') && displayobj.status == RDStore.KBC_STATUS_DELETED) ? 'alert alert-danger' : ''}">
    <div class="container-fluid">
        <div class="navbar-header">
            <span class="navbar-brand">
                <g:if test="${displayobj && displayobj.id != null}">
                    ${displayobj.getNiceName() ?: 'Component'} : ${displayobj.id}
                    <g:if test="${response.status != 403}">
                        <g:if test="${displayobj.respondsTo('getDisplayName') && displayobj.getDisplayName()}">- <strong>${displayobj.getDisplayName()}</strong></g:if>
                        <g:if test="${!editable}"><small><i>&lt;Read only&gt;</i></small></g:if>
                        <g:if test="${displayobj.respondsTo('status') && displayobj.status == RDStore.KBC_STATUS_DELETED}"><i>&lt;This ${displayobj.getNiceName() ?: 'Component'} is deleted!></i></g:if>
                    </g:if>
                    <g:else>
                        <small><i>&lt;Not Accessible&gt;</i></small>
                    </g:else>
                </g:if>
                <g:elseif test="${displayobj}">
                    Create New ${displayobj.getNiceName() ?: 'Component'}
                </g:elseif>
            </span>
        </div>
        <g:if test="${displayobj && response.status != 403 && displayobj.class.simpleName in ["Package", "Org", "Platform", "TitleInstancePackagePlatform"]}">
            <ul class="nav navbar-nav navbar-right">
                <g:if test="${org.gokb.cred.KBComponent.isAssignableFrom(displayobj.class)}">
                    <li><a onClick="javascript:toggleWatch('${displayobj.class.name}:${displayobj.id}')"
                           id="watchToggleLink"
                           title="${user_watching ? 'You are watching this item' : 'You are not watching this item'}"
                           style="cursor:pointer;">
                        <i id="watchIcon" class="fa ${user_watching ? 'fa-eye' : 'fa-eye-slash'}"></i> <span
                                id="watchCounter" class="badge badge-warning">${num_watch}</span></a></li>

                    <li><a data-toggle="modal" data-cache="false"
                           title="Show History (with Titles)"
                           data-remote='<g:createLink controller="fwk" action="history"
                                                      id="${displayobj.class.name}:${displayobj.id}"
                                                      params="[withCombos: true]"/>'
                           data-target="#infoModal"><i class="fas fa-history"></i></a></li>
                </g:if>
                <li><a data-toggle="modal" data-cache="false"
                       title="Show Notes"
                       data-remote='<g:createLink controller="fwk" action="notes"
                                                  id="${displayobj.class.name}:${displayobj.id}"/>'
                       data-target="#infoModal"><i class="fa fa-comments"></i>
                    <span class="badge badge-warning">${num_notes}</span>
                </a></li>
            </ul>
        </g:if>
    </div>
</nav>--}%
<g:if test="${displayobj != null}">

    <g:if test="${displayobj.hasProperty('status') && displayobj.status == RDStore.KBC_STATUS_REMOVED}">
        <div class="ui negative icon huge message">
            <i class="info icon"></i>

            <div class="content">
                <div class="header">
                    Removed component
                </div>

                <p>This component has been set to removed and will soon be permanently removed from this system</p>
            </div>
        </div>
    </g:if>

    <g:if test="${displayobj.respondsTo('availableActions') && editable}">

        <g:set var="object" value="${displayobj.class.name}:${displayobj.id}"/>

        <div class="ui right floated buttons">
        <semui:actionsDropdown text="Available actions">
                <g:each var="action" in="${displayobj.userAvailableActions().sort{it.label}}">
                    <g:if test="${action.code in ["packageUrlUpdate", "packageUrlUpdateAllTitles"]}">
                        <g:if test="${displayobj.source}">
                            <semui:actionsDropdownItem controller="workflow" action="action"
                                                       params="[component: object, selectedBulkAction: action.code, curationOverride: params.curationOverride]" text="${action.label}"/>
                        </g:if>
                    </g:if>
                    <g:else>
                        <semui:actionsDropdownItem controller="workflow" action="action"
                                                   params="[component: object, selectedBulkAction: action.code, curationOverride: params.curationOverride]" text="${action.label}"/>
                    </g:else>

                </g:each>
        </semui:actionsDropdown>
        </div>

        %{--<g:form controller="workflow" action="action" method="post" class='action-form'>
            <h4>Available actions</h4>
            <input type="hidden"
                   name="bulk:${displayobj.class.name}:${displayobj.id}"
                   value="true"/>

            <div class="input-group">
                <select id="selectedAction" name="selectedBulkAction" >
                    <option value="">-- Select an action to perform --</option>
                    <g:each var="action" in="${displayobj.userAvailableActions()}">
                        <g:if test="${action.code in ["packageUrlUpdate", "packageUrlUpdateAllTitles"]}">
                            <g:if test="${displayobj.source}">
                                <option value="${action.code}">
                                    ${action.label}
                                </option>
                            </g:if>
                        </g:if>
                        <g:else>
                            <option value="${action.code}">
                                ${action.label}
                            </option>
                        </g:else>

                    </g:each>
                </select>
                <span class="input-group-btn">
                    <button type="submit" class="btn btn-default">Go</button>
                </span>
            </div>
        </g:form>--}%
    </g:if>

    <h1 class="ui header">${displayobj.getDomainName()}: ${displayobj.hasProperty('name') ? displayobj.name : ''}</h1>

    <div class="ui segment">
        <g:render template="rightBox" model="${[d: displayobj]}"/>

        <div class="content wekb-inline-lists">
            <g:if test="${displaytemplate != null}">
                <!-- Using display template ${displaytemplate.rendername} -->
                <g:if test="${displaytemplate.type == 'staticgsp'}">
                    <g:render template="/templates/domains/${displaytemplate.rendername}"
                              model="${[d: displayobj, rd: refdata_properties, dtype: displayobjclassname_short]}"/>
                </g:if>
            </g:if>
        </div>
    </div>

    <br>
    <br>
    <g:if test="${displaytemplate != null}">
        <g:if test="${displaytemplate.type == 'staticgsp' && displaytemplate.rendername in ["org", "package", "platform", "source", "tipp"]}">
            <g:render template="/templates/tabTemplates/domainTabs/${displaytemplate.rendername}Tabs"
                      model="${[d: displayobj]}"/>
        </g:if>
    </g:if>
</g:if>
<g:else>
    <semui:message class="negative">
        <g:message code="component.notFound.label" args="[params.id]"/>
    </semui:message>
</g:else>


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

        $(document).ready(function(){

          $('a.editable').on('click', function(){
            var editable = $(this);

            var select = editable.next();

            var submit = select.find('.editable-submit');

            submit.on('click', function(){

              var follow_link = select.next();
              var related_editable = select.prev();

              window.setTimeout(function() {

                var new_linked_oid = null;

                editable.each(function() {
                  var new_tid = false;

                  $.each(this.attributes, function(){
                    if(this.specified && this.name == 'target-id') {
                      new_linked_oid = this.value;
                      new_tid = true;
                    }
                  })

                  if(!new_tid) {
                    new_linked_oid = null;
                  }
                })

                if (new_linked_oid) {
                  if (follow_link.attr('href')) {
                    var old_href = follow_link.attr('href');
                    var truncated_link = old_href.substring(0, old_href.lastIndexOf('/') + 1);

                    follow_link.attr('href', truncated_link + new_linked_oid);
                  }else {
                    var new_url = contextPath + "/resource/show/" + new_linked_oid;
                    related_editable.after(' &nbsp; <a href="' + new_url + '"><i class="fas fa-eye"></i></a>');
                  }
                } else if (follow_link.attr('href')) {
                  $(follow_link).remove();
                }
              }, 500);
            });
          });
        });

        function toggleWatch(oid) {
          $.ajax({
            url: '/gokb/fwk/toggleWatch?oid='+oid,
            dataType:"json"
          }).done(function(data) {
            var counter = parseInt($('#watchCounter').html());
            if ( data.change == '-1' ) {
              $('#watchToggleLink').prop('title','You are not watching this resource');
              $('#watchIcon').removeClass('fas fa-eye');
              $('#watchIcon').addClass('far fa-eye-slash');
              $('#watchCounter').html(counter-1);
            }
            else {
              $('#watchToggleLink').prop('title','You are watching this resource');
              $('#watchIcon').removeClass('far fa-eye-slash');
              $('#watchIcon').addClass('fas fa-eye');
              $('#watchCounter').html(counter+1);
            }
          });
        }

        var hash = window.location.hash;
        hash && $('ul.nav a[href="' + hash + '"]').tab('show');

        $('.nav-tabs > li > a').not('.disabled').click(function (e) {
          $(this).tab('show');
          var scrollmem = $('body').scrollTop();
          console.log("scrollTop");
          window.location.hash = this.hash;
          $('html,body').scrollTop(scrollmem);
        });

</asset:script>--}%
</body>
</html>

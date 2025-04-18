<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : <g:message code="public.search"/><g:if test="${qbetemplate}"> <g:message code="${qbetemplate.msgCode}" default="${qbetemplate.title}"/></g:if><g:if
            test="${refObject}">${refObject.getDomainName()}</g:if></title>
</head>

<body>

<g:if test="${qbetemplate}">
    <h1 class="ui header"><g:message code="public.search"/> <g:message code="${qbetemplate.msgCode}" default="${qbetemplate.title}"/> <g:if test="${refObject}">for ${refObject.getDomainName()}: <g:link
            controller="resource" action="show" id="${refObject.getOID()}">${refObject.name}</g:link></g:if></h1>
</g:if>
<g:else>
    <h1 class="ui header"><g:message code="public.search"/></h1>
</g:else>

<semui:flashMessage data="${flash}"/>

<div class="container">

    <g:if test="${qbetemplate == null}">

        <h3 class="ui header">
            Please select a resource to search for
        </h3>

        <div class="ui bulleted link list">
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:packages']"><g:message code="public.packages"/></g:link>
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:platforms']"><g:message code="public.platforms"/></g:link>
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:orgs']"><g:message code="public.providers"/></g:link>
            <g:link class="item" controller="search" action="componentSearch" params="[qbe: 'g:tipps']"><g:message code="public.titles"/></g:link>
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:curatoryGroups']"><g:message code="public.curatoryGroups"/></g:link>
            <g:link class="item" controller="search" action="componentSearch"
                    params="[qbe: 'g:sources']"><g:message code="public.sources"/></g:link>
        </div>

        <br>
        <br>
    </g:if>
    <g:else>
        <g:if test="${(qbetemplate.message != null)}">
            <semui:message message="${qbetemplate.message}"/>
        </g:if>

        <sec:ifAnyGranted roles="ROLE_ADMIN">
            <div class="ui right floated buttons">
                <semui:actionsDropdown text="Available actions">
                    <semui:actionsDropdownItem description="(Admin)" controller="create" action="index"
                                               params="[tmpl: qbetemplate.baseclass]"
                                               text="Create new ${qbetemplate.title}"/>
                </semui:actionsDropdown>
            </div>
            <br>
            <br>
        </sec:ifAnyGranted>

        <g:render template="qbeform"
                  model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>

        <g:if test="${recset && !init}">
            <g:render template="qberesult"
                      model="${[qbeConfig: qbetemplate.qbeConfig, rows: new_recset, offset: offset, det: det, page: page_current, page_max: page_total, baseClass: qbetemplate.baseclass]}"/>
        </g:if>
        <g:elseif test="${!init && !params.inline}">
            <g:render template="qbeempty"/>
        </g:elseif>
        <g:else>
            <semui:message>
                <p><g:message code="default.empty"/></p>
            </semui:message>
        </g:else>
    </g:else>
</div>

%{--	<g:if test="${displayobj != null}">
	  <div class="col-md-7 desktop-only" >
			<div class="panel panel-default quickview">
				<div class="panel-heading">
					<h3 class="panel-title">Quick View</h3>
				</div>
				<div class="panel-body">
					<!--class="well"-->
	
					<nav class="navbar navbar-inverse">
						<div class="container-fluid">
							<div class="navbar-header">
								<span class="navbar-brand">Record ${det} of ${reccount}</span>
							</div>
	
							<ul class="nav navbar-nav navbar-right">
								<li><a data-toggle="modal" data-cache="false"
									title="Show History"
									data-remote='<g:createLink controller="fwk" action="history" id="${displayobj.class.name}:${displayobj.id}"/>'
									data-target="#modal"><i class="far fa-clock"></i></a></li>
	
								<li><a data-toggle="modal" data-cache="false"
									title="Show Notes"
									data-remote='<g:createLink controller="fwk" action="notes" id="${displayobj.class.name}:${displayobj.id}"/>'
									data-target="#modal"><i class="fas fa-comment"></i></a></li>
	
								<!-- li>
		                      <a data-toggle="modal" 
		                         data-cache="false" 
		                         title="Show File Attachments"
		                         data-remote='<g:createLink controller="fwk" action="attachments" id="${displayobj.class.name}:${displayobj.id}"/>' 
		                         data-target="#modal"><i class="glyphicon glyphicon-file"></i></a>
		                    </li -->
								
								<g:if test="${ det == 1 }">
									<li class="disabled">
										<a class="disabled" href="#" ><i class="fas fa-chevron-left"></i></a>
									</li>
								</g:if>
								<g:else>
									<li><g:link controller="search" title="Previous Record"
											action="componentSearch"
											params="${params+['det':det-1, offset:((int)((det-2) / max))*max]}">
											<i class="fas fa-chevron-left"></i>
										</g:link></li>
								</g:else>
								
								<g:if test="${ det == reccount }">
									<li class="disabled">
										<a class="disabled" href="#" ><i class="fas fa-chevron-right"></i></a>
									</li>
								</g:if>
								<g:else>
									<li><g:link controller="search" title="Next Record"
										action="componentSearch"
										params="${params+['det':det+1, offset:((int)(det / max))*max]}">
										<i class="fas fa-chevron-right"></i>
									</g:link></li>
								</g:else>
								
								<li><g:link controller="search"  action="componentSearch" title="Close"
                    action="index"
                    params="${params+['det':null]}">
                    <i class="fa fa-times"></i>
                  </g:link></li>
							</ul>
						</div>
					</nav>
					<g:if test="${displaytemplate != null}">
						<g:if test="${displaytemplate.type=='staticgsp'}">
							<h4><g:render template="/apptemplates/secondTemplates/component_heading" model="${[d: displayobj]}" /></h4>
							<g:render template="/apptemplates/mainTemplates/${displaytemplate.rendername}"
								model="${[d: displayobj, dtype: displayobjclassname_short]}" />
	
						</g:if>
					</g:if>
					<g:else>
		                No template currently available for instances of ${displayobjclassname}
						${displayobj as grails.converters.JSON}
					</g:else>
				</div>
			</div>
		</div>
	</g:if>
	<div id="modal" class="qmodal modal fade" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h3 class="modal-title">Modal header</h3>
				</div>
				<div class="modal-body"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>--}%
</body>
</html>

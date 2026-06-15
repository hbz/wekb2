<%@ page import=" wekb.Platform; wekb.Org" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
    <script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "WebSite",
  "@id": "https://wekb.hbz-nrw.de/",
  "url": "https://wekb.hbz-nrw.de/",
  "name": "we:kb | wekb",
  "description": "In the we:kb (pronounced wekb), the providers manage their current electronic sales units and meta-information themselves.",
  "potentialAction": {
    "@type": "SearchAction",
    "target": "https://wekb.hbz-nrw.de/?qbe=g%3ApublicPackages&qp_name={Name of Package}&qp_identifier={Identifier}&qp_curgroup={Curatory Group}&qp_provider={Provider}&qp_source_automaticUpdates={Source Automatic Updates}&qp_status={Status}&qp_contentType={Content Type}&qp_ddc={DDCs}&qp_paymentType={Payment Type}&qp_oa={Open Access}&qp_archivingAgency={Package Archiving Agency}&searchAction=Search",
    "query-input": [
      "optional name=qp_name",
      "optional name=qp_identifier",
      "optional name=qp_curgroup",
      "optional name=qp_provider",
      "optional name=qp_source",
      "optional name=qp_status",
      "optional name=qp_contentType",
      "optional name=qp_ddc",
      "optional name=qp_paymentType",
      "optional name=qp_oa",
      "optional name=qp_archivingAgency"
    ]
  }
}
</script>
    <title>we:kb | wekb - ${message(code: 'public.genSearch')}</title>
</head>

<body>

<wekb:serviceInjection/>

<semui:flashMessage data="${flash}"/>

<h1 class="ui header">${message(code: 'public.genSearch')}</h1>

<g:render template="/search/qbeform"
          model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>

<div class="container">

    %{--<div class="ui header">
        <h1>Results ${resultsTotal}</h1>
    </div>

    <div class="ui form">
        <g:form controller="public" action="${actionName}"
                method="get"
                params="${params}">
            <div class="ui right floated header inline field">
                <label>Results on Page</label>
                <g:select name="newMax" from="[10, 25, 50, 100]" value="${params.max}"
                          onChange="this.form.submit()"/>
            </div>
        </g:form>
    </div>

    <br>
    <br>
--}%

    <g:if test="${(qbetemplate.message != null)}">
        <semui:message message="${qbetemplate.message}"/>
    </g:if>

    <g:if test="${recset && !init}">
        <g:render template="/search/qberesult"
                  model="${[qbeConfig: qbetemplate.qbeConfig, rows: new_recset, offset: offset, det: det, page: page_current, page_max: page_total, baseClass: qbetemplate.baseclass]}"/>
    </g:if>
    <g:elseif test="${!init && !params.inline}">
        <g:render template="/search/qbeempty"/>
    </g:elseif>
    <g:else>
        <semui:message>
            <p>No results.</p>
        </semui:message>
    </g:else>

</div>
</body>
</html>

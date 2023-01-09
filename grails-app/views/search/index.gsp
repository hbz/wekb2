<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>Search in All Components</title>
</head>

<body>

<%
    def addFacet = { params, facet, val ->
        def newparams = [:]
        newparams.putAll(params)

        newparams.remove('offset');
        newparams.remove('max');

        def current = newparams[facet]
        if (current == null) {
            newparams[facet] = val
        } else if (current instanceof String[]) {
            newparams.remove(current)
            newparams[facet] = current as List
            newparams[facet].add(val);
        } else {
            newparams[facet] = [current, val]
        }
        newparams
    }

    def removeFacet = { params, facet, val ->
        def newparams = [:]
        newparams.putAll(params)
        def current = newparams[facet]

        newparams.remove('offset');
        newparams.remove('max');

        if (current == null) {
        } else if (current instanceof String[]) {
            newparams.remove(current)
            newparams[facet] = current as List
            newparams[facet].remove(val);
        } else if (current?.equals(val.toString())) {
            newparams.remove(facet);
        }
        newparams
    }
%>

<h1 class="ui header">Search in All Components</h1>

<div class="ui segment">
    <g:form action="index" method="get" class="ui form">
        <div class="sixteen wide field">
            <input type="text" name="q" id="q" value="${params.q}" placeholder="Search for Packages, Titles, Providers, Platforms..."/>
        </div>

        <div class="sixteen wide field">
        <div class="ui toggle checkbox">
            <input type="checkbox" name="allProperties" ${params.allProperties ? 'checked' : ''}>
            <label>Additionally search in all properties of Packages, Titles, Providers, Platforms</label>
        </div>
        </div>

        <div class="ui right floated buttons">
            <g:link class="ui button" controller="search" action="index">Reset</g:link>
            <button class="ui button black" type="submit" value="yes" name="search">Search</button>
        </div>

        <br>
        <br>
    </g:form>
</div>


<g:if test="${resultsTotal == null}">
    <semui:message>
        <p>Please enter criteria above (* to search all)</p>
    </semui:message>
</g:if>
<g:else>
    <div class="ui header">
        <h2>Showing results ${offset + 1} to ${offset + max > resultsTotal ? resultsTotal : ( offset + max )} of
            ${resultsTotal}</h2>
    </div>

    <div class="ui blue labels">

        <g:each in="${['componentType']}" var="facet">
            <g:each in="${params.list(facet)}" var="fv">
                <div class="ui label">Filter: ${fv == 'TitleInstancePackagePlatform' ? 'Titles' : fv}&nbsp; <g:link
                        controller="${controller}" action="${action}" params="${removeFacet(params, facet, fv)}"><i
                            class="icon close"></i></g:link></div>
            </g:each>
        </g:each>
    </div>

    <div class="ui grid">
        <div class="four wide column">
            <div class="ui segment">
                <g:each in="${facets}" var="facet">
                    <div class="ui header">
                        <g:message code="facet.so.${facet.key}" default="${facet.key}"/>
                    </div>

                    <div class="ui bulleted list">
                        <g:each in="${facet.value.sort { it.display }}" var="v">
                            <div class="item">
                                <g:set var="fname" value="facet:${facet.key + ':' + v.term}"/>

                                <g:if test="${params.list('componentType').contains(v.term.toString())}">
                                    ${v.display} (${v.count})
                                </g:if>
                                <g:else>
                                    <g:link controller="${controller}" action="${action}"
                                            params="${addFacet(params, 'componentType', v.term)}">${v.display}</g:link> (${v.count})
                                </g:else>
                            </div>
                        </g:each>
                    </div>
                </g:each>
            </div></div>

        <div class="twelve wide column">
            <table class="ui selectable striped sortable celled table">
                <thead>
                <tr>
                    <th>#</th>
                    <semui:sortableColumn property="sortname" title="Name" params="${params}"/>
                    <semui:sortableColumn property="componentType" title="Type" params="${params}"/>
                    <semui:sortableColumn property="status" title="Status" params="${params}"/>
                </tr>
                </thead>
                <tbody>
                <g:each in="${hits}" var="hit" status="i">
                    <tr>
                        <td>
                            ${(params.offset? Integer.parseInt(params.offset) : 0)+1+i}
                        </td>
                        <td>
                            <g:if test="${hit.getSourceAsMap().uuid}">
                                <g:link controller="resource" action="show" id="${hit.getSourceAsMap().uuid}">
                                    ${hit.getSourceAsMap().name ?: "- Not Set -"}
                                </g:link>
                            </g:if>
                            <g:else>
                                ${hit.getSourceAsMap().name ?: "- Not Set -"}
                            </g:else>
                        </td>
                        <td>${hit.getSourceAsMap().componentType == 'TitleInstancePackagePlatform' ? hit.getSourceAsMap().titleType : (hit.getSourceAsMap().componentType == 'Org' ? 'Provider' : hit.getSourceAsMap().componentType)}</td>
                        <td>${hit.getSourceAsMap().status?.value ?: 'Unknown'}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <semui:paginate controller="search" action="index" params="${params}" max="${max}"
                            total="${resultsTotal ?: 0}"/>

        </div>
    </div>
</g:else>

<br>

</body>
</html>

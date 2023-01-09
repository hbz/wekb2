{
    "results": [
        <g:each in="${hits}" var="hit" status="counter">
            <g:if test="${counter > 0}">, </g:if>
            {
                "title": "${hit.getSourceAsMap().name}",
                "url":   "${g.createLink(controller:"resource", action:"show", id:"${hit.getSourceAsMap().uuid}")}",
                "category": "${hit.getSourceAsMap().componentType == 'TitleInstancePackagePlatform' ? hit.getSourceAsMap().titleType : hit.getSourceAsMap().componentType}",
                "description" : "${hit.getSourceAsMap().status?.value}",
                "hitsCount": "${hits.size()}",
                "query": "${query}"
            }
        </g:each>
    ]
}
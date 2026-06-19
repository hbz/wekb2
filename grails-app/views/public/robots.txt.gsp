User-agent: *
Disallow: /
<g:if test="${wekb.utils.ServerUtils.getCurrentServer() == wekb.utils.ServerUtils.SERVER_PROD}">
Allow: /$
Allow: /public/aboutWekb
Allow: /public/wcagPlainEnglish
Allow: /public/wekbNews
</g:if>

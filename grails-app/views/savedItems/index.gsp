<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="wekb" />
<title>we:kb : Saved Searchs</title>
</head>
<body>

<h3>Saved Searchs</h3>
<hr/>
<div class="container">
  <div class="row threeColGrid">
    <g:each in="${saved_items}" var="itm">
      <g:set var="savedParams" value="${itm.toParam()}"/>
        <div class="col-md-3 center-block center-text">
          <i class="fa fa-search fa-fw"></i>
          <g:link controller="${savedParams.controller ?: 'search'}" action="${savedParams.action ?: 'index'}" params="${savedParams}">${itm.name}</g:link>
        </div>
    </g:each>
  </div>
</div>
</body>
</html>

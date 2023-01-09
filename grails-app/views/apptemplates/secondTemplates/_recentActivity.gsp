<table class="ui selectable striped sortable celled table">
  <thead>
  <tr>
    <th>#</th>
    <th>Date</th>
    <th>Action</th>
    <th>Title</th>
  </tr>
  </thead>
  <tbody>
  <g:each in="${recentActivitys}" var="h" status="i">
    <tr>
      <td>${i}</td>
      <td>${h[1]}</td>
      <td>${h[2]}</td>
      <td><g:link controller="resource" action="show" id="${h[0].getClassName()+':'+h[0].id}">${h[0].name} (${h[0].id})</g:link></td>
    </tr>
  </g:each>
  </tbody>
</table>


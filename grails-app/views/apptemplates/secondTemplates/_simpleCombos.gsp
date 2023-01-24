<%@ page import="wekb.helper.RCConstants" %>
<g:set var="ctxoid" value="${wekb.KBComponent.deproxy(d).class.name}:${d.id}"/>
<wekb:serviceInjection/>
<!-- pjn created this as I couldn't ever see identifiers anywhere. -->
<table class="ui selectable striped sortable celled table">
  <thead>
    <tr>
      <g:each in="${cols}" var="ch">
        <th>${ch.colhead}</th>
      </g:each>
      <g:if test="${delete=='true'}">
        <th>Actions</th>
      </g:if>
    </tr>
  </thead>
  <tbody>
    <g:each in="${d[property]}" var="row">
     <g:set var="rowoid" value="${wekb.KBComponent.deproxy(row).class.name}:${row.id}"/>
      <tr>
        <g:each in="${cols}" var="c">
          <td>
            <g:if test="${c.action=='link'}">
              <g:link controller="resource" action="show" id="${rowoid}">${groovy.util.Eval.x(row, 'x.' + c.expr)}</g:link>
            </g:if>
            <g:else>${groovy.util.Eval.x(row, 'x.' + c.expr)}</g:else>
          </td>
        </g:each>
        <g:if test="${delete=='true'}">
        <td>
          <g:link controller='ajaxHtml'
                  action='unlinkManyToMany' 
                  params="${[__context:ctxoid,__otherEnd:'parent',__property:property,__itemToRemove:rowoid]}">Delete</g:link>
        </td>
        </g:if>
      </tr>
    </g:each>
  </tbody>
</table>

<g:if test="${targetClass && accessService.checkEditableObject(d, params)}">

  <g:if test="${direction=='in'}">
    <g:set var="recip" value="toComponent"/>
    <g:set var="comboprop" value="fromComponent"/>
  </g:if>
  <g:else>
    <g:set var="recip" value="fromComponent"/>
    <g:set var="comboprop" value="toComponent"/>
  </g:else>

  <g:form controller="ajaxHtml" action="addToCollection">
    <input type="hidden" name="__context" value="${ctxoid}"/>
    <input type="hidden" name="__newObjectClass" value="wekb.Combo"/>
    <input type="hidden" name="__recip" value="${recip}"/>
    <input type="hidden" name="type" value="${wekb.RefdataCategory.getOID(RCConstants.COMBO_TYPE,d.getComboTypeValue(property))}"/>
    Add To List : <semui:simpleReferenceDropdown  name="${comboprop}" baseClass="${targetClass}"/>
    <button type="submit" class="btn btn-default btn-primary btn-sm ">Add</button>
  </g:form>

</g:if>

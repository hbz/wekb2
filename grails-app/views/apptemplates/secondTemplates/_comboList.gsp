<%@ page import="wekb.helper.RCConstants"%>
<g:set var="ctxoid" value="${wekb.KBComponent.deproxy(d).class.name}:${d.id}"/>
<wekb:serviceInjection/>

<table class="ui selectable striped sortable celled table">
  <thead>
    <tr>
      <g:each in="${cols}" var="ch">
        <th>${ch.colhead}</th>
      </g:each>
      <th>Actions</th>
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
        <td>
          <g:if test="${accessService.checkEditableObject(d, params)&& (d.respondsTo('curatoryGroups') ? (!d.respondsTo('getCuratoryGroups') ? true : cur) : true)}">
            <g:link controller='ajaxSupport'
                    action='unlinkManyToMany'
                    params="${[__context:ctxoid,__property:property,__itemToRemove:rowoid, propagate:propagateDelete]}">Delete</g:link>
          </g:if>
        </td>
      </tr>
    </g:each>
  </tbody>
</table>

<g:if test="${targetClass && accessService.checkEditableObject(d, params) && !noadd}">

  <g:if test="${params.controller != 'create'}">
    <g:if test="${direction=='in'}">
      <g:set var="recip" value="toComponent"/>
      <g:set var="comboprop" value="fromComponent"/>
    </g:if>
    <g:else>
      <g:set var="recip" value="fromComponent"/>
      <g:set var="comboprop" value="toComponent"/>
    </g:else>
    <h4>
      Add new Entry
    </h4>
    <dl class="dl-horizontal">
      <g:form controller="ajaxSupport" action="addToCollection">
        <input type="hidden" name="__context" value="${ctxoid}"/>
        <input type="hidden" name="__newObjectClass" value="wekb.Combo"/>
        <input type="hidden" name="__recip" value="${recip}"/>
        <input type="hidden" name="type" value="${wekb.RefdataCategory.getOID(RCConstants.COMBO_TYPE,d.getComboTypeValue(property))}"/>
        <dt class="dt-label">Add To List: </dt>
        <dd>
          <semui:simpleReferenceDropdown class="form-inline select-ml" style="display:inline-block;" name="${comboprop}" baseClass="${targetClass}"/>
          <button type="submit" class="btn btn-default btn-primary">Add</button>
        </dd>
      </g:form>
    </dl>
  </g:if>
  <g:else>
    Please finish the creation process to add items to this list.
  </g:else>
</g:if>

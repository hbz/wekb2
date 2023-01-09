<%@ page import="de.wekb.helper.RCConstants" %>
<dl>
  <dt class="control-label"> Value </dt>
  <dd> <semui:xEditable  owner="${d}" field="value" /> </dd>

</dl>
<dl>
  <dt class="control-label"> Name </dt>
  <dd> <semui:xEditable  owner="${d}" field="name" /> </dd>

</dl>
<dl>
  <dt class="control-label"> Category </dt>
  <dd> <semui:xEditable  owner="${d}" field="family" /> </dd>

</dl>
<dl>
  <dt class="control-label"> Pattern </dt>
  <dd> <semui:xEditable  owner="${d}" field="pattern" /> </dd>

</dl>
<dl>
  <dt class="control-label"> Target Type </dt>
  <dd> <semui:xEditableRefData owner="${d}" field="targetType" config="${RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE}" /> </dd>
</dl>

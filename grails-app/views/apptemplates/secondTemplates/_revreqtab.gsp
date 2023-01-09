<%@ page import="de.wekb.helper.RCConstants" %>
<div>
    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>Cause</th>
            <th>Request</th>
            <th>Status</th>
            <th>Date Created</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${d.reviewRequests}" var="rr">
            <tr>
                <td>
                    <g:link controller="resource" action="show"
                            id="org.gokb.cred.ReviewRequest:${rr.id}">${rr.descriptionOfCause}</g:link>
                </td>
                <td>
                    <g:link controller="resource" action="show"
                            id="org.gokb.cred.ReviewRequest:${rr.id}">${rr.reviewRequest}</g:link>
                </td>
                <td>
                    <semui:xEditableRefData owner="${rr}" field="status" config="${RCConstants.REVIEW_REQUEST_STATUS}"/>
                </td>
                <td>
                    ${rr.dateCreated}
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
%{--<g:if test="${editable}">
    <div>
        <button
                class="hidden-license-details btn btn-default btn-primary "
                data-toggle="collapse" data-target="#collapseableAddReview">
            Add new <i class="fas fa-plus"></i></button>
        <dl id="collapseableAddReview" class="dl-horizontal collapse">
            <g:form controller="workflow" action="newRRLink">
                <input type="hidden" name="id" value="${d.id}"/>
                <dt class="dt-label">Aspect to review</dt>
                <dd>
                    <input  type="text" name="request" required/>
                </dd>
                <dt class="dt-label"></dt>
                <dd>
                    <button type="submit" class="btn btn-default btn-primary">Add</button>
                </dd>
            </g:form>
        </dl>
    </div>
</g:if>--}%
<div style="clear:both"></div>

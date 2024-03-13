<%@ page import="wekb.helper.RCConstants;" %>
<g:if test="${d.publicationType?.value == 'Serial'}">
	<semui:tabsItemContent tab="tippcoverage" activeTab="${params.activeTab}">
		<div class="content wekb-inline-lists">
			<dl>
				<dt class="control-label">
					Coverage
				</dt>
				<dd>
					<table class="ui selectable striped sortable celled table">
						<thead>
						<tr>
							<th>Start Date</th>
							<th>Start Volume</th>
							<th>Start Issue</th>
							<th>End Date</th>
							<th>End Volume</th>
							<th>End Issue</th>
							<th>Embargo</th>
							<th>Note</th>
							<th>Depth</th>
							<g:if test="${editable}">
								<th>Actions</th>
							</g:if>
						</tr>
						</thead>
						<tbody>
						<g:if test="${d.coverageStatements?.size() > 0}">
							<g:each var="cs" in="${d.coverageStatements.sort { it.startDate }}">
								<tr>
									<td><semui:xEditable owner="${cs}" type="date"
														 field="startDate"/></td>
									<td><semui:xEditable owner="${cs}"
														 field="startVolume"/></td>
									<td><semui:xEditable owner="${cs}"
														 field="startIssue"/></td>
									<td><semui:xEditable owner="${cs}" type="date"
														 field="endDate"/></td>
									<td><semui:xEditable owner="${cs}" field="endVolume"/></td>
									<td><semui:xEditable owner="${cs}" field="endIssue"/></td>
									<td><semui:xEditable owner="${cs}" field="embargo"/></td>
									<td><semui:xEditable owner="${cs}" field="coverageNote"/></td>
									<td><semui:xEditableRefData owner="${cs}" field="coverageDepth"
																config="${RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH}"/>
									</td>
									<g:if test="${editable}">
										<td>
											<g:link controller="ajaxHtml"
													action="deleteCoverageStatement"
													params="[id: cs.id, activeTab: 'tippcoverage', curationOverride: params.curationOverride]">Delete</g:link>
										</td>
									</g:if>
								</tr>
							</g:each>
						</g:if>
						<g:else>
							<tr><td colspan="8"
									style="text-align:center">${message(code: 'tipp.coverage.empty', default: 'No coverage defined')}</td>
							</tr>
						</g:else>
						</tbody>
					</table>
					<g:if test="${editable}">
						<dl>
							<a class="ui right floated primary button" href="#"
							   onclick="$('#coverageStatementsModal').modal('show');">Add Coverage Statement</a>

							<br>
							<br>

						</dl>
					</g:if>
				</dd>
			</dl>
		</div>

	<g:if test="${editable}">
	<semui:modal id="coverageStatementsModal" title="Add Coverage Statement">

		<g:form controller="ajaxHtml" action="addToCollection" params="[activeTab: 'tippcoverage']" class="ui form">
			<input type="hidden" name="__context"
				   value="${d.getOID()}"/>
			<input type="hidden" name="__newObjectClass"
				   value="wekb.TIPPCoverageStatement"/>
			<input type="hidden" name="__recip" value="owner"/>
			<div class="field">
                              <label>Start Date</label>

				<input  type="date" name="startDate"/>
			</div>
			<div class="field">
                              <label>Start Volume</label>

				<input  type="text" name="startVolume"/>
			</div>
			<div class="field">
                              <label>Start Issue</label>

				<input  type="text" name="startIssue"/>
			</div>
			<div class="field">
                              <label>End Date</label>

				<input  type="date" name="endDate"/>
			</div>
			<div class="field">
                              <label>End Volume</label>

				<input  type="text" name="endVolume"/>
			</div>
			<div class="field">
                              <label>End Issue</label>

				<input  type="text" name="endIssue"/>
			</div>
			<div class="field">
                              <label>Embargo</label>

				<input  type="text" name="embargo"/>
			</div>
			<div class="field">
                              <label>Coverage Depth</label>

				<semui:simpleReferenceDropdown name="coverageDepth"
											  baseClass="wekb.RefdataValue"
											  filter1="${RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH}"/>
			</div>
			<div class="field">
                              <label>Coverage Note</label>

				<input  type="text" name="coverageNote"/>
			</div>
		</g:form>
	</semui:modal>
		</g:if>

	</semui:tabsItemContent>
</g:if>
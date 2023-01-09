<div class="panel panel-default">

  <!-- Default panel contents -->
  <div class="panel-heading modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3 class="modal-title">Notes for ${name}</h3>
  </div>

  <div class="panel-body modal-body">

    <h4>New note</h4>

    <g:form name="newNoteForm" controller="ajaxSupport" action="addToCollection"   class="form">
      <input type="hidden" name="__context" value="${ownerClass}:${ownerId}"/>
      <input type="hidden" name="__newObjectClass" value="org.gokb.cred.Note"/>
      <input type="hidden" name="ownerClass" value="${ownerClass}"/>
      <input type="hidden" name="ownerId" value="${ownerId}"/>
      <input type="hidden" name="creator" value="org.gokb.cred.User:${user.id}"/>
      <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>
      <div class="field">
        <textarea class="form-control text-complete" style="resize:none;" rows="5" name="note"></textarea>
      </div>
      <br>
      <div class="field">
        <span class="btn btn-primary btn-sm" onClick="document.forms['newNoteForm'].submit()">Add</span>
      </div>
    </g:form>
    <br/>

    <table class="ui selectable striped sortable celled table">
      <thead>
        <tr>
          <th>Date</th>
          <th>Note</th>
        </tr>
      </thead>
      <tbody>
        <g:each in="${noteLines}" var="n">
          <tr>
            <td style="white-space:nowrap;">${n.dateCreated}</td>
            <td width="100%">${n.note}</td>
          </tr>
        </g:each>
      </tbody>
    </table>

  </div>
</div>


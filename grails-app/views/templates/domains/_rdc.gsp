<dl>
    <dt class="control-label">
        Internal Id
    </dt>
    <dd>
        ${d.id ?: 'New record'}
</dl>
<dl>
    <dt class="control-label">
        Category Name / Description
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="desc"/>
</dl>
<dl>
  <dt class="control-label">
    Category Name / Description EN
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="desc_en"/>
</dl>

<dl>
  <dt class="control-label">
    Category Name / Description DE
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="desc_de"/>
</dl>

<dl>
  <dt class="control-label">
    Hard Data
  </dt>
  <dd>
    <semui:xEditableBoolean owner="${d}" field="isHardData" overwriteEditable="false"/>
</dl>
<dl>
    <dt class="control-label">
        Label
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="label"/>
</dl>

<g:if test="${d.id != null}">

    <h3 class="ui header">Refdata Values
    </h3>

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>Value</th>
          <th>Value EN</th>
          <th>Value DE</th>
          <th>Description</th>
            %{--<td>Deprecate (Use)</td>--}%
            %{--<th>Sort Key</th>--}%
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${d.values}" var="v">
            <tr>
                <td>
                    <semui:xEditable owner="${v}" field="value"/>
                </td>
              <td>
                <semui:xEditable owner="${v}" field="value_en"/>
              </td>
              <td>
                <semui:xEditable owner="${v}" field="value_de"/>
              </td>
              <td>
                <semui:xEditable owner="${v}" field="description"/>
              </td>
                %{--<td><semui:xEditableManyToOne owner="${v}"
                                              field="useInstead" baseClass="wekb.RefdataValue"
                                              filter1="${d.desc}">
                    ${v.useInstead?.value}
                </semui:xEditableManyToOne></td>--}%
                %{--<td><semui:xEditable owner="${v}" field="sortKey"/></td>--}%
                <td></td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <sec:ifAnyGranted roles="ROLE_ADMIN">
        <a class="ui right floated black button" href="#" onclick="$('#rdvModal').modal('show');">Add Refdata Value</a>

        <br>
        <br>

        <semui:modal id="rdvModal" title="Add Refdata Value">
            <g:form controller="ajaxSupport" action="addToCollection" class="ui form">
                <input type="hidden" name="__context"
                       value="${d.className}:${d.id}"/>
                <input type="hidden" name="__newObjectClass"
                       value="wekb.RefdataValue"/>
                <input type="hidden" name="__recip" value="owner"/>

                <div class="field">
                    <label>Refdata Value</label>

                    <input type="text" name="value"/>
                </div>

              <div class="field">
                <label>Refdata Value EN</label>

                <input type="text" name="value_en"/>
              </div>

              <div class="field">
                <label>Refdata Value DE</label>

                <input type="text" name="value_de"/>
              </div>

              <div class="field">
                <label>Description</label>

                <input type="text" name="description"/>
              </div>

                %{--<div class="field">
                    <label>Sort Key</label>
                    <input type="text" name="sortKey"/>
                </div>--}%
            </g:form>
        </semui:modal>
    </sec:ifAnyGranted>
    </dl>
</g:if>
<g:else>
</g:else>

<div class="ui segment">
    <div class="content wekb-inline-lists">
        <sec:ifAnyGranted roles="ROLE_ADMIN">
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
        </sec:ifAnyGranted>
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
        <sec:ifAnyGranted roles="ROLE_ADMIN">
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
        </sec:ifAnyGranted>

        <g:if test="${d.id != null}">

            <h3 class="ui header">Reference Data Values
            </h3>

            <table class="ui selectable striped sortable celled table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Value</th>
                    <th>Value EN</th>
                    <th>Value DE</th>
                    <th>Description</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${d.values}" var="v" status="i">
                    <tr>
                        <td>${i + 1}</td>
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
                        <td></td>
                    </tr>
                </g:each>
                </tbody>
            </table>
            <sec:ifAnyGranted roles="ROLE_ADMIN">
                <a class="ui right floated primary button" href="#"
                   onclick="$('#rdvModal').modal('show');">Add Refdata Value</a>

                <br>
                <br>

                <semui:modal id="rdvModal" title="Add Refdata Value">
                    <g:form controller="ajaxHtml" action="addToCollection" class="ui form">
                        <input type="hidden" name="__context"
                               value="${d.getOID()}"/>
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
                    </g:form>
                </semui:modal>
            </sec:ifAnyGranted>
        </g:if>
    </div>
</div>
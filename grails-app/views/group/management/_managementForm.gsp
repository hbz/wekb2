<div class="ui segment">
    <h1 class="ui header">Bulk Process</h1>

<div class="two fields">
    <g:each in="${batchForm}" var="formField" status="frmidx">

        <div class="field">
            <label for="${formField.bParam}">${formField.prompt}</label>
            <g:if test="${formField.type == 'lookup'}">
                <div class="ui field">
                    <semui:simpleReferenceDropdown
                            id="refdata_${params.inline ? 'inline_' : ''}${formField.bParam}"
                            name="${formField.bParam}"
                            baseClass="${formField.baseClass}"
                            filter1="${formField.filter1 ?: ''}"
                            value="${params[formField.bParam]}"/>
                </div>
            </g:if>
            <g:else>
                <div class="ui labeled input">
                    <input type="${formField.type == 'java.lang.Long' ? 'number' : 'text'}"
                           name="${formField.bParam}" id="${formField.bParam}"
                           placeholder="${formField.placeholder}"
                           value="${params[formField.bParam]}"/>
                </div>
            </g:else>

        </div>

        <g:if test="${((frmidx + 1) % 2) == 0}">
            </div>
            <div class="two fields">
        </g:if>
    </g:each>
</div>

    <button class="ui button primary" type="submit" value="changeProperties"
                name="processOption">Start Bulk Process</button>

    <br>
    <br>
</div>
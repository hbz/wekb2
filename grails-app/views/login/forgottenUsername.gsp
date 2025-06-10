<html>
<head>
    <meta name="layout" content="wekb"/>
    <title>we:kb : <g:message
            code='forgottenUsername.title'/></title>
</head>

<body>

<semui:flashMessage data="${flash}"/>

<g:if test="${emailSent == true}">
    <semui:message>
        <g:message code='forgottenUsername.sent'/>
    </semui:message>
</g:if>


<div class="ui middle aligned center aligned grid">
    <div class="six wide column">
        <h2 class="ui header">
            <div class="content">
                <g:message code='forgottenUsername.title'/>
            </div>
        </h2>

        <g:if test="${!emailSent}">
            <g:form action='getForgottenUsername' name="forgotUsernameForm" autocomplete='off' class="ui form">

                <div class="ui stacked segment">
                    <div class="field">
                        <div class="ui left icon input">
                            <i class="user icon"></i>
                            <g:textField id="email" placeholder="Requested Email" name="fgp_email" size="25"/>
                        </div>
                    </div>

                    <button class="ui fluid submit primary button" type="submit">Send Username Recovery Email...</button>
                </div>

            </g:form>
        </g:if>

        <div class="ui message">
            <a href="#" onclick="$('#infoModal').modal('show');">Not yet registered for a we:kb: account?</a>
        </div>
    </div>
</div>

<semui:modal id="infoModal" title="Info" hideSubmitButton="true">
    Contact us at <a
        href="mailto:laser@hbz-nrw.de">laser@hbz-nrw.de</a> so that we can set up an account for you and provide you with your initial login information.
</semui:modal>

</body>
</html>

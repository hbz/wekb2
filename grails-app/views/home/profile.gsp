<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>My Profile & Preferences</title>
</head>

<body>
<semui:flashMessage data="${flash}"/>

<h1 class="ui header">My Profile & Preferences</h1>

<div class="ui two column grid">

    <div class="column wide eight">
        <semui:card text="My Details" class="fluid">
            <div class="content wekb-inline-lists">
                <g:render template="/templates/domains/user" model="${[d: user]}"></g:render>
            </div>
        </semui:card>

        <semui:card text="My Preferences" class="fluid">
            <div class="content wekb-inline-lists">
                %{-- <dl>
                     <dt class="control-label">Show Info Icon :</dt>
                     <dd>
                         <semui:xEditableRefData owner="${user}" field="showInfoIcon"
                                                 config="${RCConstants.YN}"/>
                     </dd>
                 </dl>
                 <dl>
                     <dt class="control-label">Show Quick View :</dt>
                     <dd>
                         <semui:xEditableRefData owner="${user}" field="showQuickView" config="${RCConstants.YN}"/>
                     </dd>
                 </dl>--}%
                <dl>
                    <dt class="control-label">Default Page Size :</dt>
                    <dd><semui:xEditable owner="${user}" field="defaultPageSize"/></dd>
                </dl>
                %{-- <dl>
                     <dt class="control-label">Send Alert Emails :</dt>
                     <dd><semui:xEditableRefData owner="${user}" field="send_alert_emails"
                                                 config="${RCConstants.YN}"/></dd>
                 </dl>--}%
            </div>
        </semui:card>

    </div>

    <div class="column wide eight">

        <semui:card text="Change Password" class="fluid">
            <div class="content wekb-inline-lists">
                <g:form action="changePass" class="ui form">
                    <dl>
                        <dt class="dt-label">Original Password :</dt>
                        <dd><input  name="origpass" type="password"/></dd>
                    </dl>
                    <dl>
                        <dt class="dt-label">New Password :</dt>
                        <dd><input  name="newpass" type="password"/></dd>
                    </dl>
                    <dl>
                        <dt class="dt-label">Repeat New Password :</dt>
                        <dd><input  name="repeatpass" type="password"/></dd>
                    </dl>

                    <button type="submit" class="ui black button">Change Password</button>
                </g:form>
            </div>
        </semui:card>


    </div>

</div>

</body>
</html>

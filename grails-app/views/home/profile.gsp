<%@ page import="wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>we:kb : My Profile & Preferences</title>
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
                <dl>
                    <dt class="control-label">Default Page Size :</dt>
                    <dd><semui:xEditable owner="${user}" field="defaultPageSize"/></dd>
                </dl>
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

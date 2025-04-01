<%@ page import="wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="wekb"/>
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



    </div>

    <div class="column wide eight">

        <semui:card text="Change Password" class="fluid">
            <div class="content wekb-inline-lists">
                <g:form action="changePass" class="ui js-changePassword form">
                    <div class="field required">
                        <label for="origpass">Original Password (mandatory field):</label>
                        <input aria-labelledby="origpass" type="password" name="origpass" id="origpass" class="pw"/>
                    </div>
                    <div class="field required">
                        <label for="newpass">New Password (mandatory field):</label>
                        <input aria-labelledby="newpass" type="password" name="newpass" id="newpass" class="pw pwn"/>
                    </div>
                    <div class="field required">
                        <label for="repeatpass">Repeat New Password (mandatory field):</label>
                        <input aria-labelledby="newpass" type="password" name="repeatpass" id="repeatpass" class="pw pwn"/>
                    </div>
                    <div class="field">
                        <label for="password_show_toggler">Show Passwords</label>
                        <input type="checkbox" name="showPasswords" id="password_show_toggler">
                    </div>

                    <button type="submit" class="ui primary button" id="password_submit">Change Password</button>
                </g:form>
            </div>
        </semui:card>


        <semui:card text="My Preferences" class="fluid">
            <div class="content wekb-inline-lists">
                <dl>
                    <dt class="control-label">Default Page Size :</dt>
                    <dd><semui:xEditableDropDown owner="${user}" field="defaultPageSize" dataLink="getProfilPageSizeList"/></dd>
                </dl>
            </div>
        </semui:card>


    </div>

</div>

<script>
    $('.js-changePassword .form').form({
        on: 'change',
        inline: true,
        fields: {
            origpass: {
                identifier  : 'origpass',
                rules: [
                    {
                        type   : 'empty',
                        prompt : '{name} needs to be filled out'
                    }
                ]
            },
            newpass: {
                identifier  : 'newpass',
                rules: [
                    {
                        type   : 'empty',
                        prompt : '{name} needs to be filled out'
                    }
                ]
            },
            repeatpass: {
                identifier  : 'repeatpass',
                rules: [
                    {
                        type   : 'empty',
                        prompt : '{name} needs to be filled out'
                    },
                    {
                        type: 'match[newpass]',
                        prompt : '{name} must match'
                    }
                ]
            }
        }
    });

    $('#password_show_toggler').on('change', function(e) {
        $('input.pw').attr('type', ($(this).is(":checked") ? 'text' : 'password'))
    })

    $('#password_submit').on('click', function(e) {
        e.preventDefault()
        var pw1 = $('input[name=newpass]')
        var pw2 = $('input[name=repeatpass]')

        $('input.pwn').parents('div.field').removeClass('error')

        if ( pw1.val() && (pw1.val() == pw2.val()) ) {
            $(this).parents('form').submit()
        } else {
            $('input.pwn').parents('div.field').addClass('error')
        }
    })
</script>

</body>
</html>

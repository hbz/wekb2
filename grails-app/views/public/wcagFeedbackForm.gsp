<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Accessibility Feedback Form</title>
</head>

<body>

<h1 class="ui header">Accessibility Feedback Form</h1>

<div class="ui segment">

    <g:form action="sendFeedbackForm" controller="public" method="get" class="ui small form">
        <div class="field">
            <label for="name">Name</label>

            <div>
                <input type="text" id="name" name="name" placeholder="Name" value=""/>
            </div>
        </div>

        <div class="field">
            <label for="eMail">E-Mail</label>

            <div>
                <input type="text" name="eMail" id="eMail" placeholder="E-Mail" value=""/>
            </div>
        </div>

        <div class="field">
            <label for="url">URL of the page you are commenting on</label>

            <div>
                <input type="text" name="url" id="url" placeholder="url" value=""/>
            </div>
        </div>

        <div class="field">
            <label for="comment">Comment</label>

            <div>
                <g:textArea name="comment" id="comment" rows="5" cols="40"/>
            </div>
        </div>

        <div class="ui right floated buttons">
            <a href="${request.forwardURI}" class="ui button">Reset</a>
            <input type="submit" class="ui black button " value="Send">
        </div>

        <br>
        <br>
    </g:form>

</div>
</body>
</html>

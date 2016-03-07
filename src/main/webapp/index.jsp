<%@ page import="info.xonix.passmanager.Env" %>
<%@ page import="info.xonix.passmanager.Logic" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    boolean offline = request.getParameter("offline") != null;
    if (offline) {
        response.addHeader("Content-Disposition", "attachment; filename=Passwords_" +
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".html");
    }
%>
<!DOCTYPE html>
<html ng-app="pass-manager">
<title>Personal password manager</title>

<c:set var="minSuffix" value="<%= Env.getMinSuffix() %>"/>
<c:set var="jsCss">
    lib/jquery/jquery-2.2.1${minSuffix}.js
    lib/bootstrap/js/bootstrap${minSuffix}.js
    lib/angular-1.4.9/angular${minSuffix}.js
    lib/angular-1.4.9/angular-route${minSuffix}.js
    lib/angular-1.4.9/angular-resource${minSuffix}.js
    lib/ng-tags-input/ng-tags-input${minSuffix}.js
    lib/sjcl/sjcl.js
    lib/clipboardjs/clipboard${minSuffix}.js
    lib/bootstrap/css/bootstrap${minSuffix}.css
    lib/bootstrap/css/bootstrap-theme${minSuffix}.css
    lib/ng-tags-input/ng-tags-input${minSuffix}.css
    app.js
    app.css
</c:set>

<head>
    <script>
        var global=<%= Logic.renderGlobals(offline) %>;
    </script>
    <%= Logic.renderNgTemplates(pageContext, offline) %>
    <%= Logic.renderJsCss(pageContext, "jsCss", offline) %>
</head>

<body ng-controller="RootCtrl">
    <div class="alert alert-success" ng-if="flashMsg">{{flashMsg}}</div>
    <div class="alert alert-danger" ng-if="flashErr">
        <button type="button" class="close" title="Close" ng-click="$parent.flashErr=null">&times;</button>
        <b>{{flashErr}}</b>
    </div>
    <ng-view></ng-view>
</body>
</html>
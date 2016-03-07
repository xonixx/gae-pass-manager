<%@ page import="info.xonix.passmanager.Env" %>
<%@ page import="info.xonix.passmanager.Logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
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
    <%= Logic.renderJsCss((String) pageContext.getAttribute("jsCss")) %>
    <script>
        var global=<%= Logic.renderGlobals() %>;
    </script>
</head>

<body ng-app="pass-manager" ng-controller="RootCtrl">
    <div class="alert alert-success" ng-if="flashMsg">{{flashMsg}}</div>
    <div class="alert alert-danger" ng-if="flashErr">
        <button type="button" class="close" title="Close" ng-click="$parent.flashErr=null">&times;</button>
        <b>{{flashErr}}</b>
    </div>
    <ng-view></ng-view>
</body>
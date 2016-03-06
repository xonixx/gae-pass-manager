<%@ page import="info.xonix.passmanager.Env" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<title>Personal password manager</title>

<c:set var="minSuffix" value="<%= Env.getMinSuffix() %>"/>

<head>
    <script src="lib/jquery/jquery-2.2.1${minSuffix}.js"></script>
    <script src="lib/bootstrap/js/bootstrap${minSuffix}.js"></script>
    <script src="lib/angular-1.4.9/angular${minSuffix}.js"></script>
    <script src="lib/angular-1.4.9/angular-route${minSuffix}.js"></script>
    <script src="lib/angular-1.4.9/angular-resource${minSuffix}.js"></script>
    <script src="lib/ng-tags-input/ng-tags-input${minSuffix}.js"></script>
    <script src="lib/sjcl/sjcl.js"></script>
    <script src="lib/clipboardjs/clipboard${minSuffix}.js"></script>
    <link rel="stylesheet" href="lib/bootstrap/css/bootstrap${minSuffix}.css"/>
    <link rel="stylesheet" href="lib/bootstrap/css/bootstrap-theme${minSuffix}.css"/>
    <link rel="stylesheet" href="lib/ng-tags-input/ng-tags-input${minSuffix}.css"/>
    <script src="app.js"></script>
    <link rel="stylesheet" href="app.css"/>
</head>

<body ng-app="pass-manager" ng-controller="RootCtrl">
    <div class="alert alert-success" ng-if="flashMsg">{{flashMsg}}</div>
    <ng-view></ng-view>
</body>
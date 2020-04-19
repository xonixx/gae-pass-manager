<%@ page import="info.xonix.passmanager.Env" %>
<%@ page import="info.xonix.passmanager.JspLogic" %>
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
    lib/jquery/jquery-2.2.4${minSuffix}.js
    lib/bootstrap/js/bootstrap${minSuffix}.js
    lib/angular/angular${minSuffix}.js
    lib/angular/angular-route${minSuffix}.js
    lib/angular/angular-resource${minSuffix}.js
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
        var global =<%= JspLogic.getInstance().renderGlobals(offline) %>;
    </script>
    <%= JspLogic.getInstance().renderNgTemplates(pageContext, offline) %>
    <%= JspLogic.getInstance().renderJsCss(pageContext, "jsCss", offline) %>
</head>

<body ng-controller="RootCtrl">
<div class="alert alert-success" ng-if="flashMsg">{{flashMsg}}</div>
<div class="alert alert-danger" ng-if="flashErr">
    <button type="button" class="close" title="Close" ng-click="$parent.flashErr=null">&times;</button>
    <b>{{flashErr}}</b>
</div>
<ng-view></ng-view>
<div class="modal fade" id="confirmDeleteModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" ng-if="deleteObj">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">Confirm deletion</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-2" style="font-size: 4em; color: #f40">
                        <span class="glyphicon glyphicon-exclamation-sign"></span></div>
                    <div class="col-xs-10">
                        <p style="font-size: 1.8em;" ng-bind-html="deleteObj.text | to_trusted"></p>
                        <p style="font-size: 2em; color: #f40">
                            {{deleteConfirm2 ? 'Are you REALLY sure?' : '&nbsp;'}}
                        </p>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-danger" style="float: left"
                        ng-if="deleteConfirm2"
                        ng-click="deleteObj.doConfirmDelete()">
                    Yes, delete it!
                </button>
                <button type="button" class="btn btn-primary"
                        ng-if="!deleteConfirm2"
                        ng-click="$scope.deleteConfirm2=1">Delete!
                </button>
                <button type="button" class="btn btn-default"
                        ng-click="deleteObj.cancel()">Cancel
                </button>
            </div>
        </div>
    </div>
</div>
</body>

</html>
<%@ page import="info.xonix.passmanager.Logic" %>
<div class="container-fluid">
    <h3>Passwords
        <button type="button" ng-click="addNew()"
                class="btn btn-info btn-sm"
                style="margin-left: 150px;margin-top: -10px">Add New
        </button>
    </h3>
    <div id="info-block">
        <div id="current-user">
            <%= Logic.getCurrentUser().getEmail() %>
            <a href="#/logout">(Logout)</a>
        </div>
        <div id="pass-count">Total passwords: {{passwords.length}}. Last updated: {{lastUpdated | date1}}</div>
    </div>
    <div class="row">
        <div class="col-xs-12">
            <input type="text" id="search-box" ng-model="searchStr" placeholder="Filter..."/>
        </div>
    </div>
    <div ng-repeat="p in (passwords | filter:searchStr)" class="pass-block row">
        <div class="col-xs-10">
            <div style="margin-bottom: 5px">
                <a href="{{p.url}}">{{p.url}}</a> - <span class="descr">{{p.descr}}</span>
            </div>
            <div class="row">
                <div class="col-xs-6"><span ng-repeat="t in p.tags" class="tag">{{t}}</span></div>
                <div class="col-xs-6">
                    <button type="button" class="btn btn-success btn-xs" ng-click="edit(p)">Edit</button>
                    <button type="button" class="btn btn-warning btn-xs" ng-click="delete(p)">Delete</button>
                </div>
            </div>
        </div>
        <div class="col-xs-2">
            <%--TODO ipad?--%>
            <%--<input readonly type="password" pass="" ng-model="p.pass" style="width: 100%; margin-top: 3px">--%>
            <button ng-disabled="!p.pass" type="button" class="btn btn-xs btn-primary"
                    copy-to-clipboard=""
                    data-clipboard-text="{{p.pass}}">Copy password</button>
            <span></span>
            <button ng-disabled="!p.secureData" type="button" class="btn btn-xs btn-primary"
                    copy-to-clipboard=""
                    data-clipboard-text="{{p.secureData}}">Copy secure data</button>
            <div ng-if="p.created">Created: {{p.created | date1}}</div>
            <div ng-if="p.updated">Updated: {{p.updated | date1}}</div>
        </div>
    </div>
</div>

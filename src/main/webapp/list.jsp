<div class="container-fluid">
    <h3>Passwords</h3>
    <div class="row">
        <div class="col-xs-12">
            <input type="text" id="search-box" ng-model="searchStr" placeholder="Filter..."/>
        </div>
    </div>
    <div ng-repeat="p in passwords" class="pass-block row">
        <div class="col-xs-9">
            <div style="margin-bottom: 5px">
                <a href="{{p.url}}">{{p.url}}</a> - <span class="descr">{{p.descr}}</span>
            </div>
            <span ng-repeat="t in p.tags" class="tag">{{t}}</span>
        </div>
        <div class="col-xs-3">
            <input type="password" ng-model="p.pass">
        </div>
    </div>
</div>

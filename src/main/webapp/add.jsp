<div class="container">
    <h3 class="text-center">{{isEdit ? 'Edit' : 'Add'}}</h3>
    <form name="passForm" class="form-horizontal" novalidate autocomplete="off">
        <div style="height: 0;width: 0;overflow: hidden"><%-- hack against chrome autocomplete --%>
            <input type="text" name="fakeusername"/>
            <input type="password" name="fakepassword"/>
        </div>
        <div class="form-group">
            <label for="inputUrl" class="col-sm-2 control-label">URL</label>
            <div class="col-sm-10">
                <input ng-model="password.url" type="text" class="form-control" id="inputUrl" placeholder="URL">
            </div>
        </div>
        <div class="form-group">
            <label for="inputLogin" class="col-sm-2 control-label">Login</label>
            <div class="col-sm-10">
                <input ng-model="password.login" type="text" class="form-control" id="inputLogin" placeholder="Login">
            </div>
        </div>
        <div class="form-group">
            <label for="inputDescr" class="col-sm-2 control-label">Description</label>
            <div class="col-sm-10">
                <textarea ng-model="password.descr" class="form-control" id="inputDescr"
                          placeholder="Description"></textarea>
            </div>
        </div>
        <div class="form-group">
            <label for="inputPass" class="col-sm-2 control-label">Password</label>
            <div class="col-sm-10">
                <input ng-model="password.pass" type="password" pass="" class="form-control" id="inputPass"
                       placeholder="Password">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTags" class="col-sm-2 control-label">Tags</label>
            <div class="col-sm-10">
                <input ng-model="password.tags" type="text" class="form-control" id="inputTags" placeholder="Tags">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="button" class="btn btn-success" ng-click="save(password)">Save</button>
                <button type="button" class="btn btn-default" ng-click="cancel()">Cancel</button>
            </div>
        </div>
    </form>
</div>

<div class="container">
    <h3 class="text-center">{{isEdit ? 'Edit' : 'Add'}}</h3>
    <form name="passForm" class="form-horizontal" novalidate autocomplete="off">
        <div style="height: 0;width: 0;overflow: hidden"><%-- hack against chrome autocomplete --%>
            <input type="text" name="fakeusername"/>
            <input type="password" name="fakepassword"/>
        </div>
        <div class="form-group">
            <label for="inputPass" class="col-sm-2 control-label">Password</label>
            <div class="col-sm-10">
                <input ng-model="password.pass" type="password" pass="" class="form-control" id="inputPass"
                       placeholder="Password">
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

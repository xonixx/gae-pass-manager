<div class="container">
    <h3 class="text-center">Change master password</h3>
    <form name="passForm" class="form-horizontal" novalidate autocomplete="off">
        <div style="height: 0;width: 0;overflow: hidden"><%-- hack against chrome autocomplete --%>
            <input type="text" name="fakeusername"/>
            <input type="password" name="fakepassword"/>
        </div>
        <div class="alert alert-warning" ng-if="error">
            {{error}}
        </div>
        <div class="alert alert-danger" ng-if="errorDanger">
            {{errorDanger}}
        </div>
        <div class="form-group">
            <label for="inputPassOld" class="col-sm-2 control-label">Old Password<sup>*</sup></label>
            <div class="col-sm-10">
                <input ng-model="passOld" required type="password" class="form-control" id="inputPassOld"
                       placeholder="Old Password">
            </div>
        </div>
        <hr/>
        <div class="form-group">
            <label for="inputPass" class="col-sm-2 control-label">New Password<sup>*</sup></label>
            <div class="col-sm-10">
                <input ng-model="pass" required type="password" class="form-control" id="inputPass"
                       placeholder="New Password">
            </div>
        </div>
        <div class="form-group">
            <label for="inputPassConfirm" class="col-sm-2 control-label">Confirm New Password<sup>*</sup></label>
            <div class="col-sm-10">
                <input ng-model="passConfirm" required type="password" class="form-control" id="inputPassConfirm"
                       placeholder="Confirm New Password">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="button" class="btn btn-warning"
                        ng-disabled="!passOld || !pass || !passConfirm"
                        ng-click="doChangeMaster(passOld, pass, passConfirm)">Do Change!</button>
                <button type="button" class="btn btn-default" ng-click="cancel()">Cancel</button>
            </div>
        </div>
    </form>
</div>

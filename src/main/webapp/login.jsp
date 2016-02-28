<div class="container">
    <h3 class="text-center">{{isNew ? 'Create master password' : 'Login'}}</h3>
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
            <label for="inputPass" class="col-sm-2 control-label">Password</label>
            <div class="col-sm-10">
                <input ng-model="pass" type="password" class="form-control" id="inputPass"
                       placeholder="Password">
            </div>
        </div>
        <div class="form-group" ng-show="isNew">
            <label for="inputPassConfirm" class="col-sm-2 control-label">Confirm Password</label>
            <div class="col-sm-10">
                <input ng-model="passConfirm" type="password" class="form-control" id="inputPassConfirm"
                       placeholder="Confirm Password">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button ng-if="!isNew" type="button" class="btn btn-success" ng-click="login(pass)">Login</button>
                <button ng-if="isNew" type="button" class="btn btn-success" ng-click="register(pass,passConfirm)">
                    Register
                </button>
            </div>
        </div>
    </form>
</div>

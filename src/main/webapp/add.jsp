<div class="container">
    <h3 class="text-center">{{isEdit ? 'Edit' : 'Add'}}</h3>
    <form class="form-horizontal">
        <div class="form-group">
            <label for="inputUrl" class="col-sm-2 control-label">URL</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="inputUrl" placeholder="URL">
            </div>
        </div>
        <div class="form-group">
            <label for="inputEmail" class="col-sm-2 control-label">Email</label>
            <div class="col-sm-10">
                <input type="email" class="form-control" id="inputEmail" placeholder="Email">
            </div>
        </div>
        <div class="form-group">
            <label for="inputDescr" class="col-sm-2 control-label">Description</label>
            <div class="col-sm-10">
                <textarea class="form-control" id="inputDescr" placeholder="Description"></textarea>
            </div>
        </div>
        <div class="form-group">
            <label for="inputPass" class="col-sm-2 control-label">Password</label>
            <div class="col-sm-10">
                <input type="password" pass="" class="form-control" id="inputPass" placeholder="Password">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTags" class="col-sm-2 control-label">Tags</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="inputTags" placeholder="Tags">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" ng-click="cancel()">Cancel</button>
            </div>
        </div>
    </form>
</div>

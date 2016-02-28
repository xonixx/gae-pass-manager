angular.module('pass-manager', ['ngRoute', 'ngResource', 'ngTagsInput'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/login', {templateUrl: 'login.jsp', controller: 'LoginCtrl'})
            .when('/list', {templateUrl: 'list.jsp', controller: 'ListCtrl'})
            .when('/add', {templateUrl: 'add.jsp', controller: 'AddCtrl'})
            .when('/edit/:uid', {templateUrl: 'add.jsp', controller: 'AddCtrl'})
            .otherwise({redirectTo: '/login'});
    }])
    .directive('pass', [function () {
        return function (scope, elem, attrs) {
            elem.on('focus', function () {
                elem.attr('type', 'text')
            }).on('blur', function () {
                elem.attr('type', 'password')
            });
        }
    }])
    .factory('Api', ['$resource', function ($resource) {
        return $resource('api?action=:action', {}, {
            loadData: {method: 'GET', params: {action: 'load'}},
            saveData: {method: 'POST', params: {action: 'save'}}
        });
    }])
    .factory('PasswordsFunctions', ['Api', function (Api) {
        // TODO: date created/updated
        var pf;
        return pf = {
            data: {},
            dataEncrypted: null,
            masterPassword: null,
            loadData: function () {
                return Api.loadData(function (res) {
                    pf.dataEncrypted = res.data;
                });
            },
            isNew: function () {
                return !this.dataEncrypted;
            },
            setMasterPassword: function (pass) {
                // TODO check if we change pw!!!
                this.masterPassword = pass;
            },
            encrypt: function () {
                this.dataEncrypted = encrypt(this.masterPassword, angular.toJson(this.data));
            },
            decrypt: function (pass) {
                var decrText = decrypt(pass, this.dataEncrypted);

                if (decrText === null)
                    return false;

                this.data = angular.fromJson(decrText);
                if (!this.data.passwords)
                    this.data.passwords = [];
                this.setMasterPassword(pass);
                return true;
            },
            store: function () {
                this.encrypt();
                return Api.saveData({data: this.dataEncrypted});
            },
            getPasswords: function () {
                return this.data.passwords;
            },
            addOrUpdate: function (password) {
                var existing = this.getByUid(password.uid);
                if (existing) {
                    angular.extend(existing, password);
                } else {
                    this.getPasswords().push(password);
                }
                this.store();
            },
            remove: function (password) {
                var p = this.getByUid(password.uid);
                var pp = this.getPasswords();
                var idx = pp.indexOf(p);
                pp.splice(idx, 1);
                this.store();
            },
            getByUid: function (uid) {
                var pp = this.getPasswords();
                for (var i = 0; i < pp.length; i++) {
                    var p = pp[i];
                    if (p.uid == uid)
                        return p;
                }
                return null;
            },
            listTags: function (filter) {
                var th = {};
                var pp = this.getPasswords();
                for (var i = 0; i < pp.length; i++) {
                    var p = pp[i];
                    if (!p.tags)
                        continue;
                    for (var j = 0; j < p.tags.length; j++) {
                        var tag = p.tags[j];
                        if (!filter || tag.toLowerCase().indexOf(filter.toLowerCase()) >= 0)
                            th[tag] = 1;
                    }
                }
                var tags = [];
                for (var k in th) {
                    tags.push(k);
                }
                tags.sort();
                return tags;
            }
        }
    }])
    .controller('LoginCtrl', ['$scope', 'PasswordsFunctions', function ($scope, PasswordsFunctions) {
        PasswordsFunctions.loadData().$promise.then(function () {
            $scope.isNew = PasswordsFunctions.isNew();
        });

        $scope.login = function (pass) {
            if (!PasswordsFunctions.decrypt(pass)) {
                $scope.errorDanger = 'Wrond password! Please try again.'
            } else {
                location.href = '#/list';
            }
        };
        $scope.register = function (pass, passConfirm) {
            $scope.error = null;
            if (pass != passConfirm) {
                $scope.error = 'Password and Confirm Password are not same!'
            } else {
                PasswordsFunctions.setMasterPassword(pass);
                PasswordsFunctions.store().$promise.then(function () {
                    location.href = '#/list';
                });
            }
        }
    }])
    .controller('ListCtrl', ['$scope', 'PasswordsFunctions', function ListCtrl($scope, PasswordsFunctions) {
        if (PasswordsFunctions.isNew()) {
            location.href = '#/login';
            return;
        }

        $scope.passwords = PasswordsFunctions.getPasswords();

        $scope.addNew = function () {
            location.href = '#/add';
        };
        $scope.edit = function (password) {
            location.href = '#/edit/' + password.uid;
        };
        $scope.delete = function (password) {
            var l = '--------------------------------------------\n';
            if (confirm(l + 'Are you sure you want to remove password for ' + (password.url || password.login) + '?\n' + l)
                && confirm(l + '???   ARE YOU REALLY SURE   ???\n' + l)) {
                PasswordsFunctions.remove(password);
            }
        }
    }])
    .controller('AddCtrl', ['$scope', '$routeParams', 'PasswordsFunctions',
        function AddCtrl($scope, $routeParams, PasswordsFunctions) {
            if (PasswordsFunctions.isNew()) {
                location.href = '#/login';
                return;
            }

            var uid = $routeParams.uid;
            $scope.isEdit = !!uid;
            var p = $scope.password = uid ? angular.copy(PasswordsFunctions.getByUid(uid)) : {uid: newUid()};

            $scope.tags = tagsToObjArr(p.tags);

            $scope.cancel = function () {
                location.href = '#/list';
            };
            $scope.save = function (password) {
                password.tags = [];
                for (var i = 0; i < $scope.tags.length; i++) {
                    password.tags.push($scope.tags[i].text);
                }
                PasswordsFunctions.addOrUpdate(password);
                $scope.cancel();// TODO: error reporting
            };
            $scope.loadTags = function (q) {
                return tagsToObjArr(PasswordsFunctions.listTags(q));
            }
        }]);

function newUid() {
    return '' + new Date().getTime() + '-' + Math.floor(Math.random() * 1e10);
}
function tagsToObjArr(tags) {
    var res = [];
    if (tags) {
        for (var i = 0; i < tags.length; i++) {
            res.push({text: tags[i]})
        }
    }
    return res;
}

function encrypt(password, text) {
    // aes-256
    return sjcl.encrypt(password, text, {ks: 256})
}

function decrypt(password, encryptedText) {
    try {
        return sjcl.decrypt(password, encryptedText);
    } catch (e) {
        return null;
    }
}
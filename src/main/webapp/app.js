angular.module('pass-manager', ['ngRoute', 'ngResource', 'ngTagsInput'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/login', {templateUrl: 'login.jsp', controller: 'LoginCtrl'})
            .when('/logout', {template: '', controller: 'LogoutCtrl'})
            .when('/changeMaster', {templateUrl: 'changeMaster.jsp', controller: 'ChangeMasterCtrl'})
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
    .directive('secureData', [function () {
        function secure(elem) {
            elem.toggleClass('secure-data-hide', !!elem.val())
        }

        return function (scope, elem, attrs) {
            elem.addClass('secure-data-hide');
            setTimeout(function () {
                secure(elem)
            }, 100);
            elem.on('focus', function () {
                elem.removeClass('secure-data-hide')
            }).on('blur', function () {
                secure(elem);
            });
        }
    }])
    .directive('copyToClipboard', [function () {
        function showTooltip(elem, msg) {
            elem.attr('title', msg);
            elem.tooltip(msg ? 'show' : 'hide');
        }

        return function (scope, elem, attrs) {
            elem.attr('data-placement', 'bottom');
            elem.attr('data-trigger', 'manual');

            var cb = new Clipboard(elem[0]);

            cb.on('success', function (e) {
                e.clearSelection();
                showTooltip(elem, 'Copied!');
            });

            cb.on('error', function (e) {
                showTooltip(elem, 'Failed to copy :-(');
            });

            elem.on('mouseleave', function (e) {
                showTooltip(elem, '')
            })
        }
    }])
    .filter('date1', ['$filter', function ($filter) {
        var dateF = $filter('date');
        return function (input) {
            if (!input) return '';
            return dateF(input, 'd MMM yyyy HH:mm');
        }
    }])
    .factory('Api', ['$resource', function ($resource) {
        return $resource('api?action=:action', {}, {
            loadData: {method: 'GET', params: {action: 'load'}},
            saveData: {method: 'POST', params: {action: 'save'}}
        });
    }])
    .factory('Logic', ['Api', '$rootScope', function (Api, $rootScope) {
        // TODO: date created/updated
        function persistMasterPass(pass) {
            window.name = pass; // TODO: is this secure???
        }

        function getPersitedMasterPass() {
            return window.name;
        }

        var pf;
        return pf = {
            data: {},
            dataEncrypted: null,
            masterPassword: null,
            decrypted: false,

            reset: function () {
                this.setMasterPassword(null);
                this.data = {};
                this.dataEncrypted = null;
                this.decrypted = false;
            },
            loadData: function () {
                return Api.loadData(function (res) {
                    pf.dataEncrypted = res.data;
                    pf.decrypted = false;
                    var mp = getPersitedMasterPass();
                    if (mp)
                        pf.decrypt(mp);
                    $rootScope.lastUpdated = res.lastUpdated;
                });
            },
            isNew: function () {
                return !this.dataEncrypted;
            },
            isDecrypted: function () {
                return this.decrypted;
            },
            setMasterPassword: function (pass) {
                // TODO check if we change pw!!!
                this.masterPassword = pass;
                persistMasterPass(pass)
            },
            encrypt: function () {
                this.dataEncrypted = encrypt(this.masterPassword, angular.toJson(this.data));
            },
            decrypt: function (pass) {
                var decrText = decrypt(pass, this.dataEncrypted);

                if (decrText === null)
                    return false;

                this.decrypted = true;
                this.data = angular.fromJson(decrText);
                this.setMasterPassword(pass);

                return true;
            },
            store: function () {
                this.encrypt();
                return Api.saveData({data: this.dataEncrypted}, function (res) {
                    $rootScope.lastUpdated = res.lastUpdated;
                });
            },
            getPasswords: function () {
                return (this.data.passwords || (this.data.passwords = []));
            },
            addOrUpdate: function (password) {
                var existing = this.getByUid(password.uid);
                if (existing) {
                    password.updated = new Date().getTime();
                    angular.extend(existing, password);
                } else {
                    password.created = new Date().getTime();
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
    .controller('RootCtrl', ['$scope', '$timeout', function($scope, $timeout) {
        $scope.flash = function (msg) {
            $scope.flashMsg = msg;
            $timeout(function () {
                delete $scope.flashMsg;
            }, 2000)
        }
    }])
    .controller('LoginCtrl', ['$scope', 'Logic', function ($scope, Logic) {
        Logic.loadData().$promise.then(function () {
            $scope.isNew = Logic.isNew();
            if (Logic.isDecrypted()) {
                location.href = '#/list';
            }
        });

        $scope.login = function (pass) {
            if (!Logic.decrypt(pass)) {
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
                Logic.setMasterPassword(pass);
                Logic.store().$promise.then(function () {
                    location.href = '#/list';
                });
            }
        }
    }])
    .controller('LogoutCtrl', ['$scope', 'Logic', function ($scope, Logic) {
        Logic.reset();
        location.href = '#/login';
    }])
    .controller('ChangeMasterCtrl', ['$scope', 'Logic', function ($scope, Logic) {
        if (Logic.isNew()) {
            location.href = '#/login';
            return;
        }

        $scope.doChangeMaster = function (passOld, pass, passConfirm) {
            if (Logic.masterPassword != passOld) {
                $scope.error = 'Old Password is incorrect!';
                return;
            }

            if (pass != passConfirm) {
                $scope.error = 'New Password and Confirm New Password are not same!';
                return;
            }

            Logic.setMasterPassword(pass);
            Logic.store().$promise.then(function () {
                $scope.flash('Successfully updated master password.');
                location.href = '#/list';
            });
        };
        $scope.cancel = function() {
            location.href = '#/list';
        }
    }])
    .controller('ListCtrl', ['$scope', 'Logic', function ListCtrl($scope, Logic) {
        if (Logic.isNew()) {
            location.href = '#/login';
            return;
        }

        $scope.passwords = Logic.getPasswords();

        $scope.addNew = function () {
            location.href = '#/add';
        };
        $scope.edit = function (password) {
            location.href = '#/edit/' + password.uid;
        };
        $scope.delete = function (password) {
            // TODO: better dialog with danger logo
            var l = '--------------------------------------------\n';
            if (confirm(l + 'Are you sure you want to remove password for ' + (password.url || password.login) + '?\n' + l)
                && confirm(l + '???   ARE YOU REALLY SURE   ???\n' + l)) {
                Logic.remove(password);
            }
        }
    }])
    .controller('AddCtrl', ['$scope', '$routeParams', 'Logic',
        function AddCtrl($scope, $routeParams, Logic) {
            if (Logic.isNew()) {
                location.href = '#/login';
                return;
            }

            var uid = $routeParams.uid;
            $scope.isEdit = !!uid;
            var p = $scope.password = uid ? angular.copy(Logic.getByUid(uid)) : {uid: newUid()};

            $scope.tags = tagsToObjArr(p.tags);

            $scope.cancel = function () {
                location.href = '#/list';
            };
            $scope.save = function (password) {
                password.tags = [];
                for (var i = 0; i < $scope.tags.length; i++) {
                    password.tags.push($scope.tags[i].text);
                }
                Logic.addOrUpdate(password);
                $scope.cancel();// TODO: error reporting
            };
            $scope.loadTags = function (q) {
                return tagsToObjArr(Logic.listTags(q));
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
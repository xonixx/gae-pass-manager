angular.module('pass-manager', ['ngRoute', 'ngResource', 'ngTagsInput'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/login', {templateUrl: '/ng-tpl/login.html', controller: 'LoginCtrl'})
            .when('/logout', {template: '', controller: 'LogoutCtrl'})
            .when('/changeMaster', {templateUrl: '/ng-tpl/changeMaster.html', controller: 'ChangeMasterCtrl'})
            .when('/list', {templateUrl: '/ng-tpl/list.html', controller: 'ListCtrl'})
            .when('/add', {templateUrl: '/ng-tpl/add.html', controller: 'AddCtrl'})
            .when('/edit/:uid', {templateUrl: '/ng-tpl/add.html', controller: 'AddCtrl'})
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
    .factory('Logic', ['Api', '$rootScope', '$q', function (Api, $rootScope, $q) {
        var offlineData = window.global.offlineData;

        // restore on F5
        $(window).on('beforeunload', function() {
            window.name = pf.masterPassword;
        });
        var restoredMasterPass = window.name || null;
        window.name = '';

        var pf;
        return pf = {
            data: {},
            dataEncrypted: null,
            masterPassword: restoredMasterPass,
            decrypted: false,

            reset: function () {
                this.setMasterPassword(null);
                this.data = {};
                this.dataEncrypted = null;
                this.decrypted = false;
            },
            loadData: function () {
                var postLoad = function (res) {
                    pf.dataEncrypted = res.data;
                    pf.decrypted = false;
                    if (pf.masterPassword)
                        pf.decrypt(pf.masterPassword);
                    $rootScope.lastUpdated = res.lastUpdated;
                };
                if (offlineData) {
                    return { $promise: $q.when(offlineData, postLoad) };
                } else
                    return Api.loadData(postLoad);
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
                return this.store();
            },
            remove: function (password) {
                var p = this.getByUid(password.uid);
                var pp = this.getPasswords();
                var idx = pp.indexOf(p);
                pp.splice(idx, 1);
                return this.store();
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
    .controller('RootCtrl', ['$scope', '$location', '$timeout', 'Logic', function ($scope, $location, $timeout, Logic) {
        $scope.global = window.global;
        $scope.readonly = !!window.global.offlineData;

        $scope.flash = function (msg) {
            $scope.flashError(null);
            $scope.flashMsg = msg;
            $timeout(function () {
                delete $scope.flashMsg;
            }, 2000);
        };
        $scope.toFlash = function (msg) {
            return function () {
                $scope.flash(msg);
            }
        };
        $scope.flashError = function (err) {
            $scope.flashErr = err;
        };
        $scope.toFlashError = function (err) {
            return function () {
                $scope.flashError(err);
            }
        };
        function inactivityChecker(allowedInactivitySec, callback) {
            var idleSecs = 0;

            function timerIncrement() {
                idleSecs++;
                //console.info('inactive', idleSecs);
                if (idleSecs > allowedInactivitySec) {
                    canceler();
                    callback()
                }
            }

            var idleInterval = setInterval(timerIncrement, 1000);

            var body = $('body');
            body.on('click.inactivityChecker mousemove.inactivityChecker keyup.inactivityChecker', function () {
                idleSecs = 0;
                //console.info('zeroing inactive', idleSecs);
            });

            function canceler() {
                clearInterval(idleInterval);
                body.off('click.inactivityChecker mousemove.inactivityChecker keyup.inactivityChecker');
                //console.info('canceled inactive');
            }

            return canceler;
        }

        var ALLOWED_INACTIVITY_MINS = 5;
        var inactiveCheckerrCanceler;
        $scope.startInactivityChecker = function () {
            inactiveCheckerrCanceler = inactivityChecker(ALLOWED_INACTIVITY_MINS * 60, function () {
                Logic.reset();
                $scope.flashError('You were logged out after ' + ALLOWED_INACTIVITY_MINS + ' min of inactivity!');
                $location.path('/login');
                $scope.$apply();
            })
        };

        $scope.cancelInactivityChecker = function () {
            if (inactiveCheckerrCanceler)
                inactiveCheckerrCanceler();
        }
    }])
    .controller('LoginCtrl', ['$scope', '$location', 'Logic', function ($scope, $location, Logic) {
        $scope.$scope = $scope;
        
        function proceedToMainScreen() {
            $scope.flashError(null);
            $scope.startInactivityChecker();
            $location.path('/list');
        }

        Logic.loadData().$promise.then(function () {
            $scope.isNew = Logic.isNew();
            if (Logic.isDecrypted())
                proceedToMainScreen();
        });

        $scope.login = function (pass) {
            if (!Logic.decrypt(pass)) {
                $scope.errorDanger = 'Wrond password! Please try again.'
            } else
                proceedToMainScreen();
        };
        $scope.register = function (pass, passConfirm) {
            $scope.error = null;
            if (pass != passConfirm) {
                $scope.error = 'Password and Confirm Password are not same!'
            } else {
                Logic.setMasterPassword(pass);
                Logic.store().$promise.then(proceedToMainScreen);
            }
        };
        $scope.submit = function (action) {
            $scope.$eval(action);    
        };
    }])
    .controller('LogoutCtrl', ['$scope', '$location', 'Logic', function ($scope, $location, Logic) {
        Logic.reset();
        $scope.cancelInactivityChecker();
        $location.path('/login');
    }])
    .controller('ChangeMasterCtrl', ['$scope', '$location', 'Logic', function ($scope, $location, Logic) {
        if (Logic.isNew()) {
            $location.path('/login');
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
                $location.path('/list');
            });
        };
        $scope.cancel = function() {
            $location.path('/list');
        }
    }])
    .controller('ListCtrl', ['$scope', '$location', 'Logic', function ListCtrl($scope, $location, Logic) {
        if (Logic.isNew()) {
            $location.path('/login');
            return;
        }

        $scope.$scope = $scope;
        $scope.passwords = Logic.getPasswords();

        $scope.filterPasswords = function (passwords, searchStr) {
            if (!searchStr)
                return passwords;
            var parts = searchStr.toLowerCase().replace(/ +/g, ' ').split(' ');
            var res = [];
            for (var i = 0; i < passwords.length; i++) {
                var p = passwords[i];
                var satisfies = true;
                for (var j = 0; j < parts.length; j++) {
                    var filterPart = parts[j];
                    var pStr = [p.url, p.login, p.descr, (p.tags||[]).join(' ')].join(' ').toLowerCase();
                    if (pStr.indexOf(filterPart) < 0) {
                        satisfies = false;
                        break;
                    }
                }
                if (satisfies)
                    res.push(p);
            }
            return res;
        };
        $scope.delete = function (password) {
            Logic.remove(password).$promise.then(function () {
                $scope.flash('Password deleted.');
                $('#confirmDeleteModal').modal('hide');
                delete $scope.passToDel;
            });
        };
        $scope.preDelete = function (p) {
            delete $scope.deleteConfirm2;
            $scope.passToDel = p;
        };
    }])
    .controller('AddCtrl', ['$scope', '$location', '$routeParams', 'Logic',
        function AddCtrl($scope, $location, $routeParams, Logic) {
            if (Logic.isNew()) {
                $location.path('/login');
                return;
            }

            var uid = $routeParams.uid;
            $scope.isEdit = !!uid;
            var p = $scope.password = uid ? angular.copy(Logic.getByUid(uid)) : {uid: newUid()};

            $scope.tags = tagsToObjArr(p.tags);

            $scope.cancel = function () {
                $location.path('/list');
            };
            $scope.save = function (password) {
                password.tags = [];
                for (var i = 0; i < $scope.tags.length; i++) {
                    password.tags.push($scope.tags[i].text);
                }
                Logic.addOrUpdate(password).$promise.then($scope.toFlash('Password saved.'));
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
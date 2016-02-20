angular.module('pass-manager', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/list', {templateUrl: 'list.jsp', controller: 'ListCtrl'})
            .when('/add', {templateUrl: 'add.jsp', controller: 'AddCtrl'})
            .when('/edit/:uid', {templateUrl: 'add.jsp', controller: 'AddCtrl', edit: 1})
            .otherwise({redirectTo: '/list'});
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
    .factory('PasswordsFunctions', [function () {
        // TODO: date created/updated
        var initial = [
            {
                uid: newUid(),
                tags: ['tag1', 'tag2'],
                descr: 'Some long detailed description 123',
                url: 'https://google.com',
                login: 'aaa@bbb.ccc',
                pass: "password1"
            },
            {
                uid: newUid(),
                tags: ['tag1', 'tag2', 'tag3'],
                descr: 'Some other long description 123',
                url: 'https://google.com',
                login: 'login',
                pass: "password1"
            },
            {
                uid: newUid(),
                tags: ['tag3'],
                descr: 'dolor sit amet',
                url: 'https://apple.com',
                login: 'login2',
                pass: "password2"
            }
        ];
        return {
            passwords: initial,
            getCurrentList: function () {
                return this.passwords;
            },
            addOrUpdate: function (password) {
                var existing = this.getByUid(password.uid);
                if (existing) {
                    angular.extend(existing, password);
                } else {
                    this.passwords.push(password);
                }
            },
            getByUid: function (uid) {
                for (var i = 0; i < this.passwords.length; i++) {
                    var p = this.passwords[i];
                    if (p.uid == uid)
                        return p;
                }
                return null;
            }
        }
    }])
    .controller('ListCtrl', ['$scope', 'PasswordsFunctions', function ListCtrl($scope, PasswordsFunctions) {
        $scope.passwords = PasswordsFunctions.getCurrentList();

        $scope.addNew = function () {
            location.href = '#/add';
        };
        $scope.edit = function (password) {
            location.href = '#/edit/' + password.uid;
        };
    }])
    .controller('AddCtrl', ['$scope', '$routeParams', 'PasswordsFunctions',
        function AddCtrl($scope, $routeParams, PasswordsFunctions) {
            var uid = $routeParams.uid;
            $scope.isEdit = !!uid;
            $scope.password = uid ? PasswordsFunctions.getByUid(uid) : {uid:newUid()};
            $scope.cancel = function () {
                location.href = '#/list';
            };
            $scope.save = function (password) {
                PasswordsFunctions.addOrUpdate(password);
                $scope.cancel();// TODO: error reporting
            }
        }]);

function newUid() {
    return '' + new Date().getTime() + '-' + Math.floor(Math.random() * 1e10);
}
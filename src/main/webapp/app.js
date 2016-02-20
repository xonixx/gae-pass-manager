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
            add: function (password) {
                this.passwords.push(password);
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
        $scope.edit = function (p) {
            location.href = '#/edit/' + p.uid;
        };
    }])
    .controller('AddCtrl', ['$scope', '$routeParams', 'PasswordsFunctions',
        function AddCtrl($scope, $routeParams, PasswordsFunctions) {
            var uid = $routeParams.uid;
            $scope.isEdit = !!uid;
            $scope.password = uid ? PasswordsFunctions.getByUid(uid) : {};
            $scope.cancel = function () {
                location.href = '#/list';
            }
        }]);

function newUid() {
    return '' + new Date().getTime() + '-' + Math.floor(Math.random() * 1e10);
}
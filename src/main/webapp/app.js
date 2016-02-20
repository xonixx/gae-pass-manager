angular.module('pass-manager', ['ngRoute', 'ngTagsInput'])
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
                uid: '1',
                tags: ['tag1', 'tag2'],
                descr: 'Some long detailed description 123',
                url: 'https://google.com',
                login: 'aaa@bbb.ccc',
                pass: "password1"
            },
            {
                uid: '2',
                tags: ['tag1', 'tag2', 'tag3'],
                descr: 'Some other long description 123',
                url: 'https://google.com',
                login: 'login',
                pass: "password1"
            },
            {
                uid: '3',
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
            },
            listTags: function (filter) {
                var th = {};
                for (var i = 0; i < this.passwords.length; i++) {
                    var p = this.passwords[i];
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
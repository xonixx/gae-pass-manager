angular.module('pass-manager', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/list', {templateUrl: 'list.jsp', controller: 'ListCtrl'})
            .when('/add', {templateUrl: 'add.jsp', controller: 'AddCtrl'})
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
    .controller('ListCtrl', ['$scope', function ListCtrl($scope) {
        $scope.passwords = [
            {tags:['tag1', 'tag2'], descr: 'Some long detailed description 123', url:'https://google.com', pass: "password1"},
            {tags:['tag1', 'tag2', 'tag3'], descr: 'Some other long description 123', url:'https://google.com', pass: "password1"},
            {tags:['tag3'], descr: 'dolor sit amet', url:'https://apple.com', pass: "password2"}
        ]
    }])
    .controller('AddCtrl', ['$scope', function AddCtrl($scope) {

    }]);
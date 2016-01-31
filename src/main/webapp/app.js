angular.module('pass-manager', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/list', {templateUrl: 'list.jsp', controller: 'ListCtrl'})
            .when('/add', {templateUrl: 'add.jsp', controller: 'AddCtrl'})
            .otherwise({redirectTo: '/list'});
    }])
    .controller('ListCtrl', ['$scope', function ListCtrl($scope) {
        $scope.passwords = [
            {tags:['tag1', 'tag2'], descr: 'descr 123', url:'https://google.com'},
            {tags:['tag3'], descr: 'dolor sit amet', url:'https://apple.com'}
        ]
    }])
    .controller('AddCtrl', ['$scope', function AddCtrl($scope) {

    }]);
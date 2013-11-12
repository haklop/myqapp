"use strict";

angular.module("myqapp").controller("HeaderController", ["$scope", "$location", "UserService", function ($scope, $location, UserService) {
    $scope.userinfo = UserService.query();

    $scope.isActive = function (route) {
        return $location.path().indexOf(route) === 0;
    };

    $scope.isAdmin = UserService.isAdmin;
    $scope.isEditor = UserService.isEditor;

    $scope.$on("$routeChangeStart", function () {
        UserService.query(function (response) {
            $scope.userinfo = response;
        });
    });

}]);

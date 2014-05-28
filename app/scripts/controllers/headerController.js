"use strict";

angular.module("myqapp").controller("HeaderController", ["$scope", "$location", "userService", function ($scope, $location, userService) {
    userService.query(function(userinfo) {
        $scope.userinfo = userinfo;
    });

    $scope.isActive = function (route) {
        return $location.path().indexOf(route) === 0;
    };

    $scope.isAdmin = userService.isAdmin;
    $scope.isEditor = userService.isEditor;

    $scope.$on("$routeChangeStart", function () {
        userService.query(function (response) {
            $scope.userinfo = response;
        });
    });

}]);

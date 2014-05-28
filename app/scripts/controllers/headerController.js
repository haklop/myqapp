"use strict";

angular.module("myqapp").controller("HeaderController", ["$scope", "$location", "userService", function ($scope, $location, userService) {
    $scope.userinfo = {};
    $scope.isAdmin = userService.isAdmin;
    $scope.isEditor = userService.isEditor;

    userService.query(function(userinfo) {
        $scope.userinfo = userinfo;
    });

    $scope.isActive = function (route) {
        return $location.path().indexOf(route) === 0;
    };

}]);

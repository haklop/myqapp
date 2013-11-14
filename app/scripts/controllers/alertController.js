"use strict";

angular.module("myqapp").controller("AlertController", ["$scope", function ($scope) {

    $scope.alerts = [];

    $scope.$on("handleAlert", function(evt, alert) {
        $scope.alerts.push(alert);
    });

    $scope.$on("$routeChangeStart", function () {
        $scope.alerts = [];
    });

    $scope.removeAlert = function removeAlert(alert) {
        $scope.alerts.splice($scope.alerts.indexOf(alert), 1);
    };

    $scope.getClassAlert = function getClassAlert(alert) {
        switch (alert.type) {
            case "success":
                return "myqapp-alert-success";
            case "warning":
                return "myqapp-alert-warning";
            case "error":
                return "myqapp-alert-error";
            default :
                return "";
        }
    };

}]);


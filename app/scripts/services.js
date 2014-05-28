"use strict";

var angularModule = angular.module("myqapi", ["ngResource"]);

angularModule.factory("User", ["$resource", function ($resource) {
    return $resource("api/user/:userId", {}, {
        findAll: {method: "GET", isArray: true},
        query: {method: "GET", isArray: false},
        create: {method: "POST"},
        update: {method: "PUT"}
    });
}]);

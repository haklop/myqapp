"use strict";

angular.module("myqapp").service("confService", ["$resource", ConfService]);

function ConfService($resource) {
    this.createConf = function(conf, successCallback, errorCallback) {
        return $resource("api/conf").save(conf, successCallback, errorCallback);
    };
}
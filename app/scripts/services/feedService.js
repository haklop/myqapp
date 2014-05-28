"use strict";

angular.module("myqapp").service("feedService", ["$resource", FeedService]);

function FeedService($resource) {
    this.getFeedList = function(pageNumber, successCallback, errorCallback) {
        return $resource("api/feed/:page").get({"page": pageNumber}, successCallback, errorCallback);
    };

    this.refreshFeed = function(successCallback, errorCallback) {
        $resource("api/feed/refresh").get({}, successCallback, errorCallback);
    };
}
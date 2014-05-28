"use strict";

angular.module("myqapp").service("trelloService", ["$resource", TrelloService]);

function TrelloService($resource) {
    this.addFeedToTrello = function (feed, successCallback, errorCallback) {
        $resource("api/trello/card").save(feed, successCallback, errorCallback);
    };

    this.getValidatedArticle = function(successCallback, errorCallback) {
        $resource("api/trello/validated").query(successCallback, errorCallback);
    };

    this.getTrelloMemberInformation = function(successCallback, errorCallback) {
        $resource("api/trello/userinfo").get({}, successCallback, errorCallback);
    };
}
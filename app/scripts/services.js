"use strict";

var angularModule = angular.module("myqapi", ["ngResource"]);

angularModule.factory("Feed", ["$resource", function ($resource) {
    return $resource("api/feed/:page");
}]);

angularModule.factory("RefreshFeed", ["$resource", function ($resource) {
    return $resource("api/feed/refresh");
}]);

angularModule.factory("Trello", ["$resource", function ($resource) {
    return $resource("api/trello/card", {}, {
        add: {method: "POST"}
    });
}]);

angularModule.factory("TrelloList", ["$resource", function ($resource) {
    return $resource("api/trello/list/:id");
}]);

angularModule.factory("TrelloValidatedList", ["$resource", function ($resource) {
    return $resource("api/trello/validated");
}]);

angularModule.factory("TrelloMember", ["$resource", function ($resource) {
    return $resource("api/trello/userinfo", {}, {
        query: {method: "GET"}
    });
}]);

angularModule.factory("TrelloUser", ["$resource", function ($resource) {
    return $resource("api/trello/member");
}]);

angularModule.factory("Confs", ["$resource", function ($resource) {
    return $resource("api/conf", {}, {
        save: {method: "POST"}
    });
}]);

angularModule.factory("MarkdownGenerator", ["$resource", function ($resource) {
    return $resource("api/markdown", {}, {
        generate: {method: "POST"}
    });
}]);

angularModule.factory("GithubRaw", ["$resource", function ($resource) {
    return $resource("api/github/raw");
}]);

angularModule.factory("User", ["$resource", function ($resource) {
    return $resource("api/user/:userId", {}, {
        findAll: {method: "GET", isArray: true},
        query: {method: "GET", isArray: false},
        create: {method: "POST"},
        update: {method: "PUT"}
    });
}]);

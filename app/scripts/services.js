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

angularModule.factory("StatsUsers", ["$resource", function ($resource) {
    return $resource("api/stats/users");
}]);

angularModule.factory("StatsLists", ["$resource", function ($resource) {
    return $resource("api/stats/lists");
}]);

angularModule.factory("Stats", ["$resource", function ($resource) {
    return $resource("api/stats/refresh", {}, {
        refresh: {method: "GET", isArray: false}
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

angularModule.service("UserService", ["TrelloMember", function (TrelloMember) {

    var self = this;
    this._isEditor = true;
    this._isEditor = true;

    this.query = function () {
        if (!this.member) {
            this.member = TrelloMember.query(function(result){
                self._isEditor = self.hasAuthority(result, "ROLE_EDITOR");
                self._isAdmin = self.hasAuthority(result, "ROLE_ADMIN");
            });
        }
        return this.member;
    };

    this.isEditor = function () {
        return self._isEditor;
    };

    this.isAdmin = function () {
        return self._isAdmin;
    };

    this.hasAuthority = function(user, authority) {
        if (user && user.authorities) {
            for (var i = 0; i < user.authorities.length; i++) {
                if (user.authorities[i] === authority) {
                    return true;
                }
            }
        }
        return false;
    };
}]);

"use strict";

angular.module("myqapp").controller("FeedListCtrl", ["$scope", "$rootScope", "$routeParams", "feedService", "trelloService", "userService", function ($scope, $rootScope, $routeParams, feedService, trelloService, userService) {

    var types = [
        {"name": "News", "selected": true},
        {"name": "Article", "selected": true},
        {"name": "Interview", "selected": true},
        {"name": "Presentation", "selected": true}
    ];

    $scope.feedRefreshing = false;

    $scope.types = types;

    $scope.isEditor = userService.isEditor;

    var feedPage;
    if ($routeParams.page) {
        feedPage = $routeParams.page;
    } else {
        feedPage = 0;
    }

    $scope.feeds = feedService.getFeedList(feedPage, function (feeds) {
        if (feeds.length === 0) {
            $scope.refreshFeed();
        } else {
            $scope.feeds = feeds;
        }
    });

    $scope.addingToTrello = function (feed) {
        return feed.addingToTrello ? "disabled" : undefined;
    };

    $scope.addToTrello = function (feed) {
        feed.addingToTrello = true;
        trelloService.addFeedToTrello(feed, function () {

            $rootScope.$broadcast("handleAlert", {"title": "Carte ajoutée dans Trello", "type": "success",
                "content": "", category: "message"});
            $scope.feeds = feedService.getFeedList(feedPage, function (feeds) {
                $scope.feeds = feeds;
            });
            feed.addingToTrello = false;
        }, function (response) {
            if (response.status === 409) {
                $rootScope.$broadcast("handleAlert", {"title": "Erreur lors de la création de la carte dans Trello",
                    "type": "warning", "content": "Carte déjà existante", category: "message"});
            } else if (response.status !== 403) {
                $rootScope.$broadcast("handleAlert", {"title": "Erreur lors de la création de la carte dans Trello",
                    "type": "error", "content": "", category: "message"});
            }
            feed.addingToTrello = false;
        });
    };

    $scope.filterCategory = function (entryToFilter) {
        if (entryToFilter.type) {
            for (var i = 0; i < types.length; i++) {
                if (types[i].selected === true && types[i].name === entryToFilter.type) {
                    return true;
                }
            }
        }
        return false;
    };

    $scope.refreshFeed = function () {
        $scope.feedRefreshing = true;
        feedService.refreshFeed(function (feeds) {
            $scope.feedRefreshing = false;
            $scope.feeds = feeds;
            $rootScope.$broadcast("handleAlert", {"title": "Mise à jour terminée", "type": "success", "content": "", category: "message"});
        });
    };
}]);
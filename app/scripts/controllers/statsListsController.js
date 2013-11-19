"use strict";

angular.module("myqapp").controller("StatsListCtrl", ["$scope","$rootScope","StatsLists","Stats","UserService", function ($scope, $rootScope, StatsLists, Stats, UserService) {
    $scope.lists = StatsLists.query();

    $scope.predicate = $scope.name;
    $scope.reverse = true;

    $scope.isEditor = UserService.isEditor;

    $scope.news = function (stats) {
        return stats.originalNews + stats.translatedNews;
    };

    $scope.articles = function (stats) {
        return stats.originalArticles + stats.translatedArticles;
    };

    $scope.valids = function (stats) {
        return stats.validatedArticles + stats.validatedNews;
    };

    $scope.mentors = function (stats) {
        return stats.mentoredNews + stats.mentoredArticles;
    };

    $scope.name = function (stats) {
        return stats.fullName;
    };

    $scope.refreshInProgress = false;

    $scope.refreshStats = function () {
        $scope.refreshInProgress = true;
        Stats.refresh(function () {
            $scope.authorStats = StatsLists.query();
            $scope.refreshInProgress = false;
        }, function() {
            $scope.refreshInProgress = false;
            $rootScope.$broadcast("handleAlert", {"title": "Erreur lors du rafraichissement des stats",
                "type": "error", "content": "", category: "message"});
        });
    };

    $scope.refreshing = function () {
        return $scope.refreshInProgress ? "disabled" : undefined;
    };

}]);

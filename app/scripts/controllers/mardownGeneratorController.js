"use strict";

angular.module("myqapp").controller("MarkdownGeneratorCtrl", ["$scope", "MarkdownGenerator", "trelloService", "GithubRaw", function ($scope, MarkdownGenerator, trelloService, GithubRaw) {
    $scope.markdown = {};
    $scope.cards = [];
    $scope.cardsNoGithub = [];

    $scope.generateHtml = function () {
        $scope.isGeneratingHtml = true;
        if ($scope.markdown.text) {
            MarkdownGenerator.generate(JSON.stringify($scope.markdown), function (result) {
                $scope.generated = result.value;
                $scope.isGeneratingHtml = false;
            }, function () {
                $scope.isGeneratingHtml = false;
            });
        }
    };

    $scope.fetchRaw = function (githubUrl, isArticle, nodeName) {
        $scope.selectedUrl = githubUrl;
        GithubRaw.get({url: githubUrl}, function (result) {
            $scope.generated = "";
            $scope.markdown.text = result.content;
            $scope.markdown.type = isArticle ? "article" : "news";
            $scope.markdown.node = nodeName;

        }, function (error) {
            if (error.status === 412) {
                alert("Le contenu recupéré n'appartient pas à la branche master, veuillez mettre à jour l'URL dans Trello");
            }
        });
    };

    $scope.isRefreshing = false;

    $scope.retrieveList = function retrieveList() {
        $scope.isRefreshing = true;

        trelloService.getValidatedArticle(function (result) {
            $scope.isRefreshing = false;

            $scope.cards = [];
            $scope.cardsNoGithub = [];

            for (var i = 0; i < result.length; i++) {
                var card = result[i];
                if (card.githubUrl) {
                    $scope.cards.push(card);
                } else {
                    $scope.cardsNoGithub.push(card);
                }
            }
        }, function () {
            $scope.isRefreshing = false;

        });
    };

    $scope.isSelected = function (card) {
        return card.githubUrl === $scope.selectedUrl;
    };

    $scope.retrieveList();

    $("#tabs").find("li a").click(function(e){

        $("#tabs li, #panel-container .current").removeClass("current");
        $(this).parent().addClass("current");
        var currentTab = $(this).attr("href");
        $(currentTab).addClass("current");
        e.preventDefault();

    });
}]);

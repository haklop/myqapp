"use strict";

angular.module("myqapp", ["ngRoute", "myqapi", "ngSanitize"]).
    config(["$routeProvider", function($routeProvider) {
        $routeProvider.
            when("/feed/:page", {templateUrl: "views/feed-list.html",   controller: "FeedListCtrl"}).
            when("/conf", {templateUrl: "views/conf-list.html",   controller: "ConfListCtrl"}).
            when("/markdown", {templateUrl: "views/markdown-generator.html",   controller: "MarkdownGeneratorCtrl"}).
            when("/admin", {templateUrl: "views/admin.html",   controller: "AdminController"}).
            otherwise({redirectTo: "/feed/0"});
    }]);

angular.module("myqapp").factory("httpInterceptor", ["$q", "$rootScope", function ($q, $rootScope) {
    return function (promise) {
        return promise.then(function (response) {
            return response;
        }, function (response) {
            if (response.data.type) {
                switch (response.data.type) {
                    case "githubToken":
                        $rootScope.$broadcast("handleAlert", {title: "Erreur GitHub", type: "error", category: response.data.type,
                            content: "Vous devez vous <a href=\"/api/github/login\">authentifier</a> sur GitHub pour pouvoir réaliser cette action"});
                        break;
                    case "trelloToken":
                        $rootScope.$broadcast("handleAlert", {title: "Erreur Trello", type: "error", category: response.data.type,
                            content: "Vous devez vous <a href=\"/api/trello/login\">authentifier</a> sur Trello pour pouvoir réaliser cette action"});
                        break;
                    case "invalidArgument":
                        $rootScope.$broadcast("handleAlert", {title: "Bad request", type: "error", category: response.data.type,
                            content: "Données invalides"});
                        break;
                    case "googleAuthentication":
                        window.location = window.location.pathname + "google-signin.html";
                        break;
                    case "accessDenied":
                        window.location = window.location.pathname;
                        break;
                    case "githubError":
                        if (response.data.code === 400) {
                            $rootScope.$broadcast("handleAlert", {title: "Erreur GitHub", type: "error", category: response.data.type,
                                content: "Impossible de trouver le markdown sur GitHub. Etes-vous sûr que le lien dans la carte Trello est à jour ?"});
                        }
                        break;
                    case "unknownError":
                        $rootScope.$broadcast("handleAlert", {title: "Erreur inconnu lors de l\"execution de la requête", type: "error", category: response.data.type,
                            content: ""});
                }
            }
            return $q.reject(response);
        });
    };
}]);

angular.module("myqapp").config(["$httpProvider", function ($httpProvider) {
    $httpProvider.responseInterceptors.push("httpInterceptor");
}]);

"use strict";

var module = angular.module('myqapp', ['myqapi', '$strap.directives']).
    config(['$routeProvider', function($routeProvider) {
        $routeProvider.
            when('/feed/:page', {templateUrl: 'app/partials/feed-list.html',   controller: FeedListCtrl}).
            when('/stats', {templateUrl: 'app/partials/stats-list.html',   controller: StatsListCtrl}).
            when('/users', {templateUrl: 'app/partials/stats-user.html',   controller: StatsUserCtrl}).
            when('/users/:user', {templateUrl: 'app/partials/user-detail.html',   controller: UserDetailCtrl}).
            when('/conf', {templateUrl: 'app/partials/conf-list.html',   controller: ConfListCtrl}).
            when('/markdown', {templateUrl: 'app/partials/markdown-generator.html',   controller: MarkdownGeneratorCtrl}).
            when('/admin', {templateUrl: 'app/partials/admin.html',   controller: AdminCtrl}).
            otherwise({redirectTo: '/feed/0'});
    }]);

module.factory('httpInterceptor', function ($q, $rootScope) {
    return function (promise) {
        return promise.then(function (response) {
            return response;
        }, function (response) {
            if (response.data.type) {
                switch (response.data.type) {
                    case 'githubToken':
                        $rootScope.$broadcast('handleAlert', {title: 'Erreur GitHub', type: 'error', category: response.data.type,
                            content: 'Vous devez vous <a href="/api/github/login">authentifier</a> sur GitHub pour pouvoir réaliser cette action'});
                        break;
                    case 'trelloToken':
                        $rootScope.$broadcast('handleAlert', {title: 'Erreur Trello', type: 'error', category: response.data.type,
                            content: 'Vous devez vous <a href="/api/trello/login">authentifier</a> sur Trello pour pouvoir réaliser cette action'});
                        break;
                    case 'googleAuthentication':
                        window.location = window.location.pathname + "google-signin.html";
                        break;
                    case 'accessDenied':
                        window.location = window.location.pathname;
                        break;
                    case 'githubError':
                        if (response.data.code === 400) {
                            $rootScope.$broadcast('handleAlert', {title: 'Erreur GitHub', type: 'error', category: response.data.type,
                                content: 'Impossible de trouver le markdown sur GitHub. Etes-vous sûr que le lien dans la carte Trello est à jour ?'});
                        }
                        break;
                }
            }
            return $q.reject(response);
        });
    };
});

module.config(function ($httpProvider) {
    $httpProvider.responseInterceptors.push('httpInterceptor');
});

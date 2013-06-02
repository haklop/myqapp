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

module.factory('http401Interceptor', function ($q) {
    return function (promise) {
        return promise.then(function (response) {
            return response;
        }, function (response) {
            if (response.status === 401 || response.status === 403) {
                window.location = window.location.pathname + "trello-token.html";
            }
            // do something on error
            return $q.reject(response);
        });
    };
});

module.config(function ($httpProvider) {
    $httpProvider.responseInterceptors.push('http401Interceptor');
});

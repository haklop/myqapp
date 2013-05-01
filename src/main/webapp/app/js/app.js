"use strict";

angular.module('myqapp', ['myqapi']).
    config(['$routeProvider', function($routeProvider) {
        $routeProvider.
            when('/', {templateUrl: 'app/partials/feed-list.html',   controller: FeedListCtrl}).
            otherwise({redirectTo: '/'});
    }]);
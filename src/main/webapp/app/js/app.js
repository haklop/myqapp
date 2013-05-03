"use strict";

var module = angular.module('myqapp', ['myqapi', '$strap.directives']).
    config(['$routeProvider', function($routeProvider) {
        $routeProvider.
            when('/', {templateUrl: 'app/partials/feed-list.html',   controller: FeedListCtrl}).
            otherwise({redirectTo: '/'});
    }]);

module.directive('ngIf', function() {
    return {
        link: function(scope, element, attrs) {
            if(scope.$eval(attrs.ngIf)) {
                // remove '<div ng-if...></div>'
                element.replaceWith(element.children())
            } else {
                element.replaceWith(' ')
            }
        }
    }
});
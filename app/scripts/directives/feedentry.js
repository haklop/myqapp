'use strict';

angular.module('myqapp')
    .directive('feedEntry', function () {
        return {
            restrict: 'E',
            scope: {
                feed: '=feed'
            },
            link: function link(scope, element, attrs) {
                var panelClass = 'panel-default';
                if (new Date(scope.feed.date) < Date.now() - 30 * 24 * 60 * 60 * 1000) {
                    panelClass = 'panel-danger';
                }

                var panelDiv = element.find('.panel');
                panelDiv[0].className = panelDiv[0].className + ' ' + panelClass;
            },
            templateUrl: 'views/feed-entry.html'
        };
    });

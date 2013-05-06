var angularModule = angular.module('myqapi', ['ngResource']);

angularModule.factory('Feed', function ($resource) {
    return $resource('api/feed/:page', {}, {
        query: {method: 'GET', isArray: false}
    });
});

angularModule.factory('RefreshFeed', function ($resource) {
    return $resource('api/feed/refresh', {}, {
        query: {method: 'GET', isArray: false}
    });
});

angularModule.factory('Trello', function ($resource) {
    return $resource('api/trello/card', {}, {
        add: {method: 'POST'}
    });
});

angularModule.factory('TrelloList', function ($resource) {
    return $resource('api/trello/lists', {}, {
        query: {method: 'GET', isArray: true}
    });
});

angularModule.factory('TrelloMember', function ($resource) {
    return $resource('api/trello/userinfo', {}, {
        query: {method: 'GET'}
    });
});

angularModule.factory('TrelloUser', function ($resource) {
    return $resource('api/trello/member', {}, {
        query: {method: 'GET', isArray: true}
    });
});

angularModule.factory('StatsHelper', function () {
    var statsHelperService = {};

    statsHelperService.hasLabel = function(card, label) {
        var labels = card.labels;
        for (var i = 0; i < labels.length; i++) {
            if (labels[i].name == label) {
                return true;
            }
        }
        return false;
    }

    return statsHelperService;
});

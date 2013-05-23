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

angularModule.factory('Confs', function ($resource) {
    return $resource('api/conf', {}, {
        query: {method: 'GET', isArray: true},
        save: {method: 'POST'}
    });
});

angularModule.factory('StatsUsers', function ($resource) {
    return $resource('api/stats/users', {}, {
        query: {method: 'GET', isArray: true}
    });
});

angularModule.factory('StatsLists', function ($resource) {
    return $resource('api/stats/lists', {}, {
        query: {method: 'GET', isArray: true}
    });
});

angularModule.factory('Stats', function ($resource) {
    return $resource('api/stats/refresh', {}, {
        refresh: {method: 'GET', isArray: false}
    });
});

angularModule.factory('MarkdownGenerator', function ($resource) {
    return $resource('api/markdown', {}, {
        generate: {method: 'POST'}
    });
});

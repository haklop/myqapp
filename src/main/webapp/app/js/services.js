var angularModule = angular.module('myqapi', ['ngResource']);

angularModule.factory('feed', function ($resource) {
    return $resource('api/feed/', {}, {
        query: {method: 'GET', isArray: true}
    });
});

angularModule.factory('refreshFeed', function ($resource) {
    return $resource('api/feed/refresh', {}, {
        query: {method: 'GET', isArray: true}
    });
});

angularModule.factory('trello', function ($resource) {
    return $resource('api/trello/card', {}, {
        add: {method: 'POST'}
    });
});

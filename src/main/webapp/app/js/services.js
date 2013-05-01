var angularModule = angular.module('myqapi', ['ngResource']);

angularModule.factory('Feed', function ($resource) {
        return $resource('api/feed/', {}, {
            query: {method: 'GET', isArray:true}
        });
    });

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
    return $resource('api/trello/list/:id', {}, {
        query: {method: 'GET', isArray: false}
    });
});

angularModule.factory('TrelloValidatedList', function ($resource) {
    return $resource('api/trello/validated');
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

angularModule.factory('GithubRaw', function ($resource) {
    return $resource('api/github/raw', {}, {
        query: {method: 'GET', isArray: false}
    });
});

angularModule.factory('User', function ($resource) {
    return $resource('api/user/:userId', {}, {
        findAll: {method: 'GET', isArray: true},
        query: {method: 'GET', isArray: false},
        create: {method: 'POST'},
        update: {method: 'PUT'},
        remove: {method: 'DELETE'}
    });
});

angularModule.service('UserService', function (TrelloMember) {

    var self = this;
    this._isEditor = true;
    this._isEditor = true;

    this.query = function () {
        if (!this.member) {
            this.member = TrelloMember.query(function(result){
                self._isEditor = self.hasAuthority(result, "ROLE_EDITOR");
                self._isAdmin = self.hasAuthority(result, "ROLE_ADMIN");
            });
        }
        return this.member
    }

    this.isEditor = function () {
       return self._isEditor;
    }

    this.isAdmin = function () {
        return self._isAdmin;
    }

    this.hasAuthority = function(user, authority) {
        if (user && user.authorities) {
            for (var i = 0; i < user.authorities.length; i++) {
                if (user.authorities[i] === authority) {
                    return true;
                }
            }
        }
        return false;
    }
});

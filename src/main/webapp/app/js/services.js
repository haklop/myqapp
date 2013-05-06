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

    statsHelperService.countNews = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (statsHelperService.hasLabel(cards[i], "News")) {
                count++;
            }
        }
        return count;
    };

    statsHelperService.countNewsOriginal = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (statsHelperService.hasLabel(cards[i], "News") && statsHelperService.hasLabel(cards[i], "Original")) {
                count++;
            }
        }
        return count;
    };

    statsHelperService.countNewsTraduction = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (statsHelperService.hasLabel(cards[i], "News") && statsHelperService.hasLabel(cards[i], "Traduction")) {
                count++;
            }
        }
        return count;
    };

    statsHelperService.countArticles = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (statsHelperService.hasLabel(cards[i], "Articles")) {
                count++;
            }
        }
        return count;
    };

    statsHelperService.countArticlesOriginal = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (statsHelperService.hasLabel(cards[i], "Articles") && statsHelperService.hasLabel(cards[i], "Original")) {
                count++;
            }
        }
        return count;
    };

    statsHelperService.countArticlesTraduction = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (statsHelperService.hasLabel(cards[i], "Articles") && statsHelperService.hasLabel(cards[i], "Traduction")) {
                count++;
            }
        }
        return count;
    };

    return statsHelperService;
});

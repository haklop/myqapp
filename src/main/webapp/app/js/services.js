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
        query: {method: 'GET', isArray: true}
    });
});

angularModule.factory('CreateConf', function ($resource) {
    return $resource('api/conf', {}, {
        query: {method: 'POST'}
    });
});

angularModule.factory('StatsHelper', function () {
    var statsHelperService = {};

    statsHelperService.hasLabel = function (card, label) {
        var labels = card.labels;
        for (var i = 0; i < labels.length; i++) {
            if (labels[i].name == label) {
                return true;
            }
        }
        return false;
    };

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

    statsHelperService.getAuthorsStats = function (cards) {
        var authors = {};

        function newStats() {
            return {
                newsoriginal: 0,
                newstraduction: 0,
                articlesoriginal: 0,
                articlestraduction: 0,
                articlesvalids: 0,
                newsvalids: 0,
                newsmentor: 0,
                articlesmentor: 0
            };
        }

        for (var i = 0; i < cards.length; i++) {
            var card = cards[i];
            var idAuthor = card.idMembers[0];
            var idValidator = card.idMembers[1];

            if (!authors[idAuthor]) {
                authors[idAuthor] = newStats();
            }
            if (!authors[idValidator]) {
                authors[idValidator] = newStats();
            }

            if (statsHelperService.hasLabel(card, "Articles")) {
                if (statsHelperService.hasLabel(card, "Mentorat")) {
                    authors[idAuthor]["articlesmentor"]++;
                } else {
                    authors[idValidator]["articlesvalids"]++;
                    if (statsHelperService.hasLabel(card, "Original")) {
                        authors[idAuthor]["articlesoriginal"]++;
                    } else if (statsHelperService.hasLabel(card, "Traduction")) {
                        authors[idAuthor]["articlestraduction"]++;
                    }
                }
            } else if (statsHelperService.hasLabel(card, "News")) {
                if (statsHelperService.hasLabel(card, "Mentorat")) {
                    authors[idAuthor]["newsmentor"]++;
                } else {
                    authors[idValidator]["newsvalids"]++;
                    if (statsHelperService.hasLabel(card, "Original")) {
                        authors[idAuthor]["newsoriginal"]++;
                    } else if (statsHelperService.hasLabel(card, "Traduction")) {
                        authors[idAuthor]["newstraduction"]++;
                    }
                }
            }

        }

        var array = [];
        for (key in authors) {
            if (authors.hasOwnProperty(key)) {
                authors[key].user = key;
                //We remove Al Amine from the display list "5024fa0753f944277fba9907"
                if (key != "undefined" && key != "5024fa0753f944277fba9907") {
                    array.push(authors[key]);
                }
            }
        }
        return array;
    };

    return statsHelperService;
});

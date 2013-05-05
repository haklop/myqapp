function StatsListCtrl($scope, TrelloList, TrelloUser) {
    $scope.lists = TrelloList.query();

    function hasLabel(card, label) {
        var labels = card.labels;
        for (var i = 0; i < labels.length; i++) {
            if (labels[i].name == label) {
                return true;
            }
        }
        return false;
    }

    $scope.countNews = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "News")) {
                count++;
            }
        }
        return count;
    };

    TrelloUser.query(function (users) {
        var userMap = {};
        for (var i = 0; i < users.length; i++) {
            userMap[users[i].id] = users[i];
        }
        $scope.users = userMap;
    });

    $scope.matchUserId = function (memberId) {
        return $scope.users[memberId];
    };

    $scope.countNewsOriginal = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "News") && hasLabel(cards[i], "Original")) {
                count++;
            }
        }
        return count;
    };

    $scope.countNewsTraduction = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "News") && hasLabel(cards[i], "Traduction")) {
                count++;
            }
        }
        return count;
    };

    $scope.countArticles = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "Articles")) {
                count++;
            }
        }
        return count;
    };

    $scope.countArticlesOriginal = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "Articles") && hasLabel(cards[i], "Original")) {
                count++;
            }
        }
        return count;
    };

    $scope.countArticlesTraduction = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "Articles") && hasLabel(cards[i], "Traduction")) {
                count++;
            }
        }
        return count;
    };

    $scope.getAuthors = function (cards) {
        var authors = {};
        for (var i = 0; i < cards.length; i++) {
            var card = cards[i];
            var idAuthor = card.idMembers[0];

            if (!authors[ idAuthor]) {
                authors[idAuthor] = { newsoriginal: 0, newstraduction: 0, articlesoriginal: 0, articlestraduction: 0};
            }
            if (hasLabel(card, "Articles") && hasLabel(card, "Original")) {
                authors[idAuthor]["articlesoriginal"]++;
            }
            if (hasLabel(card, "Articles") && hasLabel(card, "Traduction")) {
                authors[idAuthor]["articlestraduction"]++;
            }
            if (hasLabel(card, "News") && hasLabel(card, "Original")) {
                authors[idAuthor]["newsoriginal"]++;
            }
            if (hasLabel(card, "News") && hasLabel(card, "Traduction")) {
                authors[idAuthor]["newstraduction"]++;
            }
        }
        return authors;
    };

}
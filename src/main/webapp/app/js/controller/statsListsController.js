function StatsListCtrl($scope, TrelloList, TrelloUser, StatsHelper) {
    $scope.lists = TrelloList.query();

    $scope.countNews = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (StatsHelper.hasLabel(cards[i], "News")) {
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
            if (StatsHelper.hasLabel(cards[i], "News") && StatsHelper.hasLabel(cards[i], "Original")) {
                count++;
            }
        }
        return count;
    };

    $scope.countNewsTraduction = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (StatsHelper.hasLabel(cards[i], "News") && StatsHelper.hasLabel(cards[i], "Traduction")) {
                count++;
            }
        }
        return count;
    };

    $scope.countArticles = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (StatsHelper.hasLabel(cards[i], "Articles")) {
                count++;
            }
        }
        return count;
    };

    $scope.countArticlesOriginal = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (StatsHelper.hasLabel(cards[i], "Articles") && StatsHelper.hasLabel(cards[i], "Original")) {
                count++;
            }
        }
        return count;
    };

    $scope.countArticlesTraduction = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (StatsHelper.hasLabel(cards[i], "Articles") && StatsHelper.hasLabel(cards[i], "Traduction")) {
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
            if (StatsHelper.hasLabel(card, "Articles") && StatsHelper.hasLabel(card, "Original")) {
                authors[idAuthor]["articlesoriginal"]++;
            }
            if (StatsHelper.hasLabel(card, "Articles") && StatsHelper.hasLabel(card, "Traduction")) {
                authors[idAuthor]["articlestraduction"]++;
            }
            if (StatsHelper.hasLabel(card, "News") && StatsHelper.hasLabel(card, "Original")) {
                authors[idAuthor]["newsoriginal"]++;
            }
            if (StatsHelper.hasLabel(card, "News") && StatsHelper.hasLabel(card, "Traduction")) {
                authors[idAuthor]["newstraduction"]++;
            }
        }
        return authors;
    };

}
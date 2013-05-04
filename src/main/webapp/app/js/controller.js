function FeedListCtrl($scope, $routeParams, feed, refreshFeed, trello, trelloMember) {
    var types = [
        {"name": "News", "selected": true},
        {"name": "Article", "selected": true},
        {"name": "Interview", "selected": true},
        {"name": "Presentation", "selected": true}
    ];
    $scope.types = types;

    var feedPage;
    if ($routeParams.page) {
        feedPage = $routeParams.page;
    } else {
        feedPage = 0;
    }
    $scope.feeds = feed.query({"page": feedPage}, function (f) {
        if (f.length === 0) {
            $scope.refreshFeed();
        } else {
            $scope.feeds = f;
        }
    });

    $scope.alerts = [];

    $scope.connected = false;

    $scope.userinfo = trelloMember.query(function () {
        $scope.connected = true;
    });

    $scope.formatCategories = function (categories) {
        var s = "";
        var append = "";
        for (i = 0; i < categories.length; i++) {
            s += append;
            append = ", ";
            s += categories[i];
        }
        return s;
    };

    $scope.addToTrello = function (index) {
        trello.add($scope.feeds.content[index], function (result) {
            if (result.result != "") {
                window.location = window.location.pathname + result.result;
            } else {
                $scope.alerts.push({"title": "Carte créée dans Trello", "type": "success", "content": ""});
                $scope.feeds = feed.query({"page": feedPage}, function (f) {
                    $scope.feeds = f;
                });
            }
        }, function (error) {
            $scope.alerts.push({"title": "Erreur lors de la création de la carte dans Trello", "type": "error", "content": ""});
        });
    };

    $scope.filterCategory = function (entryToFilter) {
        if (entryToFilter.type) {
            for (var i = 0; i < types.length; i++) {
                if (types[i].selected === true && types[i].name === entryToFilter.type) {
                    return true;
                }
            }
        }
        return false;
    };

    $scope.refreshFeed = function () {
        refreshFeed.query(function (f) {
            $scope.feeds = f;
            $scope.alerts.push({"title": "Mise à jour terminé", "type": "success", "content": ""});
        });
    };
}

function StatsCtrl($scope, trelloList, trelloUser) {
    trelloList.query(function (l) {
        if (l.result != null && l.result != "") {
            window.location = window.location.pathname + l.result;
        } else {
            $scope.lists = l.body;
        }
    });

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
    }

     trelloUser.query(function(users){
         var userMap = {}
         for (var i = 0; i < users.length; i++) {
            userMap[users[i].id] = users[i];
         }
         $scope.users = userMap;
     });

    $scope.matchUserId = function(memberId){
        return $scope.users[memberId];
    }

    $scope.countNewsOriginal = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "News") && hasLabel(cards[i], "Original")) {
                count++;
            }
        }
        return count;
    }

    $scope.countNewsTraduction = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "News") && hasLabel(cards[i], "Traduction")) {
                count++;
            }
        }
        return count;
    }

    $scope.countArticles = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "Articles")) {
                count++;
            }
        }
        return count;
    }

    $scope.countArticlesOriginal = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "Articles") && hasLabel(cards[i], "Original")) {
                count++;
            }
        }
        return count;
    }

    $scope.countArticlesTraduction = function (cards) {
        var count = 0;
        for (var i = 0; i < cards.length; i++) {
            if (hasLabel(cards[i], "Articles") && hasLabel(cards[i], "Traduction")) {
                count++;
            }
        }
        return count;
    }

    $scope.getAuthors = function(cards){
        var authors = {};
        for (var i = 0; i < cards.length; i++) {
            var card = cards[i];
            var idAuthor = card.idMembers[0];

            if(!authors[ idAuthor]){
                authors[idAuthor] = { newsoriginal : 0, newstraduction: 0, articlesoriginal : 0, articlestraduction: 0} ;
            }
            if (hasLabel(card, "Articles") && hasLabel(card, "Original")){
                authors[idAuthor]["articlesoriginal"]++;
            }
            if (hasLabel(card, "Articles") && hasLabel(card, "Traduction")){
                authors[idAuthor]["articlestraduction"]++;
            }
            if (hasLabel(card, "News") && hasLabel(card, "Original")){
                authors[idAuthor]["newsoriginal"]++;
            }
            if (hasLabel(card, "News") && hasLabel(card, "Traduction")){
                authors[idAuthor]["newstraduction"]++;
            }
        }
        return authors;
    }
}


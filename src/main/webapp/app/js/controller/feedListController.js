function FeedListCtrl($scope, $routeParams, Feed, RefreshFeed, Trello, UserService) {
    var types = [
        {"name": "News", "selected": true},
        {"name": "Article", "selected": true},
        {"name": "Interview", "selected": true},
        {"name": "Presentation", "selected": true}
    ];
    $scope.types = types;

    $scope.isEditor = UserService.isEditor;

    var feedPage;
    if ($routeParams.page) {
        feedPage = $routeParams.page;
    } else {
        feedPage = 0;
    }
    $scope.feeds = Feed.query({"page": feedPage}, function (f) {
        if (f.length === 0) {
            $scope.refreshFeed();
        } else {
            $scope.feeds = f;
        }
    });

    $scope.alerts = [];

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

    $scope.addingToTrello = function (index) {
        return $scope.feeds.content[index].addingToTrello ? 'disabled' : undefined;
    }

    $scope.addToTrello = function (index) {
        $scope.feeds.content[index].addingToTrello = true;
        Trello.add($scope.feeds.content[index], function (result) {

            $scope.alerts.push({"title": "Carte créée dans Trello", "type": "success", "content": ""});
            $scope.feeds = Feed.query({"page": feedPage}, function (f) {
                $scope.feeds = f;
            });
            $scope.feeds.content[index].addingToTrello = false;
        }, function (response) {
            if (response.status === 409) {
                $scope.alerts.push({"title": "Erreur lors de la création de la carte dans Trello", "type": "info", "content": "Carte déjà existante"});
            } else {
                $scope.alerts.push({"title": "Erreur lors de la création de la carte dans Trello", "type": "error", "content": ""});
            }
            $scope.feeds.content[index].addingToTrello = false;
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
        RefreshFeed.query(function (f) {
            $scope.feeds = f;
            $scope.alerts.push({"title": "Mise à jour terminée", "type": "success", "content": ""});
        });
    };
}
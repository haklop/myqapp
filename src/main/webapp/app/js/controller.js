function FeedListCtrl($scope, feed, refreshFeed, trello) {
    var types = [
        {"name": "News", "selected": true},
        {"name": "Article", "selected": true},
        {"name": "Interview", "selected": true},
        {"name": "Presentation", "selected": true}
    ];
    $scope.types = types;

    $scope.feeds = feed.query(function (f) {
        if (f.length === 0) {
            $scope.refreshFeed();
        } else {
            $scope.feeds = f;
        }
    });

    $scope.formatDate = function (date) {
        return new Date(date).toLocaleDateString();
    };

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

    $scope.addToTrello = function (feed) {
        Trello.add(feed, function (result) {
            if (result.result != "") {
                window.location = window.location.pathname + result.result;
            }
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
            if ($scope.feeds.length === 0) {
                $scope.feeds = f;
            } else {
                $scope.feeds.unshift(f);
            }
        });

    };
}


function FeedListCtrl($scope, Feed, Trello) {
    $scope.feeds = Feed.query();

    types = [
        {"name": "News", "selected": true},
        {"name": "Article", "selected": true},
        {"name": "Interview", "selected": true},
        {"name": "Presentation", "selected": true}
    ];
    $scope.types = types;


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
}


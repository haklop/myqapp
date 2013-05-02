function FeedListCtrl($scope, Feed, Trello) {
    $scope.feeds = Feed.query();
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
    }
    $scope.addToTrello = function (feed) {
        Trello.add(feed, function (result) {
            if (result.result != "") {
                window.location = window.location.pathname + result.result;
            }
        });
    }
}


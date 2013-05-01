function FeedListCtrl($scope, Feed) {
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
}


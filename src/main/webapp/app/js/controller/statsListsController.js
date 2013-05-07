function StatsListCtrl($scope, TrelloList, TrelloUser, StatsHelper) {
    $scope.lists = TrelloList.query();

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

    $scope.countNewsOriginal = StatsHelper.countNewsOriginal;
    $scope.countNewsTraduction = StatsHelper.countNewsTraduction;
    $scope.countArticles = StatsHelper.countArticles;
    $scope.countArticlesOriginal = StatsHelper.countArticlesOriginal;
    $scope.countArticlesTraduction = StatsHelper.countArticlesTraduction;
    $scope.countNews = StatsHelper.countNews;

    $scope.getAuthors = StatsHelper.getAuthorsStats;

    $scope.orderby = '';
    $scope.reverse = true;

}
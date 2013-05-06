function StatsUserCtrl($scope, TrelloList, TrelloUser) {
    $scope.lists = TrelloList.query();

    $scope.countArticlesDone = function (user) {
        var cards = $scope.lists[2].cards;
        cards.concat($scope.lists[3].cards);
        cards.concat($scope.lists[4].cards);


        for (var i = 0; i < cards.length; i++) {

        }
    }

    TrelloUser.query(function (users) {
        var userMap = {};
        for (var i = 0; i < users.length; i++) {
            userMap[users[i].id] = users[i];
        }
        $scope.users = userMap;
    });


}
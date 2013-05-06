function StatsUserCtrl($scope, TrelloList, TrelloUser, StatsHelper) {

    $scope.cardsDone = {};
    $scope.authorStats = {};

    TrelloList.query(function (lists) {
        var cards = lists[2].cards;
        cards = cards.concat(lists[3].cards);
        cards = cards.concat(lists[4].cards);
        $scope.authorStats = StatsHelper.getAuthorsStats(cards);
    });

    $scope.matchUserId = function (memberId) {
        return $scope.users[memberId];
    };



    TrelloUser.query(function (users) {
        var userMap = {};
        for (var i = 0; i < users.length; i++) {
            userMap[users[i].id] = users[i];
        }
        $scope.users = userMap;
    });


}
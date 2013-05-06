function StatsUserCtrl($scope, TrelloList, TrelloUser) {
    $scope.lists = TrelloList.query();

    TrelloUser.query(function (users) {
        var userMap = {};
        for (var i = 0; i < users.length; i++) {
            userMap[users[i].id] = users[i];
        }
        $scope.users = userMap;
    });


}
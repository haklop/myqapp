function StatsUserCtrl($scope, TrelloUser, StatsUsers) {

    $scope.cardsDone = {};
    $scope.authorStats = StatsUsers.query();

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


    $scope.predicate = $scope.name;
    $scope.reverse = true;

    $scope.news = function(stats) {
        return stats.originalNews + stats.translatedNews;
    };

    $scope.articles = function(stats){
        return stats.originalArticles + stats.translatedArticles;
    };

    $scope.valids = function(stats){
        return stats.validatedArticles + stats.validatedNews;
    };

    $scope.mentors = function(stats){
        return stats.mentoredNews + stats.mentoredArticles;
    };

    $scope.name = function(stats){
        return stats.member.fullName;
    }
}
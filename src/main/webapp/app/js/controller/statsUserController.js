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
        return stats.newsoriginal + stats.newstraduction;
    };

    $scope.articles = function(stats){
        return stats.articlesoriginal + stats.articlestraduction;
    };

    $scope.valids = function(stats){
        return stats.articlesvalids + stats.newsvalids;
    };

    $scope.mentors = function(stats){
        return stats.newsmentor + stats.articlesmentor;
    };

    $scope.name = function(stats){
        if($scope.matchUserId(stats.user))
        return $scope.matchUserId(stats.user).fullName;
    }



}
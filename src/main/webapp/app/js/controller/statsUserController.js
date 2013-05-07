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

    $scope.predicate = $scope.name;
    $scope.reverse = true;

    $scope.news = function(stats) {
        return stats.newsoriginal + stats.newstraduction;
    };

    $scope.articles = function(stats){
        return stats.articlesoriginal + stats.articlestraduction;
    }

    $scope.valids = function(stats){
        return stats.articlesvalids + stats.newsvalids;
    }

    $scope.mentors = function(stats){
        return stats.newsmentor + stats.articlesmentor;
    }

    $scope.name = function(stats){
        if($scope.matchUserId(stats.user))
        return $scope.matchUserId(stats.user).fullName;
    }



}
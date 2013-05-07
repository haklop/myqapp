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



    $scope.getAuthors = function(list){
        if($scope.statsList && $scope.statsList[list.name]){
            return $scope.statsList[list.name];
        } else {
            if(!$scope.statsList){
                $scope.statsList = {};
            }
            $scope.statsList[list.name] = StatsHelper.getAuthorsStats(list.cards);
        }
    }


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
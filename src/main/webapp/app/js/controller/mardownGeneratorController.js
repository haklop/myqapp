function MarkdownGeneratorCtrl($scope, MarkdownGenerator, TrelloValidatedList, GithubRaw) {
    $scope.markdown = {};
    $scope.cards = [];
    $scope.cardsNoGithub = [];
    $scope.selectedUrl;

    $scope.generateHtml = function () {
        $scope.isGeneratingHtml = true;
        if ($scope.markdown.text) {
            MarkdownGenerator.generate(JSON.stringify($scope.markdown), function (result) {
                $scope.generated = result.value;
                $scope.isGeneratingHtml = false;
            }, function (error) {
                $scope.isGeneratingHtml = false;
            });
        }
    };

    $scope.fetchRaw = function (githubUrl, isArticle) {
        $scope.selectedUrl = githubUrl;
        GithubRaw.query({url: githubUrl}, function (result) {
            $scope.markdown.text = result.content;
            $scope.markdown.type = isArticle ? "article" : "news";
        }, function (error) {
            if (error.status == 412) {
                alert("Le contenu recupéré n'appartient pas à la branche master, veuillez mettre à jour l'URL dans Trello");
            }
        })
    };

    $scope.isRefreshing = false;

    $scope.retrieveList = function retrieveList() {
        $scope.isRefreshing = true;

        TrelloValidatedList.query(function (result) {
            $scope.isRefreshing = false;

            $scope.cards = [];
            $scope.cardsNoGithub = [];

            for (var i = 0; i < result.length; i++) {
                var card = result[i];
                if (card.githubUrl) {
                    $scope.cards.push(card);
                } else {
                    $scope.cardsNoGithub.push(card);
                }

                if (card.mentoring) {
                    card.avatarUrl = card.mentor.avatarUrl;
                    card.fullName = card.mentor.fullName;
                } else {
                    card.avatarUrl = card.author.avatarUrl;
                    card.fullName = card.author.fullName;
                }
            }
        }, function () {
            $scope.isRefreshing = false;

        });
    };

    $scope.isSelected = function (card) {
        return card.githubUrl === $scope.selectedUrl;
    };

    $scope.retrieveList();
}
function MarkdownGeneratorCtrl($scope, MarkdownGenerator, TrelloValidatedList, GithubRaw) {
    $scope.markdown = {};
    $scope.cards = [];
    $scope.cardsNoGithub = [];

    $scope.generateHtml = function () {
        if ($scope.markdown.text) {
            MarkdownGenerator.generate(JSON.stringify($scope.markdown), function (result) {
                $scope.generated = result.value;
            });
        }
    };

    $scope.fetchRaw = function (githubUrl, type) {
        GithubRaw.query({url: githubUrl}, function (result) {
            $scope.markdown.text = result.content;
            $scope.markdown.type = type;
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

    $scope.retrieveList();
}
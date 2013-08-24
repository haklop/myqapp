function MarkdownGeneratorCtrl($scope, MarkdownGenerator, TrelloValidatedList, GithubRaw) {
    $scope.markdown = {};

    $scope.generateHtml = function () {
        if ($scope.markdown.text) {
            MarkdownGenerator.generate(JSON.stringify($scope.markdown), function (result) {
                $scope.generated = result.value;
            });
        }
    };

    $scope.cardType = function (card) {
        for (var i = 0; i < card.labels.length; i++) {
            var label = card.labels[i];
            if (label.name === "Articles") {
                return "article"
            } else if (label.name === "News") {
                return "news";
            }
        }
        return "";
    };

    $scope.isAnArticle = function (card) {
        return $scope.cardType(card) === 'article';
    };

    $scope.fetchRaw = function (githubUrl, type) {
        GithubRaw.query({url: githubUrl}, function (result) {
            $scope.markdown.text = result.content;
            $scope.markdown.type = type;
        }, function(error){
            if(error.status == 412){
                alert("Le contenu recupéré n'appartient pas à la branche master, veuillez mettre à jour l'URL dans Trello");
            }
        })
    };

    $scope.isRefreshing = false;

    $scope.retrieveList = function retrieveList() {
        $scope.isRefreshing = true;

        TrelloValidatedList.get({}, function (result) {
            $scope.isRefreshing = false;

            $scope.cards = [];
            $scope.cardsNoGithub = [];
            for (var i = 0; i < result.cards.length; i++) {
                var card = result.cards[i];
                if (card.desc.indexOf("github.com") !== -1) {
                    var urls = findUrls(card.desc);
                    for (var j = 0; j < urls.length; j++) {
                        if (urls[j].indexOf("github.com") !== -1) {
                            card.githubUrl = urls[j];
                        } else if (urls[j].indexOf("infoq.com") !== -1) {
                            card.infoqUrl = urls[j];
                        }
                    }
                    $scope.cards.push(card);
                } else {
                    $scope.cardsNoGithub.push(card);
                }
            }
        }, function() {
            $scope.isRefreshing = false;

        });
    };

    $scope.retrieveList();

    function findUrls(text) {
        var source = (text || '').toString();
        var urlArray = [];
        var matchArray;

        // Regular expression to find FTP, HTTP(S) and email URLs.
        var regexToken = /(((ftp|https?):\/\/)[\-\w@:%_\+.~#?,&\/\/=]+)|((mailto:)?[_.\w-]+@([\w][\w\-]+\.)+[a-zA-Z]{2,3})/g;

        // Iterate through any URLs in the text.
        while ((matchArray = regexToken.exec(source)) !== null) {
            var token = matchArray[0];
            urlArray.push(token);
        }

        return urlArray;
    }
}
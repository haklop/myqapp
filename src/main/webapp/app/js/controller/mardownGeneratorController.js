function MarkdownGeneratorCtrl($scope, MarkdownGenerator, TrelloList) {
    $scope.markdown = {};

    $scope.generateHtml = function () {
        if ($scope.markdown.text) {
            MarkdownGenerator.generate(JSON.stringify($scope.markdown), function (result) {
                $scope.generated = result.generated;
            });
        }
    }

    TrelloList.query({id:"51499c4cb867d5eb59006794"}, function (result) {
        $scope.cards = [];
        $scope.cardsNoGithub = [];
        for(var i =0; i< result.cards.length; i++){
            var card = result.cards[i];
            if(card.desc.indexOf("github.com") !== -1){
                $scope.cards.push(card);
            } else {
                $scope.cardsNoGithub.push(card);
            }
        }


    })
}
function MarkdownGeneratorCtrl($scope, MarkdownGenerator, TrelloList) {
    $scope.markdown = {};

    $scope.generateHtml = function () {
        if ($scope.markdown.text) {
            MarkdownGenerator.generate(JSON.stringify($scope.markdown), function (result) {
                $scope.generated = result.value;
            });
        }
    }

//    FIXME Ne pas mettre cet ID en dur ici
    TrelloList.query({id:"51499c4cb867d5eb59006794"}, function (result) {
        $scope.cards = [];
        $scope.cardsNoGithub = [];
        for(var i =0; i< result.cards.length; i++){
            var card = result.cards[i];
            if(card.desc.indexOf("github.com") !== -1){
                var urls = findUrls(card.desc);
                for(var j =0; j< urls.length; j++){
                    if(urls[j].indexOf("github.com") !== -1){
                        card.githubUrl = urls[j];
                    }
                }
                $scope.cards.push(card);
            } else {
                $scope.cardsNoGithub.push(card);
            }
        }
    })

    function findUrls( text )
    {
        var source = (text || '').toString();
        var urlArray = [];
        var url;
        var matchArray;

        // Regular expression to find FTP, HTTP(S) and email URLs.
        var regexToken = /(((ftp|https?):\/\/)[\-\w@:%_\+.~#?,&\/\/=]+)|((mailto:)?[_.\w-]+@([\w][\w\-]+\.)+[a-zA-Z]{2,3})/g;

        // Iterate through any URLs in the text.
        while( (matchArray = regexToken.exec( source )) !== null )
        {
            var token = matchArray[0];
            urlArray.push( token );
        }

        return urlArray;
    }
}
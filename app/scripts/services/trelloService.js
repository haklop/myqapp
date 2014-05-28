angular.module("myqapi").service("trelloService", ["$resource", TrelloService]);

function TrelloService($resource) {
    this.addFeedToTrello = function (feed, successCallback, errorCallback) {
        $resource("api/trello/card").save(feed, successCallback, errorCallback);
    };

    this.getValidatedArticle = function(successCallback, errorCallback) {
        $resource("api/trello/validated").query(successCallback, errorCallback);
    };
}
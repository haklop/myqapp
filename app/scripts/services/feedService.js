angular.module("myqapi").service("feedService", ["$resource", FeedService]);

function FeedService($resource) {
    this.getFeedList = function(pageNumber, callback) {
        return $resource("api/feed/:page").get({"page": pageNumber}, function (feeds) {
            if (callback) {
                callback(feeds);
            }
        });
    };

    this.refreshFeed = function(callback) {
        $resource("api/feed/refresh").get(function(feeds) {
           if (callback) {
               callback(feeds);
           }
        });
    };
}
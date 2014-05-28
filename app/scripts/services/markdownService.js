angular.module("myqapi").service("markdownService", ["$resource", MarkdownService]);

function MarkdownService($resource) {
    this.generateHtml = function(html, successCallback, errorCallback) {
        $resource("api/markdown").save(html, successCallback, errorCallback);
    };

    this.getMarkdownFromGitHub = function(githubUrl, successCallback, errorCallback) {
        $resource("api/github/raw").get({url: githubUrl}, successCallback, errorCallback);
    };
}
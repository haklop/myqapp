angular.module("myqapp").directive("previewInfoq", function () {
    return {
        restrict: "A",
        replace: true,
        transclude: false,
        scope: { content:"@preview" },
        template: "<div class=\"preview\">" +
            "<div id=\"content\">" +
            "<div class=\"article_page_left news_container text_content_container\">" +
            "<div class=\"text_info\" ng-bind-html=\"content\"></div>" +
            "</div>" +
            "</div>" +
            "</div>"
    };
});
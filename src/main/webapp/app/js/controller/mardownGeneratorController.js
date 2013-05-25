function MarkdownGeneratorCtrl($scope, MarkdownGenerator) {
    $scope.markdown = {};

    $scope.generateHtml = function () {
        if ($scope.markdown.text) {
            MarkdownGenerator.generate(JSON.stringify($scope.markdown), function(result){
                $scope.generated = result.generated;
            });
        }
    }
}
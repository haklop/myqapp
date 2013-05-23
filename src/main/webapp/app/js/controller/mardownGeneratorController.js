function MarkdownGeneratorCtrl($scope, MarkdownGenerator) {

    $scope.generateHtml = function () {
        if ($scope.markdown) {
            MarkdownGenerator.generate(JSON.stringify($scope.markdown), function(result){
                $scope.generated = result.generated;
            });
        }
    }
}
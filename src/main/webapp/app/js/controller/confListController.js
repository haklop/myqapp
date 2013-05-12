function ConfListCtrl($scope, $http, Confs, CreateConf) {
    $scope.alerts = [];

    $scope.newconf = {};

    $scope.confs = Confs.query();

    $scope.createConf = function () {
        if ($scope.newconfForm.$valid) {
            CreateConf.query($scope.newconf, function(result) {
                $scope.alerts.push({"title": "Conference ajoutée", "type": "success", "content": ""});
                $scope.confs = Confs.query();
                $scope.newconf = {};

            }, function(response) {
                $scope.alerts.push({"title": "Erreur lors de la création de la conference", "type": "error", "content": ""});
            });
        }
    }
}
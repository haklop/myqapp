function ConfListCtrl($scope, Confs) {
    $scope.alerts = [];

    $scope.newconf = {};

    $scope.confs = Confs.query();

    $scope.createConf = function () {
        if ($scope.newconfForm.$valid) {
            Confs.save($scope.newconf, function() {
                $scope.alerts.push({"title": "Conference ajoutée", "type": "success", "content": ""});
                $scope.confs = Confs.query();
                $scope.newconf = {};

            }, function() {
                $scope.alerts.push({"title": "Erreur lors de la création de la conference", "type": "error", "content": ""});
            });
        }
    }
}
function AlertController($scope, $q, $modal) {

    $scope.alerts = [];

    $scope.$on('handleAlert', function(evt, alert) {
        $scope.alerts.push(alert);

        switch (alert.category) {
            case 'trelloToken':
                $q.when(modalTrelloToken).then(function (modalEl) {
                    modalEl.modal('show');
                });
                break;
            case 'githubToken':
                $q.when(modalGithubToken).then(function (modalEl) {
                    modalEl.modal('show');
                });
                break;

        }
    });

    $scope.removeAlert = function removeAlert(alert) {
      $scope.alerts.splice($scope.alerts.indexOf(alert), 1);
    };

    $scope.getClassAlert = function getClassAlert(alert) {
        switch (alert.type) {
            case 'success':
                return 'myqapp-alert-success';
            case 'warning':
                return 'myqapp-alert-warning';
            case 'error':
                return 'myqapp-alert-error';
            default :
                return '';
        }
    };

    var modalTrelloToken = $modal({template: '/app/partials/trelloTokenModal.html',
        persist: true, show: false, backdrop: 'static', scope: $scope});

    var modalGithubToken = $modal({template: '/app/partials/githubTokenModal.html',
        persist: true, show: false, backdrop: 'static', scope: $scope});

}


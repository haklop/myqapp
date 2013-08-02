function HeaderController($scope, $location, $modal, $q, UserService) {
    $scope.userinfo = UserService.query();

    $scope.isActive = function (route) {
        return $location.path().indexOf(route) === 0;
    };

    $scope.isAdmin = UserService.isAdmin;
    $scope.isEditor = UserService.isEditor;

    $scope.$on('$routeChangeStart', function (next, current) {
        UserService.query(function (response) {
            $scope.userinfo = response;
        });
    });

    var modalTrelloToken = $modal({template: '/app/partials/trelloTokenModal.html',
        persist: true, show: false, backdrop: 'static', scope: $scope});

    var modalGithubToken = $modal({template: '/app/partials/githubTokenModal.html',
        persist: true, show: false, backdrop: 'static', scope: $scope});

    $scope.$on('handleAlert', function (evt, alert) {

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

}


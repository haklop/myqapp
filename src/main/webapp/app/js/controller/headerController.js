function HeaderController($scope, $location, UserService) {
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

}


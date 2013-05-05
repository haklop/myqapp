function HeaderController($scope, $location, trelloMember) {
    $scope.isActive = function (route) {
        return $location.path().indexOf(route) === 0;
    };

    $scope.userinfo = trelloMember.query();

    $scope.$on('$routeChangeStart', function(next, current) {
        trelloMember.query(function(response) {
            $scope.userinfo = response;
        }, function(response) {
            window.location = window.location.pathname + "api/trello/login";
        });
    });

}


function HeaderController($scope, $location, TrelloMember) {
    $scope.isActive = function (route) {
        return $location.path().indexOf(route) === 0;
    };

    $scope.userinfo = TrelloMember.query();

    $scope.$on('$routeChangeStart', function(next, current) {
        TrelloMember.query(function(response) {
            $scope.userinfo = response;
        }, function(response) {
            window.location = window.location.pathname + "api/trello/login";
        });
    });

}


function HeaderController($scope, $location, TrelloMember) {
    $scope.isActive = function (route) {
        return $location.path().indexOf(route) === 0;
    };

    $scope.userinfo = TrelloMember.query();

    $scope.$on('$routeChangeStart', function(next, current) {
        TrelloMember.query(function(response) {
            $scope.userinfo = response;
        }, function(response) {
            if (response.status === 400) {
                window.location = window.location.pathname + "api/google/login";
            } else {
                window.location = window.location.pathname + "api/trello/login";
            }
        });
    });

}


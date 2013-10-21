function UserModificationCtrl($scope, User, UserService) {
    $scope.title = "Modifier un utilisateur";
    $scope.action = "Mettre à jour";
    $scope.newuser = JSON.parse(JSON.stringify($scope.user));

    $scope.roles = [
        { name: 'ROLE_EDITOR', checked: UserService.hasAuthority($scope.user, "ROLE_EDITOR") },
        { name: 'ROLE_ADMIN', checked: UserService.hasAuthority($scope.user, "ROLE_ADMIN") }
    ];

    $scope.doWithUser = function () {
        var authorities = [];
        for (var i = 0; i < $scope.roles.length; i++) {
            if ($scope.roles[i].checked) {
                authorities.push($scope.roles[i].name)
            }

        }
        $scope.newuser.authorities = authorities;
        User.update({userId: $scope.user.email}, $scope.newuser, function () {
            $scope.dismiss();
            $scope.refresh();
        });
    }

}
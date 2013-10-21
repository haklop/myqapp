function UserCreationCtrl($scope, User, UserService) {
    $scope.title = "Ajouter un nouvel utilisateur";
    $scope.action = "Ajouter";

    $scope.roles = [
        { name: 'ROLE_EDITOR', checked: UserService.hasAuthority($scope.user, "ROLE_EDITOR") },
        { name: 'ROLE_ADMIN', checked: UserService.hasAuthority($scope.user, "ROLE_ADMIN") }
    ];

    $scope.doWithUser = function () {
        var authorities = [];
        for (var i = 0; i < $scope.roles.length; i++) {
            if($scope.roles[i].checked){
                authorities.push($scope.roles[i].name)
            }

        }
        $scope.newuser.authorities = authorities;
        User.create($scope.newuser, function () {
            $scope.dismiss();
            $scope.refresh();
        });
    }
}
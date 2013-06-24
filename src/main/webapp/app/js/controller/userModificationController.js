function UserModificationCtrl($scope, User) {
    $scope.title = "Modifier un utilisateur";
    $scope.action = "Mettre à jour";
    $scope.newuser = JSON.parse(JSON.stringify($scope.user));

    function hasAuthority(s) {
        if ($scope.user.authorities) {
            for (var i = 0; i < $scope.user.authorities.length; i++) {
                if ($scope.user.authorities[i] === s) {
                    return true;
                }
            }
        }
        return false;
    }

    $scope.roles = [
        { name: 'ROLE_EDITOR', checked: hasAuthority("ROLE_EDITOR") },
        { name: 'ROLE_ADMIN', checked: hasAuthority("ROLE_ADMIN") }
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
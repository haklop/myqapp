function UserCreationCtrl($scope, User) {
    $scope.title = "Ajouter un nouvel utilisateur";
    $scope.action = "Ajouter";

    function hasAuthority(s) {
        if ($scope.user && $scope.user.authorities) {
            for (var i = 0; i < $scope.user.authorities.length; i++) {
                if ($scope.user.authorities[i] === s) {
                    return true;
                }
            }
        }
        return false;
    }

    $scope.roles = [
        { name: 'Visitor', checked: hasAuthority("ROLE_VISITOR") },
        { name: 'Editor', checked: hasAuthority("ROLE_EDITOR") },
        { name: 'Admin', checked: hasAuthority("ROLE_ADMIN") }
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
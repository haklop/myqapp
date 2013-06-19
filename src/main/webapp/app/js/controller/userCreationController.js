function UserCreationCtrl($scope, User) {
    $scope.title = "Ajouter un nouvel utilisateur";
    $scope.action = "Ajouter";

    $scope.roles = [
        { name: 'Visitor',    checked: false },
        { name: 'Editor',   checked: false },
        { name: 'Admin',     checked: false }
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
function UserModificationCtrl($scope, User) {
    $scope.title = "Modifier un utilisateur";
    $scope.action = "Mettre à jour";
    $scope.newuser = JSON.parse(JSON.stringify($scope.user));

    $scope.doWithUser = function () {
        User.update({userId: $scope.user.email}, $scope.newuser, function () {
            $scope.dismiss();
            $scope.refresh();
        });
    }

}
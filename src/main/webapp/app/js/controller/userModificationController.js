function UserModificationCtrl($scope) {
    $scope.title = "Modifier un utilisateur";
    $scope.action = "Mettre à jour";
    $scope.newuser = JSON.parse(JSON.stringify($scope.user));

    $scope.doWithUser = function(){

    }


}
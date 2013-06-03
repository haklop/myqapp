function UserCreationCtrl($scope, User) {
    $scope.title = "Ajouter un nouvel utilisateur";
    $scope.action = "Ajouter";

    $scope.doWithUser = function(){
        User.create($scope.newuser, function(){
           $scope.dismiss();
        });
    }
}
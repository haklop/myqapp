function AdminCtrl($scope, User) {
    $scope.users = User.findAll();

    $scope.remove = function (user) {
        var answer = confirm("Voulez-vous vraiment supprimer l'utilisateur " + user.email);
        if (answer) {
            User.remove({userId: user.email});
            $scope.refresh();
        }
    }

    $scope.refresh = function () {
        $scope.users = User.findAll();
    }
}
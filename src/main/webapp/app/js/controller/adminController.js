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

    $scope.formatRoles = function (user) {
        var s = "";
        var separator = "";
        if (user.authorities) {
            for (var i = 0; i < user.authorities.length; i++) {
                s += separator;
                s += user.authorities[i];
                separator = ", ";
            }
        }
        return s;
    }
}